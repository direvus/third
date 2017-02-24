/*
 *
 * third: That's How I Roll Dice
 *     A dice roller for roleplaying nerds.
 *         http://swords.id.au/third/
 * 
 * Copyright (c) 2010, Brendan Jurd <bj@swords.id.au>
 * All rights reserved.
 * 
 * third is open-source, licensed under the Simplified BSD License, a copy of
 * which can be found in the file LICENSE at the top level of the source code.
 */
package au.id.swords.third2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThirdActivity extends AppCompatActivity
{
    ThirdConfig mConfig = new ThirdConfig();
    boolean mConfigImmutable = false;
    DiceCounter[] mDice;
    DxCounter mDx;
    ButtonCounter mMul;
    ButtonCounter mMod;
    ViewPager mPager;
    PagerAdapter mPagerAdapter;
    PresetListFragment mPresetListFragment;
    LogFragment mLogFragment;
    SharedPreferences mPrefs;
    Vector<ThirdProfile> mProfiles = new Vector<>();
    LinkedHashMap<Integer, ThirdConfig> mPresets = new LinkedHashMap<>();
    ArrayAdapter<String> mProfileAdapter;
    ArrayAdapter<ThirdConfig> mPresetAdapter;
    TextView mResult;
    Vector<TextView> mResultLog;
    Integer mProfile;

    // Indicates "none" in zero-based index values (e.g. arrays).
    private static final int NONE = -1;

    static final int REQUEST_TRIGGER = 0;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        setContentView(R.layout.main);

        mPresetListFragment = (PresetListFragment) getSupportFragmentManager().findFragmentById(R.id.preset_fragment);
        if(mPresetListFragment == null)
            mPresetListFragment = new PresetListFragment();

        mLogFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        if(mLogFragment == null)
            mLogFragment = new LogFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mProfileAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item);

        mProfileAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);

        mPresetAdapter = new ArrayAdapter<>(this, R.layout.preset);

        mDice = new DiceCounter[8];
        mDice[0] = new DiceCounter(this,   2, R.drawable.d2);
        mDice[1] = new DiceCounter(this,   4, R.drawable.d4);
        mDice[2] = new DiceCounter(this,   6, R.drawable.d6);
        mDice[3] = new DiceCounter(this,   8, R.drawable.d8);
        mDice[4] = new DiceCounter(this,  10, R.drawable.d10);
        mDice[5] = new DiceCounter(this,  12, R.drawable.d12);
        mDice[6] = new DiceCounter(this,  20, R.drawable.d20);
        mDice[7] = new DiceCounter(this, 100, R.drawable.d100);
        mMul = new ButtonCounter(this, "mul", R.drawable.mul);
        mMod = new ButtonCounter(this, "mod", R.drawable.mod);
        mDx = new DxCounter(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        if(mPager != null)
        {
            mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);

            PagerTabStrip strip = (PagerTabStrip) findViewById(R.id.pager_title);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorAccent, v, true);
            strip.setTabIndicatorColor(v.data);
        }

        mPrefs = getPreferences(MODE_PRIVATE);

        mPresets = new LinkedHashMap<>();

        TableLayout t = (TableLayout) findViewById(R.id.counters);
        for(DiceCounter c: mDice)
            t.addView(c);
        t.addView(mMod);
        t.addView(mMul);
        t.addView(mDx);
        t.requestLayout();

        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                resetCounters();
            }
        });

        Button roll = (Button) findViewById(R.id.roll);
        roll.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                roll();
            }
        });

        mResult = (TextView) findViewById(R.id.result);

        mResultLog = new Vector<>();
        mResultLog.add((TextView) findViewById(R.id.result_log1));
        mResultLog.add((TextView) findViewById(R.id.result_log2));
        mResultLog.add((TextView) findViewById(R.id.result_log3));
        mResultLog.add((TextView) findViewById(R.id.result_log4));

        updateFromConfig();
        loadProfiles();
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        state.putString("config", mConfig.toJSON().toString());
        super.onSaveInstanceState(state);
    }

    @Override
    public void onRestoreInstanceState(Bundle state)
    {
        super.onRestoreInstanceState(state);
        if(state != null)
        {
            try
            {
                JSONObject json = new JSONObject(state.getString("config"));
                setConfig(new ThirdConfig(json));
            }
            catch(JSONException e)
            {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        int profiles = mProfiles.size();
        menu.findItem(R.id.action_delete_profile).setEnabled(profiles > 1);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        DialogFragment dialog;
        Bundle bundle;
        ThirdProfile profile = getProfile();
        switch(item.getItemId())
        {
            case R.id.action_add_preset:
                dialog = new NamePresetDialogFragment();
                bundle = new Bundle();
                bundle.putString("config", describeConfig());
                bundle.putString("title", getString(R.string.add_preset));
                bundle.putInt("index", NONE);
                bundle.putInt("include", NONE);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "name_preset");
                return true;
            case R.id.action_add_profile:
                dialog = new NameProfileDialogFragment();
                bundle = new Bundle();
                bundle.putInt("index", NONE);
                bundle.putString("title", getString(R.string.add_profile));
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "name_profile");
                return true;
            case R.id.action_rename_profile:
                dialog = new NameProfileDialogFragment();
                bundle = new Bundle();
                bundle.putString("title", getString(R.string.rename_profile));
                bundle.putInt("index", mProfile);
                bundle.putString("name", profile.getName());
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "name_profile");
                return true;
            case R.id.action_delete_profile:
                dialog = new DeleteProfileDialogFragment();
                bundle = new Bundle();
                bundle.putInt("index", mProfile);
                bundle.putString("label", profile.toString());
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "delete_profile");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo info)
    {
        super.onCreateContextMenu(menu, v, info);
        getMenuInflater().inflate(R.menu.preset_context, menu);
        menu.findItem(R.id.action_add_inc).setEnabled(mPresets.size() > 1);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        DialogFragment dialog;
        Bundle bundle;
        AdapterContextMenuInfo info;
        info = (AdapterContextMenuInfo) item.getMenuInfo();
        ThirdConfig conf = mPresetAdapter.getItem(info.position);
        switch(item.getItemId())
        {
            case R.id.action_rename_preset:
                dialog = new NamePresetDialogFragment();
                bundle = new Bundle();
                bundle.putString("name", conf.getName());
                bundle.putString("config", conf.toString());
                bundle.putString("title", getString(R.string.rename_preset));
                bundle.putInt("index", conf.getId());
                bundle.putInt("include", NONE);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "name_preset");
                return true;
            case R.id.action_add_inc_preset:
                dialog = new NamePresetDialogFragment();
                bundle = new Bundle();
                {
                    ThirdConfig preset = new ThirdConfig();
                    preset.update(mConfig);
                    preset.addInclude(conf);
                    bundle.putString("config", preset.describe());
                    bundle.putString("title", getString(R.string.add_preset));
                    bundle.putInt("index", NONE);
                    bundle.putInt("include", conf.getId());
                }
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "name_preset");
                return true;
            case R.id.action_add_inc:
                dialog = new AddIncludeDialogFragment();
                bundle = new Bundle();

                {
                    int[] ids = new int[mPresets.size() - 1];
                    CharSequence[] names = new CharSequence[mPresets.size() - 1];
                    int i = 0;

                    for(ThirdConfig preset: mPresets.values())
                    {
                        if(preset.getId() != conf.getId())
                        {
                            ids[i] = preset.getId();
                            names[i] = preset.toString();
                            i++;
                        }
                    }
                    bundle.putString("title", getString(R.string.add_inc));
                    bundle.putInt("id", conf.getId());
                    bundle.putIntArray("ids", ids);
                    bundle.putCharSequenceArray("names", names);
                }
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "add_include");
                return true;
            case R.id.action_update_preset:
                conf.update(mConfig);
                saveProfiles();
                loadProfiles();
                showToast(getString(R.string.success_update_preset, conf.getName(), conf.describe()));
                return true;
            case R.id.action_add_trigger:
                {
                    LinkedHashSet<Integer> dice = conf.getActiveDice();
                    if(dice.size() == 0)
                    {
                        showToast(getString(R.string.error_trigger_no_dice));
                        return true;
                    }

                    Intent i = new Intent(this, ThirdTriggerActivity.class);
                    i.putExtra("preset", conf.getId());
                    i.putExtra("config", conf.toString());
                    i.putExtra("dice", ThirdUtil.primitiveIntArray(dice));
                    startActivityForResult(i, REQUEST_TRIGGER);
                }
                return true;
            case R.id.action_del_preset:
                boolean included = false;
                for(ThirdConfig preset: mPresets.values())
                {
                    if(preset.hasInclude(conf.getId()))
                    {
                        included = true;
                        break;
                    }
                }
                if(included)
                {
                    dialog = new DeletePresetDialogFragment();
                    bundle = new Bundle();
                    bundle.putInt("index", conf.getId());
                    bundle.putString("name", conf.getName());
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "delete_preset");
                }
                else
                {
                    deletePreset(conf.getId());
                }
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        switch(reqCode)
        {
            case REQUEST_TRIGGER:
                if(resultCode == RESULT_OK)
                {
                    int id = data.getIntExtra("preset", -1);
                    ThirdProfile profile = getProfile();
                    if(profile.getPresets().containsKey(id))
                    {
                        try
                        {
                            ThirdTrigger trigger = new ThirdTrigger(
                                    new JSONObject(data.getStringExtra("trigger")));
                            ThirdConfig preset = profile.getPreset(id);
                            preset.addTrigger(trigger);

                            saveProfiles();
                            loadProfiles();
                        }
                        catch(ThirdTrigger.InfiniteLoop e)
                        {
                            showToast(getString(R.string.error_trigger_infinite_loop));
                        }
                        catch(JSONException e)
                        {
                        }
                    }
                }
                break;
        }
    }

    private void showToast(String text)
    {
        Toast.makeText(
                getApplicationContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }

    private void loadProfiles()
    {
        JSONArray json;
        try
        {
            json = new JSONArray(mPrefs.getString("profiles", "[]"));
        }
        catch(JSONException e)
        {
            json = new JSONArray();
        }

        mProfiles.clear();
        for(int i = 0; i < json.length(); i++)
        {
            mProfiles.add(new ThirdProfile(json.optJSONObject(i)));
        }

        if(mProfiles.size() == 0)
        {
            mProfiles.add(new ThirdProfile(
                        getString(R.string.default_profile_name)));
        }

        if(mPresetListFragment != null)
        {
            Spinner view = mPresetListFragment.getProfileSpinner();
            if(view != null)
                loadProfileList(view);
        }
        invalidateOptionsMenu();
    }

    void loadProfileList(Spinner view)
    {
        if(mProfileAdapter == null || view == null)
            return;

        mProfileAdapter.clear();
        for(int i = 0; i < mProfiles.size(); i++)
        {
            ThirdProfile profile = mProfiles.get(i);
            String name = profile.getName();
            if(name.length() == 0)
            {
                name = String.format("Profile %d", i + 1);
            }

            mProfileAdapter.add(name);
            if(mProfile == null || mProfile == i)
            {
                setProfile(i, profile);
            }
        }
        view.setAdapter(mProfileAdapter);
        view.setSelection(mProfile);
    }

    private void saveProfiles()
    {
        SharedPreferences.Editor edit = mPrefs.edit();
        JSONArray json = new JSONArray();
        for(ThirdProfile profile: mProfiles)
            json.put(profile.toJSON());
        edit.putString("profiles", json.toString());
        edit.commit();
    }

    private void loadPresets(ThirdProfile profile)
    {
        LinkedHashMap<Integer, ThirdConfig> presets = profile.getPresets();

        mPresets.clear();
        for(ThirdConfig conf: presets.values())
            mPresets.put(conf.getId(), conf);

        if(mPresetListFragment != null)
        {
            ListView view = mPresetListFragment.getPresetList();
            if(view != null)
                loadPresetList(view);
        }
    }

    void loadPresetList(ListView view)
    {
        if(mPresetAdapter == null || view == null)
            return;

        mPresetAdapter.clear();
        for(ThirdConfig conf: mPresets.values())
            mPresetAdapter.add(conf);

        view.setAdapter(mPresetAdapter);
    }

    private ThirdProfile getProfile(int index)
    {
        return mProfiles.get(index);
    }

    private ThirdProfile getProfile()
    {
        return getProfile(mProfile);
    }

    private void setProfile(int index, ThirdProfile profile)
    {
        mProfile = index;
        loadPresets(profile);
    }

    private void setProfile(int index)
    {
        setProfile(index, getProfile(index));
    }

    private void unsetProfile()
    {
        mProfile = null;
    }

    public void nameProfile(int index, String name)
    {
        if(name == null)
            name = "";

        name = name.trim();
        if(name.length() == 0)
        {
            showToast(getString(R.string.error_empty_name));
            return;
        }

        if(index >= 0 && index < mProfiles.size())
        {
            mProfiles.get(index).setName(name);
        }
        else
        {
            mProfiles.add(new ThirdProfile(name));
            setProfile(mProfiles.size() - 1);
        }
        saveProfiles();
        loadProfiles();
    }

    public void deleteProfile(int index)
    {
        if(index < 0 || index >= mProfiles.size())
            return;

        if(mProfiles.size() <= 1)
        {
            showToast(getString(
                        R.string.error_del_last_profile));
            return;
        }
        mProfiles.remove(index);
        if(mProfile == index)
        {
            unsetProfile();
        }
        else if(mProfile > index)
        {
            mProfile -= 1;
        }
        saveProfiles();
        loadProfiles();
    }

    public void namePreset(int index, int include, String name)
    {
        if(name == null)
            name = "";

        name = name.trim();
        if(name.length() == 0)
        {
            showToast(getString(R.string.error_empty_name));
            return;
        }

        ThirdProfile profile = getProfile();
        if(index >= 0)
        {
            // Rename existing preset at 'index'.
            ThirdConfig preset = profile.getPreset(index);
            if(preset == null)
                return;

            preset.setName(name);
        }
        else if(include >= 0)
        {
            // Create new preset from current roll, and include existing preset
            // 'include'.
            ThirdConfig base = profile.getPreset(include);
            if(base == null)
                return;

            mConfig.setName(name);
            mConfig.addInclude(base);
            profile.createPreset(mConfig);
        }
        else
        {
            // Create new preset from current roll configuration.
            mConfig.setName(name);
            profile.createPreset(mConfig);
        }
        saveProfiles();
        loadProfiles();
    }

    public void deletePreset(int id)
    {
        getProfile().removePreset(id);
        mConfig.removeInclude(id);

        saveProfiles();
        loadProfiles();
    }

    public void addInclude(int preset_id, int include_id)
    {
        getProfile().addInclude(preset_id, include_id);
        saveProfiles();
        loadProfiles();
    }

    private void updateConfig()
    {
        if(mConfigImmutable)
            return;

        for(DiceCounter c: mDice)
            mConfig.setDie(c.getSides(), c.getValue());
        mConfig.setDx(mDx.getValue());
        mConfig.setDxSides(mDx.getSides());
        mConfig.setMultiplier(mMul.getValue());
        mConfig.setModifier(mMod.getValue());
    }

    private void setConfig(ThirdConfig conf)
    {
        mConfig.setName(conf.getName());
        mConfig.update(conf);
        updateFromConfig();
    }

    private void updateCounters()
    {
        for(DiceCounter c: mDice)
            c.setValue(mConfig.getDie(c.getSides()));
        mDx.setValue(mConfig.getDx());
        mDx.setSides(mConfig.getDxSides());
        mMul.setValue(mConfig.getMultiplier());
        mMod.setValue(mConfig.getModifier());
    }

    private void updateDescription()
    {
        TextView label = (TextView) findViewById(R.id.config);
        label.setText(mConfig.toString());

        TextView range = (TextView) findViewById(R.id.range);
        range.setText(mConfig.describeRange());

        ProgressBar bar = (ProgressBar) findViewById(R.id.result_bar);
        if(mConfig.isBounded())
        {
            bar.setEnabled(true);
            bar.setMax(mConfig.getRange());
            bar.setProgress(0);
        }
        else
        {
            bar.setEnabled(false);
            bar.setMax(0);
            bar.setProgress(0);
        }
    }

    private void updateFromConfig()
    {
        mConfigImmutable = true;
        updateCounters();
        updateDescription();
        mConfigImmutable = false;
    }

    private String describeConfig()
    {
        return mConfig.describe();
    }

    private void clearLog()
    {
        mLogFragment.clearLog();
    }

    private void addLog(String label, String outcome)
    {
        mLogFragment.addLog(label, outcome);
    }

    private int rollDie(int sides)
    {
        int floor = (int) Math.round(Math.floor(Math.random() * sides));
        if(sides > 0)
            return floor + 1;
        else
            return floor - 1;
    }

    private void roll()
    {
        clearLog();
        int result = roll(mConfig);

        ProgressBar bar = (ProgressBar) findViewById(R.id.result_bar);
        bar.setProgress(result - mConfig.getMin());

        shiftResults();
        mResult.setText(String.valueOf(result));
    }

    private int roll(ThirdConfig conf)
    {
        int result = 0;
        int outcome;
        int roll;
        boolean raw;

        Vector<Integer> v = conf.getDice();
        for(Integer sides: v)
        {
            raw = true;
            outcome = rollDie(sides);
            String label = String.format("d%d", Math.abs(sides));
            addLog(label, String.valueOf(outcome));

            for(ThirdTrigger trigger: conf.getTriggers())
            {
                roll = outcome;
                while(trigger.firesOn(sides, roll, raw))
                {
                    roll = rollDie(sides);
                    addLog(trigger.getName(), String.valueOf(roll));
                    outcome = trigger.resolve(outcome, roll);
                    raw = false;
                }
            }
            result += outcome;
        }

        for(ThirdConfig inc: conf.getIncludes())
        {
            result += roll(inc);
        }

        Integer mul = conf.getMultiplier();
        if(mul != 1)
        {
            addLog("*", mul.toString());
            result *= mul;
        }

        Integer mod = conf.getModifier();
        if(mod != 0)
        {
            String sign = (mod < 0) ? "-" : "+";
            addLog(sign, String.valueOf(Math.abs(mod)));
            result += mod;
        }
        return result;
    }

    private void shiftResults()
    {
        String value = mResult.getText().toString();
        String next;
        for(TextView v: mResultLog)
        {
            if(value.equals(""))
                break;

            next = v.getText().toString();
            v.setText(value);
            value = next;
        }
    }

    private void resetCounters()
    {
        mConfig.reset();
        updateFromConfig();
    }

    abstract private class Counter extends TableRow
    {
        String mName;
        EditText mCounter;

        public Counter(Context ctx, String name)
        {
            super(ctx);
            mName = name;
        }

        protected void initCounter()
        {
            mCounter.addTextChangedListener(new TextWatcher()
            {
                public void afterTextChanged(Editable s)
                {
                    updateConfig();
                    updateDescription();
                }
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after)
                {
                }
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count)
                {
                }
            });
        }

        public String getName()
        {
            return mName;
        }

        public Integer getValue()
        {
            try
            {
                return Integer.valueOf(mCounter.getText().toString());
            }
            catch(NumberFormatException e)
            {
                return 0;
            }
        }

        public Integer setValue(Integer value)
        {
            mCounter.setText(value.toString());
            return getValue();
        }

        public void resetValue()
        {
            setValue(0);
        }

        public Integer modValue(Integer mod)
        {
            Integer value = getValue();
            return setValue(value + mod);
        }
    }

    private class ButtonCounter extends Counter
    {
        ImageButton mButton;

        public ButtonCounter(Context ctx, String name, int image)
        {
            super(ctx, name);

            LayoutInflater li;
            li = (LayoutInflater) ctx.getSystemService(
                LAYOUT_INFLATER_SERVICE);
            li.inflate(R.layout.counter, this);

            mButton = (ImageButton) findViewById(R.id.button);
            mButton.setImageResource(image);
            mButton.setOnClickListener(new ImageButton.OnClickListener()
            {
                public void onClick(View v)
                {
                    modValue(1);
                }
            });
            mCounter = (EditText) findViewById(R.id.counter);
            initCounter();
        }
    }

    private class DiceCounter extends ButtonCounter
    {
        int mSides;

        public DiceCounter(Context ctx, int sides, int image)
        {
            super(ctx, String.format("d%d", sides), image);
            mSides = sides;
        }

        public int getSides()
        {
            return mSides;
        }
    }

    private class DxCounter extends Counter
    {
        EditText mSides;

        public DxCounter(Context ctx)
        {
            super(ctx, "dx");

            LayoutInflater li;
            li = (LayoutInflater) ctx.getSystemService(
                LAYOUT_INFLATER_SERVICE);
            li.inflate(R.layout.dx, this);

            mSides = (EditText) findViewById(R.id.dx_sides);
            mSides.addTextChangedListener(new TextWatcher()
            {
                public void afterTextChanged(Editable s)
                {
                    updateConfig();
                    updateDescription();
                }
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after)
                {
                }
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count)
                {
                }
            });

            mCounter = (EditText) findViewById(R.id.dx);
            initCounter();
        }

        public Integer getSides()
        {
            try
            {
                return Integer.valueOf(mSides.getText().toString());
            }
            catch(NumberFormatException e)
            {
                return 0;
            }
        }

        public void setSides(Integer sides)
        {
            mSides.setText(sides.toString());
        }
    }

    public static class DeleteProfileDialogFragment extends DialogFragment
    {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle state)
        {
            Bundle args = getArguments();
            String message = String.format(
                    getString(R.string.confirm_del_profile),
                    args.getString("label"));
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.del_profile)
                .setMessage(message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                int index = DeleteProfileDialogFragment.this.getArguments().getInt("index");
                                ThirdActivity activity = (ThirdActivity) getActivity();
                                activity.deleteProfile(index);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
            return builder.create();
        }
    }

    public static class NameProfileDialogFragment extends DialogFragment
    {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle state)
        {
            Bundle args = getArguments();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.name_profile, null);
            EditText edit = (EditText) view.findViewById(R.id.profile_name);

            if(args.containsKey("name"))
                edit.setText(args.getString("name"));

            builder.setTitle(args.getString("title"))
                .setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                int index = NameProfileDialogFragment.this.getArguments().getInt("index");
                                AlertDialog alert = (AlertDialog) dialog;
                                EditText edit = (EditText) alert.findViewById(R.id.profile_name);
                                ThirdActivity activity = (ThirdActivity) getActivity();
                                activity.nameProfile(index, edit.getText().toString());
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
            return builder.create();
        }
    }

    public static class NamePresetDialogFragment extends DialogFragment
    {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle state)
        {
            Bundle args = getArguments();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.name_preset, null);
            EditText edit = (EditText) view.findViewById(R.id.preset_name);
            TextView text = (TextView) view.findViewById(R.id.name_preset_config);

            if(args.containsKey("name"))
                edit.setText(args.getString("name"));

            if(args.containsKey("config"))
                text.setText(args.getString("config"));

            builder.setTitle(args.getString("title"))
                .setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Bundle args = NamePresetDialogFragment.this.getArguments();
                                int index = args.getInt("index", NONE);
                                int include = args.getInt("include", NONE);

                                AlertDialog alert = (AlertDialog) dialog;
                                EditText edit = (EditText) alert.findViewById(R.id.preset_name);
                                ThirdActivity activity = (ThirdActivity) getActivity();
                                activity.namePreset(index, include, edit.getText().toString());
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
            return builder.create();
        }
    }

    public static class AddIncludeDialogFragment extends DialogFragment
    {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle state)
        {
            Bundle args = getArguments();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(args.getString("title"))
                .setItems(args.getCharSequenceArray("names"), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Bundle args = AddIncludeDialogFragment.this.getArguments();
                                int[] ids = args.getIntArray("ids");
                                int preset = args.getInt("id", NONE);

                                ThirdActivity activity = (ThirdActivity) getActivity();
                                activity.addInclude(preset, ids[id]);
                                dialog.dismiss();
                            }
                        });
            return builder.create();
        }
    }

    public static class DeletePresetDialogFragment extends DialogFragment
    {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle state)
        {
            Bundle args = getArguments();
            String message = getString(R.string.confirm_del_preset, args.getString("name"));
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.del_preset)
                .setMessage(message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                int index = DeletePresetDialogFragment.this.getArguments().getInt("index");
                                ThirdActivity activity = (ThirdActivity) getActivity();
                                activity.deletePreset(index);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
            return builder.create();
        }
    }

    class PagerAdapter extends FragmentPagerAdapter
    {
        final int COUNT = 2;

        PagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount()
        {
            return COUNT;
        }

        @Override
        public Fragment getItem(int pos)
        {
            switch(pos)
            {
                case 0:
                    return mPresetListFragment;
                case 1:
                    return mLogFragment;
            }
            return null;
        }

        @Override
        public String getPageTitle(int pos)
        {
            switch(pos)
            {
                case 0:
                    return getString(R.string.presets).toUpperCase();
                case 1:
                    return getString(R.string.results).toUpperCase();
            }
            return "";
        }
    }

    public static class PresetListFragment extends Fragment
    {
        Spinner mProfileView;
        ListView mPresetView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state)
        {
            ThirdActivity a = (ThirdActivity) getActivity();
            View v = inflater.inflate(R.layout.preset_list, container, false);

            mProfileView = (Spinner) v.findViewById(R.id.profiles);
            mProfileView.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView parent, View v,
                                           int pos, long id)
                {
                    ThirdActivity a = (ThirdActivity) getActivity();
                    a.setProfile((int) id);
                }

                public void onNothingSelected(AdapterView parent)
                {
                }
            });

            mPresetView = (ListView) v.findViewById(R.id.presets);
            mPresetView.setOnItemClickListener(new ListView.OnItemClickListener()
            {
                public void onItemClick(AdapterView parent, View v,
                                        int pos, long id)
                {
                    ThirdActivity a = (ThirdActivity) getActivity();
                    a.setConfig((ThirdConfig) parent.getItemAtPosition(pos));
                    a.roll();
                }
            });
            registerForContextMenu(mPresetView);
            a.loadProfileList(mProfileView);
            a.loadPresetList(mPresetView);
            return v;
        }

        Spinner getProfileSpinner()
        {
            return mProfileView;
        }

        ListView getPresetList()
        {
            return mPresetView;
        }
    }

    public static class LogFragment extends Fragment
    {
        TableLayout mLog;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state)
        {
            View v = inflater.inflate(R.layout.log, container, false);
            mLog = (TableLayout) v.findViewById(R.id.log);
            return v;
        }

        void addLog(String label, String outcome)
        {
            ThirdActivity a = (ThirdActivity) getActivity();
            TableRow row = new TableRow(a);
            mLog.addView(row);

            TextView tv1 = new TextView(a);
            tv1.setText(String.valueOf(mLog.getChildCount()));
            tv1.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(tv1);

            TextView tv2 = new TextView(a);
            tv2.setText(label);
            row.addView(tv2);

            TextView tv3 = new TextView(a);
            tv3.setText(outcome);
            tv3.setGravity(Gravity.RIGHT);
            row.addView(tv3);
        }

        void clearLog()
        {
            mLog.removeAllViews();
        }
    }
}

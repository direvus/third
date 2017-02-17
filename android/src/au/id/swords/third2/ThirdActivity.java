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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.DialogFragment;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;

public class ThirdActivity extends AppCompatActivity
{
    ThirdConfig mConfig = new ThirdConfig();
    boolean mConfigImmutable = false;
    DiceCounter[] mDice;
    DxCounter mDx;
    ButtonCounter mMul;
    ButtonCounter mMod;
    TableLayout mLog;
    ViewFlipper mFlip;
    RadioButton mFlipPresets;
    RadioButton mFlipResults;
    SharedPreferences mPrefs;
    Vector<ThirdProfile> mProfiles = new Vector<>();
    LinkedHashMap<Integer, ThirdConfig> mPresets = new LinkedHashMap<>();
    ArrayAdapter<String> mProfileAdapter;
    ArrayAdapter<ThirdConfig> mPresetAdapter;
    ArrayAdapter<ThirdConfig> mOtherAdapter;
    Spinner mProfileView;
    ListView mPresetView;
    TextView mResult;
    Vector<TextView> mResultLog;
    Integer mProfile;
    Random mRNG;

    private static final int ACT_NAME_PRESET = 0;
    private static final int ACT_NAME_PROFILE = 1;
    private static final int ACT_ADD_INC = 3;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

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

        mPrefs = getPreferences(MODE_PRIVATE);

        mProfileAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item);

        mProfileAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);

        mPresets = new LinkedHashMap<>();
        mPresetAdapter = new ArrayAdapter<>(this, R.layout.preset);
        mOtherAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);

        TableLayout t = (TableLayout) findViewById(R.id.counters);
        for(DiceCounter c: mDice)
            t.addView(c);
        t.addView(mMod);
        t.addView(mMul);
        t.addView(mDx);

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

        mFlip = (ViewFlipper) findViewById(R.id.preset_flipper);
        mFlip.setDisplayedChild(0);
        mFlipPresets = (RadioButton) findViewById(R.id.show_presets);
        mFlipResults = (RadioButton) findViewById(R.id.show_results);
        mFlipPresets.setChecked(true);
        mFlipPresets.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                mFlip.setDisplayedChild(0);
            }
        });
        mFlipResults.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                mFlip.setDisplayedChild(1);
            }
        });

        mProfileView = (Spinner) findViewById(R.id.profiles);
        mProfileView.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView parent, View v,
                                       int pos, long id)
            {
                setProfile((int) id);
            }

            public void onNothingSelected(AdapterView parent)
            {
            }
        });

        mPresetView = (ListView) findViewById(R.id.presets);
        mPresetView.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            public void onItemClick(AdapterView parent, View v,
                                    int pos, long id)
            {
                setConfig((ThirdConfig) parent.getItemAtPosition(pos));
                roll();
            }
        });
        registerForContextMenu(mPresetView);
        loadProfiles();
        mLog = (TableLayout) findViewById(R.id.log);
        mResult = (TextView) findViewById(R.id.result);

        mResultLog = new Vector<>();
        mResultLog.add((TextView) findViewById(R.id.result_log1));
        mResultLog.add((TextView) findViewById(R.id.result_log2));
        mResultLog.add((TextView) findViewById(R.id.result_log3));
        mResultLog.add((TextView) findViewById(R.id.result_log4));

        mRNG = new Random();
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        for(DiceCounter c: mDice)
            state.putInt(c.getName(), c.getValue());
        state.putInt("mul", mMul.getValue());
        state.putInt("mod", mMod.getValue());
        super.onSaveInstanceState(state);
    }

    @Override
    public void onRestoreInstanceState(Bundle state)
    {
        super.onRestoreInstanceState(state);
        for(DiceCounter c: mDice)
            c.setValue(state.getInt(c.getName()));
        mMul.setValue(state.getInt("mul"));
        mMod.setValue(state.getInt("mod"));
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
        menu.findItem(R.id.action_delete_profile).setEnabled(mProfileView.getCount() > 1);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent i;
        switch(item.getItemId())
        {
            case R.id.action_add_preset:
                i = new Intent(this, ThirdNamePreset.class);
                i.putExtra("config", describeConfig());
                startActivityForResult(i, ACT_NAME_PRESET);
                return true;
            case R.id.action_add_profile:
                i = new Intent(this, ThirdNameProfile.class);
                startActivityForResult(i, ACT_NAME_PROFILE);
                return true;
            case R.id.action_rename_profile:
                i = new Intent(this, ThirdNameProfile.class);
                i.putExtra("id", mProfile);
                i.putExtra("name", getProfileName());
                startActivityForResult(i, ACT_NAME_PROFILE);
                return true;
            case R.id.action_delete_profile:
                DialogFragment dialog = new DeleteProfileDialogFragment();
                ThirdProfile profile = getProfile(mProfile);
                Bundle bundle = new Bundle();
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
        menu.findItem(R.id.action_add_inc_preset).setEnabled(mPresets.size() > 1);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        Intent intent;
        AdapterContextMenuInfo info;
        info = (AdapterContextMenuInfo) item.getMenuInfo();
        ThirdConfig conf = mPresetAdapter.getItem(info.position);
        ThirdProfile profile = getProfile(mProfile);
        switch(item.getItemId())
        {
            case R.id.action_rename_preset:
                intent = new Intent(this, ThirdNamePreset.class);
                intent.putExtra("id", conf.getId());
                intent.putExtra("name", conf.getName());
                intent.putExtra("config", conf.describe());
                startActivityForResult(intent, ACT_NAME_PRESET);
                return true;
            case R.id.action_add_inc_preset:
                intent = new Intent(this, ThirdNamePreset.class);
                intent.putExtra("config", conf.describeInclude());
                intent.putExtra("include", conf.getId());
                startActivityForResult(intent, ACT_NAME_PRESET);
                return true;
            case R.id.action_add_inc:
                intent = new Intent(this, ThirdAddInclude.class);
                intent.putExtra("id", conf.getId());
                intent.putExtra("config", conf.describe());

                int[] ids = new int[mPresets.size() - 1];
                String[] labels = new String[mPresets.size() - 1];
                int i = 0;

                for(ThirdConfig preset: mPresets.values())
                {
                    if(preset.getId() != conf.getId())
                    {
                        ids[i] = preset.getId();
                        labels[i] = preset.toString();
                        i++;
                    }
                }
                intent.putExtra("ids", ids);
                intent.putExtra("labels", labels);
                startActivityForResult(intent, ACT_ADD_INC);
                return true;
            case R.id.action_update_preset:
                conf.update(mConfig);
                saveProfiles();
                loadProfiles();
                return true;
            case R.id.action_del_preset:
                profile.removePreset(conf.getId());
                saveProfiles();
                loadProfiles();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent)
    {
        super.onActivityResult(reqCode, resCode, intent);
        if(resCode == RESULT_CANCELED)
            return;

        switch(reqCode)
        {
            case ACT_NAME_PRESET:
                {
                    String name = intent.getStringExtra("name");
                    int id = intent.getIntExtra("id", -1);
                    int inc = intent.getIntExtra("include", -1);

                    if(name == null)
                        name = "";

                    name = name.trim();
                    if(name.length() == 0)
                    {
                        showToast(getString(R.string.error_empty_name));
                        break;
                    }

                    ThirdProfile profile = getProfile(mProfile);

                    if(id >= 0)
                    {
                        ThirdConfig preset = profile.getPreset(id);
                        if(preset == null)
                            break;

                        preset.setName(name);
                    }
                    else if(inc >= 0)
                    {
                        ThirdConfig include = profile.getPreset(inc);
                        if(include == null)
                            break;

                        ThirdConfig preset = profile.createPreset(name);
                        preset.addInclude(include);
                    }
                    else
                    {
                        mConfig.setName(name);
                        profile.createPreset(mConfig);
                    }
                    saveProfiles();
                    loadProfiles();
                }
                break;
            case ACT_NAME_PROFILE:
                {
                    String name = intent.getStringExtra("name");
                    if(name == null)
                        name = "";

                    name = name.trim();
                    if(name.length() == 0)
                    {
                        showToast(getString(R.string.error_empty_name));
                        break;
                    }

                    int id = intent.getIntExtra("id", -1);
                    if(id >= 0 && id < mProfiles.size())
                    {
                        mProfiles.get(id).setName(name);
                    }
                    else
                    {
                        mProfiles.add(new ThirdProfile(name));
                    }
                    saveProfiles();
                    loadProfiles();
                }
                break;
            case ACT_ADD_INC:
                {
                    int id = intent.getIntExtra("id", -1);
                    int inc = intent.getIntExtra("include", -1);

                    if(id < 0 || inc < 0 || id == inc)
                        break;

                    ThirdProfile profile = getProfile(mProfile);
                    ThirdConfig preset = profile.getPreset(id);
                    ThirdConfig include = profile.getPreset(inc);

                    if(preset == null || include == null)
                        break;

                    preset.addInclude(include);
                    saveProfiles();
                    loadProfiles();
                }
                break;
        }
        invalidateOptionsMenu();
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
        mProfileView.setAdapter(mProfileAdapter);
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
        mPresetAdapter.clear();
        for(ThirdConfig conf: presets.values())
        {
            mPresets.put(conf.getId(), conf);
            mPresetAdapter.add(conf);
        }

        mPresetView.setAdapter(mPresetAdapter);
    }

    private ThirdProfile getProfile(int index)
    {
        return mProfiles.get(index);
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
        bar.setMax(mConfig.getRange());
        bar.setProgress(0);
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

    private String getProfileName()
    {
        return getProfile(mProfile).getName();
    }

    private void clearLog()
    {
        mLog.removeAllViews();
    }

    private void addLog(String label, String outcome)
    {
        TableRow row = new TableRow(this);
        mLog.addView(row);

        TextView tv1 = new TextView(this);
        tv1.setText(String.valueOf(mLog.getChildCount()));
        tv1.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(label);
        row.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(outcome);
        tv3.setGravity(Gravity.RIGHT);
        row.addView(tv3);
    }

    private Integer rollDie(Integer sides)
    {
        Integer result = mRNG.nextInt(Math.abs(sides)) + 1;
        if(sides < 0)
            result = -result;
        return result;
    }

    private void roll()
    {
        clearLog();
        Integer result = roll(mConfig);

        ProgressBar bar = (ProgressBar) findViewById(R.id.result_bar);
        bar.setProgress(result - mConfig.getMin());

        shiftResults();
        mResult.setText(result.toString());
    }

    private Integer roll(ThirdConfig conf)
    {
        Integer result = new Integer(0);
        Integer outcome;

        for(ThirdConfig inc: conf.getIncludes())
        {
            result += roll(inc);
        }

        Vector<Integer> v = conf.getDice();
        for(Integer sides: v)
        {
            outcome = rollDie(sides);
            String label = String.format("d%d", Math.abs(sides));
            addLog(label, outcome.toString());
            result += outcome;
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
                return new Integer(mCounter.getText().toString());
            }
            catch(NumberFormatException e)
            {
                return new Integer(0);
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
                return new Integer(mSides.getText().toString());
            }
            catch(NumberFormatException e)
            {
                return new Integer(0);
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
}

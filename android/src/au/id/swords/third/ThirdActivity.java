package au.id.swords.third;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.AdapterView;
import android.widget.TableLayout;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.text.Editable;
import android.text.TextWatcher;
import android.database.Cursor;
import java.util.Random;
import java.util.Vector;

public class ThirdActivity extends Activity
{
    ThirdDb mDb;
    ThirdConfig mConfig;
    DiceCounter[] mDice;
    Counter mMul;
    Counter mMod;
    TableLayout mLog;
    ViewFlipper mFlip;
    RadioButton mFlipPresets;
    RadioButton mFlipResults;
    ArrayAdapter<ThirdConfig> mPresets;
    SimpleCursorAdapter mProfiles;
    Cursor mProfileCursor;
    Cursor mPresetCursor;
    Integer mProfile;
    Random mRNG;

    private static final int ACT_NAME_PRESET = 0;
    private static final int ADD_PRESET = Menu.FIRST;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDice = new DiceCounter[8];
        mDice[0] = new DiceCounter(this, R.drawable.d2,   2);
        mDice[1] = new DiceCounter(this, R.drawable.d4,   4);
        mDice[2] = new DiceCounter(this, R.drawable.d6,   6);
        mDice[3] = new DiceCounter(this, R.drawable.d8,   8);
        mDice[4] = new DiceCounter(this, R.drawable.d10,  10);
        mDice[5] = new DiceCounter(this, R.drawable.d12,  12);
        mDice[6] = new DiceCounter(this, R.drawable.d20,  20);
        mDice[7] = new DiceCounter(this, R.drawable.d100, 100);
        mMul  = new Counter(this, R.drawable.mul,  "mul");
        mMod  = new Counter(this, R.drawable.mod,  "mod");

        TableLayout t = (TableLayout)findViewById(R.id.counters);
        for(DiceCounter c: mDice)
            t.addView(c);
        t.addView(mMod);
        t.addView(mMul);

        setConfig(new ThirdConfig());

        Button reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                resetCounters();
            }
        });

        Button roll = (Button)findViewById(R.id.roll);
        roll.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                roll();
            }
        });

        mFlip = (ViewFlipper)findViewById(R.id.preset_flipper);
        mFlip.setDisplayedChild(0);
        mFlipPresets = (RadioButton)findViewById(R.id.show_presets);
        mFlipResults = (RadioButton)findViewById(R.id.show_results);
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

        mDb = new ThirdDb(this);

        mProfileCursor = mDb.getAllProfiles();
        startManagingCursor(mProfileCursor);
        String[] cols = new String[] {"name"};
        int[] views = new int[] {android.R.id.text1};
        mProfiles = new SimpleCursorAdapter(this,
            android.R.layout.simple_spinner_item,
            mProfileCursor, cols, views);

        Spinner profile_view = (Spinner)findViewById(R.id.profiles);
        mProfiles.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
        profile_view.setAdapter(mProfiles);

        mProfileCursor.moveToFirst();
        mProfile = mProfileCursor.getInt(0);

        ListView preset_view = (ListView)findViewById(R.id.presets);
        preset_view.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            public void onItemClick(AdapterView parent, View v,
                                    int pos, long id)
            {
                setConfig((ThirdConfig)parent.getItemAtPosition(pos));
                updateDescription();
                roll();
            }
        });
        loadPresets();
        mLog = (TableLayout)findViewById(R.id.log);
        mRNG = new Random();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_PRESET, 0, R.string.add_preset);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case ADD_PRESET:
                Intent i = new Intent(this, ThirdNamePreset.class);
                i.putExtra("config", describeConfig());
                startActivityForResult(i, ACT_NAME_PRESET);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                String name = intent.getStringExtra("name");
                mConfig.setName(name);
                mDb.addPreset(mProfile, mConfig);
                loadPresets();
                break;
        }
    }


    private ThirdConfig getConfig()
    {
        ThirdConfig conf = new ThirdConfig();
        for(DiceCounter c: mDice)
            conf.setDie(c.sides, c.getValue());
        conf.setMultiplier(mMul.getValue());
        conf.setModifier(mMod.getValue());
        return conf;
    }

    private void loadPresets()
    {
        mPresetCursor = mDb.getPresets(mProfile);
        startManagingCursor(mPresetCursor);
        mPresetCursor.moveToFirst();
        mPresets = new ArrayAdapter(this, R.layout.preset);
        while(!mPresetCursor.isAfterLast())
        {
            mPresets.add(new ThirdConfig(mPresetCursor));
            mPresetCursor.moveToNext();
        }

        ListView preset_view = (ListView)findViewById(R.id.presets);
        preset_view.setAdapter(mPresets);
    }

    private void updateConfig()
    {
        mConfig = getConfig();
    }

    private void setConfig(ThirdConfig conf)
    {
        for(DiceCounter c: mDice)
            c.setValue(conf.getDie(c.sides));
        mMul.setValue(conf.getMultiplier());
        mMod.setValue(conf.getModifier());
    }

    private String describeConfig()
    {
        return mConfig.describeConfig();
    }

    private void updateDescription()
    {
        updateConfig();
        TextView label = (TextView)findViewById(R.id.config);
        label.setText(describeConfig());

        TextView range = (TextView)findViewById(R.id.range);
        range.setText(mConfig.describeRange());

        ProgressBar bar = (ProgressBar)findViewById(R.id.result_bar);
        bar.setMax(mConfig.getRange());
        bar.setProgress(0);
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
        return mRNG.nextInt(sides) + 1;
    }

    private void roll()
    {
        Integer result = new Integer(0);
        Integer outcome;

        clearLog();

        Vector<Integer> v = mConfig.getDice();
        for(Integer sides: v)
        {
            outcome = rollDie(Math.abs(sides));
            String label = String.format("d%d", Math.abs(sides));
            addLog(label, outcome.toString());
            result += outcome;
        }

        Integer mul = mConfig.getMultiplier();
        if(mul != 1)
        {
            addLog("*", mul.toString());
            result *= mul;
        }

        Integer mod = mConfig.getModifier();
        if(mod != 0)
        {
            String sign = (mod < 0) ? "-" : "+";
            addLog(sign, String.valueOf(Math.abs(mod)));
            result += mod;
        }

        ProgressBar bar = (ProgressBar)findViewById(R.id.result_bar);
        bar.setProgress(result - mConfig.getMin());

        TextView tv = (TextView)findViewById(R.id.result);
        tv.setText(result.toString());
    }

    private void resetCounters()
    {
        setConfig(new ThirdConfig());
        updateDescription();
    }

    private class Counter extends TableRow
    {
        String name;
        ImageButton button;
        EditText counter;

        public Counter(Context ctx, int image, String name)
        {
            super(ctx);

            LayoutInflater li;
            li = (LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            li.inflate(R.layout.counter, this);

            this.name = name;
            button = (ImageButton)findViewById(R.id.button);
            button.setImageResource(image);
            button.setOnClickListener(new ImageButton.OnClickListener()
            {
                public void onClick(View v)
                {
                    modValue(1);
                    updateDescription();
                }
            });
            counter = (EditText)findViewById(R.id.counter);
            counter.addTextChangedListener(new TextWatcher()
            {
                public void afterTextChanged(Editable s)
                {
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

        public Integer getValue()
        {
            try 
            {
                return new Integer(counter.getText().toString());
            }
            catch(NumberFormatException e)
            {
                return new Integer(0);
            }
        }

        public Integer setValue(Integer value)
        {
            counter.setText(value.toString());
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

    private class DiceCounter extends Counter
    {
        int sides;

        public DiceCounter(Context ctx, int image, int sides)
        {
            super(ctx, image, String.format("d%d", sides));
            this.sides = sides;
        }
    }
}

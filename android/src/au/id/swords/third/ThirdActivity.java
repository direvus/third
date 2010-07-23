package au.id.swords.third;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.Random;
import java.util.Vector;

public class ThirdActivity extends Activity
{
    ThirdConfig config;
    Random rng;
    DiceCounter[] dice;
    Counter mul;
    Counter mod;
    TableLayout log;
    ViewFlipper flip;
    RadioButton flip_presets;
    RadioButton flip_results;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dice = new DiceCounter[8];
        dice[0] = new DiceCounter(this, R.drawable.d2,   2);
        dice[1] = new DiceCounter(this, R.drawable.d4,   4);
        dice[2] = new DiceCounter(this, R.drawable.d6,   6);
        dice[3] = new DiceCounter(this, R.drawable.d8,   8);
        dice[4] = new DiceCounter(this, R.drawable.d10,  10);
        dice[5] = new DiceCounter(this, R.drawable.d12,  12);
        dice[6] = new DiceCounter(this, R.drawable.d20,  20);
        dice[7] = new DiceCounter(this, R.drawable.d100, 100);
        mul  = new Counter(this, R.drawable.mul,  "mul");
        mod  = new Counter(this, R.drawable.mod,  "mod");

        TableLayout t = (TableLayout)findViewById(R.id.counters);
        for(DiceCounter c: dice)
            t.addView(c);
        t.addView(mod);
        t.addView(mul);

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

        flip = (ViewFlipper)findViewById(R.id.preset_flipper);
        flip_results = (RadioButton)findViewById(R.id.show_results);
        flip_presets = (RadioButton)findViewById(R.id.show_presets);
        flip_results.setChecked(true);
        flip_results.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                flip.setDisplayedChild(0);
            }
        });
        flip_presets.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                flip.setDisplayedChild(1);
            }
        });

        log = (TableLayout)findViewById(R.id.log);
        rng = new Random();
    }

    private ThirdConfig getConfig()
    {
        ThirdConfig conf = new ThirdConfig();
        for(DiceCounter c: dice)
            conf.setDie(c.sides, c.getValue());
        conf.setMultiplier(mul.getValue());
        conf.setModifier(mod.getValue());
        return conf;
    }

    private void updateConfig()
    {
        config = getConfig();
    }

    private void setConfig(ThirdConfig conf)
    {
        for(DiceCounter c: dice)
            c.setValue(conf.getDie(c.sides));
        mul.setValue(conf.getMultiplier());
        mod.setValue(conf.getModifier());
    }

    private String describeConfig()
    {
        return config.toString();
    }

    private void updateDescription()
    {
        updateConfig();
        TextView label = (TextView)findViewById(R.id.config);
        label.setText(describeConfig());

        TextView range = (TextView)findViewById(R.id.range);
        range.setText(config.describeRange());

        ProgressBar bar = (ProgressBar)findViewById(R.id.result_bar);
        bar.setMax(config.getRange());
        bar.setProgress(0);
    }

    private void clearLog()
    {
        log.removeAllViews();
    }

    private void addLog(String label, String outcome)
    {
        TableRow row = new TableRow(this);
        log.addView(row);

        TextView tv1 = new TextView(this);
        tv1.setText(String.valueOf(log.getChildCount()));
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
        return rng.nextInt(sides) + 1;
    }

    private void roll()
    {
        Integer result = new Integer(0);
        Integer outcome;

        clearLog();

        Vector<Integer> v = config.getDice();
        for(Integer sides: v)
        {
            outcome = rollDie(Math.abs(sides));
            String label = String.format("d%d", Math.abs(sides));
            addLog(label, outcome.toString());
            result += outcome;
        }

        Integer mul = config.getMultiplier();
        if(mul != 1)
        {
            addLog("*", mul.toString());
            result *= mul;
        }

        Integer mod = config.getModifier();
        if(mod != 0)
        {
            String sign = (mod < 0) ? "-" : "+";
            addLog(sign, String.valueOf(Math.abs(mod)));
            result += mod;
        }

        ProgressBar bar = (ProgressBar)findViewById(R.id.result_bar);
        bar.setProgress(result - config.getMin());

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

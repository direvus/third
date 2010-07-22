package au.id.swords.third;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;

public class ThirdActivity extends Activity
{
    ThirdConfig config;
    DiceCounter[] dice;
    Counter mul;
    Counter mod;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dice = new DiceCounter[7];
        dice[0] = new DiceCounter(this, R.drawable.d2,   2);
        dice[1] = new DiceCounter(this, R.drawable.d4,   4);
        dice[2] = new DiceCounter(this, R.drawable.d8,   8);
        dice[3] = new DiceCounter(this, R.drawable.d10,  10);
        dice[4] = new DiceCounter(this, R.drawable.d12,  12);
        dice[5] = new DiceCounter(this, R.drawable.d20,  20);
        dice[6] = new DiceCounter(this, R.drawable.d100, 100);
        mul  = new Counter(this, R.drawable.mul,  "mul");
        mod  = new Counter(this, R.drawable.mod,  "mod");

        TableLayout t = (TableLayout)findViewById(R.id.counters);
        for(DiceCounter c: dice)
            t.addView(c);
        t.addView(mul);
        t.addView(mod);

        setConfig(new ThirdConfig());

        Button reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                resetCounters();
            }
        });
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
    }

    private void roll()
    {
    }

    private void resetCounters()
    {
        for(Counter c: dice)
            c.resetValue();
        mul.setValue(1);
        mod.setValue(0);
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
        }

        public Integer getValue()
        {
            return new Integer(counter.getText().toString());
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

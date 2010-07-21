package au.id.swords.third;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;

public class ThirdActivity extends Activity
{
    Counter[] dice;
    Counter mul;
    Counter mod;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dice = new Counter[7];
        dice[0] = new Counter(this, R.drawable.d2,   "d2");
        dice[1] = new Counter(this, R.drawable.d4,   "d4");
        dice[2] = new Counter(this, R.drawable.d8,   "d8");
        dice[3] = new Counter(this, R.drawable.d10,  "d10");
        dice[4] = new Counter(this, R.drawable.d12,  "d12");
        dice[5] = new Counter(this, R.drawable.d20,  "d20");
        dice[6] = new Counter(this, R.drawable.d100, "d100");
        mul  = new Counter(this, R.drawable.mul,  "mul");
        mod  = new Counter(this, R.drawable.mod,  "mod");

        TableLayout t = (TableLayout)findViewById(R.id.counters);
        for(Counter c: dice)
            t.addView(c);
        t.addView(mul);
        t.addView(mod);
    }

    private class Counter extends TableRow
    {
        ImageButton button;
        EditText counter;

        public Counter(Context ctx, int image, String name)
        {
            super(ctx);

            LayoutInflater li;
            li = (LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            li.inflate(R.layout.counter, this);

            button = (ImageButton)findViewById(R.id.button);
            button.setImageResource(image);
            button.setOnClickListener(new ImageButton.OnClickListener()
            {
                public void onClick(View v)
                {
                    modValue(1);
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

        public Integer modValue(Integer mod)
        {
            Integer value = getValue();
            return setValue(value + mod);
        }
    }
}

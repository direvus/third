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
import java.lang.reflect.Field;

public class ThirdActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TableLayout t = (TableLayout)findViewById(R.id.counters);
        t.addView(new Counter(this, R.drawable.d2,   "d2"));
        t.addView(new Counter(this, R.drawable.d4,   "d4"));
        t.addView(new Counter(this, R.drawable.d8,   "d8"));
        t.addView(new Counter(this, R.drawable.d10,  "d10"));
        t.addView(new Counter(this, R.drawable.d12,  "d12"));
        t.addView(new Counter(this, R.drawable.d20,  "d20"));
        t.addView(new Counter(this, R.drawable.d100, "d100"));
        t.addView(new Counter(this, R.drawable.mul,  "mul"));
        t.addView(new Counter(this, R.drawable.mod,  "mod"));
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
            counter = (EditText)findViewById(R.id.counter);
        }
    }
}

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

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ThirdTriggerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private int mPreset;
    private int[] mDice;
    private TextView mTypeHelp;
    private EditText mResultsEdit;
    private Spinner mDiceSpinner;
    private Spinner mTypeSpinner;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);

        Intent intent = getIntent();
        setContentView(R.layout.trigger);
        setSupportActionBar((Toolbar) findViewById(R.id.trigger_toolbar));

        mPreset = intent.getIntExtra("preset", -1);
        ((TextView) findViewById(R.id.trigger_preset)).setText(intent.getStringExtra("config"));

        mTypeHelp = (TextView) findViewById(R.id.trigger_type_help);
        mResultsEdit = (EditText) findViewById(R.id.trigger_results);
        mDiceSpinner = (Spinner) findViewById(R.id.trigger_dice);
        mTypeSpinner = (Spinner) findViewById(R.id.trigger_type);

        ArrayAdapter<String> dice_adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item);

        mDice = intent.getIntArrayExtra("dice");
        for(int die: mDice)
            dice_adapter.add(String.format("d%d", die));

        dice_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDiceSpinner.setAdapter(dice_adapter);

        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(
                this,
                R.array.trigger_types,
                android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(type_adapter);
        mTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.trigger, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_save_trigger:
                try
                {
                    int die = mDice[mDiceSpinner.getSelectedItemPosition()];
                    ThirdTrigger.Type type = ThirdTrigger.Type.values()[mTypeSpinner.getSelectedItemPosition()];
                    String results = mResultsEdit.getText().toString();
                    ThirdTrigger trigger = new ThirdTrigger(die, type, results);
                    Intent intent = new Intent();
                    intent.putExtra("preset", mPreset);
                    intent.putExtra("trigger", trigger.toJSON().toString());
                    setResult(RESULT_OK, intent);
                }
                catch(Exception e)
                {
                    setResult(RESULT_CANCELED);
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        mTypeHelp.setText(getResources().getTextArray(
                    R.array.trigger_type_helps)[pos]);
    }

    public void onNothingSelected(AdapterView<?> parent)
    {
        mTypeHelp.setText("");
    }
}

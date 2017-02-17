/*
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ThirdAddInclude extends Activity
{
    private Integer mId;
    private TextView mConfig;
    private Spinner mPresets;
    private Button mOK;
    private Button mCancel;
    private Integer mInclude;
    private int[] mIds;
    private String[] mLabels;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_inc);

        mConfig = (TextView) findViewById(R.id.config);
        mPresets = (Spinner) findViewById(R.id.presets);
        mOK = (Button) findViewById(R.id.ok);
        mCancel = (Button) findViewById(R.id.cancel);

        Intent intent = getIntent();
        mId = intent.getIntExtra("id", -1);
        String config = intent.getStringExtra("config");
        if(config != null)
            mConfig.setText(config);

        mIds = intent.getIntArrayExtra("ids");
        mLabels = intent.getStringArrayExtra("labels");

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        for(int i = 0; i < mLabels.length; i++)
        {
            adapter.add(mLabels[i]);
        }
        mPresets.setAdapter(adapter);
        mPresets.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView parent, View v,
                                       int pos, long id)
            {
                mInclude = new Integer(mIds[pos]);
            }

            public void onNothingSelected(AdapterView parent)
            {
            }
        });

        mOK.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra("id", mId);
                intent.putExtra("include", mInclude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mCancel.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}

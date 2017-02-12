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
package au.id.swords.third;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ThirdNamePreset extends Activity
{
    private Integer mId;
    private Integer mInclude;
    private EditText mName;
    private TextView mConfig;
    private Button mOK;
    private Button mCancel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_preset);

        mName = (EditText)findViewById(R.id.name);
        mConfig = (TextView)findViewById(R.id.config);
        mOK = (Button)findViewById(R.id.ok);
        mCancel = (Button)findViewById(R.id.cancel);

        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);
        mInclude = intent.getIntExtra("include", 0);
        String config = intent.getStringExtra("config");

        if(config != null)
            mConfig.setText(config);

        String name = intent.getStringExtra("name");
        if(name != null)
            mName.setText(name);

        mOK.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra("id", mId);
                intent.putExtra("name", mName.getText().toString());
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

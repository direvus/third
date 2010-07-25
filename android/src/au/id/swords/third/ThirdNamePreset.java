package au.id.swords.third;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;

public class ThirdNamePreset extends Activity
{
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
                intent.putExtra("name", mName.getText().toString());
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

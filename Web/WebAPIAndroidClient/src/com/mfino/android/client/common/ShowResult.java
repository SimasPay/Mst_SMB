package com.mfino.android.client.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.android.client.R;

public class ShowResult extends Activity
{
    private Button CloseButton;
    private TextView ResultTV;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.resultscreen);
	
	Bundle extras = getIntent().getExtras();
	String str = extras.getString("result");	
	
	ResultTV = (TextView)findViewById(R.id.ResultTextView);
	ResultTV.setText(str);	
	CloseButton = (Button)findViewById(R.id.CloseResultScreen);
	CloseButton.setOnClickListener(closelistener);
    }
    
    private OnClickListener closelistener = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            ShowResult.this.finish();    	
        }
    };
}

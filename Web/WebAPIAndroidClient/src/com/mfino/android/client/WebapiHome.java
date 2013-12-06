package com.mfino.android.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.android.client.bank.BankHome;
import com.mfino.android.client.merchant.MerchantHome;
import com.mfino.android.client.subscriber.SubscriberHome;

public class WebapiHome extends Activity
{
    
    private TextView SubscriberActivationTV;
    private TextView MerchantActivationTV;
    private TextView BankActivationTV;
    
    private Button Closebtn;
    
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.webapi_home);
	
	SubscriberActivationTV = (TextView)findViewById(R.id.SubscriberInHome);
	MerchantActivationTV = (TextView)findViewById(R.id.MerchantInHome);
	BankActivationTV = (TextView)findViewById(R.id.BankInHome);
	Closebtn = (Button)findViewById(R.id.CloseBtn);
	
	SubscriberActivationTV.setOnClickListener(socl);
	MerchantActivationTV.setOnClickListener(mocl);
	BankActivationTV.setOnClickListener(bocl);
	Closebtn.setOnClickListener(cocl);
    }
    
    private OnClickListener cocl = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            WebapiHome.this.finish();
        }
    };
    
    private OnClickListener socl = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent si = new Intent(WebapiHome.this,SubscriberHome.class);
            startActivity(si);
        }
    };
    private OnClickListener mocl = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent mi = new Intent(WebapiHome.this,MerchantHome.class);
            startActivity(mi);
        }
    };
    private OnClickListener bocl = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent bi = new Intent(WebapiHome.this,BankHome.class);
            startActivity(bi);
        }
    };
}

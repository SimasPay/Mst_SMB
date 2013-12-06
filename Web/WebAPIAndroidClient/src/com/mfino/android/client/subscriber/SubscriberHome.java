package com.mfino.android.client.subscriber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.android.client.bank.BankHome;
import com.mfino.android.client.subscriber.SubscriberActivationScreen;
import com.mfino.android.client.subscriber.SubscriberShareLoadScreen;
import com.mfino.android.client.R;

public class SubscriberHome extends Activity
{
    TextView SubscriberActivationTV;
    TextView SubscriberShareLoadTV;
    TextView SubscriberhangePinTV;
    TextView SubscriberResetPinTV;
    TextView SubscriberLastNTxnsTV;
    
    Button GoBack;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.subscriber_home);

	SubscriberActivationTV = (TextView) findViewById(R.id.ActivationTextView);
	SubscriberShareLoadTV = (TextView) findViewById(R.id.ShareLoadTextView);
	SubscriberhangePinTV = (TextView) findViewById(R.id.ChangePinTextView);
	SubscriberResetPinTV = (TextView) findViewById(R.id.ResetPinTextView);
	SubscriberLastNTxnsTV = (TextView) findViewById(R.id.LastNTxnsTextView);
	GoBack =(Button)findViewById(R.id.GoBackInSubscriberHome);

	SubscriberActivationTV.setOnClickListener(sact);
	SubscriberShareLoadTV.setOnClickListener(ssha);
	SubscriberhangePinTV.setOnClickListener(scha);
	SubscriberResetPinTV.setOnClickListener(sres);
	SubscriberLastNTxnsTV.setOnClickListener(slas);
	GoBack.setOnClickListener(sgb);

    }
    private OnClickListener sgb = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            SubscriberHome.this.finish();
        }
    };
    private OnClickListener sact = new OnClickListener()
	                         {
	                             @Override
	                             public void onClick(View v)
	                             {
		                         Intent activateIntent = new Intent(SubscriberHome.this, SubscriberActivationScreen.class);
		                         startActivity(activateIntent);
	                             }
	                         };
    private OnClickListener ssha = new OnClickListener()
	                         {
	                             @Override
	                             public void onClick(View v)
	                             {
		                         Intent activateIntent = new Intent(SubscriberHome.this, SubscriberShareLoadScreen.class);
		                         startActivity(activateIntent);
	                             }
	                         };
    private OnClickListener scha = new OnClickListener()
	                         {
	                             @Override
	                             public void onClick(View v)
	                             {
		                         Intent activateIntent = new Intent(SubscriberHome.this, com.mfino.android.client.subscriber.SubscriberChangePinScreen.class);
		                         startActivity(activateIntent);
	                             }
	                         };
    private OnClickListener sres = new OnClickListener()
	                         {
	                             @Override
	                             public void onClick(View v)
	                             {
		                         Intent activateIntent = new Intent(SubscriberHome.this, com.mfino.android.client.subscriber.SubscriberResetPinScreen.class);
		                         startActivity(activateIntent);
	                             }
	                         };
    private OnClickListener slas = new OnClickListener()
	                         {
	                             @Override
	                             public void onClick(View v)
	                             {
		                         Intent activateIntent = new Intent(SubscriberHome.this, com.mfino.android.client.subscriber.SubscriberLastNTxnsScreen.class);
		                         startActivity(activateIntent);
	                             }
	                         };

}
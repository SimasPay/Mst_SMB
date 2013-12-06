package com.mfino.android.client.merchant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.android.client.R;
import com.mfino.android.client.bank.BankHome;

public class MerchantHome extends Activity
{
    TextView MerchantChangePinTV;
    TextView MerchantResetPinTV;
    TextView MerchantLastNTxnsTV;
    TextView MerchantAirtimeTransferTV;
    TextView MerchantRechargeTV;
    TextView MerchantCheckBalanceTV;
    Button GoBack;

    /** Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.merchant_home);

	MerchantAirtimeTransferTV = (TextView) findViewById(R.id.MerchantAirtimeTransferTextView);
	MerchantResetPinTV = (TextView) findViewById(R.id.MerchantResetPinTextView);
	MerchantChangePinTV = (TextView) findViewById(R.id.MerchantChangePinTextView);
	MerchantCheckBalanceTV = (TextView) findViewById(R.id.MerchantCheckBalanceTextView);
	MerchantLastNTxnsTV = (TextView) findViewById(R.id.MerchantLastNTxnsTextView);
	MerchantRechargeTV = (TextView) findViewById(R.id.MerchantRechargeTextView);
	GoBack =(Button)findViewById(R.id.GoBackInMerchantHome);

	MerchantAirtimeTransferTV.setOnClickListener(mat);
	MerchantChangePinTV.setOnClickListener(mcp);
	MerchantCheckBalanceTV.setOnClickListener(mcb);
	MerchantLastNTxnsTV.setOnClickListener(mlt);
	MerchantRechargeTV.setOnClickListener(mrc);
	MerchantResetPinTV.setOnClickListener(mrs);
	GoBack.setOnClickListener(mgb);
    }
    private OnClickListener mgb = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            MerchantHome.this.finish();
        }
    };
    
    private OnClickListener mat = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(MerchantHome.this, com.mfino.android.client.merchant.MerchantAirtimeTransferScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener mcp = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(MerchantHome.this, com.mfino.android.client.merchant.MerchantChangePinScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener mcb = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(MerchantHome.this, com.mfino.android.client.merchant.MerchantCheckBalanceScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener mlt = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(MerchantHome.this, com.mfino.android.client.merchant.MerchantLastNTxnsScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener mrc = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(MerchantHome.this, com.mfino.android.client.merchant.MerchantRechargeScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener mrs = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(MerchantHome.this, com.mfino.android.client.merchant.MerchantResetPinScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
}

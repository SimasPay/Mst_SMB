package com.mfino.android.client.bank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.android.client.R;
public class BankHome extends Activity
{

    TextView BankActivationTV;
    TextView BankChangePinTV;
    TextView BankCheckBalanceTV;
    TextView BankLastThreeTxnsTV;
    TextView BankMoneyTransferTV;
    TextView BankRechargeTV;
    TextView BankTransferInquiryTV;
    
    Button GoBack;

    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.bank_home);

	BankActivationTV = (TextView) findViewById(R.id.BankActivationTextView);
	BankChangePinTV = (TextView) findViewById(R.id.BankChangePinTextView);
	BankCheckBalanceTV = (TextView) findViewById(R.id.BankCheckBalanceTextView);
	BankLastThreeTxnsTV = (TextView) findViewById(R.id.BankLastThreeTxnsTextView);
	BankMoneyTransferTV = (TextView) findViewById(R.id.BankMoneyTransferTextView);
	BankRechargeTV = (TextView) findViewById(R.id.BankRechargeTextView);
	BankTransferInquiryTV = (TextView) findViewById(R.id.BankTransferInquiryTextView);
	GoBack =(Button)findViewById(R.id.GoBackInBankHome);

	BankActivationTV.setOnClickListener(bat);
	BankChangePinTV.setOnClickListener(bcp);
	BankCheckBalanceTV.setOnClickListener(bcb);
	BankLastThreeTxnsTV.setOnClickListener(blt);
	BankMoneyTransferTV.setOnClickListener(bmt);
	BankRechargeTV.setOnClickListener(brc);
	BankTransferInquiryTV.setOnClickListener(bti);
	GoBack.setOnClickListener(bgb);

    }

    private OnClickListener bgb = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            BankHome.this.finish();
        }
    };
    
    
    private OnClickListener bat = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(BankHome.this, com.mfino.android.client.bank.BankActivationScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener bcp = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(BankHome.this, com.mfino.android.client.bank.BankChangePinScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener bcb = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(BankHome.this, com.mfino.android.client.bank.BankCheckBalanceScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener blt = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(BankHome.this, com.mfino.android.client.bank.BankLastNTxnsScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener bmt = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(BankHome.this, com.mfino.android.client.bank.BankMoneyTransferScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener brc = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(BankHome.this, com.mfino.android.client.bank.BankRechargeScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };
    private OnClickListener bti = new OnClickListener()
	                        {
	                            @Override
	                            public void onClick(View v)
	                            {
		                        Intent activateIntent = new Intent(BankHome.this, com.mfino.android.client.bank.BankTransferInquiryScreen.class);
		                        startActivity(activateIntent);
	                            }
	                        };

}

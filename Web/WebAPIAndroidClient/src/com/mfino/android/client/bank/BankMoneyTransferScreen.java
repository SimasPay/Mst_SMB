package com.mfino.android.client.bank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mfino.android.client.R;
import com.mfino.android.client.common.HttpConnectionManager;
import com.mfino.android.client.common.Utils;

public class BankMoneyTransferScreen extends Activity
{
    private EditText SourceMDNET;
    private EditText SourcePINET;
    private EditText DestMDNET;
    private EditText AmountET;
    private EditText BankIDET;
    private EditText TransferIDET;
    private EditText ParentTxnIDET;
    
    private Button ActivateButton;
    private Button GoBackButton;    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.bank_moneytransfer);
	
	SourceMDNET = (EditText)findViewById(R.id.bamtSourceMDNET);
	SourcePINET = (EditText)findViewById(R.id.bamtSourcePINET);
	DestMDNET = (EditText)findViewById(R.id.bamtDestinationMDNET);
	AmountET = (EditText)findViewById(R.id.bamtAmountET);
	BankIDET = (EditText)findViewById(R.id.bamtBankIDET);
	TransferIDET = (EditText)findViewById(R.id.bamtTransferIDET);
	ParentTxnIDET = (EditText)findViewById(R.id.bamtParentTxnIDET);
	
	
	ActivateButton = (Button)findViewById(R.id.bamtMoneyTransferButton);
	ActivateButton.setOnClickListener(clickActivate);
	GoBackButton = (Button)findViewById(R.id.bamtBackButton);
	GoBackButton.setOnClickListener(clickBack);
	
    }
    
    private OnClickListener clickActivate = new OnClickListener()
    {   
        @Override
        public void onClick(View v)
        {
    		List<NameValuePair> params = new ArrayList<NameValuePair>(10);
    		params.add(new BasicNameValuePair(Utils.SourceMDN,SourceMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.SourcePIN, SourcePINET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.DestMDN, DestMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.Amount, AmountET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.BankId, BankIDET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.TransferID,TransferIDET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.ParentTxnID,ParentTxnIDET.getText().toString()));
    		
    		params.add(new BasicNameValuePair(Utils.Mode,Utils.BankModeValue));
    		params.add(new BasicNameValuePair(Utils.ServiceName,Utils.MoneyTransfer));
    		params.add(new BasicNameValuePair(Utils.ChannelID,Utils.ChannelIDValue));
    		String str=null;
    		try
                {
    		    HttpConnectionManager cm = new HttpConnectionManager(Utils.URL,params);
    		     
    		      
    		    cm.execute();    		    
    		    str = cm.getResponseContent();
                }                
    		catch(IOException e)
    		{
                    Toast.makeText(BankMoneyTransferScreen.this,"Connection Error",Toast.LENGTH_LONG).show();
                    return;    		    
    		}
                catch(Exception ex)
                {
                    Toast.makeText(BankMoneyTransferScreen.this,"Error",Toast.LENGTH_LONG).show();
                    return;
                }
                
                Intent resultIntent  = new Intent(BankMoneyTransferScreen.this,com.mfino.android.client.common.ShowResult.class);
                resultIntent.putExtra("result", str);
            	startActivity(resultIntent); 
            
            	BankMoneyTransferScreen.this.finish();
        }
    };
    private OnClickListener clickBack = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            BankMoneyTransferScreen.this.finish();
        }
    };
    
}

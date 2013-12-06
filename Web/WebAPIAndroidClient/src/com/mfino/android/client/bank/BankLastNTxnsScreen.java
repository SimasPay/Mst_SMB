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

public class BankLastNTxnsScreen extends Activity
{
    private EditText SourceMDNET;
    private EditText SourcePINET;
    private EditText BankIDET;
    
    private Button ActivateButton;
    private Button GoBackButton;    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.bank_lastthreetxns);
	
	SourceMDNET = (EditText)findViewById(R.id.baltSourceMDNET);
	SourcePINET = (EditText)findViewById(R.id.baltSourcePINET);
	BankIDET = (EditText)findViewById(R.id.baltBankIDET);	
	
	ActivateButton = (Button)findViewById(R.id.baltLastNTxnsButton);
	ActivateButton.setOnClickListener(clickActivate);
	GoBackButton = (Button)findViewById(R.id.baltBackButton);
	GoBackButton.setOnClickListener(clickBack);
	
    }
    
    private OnClickListener clickActivate = new OnClickListener()
    {   
        @Override
        public void onClick(View v)
        {
    		List<NameValuePair> params = new ArrayList<NameValuePair>(6);
    		params.add(new BasicNameValuePair(Utils.SourceMDN,SourceMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.SourcePIN, SourcePINET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.BankId, BankIDET.getText().toString()));
    		
    		params.add(new BasicNameValuePair(Utils.Mode,Utils.BankModeValue));
    		params.add(new BasicNameValuePair(Utils.ServiceName,Utils.LastNTxns));
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
                    Toast.makeText(BankLastNTxnsScreen.this,"Connection Error",Toast.LENGTH_LONG).show();
                    return;    		    
    		}
                catch(Exception ex)
                {
                    Toast.makeText(BankLastNTxnsScreen.this,"Error",Toast.LENGTH_LONG).show();
                    return;
                }
                
                Intent resultIntent  = new Intent(BankLastNTxnsScreen.this,com.mfino.android.client.common.ShowResult.class);
                resultIntent.putExtra("result", str);
            	startActivity(resultIntent); 
            
            	BankLastNTxnsScreen.this.finish();
        }
    };
    private OnClickListener clickBack = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            BankLastNTxnsScreen.this.finish();
        }
    };
    
}

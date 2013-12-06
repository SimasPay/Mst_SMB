package com.mfino.android.client.merchant;

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

public class MerchantResetPinScreen extends Activity
{
    private EditText SourceMDNET;
    private EditText NewPINET;
    private EditText SecretAnswerET;
    
    private Button ActivateButton;
    private Button GoBackButton;    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.merchant_resetpin);
	
	SourceMDNET = (EditText)findViewById(R.id.merpSourceMDNET);
	NewPINET = (EditText)findViewById(R.id.merpNewPinET);
	SecretAnswerET = (EditText)findViewById(R.id.merpSecretAnswerET);
	
	ActivateButton = (Button)findViewById(R.id.merpResetPinButton);
	ActivateButton.setOnClickListener(clickActivate);
	GoBackButton = (Button)findViewById(R.id.merpBackButton);
	GoBackButton.setOnClickListener(clickBack);
	
    }
    
    private OnClickListener clickActivate = new OnClickListener()
    {   
        @Override
        public void onClick(View v)
        {
    		List<NameValuePair> params = new ArrayList<NameValuePair>(6);
    		params.add(new BasicNameValuePair(Utils.SourceMDN,SourceMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.NewPIN, NewPINET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.SecretAnswer, SecretAnswerET.getText().toString()));
    		
    		params.add(new BasicNameValuePair(Utils.Mode,Utils.MerchantModeValue));
    		params.add(new BasicNameValuePair(Utils.ServiceName,Utils.ResetPin));
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
                    Toast.makeText(MerchantResetPinScreen.this,"Connection Error",Toast.LENGTH_LONG).show();
                    return;    		    
    		}
                catch(Exception ex)
                {
                    Toast.makeText(MerchantResetPinScreen.this,"Error",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent resultIntent  = new Intent(MerchantResetPinScreen.this,com.mfino.android.client.common.ShowResult.class);
                resultIntent.putExtra("result", str);
            	startActivity(resultIntent); 
            
            	MerchantResetPinScreen.this.finish();
            
//            Toast.makeText(ActivationScreen.this, "Activate", Toast.LENGTH_LONG).show();            
        }
    };
    private OnClickListener clickBack = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            MerchantResetPinScreen.this.finish();
        }
    };
    
}

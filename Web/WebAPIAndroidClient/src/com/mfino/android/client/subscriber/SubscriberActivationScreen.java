package com.mfino.android.client.subscriber;

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

public class SubscriberActivationScreen extends Activity
{
    private EditText SourceMDNET;
    private EditText SourcePINET;
    private EditText SecretAnswerET;
    private EditText ContactNumberET;
    
    private Button ActivateButton;
    private Button GoBackButton;    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.subscriber_activation);
	
	SourceMDNET = (EditText)findViewById(R.id.acSourceMDNET);
	SourcePINET = (EditText)findViewById(R.id.acSourcePINET);
	SecretAnswerET = (EditText)findViewById(R.id.acSecretAnswerET);
	ContactNumberET = (EditText)findViewById(R.id.acContactNumberET);
	
	ActivateButton = (Button)findViewById(R.id.acActivateButton);
	ActivateButton.setOnClickListener(clickActivate);
	GoBackButton = (Button)findViewById(R.id.acBackButton);
	GoBackButton.setOnClickListener(clickBack);
	
    }
    
    private OnClickListener clickActivate = new OnClickListener()
    {   
        @Override
        public void onClick(View v)
        {           	
    		List<NameValuePair> params = new ArrayList<NameValuePair>(7);
    		params.add(new BasicNameValuePair(Utils.SourceMDN,SourceMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.SourcePIN, SourcePINET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.SecretAnswer, SecretAnswerET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.ContactNumber, ContactNumberET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.Mode,Utils.SubscriberModeValue));
    		params.add(new BasicNameValuePair(Utils.ServiceName,Utils.Activation));
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
                    Toast.makeText(SubscriberActivationScreen.this,"Connection Error",Toast.LENGTH_LONG).show();
                    return;    		    
    		}
                catch(Exception ex)
                {
                    Toast.makeText(SubscriberActivationScreen.this,"Error",Toast.LENGTH_LONG).show();
                    return;
                }
                
                Intent resultIntent  = new Intent(SubscriberActivationScreen.this,com.mfino.android.client.common.ShowResult.class);
                resultIntent.putExtra("result", str);
            	startActivity(resultIntent); 
            
            	SubscriberActivationScreen.this.finish();
        }
    };
    private OnClickListener clickBack = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            SubscriberActivationScreen.this.finish();
        }
    };
}

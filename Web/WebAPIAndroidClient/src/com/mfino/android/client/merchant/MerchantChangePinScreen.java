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

public class MerchantChangePinScreen extends Activity
{
    private EditText SourceMDNET;
    private EditText OldPINET;
    private EditText NewPinET;
    
    private Button ChangePinButton;
    private Button GoBackButton;    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.merchant_changepin);
	
	SourceMDNET = (EditText)findViewById(R.id.mecpSourceMDNET);
	OldPINET = (EditText)findViewById(R.id.mecpOldPINET);
	NewPinET = (EditText)findViewById(R.id.mecpNewPinET);
	
	ChangePinButton = (Button)findViewById(R.id.mecpChangePinButton);
	ChangePinButton.setOnClickListener(clickActivate);
	GoBackButton = (Button)findViewById(R.id.mecpBackButton);
	GoBackButton.setOnClickListener(clickBack);
	
    }
    
    private OnClickListener clickActivate = new OnClickListener()
    {   
        @Override
        public void onClick(View v)
        {
    		List<NameValuePair> params = new ArrayList<NameValuePair>(6);
    		params.add(new BasicNameValuePair(Utils.SourceMDN,SourceMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.OldPIN, OldPINET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.NewPIN, NewPinET.getText().toString()));
    		
    		params.add(new BasicNameValuePair(Utils.Mode,Utils.MerchantModeValue));
    		params.add(new BasicNameValuePair(Utils.ServiceName, Utils.ChangePin));
    		params.add(new BasicNameValuePair(Utils.ChannelID, Utils.ChannelIDValue));
    		String str=null;
    		try
                {
    		    HttpConnectionManager cm = new HttpConnectionManager(Utils.URL,params);
    		     
    		      
    		    cm.execute();    		    
    		    str = cm.getResponseContent();
                }                
    		catch(IOException e)
    		{
                    Toast.makeText(MerchantChangePinScreen.this,"Connection Error",Toast.LENGTH_LONG).show();
                    return;    		    
    		}
                catch(Exception ex)
                {
                    Toast.makeText(MerchantChangePinScreen.this,"Error",Toast.LENGTH_LONG).show();
                    return;
                }  
                
                Intent resultIntent  = new Intent(MerchantChangePinScreen.this,com.mfino.android.client.common.ShowResult.class);
                resultIntent.putExtra("result", str);
            	startActivity(resultIntent); 
            
            	MerchantChangePinScreen.this.finish();
               
        }
    };
    private OnClickListener clickBack = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            MerchantChangePinScreen.this.finish();
        }
    };
    
}

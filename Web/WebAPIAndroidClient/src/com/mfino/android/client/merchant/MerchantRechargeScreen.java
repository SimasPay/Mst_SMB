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

public class MerchantRechargeScreen extends Activity
{
    private EditText SourceMDNET;
    private EditText SourcePINET;
    private EditText DestMDNET;
    private EditText AmountET;
    private EditText BucketTypeET;
    
    private Button ActivateButton;
    private Button GoBackButton;    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.merchant_recharge);
	
	SourceMDNET = (EditText)findViewById(R.id.mercSourceMDNET);
	SourcePINET = (EditText)findViewById(R.id.mercSourcePINET);
	DestMDNET = (EditText)findViewById(R.id.mercDestinationMDNET);
	AmountET = (EditText)findViewById(R.id.mercAmountET);
	BucketTypeET = (EditText)findViewById(R.id.mercBucketTypeET);
	
	ActivateButton = (Button)findViewById(R.id.mercRechargeButton);
	ActivateButton.setOnClickListener(clickActivate);
	GoBackButton = (Button)findViewById(R.id.mercBackButton);
	GoBackButton.setOnClickListener(clickBack);
	
    }
    
    private OnClickListener clickActivate = new OnClickListener()
    {   
        @Override
        public void onClick(View v)
        {
    		List<NameValuePair> params = new ArrayList<NameValuePair>(8);
    		params.add(new BasicNameValuePair(Utils.SourceMDN,SourceMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.SourcePIN, SourcePINET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.DestMDN, DestMDNET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.Amount, AmountET.getText().toString()));
    		params.add(new BasicNameValuePair(Utils.BucketType, BucketTypeET.getText().toString()));
    		
    		params.add(new BasicNameValuePair(Utils.Mode,Utils.MerchantModeValue));
    		params.add(new BasicNameValuePair(Utils.ServiceName,Utils.Recharge));
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
                    Toast.makeText(MerchantRechargeScreen.this,"Connection Error",Toast.LENGTH_LONG).show();
                    return;    		    
    		}
                catch(Exception ex)
                {
                    Toast.makeText(MerchantRechargeScreen.this,"Error",Toast.LENGTH_LONG).show();
                    return;
                }
                
                Intent resultIntent  = new Intent(MerchantRechargeScreen.this,com.mfino.android.client.common.ShowResult.class);
                resultIntent.putExtra("result", str);
            	startActivity(resultIntent); 
            
            	MerchantRechargeScreen.this.finish();
        }
    };
    private OnClickListener clickBack = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            MerchantRechargeScreen.this.finish();
        }
    };
    
}

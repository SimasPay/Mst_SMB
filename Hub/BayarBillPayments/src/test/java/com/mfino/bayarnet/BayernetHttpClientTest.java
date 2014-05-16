package com.mfino.bayarnet;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mfino.bayar.service.BayarHttpConnector;
import com.mfino.bayar.service.BayarWebServiceResponse;


/**
 * 
 * @author Amar
 *
 */
public class BayernetHttpClientTest {

	public static void main(String[] args)
	{
		String url = "https://202.51.252.110/demo/h2h/";
		String method = "inquiry_bill";
				
		BayarHttpConnector mfinoHttpConnector = new BayarHttpConnector();
		mfinoHttpConnector.setTimeout("30000");
		mfinoHttpConnector.setUrl(url);
		
		BayarWebServiceResponse response = null;
		try {
			
			List <NameValuePair> parameters = new ArrayList <NameValuePair>();
			parameters.add(new BasicNameValuePair("partner_id", "smartfren"));
			parameters.add(new BasicNameValuePair("api_key", "1D02640903DF5E9126EDB9FC46C6FB70"));
			parameters.add(new BasicNameValuePair("product_code", "TAG031"));
			parameters.add(new BasicNameValuePair("bill_number", "6576587"));
			parameters.add(new BasicNameValuePair("reference_id", ""));
			mfinoHttpConnector.setParams(new HashMap<String,String>());
			//String urlParams = "partner_id=smartfren&api_key=1D02640903DF5E9126EDB9FC46C6FB70&product_code=TAG031&bill_number=6576587";
			response = mfinoHttpConnector.sendHttpsRequest(method, parameters);	
			
//			parameters = new ArrayList <NameValuePair>();
//			parameters.add(new BasicNameValuePair("partner_id", "smartfren"));
//			parameters.add(new BasicNameValuePair("api_key", "1D02640903DF5E9126EDB9FC46C6FB70"));
//			parameters.add(new BasicNameValuePair("payment_code", response.getPaymentCode()));
//			mfinoHttpConnector.setParams(new HashMap<String,String>());
//			response = mfinoHttpConnector.sendHttpRequest("pay_bill", parameters);
			
			
//		} catch (SocketTimeoutException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("Response:"+ response);
	}
}

package com.mfino.sterling;

import com.mfino.sterling.bank.communicator.SterlingBankWSClient;
import com.mfino.sterling.service.MfinoHttpConnector;

/**
 * 
 * @author Amar
 *
 */
public class SterlingBankHttpClientTest {

	public static void main(String[] args)
	{
		String url = "http://localhost:8080/SterlingBankMock/services/IBSServices/IBSBridge";
		String xmlRequest = "<IBSRequest><ReferenceID>0000001</ReferenceID><RequestType>201</RequestType><Account>4568984225</Account></IBSRequest>";
		SterlingBankWSClient httpclient = new SterlingBankWSClient();
		httpclient.setUrl(url);
		
		MfinoHttpConnector mfinoHttpConnector = new MfinoHttpConnector();
		mfinoHttpConnector.setTimeout("30000");
		String response = mfinoHttpConnector.sendHttpRequest(url, xmlRequest);
		System.out.println("xmlResponse:"+ response);
	}
}

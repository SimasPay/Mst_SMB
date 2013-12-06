package com.mfino.atpp.communicator;

import java.math.BigDecimal;

import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.atpp.service.MfinoHttpConnector;
/**
 * @author Satya
 *
 */
public class AtppHttpClient {
	
	private static final Logger	log = LoggerFactory.getLogger(AtppHttpClient.class);
	private String airtimePinPurchaseUrl;

	private MfinoHttpConnector httpClient;
	
	private static final String airtimePinPurchaseXmlRequest=
			"<AirtimePINPurchase>" +
			"<Amount>$amount</Amount>" +
			"<PaymentCode>$paymentCode</PaymentCode>" +
			"<CustomerID>$customerID</CustomerID>" +
			"<CustomerEmail>$customerEmail</CustomerEmail>" +
			"<TerminalID>$terminalID</TerminalID>" +
			"<RequestReference>$requestReference</RequestReference>" +
			"</AirtimePINPurchase>";
	
	
	
	
	/**
	 * Airtime Pin Purchase request
	 * @param userId
	 * @param sourceMDN
	 * @param amount
	 * @param paymentCode
	 * @param customerID
	 * @param customerEmail
	 * @param terminalID
	 * @param requestReference
	 * @return
	 * @throws Exception 
	 */
	public String createAndSendAtppRequest(String sourceMDN, BigDecimal amount, String paymentCode,String customerID,String customerEmail,String terminalID, String requestReference) throws Exception{
		requestReference = padOnLeft(requestReference, '0', 9);
		String xmlRequest =  createRequest(airtimePinPurchaseXmlRequest, (amount.toBigInteger()).toString()+"00", paymentCode, customerID, customerEmail, terminalID, requestReference);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;
		xmlResponse = httpClient.sendHttpRequest(airtimePinPurchaseUrl,xmlRequest);	    
		return xmlResponse;	
	}
	
	
	
	/**
	 * Constructs the request by replacing the values for parameters in the request
	 * @param xmlRequest
	 * @param userId
	 * @param sourceMDN
	 * @param amount
	 * @param paymentCode
	 * @param customerID
	 * @param customerEmail
	 * @param terminalID
	 * @param requestReference
	 * @return
	 */
	private String createRequest(String xmlRequest, String amount, String paymentCode,String customerID,String customerEmail,String terminalID, String requestReference) {
		log.info("Creating the xml request");
		if(xmlRequest.contains("$amount")){
			xmlRequest = xmlRequest.replace("$amount", amount);
		}
		if(xmlRequest.contains("$paymentCode")){
			xmlRequest = xmlRequest.replace("$paymentCode", paymentCode);
		}
		if(xmlRequest.contains("$customerID")){
			xmlRequest = xmlRequest.replace("$customerID", customerID);
		}
		if(xmlRequest.contains("$customerEmail")){
			xmlRequest = xmlRequest.replace("$customerEmail", customerEmail);
		}
		if(xmlRequest.contains("$terminalID")){
			xmlRequest = xmlRequest.replace("$terminalID", terminalID);
		}
		if(xmlRequest.contains("$requestReference")){
			xmlRequest = xmlRequest.replace("$requestReference", requestReference);
		}
		return xmlRequest;	
	}
	
	public static String padOnLeft(String str, char paddingChar, int finalLength) throws Exception {
		if (finalLength == str.length())
			return str;
		if (finalLength < str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for (int i = 0; i < finalLength - str.length(); i++)
			s = s + String.valueOf(paddingChar);
		str = s + str;
		return str;
	}
	
	public MfinoHttpConnector getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(MfinoHttpConnector httpClient) {
		this.httpClient = httpClient;
	}
	
	public String getAirtimePinPurchaseUrl() {
		return airtimePinPurchaseUrl;
	}
	public void setAirtimePinPurchaseUrl(String airtimePinPurchaseUrl) {
		this.airtimePinPurchaseUrl = airtimePinPurchaseUrl;
	}
	
}
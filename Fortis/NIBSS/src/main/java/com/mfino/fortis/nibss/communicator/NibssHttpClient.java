package com.mfino.fortis.nibss.communicator;

import java.math.BigDecimal;

import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.service.MfinoHttpConnector;

public class NibssHttpClient {
	
	private static final Logger	log = LoggerFactory.getLogger(NibssHttpClient.class);

	private String verifyCustomerUrl;
	private String transferUrl;
	private String queryTransactionUrl;
	private MfinoHttpConnector httpClient;
	
	private static final String customerVerificationXmlRequest=
			"<customerVerificationRequest>" +
			"<destinationCode>$destinationCode</destinationCode>" +
			"<accountIdentificationNumber>$accountIdentificationNumber</accountIdentificationNumber>" +
			"</customerVerificationRequest>";
	private static final String transferXmlRequest=
			"<transferRequest>" +
			"<destinationCode>$destinationCode</destinationCode>" +
			"<accountIdentificationName>$accountIdentificationName</accountIdentificationName>" +
			"<accountIdentificationNumber>$accountIdentificationNumber</accountIdentificationNumber>" +
			"<originatorName>$originatorName</originatorName>" +
			"<narration>$narration</narration>" +
			"<paymentReference>$paymentReference</paymentReference>" +
			"<amount>$amount</amount>" +
			"</transferRequest>";
	private static final String queryTransactionXmlRequest = "<transactionQueryRequest>" +
			"<destinationCode>$destinationCode</destinationCode>" +
			"<paymentReference>$paymentReference</paymentReference>" +
			"</transactionQueryRequest>";
	
	/**
	 * Name Inquiry Request
	 * @param destCode//ben op code
	 * @param accIdentificationNum//invoice no
	 * @return
	 * @throws Exception 
	 */
	public String createAndSendCustomerNameInquiryRequest(String destCode, String accIdentificationNum){
		String xmlRequest =  createRequest(customerVerificationXmlRequest,destCode,accIdentificationNum, null, null, null, null, null);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(verifyCustomerUrl, xmlRequest);
		return xmlResponse;	
	}
	
	/**
	 * Funds transfer request
	 * @param userId
	 * @param sourceMDN
	 * @param destCode
	 * @param accIdentificationNum
	 * @param amount
	 * @param narration
	 * @param sctlId
	 * @param originatorName
	 * @param accIdentificationName
	 * @return
	 * @throws Exception 
	 */
	public String createAndSendTransferRequest(Long userId, String sourceMDN,String destCode, String accIdentificationNum,BigDecimal amount,String narration,String sctlId,
			String originatorName,String accIdentificationName){
		String xmlRequest =  createRequest(transferXmlRequest, destCode, accIdentificationNum, accIdentificationName, originatorName, narration, sctlId, amount.toString());
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;
		xmlResponse = httpClient.sendHttpRequest(transferUrl,xmlRequest);	    
		return xmlResponse;	
	}
	
	/**
	 * Transaction status request
	 * @param destCode
	 * @param sctlId
	 * @return
	 * @throws Exception
	 */
	public String createAndSendTransactionQueryRequest(String destCode, String sctlId){
		String xmlRequest =  createRequest(queryTransactionXmlRequest, destCode, null, null, null, null, sctlId, null);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;
		xmlResponse = httpClient.sendHttpRequest(queryTransactionUrl,xmlRequest);
			    
		return xmlResponse;	
	}
	
	/**
	 * Constructs the request by replacing the values for parameters in the request
	 * @param xmlRequest
	 * @param destCode
	 * @param accIdentificationNum
	 * @param accIdentificationName
	 * @param originatorName
	 * @param narration
	 * @param paymentReference
	 * @param amount
	 * @param transactionNumber
	 * @return
	 */
	private String createRequest(String xmlRequest,String destCode, String accIdentificationNum,String accIdentificationName,String originatorName,String narration,
			String paymentReference,String amount) {
		log.info("Creating the xml request");
		if(xmlRequest.contains("$destinationCode")){
			xmlRequest = xmlRequest.replace("$destinationCode", destCode);
		}
		if(xmlRequest.contains("$accountIdentificationNumber")){
			xmlRequest = xmlRequest.replace("$accountIdentificationNumber", accIdentificationNum);
		}
		if(xmlRequest.contains("$accountIdentificationName")){
			xmlRequest = xmlRequest.replace("$accountIdentificationName", accIdentificationName);
		}
		if(xmlRequest.contains("$originatorName")){
			xmlRequest = xmlRequest.replace("$originatorName", originatorName);
		}
		if(xmlRequest.contains("$narration")){
			xmlRequest = xmlRequest.replace("$narration", narration);
		}
		if(xmlRequest.contains("$paymentReference")){
			xmlRequest = xmlRequest.replace("$paymentReference", paymentReference);
		}
		if(xmlRequest.contains("$amount")){
			xmlRequest = xmlRequest.replace("$amount", amount);
		}
		return xmlRequest;	
	}
	
	/*public String createAndSendTransactionQuerySingleItemRequest(String string,
			String string2) {
		return null;
	}*/
	
	public String getVerifyCustomerUrl() {
		return verifyCustomerUrl;
	}
	public void setVerifyCustomerUrl(String verifyCustomerUrl) {
		this.verifyCustomerUrl = verifyCustomerUrl;
	}
	
	public String getTransferUrl() {
		return transferUrl;
	}
	public void setTransferUrl(String transferUrl) {
		this.transferUrl = transferUrl;
	}
	
	public String getQueryTransactionUrl() {
		return queryTransactionUrl;
	}
	public void setQueryTransactionUrl(String queryTransactionUrl) {
		this.queryTransactionUrl = queryTransactionUrl;
	}

	public MfinoHttpConnector getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(MfinoHttpConnector httpClient) {
		this.httpClient = httpClient;
	}
	
	
	
}
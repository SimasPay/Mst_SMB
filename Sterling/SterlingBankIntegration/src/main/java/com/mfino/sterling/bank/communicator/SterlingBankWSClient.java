package com.mfino.sterling.bank.communicator;

import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.sterling.bank.util.SterlingBankWebServiceRequest;
import com.mfino.sterling.service.MfinoHttpConnector;

/**
 * 
 * @author Amar
 *
 */
public class SterlingBankWSClient {
	
	private static final Logger	log = LoggerFactory.getLogger(SterlingBankWSClient.class);

	private String url;
	private MfinoHttpConnector httpClient;
	
	public static String REQUEST_TYPE_INTER_BANK_TRANSFER = "101";
	public static String REQUEST_TYPE_INTRA_BANK_TRANSFER = "102";
	public static String REQUEST_TYPE_INTER_BANK_NAME_QUERY = "105";
	public static String REQUEST_TYPE_BALANCE_ENQUIRY = "201";
	public static String REQUEST_TYPE_MINI_STATEMENT = "211";
	
	
	private static final String interbankFundsTransferXmlRequest=		
			"<IBSRequest>" +
			"<SessionID>$SessionID</SessionID>" +
			"<ReferenceID>$ReferenceID</ReferenceID>" +
			"<RequestType>$RequestType</RequestType>" +
			"<FromAccount>$FromAccount</FromAccount>" +
			"<ToAccount>$ToAccount</ToAccount>" +
			"<Amount>$Amount</Amount>" +
			"<DestinationBankCode>$DestinationBankCode</DestinationBankCode>" +
			"<NEResponse>$NEResponse</NEResponse>" +
			"<BenefiName>$BenefiName</BenefiName>" + 
			"<PaymentReference>$PaymentReference</PaymentReference>" +
			"</IBSRequest>";
	
	private static final String intrabankFundsTransferXmlRequest=		
			"<IBSRequest>" +
			"<ReferenceID>$ReferenceID</ReferenceID>" +
			"<RequestType>$RequestType</RequestType>" +
			"<FromAccount>$FromAccount</FromAccount>" +
			"<ToAccount>$ToAccount</ToAccount>" +
			"<Amount>$Amount</Amount>" +
			"</IBSRequest>";
	
	private static final String interbankNameQueryXmlRequest=		
			"<IBSRequest>" +
			"<ReferenceID>$ReferenceID</ReferenceID>" +
			"<RequestType>$RequestType</RequestType>" +
			"<ToAccount>$ToAccount</ToAccount>" +
			"<DestinationBankCode>$DestinationBankCode</DestinationBankCode>" +
			"</IBSRequest>";
	
	private static final String balanceEnquiryXmlRequest=		
			"<IBSRequest>" +
			"<ReferenceID>$ReferenceID</ReferenceID>" +
			"<RequestType>$RequestType</RequestType>" +
			"<Account>$Account</Account>" +
			"</IBSRequest>";
	
	private static final String miniStatementXmlRequest=		
			"<IBSRequest>" +
			"<ReferenceID>$ReferenceID</ReferenceID>" +
			"<RequestType>$RequestType</RequestType>" +
			"<Account>$Account</Account>" +
			"<Records>$Records</Records>" +
			"</IBSRequest>";
	
	/**
	 * 
	 * @param sessionID
	 * @param referenceID
	 * @param requestType
	 * @param fromAccount
	 * @param toAccount
	 * @param amount
	 * @param destinationBankCode
	 * @param neResponse
	 * @param benefiName
	 * @param paymentReference
	 * @return
	 */
	public String createAndSendInterbankFundsTransferRequest(String sessionID, String referenceID, String fromAccount,
			String toAccount, String amount, String destinationBankCode, String neResponse, String benefiName, String paymentReference){
		String xmlRequest =  createRequest(interbankFundsTransferXmlRequest, referenceID, REQUEST_TYPE_INTER_BANK_TRANSFER, fromAccount, toAccount, null, destinationBankCode, benefiName, paymentReference, amount, neResponse, null, sessionID);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;	
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createAndSendInterbankFundsTransferRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_INTER_BANK_TRANSFER);
		String xmlRequest =  createRequest(interbankFundsTransferXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createInterbankFundsTransferRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_INTER_BANK_TRANSFER);
		String xmlRequest =  createRequest(interbankFundsTransferXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		return xmlRequest;
	}
	
	/**
	 * 
	 * @param referenceID
	 * @param requestType
	 * @param fromAccount
	 * @param toAccount
	 * @param amount
	 * @return
	 */
	public String createAndSendIntrabankFundsTransferRequest(String referenceID, String fromAccount, String toAccount, String amount){
		String xmlRequest =  createRequest(intrabankFundsTransferXmlRequest, referenceID, REQUEST_TYPE_INTRA_BANK_TRANSFER, fromAccount, toAccount, null, null, null, null, amount, null, null, null);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;	
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createAndSendIntrabankFundsTransferRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_INTRA_BANK_TRANSFER);
		String xmlRequest =  createRequest(intrabankFundsTransferXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createIntrabankFundsTransferRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_INTRA_BANK_TRANSFER);
		String xmlRequest =  createRequest(intrabankFundsTransferXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		return xmlRequest;
	}
	
	/**
	 * 
	 * @param referenceID
	 * @param requestType
	 * @param toAccount
	 * @param destinationBankCode
	 * @return
	 */
	public String createAndSendInterbankNameQueryRequest(String referenceID, String toAccount, String destinationBankCode){
		String xmlRequest =  createRequest(interbankNameQueryXmlRequest, referenceID, REQUEST_TYPE_INTER_BANK_NAME_QUERY, null, toAccount, null, destinationBankCode, null, null, null, null, null, null);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;	
	}
		
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createAndSendInterbankNameQueryRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_INTER_BANK_NAME_QUERY);
		String xmlRequest =  createRequest(interbankNameQueryXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createInterbankNameQueryRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_INTER_BANK_NAME_QUERY);
		String xmlRequest =  createRequest(interbankNameQueryXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		return xmlRequest;
	}

	/**
	 * 
	 * @param referenceID
	 * @param requestType
	 * @param account
	 * @return
	 */
	public String createAndSendBalanceEnquiryRequest(String referenceID, String account){
		String xmlRequest =  createRequest(balanceEnquiryXmlRequest, referenceID, REQUEST_TYPE_BALANCE_ENQUIRY, null, null, account, null, null, null, null, null, null, null);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;	
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createAndSendBalanceEnquiryRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_BALANCE_ENQUIRY);
		String xmlRequest =  createRequest(balanceEnquiryXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createBalanceEnquiryRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_BALANCE_ENQUIRY);
		String xmlRequest =  createRequest(balanceEnquiryXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		return xmlRequest;
	}
	
	/**
	 * 
	 * @param referenceID
	 * @param requestType
	 * @param account
	 * @param records
	 * @return
	 */
	public String createAndSendMiniStatementRequest(String referenceID, String account, String records){
		String xmlRequest =  createRequest(miniStatementXmlRequest, referenceID, REQUEST_TYPE_MINI_STATEMENT, null, null, account, null, null, null, null, null, records, null);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = null;		
		xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;	
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createAndSendMiniStatementRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_MINI_STATEMENT);
		String xmlRequest =  createRequest(miniStatementXmlRequest, sterlingBankWebServiceRequest);
		Log.info("Created xml is: "+xmlRequest);
		String xmlResponse = httpClient.sendHttpRequest(url, xmlRequest);
		return xmlResponse;
	}
	
	/**
	 * 
	 * @param sterlingBankWebServiceRequest
	 * @return
	 */
	public String createMiniStatementRequest(SterlingBankWebServiceRequest sterlingBankWebServiceRequest){
		sterlingBankWebServiceRequest.setRequestType(REQUEST_TYPE_MINI_STATEMENT);
		String xmlRequest =  createRequest(miniStatementXmlRequest, sterlingBankWebServiceRequest);
		return xmlRequest;
	}
	
	
	/**
	 * Constructs the request by replacing the values for parameters in the request
	 * 
	 * @param xmlRequest
	 * @param referenceID
	 * @param requestType
	 * @param fromAccount
	 * @param toAccount
	 * @param account
	 * @param destinationBankCode
	 * @param benefiName
	 * @param paymentReference
	 * @param amount
	 * @param neResponse
	 * @param records
	 * @param sessionID
	 * @return
	 */
	private String createRequest(String xmlRequest, String referenceID, String requestType,String fromAccount,String toAccount,
			String account, String destinationBankCode, String benefiName, String paymentReference,String amount, String neResponse,
			String records, String sessionID) {
		log.info("Creating the xml request");
		
		if(xmlRequest.contains("$ReferenceID")){
			xmlRequest = xmlRequest.replace("$ReferenceID", referenceID);
		}
		if(xmlRequest.contains("$RequestType")){
			xmlRequest = xmlRequest.replace("$RequestType", requestType);
		}
		if(xmlRequest.contains("$FromAccount")){
			xmlRequest = xmlRequest.replace("$FromAccount", fromAccount);
		}
		if(xmlRequest.contains("$ToAccount")){
			xmlRequest = xmlRequest.replace("$ToAccount", toAccount);
		}
		if(xmlRequest.contains("$Account")){
			xmlRequest = xmlRequest.replace("$Account", account);
		}
		if(xmlRequest.contains("$DestinationBankCode")){
			xmlRequest = xmlRequest.replace("$DestinationBankCode", destinationBankCode);
		}
		if(xmlRequest.contains("$BenefiName")){
			xmlRequest = xmlRequest.replace("$BenefiName", benefiName);
		}
		if(xmlRequest.contains("$PaymentReference")){
			xmlRequest = xmlRequest.replace("$PaymentReference", paymentReference);
		}
		if(xmlRequest.contains("$Amount")){
			xmlRequest = xmlRequest.replace("$Amount", amount);
		}
		if(xmlRequest.contains("$NEResponse")){
			xmlRequest = xmlRequest.replace("$NEResponse", neResponse);
		}
		if(xmlRequest.contains("$Records")){
			xmlRequest = xmlRequest.replace("$Records", records);
		}
		if(xmlRequest.contains("$SessionID")){
			xmlRequest = xmlRequest.replace("$SessionID", sessionID);
		}
		return xmlRequest;	
	}
	
	private String createRequest(String xmlRequest, SterlingBankWebServiceRequest sterlingBankWebServiceRequest) {
		log.info("Creating the xml request");
		
		if(xmlRequest.contains("$ReferenceID")){
			xmlRequest = xmlRequest.replace("$ReferenceID", sterlingBankWebServiceRequest.getReferenceID());
		}
		if(xmlRequest.contains("$RequestType")){
			xmlRequest = xmlRequest.replace("$RequestType", sterlingBankWebServiceRequest.getRequestType());
		}
		if(xmlRequest.contains("$FromAccount")){
			xmlRequest = xmlRequest.replace("$FromAccount", sterlingBankWebServiceRequest.getFromAccount());
		}
		if(xmlRequest.contains("$ToAccount")){
			xmlRequest = xmlRequest.replace("$ToAccount", sterlingBankWebServiceRequest.getToAccount());
		}
		if(xmlRequest.contains("$Account")){
			xmlRequest = xmlRequest.replace("$Account", sterlingBankWebServiceRequest.getAccount());
		}
		if(xmlRequest.contains("$DestinationBankCode")){
			xmlRequest = xmlRequest.replace("$DestinationBankCode", sterlingBankWebServiceRequest.getDestinationBankCode());
		}
		if(xmlRequest.contains("$BenefiName")){
			xmlRequest = xmlRequest.replace("$BenefiName", sterlingBankWebServiceRequest.getBenefiName());
		}
		if(xmlRequest.contains("$PaymentReference")){
			xmlRequest = xmlRequest.replace("$PaymentReference", sterlingBankWebServiceRequest.getPaymentReference());
		}
		if(xmlRequest.contains("$Amount")){
			xmlRequest = xmlRequest.replace("$Amount", sterlingBankWebServiceRequest.getAmount());
		}
		if(xmlRequest.contains("$NEResponse")){
			xmlRequest = xmlRequest.replace("$NEResponse", sterlingBankWebServiceRequest.getNeResponse());
		}
		if(xmlRequest.contains("$Records")){
			xmlRequest = xmlRequest.replace("$Records", sterlingBankWebServiceRequest.getRecords());
		}
		if(xmlRequest.contains("$SessionID")){
			xmlRequest = xmlRequest.replace("$SessionID", sterlingBankWebServiceRequest.getSessionID());
		}
		return xmlRequest;	
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public MfinoHttpConnector getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(MfinoHttpConnector httpClient) {
		this.httpClient = httpClient;
	}
	
}
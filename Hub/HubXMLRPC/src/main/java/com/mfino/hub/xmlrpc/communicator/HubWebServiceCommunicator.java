package com.mfino.hub.xmlrpc.communicator;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.fix.CFIXMsg;
import com.mfino.hub.xmlrpc.CXMLRPCMsg;
import com.mfino.hub.xmlrpc.service.HubHttpConnector;
import com.mfino.mce.backend.impl.BaseServiceImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.TransactionLogService;

/**
 * @author Vishal
 *
 */
public abstract class HubWebServiceCommunicator extends BaseServiceImpl{
	
	public static final Logger log = LoggerFactory.getLogger(HubWebServiceCommunicator.class);
	
	public static final String WEBSERVICE_OPERATION_NAME = "IBSBridge";
	
	private String webServiceEndpointBean;
	protected BillPaymentsService billPaymentsService;
	protected TransactionLogService transactionLogService;
	private HubHttpConnector httpClient;
	private String url;
	protected String reversalResponseQueue = "jms:suspenseAndChargesRRQueue?disableReplyTo=true";
	
	public String getWebServiceEndpointBean() {
		return webServiceEndpointBean;
	}

	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
		this.webServiceEndpointBean = webServiceEndpointBean;
	}
	
	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public HubHttpConnector getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HubHttpConnector httpClient) {
		this.httpClient = httpClient;
	}
	
	public String getReversalResponseQueue() {
	    return reversalResponseQueue;
    }

	public void setReversalResponseQueue(String reversalResponseQueue) {
		this.reversalResponseQueue = reversalResponseQueue;
	}
	
	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}
	
	protected Map<String, String>	constantFieldsMap;

	public void setConstantFieldsMap(Map<String, String> map) {
		this.constantFieldsMap = map;
	}

	public abstract CXMLRPCMsg createHubWebServiceRequest(MCEMessage mceMessage);
	
	public abstract CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage);
	
	public abstract String getMethodName(MCEMessage mceMessage);
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage process(Exchange exchange)
	{
		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		log.info("HubWebServiceCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		Object responseFromWS = new Object();
		CXMLRPCMsg hubWebServiceRequest = null;
		String method = "Biller.TopUp";
		
		try
		{
			hubWebServiceRequest = createHubWebServiceRequest(mceMessage);
			log.info("HubWebServiceCommunicator :: hubWebServiceRequest="+hubWebServiceRequest);
			method = getMethodName(mceMessage);
	
			if(hubWebServiceRequest != null)
			{
				responseFromWS = httpClient.sendHttpRequest(url, hubWebServiceRequest, method);
//				ProducerTemplate template = exchange.getContext().createProducerTemplate();
//				template.start();
//				Map<String,Object> headersMap = new HashMap<String,Object>();
//				headersMap.put("operationName",WEBSERVICE_OPERATION_NAME);
//				MCEUtil.setMandatoryHeaders(exchange.getIn().getHeaders(), headersMap);
//				responseFromWS = (Object)template.requestBodyAndHeaders("cxf:bean:"+webServiceEndpointBean,hubWebServiceRequest,headersMap);								
//				template.stop();
			}			
		}
		catch(Exception e){
			log.error("HubWebServiceCommunicator :: Exception e=",e);
			responseFromWS = handleHttpCommunicationException(e);
		}
		CFIXMsg requestFixMessage = mceMessage.getRequest();
		CFIXMsg responseFixMsg = null;
		if(responseFromWS != null)
			responseFixMsg=constructReplyMessage(responseFromWS, requestFixMessage);			
		replyMessage.setRequest(requestFixMessage);
		replyMessage.setResponse(responseFixMsg);
		replyMessage.setDestinationQueues(mceMessage.getDestinationQueues());
		
		//Handling reversal response; Based on this status will be changed
		if(method.equalsIgnoreCase("Biller.Reverse")){
			replyMessage.setDestinationQueue(getReversalResponseQueue());
		}
		
		log.info("HubWebServiceCommunicator :: process() END");
		return replyMessage;
	}
	
	public Object handleHttpCommunicationException(Exception e){
		HashMap<String,Object> wsResponseElement = new HashMap<String,Object> ();
		if( e instanceof SocketTimeoutException)
			wsResponseElement.put("status", MCEUtil.SERVICE_TIME_OUT) ;
		else
			wsResponseElement.put("status", MCEUtil.SERVICE_UNAVAILABLE) ;
		return wsResponseElement;
	}


	
	/**
	 * returns a fixed length string containing 7 digits. If the length of the string is less that 7 digits, '0's are prefixed.
	 * If its length exceeds 7 digits, last 7 digits are returned.
	 * 
	 * @param sctlID
	 * @return
	 */
	public String normalize(Long sctlID)
	{
		Long id = sctlID;
		id = id % 10000000 ;
		String referenceNo = id.toString();
		StringBuilder prefix =  new StringBuilder();
		for(int i = 0; i < 7 - referenceNo.length() ; i++)
		{
			prefix = prefix.append("0");
		}
		referenceNo = prefix.toString() + referenceNo;
		return referenceNo;
	}

	

}

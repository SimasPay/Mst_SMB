package com.mfino.bayar.communicator;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.bayar.service.BayarHttpConnector;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.fix.CFIXMsg;
import com.mfino.mce.backend.impl.BaseServiceImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.TransactionLogService;

/**
 * @author Vishal
 *
 */
public abstract class BayarHttpCommunicator extends BaseServiceImpl{
	
	public static final Logger log = LoggerFactory.getLogger(BayarHttpCommunicator.class);
	
	public static final String WEBSERVICE_OPERATION_NAME = "IBSBridge";
	public static Integer SERVICE_TIME_OUT = 8;
	public static Integer SERVICE_TIME_OUT_FROM_BAYAR = 8888;
	
	private String webServiceEndpointBean;
	protected BillPaymentsService billPaymentsService;
	protected TransactionLogService transactionLogService;
	private BayarHttpConnector httpClient;
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
	
	public BayarHttpConnector getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(BayarHttpConnector httpClient) {
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

	public abstract Object createBayarHttpRequest(MCEMessage mceMessage);
	
	public abstract CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage);
	
	public abstract MCEMessage constructResponseMessage(MCEMessage mceMceMessage);
	
	public abstract String getMethodName(MCEMessage mceMessage);
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage process(Exchange exchange)
	{
		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		log.info("BayarHttpCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		Object responseFromWS = new Object();
		Object httpRequestParams = null;
		String method = constantFieldsMap.get("billpay");
		
		try
		{
			httpRequestParams = createBayarHttpRequest(mceMessage);
			log.info("BayarHttpCommunicator :: httpRequestParams="+httpRequestParams);
			method = getMethodName(mceMessage);
	
			if(httpRequestParams != null)
			{
				responseFromWS = httpClient.sendHttpsRequest(method,(Object)httpRequestParams);

			}			
		}
		catch(Exception e){
			log.error("BayarHttpCommunicator :: Exception e=",e);
			responseFromWS = handleHttpCommunicationException(e);
		}
		CFIXMsg requestFixMessage = mceMessage.getRequest();
		CFIXMsg responseFixMsg = null;
		if(responseFromWS != null)
			responseFixMsg=constructReplyMessage(responseFromWS, requestFixMessage);			
		replyMessage.setRequest(requestFixMessage);
		replyMessage.setResponse(responseFixMsg);
		replyMessage.setDestinationQueues(mceMessage.getDestinationQueues());
		
		log.info("BayarHttpCommunicator :: process() END");
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

}

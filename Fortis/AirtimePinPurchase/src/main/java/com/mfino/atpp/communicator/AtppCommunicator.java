package com.mfino.atpp.communicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.SubscriberService;

/**
 * @author Satya
 *
 */
public abstract class AtppCommunicator extends BillPaymentsBaseServiceImpl{
	
	protected BillPaymentsService billPaymentsService;
		
	protected SubscriberService subscriberService;
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	private AtppHttpClient xmlHttpCommunicator;

	private Map<String, String> params;
	
	public abstract List<Object> getParameterList(MCEMessage mceMessage);
	
	public abstract MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage); 
	
	public abstract String getMessageName(MCEMessage mceMessage);

	public MCEMessage process(MCEMessage mceMessage){
		log.info("AtppCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		
		String messageName = getMessageName(mceMessage);
		List<Object> response = new ArrayList<Object>();
		
		String returnXml = "";
		
		try
		{
			List<Object> parameters = getParameterList(mceMessage);
			log.info("AtppCommunicator messageName="+messageName+", parameters="+parameters);
			
			if("MM_AirtimePinPurchase".equals(messageName)){
				returnXml = xmlHttpCommunicator.createAndSendAtppRequest((String)parameters.get(0), (BigDecimal)parameters.get(1), (String)parameters.get(2), (String)parameters.get(3), (String)parameters.get(4), (String)parameters.get(5), "" + (Long)parameters.get(6));
			}
						
			log.info("AtppCommunicator :: messageName="+messageName+", returnXml="+returnXml);
			
			response.add(returnXml);
		}
		catch(Exception e){
			log.error("AtppCommunicator :: Exception e=",e);
			response = handleHttpCommunicationException(e);
		}
			
		log.info("AtppCommunicator :: returnXml="+returnXml);
			
		replyMessage = constructReplyMessage(response, mceMessage);
		
		log.info("AtppCommunicator :: process() END");
		return replyMessage;
	}
	
	public List<Object> handleHttpCommunicationException(Exception e){
		List<Object> responseFromWS = new ArrayList<Object>();

		responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		return responseFromWS;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public AtppHttpClient getXmlHttpCommunicator() {
		return xmlHttpCommunicator;
	}

	public void setXmlHttpCommunicator(AtppHttpClient xmlHttpCommunicator) {
		this.xmlHttpCommunicator = xmlHttpCommunicator;
	}
	
}

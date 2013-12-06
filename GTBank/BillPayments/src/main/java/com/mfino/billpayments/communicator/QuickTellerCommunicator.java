package com.mfino.billpayments.communicator;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.interswitchng.services.quicktellerservice.QuickTellerService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;

/**
 * 
 * @author Sasi
 *
 */
public abstract class QuickTellerCommunicator extends BillPaymentsBaseServiceImpl{
	
	private QuickTellerService quickTellerService;
	
	public QuickTellerService getQuickTellerService() {
		return quickTellerService;
	}

	public void setQuickTellerService(QuickTellerService quickTellerService) {
		this.quickTellerService = quickTellerService;
	}

	public abstract List<Object> getParameterList(MCEMessage mceMessage);
	
	public abstract MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage); 
	
	public abstract String getMessageName(MCEMessage mceMessage);
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage process(MCEMessage mceMessage){
		log.info("QuickTellerCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		
		String messageName = getMessageName(mceMessage);
		
		List<Object> response = new ArrayList<Object>();
		String xmlParams = (String)getParameterList(mceMessage).get(0);
		String returnXml = "";
		
		try
		{
			if("DoBillPaymentInquiry".equals(messageName)){
				returnXml = quickTellerService.doBillPaymentInquiry(xmlParams);
			}
			else if("SendBillPaymentAdvice".equals(messageName)){
				returnXml = quickTellerService.sendBillPaymentAdvice(xmlParams);
			}
			else if("QueryTransaction".equals(messageName)){
				returnXml = quickTellerService.queryTransaction(xmlParams);
			}
			
			log.info("QuickTellerCommunicator :: messageName="+messageName+", returnXml="+returnXml);
			
			response.add(returnXml);
		}
		catch(Exception e){
			log.error("QuickTellerCommunicator :: Exception e=",e);
			response = handleWSCommunicationException(e);
		}
			
		log.info("QuickTellerCommunicator :: returnXml="+returnXml);
			
		replyMessage = constructReplyMessage(response, mceMessage);
		
		log.info("QuickTellerCommunicator :: process() END");
		return replyMessage;
	}
	
	public List<Object> handleWSCommunicationException(Exception e){
		List<Object> responseFromWS = new ArrayList<Object>();
		
  		if(e.getCause() instanceof SocketTimeoutException){
			responseFromWS.add(MCEUtil.SERVICE_TIME_OUT);
		}
		else{
			responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		}
		
		return responseFromWS;
	}
}

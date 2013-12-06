package com.mfino.fortis.frsc.communicator;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tempuri.IService;

import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;

/**
 * @author Bala Sunku
 *
 */
public abstract class FRSCCommunicator extends BillPaymentsBaseServiceImpl{
	
	protected BillPaymentsService billPaymentsService;
	
	private IService frscService;

	private Map<String, String> params;
	
	public IService getFrscService() {
		return frscService;
	}

	public void setFrscService(IService frscService) {
		this.frscService = frscService;
	}

	public abstract List<Object> getParameterList(MCEMessage mceMessage);
	
	public abstract MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage); 
	
	public abstract String getMessageName(MCEMessage mceMessage);

	public MCEMessage process(MCEMessage mceMessage){
		log.info("FRSCCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		
		String messageName = getMessageName(mceMessage);
		
		List<Object> response = new ArrayList<Object>();
		
		Integer returnResponse = null;
		
		try
		{
			List<Object> parameters = getParameterList(mceMessage);
			log.info("FRSCCommunicator messageName="+messageName+", parameters="+parameters);
			
			if("AcceptPayment".equals(messageName)){
				returnResponse = frscService.acceptPayment((String)parameters.get(0), (String)parameters.get(1), (String)parameters.get(2), (String)parameters.get(3),
						(String)parameters.get(4), (String)parameters.get(5), (String)parameters.get(6));
			}
			
			log.info("FRSCCommunicator :: messageName="+messageName+", returnResponse="+returnResponse);
			
			response.add(returnResponse);
		}
		catch(Exception e){
			log.error("FRSCCommunicator :: Exception e=",e);
			response = handleWSCommunicationException(e);
		}
			
		log.info("FRSCCommunicator :: returnResponse="+returnResponse);
			
		replyMessage = constructReplyMessage(response, mceMessage);
		
		log.info("FRSCCommunicator :: process() END");
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
}

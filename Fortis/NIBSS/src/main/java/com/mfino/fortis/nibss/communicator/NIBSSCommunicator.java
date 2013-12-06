package com.mfino.fortis.nibss.communicator;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;

/**
 * @author Sasi
 *
 */
public abstract class NIBSSCommunicator extends BillPaymentsBaseServiceImpl{
	
	protected BillPaymentsService billPaymentsService;
		
	private NibssHttpClient xmlHttpCommunicator;

	private Map<String, String> params;
	
	public abstract List<Object> getParameterList(MCEMessage mceMessage);
	
	public abstract MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage); 
	
	public abstract String getMessageName(MCEMessage mceMessage);

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage process(MCEMessage mceMessage){
		log.info("NIBSSCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		
		String messageName = getMessageName(mceMessage);
		List<Object> response = new ArrayList<Object>();
		
		String returnXml = "";
		
		try
		{
			List<Object> parameters = getParameterList(mceMessage);
			log.info("NIBSSCommunicator messageName="+messageName+", parameters="+parameters);
			
			if("MM_FundTransferSingleItem".equals(messageName)){
				returnXml = xmlHttpCommunicator.createAndSendTransferRequest(Long.valueOf((String)parameters.get(0)), (String)parameters.get(1), (String)parameters.get(2), (String)parameters.get(3), (BigDecimal)parameters.get(4), (String)parameters.get(5), "" + (Long)parameters.get(6), (String)parameters.get(7), (String)parameters.get(8));//nibssService.mmFundTransferSingleItem(Long.valueOf((String)parameters.get(0)), (String)parameters.get(1), (String)parameters.get(2), (String)parameters.get(3), (BigDecimal)parameters.get(4), (String)parameters.get(5), "" + (Long)parameters.get(6), (String)parameters.get(7), (String)parameters.get(8));
			}
			else if("MM_BenNameEnquirySingleItem".equals(messageName)){
				returnXml = xmlHttpCommunicator.createAndSendCustomerNameInquiryRequest((String)parameters.get(0),(String)parameters.get(1));
			}
			else if("MM_TransactionQuery".equals(messageName)){
				returnXml = xmlHttpCommunicator.createAndSendTransactionQueryRequest((String)parameters.get(0), (String)parameters.get(1));
			}
			/*else if("MM_TransactionQuerySingleItem".equals(messageName)){
				returnXml = xmlHttpCommunicator.createAndSendTransactionQuerySingleItemRequest((String)parameters.get(0), (String)parameters.get(1));
			}*/
			
			log.info("NIBSSCommunicator :: messageName="+messageName+", returnXml="+returnXml);
			
			response.add(returnXml);
		}
		catch(Exception e){
			log.error("NIBSSCommunicator :: Exception e=",e);
			response = handleHttpCommunicationException(e);
		}
			
		log.info("NIBSSCommunicator :: returnXml="+returnXml);
			
		replyMessage = constructReplyMessage(response, mceMessage);
		
		log.info("NIBSSCommunicator :: process() END");
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

	public NibssHttpClient getXmlHttpCommunicator() {
		return xmlHttpCommunicator;
	}

	public void setXmlHttpCommunicator(NibssHttpClient xmlHttpCommunicator) {
		this.xmlHttpCommunicator = xmlHttpCommunicator;
	}
	
}

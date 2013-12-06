package com.mfino.atpp.communicator;

import static com.mfino.atpp.util.AtppConstants.*;

import java.util.ArrayList;
import java.util.List;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.atpp.util.AtppResponse;
import com.mfino.atpp.util.AtppResponseParser;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.util.MfinoUtil;
/**
 * @author Satya
 *
 */
public class AtppCommunicatorImpl extends AtppCommunicator {

	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("AtppCommunicatorImpl :: getParameterList mceMessage="+mceMessage);
		List<Object> parameterList = new ArrayList<Object>();
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN subscriberMdn = subscriberMdnDao.getByMDN(sctl.getSourceMDN());

		Subscriber subscriber = subscriberMdn.getSubscriber();
		
		String sendersName = "";
		if(null != subscriber){
			sendersName = subscriber.getFirstName() + " " + subscriber.getLastName();
		}
		
		parameterList.add(billPayments.getSourceMDN());
		parameterList.add(billPayments.getAmount());
		parameterList.add(billPayments.getInfo1());//ben op code or dest code
		parameterList.add("0"+subscriberService.deNormalizeMDN(billPayments.getInvoiceNumber()));//dest no or acc iden number
		parameterList.add("");//email is currently empty string
		parameterList.add(billPayments.getInfo2());//terminal id is set in info2
		parameterList.add(billPayments.getSctlId());
		
		return parameterList;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "MM_AirtimePinPurchase";
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
		Object wsResponseElement = response.get(0);
		log.info("AtppCommunicatorImpl :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestMceMessage="+requestMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)requestMceMessage.getRequest();
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		
		
		
		if(wsResponseElement.toString().contains("<ResponseCode>0000</ResponseCode>")){
			AtppResponse atppResponse = AtppResponseParser.getAtppResponse((String)wsResponseElement);
			
			billPayResponse.setInResponseCode(atppResponse.getResponseCode());
			
			if(ATPP_RESPONSE_SUCCESSFUL.equals(billPayResponse.getInResponseCode())){
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);				
				billPayResponse.setInTxnId(atppResponse.getRequestReference().replaceFirst("^0+(?!$)", ""));
			}
			else{
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
		}
		else{
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			billPayResponse.setResponse(-1);
			billPayResponse.setResult(1);	
		}	
		
		responseMceMessage.setRequest(requestMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		return responseMceMessage;
	}
}

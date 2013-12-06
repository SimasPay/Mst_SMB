package com.mfino.interemoney.nibss.communicator;

import static com.mfino.interemoney.nibss.util.NIBSSCashOutConstants.*;

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
import com.mfino.interemoney.nibss.util.NIBSSCashOutResponse;
import com.mfino.interemoney.nibss.util.NIBSSCashOutResponseParser;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
/**
 * @author Sasi
 *
 */
public class NIBSSFundsTransferCommunicator extends NIBSSCommunicator {

	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("NIBSSFundsTransferCommunicator :: getParameterList mceMessage="+mceMessage);
		
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
		
		parameterList.add(getParams().get(KEY_MFINO_MOBILE_MONEY_ACCOUNT));
		parameterList.add(billPayments.getSourceMDN());
		parameterList.add(billPayments.getInfo1());//ben op code or dest code
		parameterList.add(billPayments.getInvoiceNumber());//dest no or acc iden number
		parameterList.add(billPayments.getAmount());
		parameterList.add(billPayments.getInfo2());//narration
		parameterList.add(billPayments.getSctlId());
		parameterList.add(sendersName);//originator name
		parameterList.add(billPayments.getInfo3());//dest name or acc iden name
		
		
		return parameterList;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "MM_FundTransferSingleItem";
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
		Object wsResponseElement = response.get(0);
		
		log.info("NIBSSFundsTransferCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestMceMessage="+requestMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)requestMceMessage.getRequest();
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			billPayResponse.setResponse(-1);
			billPayResponse.setResult(-1);			
		} 
		else
		{
			NIBSSCashOutResponse nibssCashOutResponse = NIBSSCashOutResponseParser.getNIBSSCashOutResponse((String)wsResponseElement);
			
			billPayResponse.setInResponseCode(nibssCashOutResponse.getResponseCode());
			
			if(NIBSS_RESPONSE_SUCCESSFUL.equals(billPayResponse.getInResponseCode())){
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
				
				billPayResponse.setInTxnId(nibssCashOutResponse.getTransactionNumber());
			}
			else{
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
		}
		
		responseMceMessage.setRequest(requestMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		
		return responseMceMessage;
	}
}

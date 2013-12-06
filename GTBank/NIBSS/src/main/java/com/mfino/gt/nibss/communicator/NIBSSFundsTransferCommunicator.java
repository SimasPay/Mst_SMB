package com.mfino.gt.nibss.communicator;

import static com.mfino.gt.nibss.util.NIBSSCashOutConstants.NIBSS_RESPONSE_SUCCESSFUL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
import com.mfino.gt.nibss.util.NIBSSCashOutResponse;
import com.mfino.gt.nibss.util.NIBSSCashOutResponseParser;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import static com.mfino.gt.nibss.util.NIBSSCashOutConstants.*;
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
		parameterList.add(billPayments.getInfo1());
		parameterList.add(billPayments.getInvoiceNumber());
		parameterList.add(billPayments.getAmount());
		parameterList.add(billPayments.getInfo2());
		parameterList.add(billPayments.getSctlId());
		parameterList.add(sendersName);
		parameterList.add(billPayments.getInfo3());
		
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
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(billPayResponse.getServiceChargeTransactionLogID());
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_TIME_OUT)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_TIME_OUT);
		}
		else if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			billPayResponse.setResponse(-1);
			billPayResponse.setResult(-1);			
		} 
		else
		{
			NIBSSCashOutResponse nibssCashOutResponse = NIBSSCashOutResponseParser.getNIBSSCashOutResponse((String)wsResponseElement);
			String inResponse = nibssCashOutResponse.getResponseCode() +  
					(StringUtils.isNotBlank(nibssCashOutResponse.getError()) ? ":" + nibssCashOutResponse.getError() : StringUtils.EMPTY);
			if (StringUtils.isNotBlank(inResponse) && inResponse.length() > 255) {
				inResponse = inResponse.substring(0,255);
			}			
			
			billPayResponse.setInResponseCode(inResponse);
			
			if(NIBSS_RESPONSE_SUCCESSFUL.equals(nibssCashOutResponse.getResponseCode())){
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setBeneficiaryName(billPayments.getInfo3());
				billPayResponse.setInTxnId(nibssCashOutResponse.getSessionId());
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

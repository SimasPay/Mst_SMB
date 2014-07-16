package com.mfino.billpayments.bsm.uangku;


import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBillInquiry;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountFromBiller;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;

/**
 * 
 * @author Vishal
 *
 */
public class BillInquiryProcessor extends BillPaymentsBaseServiceImpl implements BSMProcessor {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private SubscriberService subscriberService;
	private SubscriberMdnService subscriberMdnService;

	@Override
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("BillInquiryProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);
		
		
		MCEMessage replyMessage = new MCEMessage(); 
		CMBillInquiry requestFix = (CMBillInquiry)mceMessage.getRequest();
		
		CMBSIMGetAmountToBiller toOperator = new CMBSIMGetAmountToBiller();
		toOperator.setSourceMDN(requestFix.getSourceMDN());

		toOperator.setSourceCardPAN(requestFix.getSourceCardPAN());
		toOperator.setAmount(requestFix.getAmount());
		toOperator.setInvoiceNo(subscriberService.normalizeMDN(requestFix.getInvoiceNumber()));
		toOperator.setTransactionID(requestFix.getTransactionID());
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		toOperator.setParentTransactionID(requestFix.getTransactionID());
		toOperator.setBillerCode(requestFix.getBillerCode());
		toOperator.setBankCode(CmFinoFIX.OperatorCodeForRouting_BSM);//routing code for BSM
		
		
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:bsmBillInquiryResponseQueue?disableReplyTo=true");
		log.info("BillInquiryProcessor :: constructRequestMessage() END");
		
		
		return replyMessage;
	}
	
	@Override
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		
		log.info("BillInquiryProcessor :: constructReplyMessage() BEGIN mceMessage="+mceMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMTransferInquiryToBank requestFix = (CMTransferInquiryToBank)mceMceMessage.getRequest();
		CMBSIMGetAmountFromBiller response  = (CMBSIMGetAmountFromBiller) mceMceMessage.getResponse();
		log.info("bsmBillPayInquiry Response for BillInquiry = "+response.getResponseCode());
		
		BackendResponse billResponse = new BillPayResponse();
		billResponse.setParentTransactionID(response.getParentTransactionID());
		billResponse.setTransactionID(response.getTransactionID());
		billResponse.setPaymentInquiryDetails(response.getPaymentInquiryDetails());
		billResponse.setSourceMDN(response.getSourceMDN());
		
		if(response.getAmount()!=null && response.getAmount().compareTo(BigDecimal.ZERO)>0) //If BSM returns a positive amount use it; otherwise use the one sent in request
			billResponse.setAmount(response.getAmount());
		else
			billResponse.setAmount(requestFix.getAmount());
		
		Integer responseCode = new Integer(response.getResponseCode());
		billResponse.setResult(responseCode);
		billResponse.setInternalErrorCode(getInternalErrorCode(responseCode));
		billResponse.setOperatorMessage(getOperatorDescription(responseCode, response.getSourceMDN()));
		billResponse.setBillPaymentReferenceID(response.getInfo3());
		responseMceMessage.setRequest(mceMceMessage.getRequest());
		responseMceMessage.setResponse(billResponse);
		
		log.info("BillInquiryProcessor :: constructReplyMessage() END");
		return responseMceMessage;
	}

	private Integer getInternalErrorCode(Integer responseCode) {
		if(responseCode.equals(CmFinoFIX.ResponseCode_Success)){
			return NotificationCodes.BillDetails.getInternalErrorCode();
		}
		return NotificationCodes.GetBillDetailsFailed.getInternalErrorCode();
	}
	
	private String getOperatorDescription(Integer responseCode, String mdn) {
		Subscriber subscriber = subscriberMdnService.getSubscriberFromMDN(mdn);
		String result = null;
		if (subscriber != null) {
			result = JSONUtil.getOperatorDescription(responseCode.toString(), subscriber.getLanguage());
		} else {
			result = JSONUtil.getOperatorDescription(responseCode.toString(), 0);
		}
		return result;
	}

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}

	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}
}

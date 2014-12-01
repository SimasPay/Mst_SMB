package com.mfino.billpayments.bsim;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountFromBiller;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.fix.CmFinoFIX.CMBillInquiry;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.mce.core.util.ExternalResponseCodeHolder;
import com.mfino.mce.core.util.ResponseCodes;
import com.mfino.service.SubscriberService;

/**
 * 
 * @author Maruthi
 *
 */
public class BillInquiryProcessor extends BillPaymentsBaseServiceImpl implements BSIMProcessorNew {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private SubscriberService subscriberService;
	
	@Override
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("BillInquiryProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);
		
		MCEMessage replyMessage = new MCEMessage(); 
		CMBillInquiry requestFix = (CMBillInquiry)mceMessage.getRequest();
		
		CMBSIMGetAmountToBiller toOperator = new CMBSIMGetAmountToBiller();
		toOperator.setInfo2(requestFix.getSourceBankAccountNo());
		toOperator.setSourceMDN(requestFix.getSourceMDN());
		toOperator.setDestMDN(subscriberService.normalizeMDN(requestFix.getInvoiceNumber()));
		toOperator.setTransactionID(requestFix.getTransactionID());
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setSourceCardPAN(requestFix.getSourceBankAccountNo());
		toOperator.setBillerCode(requestFix.getBillerCode());
		toOperator.setInvoiceNo(requestFix.getInvoiceNumber());
		toOperator.setSourceBankAccountType(requestFix.getSourceBankAccountType());
		
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:bsimBillInquiryResponseQueue");
		log.info("BillInquiryProcessor :: constructRequestMessage() END");
		
		return replyMessage;
	}
	
	@Override
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		
		log.info("BillInquiryProcessor :: constructReplyMessage() BEGIN mceMessage="+mceMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
//		CMBSIMGetAmountToBiller requestFix = (CMBSIMGetAmountToBiller)mceMceMessage.getRequest();
		CMBSIMGetAmountFromBiller response  = (CMBSIMGetAmountFromBiller) mceMceMessage.getResponse();
		log.info("bsimBillPayInquiry Response for BillInquiry = "+response.getResponseCode());
		
		BackendResponse billResponse = new BillPayResponse();
		billResponse.setParentTransactionID(response.getParentTransactionID());
		billResponse.setTransactionID(response.getTransactionID());
		billResponse.setPaymentInquiryDetails(response.getAmount().toString());
		billResponse.setAdditionalInfo(response.getInfo3());
		
		if(response.getAmount() != null)
			billResponse.setAmount(response.getAmount());
		
		if(response.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success)) {
			billResponse.setResult(new Integer(response.getResponseCode()));
			billResponse.setInternalErrorCode(getInternalErrorCode(new Integer(response.getResponseCode())));

		}else{
			log.info("BillPaymentServiceImpl - Unable to get bill amount");

			ResponseCodes rs = ResponseCodes.getResponseCodes(1, response.getResponseCode());
			billResponse.setDescription(ExternalResponseCodeHolder.getNotificationText(response.getResponseCode()));
			billResponse.setExternalResponseCode(rs.getExternalResponseCode());
			billResponse.setInternalErrorCode(rs.getInternalErrorCode());

		//Pending as of now; Do it later
		// Handle Notifications for PLN Billers separately, setting only for failure case
		//if(billPayInquiryToBank.getBillerCode() != null && plnBillers.contains(billPayInquiryToBank.getBillerCode())){
		//		if(requestFix.getBillerCode() != null && isPlnBiller(requestFix.getBillerCode())){
		//			billResponse.setInternalErrorCode(getPLNErrorCode(CmFinoFIX.ResponseCode_Failure, response.getResponseCode()));
		//			billResponse.setExternalResponseCode(null);
		//		}
		}
		
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
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
	
}

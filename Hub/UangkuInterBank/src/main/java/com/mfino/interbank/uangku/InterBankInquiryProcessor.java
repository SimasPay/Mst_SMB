package com.mfino.interbank.uangku;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.SubscriberService;
import com.mfino.service.impl.SubscriberServiceImpl;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author HemanthKumar
 *
 */
public class InterBankInquiryProcessor implements IInterBankProcessor {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("InterBankProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage(); 
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		CMBillPayInquiry request = (CMBillPayInquiry)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID(); 
		//CMBillPay requestFix = (CMBillPay)mceMessage.getRequest();
		BackendResponse backendResponse = (BackendResponse) mceMessage.getResponse();
		CMInterBankTransferInquiryToBank toBank = new CMInterBankTransferInquiryToBank();
		String mdngen = MfinoUtil.CheckDigitCalculation(backendResponse.getSourceMDN()); 
		toBank.setMPan(mdngen);
		toBank.setAmount(backendResponse.getAmount());
		toBank.setBankCode(CmFinoFIX.OperatorCodeForRouting_BSM);
		toBank.setSourceCardPAN(request.getSourceBankAccountNo());
		toBank.setDestCardPAN(request.getDestinationBankAccountNo());
		toBank.setTransactionID(backendResponse.getTransactionID());
		toBank.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		//dest Bank Code is in BenOpCode of BillPayInquiry OBject
		toBank.setDestBankCode(request.getBenOpCode());
		//set missing params
		toBank.setTransferID(backendResponse.getTransferID());
		toBank.setParentTransactionID(backendResponse.getParentTransactionID());
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toBank);
		replyMessage.setDestinationQueue("jms:ibtInquiryCommunicatorResponseQueue?disableReplyTo=true");
		log.info("InterBankProcessor :: constructRequestMessage() END");
		return replyMessage;
	}
	
	@Override
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		log.info("InterBankProcessor :: constructReplyMessage() BEGIN mceMessage="+mceMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		CMInterBankTransferInquiryToBank request = (CMInterBankTransferInquiryToBank) mceMceMessage.getRequest();
		CMBase requestFix = (CMBase)mceMceMessage.getRequest();
		CMInterBankTransferInquiryFromBank response  = (CMInterBankTransferInquiryFromBank) mceMceMessage.getResponse();
		log.info("InterBankProcessor:: Response for BillPayment from IBT = "+response.getResponseCode());
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode(response.getResponseCode().toString());
		billPayResponse.setParentTransactionID(response.getParentTransactionID());
		billPayResponse.setTransferID(request.getTransferID());
		//Destination name is stored in Info3
		billPayResponse.setInfo3(response.getDestinationUserName());
		
		if("00".equals(response.getResponseCode())){
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
		}else{
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}	
		responseMceMessage.setRequest(mceMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		log.info("InterBankProcessor :: constructReplyMessage() END");
		
		return responseMceMessage;
	}

}

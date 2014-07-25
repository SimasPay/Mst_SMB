package com.mfino.interbank.uangku;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.SubscriberService;
import com.mfino.service.impl.SubscriberServiceImpl;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author HemanthKumar
 *
 */
public class InterBankProcessor implements IInterBankProcessor {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("InterBankProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage(); 
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		CMBillPay request = (CMBillPay)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		//CMBillPay requestFix = (CMBillPay)mceMessage.getRequest();
		BackendResponse backendResponse = (BackendResponse) mceMessage.getResponse();
		CMInterBankMoneyTransferToBank toBank = new CMInterBankMoneyTransferToBank();
		Timestamp ts = DateTimeUtil.getGMTTime();
		String mdngen = MfinoUtil.CheckDigitCalculation(backendResponse.getSourceMDN()); 
		toBank.setTransferTime(ts);
		toBank.setMPan(mdngen);
		toBank.setLanguage(backendResponse.getLanguage());
		toBank.setAmount(backendResponse.getAmount());
		toBank.setBankCode(CmFinoFIX.OperatorCodeForRouting_BSM);
		toBank.setSourceCardPAN(backendResponse.getSourceCardPAN());
		toBank.setDestCardPAN(request.getDestinationBankAccountNo());
		toBank.setTransactionID(backendResponse.getTransactionID());
		toBank.setPin(requestFix.getPassword());
		toBank.setDestBankCode(request.getBenOpCode());
		toBank.setServiceChargeTransactionLogID(request.getServiceChargeTransactionLogID());
		//set remaining params
		toBank.setTransferID(backendResponse.getTransferID());
		toBank.setParentTransactionID(backendResponse.getParentTransactionID());
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toBank);
		replyMessage.setDestinationQueue("jms:ibtResponseQueue?disableReplyTo=true");
		log.info("InterBankProcessor :: constructRequestMessage() END");
		return replyMessage;
	}
	
	@Override
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		log.info("InterBankProcessor :: constructReplyMessage() BEGIN mceMessage="+mceMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)mceMceMessage.getRequest();
		CMInterBankMoneyTransferFromBank response  = (CMInterBankMoneyTransferFromBank) mceMceMessage.getResponse();
		log.info("InterBankProcessor:: Response for BillPayment from clickatell = "+response.getResponseCode());
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode(response.getResponseCode().toString());
		billPayResponse.setParentTransactionID(response.getParentTransactionID());
		billPayResponse.setTransferID(response.getTransferID());
		//intxn id is stored in BillPayRefID
		billPayResponse.setInTxnId(response.getBillPaymentReferenceID());
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

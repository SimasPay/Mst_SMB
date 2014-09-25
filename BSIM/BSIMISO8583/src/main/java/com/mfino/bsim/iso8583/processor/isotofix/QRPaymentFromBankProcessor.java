package com.mfino.bsim.iso8583.processor.isotofix;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.iso.jpos.NoISOResponseException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class QRPaymentFromBankProcessor implements BSIMISOtoFixProcessor{

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMQRPaymentToBank toBank = (CMQRPaymentToBank)request;
		CMQRPaymentFromBank fromBank = new CMQRPaymentFromBank();

		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
        
		fromBank.copy(toBank);
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		if(isoMsg.hasField(48))
			fromBank.setBankAccountName(isoMsg.getString(48));
		if(isoMsg.hasField(62))
		{
			fromBank.setInfo1(isoMsg.getString(62));
	}
		
		//if(fromBank.getResponseCode().equals("00")){
		//String responseCodeFromFlashiz = sendAcknowledgementToFlashiz(isoMsg,fromBank,toBank);
		
		//update txn status from aknowledgement response
			//if(!(responseCodeFromFlashiz.equals("00"))){
			//Negative response from flashiz so have to send reversal
				//fromBank.setResponseCode(responseCodeFromFlashiz);
			//}	
		//}
//			MCEMessage reversalMCEMessage = new MCEMessage();
//			reversalMCEMessage.setRequest(toBank);
//			reversalMCEMessage.setResponse(toBank);
//			SyncProducer syncProducer = new SyncProducer();
//			syncProducer.produceMessage("bsimISOQueue", reversalMCEMessage,fromBank.getTransactionID().toString());
			//}
		//}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}
//
//	private String sendAcknowledgementToFlashiz(ISOMsg isoMsg, CMQRPaymentFromBank fromBank, CMQRPaymentToBank toBank) {
//
//		MCEMessage flashizMceMessage = new MCEMessage();
//		String responseCodeFromFlashiz = null;
//		CMPaymentAcknowledgementToBank ackToBank = constructPaymentAcknowledgementToBank(fromBank,toBank);
//		flashizMceMessage.setRequest(ackToBank);
//		flashizMceMessage.setResponse(ackToBank);
//		SyncProducer syncProducer = new SyncProducer();
//		syncProducer.produceMessage("flashizISOQueue", flashizMceMessage,fromBank.getTransactionID().toString());
//		MCEMessage responseMessage = syncProducer.consumeMessage("acknowledgementServiceQueue",fromBank.getTransactionID().toString());
//		if(null!=responseMessage && responseMessage.getResponse() instanceof CMPaymentAcknowledgementFromBank){
//			CMPaymentAcknowledgementFromBank fromFlashiz= (CMPaymentAcknowledgementFromBank) responseMessage.getResponse();
//			System.out.println("Testing123 :: responseMessage from flashiz = " +fromFlashiz.getResponseCode());
//			responseCodeFromFlashiz = fromFlashiz.getResponseCode();
//		}		
//		return responseCodeFromFlashiz;
//	}
//
//	private CMPaymentAcknowledgementToBank constructPaymentAcknowledgementToBank(
//			CMQRPaymentFromBank fromBank, CMQRPaymentToBank toBank) {
//		CMPaymentAcknowledgementToBank ackToBank = new CMPaymentAcknowledgementToBank();
//		ackToBank.copy(toBank);
//		ackToBank.setUserAPIKey(toBank.getUserAPIKey());
//		ackToBank.setMerchantData(toBank.getMerchantData());
//		ackToBank.setInvoiceNo(toBank.getInvoiceNo());
//		ackToBank.setInfo3(toBank.getInfo3());
//		ackToBank.setBillerCode(toBank.getBillerCode());
//		ackToBank.setResponseCodeString(fromBank.getResponseCode());
//		return ackToBank;
//
//	}

}
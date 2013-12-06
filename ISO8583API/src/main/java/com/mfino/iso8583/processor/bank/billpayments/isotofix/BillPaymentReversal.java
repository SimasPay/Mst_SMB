package com.mfino.iso8583.processor.bank.billpayments.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentReversalToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.billpayments.IUMGISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;
import com.mfino.util.DateTimeUtil;

public class BillPaymentReversal implements IUMGISOtoFIXProcessor {

	@Override
	public CFIXMsg process(UMGH2HISOMessage isoMsg, CFIXMsg msg) throws Exception {

		CMBillPaymentReversalToBank request = (CMBillPaymentReversalToBank) msg;
		if (!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getResponseCode()))
			ISOtoFIXProcessor.getGenericResponse(isoMsg, request);
		CMBillPaymentReversalFromBank response = new CMBillPaymentReversalFromBank();
		response.copy(request);
		response.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		response.setResponseCode(isoMsg.getResponseCode());
		response.header().setMsgSeqNum(null);
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		CMBillPaymentReversalFromBank.CGEntries[] pEntry = response.allocateEntries(10);
		String transactionResponseData = isoMsg.getTransactionResponseData();
		String paymentDetails = null;
		if (transactionResponseData.length() > 85) {
			for (int i = 0; i < 1; i++) {
				paymentDetails = transactionResponseData;
				pEntry[i] = new CMBillPaymentReversalFromBank.CGEntries();
				pEntry[i].setPaymentInquiryDetails(paymentDetails);
			}
		}
		return response;
	}
}

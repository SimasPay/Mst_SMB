package com.mfino.iso8583.processor.bank.billpayments.isotofix;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.billpayments.IUMGISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;
import com.mfino.util.DateTimeUtil;

public class BillpaymentInquiry implements IUMGISOtoFIXProcessor {

	@Override
	public CFIXMsg process(UMGH2HISOMessage isoMsg, CFIXMsg msg) throws Exception {

		CMBillPaymentInquiryToBank request = (CMBillPaymentInquiryToBank) msg;
		if (!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getResponseCode()))
			ISOtoFIXProcessor.getGenericResponse(isoMsg, request);
		CMBillPaymentInquiryFromBank response = new CMBillPaymentInquiryFromBank();
		response.copy(request);
		response.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		response.setResponseCode(isoMsg.getResponseCode());
		response.header().setMsgSeqNum(null);
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		CMBillPaymentInquiryFromBank.CGEntries[] pEntry = response.allocateEntries(10);
		String transactionResponseData = isoMsg.getTransactionResponseData();
		int billInfoOffset = request.getBillRefOffSet();
		String billInformationData = null;
		String billReferenceNumber = null;
		String paymentDetails = null;
		if (transactionResponseData.length() > 85) {
			for (int i = 0; i < 1; i++) {
				billReferenceNumber = transactionResponseData.substring(billInfoOffset, billInfoOffset + 16);
				paymentDetails = transactionResponseData;
				pEntry[i] = new CMBillPaymentInquiryFromBank.CGEntries();
				pEntry[i].setBillPaymentReferenceID(billReferenceNumber);
				pEntry[i].setPaymentInquiryDetails(paymentDetails);
				if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO)>1)
					pEntry[i].setAmount(request.getAmount());
				else {
					request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()).divide(new BigDecimal(100)));
					pEntry[i].setAmount(new BigDecimal(isoMsg.getTransactionAmount()).divide(new BigDecimal(100)));
				}
			}
		}
		return response;
	}

}

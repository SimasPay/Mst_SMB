package com.mfino.iso8583.processor.bank.billpayments.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentTopupFromBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentTopupToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.billpayments.IUMGISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;
import com.mfino.util.DateTimeUtil;

public class BillpaymentTopup implements IUMGISOtoFIXProcessor {

	@Override
	public CFIXMsg process(UMGH2HISOMessage isoMsg, CFIXMsg msg) throws Exception {

		CMBillPaymentTopupToBank request = (CMBillPaymentTopupToBank) msg;
		if (!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getResponseCode()))
			ISOtoFIXProcessor.getGenericResponse(isoMsg, request);
		CMBillPaymentTopupFromBank response = new CMBillPaymentTopupFromBank();
		response.copy(request);
		response.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		response.setResponseCode(isoMsg.getResponseCode());
		response.header().setMsgSeqNum(null);
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		String transactionResponseData = isoMsg.getTransactionResponseData();
		int billInfoOffset = 14;
		String paymentVoucherNumber = null;
		String voucherWindowPeriod = null;
		if (transactionResponseData.length() > 29) {
			paymentVoucherNumber = transactionResponseData.substring(billInfoOffset, billInfoOffset + 16);
			voucherWindowPeriod = transactionResponseData.substring(billInfoOffset + 16, billInfoOffset + 24);
			response.setPaymentVoucherNumber(paymentVoucherNumber);
			response.setPaymentVoucherPeriodYYYYMMDD(Long.parseLong(voucherWindowPeriod));
		}
		return response;
	}
}

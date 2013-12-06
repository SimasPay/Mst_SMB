package com.mfino.iso8583.processor.mobileoperator.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.iso8583.processor.mobileoperator.INBSISOtoFIXProcessor;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;
import com.mfino.util.DateTimeUtil;

public class PostpaidBillPayment implements INBSISOtoFIXProcessor {

	@Override
	public CFIXMsg process(MobileOperatorISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator) request;
		CMCommodityTransferFromOperator fromOperator = new CMCommodityTransferFromOperator();
		fromOperator.copy(toOperator);
		fromOperator.setResponseCode(Integer.parseInt(isoMsg.getResponseCode()));

		if (!CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(fromOperator.getResponseCode())) {
			fromOperator.setRejectReason(isoMsg.getResponseCode());
			return fromOperator;
		}
		// fromOperator.header().setMsgSeqNum(null);
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		String responseData = isoMsg.getTransactionResponseData();
		String billReferenceID = "000000000000";
		if (responseData.length() >= 27)
			billReferenceID = responseData.substring(13, 25);
		String billAmount = "000000000000";
		if (responseData.length() >= 37)
			billAmount = responseData.substring(25, 37);
		fromOperator.setPaymentVoucherPeriodYYYYMMDD(Long.parseLong(billReferenceID));

		return fromOperator;
	}

}

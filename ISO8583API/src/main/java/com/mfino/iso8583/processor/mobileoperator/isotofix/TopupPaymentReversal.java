package com.mfino.iso8583.processor.mobileoperator.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.iso8583.processor.mobileoperator.INBSISOtoFIXProcessor;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;
import com.mfino.util.DateTimeUtil;

public class TopupPaymentReversal implements INBSISOtoFIXProcessor{

	@Override
    public CFIXMsg process(MobileOperatorISOMessage isoMsg, CFIXMsg request) throws Exception {
		CMCommodityTransferReversalToOperator toOperator = (CMCommodityTransferReversalToOperator) request;
		CMCommodityTransferReversalFromOperator fromOperator = new CMCommodityTransferReversalFromOperator();
		fromOperator.copy(toOperator);
		fromOperator.setResponseCode(Integer.parseInt(isoMsg.getResponseCode()));

		if (!CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(fromOperator.getResponseCode())) {
			fromOperator.setRejectReason(isoMsg.getResponseCode());
			return fromOperator;
		}
		// fromOperator.header().setMsgSeqNum(null);
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		String responseData = isoMsg.getTransactionResponseData();
		String windowPeriod  = "20100101";
//		if(responseData.length() >= 33)
//			windowPeriod = responseData.substr(25, 8);
//		DebugPrint(0, "The TOPUP RESPONSE WINDOW PERIOD is %s\n", windowPeriod.c_str());
//		std::string voucherSerialNumber = "000000000000";
//		if(responseData.length() >= 45)
//			voucherSerialNumber = responseData.substr(33, 12);
//		DebugPrint(0, "The TOPUP RESPONSE VOUCHER NUMBER is %s\n", voucherSerialNumber.c_str());
//			response.SetPaymentVoucherPeriodYYYYMMDDValue(atol(windowPeriod.c_str()));

		if(responseData.length()>=33)
			windowPeriod = responseData.substring(25,33);
		String voucherSerialNumber = "000000000000";
		if(responseData.length()>=45)
			voucherSerialNumber  = responseData.substring(33,45);
		fromOperator.setPaymentVoucherPeriodYYYYMMDD(Long.parseLong(windowPeriod));
		fromOperator.setTransactionID(Long.parseLong(voucherSerialNumber));
		return fromOperator;
	}

	
	
}

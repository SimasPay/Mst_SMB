package com.mfino.zte.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.mfino.zte.iso8583.processor.ZTEISOtoFixProcessor;

public class PostpaidBillPaymentFromZTEProcessor implements ZTEISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) {

		CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator) request;
		CMCommodityTransferFromOperator fromOperator = new CMCommodityTransferFromOperator();
		fromOperator.copy(toOperator);
		fromOperator.setResponseCode(Integer.parseInt(isoMsg.getString(39)));

		if (!CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(fromOperator.getResponseCode())) {
			fromOperator.setRejectReason(isoMsg.getString(39));
			return fromOperator;
		}
		// fromOperator.header().setMsgSeqNum(null);
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		String responseData = isoMsg.getString(62);
		String billReferenceID = "000000000000";
		if((null != responseData) && (responseData.length() >= 27))
			billReferenceID = responseData.substring(13, 25);
		String billAmount = "000000000000";
		
		if((null != responseData) && (responseData.length() >= 37))
			billAmount = responseData.substring(25, 37);
		fromOperator.setPaymentVoucherPeriodYYYYMMDD(Long.parseLong(billReferenceID));
		
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromOperator.header().setMsgSeqNum(UniqueNumberGen.getNextNum());

		return fromOperator;
	}

}

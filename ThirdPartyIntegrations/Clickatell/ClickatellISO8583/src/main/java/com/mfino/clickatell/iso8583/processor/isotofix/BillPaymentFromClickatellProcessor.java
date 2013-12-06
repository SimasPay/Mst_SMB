package com.mfino.clickatell.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.clickatell.iso8583.processor.ClickatellISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BillPaymentFromClickatellProcessor implements ClickatellISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg fixmsg) {
        CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator) fixmsg;
		CMCommodityTransferFromOperator fromOperator = new CMCommodityTransferFromOperator();
		fromOperator.copy(toOperator);
     	fromOperator.setResponseCode(Integer.parseInt(isoMsg.getString(39)));

		if (!CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(fromOperator.getResponseCode())) {
			fromOperator.setRejectReason(isoMsg.getString(39));
			return fromOperator;
		}
		// fromOperator.header().setMsgSeqNum(null);
		
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		String responseData = isoMsg.getString(21);
		/*String billReferenceID = "000000000000";
		if((null != responseData) && (responseData.length() >= 27))
			billReferenceID = responseData.substring(13, 25);
		String billAmount = "000000000000";
		if((null != responseData) && (responseData.length() >= 37))
			billAmount = responseData.substring(25, 37);
		fromOperator.setPaymentVoucherPeriodYYYYMMDD(Long.parseLong(billReferenceID)); */
		fromOperator.setINTxnId(responseData);
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromOperator.header().setMsgSeqNum(UniqueNumberGen.getNextNum());

		return fromOperator;
	
	}
}

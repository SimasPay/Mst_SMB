package com.mfino.zte.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.mfino.zte.iso8583.processor.ZTEISOtoFixProcessor;

public class PostpaidBillPaymentReversalFromZTEProcessor implements ZTEISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) {
		CMCommodityTransferReversalToOperator toOperator = (CMCommodityTransferReversalToOperator) request;
		CMCommodityTransferReversalFromOperator fromOperator = new CMCommodityTransferReversalFromOperator();
		fromOperator.copy(toOperator);
		fromOperator.setResponseCode(Integer.parseInt(isoMsg.getString(39)));

		if (!CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(fromOperator.getResponseCode())) {
			fromOperator.setRejectReason(isoMsg.getString(39));
			return fromOperator;
		}
		// fromOperator.header().setMsgSeqNum(null);
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		String responseData = isoMsg.getString(62);
		String billReferenceID = responseData.substring(13,37);
		String billAmount = responseData.substring(25, 61);
		
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromOperator.header().setMsgSeqNum(UniqueNumberGen.getNextNum());

		return fromOperator;
	}

}

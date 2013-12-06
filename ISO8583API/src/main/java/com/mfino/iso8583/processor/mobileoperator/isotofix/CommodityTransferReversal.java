package com.mfino.iso8583.processor.mobileoperator.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.iso8583.processor.mobileoperator.INBSISOtoFIXProcessor;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;

public class CommodityTransferReversal implements INBSISOtoFIXProcessor {

	@Override
	public CFIXMsg process(MobileOperatorISOMessage isoMsg,CFIXMsg fixmsg) throws Exception {
		
		CMCommodityTransferReversalToOperator toOperator = (CMCommodityTransferReversalToOperator)fixmsg;
		
		if(CmFinoFIX.TransactionUICategory_Shareload.equals(toOperator.getSourceTransactionUICategory())) {
			ShareloadPaymentReversal sp = new ShareloadPaymentReversal();
			return sp.process(isoMsg,fixmsg);
		}
		else if(CmFinoFIX.TransactionUICategory_Bank_Channel_Payment.equals(toOperator.getSourceTransactionUICategory())||
				CmFinoFIX.TransactionUICategory_CC_Payment.equals(toOperator.getSourceTransactionUICategory())||
				CmFinoFIX.TransactionUICategory_VA_Payment.equals(toOperator.getSourceTransactionUICategory())) {
			PostpaidBillPaymentReversal pp = new PostpaidBillPaymentReversal();
			return pp.process(isoMsg,fixmsg);
		}
		else {
			TopupPaymentReversal tp = new TopupPaymentReversal();
			return tp.process(isoMsg,fixmsg);
		}
	}

}

package com.mfino.zte.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.zte.iso8583.processor.ZTEISOtoFixProcessor;

public class CommodityTransferReversalFromZTEProcessor implements ZTEISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg,CFIXMsg fixmsg) {
		
		CMCommodityTransferReversalToOperator toOperator = (CMCommodityTransferReversalToOperator)fixmsg;
		
		if(CmFinoFIX.TransactionUICategory_Shareload.equals(toOperator.getSourceTransactionUICategory())) {
//			ShareloadPaymentReversal sp = new ShareloadPaymentReversal();
//			return sp.process(isoMsg,fixmsg);
			return null;
		}
		else if(CmFinoFIX.TransactionUICategory_Bank_Channel_Payment.equals(toOperator.getSourceTransactionUICategory())||
				CmFinoFIX.TransactionUICategory_CC_Payment.equals(toOperator.getSourceTransactionUICategory())||
				CmFinoFIX.TransactionUICategory_VA_Payment.equals(toOperator.getSourceTransactionUICategory())) {
			PostpaidBillPaymentReversalFromZTEProcessor pp = new PostpaidBillPaymentReversalFromZTEProcessor();
			return pp.process(isoMsg,fixmsg);
		}
		else {
			TopupPaymentReversalFromZTEProcessor tp = new TopupPaymentReversalFromZTEProcessor();
			return tp.process(isoMsg,fixmsg);
		}
	}

}

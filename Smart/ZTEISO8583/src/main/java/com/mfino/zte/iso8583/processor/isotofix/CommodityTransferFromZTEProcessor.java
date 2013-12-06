package com.mfino.zte.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.zte.iso8583.processor.ZTEISOtoFixProcessor;

public class CommodityTransferFromZTEProcessor implements ZTEISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg fixmsg) {

		CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator) fixmsg;

		if (CmFinoFIX.TransactionUICategory_Shareload.equals(toOperator.getSourceTransactionUICategory())) {
//			ShareloadPayment sp = new ShareloadPayment();
//			return sp.process(isoMsg, fixmsg);
			return null;
		}
		else if (CmFinoFIX.TransactionUICategory_Bank_Channel_Payment.equals(toOperator.getSourceTransactionUICategory())
		        || CmFinoFIX.TransactionUICategory_CC_Payment.equals(toOperator.getSourceTransactionUICategory())
		        || CmFinoFIX.TransactionUICategory_VA_Payment.equals(toOperator.getSourceTransactionUICategory())) {
			PostpaidBillPaymentFromZTEProcessor pp = new PostpaidBillPaymentFromZTEProcessor();
			return pp.process(isoMsg, fixmsg);
		}
		else {
			TopupPaymentFromZTEProcessor tp = new TopupPaymentFromZTEProcessor();
			return tp.process(isoMsg, fixmsg);
		}
	}
}

package com.mfino.zte.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;

public class CommodityTransferReversalToZTEProcessor extends MobileOperatorRequestProcessor {

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		
		CMCommodityTransferReversalToOperator toOperator = (CMCommodityTransferReversalToOperator)fixmsg;
		
		if(CmFinoFIX.TransactionUICategory_Shareload.equals(toOperator.getSourceTransactionUICategory())) {
//			ShareloadPaymentReversal sp = new ShareloadPaymentReversal();
//			return sp.process(fixmsg);
			return null;
		}
//		else if(pRequest -> GetSourceTransactionUICategoryValue() == mFinoFIX_TransactionUICategory_Bank_Channel_Payment
//				|| pRequest -> GetSourceTransactionUICategoryValue() == mFinoFIX_TransactionUICategory_CC_Payment
//				|| pRequest -> GetSourceTransactionUICategoryValue() == mFinoFIX_TransactionUICategory_VA_Payment) {
//				return OnPostPaidBillPaymentToOperator(Msg, pRequest);
//			} else {
//				return OnTopupPaymentToOperator(Msg, pRequest);
//			}
//		
		else if(CmFinoFIX.TransactionUICategory_Bank_Channel_Payment.equals(toOperator.getSourceTransactionUICategory())||
				CmFinoFIX.TransactionUICategory_CC_Payment.equals(toOperator.getSourceTransactionUICategory())||
				CmFinoFIX.TransactionUICategory_VA_Payment.equals(toOperator.getSourceTransactionUICategory())) {
			PostpaidBillPaymentReversalToZTEProcessor pp = new PostpaidBillPaymentReversalToZTEProcessor();
			return pp.process(fixmsg);
		}
		else {
			TopupPaymentReversalToZTEProcessor tp = new TopupPaymentReversalToZTEProcessor();
			return tp.process(fixmsg);
		}
	}

}

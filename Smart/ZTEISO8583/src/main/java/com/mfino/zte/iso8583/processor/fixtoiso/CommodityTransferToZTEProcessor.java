package com.mfino.zte.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;

public class CommodityTransferToZTEProcessor extends MobileOperatorRequestProcessor {
	
	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator)fixmsg;
		
		if(CmFinoFIX.TransactionUICategory_Shareload.equals(toOperator.getSourceTransactionUICategory())) {
//			ShareloadPayment sp = new ShareloadPayment();
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
			PostpaidBillPaymentToZTEProcessor pp = new PostpaidBillPaymentToZTEProcessor();
			pp.setConstantFieldsMap(constantFieldsMap);
			return pp.process(fixmsg);
		}
		else {
			TopupPaymentToZTEProcessor tp = new TopupPaymentToZTEProcessor();
			tp.setConstantFieldsMap(constantFieldsMap);
			return tp.process(fixmsg);
		}
	}

}

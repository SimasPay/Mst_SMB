package com.mfino.iso8583.processor.mobileoperator.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;

public class CommodityTransfer implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		
		CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator)fixmsg;
		
		if(CmFinoFIX.TransactionUICategory_Shareload.equals(toOperator.getSourceTransactionUICategory())) {
			ShareloadPayment sp = new ShareloadPayment();
			return sp.process(fixmsg);
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
			PostpaidBillPayment pp = new PostpaidBillPayment();
			return pp.process(fixmsg);
		}
		else {
			TopupPayment tp = new TopupPayment();
			return tp.process(fixmsg);
		}
	}

}

package com.mfino.iso8583.processor.mobileoperator.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.mobileoperator.INBSISOtoFIXProcessor;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;

public class CommodityTransfer implements INBSISOtoFIXProcessor {

	@Override
	public CFIXMsg process(MobileOperatorISOMessage isoMsg, CFIXMsg fixmsg) throws Exception {

		CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator) fixmsg;

		if (CmFinoFIX.TransactionUICategory_Shareload.equals(toOperator.getSourceTransactionUICategory())) {
			ShareloadPayment sp = new ShareloadPayment();
			return sp.process(isoMsg, fixmsg);
		}
		else if (CmFinoFIX.TransactionUICategory_Bank_Channel_Payment.equals(toOperator.getSourceTransactionUICategory())
		        || CmFinoFIX.TransactionUICategory_CC_Payment.equals(toOperator.getSourceTransactionUICategory())
		        || CmFinoFIX.TransactionUICategory_VA_Payment.equals(toOperator.getSourceTransactionUICategory())) {
			PostpaidBillPayment pp = new PostpaidBillPayment();
			return pp.process(isoMsg, fixmsg);
		}
		else {
			TopupPayment tp = new TopupPayment();
			return tp.process(isoMsg, fixmsg);
		}
	}
}

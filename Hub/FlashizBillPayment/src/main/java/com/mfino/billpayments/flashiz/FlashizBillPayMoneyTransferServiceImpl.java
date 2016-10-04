package com.mfino.billpayments.flashiz;

import com.mfino.billpayments.service.impl.BillPayMoneyTransferServiceImpl;
public class FlashizBillPayMoneyTransferServiceImpl extends BillPayMoneyTransferServiceImpl {
	
	public String getReversalResponseQueue(){
		return "jms:FlashizSuspenseAndChargesRRQueue?disableReplyTo=true";
	}
	public String getSourceToSuspenseBankResponseQueue(){
		return "jms:FlashizSourceToSuspenseBRQueue?disableReplyTo=true";
	}
	public String getSuspenseToDestBankResposeQueue(){
		return "jms:FlashizSuspenseToDestBRQueue?disableReplyTo=true";
	}
	
	
}

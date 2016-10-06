package com.mfino.interbank.uangku;

import com.mfino.billpayments.service.impl.BillPayMoneyTransferServiceImpl;
/**
 * 
 * @author HemanthKumar
 *
 */
public class InterBankNewMoneyTransferServiceImpl extends BillPayMoneyTransferServiceImpl {
	
	public String getReversalResponseQueue(){
		return "jms:ibtSuspenseAndChargesRRQueue?disableReplyTo=true";
	}
	public String getSourceToSuspenseBankResponseQueue(){
		return "jms:ibtSourceToSuspenseBRQueue?disableReplyTo=true";
	}
	public String getSuspenseToDestBankResposeQueue(){
		return "jms:ibtSuspenseToDestBRQueue?disableReplyTo=true";
	}
	
	
}

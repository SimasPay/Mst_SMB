package com.mfino.billpayments.bsim;

import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;


public interface BillPaymentService {
	
	public MCEMessage billPayMoneyTransferInquirySourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferInquiryCompletionSourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferSourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferCompletionSourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage billPayAmountInquiry(MCEMessage mceMessage);
	
	public MCEMessage billPayAmountInquiryFromBank(MCEMessage mceMessage);
	
	public MCEMessage billPayReversalToBank(MCEMessage mceMessage);
	
	public MCEMessage billPayReversalFromBank(MCEMessage mceMessage);
	
}

package com.mfino.billpayments.bsim;

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
	
	public MCEMessage qrPaymentMoneyTransferSourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage qrPaymentMoneyTransferCompletionSourceToDestination(MCEMessage mceMessage);

	public MCEMessage qrPaymentMoneyTransferInquiryCompletionSourceToDestination(
			MCEMessage mceMessage);

	public MCEMessage qrPaymentMoneyTransferInquirySourceToDestination(
			MCEMessage mceMessage);
	
	public MCEMessage qrPaymentReversalToBank(MCEMessage mceMessage);
		
	public MCEMessage qrPaymentReversalFromBank(MCEMessage mceMessage);
	
	public MCEMessage qrPaymentInquiryUpdateToBillerInquiryPending(MCEMessage mceMessage);
	
	public MCEMessage qrPaymentUpdateToBillerConfirmPending(MCEMessage mceMessage);


	
}

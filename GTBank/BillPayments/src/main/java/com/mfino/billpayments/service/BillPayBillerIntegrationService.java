package com.mfino.billpayments.service;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 * Interprets response from third party Billers or Aggregators, Correspondingly updates status of bill payments.
 */
public interface BillPayBillerIntegrationService {

	public MCEMessage handleBillPayInquiryResponse(MCEMessage mceMessage);
	
	public MCEMessage handleBillPayConfirmationResponse(MCEMessage mceMessage);
	
	public MCEMessage preBillerInquiry(MCEMessage mceMessage);
	
	public MCEMessage preBillerConfirmation(MCEMessage mceMessage);
}

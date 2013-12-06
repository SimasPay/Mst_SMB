package com.mfino.billpayments.service;

import org.apache.camel.Header;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author Sasi
 * Listens for events from bill pay work flow and notifies web service accordingly.
 *
 */
public interface BillPayEventProcessingService {
	
	public MCEMessage processEvent(MCEMessage mceMessage, @Header("eventContext") String eventContext);
	
	public MCEMessage constructResponse(MCEMessage mceMessage);
	
	public MCEMessage SRC_SUSPENSE_INQ_FAILED(MCEMessage mceMessage);

	public MCEMessage SRC_SUSPENSE_CONFIRMATION_FAILED(MCEMessage mceMessage);
	
	public MCEMessage BILLER_INQUIRY_COMPLETED(MCEMessage mceMessage);
	
	public MCEMessage BILLER_CONFIRMATION_FAILED(MCEMessage mceMessage);
	
	public MCEMessage BILLER_CONFIRMATION_SUCCESSFUL(MCEMessage mceMessage);
	
	public MCEMessage SRC_SUSPENSE_INQ_SUCCESS(MCEMessage mceMessage);
}

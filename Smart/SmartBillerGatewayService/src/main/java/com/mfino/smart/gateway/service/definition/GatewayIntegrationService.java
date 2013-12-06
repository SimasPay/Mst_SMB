package com.mfino.smart.gateway.service.definition;

import org.apache.camel.Body;
import org.apache.camel.Header;

import com.mfino.billpayments.service.BillPayBillerIntegrationService;
import com.mfino.mce.core.MCEMessage;

public interface GatewayIntegrationService extends BillPayBillerIntegrationService{
	
	public MCEMessage handleBillPayAdviceResponse(MCEMessage mceMessage);
	
	public MCEMessage preBillerAdvice(MCEMessage mceMessage);
	
	public MCEMessage handleBillPayReversalResponse(MCEMessage mceMessage);
	
	public MCEMessage preBillerReversal(MCEMessage mceMessage);
	
	public MCEMessage handleNoResponse(@Body Object body,@Header("mceMessage") Object mceMessage);

}

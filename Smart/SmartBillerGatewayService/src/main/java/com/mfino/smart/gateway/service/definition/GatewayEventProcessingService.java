package com.mfino.smart.gateway.service.definition;

import com.mfino.billpayments.service.BillPayEventProcessingService;
import com.mfino.mce.core.MCEMessage;

public interface GatewayEventProcessingService extends BillPayEventProcessingService {
	
	public MCEMessage BILLER_INQUIRY_FAILED(MCEMessage mceMessage);

}

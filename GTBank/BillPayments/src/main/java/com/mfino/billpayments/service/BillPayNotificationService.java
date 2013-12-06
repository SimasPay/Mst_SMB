package com.mfino.billpayments.service;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public interface BillPayNotificationService {
	
	public MCEMessage notificationToDestination(MCEMessage mceMessage);
	
	public MCEMessage notificationToBiller(MCEMessage mceMessage);

}

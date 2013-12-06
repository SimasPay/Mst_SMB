package com.mfino.billpayments.service;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public interface BillPayPendingResolveService {

	public MCEMessage resolvePendingTransaction(MCEMessage mceMessage);
}

package com.mfino.billpayments.service;

import java.util.List;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public interface BillpaymentsPendingClearanceService {

	public List<MCEMessage> clearBillerConfirmationPendingTransactions();
}

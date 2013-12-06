package com.mfino.billpayments.service;

import java.util.List;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 * Clears money in suspence pocket.
 * Standard service common for all kinds of Bill Payments.
 */
public interface BillPaymentsSuspenceClearanceService {
	
	/**
	 * Check status in BILL_PAYMENTS, identify all suspense to destination failed transactions.
	 * Reinitiate suspence to destination transfer and mark billpay status approriately.
	 * @return
	 */
	public List<MCEMessage> clearSuspenceAccounts();
}

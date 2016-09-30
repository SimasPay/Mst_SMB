/**
 * 
 */
package com.mfino.domain;

import java.math.BigDecimal;

/**
 * This object holds the details of the Amount that needs to be debited,
 * credited and the charges applied.
 * 
 * @author Chaitanya
 *
 */
public class Transaction {

	public BigDecimal getAmountToDebit() {
		return amountToDebit;
	}

	public void setAmountToDebit(BigDecimal amountToDebit) {
		this.amountToDebit = amountToDebit;
	}

	public BigDecimal getAmountToCredit() {
		return amountToCredit;
	}

	public void setAmountToCredit(BigDecimal amountToCredit) {
		this.amountToCredit = amountToCredit;
	}

	public BigDecimal getAmountTowardsCharges() {
		return amountToCharge;
	}

	public void setAmountToCharge(BigDecimal amountToCharge) {
		this.amountToCharge = amountToCharge;
	}

	private BigDecimal amountToDebit;
	
	private BigDecimal amountToCredit;
	
	private BigDecimal amountToCharge;
	
	private ServiceChargeTxnLog serviceChargeTransactionLog;

	public ServiceChargeTxnLog getServiceChargeTransactionLog() {
		return serviceChargeTransactionLog;
	}

	public void setServiceChargeTransactionLog(
			ServiceChargeTxnLog serviceChargeTransactionLog) {
		this.serviceChargeTransactionLog = serviceChargeTransactionLog;
	}

}

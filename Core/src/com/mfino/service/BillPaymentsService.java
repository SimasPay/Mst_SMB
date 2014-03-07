/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;

/**
 * @author Sreenath
 *
 */
public interface BillPaymentsService {
	/**
	 * Gets the list of all billPayment records matching the query
	 * @param bpq
	 * @return
	 */
	public List<BillPayments> get(BillPaymentsQuery bpq);
	
	/**
	 * Saves the BillPayments record to database
	 * @param bp
	 */
	public void save(BillPayments bp);
	
	/**
	 * Gets the Bill Payments entry for the given SCTL ID
	 * @param sctlID
	 * @return
	 */
	public BillPayments getBySctlId(long sctlID);

}

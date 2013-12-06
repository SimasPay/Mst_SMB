/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.TransactionType;

/**
 * @author Sreenath
 *
 */
public interface TransactionTypeService {
	/**
	 * Returns the TransactionType by the TransactionTypeID
	 * @param TransactionTypeId
	 * @return
	 */
	public TransactionType getTransactionTypeById(Long transactionTypeId);
	public TransactionType getTransactionTypeByName(String transactionName);

}

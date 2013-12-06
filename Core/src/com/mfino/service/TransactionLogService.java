/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.TransactionsLog;

/**
 * @author Sreenath
 *
 */
public interface TransactionLogService {

	/** save transactionLog details
	 * saves transactionLog
	 * @param transactionLog
	 */
	public void save(TransactionsLog transactionLog);
	/**
	 * message , data and mfinoservicepovider with id 1 is set into transactionLog object 
	 * and saved into transactionlogtable
	 * @param messageCode
	 * @param data
	 * @return
	 */

	public TransactionsLog saveTransactionsLog(Integer messageCode, String data);
	
/**
 * saves details innto transactionLog
 * @param messageCode
 * @param data
 * @param parentTxnID
 * @return
 */
	public TransactionsLog saveTransactionsLog(Integer messageCode, String data,Long parentTxnID);
	
	public TransactionsLog getById(Long txnLogID);

}

/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.TransactionLog;


/**
 * @author Sreenath
 *
 */
public interface TransactionLogService {

	/** save transactionLog details
	 * saves transactionLog
	 * @param transactionLog
	 */
	public void save(TransactionLog transactionLog);
	/**
	 * message , data and mfinoservicepovider with id 1 is set into transactionLog object 
	 * and saved into transactionlogtable
	 * @param messageCode
	 * @param data
	 * @return
	 */

	public TransactionLog saveTransactionsLog(Integer messageCode, String data);
	
/**
 * saves details innto transactionLog
 * @param messageCode
 * @param data
 * @param parentTxnID
 * @return
 */
	public TransactionLog saveTransactionsLog(Integer messageCode, String data,Long parentTxnID);
	
	public TransactionLog getById(Long txnLogID);

}

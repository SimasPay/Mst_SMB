package com.mfino.service;


public interface TransactionIdentifierService {
	/**
	 * Creates the transactionIdentifer using the sourceMDN,timestamp and a random number generator and returns the same.
	 * @param uniqueIdMDN
	 * @return
	 */
	public String generateTransactionIdentifier(String uniqueIdMDN);
	/**
	 * This method saves the transactionIdentifier and sctlID in the database.This method is called for only those transactions that have a transactionIdentifier. 
	 * @param transactionIdentifier
	 * @param sctlID
	 */
	public void createTrxnIdentifierDbEntry(String transactionIdentifier,Long sctlID);
}

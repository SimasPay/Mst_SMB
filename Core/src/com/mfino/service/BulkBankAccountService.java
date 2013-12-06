/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.BulkBankAccount;
import com.mfino.exceptions.MfinoRuntimeException;

/**
 * @author Sreenath
 *
 */
public interface BulkBankAccountService {
	/**
	 * Saves the BulkBankAccount record
	 * @param bba
	 * @throws MfinoRuntimeException
	 */
	public void save(BulkBankAccount bba) throws MfinoRuntimeException;

}

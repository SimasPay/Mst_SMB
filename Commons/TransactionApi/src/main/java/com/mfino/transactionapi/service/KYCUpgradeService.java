/**
 * 
 */
package com.mfino.transactionapi.service;


/**
 * @author Bala Sunku
 *
 */
public interface KYCUpgradeService {

	public boolean processKYCUpgradeFile(String filePath);
	public String generateFilePath(String filePath, String extraString);
}

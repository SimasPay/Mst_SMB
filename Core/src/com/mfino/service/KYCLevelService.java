/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.KYCLevel;

/**
 * @author Sreenath
 *
 */
public interface KYCLevelService {
	/**
	 * Get KYCLevel record by the kyclevel
	 * @param kyclevel
	 * @return
	 */
	public KYCLevel getByKycLevel(Long kyclevel);

}

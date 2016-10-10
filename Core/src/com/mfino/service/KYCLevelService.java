/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.KycLevel;

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
	public KycLevel getByKycLevel(Long kyclevel);

}

/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.InterbankTransfer;

/**
 * @author Bala Sunku
 *
 */
public interface IBTService {
	
	/**
	 * Gets the Inter bank transfer entry for the given SCTL ID
	 * @param sctlID
	 * @return
	 */
	public InterbankTransfer getBySctlId(Long sctlID);

}

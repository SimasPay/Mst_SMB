/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.Pocket;

/**
 * @author Sreenath
 *
 */
public interface DefaultPocketMaintainerService {
	
	/**
	 * Checks and changes the isDefault status of the given pocket
	 * @param pocket
	 * @param isNew
	 * @return
	 */
	public int setDefaultPocket(Pocket pocket, boolean isNew);

}

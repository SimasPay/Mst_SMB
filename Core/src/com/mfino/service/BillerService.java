/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.Partner;

/**
 * @author Sreenath
 *
 */
public interface BillerService {

	/**
	 * Returns the partner from the billerCode 
	 * @param billerCode
	 * @return
	 */
	public Partner getPartner(String billerCode);
	

}

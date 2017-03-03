/**
 * 
 */
package com.mfino.service;


/**
 * @author Sreenath
 *
 */
public interface MDNRetireService {
	/**
	 * 
	 * @param subscriberMDNId
	 * @return
	 */
	public Integer retireMDN(Long subscriberMDNId);
	
	public Integer closeMDN(Long subscriberMDNId);
	
	public void retireAllCardPans(Long mdnId, boolean isRetireBankPocket);

}

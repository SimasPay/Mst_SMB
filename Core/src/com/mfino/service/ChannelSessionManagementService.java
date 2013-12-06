/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.ChannelSessionManagement;

/**
 * @author Sreenath
 *
 */
public interface ChannelSessionManagementService {

	
	/**
	 * Gets the channelSessionManagement with the MDNID
	 * @param mdnID
	 * @return
	 */
	public ChannelSessionManagement getChannelSessionManagemebtByMDNID(Long mdnID);
	
	/**
	 * Saves the ChannelSessionManagement record to dataabase
	 * @param csm
	 */
	public void saveCSM(ChannelSessionManagement csm);
}

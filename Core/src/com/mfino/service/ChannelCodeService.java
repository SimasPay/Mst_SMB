/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.MfinoServiceProvider;

/**
 * @author Sreenath
 *
 */
public interface ChannelCodeService {
	/**
	 * Calls the getByChannelSourceApplication method in the channelCodeDAO to return the channel code based
	 * on the sourceApplication
	 * @param channelSourceApplication
	 * @return
	 */
	public ChannelCode getChannelCodebySourceApplication(Integer channelSourceApplication);
	
	/**
	 * Gets the channelCode from the getChannelCodebySourceApplication method and returns the channelName
	 * @param channelSourceApplication
	 * @return
	 */
	public String getChannelNameBySourceApplication(Integer channelSourceApplication);
	
	/**
	 * Gets the channelCode Object by the given channel code string
	 * @param channelCodeStr
	 * @return
	 */
	public ChannelCode getChannelCodeByChannelCode(String channelCodeStr);
	
	/**
	 * Gets the channelCode Object by the given channel code ID
	 * @param channelID
	 * @return
	 */
	public ChannelCode getChannelCodeByChannelId(Long channelID);

	public MfinoServiceProvider getMFSPbyID(int id);
}

package com.mfino.handset.subscriber.ui;

import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

/**
 * @author sasidhar
 *
 */
public interface MfinoConfig {
	
	public void setConfigData(UserDataContainer mFinoConfigData);
	
	public UserDataContainer	getMfinoConfigData();
}

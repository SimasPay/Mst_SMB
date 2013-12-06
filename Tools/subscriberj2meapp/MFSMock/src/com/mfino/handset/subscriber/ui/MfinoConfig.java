package com.mfino.handset.subscriber.ui;

import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 *
 */
public interface MfinoConfig {
	
	public void setConfigData(MfinoConfigData mFinoConfigData);
	
	public MfinoConfigData	getMfinoConfigData();
}

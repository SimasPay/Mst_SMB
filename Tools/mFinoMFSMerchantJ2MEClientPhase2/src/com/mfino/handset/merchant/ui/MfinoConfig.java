package com.mfino.handset.merchant.ui;

import com.mfino.handset.merchant.util.MfinoConfigData;


/**
 * @author sasidhar
 *
 */
public interface MfinoConfig {
	
	public void setConfigData(MfinoConfigData mFinoConfigData);
	
	public MfinoConfigData	getMfinoConfigData();
}

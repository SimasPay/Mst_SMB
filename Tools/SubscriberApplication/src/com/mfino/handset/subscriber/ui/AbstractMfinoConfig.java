package com.mfino.handset.subscriber.ui;

import javax.microedition.lcdui.Alert;

import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

public abstract class AbstractMfinoConfig implements MfinoConfig{
	
	public UserDataContainer mFinoConfigData;
	public Alert alert;
	
	public AbstractMfinoConfig(){
		
	}
	
	public AbstractMfinoConfig(UserDataContainer mFinoConfigData){
		this.mFinoConfigData = mFinoConfigData;
	}
	
	public void setConfigData(UserDataContainer mFinoConfigData) {
		this.mFinoConfigData = mFinoConfigData;
	}

	public UserDataContainer getMfinoConfigData() {
		return this.mFinoConfigData;
	}

}

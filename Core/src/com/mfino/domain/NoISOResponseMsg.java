package com.mfino.domain;

import com.mfino.fix.CmFinoFIX.CMBase;

public class NoISOResponseMsg extends CMBase{
	
	
	public NoISOResponseMsg(){
		setMessageType(9999);
		setSourceApplication(1);
	}
	
	@Override
	public boolean checkRequiredFields(){
		return true;
	}

}

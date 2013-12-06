package com.mfino.cashin;

import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;

public class InvalidXMLException extends Exception{
	
	private CMInterswitchCashin cashin;


	public CMInterswitchCashin getCashin() {
		return cashin;
	}

	public void setCashin(CMInterswitchCashin cashin) {
		this.cashin = cashin;
	}

	public InvalidXMLException(Exception ex){
		super(ex);
	}

	public InvalidXMLException(String msg){
		super(msg);
	}

	
}

package com.mfino.domain;

/**
 * @author sasidhar
 */
public enum FIXResponseType {
	
	BANK_TRANSFER_INQUIRY("BANK_TRANSFER_INQUIRY"),
	BANK_TRANSFER("BANK_TRANSFER"),
	MOBILE_SHOPPING_SETTLEMENT("MOBILE_SHOPPING_SETTLEMENT");
	
	FIXResponseType(String displayName){
		this.displayName = displayName;
	}
	
	private String displayName;
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}

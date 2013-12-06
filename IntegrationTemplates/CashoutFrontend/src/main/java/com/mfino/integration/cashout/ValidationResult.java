package com.mfino.integration.cashout;

public enum ValidationResult {
	
	InvalidRequest(1),ValidRequest(0);
	
	Integer value;
	
	private ValidationResult(Integer value){
		this.value = value;
		
	}

}

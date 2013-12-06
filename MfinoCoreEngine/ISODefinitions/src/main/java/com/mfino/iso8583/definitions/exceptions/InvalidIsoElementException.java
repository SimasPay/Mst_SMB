package com.mfino.iso8583.definitions.exceptions;

public class InvalidIsoElementException extends Exception{
	
	private String ISOFieldNumber;
	
	public String getISOFieldNumber() {
    	return ISOFieldNumber;
    }

	public void setISOFieldNumber(String iSOFieldNumber) {
    	ISOFieldNumber = iSOFieldNumber;
    }

	public InvalidIsoElementException(String fieldNo){
		ISOFieldNumber = fieldNo;
	}

}

package com.mfino.exceptions;

public class InvalidChargeDefinitionException extends Exception {

	public InvalidChargeDefinitionException(String message){
		super(message);
	}
	
	public InvalidChargeDefinitionException(Throwable throwable){
		super(throwable);
	}
}

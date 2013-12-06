package com.mfino.exceptions;

/**
 * @author Bala Sunku
 */
public class InvalidServiceException extends Exception {

	public InvalidServiceException(String message){
		super(message);
	}
	
	public InvalidServiceException(Throwable throwable){
		super(throwable);
	}
}

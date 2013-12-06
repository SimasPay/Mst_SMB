package com.mfino.mce.backend.impl;

/**
 * @author sasidhar
 * Unchecked exception to handle RuntimeExceptions internally.
 */
public class BackendRuntimeException extends RuntimeException{

	public BackendRuntimeException(Throwable throwable){
		super(throwable);
	}
	
	public BackendRuntimeException(String message){
		super(message);
	}
}

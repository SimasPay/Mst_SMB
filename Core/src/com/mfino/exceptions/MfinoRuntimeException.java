package com.mfino.exceptions;

/**
 * @author sasidhar
 * A generic runtime exception intended to be used else where to wrap other 
 * checked exceptions.
 */
public class MfinoRuntimeException extends RuntimeException {

	public MfinoRuntimeException(String message){
		super(message);
	}
	
	public MfinoRuntimeException(Throwable throwable){
		super(throwable);
	}
}

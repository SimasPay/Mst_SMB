package com.mfino.exceptions;

/**
 * @author Bala Sunku
 */
public class DistributionException extends Exception {

	public DistributionException(String message){
		super(message);
	}
	
	public DistributionException(Throwable throwable){
		super(throwable);
	}
}

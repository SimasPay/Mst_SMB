package com.mfino.domain;

/**
 * 
 * @author sasidhar
 *
 */
public class BankTransferResponse extends FIXResponse{
	
	
	@Override
	public FIXResponseType getResponseType() {
		return FIXResponseType.BANK_TRANSFER;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}

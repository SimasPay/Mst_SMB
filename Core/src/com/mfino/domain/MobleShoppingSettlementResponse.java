package com.mfino.domain;

/**
 * 
 * @author sasidhar
 *
 */
public class MobleShoppingSettlementResponse extends FIXResponse{
	
	
	@Override
	public FIXResponseType getResponseType() {
		return FIXResponseType.MOBILE_SHOPPING_SETTLEMENT;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}

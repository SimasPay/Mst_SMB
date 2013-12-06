package com.mfino.dao.query;

/**
*
* @author Satya
*/
public class RetiredCardPANInfoQuery extends BaseQuery{
	private String cardPan;
	private Integer retireCount;
	public String getCardPan() {
		return cardPan;
	}
	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}
	public Integer getRetireCount() {
		return retireCount;
	}
	public void setRetireCount(Integer retireCount) {
		this.retireCount = retireCount;
	}	
}

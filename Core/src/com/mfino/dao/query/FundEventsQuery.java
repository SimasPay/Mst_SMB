package com.mfino.dao.query;

public class FundEventsQuery extends BaseQuery {
	Integer fundEventType;
	String fundEventDescription;
	public String getFundEventDescription() {
		return fundEventDescription;
	}
	public void setFundEventDescription(String fundEventDescription) {
		this.fundEventDescription = fundEventDescription;
	}
	public Integer getFundEventType() {
		return fundEventType;
	}
	public void setFundEventType(Integer fundEventType) {
		this.fundEventType = fundEventType;
	}
	
	
	
}

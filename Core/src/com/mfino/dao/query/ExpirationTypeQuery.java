package com.mfino.dao.query;

public class ExpirationTypeQuery extends BaseQuery {
	private Integer expiryType;
	private Integer expiryMode;	
	private Long expiryValue;
	private String expiryDescription;
	public Integer getExpiryType() {
		return expiryType;
	}
	public void setExpiryType(int expiryType) {
		this.expiryType = expiryType;
	}
	public Integer getExpiryMode() {
		return expiryMode;
	}
	public void setExpiryMode(Integer expiryMode) {
		this.expiryMode = expiryMode;
	}
	public Long getExpiryValue() {
		return expiryValue;
	}
	public void setExpiryValue(Long expiryValue) {
		this.expiryValue = expiryValue;
	}
	public String getExpiryDescription() {
		return expiryDescription;
	}
	public void setExpiryDescription(String expiryDescription) {
		this.expiryDescription = expiryDescription;
	}
	
	
	

}


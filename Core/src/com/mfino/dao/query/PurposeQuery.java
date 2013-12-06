package com.mfino.dao.query;

public class PurposeQuery extends BaseQuery {
	
	private Integer category;
	private String code;
	private String[] multiCode;
	
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String[] getMultiCode() {
		return multiCode;
	}
	public void setMultiCode(String[] multiCode) {
		this.multiCode = multiCode;
	}
	
	

}

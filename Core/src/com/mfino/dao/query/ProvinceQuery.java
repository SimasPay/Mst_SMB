package com.mfino.dao.query;

/**
 * 
 * @author Srinivaas
 */
public class ProvinceQuery extends BaseQuery {

	private Long provinceID;
	private String displayText;
	
	public Long getProvinceID() {
		return provinceID;
	}
	public void setProvinceID(Long provinceID) {
		this.provinceID = provinceID;
	}
	public String getDisplayText() {
		return displayText;
	}
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
}
package com.mfino.dao.query;

/**
 * 
 * @author Srinivaas
 */
public class ProvinceRegionQuery extends BaseQuery {

	private Long idProvince;
	private String displayText;
	
	public Long getIdProvince() {
		return idProvince;
	}
	public void setIdProvince(Long idProvince) {
		this.idProvince = idProvince;
	}
	public String getDisplayText() {
		return displayText;
	}
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}	
}

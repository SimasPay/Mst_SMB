package com.mfino.dao.query;

/**
 * 
 * @author Srinivaas
 */
public class DistrictQuery extends BaseQuery {

	private Long idRegion;
	private String displayText;
	
	public Long getIdRegion() {
		return idRegion;
	}
	public void setIdRegion(Long idRegion) {
		this.idRegion = idRegion;
	}
	public String getDisplayText() {
		return displayText;
	}
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
}

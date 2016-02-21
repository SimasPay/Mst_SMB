package com.mfino.dao.query;

/**
 * 
 * @author Srinivaas
 */
public class VillageQuery extends BaseQuery {

	private Long idDistrict;
	private String displayText;
	
	public Long getIdDistrict() {
		return idDistrict;
	}
	public void setIdDistrict(Long idDistrict) {
		this.idDistrict = idDistrict;
	}
	public String getDisplayText() {
		return displayText;
	}
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
}
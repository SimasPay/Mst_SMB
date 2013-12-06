package com.mfino.dao.query;

/**
 * 
 * @author srikanth
 */
public class RoleQuery extends BaseQuery {

	private String enumCode;
	private String enumValue;
	private String displayText;
	private Boolean isSystemUser;
	private Integer priorityLevel;

	public String getEnumCode() {
		return enumCode;
	}

	public void setEnumCode(String enumCode) {
		this.enumCode = enumCode;
	}

	public String getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(String enumValue) {
		this.enumValue = enumValue;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public Boolean getIsSystemUser() {
		return isSystemUser;
	}

	public void setIsSystemUser(Boolean isSystemUser) {
		this.isSystemUser = isSystemUser;
	}

	public Integer getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(Integer priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

}

package com.mfino.dao.query;

/**
 * @author Sasi
 *
 */
public class GroupQuery extends BaseQuery{
	
	private String groupName;
	
	private boolean includeSystemGroups;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isIncludeSystemGroups() {
		return includeSystemGroups;
	}

	public void setIncludeSystemGroups(boolean includeSystemGroups) {
		this.includeSystemGroups = includeSystemGroups;
	}
}

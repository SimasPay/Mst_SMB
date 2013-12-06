package com.mfino.dao.query;

/**
 * @author Sasi
 *
 */
public class DCTRestrictionsQuery extends BaseQuery{
	
	private Long dctRestrictionsId;
	private Long dctId;
	private Long transactionTypeId;
	private Integer relationshipType;
	private Integer level;
	private Boolean isAllowed;
	
	public Long getDctRestrictionsId() {
		return dctRestrictionsId;
	}
	
	public void setDctRestrictionsId(Long dctRestrictionsId) {
		this.dctRestrictionsId = dctRestrictionsId;
	}
	
	public Long getDctId() {
		return dctId;
	}
	
	public void setDctId(Long dctId) {
		this.dctId = dctId;
	}
	
	public Long getTransactionTypeId() {
		return transactionTypeId;
	}
	
	public void setTransactionTypeId(Long transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public Integer getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(Integer relationshipType) {
		this.relationshipType = relationshipType;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
}

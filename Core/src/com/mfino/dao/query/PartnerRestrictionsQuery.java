package com.mfino.dao.query;


/**
 * @author Sasi
 *
 */
public class PartnerRestrictionsQuery extends BaseQuery{

	private Long partnerRestrictionsId;
	private Long dctId;
	private Long transactionTypeId;
	private Long partnerId;
	private Integer relationshipType;
	private Boolean isAllowed;
	
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
	
	public Long getPartnerId() {
		return partnerId;
	}
	
	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}

	public Long getPartnerRestrictionsId() {
		return partnerRestrictionsId;
	}

	public void setPartnerRestrictionsId(Long partnerRestrictionsId) {
		this.partnerRestrictionsId = partnerRestrictionsId;
	}

	public Integer getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(Integer relationshipType) {
		this.relationshipType = relationshipType;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
}

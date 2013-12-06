package com.mfino.dao.query;

public class InteractionAdditionalQuery extends BaseQuery {
	private Long	interactionSummaryID;
	private String	mappedKey;
	private String	mappedValue;
	public Long getInteractionSummaryID() {
		return interactionSummaryID;
	}
	public void setInteractionSummaryID(Long interactionSummaryID) {
		this.interactionSummaryID = interactionSummaryID;
	}
	public String getMappedKey() {
		return mappedKey;
	}
	public void setMappedKey(String mappedKey) {
		this.mappedKey = mappedKey;
	}
	public String getMappedValue() {
		return mappedValue;
	}
	public void setMappedValue(String mappedValue) {
		this.mappedValue = mappedValue;
	}
}

package com.mfino.dao.query;

public class FundDefinitionQuery extends BaseQuery {
	
	private String FACPrefix;
	private Integer FACLength;
	private Integer maxFailAttemptsAllowed;
	private Long onFundAllocationTimeExpiry;
	private Long onFailedAttemptsExceeded;
	private Long expiryId;
	private Long generationOfOTPOnFailure;
	private Long purposeID;
	private boolean isMultipleWithdrawalAllowed;
	public String getFACPrefix() {
		return FACPrefix;
	}
	public void setFACPrefix(String fACPrefix) {
		FACPrefix = fACPrefix;
	}
	public Integer getFACLength() {
		return FACLength;
	}
	public void setFACLength(Integer fACLength) {
		FACLength = fACLength;
	}
	public Integer getMaxFailAttemptsAllowed() {
		return maxFailAttemptsAllowed;
	}
	public void setMaxFailAttemptsAllowed(Integer maxFailAttemptsAllowed) {
		this.maxFailAttemptsAllowed = maxFailAttemptsAllowed;
	}
	public Long getOnFundAllocationTimeExpiry() {
		return onFundAllocationTimeExpiry;
	}
	public void setOnFundAllocationTimeExpiry(Long onFundAllocationTimeExpiry) {
		this.onFundAllocationTimeExpiry = onFundAllocationTimeExpiry;
	}
	public Long getOnFailedAttemptsExceeded() {
		return onFailedAttemptsExceeded;
	}
	public void setOnFailedAttemptsExceeded(Long onFailedAttemptsExceeded) {
		this.onFailedAttemptsExceeded = onFailedAttemptsExceeded;
	}
	public Long getExpiryId() {
		return expiryId;
	}
	public void setExpiryId(Long expiryId) {
		this.expiryId = expiryId;
	}
	public Long getGenerationOfOTPOnFailure() {
		return generationOfOTPOnFailure;
	}
	public void setGenerationOfOTPOnFailure(Long generationOfOTPOnFailure) {
		this.generationOfOTPOnFailure = generationOfOTPOnFailure;
	}
	public Long getPurposeID() {
		return purposeID;
	}
	public void setPurposeID(Long purposeID) {
		this.purposeID = purposeID;
	}
	public boolean isMultipleWithdrawalAllowed() {
		return isMultipleWithdrawalAllowed;
	}
	public void setMultipleWithdrawalAllowed(boolean isMultipleWithdrawalAllowed) {
		this.isMultipleWithdrawalAllowed = isMultipleWithdrawalAllowed;
	}
	
	
	
}

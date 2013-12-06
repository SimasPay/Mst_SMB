/**
 * 
 */
package com.mfino.cc.message;

import com.mfino.fix.CmFinoFIX;

/**
 * This class captures information required for Notify Servlet
 * 
 * @author Chaitanya
 *
 */
public class CCNotifyMessage {

	private Boolean isSmsRequired = false; 
	private Integer failedAt = null;

	private String transactionId = null;
	private String mdn = null;
	private String email = null;
	private String originalStatus = CmFinoFIX.TransStatus_Failed;
	/**
	 * @return the isSmsRequired
	 */
	public Boolean getIsSmsRequired() {
		return isSmsRequired;
	}
	/**
	 * @param isSmsRequired the isSmsRequired to set
	 */
	public void setIsSmsRequired(Boolean isSmsRequired) {
		this.isSmsRequired = isSmsRequired;
	}
	/**
	 * @return the failedAt
	 */
	public Integer getFailedAt() {
		return failedAt;
	}
	/**
	 * @param failedAt the failedAt to set
	 */
	public void setFailedAt(Integer failedAt) {
		this.failedAt = failedAt;
	}
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	/**
	 * @return the mdn
	 */
	public String getMdn() {
		return mdn;
	}
	/**
	 * @param mdn the mdn to set
	 */
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the originalStatus
	 */
	public String getOriginalStatus() {
		return originalStatus;
	}
	/**
	 * @param originalStatus the originalStatus to set
	 */
	public void setOriginalStatus(String originalStatus) {
		this.originalStatus = originalStatus;
	}

	
}

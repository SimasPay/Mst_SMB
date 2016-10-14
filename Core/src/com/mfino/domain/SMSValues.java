/**
 * 
 */
package com.mfino.domain;


/**
 * @author Bala Sunku
 *
 */
public class SMSValues {

	private String destinationMDN;
	private String message;
	private int notificationCode;
	private Long sctlId;
	private Long notificationLogDetailsID;
	private boolean isDuplicateSMS;
	private String transactionIdentifier;
	public String getDestinationMDN() {
		return destinationMDN;
	}
	public void setDestinationMDN(String destinationMDN) {
		this.destinationMDN = destinationMDN;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Long getSctlId() {
		return sctlId;
	}
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}
	public Long getNotificationLogDetailsID() {
		return notificationLogDetailsID;
	}
	public void setNotificationLogDetailsID(Long notificationLogDetailsID) {
		this.notificationLogDetailsID = notificationLogDetailsID;
	}
	public boolean isDuplicateSMS() {
		return isDuplicateSMS;
	}
	public void setDuplicateSMS(boolean isDuplicateSMS) {
		this.isDuplicateSMS = isDuplicateSMS;
	}
	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}
	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}
	public int getNotificationCode() {
		return notificationCode;
	}
	public void setNotificationCode(int notificationCode) {
		this.notificationCode = notificationCode;
	}
	
	
}

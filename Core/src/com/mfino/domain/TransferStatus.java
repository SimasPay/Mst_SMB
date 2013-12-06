package com.mfino.domain;

/**
 * @author sasidhar
 *
 */
public enum TransferStatus {
	
	INITIALIZED(0, "INITIALIZED"),
	VALIDATION_FAILED(2, "VALIDATION_FAILED"),
	TRANSFER_FAILED(5, "TRANSFER_FAILED"),
	TRANSFER_PENDING(6, "TRANSFER_PENDING"),
	PARTNER_SERVICE_NOT_ACTIVE(7, "PARTNER_SERVICE_NOT_ACTIVE"),
	COMPLETED(8, "COMPLETED");
	
	TransferStatus(Integer transferStatus, String displayString){
		this.transferStatus = transferStatus;
		this.displayString = displayString;
	}
	
	private Integer transferStatus;
	private String displayString;
	
	public Integer getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(Integer transferStatus) {
		this.transferStatus = transferStatus;
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	@Override
	public String toString() {
		return getDisplayString();
	}
}

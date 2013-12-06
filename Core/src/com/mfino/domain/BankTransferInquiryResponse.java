package com.mfino.domain;

/**
 * 
 * @author sasidhar
 *
 */
public class BankTransferInquiryResponse extends FIXResponse {
	
	private String transferId;
	private String parentTransactionId;
	
	@Override
	public FIXResponseType getResponseType() {
		return FIXResponseType.BANK_TRANSFER_INQUIRY;
	}

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public String getParentTransactionId() {
		return parentTransactionId;
	}

	public void setParentTransactionId(String parentTransactionId) {
		this.parentTransactionId = parentTransactionId;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", trannsferId="+transferId + ", parentTransactionId="+parentTransactionId;
	}
}

package com.mfino.iframework;

public class TransactionDataHolder {

	private String	TransactionID;
	private String	ChannelID;
	private String	BillerID;
	private String	BillerCategory;
	private String	MDN;
	private String	Amount;
	private Long sctlID;

	public Long getSctlID() {
		return sctlID;
	}

	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}

	public String getBillerCategory() {
		return BillerCategory;
	}

	public void setBillerCategory(String billerCategory) {
		BillerCategory = billerCategory;
	}

	public String getBillerID() {
		return BillerID;
	}

	public void setBillerID(String billerID) {
		BillerID = billerID;
	}

	public String getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	public String getChannelID() {
		return ChannelID;
	}

	public void setChannelID(String channelID) {
		ChannelID = channelID;
	}

	public String getMDN() {
		return MDN;
	}

	public void setMDN(String mDN) {
		MDN = mDN;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

}

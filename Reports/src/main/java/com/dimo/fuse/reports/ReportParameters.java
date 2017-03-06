package com.dimo.fuse.reports;

import java.util.Date;

/**
 * 
 * @author Amar
 *
 */
public class ReportParameters {
	
	private Date startTime;
	
	private Date endTime;
	
	private Date fromUpdatedDate;
	
	private Date toUpdatedDate;
	
	private boolean isScheduledReport;
	

	private String subscriberStatusId;
	
	private String subscriberStatusText;
	
	private String pocketTemplateId;
	
	private String pocketTemplateDescription;
	
	private String subscriberRestrictions;
	
	private String subscriberRestrictionsText;
	
	private String sourceMdn;
	
	private String transactionTypeId;
	
	private String transactionTypeText;
	
	private String transactionStatusId;
	
	private String transactionStatusText;
	
	private String destinationPocketStatusId;
	
	private String destinationPocketStatusText;
	
	private String destMdn;
	
	private String partnerCode;
	
	private String billerCode;
	
	private String partnerTypeId;
	
	private String partnerTypeText;
	
	private String settlementStatusId;
	
	private String settlementStatusText;
	
	private String csrUserName;
	
	private String idNumber;
	
	private String mdn;
	
	private String merchantId;
	
	private String merchantAccount;
	
	private String referenceNo;
	
	private String userName;
	
	private String destinationFolder;
	
	private String sourcePartnerCode;
	
	private String destPartnerCode;
	
	private String channelName;
	
	private String bankRRN;
	
	private String email;
	
	private String sourcePocketType;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}
	
	public Date getFromUpdatedDate() {
		return fromUpdatedDate;
	}

	public void setFromUpdatedDate(Date fromUpdatedDate) {
		this.fromUpdatedDate = fromUpdatedDate;
	}

	public Date getToUpdatedDate() {
		return toUpdatedDate;
	}

	public void setToUpdatedDate(Date toUpdatedDate) {
		this.toUpdatedDate = toUpdatedDate;
	}

	public String getPocketTemplateId() {
		return pocketTemplateId;
	}

	public void setPocketTemplateId(String pocketTemplateId) {
		this.pocketTemplateId = pocketTemplateId;
	}

	public String getSubscriberRestrictions() {
		return subscriberRestrictions;
	}

	public void setSubscriberRestrictions(String subscriberRestrictions) {
		this.subscriberRestrictions = subscriberRestrictions;
	}

	public String getSourceMdn() {
		return sourceMdn;
	}

	public void setSourceMdn(String sourceMdn) {
		this.sourceMdn = sourceMdn;
	}

	public String getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(String transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public String getDestMdn() {
		return destMdn;
	}

	public void setDestMdn(String destMdn) {
		this.destMdn = destMdn;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getBillerCode() {
		return billerCode;
	}

	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}

	public String getPartnerTypeText() {
		return partnerTypeText;
	}

	public void setPartnerTypeText(String partnerTypeText) {
		this.partnerTypeText = partnerTypeText;
	}

	public String getCsrUserName() {
		return csrUserName;
	}

	public void setCsrUserName(String csrUserName) {
		this.csrUserName = csrUserName;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantAccount() {
		return merchantAccount;
	}

	public void setMerchantAccount(String merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	
	public String getSubscriberStatusId() {
		return subscriberStatusId;
	}

	public void setSubscriberStatusId(String subscriberStatusId) {
		this.subscriberStatusId = subscriberStatusId;
	}

	public String getSubscriberStatusText() {
		return subscriberStatusText;
	}

	public void setSubscriberStatusText(String subscriberStatusText) {
		this.subscriberStatusText = subscriberStatusText;
	}

	public String getPocketTemplateDescription() {
		return pocketTemplateDescription;
	}

	public void setPocketTemplateDescription(String pocketTemplateDescription) {
		this.pocketTemplateDescription = pocketTemplateDescription;
	}

	public String getSubscriberRestrictionsText() {
		return subscriberRestrictionsText;
	}

	public void setSubscriberRestrictionsText(String subscriberRestrictionsText) {
		this.subscriberRestrictionsText = subscriberRestrictionsText;
	}

	public String getTransactionTypeText() {
		return transactionTypeText;
	}

	public void setTransactionTypeText(String transactionTypeText) {
		this.transactionTypeText = transactionTypeText;
	}

	public String getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(String transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public String getTransactionStatusText() {
		return transactionStatusText;
	}

	public void setTransactionStatusText(String transactionStatusText) {
		this.transactionStatusText = transactionStatusText;
	}

	public String getDestinationPocketStatusId() {
		return destinationPocketStatusId;
	}

	public void setDestinationPocketStatusId(String destinationPocketStatusId) {
		this.destinationPocketStatusId = destinationPocketStatusId;
	}

	public String getDestinationPocketStatusText() {
		return destinationPocketStatusText;
	}

	public void setDestinationPocketStatusText(String destinationPocketStatusText) {
		this.destinationPocketStatusText = destinationPocketStatusText;
	}

	public String getPartnerTypeId() {
		return partnerTypeId;
	}

	public void setPartnerTypeId(String partnerTypeId) {
		this.partnerTypeId = partnerTypeId;
	}

	public String getSettlementStatusId() {
		return settlementStatusId;
	}

	public void setSettlementStatusId(String settlementStatusId) {
		this.settlementStatusId = settlementStatusId;
	}

	public String getSettlementStatusText() {
		return settlementStatusText;
	}

	public void setSettlementStatusText(String settlementStatusText) {
		this.settlementStatusText = settlementStatusText;
	}

	public boolean isScheduledReport() {
		return isScheduledReport;
	}

	public void setScheduledReport(boolean isScheduledReport) {
		this.isScheduledReport = isScheduledReport;
	}
	public String getSourcePartnerCode() {
		return sourcePartnerCode;
	}

	public void setSourcePartnerCode(String sourcePartnerCode) {
		this.sourcePartnerCode = sourcePartnerCode;
	}

	public String getDestPartnerCode() {
		return destPartnerCode;
	}

	public void setDestPartnerCode(String destPartnerCode) {
		this.destPartnerCode = destPartnerCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getBankRRN() {
		return bankRRN;
	}

	public void setBankRRN(String bankRRN) {
		this.bankRRN = bankRRN;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSourcePocketType() {
		return sourcePocketType;
	}

	public void setSourcePocketType(String sourcePocketType) {
		this.sourcePocketType = sourcePocketType;
	}
	
}

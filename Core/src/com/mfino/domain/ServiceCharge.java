/**
 * 
 */
package com.mfino.domain;

import java.math.BigDecimal;

/**
 * @author Bala Sunku
 *
 */
public class ServiceCharge {

	private String serviceProviderName;
	private String serviceName;
	private String transactionTypeName;
	private long channelCodeId;
	private String sourceMDN;
	private String destMDN;
	private long transactionLogId;
	private BigDecimal transactionAmount;
	private String mfsBillerCode;
	private String invoiceNo;
	private String onBeHalfOfMDN;
	private boolean isReverseTransaction;
	private long parentSctlId;
	private Long sctlId;
	private Long integrationTxnID;
	private String transactionIdentifier;
	private String description;
	private String info1;
	private String info2;
	private String info3;
	private String info4;
	
	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}
	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}
	
	public String getServiceProviderName() {
		return serviceProviderName;
	}
	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getTransactionTypeName() {
		return transactionTypeName;
	}
	public void setTransactionTypeName(String transactionTypeName) {
		this.transactionTypeName = transactionTypeName;
	}
	public long getChannelCodeId() {
		return channelCodeId;
	}
	public void setChannelCodeId(long channelCodeId) {
		this.channelCodeId = channelCodeId;
	}
	public String getSourceMDN() {
		return sourceMDN;
	}
	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}
	public String getDestMDN() {
		return destMDN;
	}
	public void setDestMDN(String destMDN) {
		this.destMDN = destMDN;
	}
	public long getTransactionLogId() {
		return transactionLogId;
	}
	public void setTransactionLogId(long transactionLogId) {
		this.transactionLogId = transactionLogId;
	}
	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getMfsBillerCode() {
		return mfsBillerCode;
	}
	public void setMfsBillerCode(String mfsBillerCode) {
		this.mfsBillerCode = mfsBillerCode;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getOnBeHalfOfMDN() {
		return onBeHalfOfMDN;
	}
	public void setOnBeHalfOfMDN(String onBeHalfOfMDN) {
		this.onBeHalfOfMDN = onBeHalfOfMDN;
	}
	public boolean isReverseTransaction() {
		return isReverseTransaction;
	}
	public void setReverseTransaction(boolean isReverseTransaction) {
		this.isReverseTransaction = isReverseTransaction;
	}
	public long getParentSctlId() {
		return parentSctlId;
	}
	public void setParentSctlId(long parentSctlId) {
		this.parentSctlId = parentSctlId;
	}
	public Long getSctlId() {
		return sctlId;
	}
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}
	public Long getIntegrationTxnID() {
	    return integrationTxnID;
    }
	public void setIntegrationTxnID(Long integrationTxnID) {
	    this.integrationTxnID = integrationTxnID;
    }
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the info1
	 */
	public String getInfo1() {
		return info1;
	}
	/**
	 * @param info1 the info1 to set
	 */
	public void setInfo1(String info1) {
		this.info1 = info1;
	}
	/**
	 * @return the info2
	 */
	public String getInfo2() {
		return info2;
	}
	/**
	 * @param info2 the info2 to set
	 */
	public void setInfo2(String info2) {
		this.info2 = info2;
	}
	/**
	 * @return the info3
	 */
	public String getInfo3() {
		return info3;
	}
	/**
	 * @param info3 the info3 to set
	 */
	public void setInfo3(String info3) {
		this.info3 = info3;
	}
	/**
	 * @return the info4
	 */
	public String getInfo4() {
		return info4;
	}
	/**
	 * @param info4 the info4 to set
	 */
	public void setInfo4(String info4) {
		this.info4 = info4;
	}
	
}

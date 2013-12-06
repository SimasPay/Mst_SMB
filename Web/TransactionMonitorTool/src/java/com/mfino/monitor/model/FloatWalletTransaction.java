package com.mfino.monitor.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Srikanth
 * 
 */

public class FloatWalletTransaction {
	private Long ID;
	private Long serviceChargeTransactionLogID;
	private Long transactionID;
	private Date transactionTime;
	private String transactionName;
	private String sourceMDN;
	private String destMDN;
	private Long sourcePocketID;
	private Long destPocketID;
	private Long sourceDestnPocketID;
	private BigDecimal creditAmount;
	private BigDecimal debitAmount;
	private BigDecimal destPocketBalance;
	private BigDecimal sourcePocketBalance;
	private BigDecimal sourcePocketClosingBalance;
	private BigDecimal destPocketClosingBalance;
	private String transferStatusText;
	private String commodityText;
	private String accessMethodText;
	private Date createTimeGE;
	private Date createTimeLT;
	private Integer start;
	private Integer limit;
	private Integer total;
	private String transType;

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public Long getServiceChargeTransactionLogID() {
		return serviceChargeTransactionLogID;
	}

	public void setServiceChargeTransactionLogID(
			Long serviceChargeTransactionLogID) {
		this.serviceChargeTransactionLogID = serviceChargeTransactionLogID;
	}

	public Long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(Long transactionID) {
		this.transactionID = transactionID;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
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

	public Long getSourcePocketID() {
		return sourcePocketID;
	}

	public void setSourcePocketID(Long sourcePocketID) {
		this.sourcePocketID = sourcePocketID;
	}

	public Long getDestPocketID() {
		return destPocketID;
	}

	public void setDestPocketID(Long destPocketID) {
		this.destPocketID = destPocketID;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public BigDecimal getDestPocketBalance() {
		return destPocketBalance;
	}

	public void setDestPocketBalance(BigDecimal destPocketBalance) {
		this.destPocketBalance = destPocketBalance;
	}

	public BigDecimal getSourcePocketClosingBalance() {
		return sourcePocketClosingBalance;
	}

	public void setSourcePocketClosingBalance(
			BigDecimal sourcePocketClosingBalance) {
		this.sourcePocketClosingBalance = sourcePocketClosingBalance;
	}

	public BigDecimal getDestPocketClosingBalance() {
		return destPocketClosingBalance;
	}

	public void setDestPocketClosingBalance(BigDecimal destPocketClosingBalance) {
		this.destPocketClosingBalance = destPocketClosingBalance;
	}

	public String getTransferStatusText() {
		return transferStatusText;
	}

	public void setTransferStatusText(String transferStatusText) {
		this.transferStatusText = transferStatusText;
	}

	public String getCommodityText() {
		return commodityText;
	}

	public void setCommodityText(String commodityText) {
		this.commodityText = commodityText;
	}

	public String getAccessMethodText() {
		return accessMethodText;
	}

	public void setAccessMethodText(String accessMethodText) {
		this.accessMethodText = accessMethodText;
	}

	public Date getCreateTimeGE() {
		return createTimeGE;
	}

	public void setCreateTimeGE(Date createTimeGE) {
		this.createTimeGE = createTimeGE;
	}

	public Date getCreateTimeLT() {
		return createTimeLT;
	}

	public void setCreateTimeLT(Date createTimeLT) {
		this.createTimeLT = createTimeLT;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Long getSourceDestnPocketID() {
		return sourceDestnPocketID;
	}

	public void setSourceDestnPocketID(Long sourceDestnPocketID) {
		this.sourceDestnPocketID = sourceDestnPocketID;
	}

	public BigDecimal getSourcePocketBalance() {
		return sourcePocketBalance;
	}

	public void setSourcePocketBalance(BigDecimal sourcePocketBalance) {
		this.sourcePocketBalance = sourcePocketBalance;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}
}

package com.mfino.cc.message;

import java.math.BigDecimal;

public class CCPaymentOutput {

    private String mdn;
    private String paymentMethod;
    private String errorCode;
    private String usrCode;
    private String txnStatus;
    private String description;
    private String currencyCode;
    private String EUI;
    private BigDecimal amount;
    private Long transactionID;
    private Long merchantTransactionID;
    private String transDate;
    private String transType;
    private String isBlackListed;
    private Integer fraudRiskLevel;
    private BigDecimal fraudRisksCore;
    private String exceedHighRisk;
    private String cardType;
    private String cardNoPartial;
    private String cardName;
    private String acquirerBank;
    private String bankResCode;
    private String bankResMsg;
    private String bankreference;
    private String whiteListCard;
    private String authId;
    private String operation;
    private String billReferenceNumber;
    private String sourceMdn;

    public Long getPocketid() {
        return pocketid;
    }

    public void setPocketid(Long pocketid) {
        this.pocketid = pocketid;
    }

    public String getSourceMdn() {
        return sourceMdn;
    }

    public void setSourceMdn(String sourceMdn) {
        this.sourceMdn = sourceMdn;
    }
    private Long pocketid;

    public String getBillReferenceNumber() {
        return billReferenceNumber;
    }
    public void setBillReferenceNumber(String billReferenceNumber) {
        this.billReferenceNumber = billReferenceNumber;
    }
    public Long getMerchantTransactionID() {
        return merchantTransactionID;
    }

    public void setMerchantTransactionID(Long merchanttransactionID) {
        this.merchantTransactionID = merchanttransactionID;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getUsrCode() {
        return usrCode;
    }

    public void setUsrCode(String usrCode) {
        this.usrCode = usrCode;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getEUI() {
        return EUI;
    }

    public void setEUI(String EUI) {
        this.EUI = EUI;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getIsBlackListed() {
        return isBlackListed;
    }

    public void setIsBlackListed(String isBlackListed) {
        this.isBlackListed = isBlackListed;
    }

    public Integer getFraudRiskLevel() {
        return fraudRiskLevel;
    }

    public void setFraudRiskLevel(Integer fraudRiskLevel) {
        this.fraudRiskLevel = fraudRiskLevel;
    }

    public BigDecimal getFraudRisksCore() {
        return fraudRisksCore;
    }

    public void setFraudRisksCore(BigDecimal fraudRisksCore) {
        this.fraudRisksCore = fraudRisksCore;
    }

    public String getExceedHighRisk() {
        return exceedHighRisk;
    }

    public void setExceedHighRisk(String exceedHighRisk) {
        this.exceedHighRisk = exceedHighRisk;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNoPartial() {
        return cardNoPartial;
    }

    public void setCardNoPartial(String cardNoPartial) {
        this.cardNoPartial = cardNoPartial;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getAcquirerBank() {
        return acquirerBank;
    }

    public void setAcquirerBank(String acquirerBank) {
        this.acquirerBank = acquirerBank;
    }

    public String getBankResCode() {
        return bankResCode;
    }

    public void setBankResCode(String bankResCode) {
        this.bankResCode = bankResCode;
    }

    public String getBankResMsg() {
        return bankResMsg;
    }

    public void setBankResMsg(String bankResMsg) {
        this.bankResMsg = bankResMsg;
    }

    public String getBankreference() {
        return bankreference;
    }

    public void setBankreference(String bankreference) {
        this.bankreference = bankreference;
    }

    public String getWhiteListCard() {
        return whiteListCard;
    }

    public void setWhiteListCard(String whiteListCard) {
        this.whiteListCard = whiteListCard;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

	@Override
	public String toString() {
		return "CCPaymentOutput [mdn=" + mdn + ", paymentMethod="
				+ paymentMethod + ", errorCode=" + errorCode + ", usrCode="
				+ usrCode + ", txnStatus=" + txnStatus + ", description="
				+ description + ", currencyCode=" + currencyCode + ", EUI="
				+ EUI + ", amount=" + amount + ", transactionID="
				+ transactionID + ", merchanttransactionID="
				+ merchantTransactionID + ", transDate=" + transDate
				+ ", transType=" + transType + ", isBlackListed="
				+ isBlackListed + ", fraudRiskLevel=" + fraudRiskLevel
				+ ", fraudRisksCore=" + fraudRisksCore + ", exceedHighRisk="
				+ exceedHighRisk + ", cardType=" + cardType
				+ ", cardNoPartial=" + cardNoPartial + ", cardName=" + cardName
				+ ", acquirerBank=" + acquirerBank + ", bankResCode="
				+ bankResCode + ", bankResMsg=" + bankResMsg
				+ ", bankreference=" + bankreference + ", whiteListCard="
				+ whiteListCard + ", authId=" + authId + ", operation="
				+ operation + ", billReferenceNumber=" + billReferenceNumber
				+ ", sourceMdn=" + sourceMdn + ", pocketid=" + pocketid + "]";
	}
}

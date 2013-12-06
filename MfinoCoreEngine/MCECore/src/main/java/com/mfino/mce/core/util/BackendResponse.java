package com.mfino.mce.core.util;

import java.math.BigDecimal;
import java.util.TimeZone;

import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;

/**
 * @author sasidhar
 *
 */
public class BackendResponse extends CMBase {

	private String externalResponseCode;
	private Integer internalErrorCode;
	private String description;
	private Long transferID;
	private String isSuccess;
	private BigDecimal amount;
	private Integer language;
	private String senderMDN;
	private String receiverMDN;
	private Integer result;
	private String receiverAccountNo;
	private String receiverName;
	private String currency;
	private String customerServiceShortCode; 
	private String bankName;
	private BigDecimal charges;
	private int numberOfTrailsLeft;
	private BigDecimal sourceMDNBalance;
	private BigDecimal destinationMDNBalance;
	private CMGetLastTransactionsFromBank.CGEntries[] bankHistoryCGEntries;
	private CMBalanceInquiryFromBank.CGEntries[] nfcCardBalanceCGEntries;
	private String partnerCode;
	private String billerCode;
	private String invoiceNumber;
	private String rechargeMdn;
	private Long originalReferenceID;
	private String oneTimePin;
	private String destBankAccountNumber;
	private String srcBankAccountNumber;
	private String onBehalfOfMDN;
	private String destinationType;
	private String firstName;
	private String lastName;
	private BigDecimal maxTransactionLimit;
	private BigDecimal minTransactionLimit;
	private String agentName;
	private Long sourcePocketId;
	private Long destPocketId;
	private TimeZone timeZone;
	private String SenderFirstName;
	private String SenderLastName;
	private String ReceiverFirstName;
	private String ReceiverLastName;
	private String SenderTradeName;
	private String ReceiverTradeName;
	private String beneficiaryName;
	private String SourceCardPAN;
	private String AdditionalInfo;
	private String DestinationUserName;
	private String rechargePin;
	@Override
	public boolean checkRequiredFields() {
		return true;
	}
	
	public void copy(BackendResponse inObject)
	{
		super.copy(inObject);

		this.externalResponseCode = inObject.externalResponseCode;
		this.internalErrorCode = inObject.internalErrorCode;
		this.description = inObject.description;
		this.transferID = inObject.transferID;
		this.isSuccess = inObject.isSuccess;
		this.amount = inObject.amount;
		this.language = inObject.language;
		this.senderMDN = inObject.senderMDN;
		this.receiverMDN = inObject.receiverMDN;
		this.result = inObject.result;
		this.receiverAccountNo = inObject.receiverAccountNo;
		this.receiverName = inObject.receiverName;
		this.currency = inObject.currency;
		this.customerServiceShortCode = inObject.customerServiceShortCode;
		this.bankName = inObject.bankName;
		this.charges = inObject.charges;
		this.numberOfTrailsLeft = inObject.numberOfTrailsLeft;
		this.sourceMDNBalance = inObject.sourceMDNBalance;
		this.destinationMDNBalance = inObject.destinationMDNBalance;
		this.bankHistoryCGEntries = inObject.bankHistoryCGEntries;
		this.partnerCode = inObject.partnerCode;
		this.billerCode = inObject.billerCode;
		this.invoiceNumber = inObject.invoiceNumber;
		this.rechargeMdn = inObject.rechargeMdn;
		this.firstName = inObject.firstName;
		this.lastName = inObject.lastName;
	}
	
	public String getExternalResponseCode() {
		return externalResponseCode;
	}
	
	public void setExternalResponseCode(String externalResponseCode) {
		this.externalResponseCode = externalResponseCode;
	}
	
	public Integer getInternalErrorCode() {
		return internalErrorCode;
	}
	
	public void setInternalErrorCode(Integer internalErrorCode) {
		this.internalErrorCode = internalErrorCode;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getTransferID() {
		return transferID;
	}
	
	public void setTransferID(Long transferID) {
		this.transferID = transferID;
	}
	
	public String getIsSuccess() {
		return isSuccess;
	}
	
	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public Integer getLanguage() {
		return language;
	}
	
	public void setLanguage(Integer language) {
		this.language = language;
	}
	
	public String getSenderMDN() {
		return senderMDN;
	}
	
	public void setSenderMDN(String senderMDN) {
		this.senderMDN = senderMDN;
	}
	
	public String getReceiverMDN() {
		return receiverMDN;
	}
	
	public void setReceiverMDN(String receiverMDN) {
		this.receiverMDN = receiverMDN;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}
	
	public String getReceiverAccountNo() {
		return receiverAccountNo;
	}

	public void setReceiverAccountNo(String receiverAccountNo) {
		this.receiverAccountNo = receiverAccountNo;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getCustomerServiceShortCode() {
		return customerServiceShortCode;
	}

	public void setCustomerServiceShortCode(String customerServiceShortCode) {
		this.customerServiceShortCode = customerServiceShortCode;
	}
	
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public BigDecimal getCharges() {
		return charges;
	}

	public void setCharges(BigDecimal charges) {
		this.charges = charges;
	}

	@Override
	public String DumpFields() {
		StringBuffer fields =  new StringBuffer(super.DumpFields());
		
		fields.append("getExternalResponseCode="+getExternalResponseCode()+ "\n" );
		fields.append("getInternalErrorCode="+getInternalErrorCode()+ "\n" );
		fields.append("getDescription="+getDescription()+ "\n" );
		fields.append("getTransferID="+getTransferID()+ "\n" );
		fields.append("getIsSuccess="+getIsSuccess()+ "\n" );
//		fields.append("ExternalResponseCode="+getAmount()+ "\n" );
		fields.append("getLanguage="+getLanguage()+ "\n" );
		fields.append("getSenderMDN="+getSenderMDN()+ "\n" );
		fields.append("getSourceMDN="+getSourceMDN()+ "\n" );
		fields.append("getReceiverMDN="+getReceiverMDN()+ "\n" );
		fields.append("getResult="+getResult());
		fields.append("getReceiverAccountNo="+getReceiverAccountNo()+ "\n" );
		fields.append("getReceiverName="+getReceiverName()+ "\n" );
		fields.append("getCurrency="+getCurrency()+ "\n" );
		
		return fields.toString();
	}

	public int getNumberOfTrailsLeft() {
		return numberOfTrailsLeft;
	}

	public void setNumberOfTrailsLeft(int numberOfTrailsLeft) {
		this.numberOfTrailsLeft = numberOfTrailsLeft;
	}

	public BigDecimal getSourceMDNBalance() {
		return sourceMDNBalance;
	}

	public void setSourceMDNBalance(BigDecimal sourceMDNBalance) {
		this.sourceMDNBalance = sourceMDNBalance;
	}

	public BigDecimal getDestinationMDNBalance() {
		return destinationMDNBalance;
	}

	public void setDestinationMDNBalance(BigDecimal destinationMDNBalance) {
		this.destinationMDNBalance = destinationMDNBalance;
	}

	public CMGetLastTransactionsFromBank.CGEntries[] getBankHistoryCGEntries() {
	    return bankHistoryCGEntries;
    }

	public void setBankHistoryCGEntries(CMGetLastTransactionsFromBank.CGEntries[] bankHistoryCGEntries) {
	    this.bankHistoryCGEntries = bankHistoryCGEntries;
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

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getRechargeMdn() {
		return rechargeMdn;
	}

	public void setRechargeMdn(String rechargeMdn) {
		this.rechargeMdn = rechargeMdn;
	}

	public Long getOriginalReferenceID() {
		return originalReferenceID;
	}

	public void setOriginalReferenceID(Long originalReferenceID) {
		this.originalReferenceID = originalReferenceID;
	}
	
	public String getDestBankAccountNumber() {
		return destBankAccountNumber;
	}

	public void setDestBankAccountNumber(String bankAccountNumber) {
		this.destBankAccountNumber = bankAccountNumber;
	}

	public String getSourceBankAccountNumber() {
		return srcBankAccountNumber;
	}

	public void setSourceBankAccountNumber(String bankAccountNumber) {
		this.srcBankAccountNumber = bankAccountNumber;
	}
	
	/**
	 * @param oneTimePin the oneTimePin to set
	 */
	public void setOneTimePin(String oneTimePin) {
		this.oneTimePin = oneTimePin;
	}

	/**
	 * @return the oneTimePin
	 */
	public String getOneTimePin() {
		return oneTimePin;
	}

	public String getOnBehalfOfMDN() {
	    return onBehalfOfMDN;
    }

	public void setOnBehalfOfMDN(String onBehalfOfMDN) {
	    this.onBehalfOfMDN = onBehalfOfMDN;
    }

	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public BigDecimal getMaxTransactionLimit() {
		return maxTransactionLimit;
	}

	public void setMaxTransactionLimit(BigDecimal maxTransactionLimit) {
		this.maxTransactionLimit = maxTransactionLimit;
	}

	public BigDecimal getMinTransactionLimit() {
		return minTransactionLimit;
	}

	public void setMinTransactionLimit(BigDecimal minTransactionLimit) {
		this.minTransactionLimit = minTransactionLimit;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public Long getSourcePocketId() {
	    return sourcePocketId;
    }

	public void setSourcePocketId(Long sourcePocketId) {
	    this.sourcePocketId = sourcePocketId;
    }

	public Long getDestPocketId() {
	    return destPocketId;
    }

	public void setDestPocketId(Long destPocketId) {
	    this.destPocketId = destPocketId;
    }

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getSenderFirstName() {
		return SenderFirstName;
	}

	public void setSenderFirstName(String SenderFirstName) {
		this.SenderFirstName = SenderFirstName;
	}
	public String getSenderLastName() {
		return SenderLastName;
	}

	public void setSenderLastName(String SenderLastName) {
		this.SenderLastName = SenderLastName;
	}
	public String getReceiverFirstName() {
		return ReceiverFirstName;
	}

	public void setReceiverFirstName(String ReceiverFirstName) {
		this.ReceiverFirstName = ReceiverFirstName;
	}
	public String getReceiverLastName() {
		return ReceiverLastName;
	}

	public void setReceiverLastName(String ReceiverLastName) {
		this.ReceiverLastName = ReceiverLastName;
	}
	public String getSenderTradeName() {
		return SenderTradeName;
	}

	public void setSenderTradeName(String SenderTradeName) {
		this.SenderTradeName = SenderTradeName;
	}
	public String getReceiverTradeName() {
		return ReceiverTradeName;
	}

	public void setReceiverTradeName(String ReceiverTradeName) {
		this.ReceiverTradeName = ReceiverTradeName;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getSourceCardPAN() {
		return SourceCardPAN;
	}
		 
	public void setSourceCardPAN(String SourceCardPAN) {
		this.SourceCardPAN = SourceCardPAN;
	}

	public String getAdditionalInfo() {
		return AdditionalInfo;
	}
		 
	public void setAdditionalInfo(String AdditionalInfo) {
		this.AdditionalInfo = AdditionalInfo;
	}

	public String getDestinationUserName() {
		return DestinationUserName;
	}
		 
	public void setDestinationUserName(String DestinationUserName) {
		this.DestinationUserName = DestinationUserName;
	}

	public String getRechargePin() {
		return rechargePin;
	}

	public void setRechargePin(String rechargePin) {
		this.rechargePin = rechargePin;
	}

	public CMBalanceInquiryFromBank.CGEntries[] getNfcCardBalanceCGEntries() {
		return nfcCardBalanceCGEntries;
	}

	public void setNfcCardBalanceCGEntries(CMBalanceInquiryFromBank.CGEntries[] nfcCardBalanceCGEntries) {
		this.nfcCardBalanceCGEntries = nfcCardBalanceCGEntries;
	}
}

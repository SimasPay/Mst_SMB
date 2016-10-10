/**
 * 
 */
package com.mfino.mailer;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.mfino.domain.Bank;
import com.mfino.domain.BulkLOP;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.DistributionChainLevel;
import com.mfino.domain.LOP;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.SctlSettlementMap;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;

/**
 * @author Deva
 *
 */
public class NotificationWrapper extends CmFinoFIX.CRNotification{
	
	private static final long serialVersionUID = 1L;
	private Pocket sourcePocket;	
	private Pocket destPocket;	
	private CommodityTransfer commodityTransfer;	
	private SctlSettlementMap pendingSettlement;	
	private ServiceChargeTxnLog sctl;	
	private Company company;	
	private Long transactionId;
    private String emailId;
    private String destMDN;
    private String emailSubject;
    private DistributionChainLevel dcl;
    private LOP lop;
    private BulkLOP bulkLop;
    private String username;
    private String confirmationCode;    
    private CMGetLastTransactionsFromBank.CGEntries lastBankTrxnEntry;    
    private CMBalanceInquiryFromBank.CGEntries lastNFCCheckBalanceEntry;        
    private String PocketDescription;    
    private String bankName;    
    private BigDecimal billAmount;    
    private Long billPaymentID;
    private Bank bank;    
    private BigDecimal serviceCharge;    
    private BigDecimal transactionAmount;    
    private String oneTimePin;    
    private String KycLevel;    
    private String partnerCode;    
    private Integer numberOfTriesLeft;    
    private String sourceMDN;    
    private String otherMDN;    
    private String service;    
    private String appURL;    
    private Long sctlID;    
    private Long OriginalTransferID;    
    private Long bulkTransferId;    
    private BigDecimal minAmount;    
    private BigDecimal maxAmount;    
    private String currency;    
    private String CustomerServiceShortCode;    
    private BigDecimal amount;    
    private String validDenominations;    
    private String authenticationKey;    
    private String integrationName;    
    private String institutionID;    
    private String ipAddress;    
    private String firstName;    
    private String lastName;    
    private String receiverAccountName;    
    private String invoiceNumber;    
	private String receiverMDN;	
	private String senderMDN;    
    private String agentName;    
    private String tradeName;    
    private String otpExpirationTime;   
    private String transID;    
    private String parentTransID;    
    private String cardPan;    
    private String maxFavoriteCount;    
    private String favoriteLabel;    
    private String favoriteValue;    
    private String subscriberStatus;    
    private String cardAlias;    
    private String oldCardAlias;    
    private String nickName;    
    private String remainingBlockTimeMinutes;    
    private String remainingBlockTimeHours;    
    private String subscriberPin;    
    private Integer maxTrails;    
    private BigDecimal multiplesOff;    
    private BigDecimal multiplesIn;    
    private String contactCenterNo;    
    private String voucherCode;
    
	/**
	 * @return the multiplesIn
	 */
	public BigDecimal getMultiplesIn() {
		return multiplesIn;
	}

	/**
	 * @param multiplesIn the multiplesIn to set
	 */
	public void setMultiplesIn(BigDecimal multiplesIn) {
		this.multiplesIn = multiplesIn;
	}

	public String getValidDenominations() {
		
		return validDenominations;
	}

	public void setValidDenominations(String validDenominations) {
		this.validDenominations = validDenominations;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public BigDecimal getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(BigDecimal serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public void setPocketType(String pocketType)
    {
	    PocketDescription = pocketType;
    }

	public String getPocketDescription()
    {
	    return PocketDescription;
    }
    

	/**
	 * @param notification
	 */
	public NotificationWrapper(Notification notification) {
		this.setAccessCode(notification.getAccesscode());
		this.setCode((int)notification.getCode());
		this.setCodeName(notification.getCodename());
		this.setCompany(notification.getCompany());
		this.setLanguage((int)notification.getLanguage());
		this.setMfinoServiceProviderByMSPID(notification.getMfinoServiceProvider());
		this.setNotificationMethod((int)notification.getNotificationmethod());
		
		try {
			this.setText(notification.getText().getSubString(0, ((Long)notification.getText().length()).intValue()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public NotificationWrapper() {
		// TODO Auto-generated constructor stub
	}

        public String getConfirmationCode() {
            return confirmationCode;
        }

        public void setConfirmationCode(String confirmationCode) {
            this.confirmationCode = confirmationCode;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
	/**
	 * @return the LOP
	 */
	public LOP getLOP() {
		return lop;
	}

	/**
	 * @param lop the lop to set
	 */
	public void setLOP(LOP lop) {
		this.lop = lop;
	}

	/**
	 * @return the BulkLOP
	 */
	public BulkLOP getBulkLOP() {
		return bulkLop;
	}

	/**
	 * @param bulkLop the bulkLop to set
	 */
	public void setBulkLOP(BulkLOP bulkLop) {
		this.bulkLop = bulkLop;
	}

	/**
	 * @return the DistributionChainLevel
	 */
	public DistributionChainLevel getDistributionChainLevel() {
		return dcl;
	}

	/**
	 * @param DistributionChainLevel the DistributionChainLevel to set
	 */
	public void setDistributionChainLevel(DistributionChainLevel dcl) {
		this.dcl = dcl;
	}


	/**
	 * @return the destination mdn
	 */
	public String getDestMDN() {
		return destMDN;
	}

	/**
	 * @param destination mdn the destination mdn to set
	 */
	public void setDestMDN(String destMDN) {
		this.destMDN = destMDN;
	}
	
	/**
	 * @return the sourcePocket
	 */
	public Pocket getSourcePocket() {
		return sourcePocket;
	}

	/**
	 * @param sourcePocket the sourcePocket to set
	 */
	public void setSourcePocket(Pocket sourcePocket) {
		this.sourcePocket = sourcePocket;
	}

	/**
	 * @return the destPocket
	 */
	public Pocket getDestPocket() {
		return destPocket;
	}

        /**
	 * @param emailId
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}


        /**
	 * @param emailSubject
	 */
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailSubject() {
		return emailSubject;
	}

	/**
	 * @param destPocket the destPocket to set
	 */
	public void setDestPocket(Pocket destPocket) {
		this.destPocket = destPocket;
	}

	/**
	 * @return the commodityTransfer
	 */
	public CommodityTransfer getCommodityTransfer() {
		return commodityTransfer;
	}

	/**
	 * @param commodityTransfer the commodityTransfer to set
	 */
	public void setCommodityTransfer(CommodityTransfer commodityTransfer) {
		this.commodityTransfer = commodityTransfer;
	}
	
	/**
	 * @return the ServiceChargeTransactionLog
	 */
	public ServiceChargeTxnLog getServiceChargeTransactionLog() {
		return sctl;
	}

	/**
	 * @param commodityTransfer the commodityTransfer to set
	 */
	public void setServiceChargeTransactionLog(ServiceChargeTxnLog sctl) {
		this.sctl = sctl;
	}

	/**
	 * @return the company
	 */
	public Company getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * @return the transactionId
	 */
	public Long getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the lastBankTrxnEntry
	 */
	public CMGetLastTransactionsFromBank.CGEntries getLastBankTrxnEntry() {
		return lastBankTrxnEntry;
	}

	/**
	 * @param lastBankTrxnEntry the lastBankTrxnEntry to set
	 */
	public void setLastBankTrxnEntry(
			CMGetLastTransactionsFromBank.CGEntries lastBankTrxnEntry) {
		this.lastBankTrxnEntry = lastBankTrxnEntry;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}

	public BigDecimal getBillAmount() {
		return billAmount;
	}

	public void setBillPaymentID(Long billPaymentID) {
		this.billPaymentID = billPaymentID;
	}

	public Long getBillPaymentID() {
		return billPaymentID;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Bank getBank() {
		return bank;
	}

	public void setOneTimePin(String oneTimePin) {
		this.oneTimePin = oneTimePin;
	}

	public String getOneTimePin() {
		return oneTimePin;
	}

	public void setKycLevel(String kycLevel) {
		KycLevel = kycLevel;
	}

	public String getKycLevel() {
		return KycLevel;
	}

	public Integer getNumberOfTriesLeft() {
		return numberOfTriesLeft;
	}

	public void setNumberOfTriesLeft(Integer numberOfTriesLeft) {
		this.numberOfTriesLeft = numberOfTriesLeft;
	}

	/**
	 * @param sourceMDN the sourceMDN to set
	 */
	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}

	/**
	 * @return the sourceMDN
	 */
	public String getSourceMDN() {
		return sourceMDN;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param appURL the appURL to set
	 */
	public void setAppURL(String appURL) {
		this.appURL = appURL;
	}

	/**
	 * @return the appURL
	 */
	public String getAppURL() {
		return appURL;
	}

	public Long getOriginalTransferID() {
		return OriginalTransferID;
	}

	public void setOriginalTransferID(Long originalTransferID) {
		OriginalTransferID = originalTransferID;
	}

	public Long getSctlID() {
		return sctlID;
	}

	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}

	public Long getBulkTransferId() {
		return bulkTransferId;
	}

	public void setBulkTransferId(Long bulkTransferId) {
		this.bulkTransferId = bulkTransferId;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCustomerServiceShortCode() {
		return CustomerServiceShortCode;
	}

	public void setCustomerServiceShortCode(String customerServiceShortCode) {
		CustomerServiceShortCode = customerServiceShortCode;
	}

	public String getAuthenticationKey() {
		return authenticationKey;
	}

	public void setAuthenticationKey(String authenticationKey) {
		this.authenticationKey = authenticationKey;
	}

	public String getIntegrationName() {
		return integrationName;
	}

	public void setIntegrationName(String integrationName) {
		this.integrationName = integrationName;
	}

	public String getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(String institutionID) {
		this.institutionID = institutionID;
	}

	public String getIPAddress() {
		return ipAddress;
	}

	public void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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

	public String getReceiverAccountName() {
		return receiverAccountName;
	}

	public void setReceiverAccountName(String receiverAccountName) {
		this.receiverAccountName = receiverAccountName;
	}

	public String getReceiverMDN() {
		return receiverMDN;
	}

	public void setReceiverMDN(String receiverMDN) {
		this.receiverMDN = receiverMDN;
	}
	
	public String getSenderMDN() {
		return senderMDN;
	}

	public void setSenderMDN(String senderMDN) {
		this.senderMDN = senderMDN;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public SctlSettlementMap getPendingSettlement() {
		return pendingSettlement;
	}

	public void setPendingSettlement(SctlSettlementMap pendingSettlement) {
		this.pendingSettlement = pendingSettlement;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}
	public String getOtpExpirationTime() {
		return otpExpirationTime;
	}

	public void setOtpExpirationTime(String otpExpirationTime) {
		this.otpExpirationTime = otpExpirationTime;
	}

	public String getTransID() {
		return transID;
	}

	public void setTransID(String transID) {
		this.transID = transID;
	}

	public String getParentTransID() {
		return parentTransID;
	}

	public void setParentTransID(String parentTransID) {
		this.parentTransID = parentTransID;
	}

	public String getCardPan() {
		return cardPan;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	public String getMaxFavoriteCount() {
		return maxFavoriteCount;
	}

	public void setMaxFavoriteCount(String maxFavoriteCount) {
		this.maxFavoriteCount = maxFavoriteCount;
	}

	public String getFavoriteLabel() {
		return favoriteLabel;
	}

	public void setFavoriteLabel(String favoriteLabel) {
		this.favoriteLabel = favoriteLabel;
	}

	public String getFavoriteValue() {
		return favoriteValue;
	}

	public void setFavoriteValue(String favoriteValue) {
		this.favoriteValue = favoriteValue;
	}

	/**
	 * @return the subscriberStatus
	 */
	public String getSubscriberStatus() {
		return subscriberStatus;
	}

	/**
	 * @param subscriberStatus the subscriberStatus to set
	 */
	public void setSubscriberStatus(String subscriberStatus) {
		this.subscriberStatus = subscriberStatus;
	}

	public String getCardAlias() {
		return cardAlias;
	}

	public void setCardAlias(String cardAlias) {
		this.cardAlias = cardAlias;
	}

	public CMBalanceInquiryFromBank.CGEntries getLastNFCCheckBalanceEntry() {
		return lastNFCCheckBalanceEntry;
	}

	public void setLastNFCCheckBalanceEntry(CMBalanceInquiryFromBank.CGEntries lastNFCCheckBalanceEntry) {
		this.lastNFCCheckBalanceEntry = lastNFCCheckBalanceEntry;
	}

	public String getOldCardAlias() {
		return oldCardAlias;
	}

	public void setOldCardAlias(String oldCardAlias) {
		this.oldCardAlias = oldCardAlias;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getOtherMDN() {
		return otherMDN;
	}

	public void setOtherMDN(String otherMDN) {
		this.otherMDN = otherMDN;
	}

	public String getRemainingBlockTimeMinutes() {
		return remainingBlockTimeMinutes;
	}

	public void setRemainingBlockTimeMinutes(String remainingBlockTimeMinutes) {
		this.remainingBlockTimeMinutes = remainingBlockTimeMinutes;
	}

	public String getRemainingBlockTimeHours() {
		return remainingBlockTimeHours;
	}

	public void setRemainingBlockTimeHours(String remainingBlockTimeHours) {
		this.remainingBlockTimeHours = remainingBlockTimeHours;
	}

	public String getSubscriberPin() {
		return subscriberPin;
	}

	public void setSubscriberPin(String subscriberPin) {
		this.subscriberPin = subscriberPin;
	}

	public Integer getMaxTrails() {
		return maxTrails;
	}

	public void setMaxTrails(Integer maxTrails) {
		this.maxTrails = maxTrails;
	}

	public BigDecimal getMultiplesOff() {
		return multiplesOff;
	}

	public void setMultiplesOff(BigDecimal multiplesOff) {
		this.multiplesOff = multiplesOff;
	}

	public String getContactCenterNo() {
		return contactCenterNo;
	}

	public void setContactCenterNo(String contactCenterNo) {
		this.contactCenterNo = contactCenterNo;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}
}

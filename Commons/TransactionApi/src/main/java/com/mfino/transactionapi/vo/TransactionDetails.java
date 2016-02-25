/**
 * 
 */
package com.mfino.transactionapi.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.mfino.domain.ChannelCode;
import com.mfino.result.Result.ResultType;

/**
 * @author Bala Sunku
 *
 */
public class TransactionDetails {
	
	private String transactionCode;
	private String seqNum;
	private String sourceMDN;
	private String sourcePIN;
	private String destMDN;
	private String newPIN;
	private String confirmPIN;
	private BigDecimal amount;
	private String partnerCode;
	private String billNum;
	private String sourcePocketCode;
	private String destPocketCode;
	private String sourceMessage;
	private String serviceName;
	private String transactionName;
	private String activationOTP;
	private String firstName;
	private String lastName;
	private String MothersMaidenName;
	private Date dateOfBirth;
	private String applicationId;
	private Long transferId;
	private String channelCode;
	private Long parentTxnId;
	private String confirmString;
	private String accountType;
	private ResultType resultType;
	private String billerCode;
	private String salt;
	private String authenticationString;
	private String companyID;
	private boolean isHttps;
	protected String secreteCode;
	private String sourcePocketId;
	private String destPocketId;
	
	private String destAccountNumber;
	private String destBankCode;
	
	private String onBehalfOfMDN;
	private String category;
	private String version;

	private String appVersion ;
	private String appOS	 ;
	private String appType	 ;
	
	private String kycType;
	private String city;
	private String nextOfKin;
	private String nextOfKinNo;
	private String email;
	private String plotNo;
	private String streetAddress;
	private String regionName;
	private String country;
	private String idType;
	private String idNumber;
	private String dateOfExpiry;
	private String addressProof;
	private String birthPlace;
	private String nationality;
	private String companyName;
	private String subscriberMobileCompany;
	private String certOfIncorp;
	private String language;
	private String notificationMethod;
	private String institutionID;
	private String transID;
	//tarun
	//private String approvalRequired;
	private boolean approvalRequired;
	private String bankAccountType;
	private String cardPAN;
	private String cardAlias;
	private String authorizingFirstName;
	private String authorizingLastName;
	private String authorizingIdNumber;
	private String approvalComments;
	//Unique transaction id
	private String transactionIdentifier;
	private String narration;
	private String benOpCode;
	private String description;
	private String transactionOTP;
	private String mfaTransaction;
	private String servletPath;
	
	//use this channel code for getting the channel code domain object.This will be set in the BaseApiService
	private ChannelCode cc;
	private Long srcPocketId;
	private Long destinationPocketId;
	private Long sctlId;
	private Long chargeRevFundPocket; 
	private String newEmail;
	private String confirmEmail;
	private String nickname;
	private Boolean isAppTypeChkEnabled;
	private boolean isChargeReverseAlso;
	private boolean systemIntiatedTransaction;
	private String otherMdn;
	private boolean isHashedPin = false;
	
	private String pageNumber;
	private String numRecords;
	private String transactionTypeName;
	private String parentTransID;
	private String favoriteCategoryID;
	private String favoriteLabel;
	private String favoriteValue;
	private String favoriteCode;
	
	private Integer	partnerType;
	private String	tradeName;
	private String	postalCode;
	private String	userName;
	private String	outletClasification;
	private String	franchisePhoneNumber;
	private String	faxNumber;
	private String	typeOfOrganization;
	private String	webSite;
	private String	industryClassification;
	private Integer	numberOfOutlets;
	private Integer	yearEstablished;
	private String	outletAddressLine1;
	private String	outletAddressLine2;
	private String	outletAddressCity;
	private String	outletAddressState;
	private String	outletAddressZipcode;
	private String	outletAddressCountry;
	private String	authorizedRepresentative;
	private String	representativeName;
	private String	designation;
	private String	authorizedFaxNumber;
	private Date fromDate;
	private Date toDate;
	private String paymentMode;
	private String addressLine1;
	private String zipCode;
	private String state;
	private String userAPIKey;
	private String merchantData;
	private String denomCode;
	private BigDecimal nominalAmount;
	private BigDecimal discountAmount;
	private String loyalityName;
	private String discountType;
	private String numberOfCoupons;
	
	private String ktpId;
	private Date ktpValidUntil;
	private boolean ktpLifetime;
	private String ktpLine1;
	private String ktpLine2;
	private String ktpCity;
	private String ktpState;
	private String ktpSubState;
	private String subState;
	private String ktpCountry;
	private String ktpZipCode;
	private String ktpRegionName;
	private String domesticIdentity;
	private String work;
	private BigDecimal income;
	private String goalOfOpeningAccount;
	private String sourceOfFunds;
	private Long transactionId;
	private String ktpDocument;
	private String subscriberFormDocument;
	private String supportingDocument;
	
	private String RT;
	private String RW;
	private String ktpRW;
	private String ktpRT;
	private boolean isSimaspayActivity;
	
	private String agentMDN;
	private String fullName;
	private String subscriberMDN;
	private String productDesired;
	private String others;
	
	public boolean isSimaspayActivity() {
		return isSimaspayActivity;
	}
	public void setSimaspayActivity(boolean isSimaspayActivity) {
		this.isSimaspayActivity = isSimaspayActivity;
	}
	public String getAgentMDN() {
		return agentMDN;
	}
	public void setAgentMDN(String agentMDN) {
		this.agentMDN = agentMDN;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getSubscriberMDN() {
		return subscriberMDN;
	}
	public void setSubscriberMDN(String subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}
	public String getProductDesired() {
		return productDesired;
	}
	public void setProductDesired(String productDesired) {
		this.productDesired = productDesired;
	}
	public String getOthers() {
		return others;
	}
	public void setOthers(String others) {
		this.others = others;
	}
	/**
	 * @return the ktpSubState
	 */
	public String getKtpSubState() {
		return ktpSubState;
	}
	/**
	 * @param ktpSubState the ktpSubState to set
	 */
	public void setKtpSubState(String ktpSubState) {
		this.ktpSubState = ktpSubState;
	}
	/**
	 * @return the subState
	 */
	public String getSubState() {
		return subState;
	}
	/**
	 * @param subState the subState to set
	 */
	public void setSubState(String subState) {
		this.subState = subState;
	}
	public boolean isSimpaspayActivity() {
		return isSimaspayActivity;
	}
	public void setSimpaspayActivity(boolean isSimaspayActivity) {
		this.isSimaspayActivity = isSimaspayActivity;
	}
	
	/**
	 * @return the rT
	 */
	public String getRT() {
		return RT;
	}
	/**
	 * @param rT the rT to set
	 */
	public void setRT(String rT) {
		RT = rT;
	}
	/**
	 * @return the rW
	 */
	public String getRW() {
		return RW;
	}
	/**
	 * @param rW the rW to set
	 */
	public void setRW(String rW) {
		RW = rW;
	}
	/**
	 * @return the ktpRW
	 */
	public String getKtpRW() {
		return ktpRW;
	}
	/**
	 * @param ktpRW the ktpRW to set
	 */
	public void setKtpRW(String ktpRW) {
		this.ktpRW = ktpRW;
	}
	/**
	 * @return the ktpRT
	 */
	public String getKtpRT() {
		return ktpRT;
	}
	/**
	 * @param ktpRT the ktpRT to set
	 */
	public void setKtpRT(String ktpRT) {
		this.ktpRT = ktpRT;
	}
	/**
	 * @return the ktpId
	 */
	public String getKtpId() {
		return ktpId;
	}
	/**
	 * @param ktpId the ktpId to set
	 */
	public void setKtpId(String ktpId) {
		this.ktpId = ktpId;
	}
	/**
	 * @return the ktpValidUntil
	 */
	public Date getKtpValidUntil() {
		return ktpValidUntil;
	}
	/**
	 * @param ktpValidUntil the ktpValidUntil to set
	 */
	public void setKtpValidUntil(Date ktpValidUntil) {
		this.ktpValidUntil = ktpValidUntil;
	}
	/**
	 * @return the ktpLifetime
	 */
	public boolean isKtpLifetime() {
		return ktpLifetime;
	}
	/**
	 * @param ktpLifetime the ktpLifetime to set
	 */
	public void setKtpLifetime(boolean ktpLifetime) {
		this.ktpLifetime = ktpLifetime;
	}
	/**
	 * @return the ktpLine1
	 */
	public String getKtpLine1() {
		return ktpLine1;
	}
	/**
	 * @param ktpLine1 the ktpLine1 to set
	 */
	public void setKtpLine1(String ktpLine1) {
		this.ktpLine1 = ktpLine1;
	}
	/**
	 * @return the ktpLine2
	 */
	public String getKtpLine2() {
		return ktpLine2;
	}
	/**
	 * @param ktpLine2 the ktpLine2 to set
	 */
	public void setKtpLine2(String ktpLine2) {
		this.ktpLine2 = ktpLine2;
	}
	/**
	 * @return the ktpCity
	 */
	public String getKtpCity() {
		return ktpCity;
	}
	/**
	 * @param ktpCity the ktpCity to set
	 */
	public void setKtpCity(String ktpCity) {
		this.ktpCity = ktpCity;
	}
	/**
	 * @return the ktpState
	 */
	public String getKtpState() {
		return ktpState;
	}
	/**
	 * @param ktpState the ktpState to set
	 */
	public void setKtpState(String ktpState) {
		this.ktpState = ktpState;
	}
	/**
	 * @return the ktpCountry
	 */
	public String getKtpCountry() {
		return ktpCountry;
	}
	/**
	 * @param ktpCountry the ktpCountry to set
	 */
	public void setKtpCountry(String ktpCountry) {
		this.ktpCountry = ktpCountry;
	}
	/**
	 * @return the ktpZipCode
	 */
	public String getKtpZipCode() {
		return ktpZipCode;
	}
	/**
	 * @param ktpZipCode the ktpZipCode to set
	 */
	public void setKtpZipCode(String ktpZipCode) {
		this.ktpZipCode = ktpZipCode;
	}
	/**
	 * @return the ktpRegionName
	 */
	public String getKtpRegionName() {
		return ktpRegionName;
	}
	/**
	 * @param ktpRegionName the ktpRegionName to set
	 */
	public void setKtpRegionName(String ktpRegionName) {
		this.ktpRegionName = ktpRegionName;
	}
	/**
	 * @return the domesticIdentity
	 */
	public String getDomesticIdentity() {
		return domesticIdentity;
	}
	/**
	 * @param domesticIdentity the domesticIdentity to set
	 */
	public void setDomesticIdentity(String domesticIdentity) {
		this.domesticIdentity = domesticIdentity;
	}
	/**
	 * @return the work
	 */
	public String getWork() {
		return work;
	}
	/**
	 * @param work the work to set
	 */
	public void setWork(String work) {
		this.work = work;
	}
	/**
	 * @return the income
	 */
	public BigDecimal getIncome() {
		return income;
	}
	/**
	 * @param income the income to set
	 */
	public void setIncome(BigDecimal income) {
		this.income = income;
	}
	/**
	 * @return the goalOfOpeningAccount
	 */
	public String getGoalOfOpeningAccount() {
		return goalOfOpeningAccount;
	}
	/**
	 * @param goalOfOpeningAccount the goalOfOpeningAccount to set
	 */
	public void setGoalOfOpeningAccount(String goalOfOpeningAccount) {
		this.goalOfOpeningAccount = goalOfOpeningAccount;
	}
	/**
	 * @return the sourceOfFunds
	 */
	public String getSourceOfFunds() {
		return sourceOfFunds;
	}
	/**
	 * @param sourceOfFunds the sourceOfFunds to set
	 */
	public void setSourceOfFunds(String sourceOfFunds) {
		this.sourceOfFunds = sourceOfFunds;
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
	 * @return the ktpDocument
	 */
	public String getKtpDocument() {
		return ktpDocument;
	}
	/**
	 * @param ktpDocument the ktpDocument to set
	 */
	public void setKtpDocument(String ktpDocument) {
		this.ktpDocument = ktpDocument;
	}
	/**
	 * @return the subscriberFormDocument
	 */
	public String getSubscriberFormDocument() {
		return subscriberFormDocument;
	}
	/**
	 * @param subscriberFormDocument the subscriberFormDocument to set
	 */
	public void setSubscriberFormDocument(String subscriberFormDocument) {
		this.subscriberFormDocument = subscriberFormDocument;
	}
	/**
	 * @return the supportingDocument
	 */
	public String getSupportingDocument() {
		return supportingDocument;
	}
	/**
	 * @param supportingDocument the supportingDocument to set
	 */
	public void setSupportingDocument(String supportingDocument) {
		this.supportingDocument = supportingDocument;
	}
	/**
	 * @param isHttps the isHttps to set
	 */
	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}
	public String getTransactionTypeName() {
		return transactionTypeName;
	}
	public void setTransactionTypeName(String transactionTypeName) {
		this.transactionTypeName = transactionTypeName;
	}
	public boolean isHashedPin() {
		return isHashedPin;
	}
	public void setHashedPin(boolean isHashedPin) {
		this.isHashedPin = isHashedPin;
	}
	public boolean isSystemIntiatedTransaction() {
		return systemIntiatedTransaction;
	}
	public void setSystemIntiatedTransaction(boolean systemIntiatedTransaction) {
		this.systemIntiatedTransaction = systemIntiatedTransaction;
	}
	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}
	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}
	
	
	/*
	 * used by e-money2bank and bank2bank transfers.
	 */
	private String destinationBankAccountNo;

	
	public String getAppVersion() {
    	return appVersion;
    }
	public void setAppVersion(String appVersion) {
    	this.appVersion = appVersion;
    }
	public String getAppOS() {
    	return appOS;
    }
	public void setAppOS(String appOS) {
    	this.appOS = appOS;
    }
	public String getTransID() {
		return transID;
	}
	public void setTransID(String transID) {
		this.transID = transID;
	}
	public String getAppType() {
    	return appType;
    }
	public void setAppType(String appType) {
    	this.appType = appType;
    }
	/**
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}
	/**
	 * @param salt the salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}
	/**
	 * @return the authenticationString
	 */
	public String getAuthenticationString() {
		return authenticationString;
	}
	/**
	 * @param authenticationString the authenticationString to set
	 */
	public void setAuthenticationString(String authenticationString) {
		this.authenticationString = authenticationString;
	}
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}
	public String getSourceMDN() {
		return sourceMDN;
	}
	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}
	public String getSourcePIN() {
		return sourcePIN;
	}
	public void setSourcePIN(String sourcePIN) {
		this.sourcePIN = sourcePIN;
	}
	public String getDestMDN() {
		return destMDN;
	}
	public void setDestMDN(String destMDN) {
		this.destMDN = destMDN;
	}
	public String getNewPIN() {
		return newPIN;
	}
	public void setNewPIN(String newPIN) {
		this.newPIN = newPIN;
	}
	public String getConfirmPIN() {
		return confirmPIN;
	}
	public void setConfirmPIN(String confirmPIN) {
		this.confirmPIN = confirmPIN;
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
	public String getBillNum() {
		return billNum;
	}
	public String getServletPath() {
		return servletPath;
	}
	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}
	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}
	public String getSourcePocketCode() {
		return sourcePocketCode;
	}
	public void setSourcePocketCode(String sourcePocketCode) {
		this.sourcePocketCode = sourcePocketCode;
	}
	public String getDestPocketCode() {
		return destPocketCode;
	}
	public void setDestPocketCode(String destPocketCode) {
		this.destPocketCode = destPocketCode;
	}
	public String getSourceMessage() {
		return sourceMessage;
	}
	public void setSourceMessage(String sourceMessage) {
		this.sourceMessage = sourceMessage;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getTransactionName() {
		return transactionName;
	}
	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
	public String getActivationOTP() {
		return activationOTP;
	}
	public void setActivationOTP(String activationOTP) {
		this.activationOTP = activationOTP;
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
	public String getMothersMaidenName() {
		return  MothersMaidenName;
	}
	public void setMothersMaidenName(String  MothersMaidenName) {
		this. MothersMaidenName =  MothersMaidenName;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public Long getTransferId() {
		return transferId;
	}
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public Long getParentTxnId() {
		return parentTxnId;
	}
	public void setParentTxnId(Long parentTxnId) {
		this.parentTxnId = parentTxnId;
	}
	public String getConfirmString() {
		return confirmString;
	}
	public void setConfirmString(String confirmString) {
		this.confirmString = confirmString;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public ResultType getResultType() {
		return resultType;
	}
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}
	/**
	 * @param billerCode the billerCode to set
	 */
	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}
	/**
	 * @return the billerCode
	 */
	public String getBillerCode() {
		return billerCode;
	}
	/**
	 * @param isHttps the isHttps to set
	 */
	public void setIsHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}
	/**
	 * @return the isHttps
	 */
	public boolean isHttps() {
		return isHttps;
	}
	
	public String getCompanyID() {
		return companyID;
	}
	
	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}
	/**
	 * @param destAccountNumber the destAccountNumber to set
	 */
	public void setDestAccountNumber(String destAccountNumber) {
		this.destAccountNumber = destAccountNumber;
	}
	/**
	 * @return the destAccountNumber
	 */
	public String getDestAccountNumber() {
		return destAccountNumber;
	}
	/**
	 * @param destBankCode the destBankCode to set
	 */
	public void setDestBankCode(String destBankCode) {
		this.destBankCode = destBankCode;
	}
	/**
	 * @return the destBankCode
	 */
	public String getDestBankCode() {
		return destBankCode;
	}
	
	public String getSecreteCode() {
		return secreteCode;
	}
	public void setSecreteCode(String secreteCode) {
		this.secreteCode = secreteCode;
	}
	public String getSourcePocketId() {
		return sourcePocketId;
	}
	public void setSourcePocketId(String sourcePocketId) {
		this.sourcePocketId = sourcePocketId;
	}
	public String getDestPocketId() {
		return destPocketId;
	}
	public void setDestPocketId(String destPocketId) {
		this.destPocketId = destPocketId;
	}
	public String getOnBehalfOfMDN() {
	    return onBehalfOfMDN;
    }
	public void setOnBehalfOfMDN(String onBehalfOfMDN) {
	    this.onBehalfOfMDN = onBehalfOfMDN;
    }
	public String getDestinationBankAccountNo() {
		return destinationBankAccountNo;
	}
	public void setDestinationBankAccountNo(String destinationBankAccountNo) {
		this.destinationBankAccountNo = destinationBankAccountNo;
	}
	
	public String getKycType() {
		return kycType;
	}
	public void setKycType(String kycType) {
		this.kycType = kycType;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getNextOfKin() {
		return nextOfKin;
	}
	public void setNextOfKin(String nextOfKin) {
		this.nextOfKin = nextOfKin;
	}
	public String getNextOfKinNo() {
		return nextOfKinNo;
	}
	public void setNextOfKinNo(String nextOfKinNo) {
		this.nextOfKinNo = nextOfKinNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPlotNo() {
		return plotNo;
	}
	public void setPlotNo(String plotNo) {
		this.plotNo = plotNo;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getDateOfExpiry() {
		return dateOfExpiry;
	}
	public void setDateOfExpiry(String dateOfExpiry) {
		this.dateOfExpiry = dateOfExpiry;
	}
	public String getAddressProof() {
		return addressProof;
	}
	public void setAddressProof(String addressProof) {
		this.addressProof = addressProof;
	}
	public String getBirthPlace() {
		return birthPlace;
	}
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getSubscriberMobileCompany() {
		return subscriberMobileCompany;
	}
	public void setSubscriberMobileCompany(String subscriberMobileCompany) {
		this.subscriberMobileCompany = subscriberMobileCompany;
	}
	public String getCertOfIncorp() {
		return certOfIncorp;
	}
	public void setCertOfIncorp(String certOfIncorp) {
		this.certOfIncorp = certOfIncorp;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getNotificationMethod() {
		return notificationMethod;
	}
	public void setNotificationMethod(String notificationMethod) {
		this.notificationMethod = notificationMethod;
	}
	public String getInstitutionID() {
		return institutionID;
	}
	public void setInstitutionID(String institutionID) {
		this.institutionID = institutionID;
	}
	
	// tarun
	/*public String getApprovalRequired() {
		
		return approvalRequired;
	}*/
	public boolean getApprovalRequired() {
		
		return approvalRequired;
	}
	public void setApprovalRequired(boolean approvalRequired ) {
		this.approvalRequired = approvalRequired;
	}
	
	public  String getBankAccountType() { 
		return bankAccountType;
	}
	public void setBankAccountType( String bankAccountType ) {
		this.bankAccountType = bankAccountType;
	}
	
	public  String getCardPAN() { 
		return cardPAN;
	}
	public void setCardPAN( String cardPAN ) {
		this.cardPAN = cardPAN;
	}
	
	public String getCardAlias() {
		return cardAlias;
	}
	public void setCardAlias(String cardAlias) {
		this.cardAlias = cardAlias;
	}
	public  String getAuthorizingFirstName() { 
		return authorizingFirstName;
	}
	public void setAuthorizingFirstName( String authorizingFirstName ) {
		this.authorizingFirstName = authorizingFirstName;
	}
	
	public  String getAuthorizingLastName() { 
		return authorizingLastName;
	}
	public void setAuthorizingLastName( String authorizingLastName ) {
		this.authorizingLastName = authorizingLastName;
	}
	
	public  String getAuthorizingIdNumber() { 
		return authorizingIdNumber;
	}
	public void setAuthorizingIdNumber( String authorizingIdNumber ) {
		this.authorizingIdNumber = authorizingIdNumber;
	}
	
	public  String getApprovalComments() { 
		return approvalComments;
	}
	public void setApprovalComments( String approvalComments ) {
		this.approvalComments = approvalComments;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getBenOpCode() {
		return benOpCode;
	}
	public void setBenOpCode(String benOpCode) {
		this.benOpCode = benOpCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTransactionOTP() {
		return transactionOTP;
	}
	public void setTransactionOTP(String transactionOTP) {
		this.transactionOTP = transactionOTP;
	}
	public String getMfaTransaction() {
		return mfaTransaction;
	}
	public void setMfaTransaction(String mfaTransaction) {
		this.mfaTransaction = mfaTransaction;
	}
	/**
	 * 
	 * @return the channel code object
	 */
	public ChannelCode getCc() {
		return cc;
	}
	/**
	 * Set th channel Code Object
	 * @param cc
	 */
	public void setCc(ChannelCode cc) {
		this.cc = cc;
	}
	public Boolean getIsAppTypeChkEnabled() {
		return isAppTypeChkEnabled;
	}
	public void setIsAppTypeChkEnabled(Boolean isAppTypeChkEnabled) {
		this.isAppTypeChkEnabled = isAppTypeChkEnabled;
	}
	/**
	 * @return the srcPocketId
	 */
	public Long getSrcPocketId() {
		return srcPocketId;
	}
	/**
	 * @param srcPocketId the srcPocketId to set
	 */
	public void setSrcPocketId(Long srcPocketId) {
		this.srcPocketId = srcPocketId;
	}
	/**
	 * @return the destinationPocketId
	 */
	public Long getDestinationPocketId() {
		return destinationPocketId;
	}
	/**
	 * @param destinationPocketId the destinationPocketId to set
	 */
	public void setDestinationPocketId(Long destinationPocketId) {
		this.destinationPocketId = destinationPocketId;
	}
	public Long getChargeRevFundPocket() {
		return chargeRevFundPocket;
	}
	public void setChargeRevFundPocket(Long chargeRevFundPocket) {
		this.chargeRevFundPocket = chargeRevFundPocket;
	}
	public Long getSctlId() {
		return sctlId;
	}
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}
	public boolean isChargeReverseAlso() {
		return isChargeReverseAlso;
	}
	public void setChargeReverseAlso(boolean isChargeReverseAlso) {
		this.isChargeReverseAlso = isChargeReverseAlso;
	}
	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getNumRecords() {
		return numRecords;
	}
	public void setNumRecords(String numRecords) {
		this.numRecords = numRecords;
	}
	public String getConfirmEmail() {
		return confirmEmail;
	}
	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}
	public String getNewEmail() {
		return newEmail;
	}
	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getOtherMdn() {
		return otherMdn;
	}
	public void setOtherMdn(String otherMdn) {
		this.otherMdn = otherMdn;
	}
	public String getParentTransID() {
		return parentTransID;
	}
	public void setParentTransID(String parentTransID) {
		this.parentTransID = parentTransID;
	}
	public String getFavoriteCategoryID() {
		return favoriteCategoryID;
	}
	public void setFavoriteCategoryID(String favoriteCategoryID) {
		this.favoriteCategoryID = favoriteCategoryID;
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
	public Integer getPartnerType() {
		return partnerType;
	}
	public void setPartnerType(Integer partnerType) {
		this.partnerType = partnerType;
	}
	public String getTradeName() {
		return tradeName;
	}
	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOutletClasification() {
		return outletClasification;
	}
	public void setOutletClasification(String outletClasification) {
		this.outletClasification = outletClasification;
	}
	public String getFranchisePhoneNumber() {
		return franchisePhoneNumber;
	}
	public void setFranchisePhoneNumber(String franchisePhoneNumber) {
		this.franchisePhoneNumber = franchisePhoneNumber;
	}
	public String getFaxNumber() {
		return faxNumber;
	}
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	public String getTypeOfOrganization() {
		return typeOfOrganization;
	}
	public void setTypeOfOrganization(String typeOfOrganization) {
		this.typeOfOrganization = typeOfOrganization;
	}
	public String getWebSite() {
		return webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public String getIndustryClassification() {
		return industryClassification;
	}
	public void setIndustryClassification(String industryClassification) {
		this.industryClassification = industryClassification;
	}
	public Integer getNumberOfOutlets() {
		return numberOfOutlets;
	}
	public void setNumberOfOutlets(Integer numberOfOutlets) {
		this.numberOfOutlets = numberOfOutlets;
	}
	public Integer getYearEstablished() {
		return yearEstablished;
	}
	public void setYearEstablished(Integer yearEstablished) {
		this.yearEstablished = yearEstablished;
	}
	public String getOutletAddressLine1() {
		return outletAddressLine1;
	}
	public void setOutletAddressLine1(String outletAddressLine1) {
		this.outletAddressLine1 = outletAddressLine1;
	}
	public String getOutletAddressLine2() {
		return outletAddressLine2;
	}
	public void setOutletAddressLine2(String outletAddressLine2) {
		this.outletAddressLine2 = outletAddressLine2;
	}
	public String getOutletAddressCity() {
		return outletAddressCity;
	}
	public void setOutletAddressCity(String outletAddressCity) {
		this.outletAddressCity = outletAddressCity;
	}
	public String getOutletAddressState() {
		return outletAddressState;
	}
	public void setOutletAddressState(String outletAddressState) {
		this.outletAddressState = outletAddressState;
	}
	public String getOutletAddressZipcode() {
		return outletAddressZipcode;
	}
	public void setOutletAddressZipcode(String outletAddressZipcode) {
		this.outletAddressZipcode = outletAddressZipcode;
	}
	public String getOutletAddressCountry() {
		return outletAddressCountry;
	}
	public void setOutletAddressCountry(String outletAddressCountry) {
		this.outletAddressCountry = outletAddressCountry;
	}
	public String getAuthorizedRepresentative() {
		return authorizedRepresentative;
	}
	public void setAuthorizedRepresentative(String authorizedRepresentative) {
		this.authorizedRepresentative = authorizedRepresentative;
	}
	public String getRepresentativeName() {
		return representativeName;
	}
	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getAuthorizedFaxNumber() {
		return authorizedFaxNumber;
	}
	public void setAuthorizedFaxNumber(String authorizedFaxNumber) {
		this.authorizedFaxNumber = authorizedFaxNumber;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getFavoriteCode() {
		return favoriteCode;
	}
	public void setFavoriteCode(String favoriteCode) {
		this.favoriteCode = favoriteCode;
	}
	public String getUserAPIKey() {
		return userAPIKey;
	}
	public void setUserAPIKey(String userAPIKey) {
		this.userAPIKey = userAPIKey;
	}
	public String getMerchantData() {
		return merchantData;
	}
	public void setMerchantData(String merchantData) {
		this.merchantData = merchantData;
	}
	public String getDenomCode() {
		return denomCode;
	}
	public void setDenomCode(String denomCode) {
		this.denomCode = denomCode;
	}
	public BigDecimal getNominalAmount() {
		return nominalAmount;
	}
	public void setNominalAmount(BigDecimal nominalAmount) {
		this.nominalAmount = nominalAmount;
	}
	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getLoyalityName() {
		return loyalityName;
	}
	public void setLoyalityName(String loyalityName) {
		this.loyalityName = loyalityName;
	}
	public String getDiscountType() {
		return discountType;
	}
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}
	public String getNumberOfCoupons() {
		return numberOfCoupons;
	}
	public void setNumberOfCoupons(String numberOfCoupons) {
		this.numberOfCoupons = numberOfCoupons;
	}
}
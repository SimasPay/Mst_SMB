package com.mfino.webapi.utilities;

import com.mfino.transactionapi.constants.ApiConstants;


/**
 * @author sasidhar
 */
public class ReceivedUserDataContainer {

    private String sourceMdn;
    private String destinationMdn;
    private String sourcePin;
    private String amount;
    private String transferId;
    private String parentTxnId;
    private String secretAnswer;
    private String oldPin;
    private String newPin;
    private String ActivationConfirmPin;
    private String ActivationNewPin;

    private String confirmPin;
    private String otp;
    private String TransactionID;
    private String Transactioncharges;
    private String creditAmount;
    private String debitAmount;
    private String authenticationString;
    private String salt;
    private String confirmed;
    private String channelId;
    private String sourcePocketCode;
    private String destinationPocketCode;
    private String serviceName;
    private String TransactionName;
    private String partnerCode;
    private String firstName;
    private String lastName;
    private String mothersMaidenName;
    private String applicationID;
    private String SubscriberMDN;
    private String DateOfBirth;
    private String AccountType;
    private String ResponseMessage;
    private String agentCode;
    private String SourceMessage;
    private String billerCode;
    private String billNo;
    private String companyID;
	private String destBankCode;
	private String destAccountNumber;
	private String secreteCode;
	private String appVersion ;
	private String appOS	 ;
	private String appType	 ;
	private String destinationBankAccountNo;
	
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
	private String onBehalfOfMDN;
	private String category;
	private String version;
	//tarun
	private boolean approvalRequired;
	private String bankAccountType;
	private String cardPAN;
	private String cardAlias;
	private String authorizingFirstName;
	private String authorizingLastName;
	private String authorizingIdNumber;
	private String approvalComments;
	private String narration;
	private String benOpCode;
	private String description;
	private String mfaTransaction;
	private String mfaOtp; 
	private String transID;
	private String pageNumber;
	private String numRecords;
	private String newEmail;
	private String confirmEmail;
	private String nickname;	
	private String otherMdn;
	private String parentTransID;
	private String favoriteCategoryID;
	private String favoriteLabel;
	private String favoriteValue;
	private String favoriteCode;
	
	private String	partnerType;
	private String	tradeName;
	private String	postalCode;
	private String	userName;
	private String	outletClasification;
	private String	franchisePhoneNumber;
	private String	faxNumber;
	private String	typeOfOrganization;
	private String	webSite;
	private String	industryClassification;
	private String	numberOfOutlets;
	private String	yearEstablished;
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
	private String  paymentMode;
	private String fromDate;
	private String toDate;
	private String addressLine1;
	private String zipCode;
	private String state;
	private String merchantData;
	private String userAPIKey;
	
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

	public String getAppType() {
    	return appType;
    }

	public void setAppType(String appType) {
    	this.appType = appType;
    }

	/**
     * @return the agentCode
     */
    public String getAgentCode() {
        return agentCode;
    }

    /**
     * @param agentCode the agentCode to set
     */
    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getSourceMdn() {
        return sourceMdn;
    }

    public void setSourceMdn(String sourceMdn) throws Exception {
        if (this.sourceMdn == null) {
            this.sourceMdn = sourceMdn;
        } else {
            throw new Exception("SourceMDN can not be changed");
        }
    }

    public String getDestinationMdn() {
        return destinationMdn;
    }

    public void setDestinationMdn(String destinationMdn) {
        this.destinationMdn = destinationMdn;
    }

    public String getSourcePin() {
        return sourcePin;
    }

    public void setSourcePin(String sourcePin) {
        this.sourcePin = sourcePin;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getSourcePocketCode() {
        return sourcePocketCode;
    }

    public void setSourcePocketCode(String sourcePocketCode) {
        this.sourcePocketCode = sourcePocketCode;
    }

    public String getDestinationPocketCode() {
        return destinationPocketCode;
    }

    public void setDestinationPocketCode(String destinationPocketCode) {
        this.destinationPocketCode = destinationPocketCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getParentTxnId() {
        return parentTxnId;
    }

    public void setParentTxnId(String parentTxnId) {
        this.parentTxnId = parentTxnId;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getOldPin() {
        return oldPin;
    }

    public void setOldPin(String oldPin) {
        this.oldPin = oldPin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getConfirmPin() {
        return confirmPin;
    }

    public void setConfirmPin(String confirmPin) {
        this.confirmPin = confirmPin;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAuthenticationString() {
        return authenticationString;
    }

    public void setAuthenticationString(String authenticationString) {
        this.authenticationString = authenticationString;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setMothersMaidenName(String mothersMaidenName) {
        this.mothersMaidenName = mothersMaidenName;
    }
    public String getMothersMaidenName() {
        return mothersMaidenName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the applicationID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * @param applicationID the applicationID to set
     */
    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * @return the SubscriberMDN
     */
    public String getSubscriberMDN() {
        return SubscriberMDN;
    }

    /**
     * @param SubscriberMDN the SubscriberMDN to set
     */
    public void setSubscriberMDN(String SubscriberMDN) {
        this.SubscriberMDN = SubscriberMDN;
    }

    /**
     * @return the DateOfBirth
     */
    public String getDateOfBirth() {
        return DateOfBirth;
    }

    /**
     * @param DateOfBirth the DateOfBirth to set
     */
    public void setDateOfBirth(String DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }

    /**
     * @return the AccountType
     */
    public String getAccountType() {
        return AccountType;
    }

    /**
     * @param AccountType the AccountType to set
     */
    public void setAccountType(String AccountType) {
        this.AccountType = AccountType;
    }

    /**
     * @return the TransactionName
     */
    public String getTransactionName() {
        return TransactionName;
    }

    /**
     * @param TransactionName the TransactionName to set
     */
    public void setTransactionName(String TransactionName) {
        this.TransactionName = TransactionName;
    }

    /**
     * @return the ResponseMessage
     */
    public String getResponseMessage() {
        return ResponseMessage;
    }

    /**
     * @param ResponseMessage the ResponseMessage to set
     */
    public void setResponseMessage(String ResponseMessage) {
        this.ResponseMessage = ResponseMessage;
    }

    /**
     * @return the merchantCode
     */
    public String getPartnerCode() {
        return partnerCode;
    }

    /**
     * @param partnerCode the merchantCode to set
     */
    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    /**
     * @return the otp
     */
    public String getOTP() {
        return otp;
    }

    /**
     * @param otp the otp to set
     */
    public void setOtp(String otp) {
        this.otp = otp;
    }

    /**
     * @return the Transactioncharges
     */
    public String getTransactioncharges() {
        return Transactioncharges;
    }

    /**
     * @param Transactioncharges the Transactioncharges to set
     */
    public void setTransactioncharges(String Transactioncharges) {
        this.Transactioncharges = Transactioncharges;
    }

    /**
     * @return the TransactionID
     */
    public String getTransactionID() {
        return TransactionID;
    }

    /**
     * @param TransactionID the TransactionID to set
     */
    public void setTransactionID(String TransactionID) {
        this.TransactionID = TransactionID;
    }

    /**
     * @return the netCahsinAmount
     */
    public String getCreditAmount() {
        return creditAmount;
    }

    /**
     * @param netCahsinAmount the netCahsinAmount to set
     */
    public void setCreditAmount(String netCahsinAmount) {
        this.creditAmount = netCahsinAmount;
    }

    /**
     * @return the debitAmount
     */
    public String getDebitAmount() {
        return debitAmount;
    }

    /**
     * @param debitAmount the debitAmount to set
     */
    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

	public void setChannelId(String channelId) {
	    this.channelId = channelId;
    }

	public String getSourceMessage() {
	    return SourceMessage;
    }

	public void setSourceMessage(String sourceMessage) {
	    SourceMessage = sourceMessage;
    }

	public String getActivationNewPin() {
	    return ActivationNewPin;
    }

	public void setActivationNewPin(String activationNewPin) {
	    ActivationNewPin = activationNewPin;
    }

	public String getActivationConfirmPin() {
	    return ActivationConfirmPin;
    }

	public void setActivationConfirmPin(String activationConfirmPin) {
	    ActivationConfirmPin = activationConfirmPin;
    }

	public String getBillerCode() {
		return billerCode;
	}

	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
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
	//tarun
	public boolean getApprovalRequired() {
		return approvalRequired;
	}
	public void setApprovalRequired(boolean approvalRequired) {
		this.approvalRequired = approvalRequired;
	}
	
	public  String getBankAccountType() { 
		return bankAccountType;
	}
	public void setBankAccountType( String bankAccountType) {
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

	public String getOnBehalfOfMDN() {
		return onBehalfOfMDN;
	}

	public void setOnBehalfOfMDN(String onBehalfOfMDN) {
		this.onBehalfOfMDN = onBehalfOfMDN;
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
	
	public String getMfaTransaction() {
		return mfaTransaction;
	}

	public void setMfaTransaction(String mfaTransaction) {
		this.mfaTransaction = mfaTransaction;
	}
	
	public String getMfaOtp() {
		return mfaOtp;
	}

	public void setMfaOtp(String mfaOTP) {
		this.mfaOtp = mfaOTP;
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

	public String getPartnerType() {
		return partnerType;
	}

	public void setPartnerType(String partnerType) {
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

	public String getNumberOfOutlets() {
		return numberOfOutlets;
	}

	public void setNumberOfOutlets(String numberOfOutlets) {
		this.numberOfOutlets = numberOfOutlets;
	}

	public String getYearEstablished() {
		return yearEstablished;
	}

	public void setYearEstablished(String yearEstablished) {
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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
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
	public String getMerchantData() {
		return merchantData;
	}

	public void setMerchantData(String merchantData) {
		this.merchantData = merchantData;
	}

	public String getUserAPIKey() {
		return userAPIKey;
	}

	public void setUserAPIKey(String userAPIKey) {
		this.userAPIKey = userAPIKey;
	}
}
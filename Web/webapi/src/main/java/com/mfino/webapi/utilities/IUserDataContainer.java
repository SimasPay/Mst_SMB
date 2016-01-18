package com.mfino.webapi.utilities;

import org.bouncycastle.crypto.params.KeyParameter;

public interface IUserDataContainer {

	public abstract String getChannelId();

	public abstract String getSourceMessage();

	/**
	 * @return the agentCode
	 */
	public abstract String getAgentCode();

	/**
	 * @return the sourceMdn
	 */
	public abstract String getSourceMdn();

	/**
	 * @return the destinationMdn
	 */
	public abstract String getDestinationMdn();

	/**
	 * @return the sourcePin
	 */
	public abstract String getSourcePin();

	/**
	 * @return the Amount
	 */
	public abstract String getAmount();

	/**
	 * @return the TransferId
	 */
	public abstract String getTransferId();

	/**
	 * @return the ParentTxnId
	 */
	public abstract String getParentTxnId();

	/**
	 * @return the SecretAnswer
	 */
	public abstract String getSecretAnswer();

	/**
	 * @return the OldPin
	 */
	public abstract String getOldPin();

	/**
	 * @return the NewPin
	 */
	public abstract String getNewPin();

	/**
	 * @return the ConfirmPin
	 */
	public abstract String getConfirmPin();

	/**
	 * @return the ConfirmPin
	 */
	public abstract String getActivationConfirmPin();
	
	/**
	 * @return the ConfirmPin
	 */
	public abstract String getActivationNewPin();
	
	/**
	 * @return the OTP
	 */
	public abstract String getOTP();

	/**
	 * @return the TransactionID
	 */
	public abstract String getTransactionID();

	/**
	 * @return the Transactioncharges
	 */
	public abstract String getTransactioncharges();

	/**
	 * @return the creditAmount
	 */
	public abstract String getCreditAmount();

	/**
	 * @return the debitAmount
	 */
	public abstract String getDebitAmount();

	/**
	 * @return the authenticationString
	 */
	public abstract String getAuthenticationString();

	/**
	 * @return the salt
	 */
	public abstract String getSalt();

	/**
	 * @return the confirmed
	 */
	public abstract String getConfirmed();

	/**
	 * @return the sourcePocketCode
	 */
	public abstract String getSourcePocketCode();

	/**
	 * @return the destinationPocketCode
	 */
	public abstract String getDestinationPocketCode();

	/**
	 * @return the serviceName
	 */
	public abstract String getServiceName();

	/**
	 * @return the TransactionName
	 */
	public abstract String getTransactionName();

	/**
	 * @return the partnerCode
	 */
	public abstract String getPartnerCode();

	/**
	 * @return the firstName
	 */
	public abstract String getFirstName();

	/**
	 * @return the lastName
	 */
	public abstract String getLastName();
	public abstract String getMothersMaidenName();

	/**
	 * @return the applicationID
	 */
	public abstract String getApplicationID();

	/**
	 * @return the SubscriberMDN
	 */
	public abstract String getSubscriberMDN();

	/**
	 * @return the DateOfBirth
	 */
	public abstract String getDateOfBirth();

	/**
	 * @return the AccountType
	 */
	public abstract String getAccountType();

	public abstract KeyParameter getKeyParameter();
	public abstract void setKeyParameter(KeyParameter keyParameter);
	
	public abstract boolean getIsHttps();
	public abstract void setIsHttps(boolean isHttps);
	
	/**
	 * 
	 * @return Biller Code
	 */
	public String getBillerCode();
	
	/**
	 * 
	 * @return Bill No.
	 */
	public String getBillNo();
	
	public String getCompanyID();
	
	/**
	 * @return the destBankCode
	 */
	public String getDestBankCode();
	
	/**
	 * @return the destAccountNumber
	 */
	public String getDestAccountNumber();
	
	/**
	 * @return the secreteCode
	 */
	public String getSecreteCode();
	
	public String getAppOS();
	
	public String getAppType();
	
	public String getAppVersion();
	
	public String getDestinationBankAccountNo();
	
	public abstract String getKycType();

	public abstract String getCity();

	public abstract String getNextOfKin();

	public abstract String getNextOfKinNo();
	
	public abstract String getEmail();

	public abstract String getPlotNo();
	
	public abstract String getStreetAddress();

	public abstract String getRegionName();

	public abstract String getCountry();

	public abstract String getIdType();

	public abstract String getIdNumber();

	public abstract String getDateOfExpiry();

	public abstract String getAddressProof();

	public abstract String getBirthPlace();

	public abstract String getNationality();

	public abstract String getCompanyName();

	public abstract String getSubscriberMobileCompany();
	
	public abstract String getCertOfIncorp();	
	
	public abstract String getLanguage();
	
	public abstract String getNotificationMethod();
	
	public abstract String getInstitutionID();
	
	//tarun
	
	public abstract String getOnBehalfOfMDN();
	
	public abstract String getCategory();

	public abstract String getVersion();
	
	public abstract boolean getApprovalRequired();
	
	public abstract String getBankAccountType();
	
	public abstract String getCardPAN();
	
	public abstract String getCardAlias();
	
	public abstract String getAuthorizingFirstName();
	
	public abstract String getAuthorizingLastName();
	
	public abstract String getAuthorizingIdNumber();
	
	public abstract String getApprovalComments();
	
	public abstract String getNarration();
	
	public abstract String getBenOpCode();
	
	public abstract String getDescription();
	
	public abstract String getMFATransaction();
	
	public abstract String getMfaOtp();
	
	public abstract String getPageNumber();
	
	public abstract String getNumRecords();
	
	public abstract String getNewEmail();
	
	public abstract String getConfirmEmail();
	
	public abstract String getNickname();
	
	public abstract String getOtherMdn();
	
	public String getTransID();
	
	public abstract String getParentTransID();
	
	public abstract String getFavoriteCategoryID();
	
	public abstract String getFavoriteLabel();
	
	public abstract String getFavoriteValue();
	
	public abstract String getFavoriteCode();
	
	public abstract String	getPartnerType();
	public abstract String	getTradeName();
	public abstract String	getPostalCode();
	public abstract String	getUserName();
	public abstract String	getOutletClasification();
	public abstract String	getFranchisePhoneNumber();
	public abstract String	getFaxNumber();
	public abstract String	getTypeOfOrganization();
	public abstract String	getWebSite();
	public abstract String	getIndustryClassification();
	public abstract String	getNumberOfOutlets();
	public abstract String	getYearEstablished();
	public abstract String	getOutletAddressLine1();
	public abstract String	getOutletAddressLine2();
	public abstract String	getOutletAddressCity();
	public abstract String	getOutletAddressState();
	public abstract String	getOutletAddressZipcode();
	public abstract String	getOutletAddressCountry();
	public abstract String	getAuthorizedRepresentative();
	public abstract String	getRepresentativeName();
	public abstract String	getDesignation();
	public abstract String	getAuthorizedFaxNumber();
	public abstract String  getPaymentMode();
	public abstract String  getFromDate();
	public abstract String  getToDate();
	public abstract String getAddressLine1();
	public abstract String getZipCode();
	public abstract String getState();
	public abstract String getMerchantData();
	public abstract String getDenomCode();
	public abstract String getNominalAmount();
	public abstract String getUserAPIKey();
	public abstract String getSctlId();

	
	public String getDiscountAmount();
	public String getLoyalityName();
	public String getDiscountType();
	public String getNumberOfCoupons();
	
	public String getKtpDocument();
	public String getSubscriberFormDocument();
	public String getSupportingDocument();
	public String getKtpId();
	public String getKtpValidUntil();
	public String getKtpLifetime();
	public String getKtpLine1();
	public String getKtpLine2();
	public String getKtpCity();
	public String getKtpState();
	public String getKtpCountry();
	public String getKtpZipCode();
	public String getKtpRegionName();
	public String getDomesticIdentity();
	public String getWork();
	public String getIncome();
	public String getGoalOfOpeningAccount();
	public String getSourceOfFunds();
	public String getTransactionId();
	
	public String getRT();	
	public String getRW();
	public String getKtpRW();
	public String getKtpRT();
}
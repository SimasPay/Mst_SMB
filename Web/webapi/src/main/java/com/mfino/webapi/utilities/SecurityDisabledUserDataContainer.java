/*
2 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.webapi.utilities;

import org.bouncycastle.crypto.params.KeyParameter;


/**
 * 
 * @author karthik
 */
public class SecurityDisabledUserDataContainer implements IUserDataContainer {

	private ReceivedUserDataContainer	container;

	private SecurityDisabledUserDataContainer(ReceivedUserDataContainer container) {
		this.container = container;
	}

	public static IUserDataContainer createSecurityDisabledUserDataContainer(ReceivedUserDataContainer container) {
		IUserDataContainer sac = new SecurityDisabledUserDataContainer(container);
		return sac;
	}
	
	private boolean isHttpsRequest;


//	private String	sourcePin;
//	private String	Amount;
//	private String	TransferId;
//	private String	ParentTxnId;
//	private String	SecretAnswer;
//	private String	OldPin;
//	private String	NewPin;
//	private String	ConfirmPin;
//	private String	OTP;
//	private String	TransactionID;
//	private String	Transactioncharges;
//	private String	creditAmount;
//	private String	debitAmount;
//	private String	DateOfBirth;
//	private String	AccountType;
//	private String	agentCode;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getChannelId()
	 */
	@Override
	public String getChannelId() {
		return container.getChannelId();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getSourceMessage()
	 */
	@Override
	public String getSourceMessage() {
		return container.getSourceMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getAgentCode()
	 */
	@Override
	public String getAgentCode() {
		return container.getAgentCode();
	}

	/**
	 * @return the container
	 */
	public ReceivedUserDataContainer getContainer() {
		return container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getSourceMdn()
	 */
	@Override
	public String getSourceMdn() {
		return container.getSourceMdn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getDestinationMdn()
	 */
	@Override
	public String getDestinationMdn() {
		return container.getDestinationMdn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getSourcePin()
	 */
	@Override
	public String getSourcePin() {
		return this.container.getSourcePin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getAmount()
	 */
	@Override
	public String getAmount() {
		return this.container.getAmount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getTransferId()
	 */
	@Override
	public String getTransferId() {
		return this.container.getTransferId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getParentTxnId()
	 */
	@Override
	public String getParentTxnId() {
		return this.container.getParentTxnId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getSecretAnswer()
	 */
	@Override
	public String getSecretAnswer() {
		return this.container.getSecretAnswer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getOldPin()
	 */
	@Override
	public String getOldPin() {
		return this.container.getOldPin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getNewPin()
	 */
	@Override
	public String getNewPin() {
		return this.container.getNewPin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getConfirmPin()
	 */
	@Override
	public String getConfirmPin() {
		return this.container.getConfirmPin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getOTP()
	 */
	@Override
	public String getOTP() {
		return this.container.getOTP();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getTransactionID()
	 */
	@Override
	public String getTransactionID() {
		return this.container.getTransactionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfino.webapi.utilities.IUserDataContainer#getTransactioncharges()
	 */
	@Override
	public String getTransactioncharges() {
		return this.container.getTransactioncharges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getCreditAmount()
	 */
	@Override
	public String getCreditAmount() {
		return this.container.getCreditAmount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getDebitAmount()
	 */
	@Override
	public String getDebitAmount() {
		return this.container.getDebitAmount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfino.webapi.utilities.IUserDataContainer#getAuthenticationString()
	 */
	@Override
	public String getAuthenticationString() {
		return container.getAuthenticationString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getSalt()
	 */
	@Override
	public String getSalt() {
		return container.getSalt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getConfirmed()
	 */
	@Override
	public String getConfirmed() {
		return container.getConfirmed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getSourcePocketCode()
	 */
	@Override
	public String getSourcePocketCode() {
		return container.getSourcePocketCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfino.webapi.utilities.IUserDataContainer#getDestinationPocketCode()
	 */
	@Override
	public String getDestinationPocketCode() {
		return container.getDestinationPocketCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getServiceName()
	 */
	@Override
	public String getServiceName() {
		return container.getServiceName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getTransactionName()
	 */
	@Override
	public String getTransactionName() {
		return container.getTransactionName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getPartnerCode()
	 */
	@Override
	public String getPartnerCode() {
		return container.getPartnerCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getFirstName()
	 */
	@Override
	public String getFirstName() {
		return container.getFirstName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getLastName()
	 */
	@Override
	public String getLastName() {
		return container.getLastName();
	}
	public String getMothersMaidenName() {
		return container.getMothersMaidenName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getApplicationID()
	 */
	@Override
	public String getApplicationID() {
		return container.getApplicationID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getSubscriberMDN()
	 */
	@Override
	public String getSubscriberMDN() {
		return container.getSubscriberMDN();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getDateOfBirth()
	 */
	@Override
	public String getDateOfBirth() {
		return this.container.getDateOfBirth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.webapi.utilities.IUserDataContainer#getAccountType()
	 */
	@Override
	public String getAccountType() {
		return this.container.getAccountType();
	}

	@Override
    public KeyParameter getKeyParameter() {
		return null;
		
	}

	@Override
    public void setKeyParameter(KeyParameter keyParameter) {
		
    }
	
	@Override
    public String getActivationConfirmPin() {
		return container.getActivationConfirmPin();
	}

	@Override
    public String getActivationNewPin() {
		return this.container.getActivationNewPin();
	}

	@Override
    public boolean getIsHttps() {
	    return isHttpsRequest;
    }
	
	@Override
	public void setIsHttps(boolean isHttps) {
		this.isHttpsRequest = isHttps;
	}

	@Override
	public String getBillerCode() {
		return container.getBillerCode();
	}

	@Override
	public String getBillNo() {
		return container.getBillNo();
	}

	@Override
	public String getCompanyID() {
		return container.getCompanyID();
	}

	@Override
	public String getDestBankCode() {
		return container.getDestBankCode();
	}

	@Override
	public String getDestAccountNumber() {
		return container.getDestAccountNumber();
	}
	
	@Override
	public String getSecreteCode() {
		return this.container.getSecreteCode();
	}

	@Override
    public String getAppOS() {
		return this.container.getAppOS();
	}

	@Override
    public String getAppType() {
		return this.container.getAppType();
	}

	@Override
    public String getAppVersion() {
		return this.container.getAppVersion();
	}

	@Override
	public String getDestinationBankAccountNo() {
		return this.container.getDestinationBankAccountNo();
	}

	@Override
	public String getKycType() {
		return this.container.getKycType();
	}

	@Override
	public String getCity() {
		return this.container.getCity();
	}

	@Override
	public String getNextOfKin() {
		return this.container.getNextOfKin();
	}

	@Override
	public String getNextOfKinNo() {
		return this.container.getNextOfKinNo();
	}

	@Override
	public String getEmail() {
		return this.container.getEmail();
	}

	@Override
	public String getPlotNo() {
		return this.container.getPlotNo();
	}

	@Override
	public String getStreetAddress() {
		return this.container.getStreetAddress();
	}

	@Override
	public String getRegionName() {
		return this.container.getRegionName();
	}

	@Override
	public String getCountry() {
		return this.container.getCountry();
	}

	@Override
	public String getIdType() {
		return this.container.getIdType();
	}

	@Override
	public String getIdNumber() {
		return this.container.getIdNumber();
	}

	@Override
	public String getDateOfExpiry() {
		return this.container.getDateOfExpiry();
	}

	@Override
	public String getAddressProof() {
		return this.container.getAddressProof();
	}

	@Override
	public String getBirthPlace() {
		return this.container.getBirthPlace();
	}

	@Override
	public String getNationality() {
		return this.container.getNationality();
	}

	@Override
	public String getCompanyName() {
		return this.container.getCompanyName();
	}

	@Override
	public String getSubscriberMobileCompany() {
		return this.container.getSubscriberMobileCompany();
	}

	@Override
	public String getCertOfIncorp() {
		return this.container.getCertOfIncorp();
	}

	@Override
	public String getLanguage() {
		return this.container.getLanguage();
	}

	@Override
	public String getNotificationMethod() {
		return this.container.getNotificationMethod();
	}
	public String getInstitutionID() {
    	return container.getInstitutionID();
    }

	@Override
	public boolean getApprovalRequired() {
		// TODO Auto-generated method stub
		return this.container.getApprovalRequired();
	}

	@Override
	public String getBankAccountType() {
		// TODO Auto-generated method stub
		return this.container.getBankAccountType();
	}

	@Override
	public String getCardPAN() {
		// TODO Auto-generated method stub
		return this.container.getCardPAN();
	}

	@Override
	public String getAuthorizingFirstName() {
		// TODO Auto-generated method stub
		return this.container.getAuthorizingFirstName();
	}

	@Override
	public String getAuthorizingLastName() {
		// TODO Auto-generated method stub
		return this.container.getAuthorizingLastName();
	}

	@Override
	public String getAuthorizingIdNumber() {
		// TODO Auto-generated method stub
		return this.container.getAuthorizingIdNumber();
	}

	@Override
	public String getApprovalComments() {
		// TODO Auto-generated method stub
		return this.container.getApprovalComments();
	}

	@Override
	public String getOnBehalfOfMDN() {
		// TODO Auto-generated method stub
		return this.container.getOnBehalfOfMDN();
	}
	
	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return this.container.getCategory();
	}
	
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return this.container.getVersion();
	}

	@Override
	public String getNarration() {
		// TODO Auto-generated method stub
		return this.container.getNarration();
	}
	
	@Override
	public String getBenOpCode() {
		// TODO Auto-generated method stub
		return this.container.getBenOpCode();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return this.container.getDescription();
	}

	@Override
	public String getMFATransaction() {
		// TODO Auto-generated method stub
		return this.container.getMfaTransaction();
	}

	@Override
	public String getMfaOtp() {
		// TODO Auto-generated method stub
		return this.container.getMfaOtp();
	}

	@Override
	public String getPageNumber() {
		return this.container.getPageNumber();
	}

	@Override
	public String getNumRecords() {
		return this.container.getNumRecords();
	}
	
	@Override
	public String getTransID() {
		return this.container.getTransID();
	}

	@Override
	public String getConfirmEmail() {
		// TODO Auto-generated method stub
		return this.container.getConfirmEmail();
	}

	@Override
	public String getNewEmail() {
		// TODO Auto-generated method stub
		return this.container.getNewEmail();
	}

	@Override
	public String getNickname() {
		// TODO Auto-generated method stub
		return this.container.getNickname();
	}

	@Override
	public String getOtherMdn() {
		return this.container.getOtherMdn();
	}

	@Override
	public String getParentTransID() {
		return this.container.getParentTransID();
	}
	@Override
	public String getCardAlias() {
		return this.container.getCardAlias();
	}

	@Override
	public String getFavoriteCategoryID() {
		return this.container.getFavoriteCategoryID();
	}

	@Override
	public String getFavoriteLabel() {
		return this.container.getFavoriteLabel();
	}

	@Override
	public String getFavoriteValue() {
		return this.container.getFavoriteValue();
	}
	
	@Override
	public String getFavoriteCode() {
		return this.container.getFavoriteCode();
	}

	@Override
	public String getPartnerType() {
		return this.container.getPartnerType();
		
	}

	@Override
	public String getTradeName() {
		return this.container.getTradeName();		
	}

	@Override
	public String getPostalCode() {
		return this.container.getPostalCode();		
	}

	@Override
	public String getUserName() {
		return this.container.getUserName();		
	}

	@Override
	public String getOutletClasification() {
		return this.container.getOutletClasification();		
	}

	@Override
	public String getFranchisePhoneNumber() {
		return this.container.getFranchisePhoneNumber();		
	}

	@Override
	public String getFaxNumber() {
		return this.container.getFaxNumber();		
	}

	@Override
	public String getTypeOfOrganization() {
		return this.container.getTypeOfOrganization();		
	}

	@Override
	public String getWebSite() {
		return this.container.getWebSite();		
	}

	@Override
	public String getIndustryClassification() {
		return this.container.getIndustryClassification();		
	}

	@Override
	public String getNumberOfOutlets() {
		return this.container.getNumberOfOutlets();		
	}

	@Override
	public String getYearEstablished() {
		return this.container.getYearEstablished();		
	}

	@Override
	public String getOutletAddressLine1() {
		return this.container.getOutletAddressLine1();
		
	}

	@Override
	public String getOutletAddressLine2() {
		return this.container.getOutletAddressLine2();		
	}

	@Override
	public String getOutletAddressCity() {
		return this.container.getOutletAddressCity();		
	}

	@Override
	public String getOutletAddressState() {
		return this.container.getOutletAddressState();		
	}

	@Override
	public String getOutletAddressZipcode() {
		return this.container.getOutletAddressZipcode();		
	}

	@Override
	public String getOutletAddressCountry() {
		return this.container.getOutletAddressCountry();		
	}

	@Override
	public String getAuthorizedRepresentative() {
		return this.container.getAuthorizedRepresentative();		
	}

	@Override
	public String getRepresentativeName() {
		return this.container.getRepresentativeName();
	}

	@Override
	public String getDesignation() {
		return this.container.getDesignation();		
	}

	@Override
	public String getAuthorizedFaxNumber() {
		return this.container.getAuthorizedFaxNumber();		
	}
	
	@Override
	public String getPaymentMode() {
		return this.container.getPaymentMode();		
	}

	@Override
	public String getFromDate() {
		return this.container.getFromDate();
	}

	@Override
	public String getToDate() {
		return this.container.getToDate();
	}
	
	@Override
	public String getAddressLine1() {
		return this.container.getAddressLine1();
	}
	
	@Override
	public String getZipCode() {
		return this.container.getZipCode();
	}
	
	@Override
	public String getState() {
		return this.container.getState();
	}
	
	@Override
	public String getMerchantData() {
		return this.container.getMerchantData();
	}
	
	@Override
	public String getDenomCode() {
		return this.container.getDenomCode();
	}
	
	@Override
	public String getNominalAmount() {
		return this.container.getNominalAmount();
	}

	@Override
	public String getUserAPIKey() {
		// TODO Auto-generated method stub
		return this.container.getUserAPIKey();
	}

	@Override
	public String getSctlId() {
		// TODO Auto-generated method stub
		return this.container.getSctlId();
	}
}

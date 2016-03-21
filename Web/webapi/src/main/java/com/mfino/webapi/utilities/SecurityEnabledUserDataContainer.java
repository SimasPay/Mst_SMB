/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.webapi.utilities;

import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.crypto.CryptographyService;


/**
 *
 * @author karthik
 */
public class SecurityEnabledUserDataContainer implements IUserDataContainer {

    private ReceivedUserDataContainer container;
    private KeyParameter keyParameter;
    
    private Logger log = LoggerFactory.getLogger(getClass());

    private SecurityEnabledUserDataContainer(ReceivedUserDataContainer container,KeyParameter kp) {
        this.container = container;
        this.keyParameter = kp;
    }

    public static IUserDataContainer createSecurityEnabledUserDataContainer(ReceivedUserDataContainer container,KeyParameter keyParameter) {
        IUserDataContainer sac = new SecurityEnabledUserDataContainer(container,keyParameter);
        return sac;
    }

	/* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getChannelId()
     */
	@Override
    public String getChannelId() {
    	return container.getChannelId();
    }

	/* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getSourceMessage()
     */
	@Override
    public String getSourceMessage() {
    	return container.getSourceMessage();
    }
    private String sourcePin;
    private String Amount;
    private String TransferId;
    private String ParentTxnId;
    private String SecretAnswer;
    private String OldPin;
    private String NewPin;
    private String ConfirmPin;
    private String TransactionID;
    private String Transactioncharges;
    private String creditAmount;
    private String debitAmount;
    private String DateOfBirth;
    private String AccountType;
    private String secreteCode;
    private String language;
    private String notificationMethod;
    private boolean isHttpsRequest;
    
    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getSourceMdn()
     */
    @Override
    public String getSourceMdn() {
        return container.getSourceMdn();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getDestinationMdn()
     */
    @Override
    public String getDestinationMdn() {
        return container.getDestinationMdn();
    }

    private String decryptedString(String str) {
        if (str == null) {
            return null;
        }
        try {
        	
        	byte[] strBytes = CryptographyService.hexToBin(str.toCharArray());
        	byte[] strOut = CryptographyService.decryptWithAES(keyParameter, strBytes);
        	return new String(strOut,GeneralConstants.UTF_8);
//            return new String(CryptographyService.decryptWithAES(keyParameter, CryptographyService.hexToBin(str.toCharArray())),GeneralConstants.UTF_8);
        } catch (Exception ex) {
            log.error("Error during decryption", ex);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getSourcePin()
     */
    @Override
    public String getSourcePin() {
        if(this.sourcePin==null)        
            this.sourcePin = decryptedString(container.getSourcePin());;
        return this.sourcePin;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getAmount()
     */
    @Override
    public String getAmount() {
        
        if(this.Amount==null)        
            this.Amount = decryptedString(container.getAmount());;
        return this.Amount;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getTransferId()
     */
    @Override
    public String getTransferId() {
        if(this.TransferId==null)        
            this.TransferId = decryptedString(container.getTransferId());;
        return this.TransferId;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getParentTxnId()
     */
    @Override
    public String getParentTxnId() {
        if(this.ParentTxnId==null)        
            this.ParentTxnId = decryptedString(container.getParentTxnId());;
        return this.ParentTxnId;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getSecretAnswer()
     */
    @Override
    public String getSecretAnswer() {
        if(this.SecretAnswer==null)        
            this.SecretAnswer = decryptedString(container.getSecretAnswer());;
        return this.SecretAnswer;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getOldPin()
     */
    @Override
    public String getOldPin() {
        
        if(this.OldPin==null)        
            this.OldPin = decryptedString(container.getOldPin());;
        return this.OldPin;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getNewPin()
     */
    @Override
    public String getNewPin() {
        if(this.NewPin==null)        
            this.NewPin = decryptedString(container.getNewPin());;
        return this.NewPin;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getConfirmPin()
     */
    @Override
    public String getConfirmPin() {
        if(this.ConfirmPin==null)        
            this.ConfirmPin = decryptedString(container.getConfirmPin());;
        return this.ConfirmPin;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getOTP()
     */
    @Override
    public String getOTP() {
        return this.container.getOTP();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getTransactionID()
     */
    @Override
    public String getTransactionID() {
    if(this.TransactionID==null)        
            this.TransactionID = decryptedString(container.getTransactionID());
        return this.TransactionID;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getTransactioncharges()
     */
    @Override
    public String getTransactioncharges() {
        if(this.Transactioncharges==null)        
            this.Transactioncharges = decryptedString(container.getTransactioncharges());;
        return this.Transactioncharges;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getCreditAmount()
     */
    @Override
    public String getCreditAmount() {
        if(this.creditAmount==null)        
            this.creditAmount= decryptedString(container.getCreditAmount());;
        return this.creditAmount;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getDebitAmount()
     */
    @Override
    public String getDebitAmount() {
        if(this.debitAmount==null)        
            this.debitAmount = decryptedString(container.getDebitAmount());;
        return this.debitAmount;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getAuthenticationString()
     */
    @Override
    public String getAuthenticationString() {
        return container.getAuthenticationString();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getSalt()
     */
    @Override
    public String getSalt() {
        return container.getSalt();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getConfirmed()
     */
    @Override
    public String getConfirmed() {
        return container.getConfirmed();
    }
    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getSourcePocketCode()
     */
    @Override
    public String getSourcePocketCode() {
        return container.getSourcePocketCode();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getDestinationPocketCode()
     */
    @Override
    public String getDestinationPocketCode() {
        return container.getDestinationPocketCode();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getServiceName()
     */
    @Override
    public String getServiceName() {
        return container.getServiceName();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getTransactionName()
     */
    @Override
    public String getTransactionName() {
        return container.getTransactionName();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getPartnerCode()
     */
    @Override
    public String getPartnerCode() {
        return container.getPartnerCode();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getFirstName()
     */
    @Override
    public String getFirstName() {
        return container.getFirstName();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getLastName()
     */
    @Override
    public String getLastName() {
        return container.getLastName();
    }
    public String getMothersMaidenName() {
        return container.getMothersMaidenName();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getApplicationID()
     */
    @Override
    public String getApplicationID() {
        return container.getApplicationID();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getSubscriberMDN()
     */
    @Override
    public String getSubscriberMDN() {
        return container.getSubscriberMDN();
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getDateOfBirth()
     */
    @Override
    public String getDateOfBirth() {
        if(this.DateOfBirth==null)        
            this.DateOfBirth = decryptedString(container.getDateOfBirth());;
        return this.DateOfBirth;
    }

    /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getAccountType()
     */
    @Override
    public String getAccountType() {
        if(this.AccountType==null)        
            this.AccountType = decryptedString(container.getAccountType());;
        return this.AccountType;
    }

	@Override
    public KeyParameter getKeyParameter() {
		return this.keyParameter;
	}

	@Override
    public void setKeyParameter(KeyParameter keyParameter) {
		this.keyParameter = keyParameter;
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
	
	 /* (non-Javadoc)
     * @see com.mfino.webapi.utilities.IUserDataContainer#getNewPin()
     */
    @Override
    public String getSecreteCode() {
        if(this.secreteCode==null)        
            this.secreteCode = decryptedString(container.getSecreteCode());;
        return this.secreteCode;
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
		 if(this.language==null)        
	            this.language= decryptedString(container.getLanguage());;
	        return this.language;
	}

	@Override
	public String getNotificationMethod() {
		 if(this.notificationMethod==null)        
	            this.notificationMethod= decryptedString(container.getNotificationMethod());;
	        return this.notificationMethod;
	}

	 public String getInstitutionID() {
	    	return container.getInstitutionID();
	    }
	 
	 public boolean getApprovalRequired(){
		 	return this.container.getApprovalRequired();
	 }
	 
	 public String getAuthorizingFirstName(){
		 	return this.container.getAuthorizingFirstName();
	 }
	 public String getAuthorizingLastName(){
		 	return this.container.getAuthorizingLastName();
	 }
	 
	 public String getCardPAN(){
		 	return this.container.getCardPAN();
	 }
	 
	 public String getCardAlias(){
		 	return this.container.getCardAlias();
	 }
	 
	 public String getBankAccountType(){
		 	return this.container.getBankAccountType();
	 }
	 
	 public String getAuthorizingIdNumber(){
		 	return this.container.getAuthorizingIdNumber();
	 }
	 
	 public String getApprovalComments(){
		 	return this.container.getApprovalComments();
	 }

		@Override
		public String getOnBehalfOfMDN() {
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
		public String getDescription() {
			// TODO Auto-generated method stub
			return this.container.getDescription();
		}
	 
	 public String getNarration(){
		 	return this.container.getNarration();
	 }
	 
	 public String getBenOpCode(){
		 	return this.container.getBenOpCode();
	 }

	@Override
	public String getMFATransaction() {
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
	public String getNewEmail() {
		// TODO Auto-generated method stub
		return this.container.getNewEmail();
	}

	@Override
	public String getConfirmEmail() {
		// TODO Auto-generated method stub
		return this.container.getConfirmEmail();
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
	public String getTransID() {
		return this.container.getTransID();
	}

	@Override
	public String getParentTransID() {
		return this.container.getParentTransID();
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

	@Override
	public String getDiscountAmount() {
		// TODO Auto-generated method stub
		return this.container.getDiscountAmount();
	}

	@Override
	public String getLoyalityName() {
		// TODO Auto-generated method stub
		return this.container.getLoyalityName();
	}

	@Override
	public String getDiscountType() {
		// TODO Auto-generated method stub
		return this.container.getDiscountType();
	}

	@Override
	public String getNumberOfCoupons() {
		// TODO Auto-generated method stub
		return this.container.getNumberOfCoupons();
	}

	@Override
	public String getTippingAmount() {
		// TODO Auto-generated method stub
		return this.container.getTippingAmount();
	}

	@Override
	public String getPointsRedeemed() {
		// TODO Auto-generated method stub
		return this.container.getPointsRedeemed();
	}

	@Override
	public String getAmountRedeemed() {
		// TODO Auto-generated method stub
		return this.container.getAmountRedeemed();
	}
}

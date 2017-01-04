/**
 * 
 */
package com.mfino.transactionapi.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.crypto.CryptographyService;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.IntegrationPartnerMap;
import com.mfino.domain.SubscriberMdn;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.service.MFAService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.account.AgentActivationHandler;
import com.mfino.transactionapi.handlers.account.ExistingSubscriberReactivationHandler;
import com.mfino.transactionapi.handlers.account.FavoriteHandler;
import com.mfino.transactionapi.handlers.account.GenerateFavoriteJSONHandler;
import com.mfino.transactionapi.handlers.account.GetPromoImageHandler;
import com.mfino.transactionapi.handlers.account.GetPublicKeyHandler;
import com.mfino.transactionapi.handlers.account.GetRegistrationMediumHandler;
import com.mfino.transactionapi.handlers.account.GetUserAPIKeyHandler;
import com.mfino.transactionapi.handlers.account.KYCUpgradeHandler;
import com.mfino.transactionapi.handlers.account.MFAExistingSubscriberReactivationHandler;
import com.mfino.transactionapi.handlers.account.MFASubscriberActivationHandler;
import com.mfino.transactionapi.handlers.account.MdnValidationForForgotPINHandler;
import com.mfino.transactionapi.handlers.account.PartnerRegistrationHandler;
import com.mfino.transactionapi.handlers.account.PendingSettlementsForPartnerHandler;
import com.mfino.transactionapi.handlers.account.ResendOtp;
import com.mfino.transactionapi.handlers.account.SubscriberActivationHandler;
import com.mfino.transactionapi.handlers.account.SubscriberClosingHandler;
import com.mfino.transactionapi.handlers.account.SubscriberClosingInquiryHandler;
import com.mfino.transactionapi.handlers.account.TransactionStatusHandler;
import com.mfino.transactionapi.handlers.subscriber.ChangeEmailHandler;
import com.mfino.transactionapi.handlers.subscriber.ChangeNicknameHandler;
import com.mfino.transactionapi.handlers.subscriber.ChangeOtherMDNHandler;
import com.mfino.transactionapi.handlers.subscriber.ChangePinHandler;
import com.mfino.transactionapi.handlers.subscriber.ForgotPinHandler;
import com.mfino.transactionapi.handlers.subscriber.ForgotPinInquiryHandler;
import com.mfino.transactionapi.handlers.subscriber.GenerateOTPHandler;
import com.mfino.transactionapi.handlers.subscriber.KYCUpgradeInquiryHandler;
import com.mfino.transactionapi.handlers.subscriber.LoginHandler;
import com.mfino.transactionapi.handlers.subscriber.LogoutHandler;
import com.mfino.transactionapi.handlers.subscriber.MFAChangePinHandler;
import com.mfino.transactionapi.handlers.subscriber.ResetPinByOTPHandler;
import com.mfino.transactionapi.handlers.subscriber.SelfRegistrationHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberChangeSettingsHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberDetailsHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegistrationThroughWebHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegistrationWithActivationHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegularWithEmoneyHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegularWithEmoneyInquiryHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberStatusHandler;
import com.mfino.transactionapi.handlers.subscriber.UpdateProfileHandler;
import com.mfino.transactionapi.handlers.subscriber.ValidateOTPHandler;
import com.mfino.transactionapi.result.xmlresulttypes.XMLError;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.PublicKeyXMLResult;
import com.mfino.transactionapi.service.AccountAPIServices;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * Handles all Account Service related Transactions. Subscriber Activation Agent
 * Activation Transaction Status Change Pin
 * 
 * @author Bala Sunku
 * 
 */
@Service("AccountAPIServicesImpl")
public class AccountAPIServicesImpl  extends BaseAPIService implements AccountAPIServices{

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("ResendOtpImpl")
	private ResendOtp resendOtp;
	
	@Autowired
	@Qualifier("IntegrationPartnerMappingServiceImpl")
	private IntegrationPartnerMappingService integrationPartnerMappingService;
	
	@Autowired
	@Qualifier("LoginHandlerImpl")
	private LoginHandler loginHandler;
	
	@Autowired
	@Qualifier("LogoutHandlerImpl")
	private LogoutHandler logoutHandler;
	
	@Autowired
	@Qualifier("ChangePinHandlerImpl")
	private ChangePinHandler changePinHandler;
	
	@Autowired
	@Qualifier("MFAChangePinHandlerImpl")
	private MFAChangePinHandler mfaChangePinHandler;
	
	@Autowired
	@Qualifier("AgentActivationHandlerImpl")
	private AgentActivationHandler agentActivationHandler;
	
	@Autowired
	@Qualifier("TransactionStatusHandlerImpl")
	private TransactionStatusHandler transactionStatusHandlerImpl;
	
	@Autowired
	@Qualifier("SubscriberClosingInquiryHandlerImpl")
	SubscriberClosingInquiryHandler subscriberClosingInquiryHandler;
	
	@Autowired
	@Qualifier("SubscriberClosingHandlerImpl")
	SubscriberClosingHandler subscriberClosingHandler;
	
	@Autowired
	@Qualifier("SubscriberActivationHandlerImpl")
	private SubscriberActivationHandler subscriberActivationHandler;
	
	@Autowired
	@Qualifier("GetRegistrationMediumHandlerImpl")
	private GetRegistrationMediumHandler getRegistrationMediumHandler;
	
	@Autowired
	@Qualifier("MFASubscriberActivationHandlerImpl")
	private MFASubscriberActivationHandler mfaSubscriberActivationHandler;
	
	@Autowired
	@Qualifier("PendingSettlementsForPartnerHandlerImpl")
	private PendingSettlementsForPartnerHandler pendingSettlementsForPartnerHandler;
 
	@Autowired
	@Qualifier("SubscriberRegistrationThroughWebHandlerImpl")
	private SubscriberRegistrationThroughWebHandler subRegistrationThroughWebHandler;
	
	@Autowired
	@Qualifier("ExistingSubscriberReactivationHandlerImpl")
	private ExistingSubscriberReactivationHandler existingSubscriberReactivationHandler;
	
	@Autowired
	@Qualifier("SubscriberRegistrationWithActivationHandlerImpl")
	private SubscriberRegistrationWithActivationHandler subRegistrationWithActivationHandler;
	
	@Autowired
	@Qualifier("MFAExistingSubscriberReactivationHandlerImpl")
	private MFAExistingSubscriberReactivationHandler mfaExistingSubscriberReactivationHandler;
	
	
	@Autowired
	@Qualifier("SubscriberChangeSettingsHandlerImpl")
	private SubscriberChangeSettingsHandler subChangeSettingsHandler;
	
	@Autowired
	@Qualifier("SubscriberDetailsHandlerImpl")
	private SubscriberDetailsHandler subDetailsHandler;
	
	@Autowired
	@Qualifier("ResetPinByOTPHandlerImpl")
	private ResetPinByOTPHandler resetPinByOTPHandler;
	
	@Autowired
	@Qualifier("SubscriberStatusHandlerImpl")
	private SubscriberStatusHandler subStatusHandler;
	
	@Autowired
	@Qualifier("SelfRegistrationForNonKYCHandlerImpl")
	private SelfRegistrationHandler selfRegistrationHandler;
	
	@Autowired
	@Qualifier("TransactionRequestValidationServiceImpl")
	private TransactionRequestValidationService validationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("ChangeEmailHandlerImpl")
	private ChangeEmailHandler changeEmailHandler;
	
	@Autowired
	@Qualifier("ChangeNicknameHandlerImpl")
	private ChangeNicknameHandler changeNicknameHandler;
	
	@Autowired
	@Qualifier("ChangeOtherMDNHandlerImpl")
	private ChangeOtherMDNHandler changeOtherMDNHandler;
	
	@Autowired
	@Qualifier("ForgotPinInquiryHandlerImpl")
	private ForgotPinInquiryHandler forgotPinInquiryHandler;

	@Autowired
	@Qualifier("ForgotPinHandlerImpl")
	private ForgotPinHandler forgotPinHandler;

	@Autowired
	@Qualifier("FavoriteHandlerImpl")
	private FavoriteHandler favoriteHandler;
	
	@Autowired
	@Qualifier("GenerateFavoriteJSONHandlerImpl")
	private GenerateFavoriteJSONHandler generateFavoriteJSONHandler;

	@Autowired
	@Qualifier("PartnerRegistrationHandlerImpl")
	private PartnerRegistrationHandler partnerRegistrationHandler;
	
	@Autowired
	@Qualifier("GenerateOTPHandlerImpl")
	private GenerateOTPHandler generateOTPHandler;
	
	@Autowired
	@Qualifier("ValidateOTPHandlerImpl")
	private ValidateOTPHandler validateOTPHandler;
	
	@Autowired
	@Qualifier("KYCUpgradeInquiryHandlerImpl")
	private KYCUpgradeInquiryHandler kycUpgradeInquiryHandler;
	
	@Autowired
	@Qualifier("KYCUpgradeHandlerImpl")
	private KYCUpgradeHandler kycUpgradeHandler;

	@Autowired
	@Qualifier("GetUserAPIKeyHandlerImpl")
	private GetUserAPIKeyHandler getUserAPIKeyHandler;

	@Autowired
	@Qualifier("GetPromoImageHandlerImpl")
	private GetPromoImageHandler getPromoImageHandler;
	
	@Autowired
	@Qualifier("UpdateProfileHandlerImpl")
	private UpdateProfileHandler updateProfileHandler;	
	
	@Autowired
	@Qualifier("MdnValidationForForgotPINHandlerImpl")
	private MdnValidationForForgotPINHandler mdnValidationForForgotPINHandler;
	
	@Autowired
	@Qualifier("SubscriberRegularWithEmoneyInquiryHandlerImpl")
	private SubscriberRegularWithEmoneyInquiryHandler subscriberRegularWithEmoneyInquiryHandler;
	
	@Autowired
	@Qualifier("SubscriberRegularWithEmoneyHandlerImpl")
	private SubscriberRegularWithEmoneyHandler subscriberRegularWithEmoneyHandler;

	@Autowired
	@Qualifier("GetPublicKeyHandlerImpl")
	private GetPublicKeyHandler getPublicKeyHandler;
	
	public XMLResult handleRequest(TransactionDetails transactionDetails) throws InvalidDataException {
		
		XMLResult xmlResult = null;
		ChannelCode channelCode = transactionDetails.getCc();

		String transactionName = transactionDetails.getTransactionName();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);

		if (ServiceAndTransactionConstants.TRANSACTION_ACTIVATION.equals(transactionName)) {
			
			validationService.validateSubscriberActivationDetails(transactionDetails);
			String mfaTransaction = transactionDetails.getMfaTransaction();

			//if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_ACCOUNT, transactionName, channelCode.getID()) == true) {
				if(mfaTransaction != null
						&& (mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_INQUIRY) 
									|| mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_CONFIRM))){
					
					transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
					xmlResult = (XMLResult) mfaSubscriberActivationHandler.handle(transactionDetails);
				}
				else{
					log.info("mfaTransaction parameter is Invalid");
				}
			/*}
			else {
				xmlResult = (XMLResult) subscriberActivationHandler.handle(transactionDetails);
			}*/
		} else if (ServiceAndTransactionConstants.TRANSACTION_AGENTACTIVATION.equals(transactionName)) {
		
			validationService.validateSubscriberActivationDetails(transactionDetails);
    		xmlResult = (XMLResult) agentActivationHandler.handle(transactionDetails);
		
		} else if (ServiceAndTransactionConstants.TRANSACTION_TRANSACTIONSTATUS.equals(transactionName)) {
		
			validationService.validateTransactionStatusDetails(transactionDetails);
  			xmlResult = (XMLResult) transactionStatusHandlerImpl.handle(transactionDetails);
	
		} else if (ServiceAndTransactionConstants.TRANSACTION_CHANGEPIN.equals(transactionName)) {
			
			validationService.validateChangePinDetails(transactionDetails);
			String mfaTransaction = transactionDetails.getMfaTransaction();

			if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_ACCOUNT, transactionName, channelCode.getId().longValue()) == true){
				if(mfaTransaction != null && (mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_INQUIRY) ||
						mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_CONFIRM))){
					xmlResult = (XMLResult) mfaChangePinHandler.handle(transactionDetails);
				}
				else{
					log.info("mfaTransaction parameter is Invalid");
				}
			}
			else {
				
				xmlResult = (XMLResult) changePinHandler.handle(transactionDetails);
			}
				
		} else if (ApiConstants.TRANSACTION_LOGIN.equalsIgnoreCase(transactionName)) {

			validationService.validateLoginDetails(transactionDetails);
			if (StringUtils.isNotBlank(transactionDetails.getInstitutionID())) {
				IntegrationPartnerMap integrationPartnerMapping = integrationPartnerMappingService.getByInstitutionID(transactionDetails.getInstitutionID());
				if(integrationPartnerMapping!=null)
					transactionDetails.setIsAppTypeChkEnabled(integrationPartnerMapping.getIsapptypecheckenabled()==1?true:false);
			}

			xmlResult = (XMLResult) loginHandler.handle(transactionDetails);

		} else if (ApiConstants.TRANSACTION_LOGOUT.equalsIgnoreCase(transactionName)) {
		
			validationService.validateLogoutDetails(transactionDetails);
			xmlResult = (XMLResult) logoutHandler.handle(transactionDetails);

		} else if (ApiConstants.TRANSACTION_SUBSCRIBER_REGISTRATION_THROUGH_WEB.equalsIgnoreCase(transactionName)) {
		
			validationService.validateSubscriberRegistrationThroughWebDetails(transactionDetails);
			xmlResult = (XMLResult) subRegistrationThroughWebHandler.handle(transactionDetails);

		} else if (ApiConstants.TRANSACTION_SUBSCRIBERREGISTRATION.equalsIgnoreCase(transactionName)) {
			
			validationService.validateSubscriberRegistrationForNonKyc(transactionDetails);
			xmlResult = (XMLResult) selfRegistrationHandler.handle(transactionDetails);

		} else if (ApiConstants.TRANSACTION_REGISTRATION_WITH_ACTIVATION.equalsIgnoreCase(transactionName)) {
			if (checkIfActiveSubscribersReachedLimit()) {
				xmlResult = new XMLError();
				xmlResult.setLanguage(language);
				xmlResult.setTransactionTime(new Timestamp());
				xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_ActiveSubscribersReachedMaxLimit);
				return xmlResult;
			}
			validationService.validateSubscriberRegistrationWithActivation(transactionDetails);
			xmlResult = (XMLResult) subRegistrationWithActivationHandler.handle(transactionDetails);

		}
        else if (ServiceAndTransactionConstants.TRANSACTION_UPDATE_PROFILE.equalsIgnoreCase(transactionName)) {
            validationService.validateUpdateProfile(transactionDetails);
            xmlResult = (XMLResult) updateProfileHandler.handle(transactionDetails);
        }		
		else if (ApiConstants.TRANSACTION_GENERATE_OTP.equalsIgnoreCase(transactionName)) {
			validationService.validateGenerateOTPDetails(transactionDetails);
			xmlResult = (XMLResult) generateOTPHandler.handle(transactionDetails);
		}else if (ApiConstants.TRANSACTION_VALIDATE_OTP
				.equalsIgnoreCase(transactionName)) {
			validationService.validateOTPValidationDetails(transactionDetails);
			xmlResult = (XMLResult) validateOTPHandler.handle(transactionDetails);
		}else if (ApiConstants.TRANSACTION_REGISTRATION_WITH_ACTIVATION_HUB.equalsIgnoreCase(transactionName)) {
// Commented as there is no such requirement for HUB (Bala Sunku)			
//			if (checkIfActiveSubscribersReachedLimit()) {
//				xmlResult = new XMLError();
//				xmlResult.setLanguage(CmFinoFIX.Language_English);
//				xmlResult.setTransactionTime(new Timestamp());
//				xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_ActiveSubscribersReachedMaxLimit);
//				return xmlResult;
//			}
			validationService.validateSubscriberRegistrationWithActivationForHub(transactionDetails);
			xmlResult = (XMLResult) subRegistrationWithActivationHandler.handle(transactionDetails);
		}		
		else if (ServiceAndTransactionConstants.TRANSACTION_CHANGE_SETTINGS.equals(transactionName)) {
		
			validationService.validateChangeSettingDetails(transactionDetails);
			xmlResult = (XMLResult) subChangeSettingsHandler.handle(transactionDetails);
	
		} else if (ServiceAndTransactionConstants.TRANSACTION_PENDING_SETTLEMENTS_FOR_PARTNER.equals(transactionName)) {
			
			validationService.validateTransactionHistoryDetails(transactionDetails);
			xmlResult = (XMLResult) pendingSettlementsForPartnerHandler.handle(transactionDetails);
			
		}else if (ServiceAndTransactionConstants.TRANSACTION_REACTIVATION.equals(transactionName)) {
		
			validationService.validateSubscriberReactivationDetails(transactionDetails);
			Long parentTxnIdStr = transactionDetails.getParentTxnId();
			String mfaTransaction = transactionDetails.getMfaTransaction();
			
			if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_ACCOUNT, transactionName, channelCode.getId().longValue()) == true){
				if(mfaTransaction != null && (mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_INQUIRY) ||
								mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_CONFIRM))){
					long parentTrxnId = -1;
					if(parentTxnIdStr != null){
						parentTrxnId = parentTxnIdStr;
					}

					transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
					transactionDetails.setParentTxnId(parentTrxnId);

					xmlResult = (XMLResult) mfaExistingSubscriberReactivationHandler.handle(transactionDetails);	
 				}
				else{
					log.info("mfaTransaction parameter is Invalid");
				}
			}
			else{

 				xmlResult = (XMLResult) existingSubscriberReactivationHandler.handle(transactionDetails);
 				
			}
		}
		
		else if(ServiceAndTransactionConstants.TRANSACTION_GET_REGISTRATION_MEDIUM.equals(transactionName)){
  
			xmlResult = (XMLResult) getRegistrationMediumHandler.handle(transactionDetails);
			
		}else if (ServiceAndTransactionConstants.TRANSACTION_CHANGEEMAIL.equals(transactionName)) {
			validationService.validateChangeEmailDetails(transactionDetails);

			xmlResult = (XMLResult) changeEmailHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_CHANGENICKNAME.equals(transactionName)) {
			validationService.validateChangeNicknameDetails(transactionDetails);

			xmlResult = (XMLResult) changeNicknameHandler.handle(transactionDetails);
		
		}else if (ServiceAndTransactionConstants.TRANSACTION_CHANGEOTHERMDN.equals(transactionName)) {
			validationService.validateChangeOtherMDNDetails(transactionDetails);
			xmlResult = (XMLResult) changeOtherMDNHandler.handle(transactionDetails);
		
		}else if (ServiceAndTransactionConstants.TRANSACTION_FORGOTPIN_INQUIRY.equals(transactionName)) {			

			xmlResult = (XMLResult) forgotPinInquiryHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_FORGOTPIN.equals(transactionName)) {
			validationService.validateForgotPinDetails(transactionDetails);
			xmlResult = (XMLResult) forgotPinHandler.handle(transactionDetails);
			
		}else if(ServiceAndTransactionConstants.TRANSACTION_RESEND_OTP.equals(transactionName)){
			
			xmlResult = (XMLResult) resendOtp.handle(transactionDetails);

		}else if (ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBER_DETAILS.equals(transactionName)) {
		
			xmlResult = (XMLResult) subDetailsHandler.handle(transactionDetails);
        
		}else if (ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBER_STATUS.equals(transactionName)){
			
			validationService.validateSubscriberStatusDetails(transactionDetails);
			xmlResult = (XMLResult) subStatusHandler.handle(transactionDetails);
		
		}else if (ServiceAndTransactionConstants.TRANSACTION_RESETPIN_BY_OTP.equals(transactionName)) {
			
			validationService.validateResetPinByOTPDetails(transactionDetails);
			xmlResult = (XMLResult) resetPinByOTPHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_ADD_FAVORITE.equals(transactionName) || 
					ServiceAndTransactionConstants.TRANSACTION_EDIT_FAVORITE.equals(transactionName) ||
						ServiceAndTransactionConstants.TRANSACTION_DELETE_FAVORITE.equals(transactionName)) {
			validationService.validateFavoriteDetails(transactionDetails);
			xmlResult = (XMLResult) favoriteHandler.handle(transactionDetails);		
		}else if (ServiceAndTransactionConstants.TRANSACTION_GENERATE_FAVORITE_JSON.equals(transactionName)) {			
			validationService.validateFavoriteDetails(transactionDetails);
			xmlResult = (XMLResult) generateFavoriteJSONHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_PARTNER_REGISTRATION_THROUGH_API.equals(transactionName)) {
			
			validationService.validatePartnerRegistrationDetails(transactionDetails);
			xmlResult = (XMLResult) partnerRegistrationHandler.handle(transactionDetails);
		}else if(ServiceAndTransactionConstants.TRANSACTION_GET_PUBLIC_KEY.equals(transactionName)){
//			xmlResult= new PublicKeyXMLResult();
//		      // Encrypt the string using the public key
//			try{
//				log.info("Getting the public to send to the mobile app");
//				String[] publicKeyParams = CryptographyService.getPubKeyStrings();
//				log.info("Public key Modulus: "+publicKeyParams[0]+"\n Public key expo: "+publicKeyParams[1]);
//				xmlResult.setPublicKeyModulus(publicKeyParams[0]);
//				xmlResult.setPublicKeyExponent(publicKeyParams[1]);
//			}catch(Exception e){
//				log.error("Exception occured while sending the public key parameters");
//				e.printStackTrace();
//			}	
			
			validationService.validateAppOSAndVersion(transactionDetails);
			xmlResult = (XMLResult) getPublicKeyHandler.handle(transactionDetails);
		}
		else if(ServiceAndTransactionConstants.TRANSACTION_KYC_UPGRADE_INQUIRY.equals(transactionName)){
			xmlResult = (XMLResult)kycUpgradeInquiryHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_KYCUpgrade.equals(transactionName)) {
			validationService.validateKYCUpgrade(transactionDetails);
			xmlResult = (XMLResult) kycUpgradeHandler.handle(transactionDetails);			
		}else if(ServiceAndTransactionConstants.TRANSACTION_GET_USER_API_KEY.equals(transactionName)){
			xmlResult = (XMLResult) getUserAPIKeyHandler.handle(transactionDetails);
		}else if(ServiceAndTransactionConstants.TRANSACTION_GET_PROMO_IMAGE.equals(transactionName)){
			xmlResult = (XMLResult) getPromoImageHandler.handle(transactionDetails);
			
		}else if(ServiceAndTransactionConstants.TRANSACTION_MDN_VALIDATION_FOR_FORGOTPIN.equals(transactionName)){
			xmlResult = (XMLResult) mdnValidationForForgotPINHandler.handle(transactionDetails);
			
		}else if(ServiceAndTransactionConstants.TRANSACTION_SUB_REGULAR_WITH_EMONEY_INQUIRY.equals(transactionName)){
			xmlResult = (XMLResult) subscriberRegularWithEmoneyInquiryHandler.handle(transactionDetails);
			
		}else if(ServiceAndTransactionConstants.TRANSACTION_SUB_REGULAR_WITH_EMONEY.equals(transactionName)){
			xmlResult = (XMLResult) subscriberRegularWithEmoneyHandler.handle(transactionDetails);
		
		}else if (ServiceAndTransactionConstants.SUBSCRIBER_CLOSING_INQUIRY.equals(transactionName)) {
			
			/*
			 * Subscriber Closing Inquiry
			 */
			
			validationService.validateSubscriberClosingInquiryDetails(transactionDetails);
	  		xmlResult = (XMLResult) subscriberClosingInquiryHandler.handle(transactionDetails);
		
		}else if (ServiceAndTransactionConstants.SUBSCRIBER_CLOSING.equals(transactionName)) {
			
			/*
			 * Subscriber Closing
			 */
			
			validationService.validateSubscriberClosingDetails(transactionDetails);
  			xmlResult = (XMLResult) subscriberClosingHandler.handle(transactionDetails);
	
		}else {
			xmlResult = new XMLError();
			xmlResult.setLanguage(language);
			xmlResult.setTransactionTime(new Timestamp());
			xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
		}
		return xmlResult;
	}

	public boolean checkIfActiveSubscribersReachedLimit() {
		int subscribersLimit = systemParametersService.getInteger(SystemParameterKeys.MAX_NO_OF_ALLOWED_SUBSCRIBERS);
		if (subscribersLimit > 0) {
			SubscriberMdnQuery query = new SubscriberMdnQuery();
			query.setOnlySubscribers(true);
			query.setStatusIn(new Integer[] {
					CmFinoFIX.SubscriberStatus_Initialized,
					CmFinoFIX.SubscriberStatus_Active,
					CmFinoFIX.SubscriberStatus_Registered,
					CmFinoFIX.SubscriberStatus_Suspend,
					CmFinoFIX.SubscriberStatus_InActive,
					CmFinoFIX.SubscriberStatus_NotRegistered});
			List<SubscriberMdn> results =subscriberMdnService.getByQuery(query);
			
			if (results.size() >= subscribersLimit) {
				return true;
			}
		}
		return false;
	}
	public long getParentTxnId(String parentTxnIdStr) throws InvalidDataException { 
		long parentTrxnId = -1L;
		try {
			parentTrxnId = Long.parseLong(parentTxnIdStr);
		}
		catch (NumberFormatException ex) {
			log.error("Error parsing parent transaction id string", ex);
			throw new InvalidDataException("Invalid Amount", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_PARENTTXN_ID);
		}		
		return parentTrxnId;
	}

}

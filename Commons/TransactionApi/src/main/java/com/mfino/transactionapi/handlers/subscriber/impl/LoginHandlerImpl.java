package com.mfino.transactionapi.handlers.subscriber.impl;

import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.crypto.CryptographyService;
import com.mfino.crypto.KeyService;
import com.mfino.domain.ChannelSessionManagement;
import com.mfino.domain.Partner;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMWebApiLoginRequest;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.ChannelSessionManagementService;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.definition.MobileappVersionCheckerService;
import com.mfino.service.impl.appversionchecker.VersionCheckerFactory;
import com.mfino.transactionapi.handlers.subscriber.LoginHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.LoginXMLResult;
import com.mfino.transactionapi.service.AppTypeCheckService;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

@Service("LoginHandlerImpl")
public class LoginHandlerImpl extends FIXMessageHandler implements LoginHandler{

	private static Logger	log	= LoggerFactory.getLogger(LoginHandlerImpl.class);
	private boolean	      isHttps;
	private boolean App_Type;
	//get these from some where else
	@Autowired
	@Qualifier("ChannelSessionManagementServiceImpl")
	private ChannelSessionManagementService channelSessionManagementService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("MfinoUtilServiceImpl")
	private MfinoUtilService mfinoUtilService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public XMLResult handle(TransactionDetails transDetails) {
		CMWebApiLoginRequest request = new CMWebApiLoginRequest();
		request.setSourceMDN(transDetails.getSourceMDN());
		request.setWebApiSalt(transDetails.getSalt());
		request.setAuthMAC(transDetails.getAuthenticationString());
		request.setIsAppTypeCheckEnabled(transDetails.getIsAppTypeChkEnabled());
		isHttps = transDetails.isHttps();
		
		String Apptype=request.getAppType();
		log.info("Handling webapi login request");
		LoginXMLResult result = new LoginXMLResult();

		TransactionsLog tLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_WebApiLoginRequest, request.DumpFields());
		request.setTransactionID(tLog.getID());
		result.setTransactionID(tLog.getID());
		result.setTransactionTime(tLog.getTransactionTime());
		result.setSourceMessage(request);
		MobileappVersionCheckerService checkerService = VersionCheckerFactory.getInstance().getService(VersionCheckerFactory.VERSION_DB);
		result.setValidVersion(checkerService.isValidVersion(request));
		result.setNewAppURL(checkerService.checkForNewVersionOfMobileApp(request));
		
		if(result.isValidVersion()){

		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(request.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+request.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}
		
		
	//	addCompanyANDLanguageToResult(srcSubscriberMDN, result);

		String password = srcSubscriberMDN.getDigestedPIN();
		String authToken = srcSubscriberMDN.getAuthorizationToken();
		// for deployments which dont have this new code we need to use the digested pin.
		// also set this value for use later on 
		if(authToken==null||authToken.trim().equalsIgnoreCase(""))
		{
			authToken = password;
			//ATMregistration change
			//srcSubscriberMDN.setAuthorizationToken(password);
		}
		try {
//			String userPwd;
//			if (!isHttps) {
//				log.info("Not a https webapi request");
////				byte[] salt = CryptographyService.hexToBin(request.getWebApiSalt().toCharArray());
////				byte[] encryptedBytes = CryptographyService.hexToBin(request.getAuthMAC().toCharArray());
////				try {
////					byte[] decryptedBytes = CryptographyService.decryptWithPBE(encryptedBytes, password.toCharArray(), salt, GeneralConstants.PBE_ITERATION_COUNT);
////					String receivedZeroesString = new String(decryptedBytes, GeneralConstants.UTF_8);
////					log.info("comparing the decrypted ZEROES_STRING");
////					if (!GeneralConstants.ZEROES_STRING.equals(receivedZeroesString)) {
////						log.info("comparision of zeroes string failed. recalculating resitrictions, wrong pin counts");
////						recalculateWrongPinCounts(srcSubscriberMDN, result);
////						log.info("saving subscriber mdn after reseting wrong pin counts and restrictions");
////						subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
////						log.info("returning NotificationCode_WrongPINSpecified");
////						result.setNotificationCode(CmFinoFIX.NotificationCode_WrongPINSpecified);
////						return result;
////					}
////				}
////				catch (InvalidCipherTextException ex) {
////					log.info("INvalidCipherTextException thrown, decryption of ZEROES_STRING failed.recalculating resitrictions, wrong pin counts");
////					recalculateWrongPinCounts(srcSubscriberMDN, result);
////					log.info("saving subscriber mdn after reseting wrong pin counts and restrictions");
////					subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
////					log.info("returning NotificationCode_WrongPINSpecified");
////					result.setNotificationCode(CmFinoFIX.NotificationCode_WrongPINSpecified);
////					return result;
////				}
//				
//				/** 
//				 * Pin that is received is hashed so need to convert it to hashed pin 
//				 *with length same as pin lengh allowed on the system
//				*/
//				//String userPwd = MfinoUtil.convertPinForValidation(request.getAuthMAC(),SystemParametersUtil.getPinLength());
//				userPwd = CryptographyService.decryptWithPrivateKey(request.getAuthMAC());
//				//userPwd = new String(CryptographyService.generateSHA256Hash(subscriberMDN.getMDN(), userPwd));
//				//if (!password.equals(userPwd)) {
//				//done this change for introducing HSM for validation
//				log.info("validating pin");
//				if(!mfinoUtilService.validatePin(srcSubscriberMDN.getMDN(), userPwd, password))
//				{
//					log.info("invalid pin received in login request");
//					recalculateWrongPinCounts(srcSubscriberMDN, result);
//					log.info("saving subscriber mdn after reseting wrong pin counts and restrictions");
//					subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
//					log.info("returning NotificationCode_WrongPINSpecified");
//					result.setNotificationCode(CmFinoFIX.NotificationCode_WrongPINSpecified);
//					return result;
//				}
//			}
//			else {
//				log.info("https login webapi request received");
				
				/** 
				 * Pin that is received is hashed so need to convert it to hashed pin 
				 *with length same as pin lengh allowed on the system
				*/
				//String userPwd = MfinoUtil.convertPinForValidation(request.getAuthMAC(),SystemParametersUtil.getPinLength());
				String userPwd = CryptographyService.decryptWithPrivateKey(request.getAuthMAC());
				//userPwd = new String(CryptographyService.generateSHA256Hash(subscriberMDN.getMDN(), userPwd));
				//if (!password.equals(userPwd)) {
				//done this change for introducing HSM for validation
				log.info("validating pin");
				String pinValidationResponse = mfinoUtilService.validatePin(srcSubscriberMDN.getMDN(), userPwd, password, 
						systemParametersService.getPinLength());
				if(GeneralConstants.LOGIN_RESPONSE_FAILED.equals(pinValidationResponse))
				{
					log.info("invalid pin received in login request");
					recalculateWrongPinCounts(srcSubscriberMDN, result);
					log.info("saving subscriber mdn after reseting wrong pin counts and restrictions");
					subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
					log.info("returning NotificationCode_WrongPINSpecified");
					result.setNotificationCode(CmFinoFIX.NotificationCode_WrongPINSpecified);
					return result;
				}else if(GeneralConstants.LOGIN_RESPONSE_INTERNAL_ERROR.equals(pinValidationResponse))
				{
					log.info("Pin validation failed due to internal error");
					log.info("DONOT update wrong pin counts as the error is due to internal failure");
					result.setNotificationCode(CmFinoFIX.NotificationCode_InternalLoginError);
					return result;
				}
				
//			}
			if (StringUtils.isNotBlank(Apptype)) {
				if(request.getIsAppTypeCheckEnabled() == null || (request.getIsAppTypeCheckEnabled()!=null && request.getIsAppTypeCheckEnabled().booleanValue())) {
					App_Type = AppTypeCheckService.appTypeCheck(srcSubscriberMDN,Apptype);
					if(App_Type == false) {
						log.info("Subscriber type and app type  are different");
						result.setNotificationCode(CmFinoFIX.NotificationCode_AppTypeAndSubscriberTypeMismatch);
						return result;
					}
				}
			}
			
			log.info("generating salt, aes key");
			byte[] salt = CryptographyService.generateSalt();
			byte[] aesKey = KeyService.generateAESKey();
			String hexEncodedKey = new String(CryptographyService.binToHex(aesKey));

			log.info("recalculating channelsession management data");
			ChannelSessionManagement csm  = channelSessionManagementService.getChannelSessionManagemebtByMDNID(srcSubscriberMDN.getID());
			if (csm == null)
				csm = new ChannelSessionManagement();
			csm.setSubscriberMDNByMDNID(srcSubscriberMDN);
			csm.setCreatedBy("System");
			csm.setCreateTime(tLog.getTransactionTime());
			csm.setLastLoginTime(tLog.getTransactionTime());
			csm.setLastUpdateTime(tLog.getTransactionTime());
			csm.setLastRequestTime(tLog.getTransactionTime());
			csm.setRequestCountAfterLogin(0);
			csm.setSessionKey(hexEncodedKey);
			channelSessionManagementService.saveCSM(csm);
			log.info("channelsessionmanagement data saved");
			
			log.info("checking for pin strength with Subscriber MDN "+srcSubscriberMDN.getMDN() );
			if(!MfinoUtil.isPinStrongEnough(userPwd)){
				log.info("Subscriber is Registered with weak mPIN and forced to change the Pin");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ForceUpgradeAppForUsers);
				result.setResponseStatus(GeneralConstants.LOGIN_RESPONSE_FAILED);
				return result;
			}
			
			if(srcSubscriberMDN.getMigrateToSimobiPlus() != null && 
					srcSubscriberMDN.getMigrateToSimobiPlus()){
				log.info("Subscriber Migrated to SimobiPlus");
				result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberMigratedToSimobiPlus);
				result.setResponseStatus(GeneralConstants.LOGIN_RESPONSE_FAILED);
				return result;
			}
			
			String configuredStrDate = systemParametersService.getString(SystemParameterKeys.DATE_TO_EXPIRE_MOBILE_APP_PIN);
			
			if(StringUtils.isNotBlank(configuredStrDate)) {
			
				Date configureDate = DateUtil.getDate(configuredStrDate);
				Date currentDate = new Date();
				
				if(configureDate.before(currentDate)) {
					
					Timestamp lastAppPinChange = srcSubscriberMDN.getLastAppPinChange();
					if(null != lastAppPinChange) {
						
						if(lastAppPinChange.before(configureDate)) {
							
							log.info("Subscriber is forced to change the Pin");
							result.setNotificationCode(CmFinoFIX.NotificationCode_ForceUpgradeAppForUsers);
							result.setResponseStatus(GeneralConstants.LOGIN_RESPONSE_FAILED);
							return result;
						}
						
					} else {
						
						log.info("Subscriber is forced to change the Pin");
						result.setNotificationCode(CmFinoFIX.NotificationCode_ForceUpgradeAppForUsers);
						result.setResponseStatus(GeneralConstants.LOGIN_RESPONSE_FAILED);
						return result;
					}
				}
			}
			
			result.setSimobiPlusUpgrade(CmFinoFIX.MigrateToSimobiPlusEventStatus_InActive);
			if((srcSubscriberMDN.getIsMigrateableToSimobiPlus() == null ||
					srcSubscriberMDN.getIsMigrateableToSimobiPlus()) &&
					StringUtils.equalsIgnoreCase(systemParametersService.getString(
							SystemParameterKeys.SHOW_MIGRATE_TO_SIMOBIPLUS_EVENT), "true")){
				
				result.setSimobiPlusUpgrade(CmFinoFIX.MigrateToSimobiPlusEventStatus_Active);
				String lastDateStr = systemParametersService.getString(SystemParameterKeys.FORCE_MIGRATE_TO_SIMOBIPLUS_DATE);
				
				if(StringUtils.isNotBlank(lastDateStr)){
					Date lastDate = DateUtil.getDate(lastDateStr);
					Date currentDate = new Date();
					if(currentDate.after(lastDate)) {
						result.setSimobiPlusUpgrade(CmFinoFIX.MigrateToSimobiPlusEventStatus_ForceMigrate);
					}
				}
			}
			
			log.info("setting login response data to LoginXMLResult");
			authToken =srcSubscriberMDN.getAuthorizationToken();
			if(authToken==null||StringUtils.isBlank(authToken))
			{
				authToken = MfinoUtil.calculateAuthorizationToken(request.getSourceMDN(),userPwd);
				srcSubscriberMDN.setAuthorizationToken(authToken);
				subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
			}
			byte[] encryptedAESKey = CryptographyService.encryptWithPBE(aesKey, authToken.toCharArray(), salt, GeneralConstants.PBE_ITERATION_COUNT);
			byte[] encryptedZeroes = CryptographyService.encryptWithAES(aesKey, GeneralConstants.ZEROES_STRING.getBytes(GeneralConstants.UTF_8));
			String hexEncodedEncKey = new String(CryptographyService.binToHex(encryptedAESKey));
			String hexEncodedEncZeroes = new String(CryptographyService.binToHex(encryptedZeroes));
			String hexEncodedSalt = new String(CryptographyService.binToHex(salt));
			result.setKey(hexEncodedEncKey);
			result.setSalt(hexEncodedSalt);
			result.setAuthentication(hexEncodedEncZeroes);
			result.setNotificationCode(CmFinoFIX.NotificationCode_WebapiLoginSuccessful);
			result.setSubscriberType(srcSubscriberMDN.getSubscriber().getType());
			result.setUserAPIKey(srcSubscriberMDN.getUserAPIKey());
			if(srcSubscriberMDN.getWrongPINCount() > 0){
				log.info("setting wrong pin count to 0, and saving subscribermdn status");
				srcSubscriberMDN.setWrongPINCount(0);
				subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
			}
		}
		catch (Exception ex) {
			log.error("Exception occured while handling login", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_WebapiLoginFailureDueToSystemError);
			return result;
		}
		}else{
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidAppVersion);
			log.info("User: "+request.getSourceMDN()+" tried to login from invalid version app: "+request.getAppOS()+"."+request.getAppType()+"."+request.getAppVersion());
		}

		return result;
	}
	
	

	private void recalculateWrongPinCounts(SubscriberMDN mdn, LoginXMLResult result) {
		int wrongPinCount = systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT);
		if (mdn.getWrongPINCount() <wrongPinCount )
			mdn.setWrongPINCount(mdn.getWrongPINCount() + 1);
		if(mdn.getWrongPINCount()==wrongPinCount)
			recalculateMDNRestrictions(mdn);
		result.setNumberOfTriesLeft(wrongPinCount - mdn.getWrongPINCount());
		
	}

	private void recalculateMDNRestrictions(SubscriberMDN subscriberMDN) {
		if ((subscriberMDN.getRestrictions() & CmFinoFIX.SubscriberRestrictions_SecurityLocked) != 0) {
			return;
		}
		if (subscriberMDN.getWrongPINCount() >= systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)) {
			Timestamp now = new Timestamp();
			subscriberMDN.setRestrictions(subscriberMDN.getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
			Integer mdnStatus = subscriberMDN.getStatus();
			if( !CmFinoFIX.MDNStatus_Retired.equals(mdnStatus)
					&& !CmFinoFIX.MDNStatus_PendingRetirement.equals(mdnStatus) 
					&& !CmFinoFIX.MDNStatus_Suspend.equals(mdnStatus) )
			{
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriberMDN.setStatusTime(now);
			}
			subscriberMDN.getSubscriber().setRestrictions(subscriberMDN.getSubscriber().getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
			Integer subscriberStatus = subscriberMDN.getSubscriber().getStatus();
			if( !CmFinoFIX.SubscriberStatus_Retired.equals(subscriberStatus) 
					&& !CmFinoFIX.SubscriberStatus_PendingRetirement.equals(subscriberStatus)
					&& !CmFinoFIX.SubscriberStatus_Suspend.equals(subscriberStatus) )
			{
				subscriberMDN.getSubscriber().setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriberMDN.getSubscriber().setStatusTime(now);
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriberMDN.getSubscriber(),true);
			}
			
			// Check if the Subscriber is of Partner type
			if (CmFinoFIX.SubscriberType_Partner.equals(subscriberMDN.getSubscriber().getType())) {
				Set<Partner> setPartners = subscriberMDN.getSubscriber().getPartnerFromSubscriberID();
				if (CollectionUtils.isNotEmpty(setPartners)) {
					Partner partner = setPartners.iterator().next();
					Integer partnerStatus = partner.getPartnerStatus();
					if( !CmFinoFIX.PartnerServiceStatus_Retired.equals(partnerStatus) 
							&& !CmFinoFIX.PartnerServiceStatus_PendingRetirement.equals(partnerStatus) )
					{
						partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_InActive);
						partnerService.savePartner(partner);
					}
				}
			}
		}
	}

}

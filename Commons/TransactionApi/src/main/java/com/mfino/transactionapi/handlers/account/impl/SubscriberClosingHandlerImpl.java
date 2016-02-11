/**
 * 
 */
package com.mfino.transactionapi.handlers.account.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.crypto.CryptographyService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.errorcodes.Codes;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberClosing;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.MFAService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.SubscriberClosingHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
@Service("SubscriberClosingHandlerImpl")
public class SubscriberClosingHandlerImpl  extends FIXMessageHandler implements SubscriberClosingHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
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
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		CMSubscriberClosing subscriberClosing = new CMSubscriberClosing();
		subscriberClosing.setAgentMDN(transactionDetails.getSourceMDN());
		subscriberClosing.setMDN(transactionDetails.getDestMDN());
		
		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberClosing,subscriberClosing.DumpFields());
		
		SubscriberMDN agentMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		
		result.setCompany(agentMDN.getSubscriber().getCompany());
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		Integer validationResult = transactionApiValidationService.validateAgentMDN(agentMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(agentMDN, transactionDetails.getSourcePIN());
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)-agentMDN.getWrongPINCount());
			return result;
		}
		
		SubscriberMDN subMDN = subscriberMdnService.getByMDN(subscriberClosing.getMDN());
		
		if (subMDN != null) {
			
			int code = subscriberService.retireSubscriber(subMDN);
			
			if(code == Codes.SUCCESS) {
				
				boolean isHttps = transactionDetails.isHttps();
				boolean isHashedPin = transactionDetails.isHashedPin();
				
				String oneTimeOTP = transactionDetails.getActivationOTP();
				String mfaOneTimeOTP = transactionDetails.getTransactionOTP();
				Long parentTxnId = transactionDetails.getParentTxnId();
				
				ServiceChargeTransactionLog sctlForMFA = sctlService.getBySCTLID(parentTxnId);

				if(!(mfaService.isValidOTP(mfaOneTimeOTP , sctlForMFA.getID(), agentMDN.getMDN()))){
						
					result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidData);
					
				} else {
					
					subMDN.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					subMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
					
					Subscriber subscriber = subMDN.getSubscriber();
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					
					SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
					subscriberDAO.save(subscriber);
					
					subMDN.setSubscriber(subscriber);
					
					SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
					subscriberMDNDAO.save(subMDN);
					
					if(CmFinoFIX.NotificationCode_OTPValidationSuccessful.equals(validateOTP(subMDN, isHttps, isHashedPin, oneTimeOTP))) {
					
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
						result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingSuccess);
						
						log.debug("Subscriber state modifeid to retired....");
						
					} else {
						
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
						result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingFailed);
						
						log.debug("Subscriber state is not modified to retired due to some error....");
					}
				}
			} else {
				
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingFailed);
				
				log.debug("Subscriber state is not modified to retired due to some error....");
			}
			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			log.debug("Subscriber not found....");
		}		
		
		return result;
	}
	
	public  Integer validateOTP(SubscriberMDN subscriberMDN, boolean isHttps, boolean isHashedPin, String oneTimeOTP) {

		if (subscriberMDN == null) {
			
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		int int_subscriberType=subscriberMDN.getSubscriber().getType();

		if (!(CmFinoFIX.SubscriberType_Subscriber.equals(int_subscriberType)
				||CmFinoFIX.SubscriberType_Partner.equals(int_subscriberType))) {
			
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		
		if (subscriberMDN.getOTPExpirationTime().before(new Date())) {
			
			log.info("OTP Expired failed for the subscriber "+ subscriberMDN.getMDN());
			return CmFinoFIX.NotificationCode_OTPExpired;
		}

		String originalOTP =subscriberMDN.getOTP();

		if (!isHttps) {
			
		try {
				String authStr = oneTimeOTP;
				byte[] authBytes = CryptographyService.hexToBin(authStr.toCharArray());
				byte[] salt = { 0, 0, 0, 0, 0, 0, 0, 0 };
				byte[] decStr = CryptographyService.decryptWithPBE(authBytes, originalOTP.toCharArray(), salt, 20);
				String str = new String(decStr, GeneralConstants.UTF_8);
				
				if (!GeneralConstants.ZEROES_STRING.equals(str))
					return CmFinoFIX.NotificationCode_OTPInvalid;
				
			} catch (Exception ex) {
				log.info("OTP Check failed for the subscriber "
						+ subscriberMDN.getMDN());
				return CmFinoFIX.NotificationCode_OTPInvalid;
			}
		
		} else {
			String receivedOTP = oneTimeOTP;
			if (CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN.getStatus())) {
				
				/*String prefix = systemParametersService.getString(SystemParameterKeys.FAC_PREFIX_VALUE);
				prefix = (prefix == null) ? StringUtils.EMPTY : prefix;
				int otpLength = Integer.parseInt(systemParametersService.getString(SystemParameterKeys.OTP_LENGTH));
				
				String receivedFAC = receivedOTP;
				
				if (receivedOTP.length() < (otpLength + prefix.length()))
					receivedFAC = prefix + receivedOTP;

				String receivedFACDigest = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), receivedFAC);*/
			
			} else {
				receivedOTP = new String(CryptographyService.generateSHA256Hash(subscriberMDN.getMDN(), receivedOTP));
				
				if (!originalOTP.equals(receivedOTP)) {
					
					log.info("OTP Check failed for the subscriber "+ subscriberMDN.getMDN());
					return CmFinoFIX.NotificationCode_OTPInvalid;
				}
			}
		}
	
		return CmFinoFIX.NotificationCode_OTPValidationSuccessful;
	}
}
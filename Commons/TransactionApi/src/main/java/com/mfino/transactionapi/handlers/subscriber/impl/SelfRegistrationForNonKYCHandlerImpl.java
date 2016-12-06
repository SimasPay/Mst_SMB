package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MdnOtpDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MdnOtp;
import com.mfino.domain.Notification;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SelfRegistrationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

@Service("SelfRegistrationForNonKYCHandlerImpl")
public class SelfRegistrationForNonKYCHandlerImpl extends FIXMessageHandler implements SelfRegistrationHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private String email;

	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;	
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;	
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	public Result handle(TransactionDetails transDetails) {
		
		CMSubscriberRegistration subscriberRegistration = new CMSubscriberRegistration();
		ChannelCode cc = transDetails.getCc();
		
		subscriberRegistration.setSourceMDN(transDetails.getSourceMDN());
		subscriberRegistration.setMDN(subscriberService.normalizeMDN(transDetails.getSourceMDN()));
		subscriberRegistration.setFirstName(transDetails.getFirstName());
		subscriberRegistration.setPin(transDetails.getNewPIN());
		subscriberRegistration.setKYCLevel(ConfigurationUtil.getIntialKyclevel());
		subscriberRegistration.setChannelCode(cc.getChannelcode());
		subscriberRegistration.setSourceApplication((int)cc.getChannelsourceapplication());
		subscriberRegistration.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		email = transDetails.getEmail();
		
		TransactionLog transactionsLog = null;
		log.info("Handling subscriber services Registration webapi request");
		XMLResult result = new RegistrationXMLResult();
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(transDetails.getSourceMDN());

		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
		result.setSourceMessage(subscriberRegistration);
		result.setDestinationMDN(subscriberRegistration.getMDN());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		subscriberRegistration.setTransactionID(transactionsLog.getId().longValue());
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);
		result.setActivityStatus(false);

		Transaction transactionDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(subscriberRegistration.getSourceMDN());
		sc.setDestMDN(subscriberRegistration.getMDN());
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(subscriberRegistration.getTransactionIdentifier());

		try{
			
			transactionDetails =transactionChargingService.getCharge(sc);
			
		}catch (InvalidServiceException e) {
			
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
			
		} catch (InvalidChargeDefinitionException e) {
			
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		
		ServiceChargeTxnLog sctl = transactionDetails.getServiceChargeTransactionLog();
		subscriberRegistration.setServiceChargeTransactionLogID(sctl.getId().longValue());
		result.setSctlID(sctl.getId().longValue());

		if(isValidOTP(transDetails.getSourceMDN(), transDetails.getActivationOTP())) {
			
			Subscriber subscriber = new Subscriber();
			SubscriberMdn subscriberMDN = new SubscriberMdn();
			
			subscriber.setEmail(email);
			subscriber.setIsemailverified(CmFinoFIX.Boolean_False);
			
			Integer regResponse = subscriberServiceExtended.registerNonKycSubscriber(subscriber, subscriberMDN, subscriberRegistration);
			
			if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
				
				Notification notification = notificationService.getByNoticationCode(regResponse);
				String notificationName = null;
				if(notification != null){
					
					notificationName = notification.getCodename();
					
				}else{
					
					log.error("Could not find the failure notification code: "+regResponse);
				}
				
				result.setActivityStatus(false);
				result.setNotificationCode(regResponse);
				transactionChargingService.failTheTransaction(sctl, MessageText._("Subscriber Registration failed. Notification Code: "+regResponse+" NotificationName: "+notificationName));
				
			}else{
				
				result.setActivityStatus(true);
				result.setNotificationCode(CmFinoFIX.NotificationCode_NonKycSubscriberSuccessfullyRegistered);
				
				if (sctl != null) {
					
					sc.setSctlId(sctl.getId().longValue());
					
					try{
						
						transactionDetails =transactionChargingService.getCharge(sc);
						
					} catch (InvalidChargeDefinitionException e) {
						
						log.error(e.getMessage());
						
					} catch (Exception e) {
						
						log.error("Exception occured in getting charges for Registration",e);
					}
					
					transactionChargingService.confirmTheTransaction(sctl);
				}	
			}
			
		} else {
			
			result.setNotificationCode(CmFinoFIX.NotificationCode_OTPInvalid);
		}

		return result;
	}
	
	private boolean isValidOTP(String mdn, String otp) {
		
		boolean isValid = false;
		MdnOtpDAO mdnOtpDao = DAOFactory.getInstance().getMdnOtpDAO();
		MdnOtp mdnOtp = null;
		
		List<MdnOtp> mdnList = mdnOtpDao.getByMdn(mdn);
			
		if(null != mdnList && mdnList.size() > 0) {
				
			mdnOtp = mdnList.get(0);
		
			String receivedOTPDigest = MfinoUtil.calculateDigestPin(mdn, otp);
	
			if(CmFinoFIX.OTPStatus_FailedOrExpired.equals(mdnOtp.getStatus())) {
				log.info("OTP validation failed for MDN " + mdn);
			}
			else if (mdnOtp.getOtpexpirationtime().after(new Date()) ) {
				
				if(mdnOtp.getOtp().equals(receivedOTPDigest)) {
					
					mdnOtp.setStatus(CmFinoFIX.OTPStatus_Validated);
					mdnOtpDao.save(mdnOtp);
					isValid = true;
					
					log.info("OTP validation successful for MDN " + mdn);
					
				} else {
					
					int currentOtpTrials = mdnOtp.getOtpretrycount().intValue()+1;
					mdnOtp.setOtpretrycount((long)currentOtpTrials);
					int remainingTrials = getNumberOfRemainingTrials(currentOtpTrials);
					
					if(remainingTrials < 0){
						
						mdnOtp.setStatus(CmFinoFIX.OTPStatus_FailedOrExpired);
						log.info("OTP expired for MDN " + mdn);					
					}
					mdnOtpDao.save(mdnOtp);
					log.info("Invalid OTP for MDN " + mdn);					
				}
				
			} else{
				
				mdnOtp.setStatus(CmFinoFIX.OTPStatus_FailedOrExpired);
				mdnOtpDao.save(mdnOtp);
				log.info("OTP validation failed for MDN " + mdn);
			}
		}  else {
			
			log.info("MDN " +  mdn + "doesn't exist");
		}
		
		return isValid;
	}
	
	private int getNumberOfRemainingTrials(int currentOtpTrials) {
		
		int maxOtpTrials = systemParametersService.getInteger(SystemParameterKeys.MAX_OTP_TRAILS);
		return (maxOtpTrials-currentOtpTrials);
	}
}
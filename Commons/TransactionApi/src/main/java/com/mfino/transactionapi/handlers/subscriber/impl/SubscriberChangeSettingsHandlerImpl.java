package com.mfino.transactionapi.handlers.subscriber.impl;

import static com.mfino.fix.CmFinoFIX.MessageType_SubscriberChangeSettings;
import static com.mfino.fix.CmFinoFIX.NotificationCode_InvalidLanguageInChangeSettingsWebAPIRequest;
import static com.mfino.fix.CmFinoFIX.NotificationCode_InvalidNotificationMethodInChangeSettingsWebAPIRequest;
import static com.mfino.fix.CmFinoFIX.NotificationCode_NoValidEmailExistingForSubscriber;
import static com.mfino.fix.CmFinoFIX.NotificationCode_SubscriberSettingsChangeComplete;
import static com.mfino.fix.CmFinoFIX.ResponseCode_Failure;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.EnumText;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberChangeSettings;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.EnumTextService;
import com.mfino.service.MailService;
import com.mfino.service.ResultService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberChangeSettingsHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeSettingsXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ValidationUtil;

@Service("SubscriberChangeSettingsHandlerImpl")
public class SubscriberChangeSettingsHandlerImpl extends FIXMessageHandler implements SubscriberChangeSettingsHandler{

	private static Logger log	= LoggerFactory.getLogger(SubscriberChangeSettingsHandlerImpl.class);
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("ResultServiceImpl")
	private ResultService resultService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	public Result handle(TransactionDetails transDetails) {
		CMSubscriberChangeSettings	setting = new CMSubscriberChangeSettings();
		ChannelCode cc = transDetails.getCc();
		
		setting.setSourceMDN(transDetails.getSourceMDN());
		setting.setPin(transDetails.getSourcePIN());
		setting.setEmail(transDetails.getEmail());
		setting.setChannelCode(cc.getChannelcode());
		setting.setSourceApplication((int)cc.getChannelsourceapplication());
		String language = transDetails.getLanguage();
		String notificationMethod = transDetails.getNotificationMethod();
		setting.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		
		log.info("Handling ChangeSettings of Subscriber webapi request");

		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(MessageType_SubscriberChangeSettings, setting.DumpFields());
		setting.setTransactionID(transactionLog.getId().longValue());
		XMLResult result = new ChangeSettingsXMLResult();
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setSourceMessage(setting);
		result.setTransactionID(transactionLog.getId().longValue());
		

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(setting.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+setting.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}
		
		validationResult = transactionApiValidationService.validatePin(srcSubscriberMDN, setting.getPin());
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+setting.getSourceMDN()+" has failed PIN validations");
			result.setNotificationCode(validationResult);
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)-srcSubscriberMDN.getWrongpincount()));
			return result;
		}
		
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		
		Subscriber subscriber = srcSubscriberMDN.getSubscriber();
		String email = subscriber.getEmail();

		if (StringUtils.isNotBlank(setting.getEmail())
				&&StringUtils.isNotBlank(email) 
				&& !ValidationUtil.isValidEmail2(setting.getEmail()))
			return resultService.returnResult(setting, NotificationCode_NoValidEmailExistingForSubscriber, srcSubscriberMDN, result, false);
		if (StringUtils.isNotBlank(notificationMethod)){
			if(!isValidEnum("NotificationMethod",setting,language,notificationMethod))
				return resultService.returnResult(setting, NotificationCode_InvalidNotificationMethodInChangeSettingsWebAPIRequest, srcSubscriberMDN, result, false);
			if(((setting.getNotificationMethod()&CmFinoFIX.NotificationMethod_Email)==CmFinoFIX.NotificationMethod_Email)
					&&StringUtils.isBlank(email))
				return resultService.returnResult(setting, NotificationCode_NoValidEmailExistingForSubscriber, srcSubscriberMDN, result, false);
		}
		if (StringUtils.isNotBlank(language)
				&&!isValidEnum("Language",setting,language,notificationMethod))
			return resultService.returnResult(setting, NotificationCode_InvalidLanguageInChangeSettingsWebAPIRequest, srcSubscriberMDN, result, false);
		

		Transaction transactionDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(setting.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(StringUtils.isNotBlank(setting.getChannelCode()) ? Long.valueOf(setting.getChannelCode()) : null);
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CHANGE_SETTINGS);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(setting.getTransactionID());
		sc.setTransactionIdentifier(setting.getTransactionIdentifier());

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
		result.setSctlID(sctl.getId().longValue());
		
		try {
			if (StringUtils.isNotBlank(language))
				subscriber.setLanguage(setting.getLanguage());
			if (StringUtils.isNotBlank(setting.getEmail())
					&&StringUtils.isNotBlank(email)){
				subscriber.setEmail(setting.getEmail());
				subscriber.setIsemailverified((short)0);}
			if (StringUtils.isNotBlank(notificationMethod))
				subscriber.setNotificationmethod(setting.getNotificationMethod().longValue());

 			subscriberService.saveSubscriber(subscriber);
			if(setting.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
				mailService.generateEmailVerificationMail(subscriber, setting.getEmail());
			}
		}
		catch (Exception ex) {
			log.error("Exception occured while changing subscriber settings:", ex);
			return resultService.returnResult(setting, ResponseCode_Failure, srcSubscriberMDN, result, false);
		}
		if (sctl != null) {
			sctl.setCalculatedcharge(BigDecimal.ZERO);
			transactionChargingService.completeTheTransaction(sctl);
		}
		return resultService.returnResult(setting, NotificationCode_SubscriberSettingsChangeComplete, srcSubscriberMDN, result, false);
	}

	private boolean isValidEnum(String str,CMSubscriberChangeSettings setting,String language,String notificationMethod) {
		EnumTextQuery etq = new EnumTextQuery();
		etq.setTagName(str);
		try{
			if (str.equals("NotificaionMethod")){
				setting.setNotificationMethod(Integer.parseInt(notificationMethod));
				etq.setEnumCode(String.valueOf(setting.getNotificationMethod()));
			}else if (str.equals("Language")){
				setting.setLanguage(Integer.parseInt(language));
				etq.setEnumCode(String.valueOf(setting.getLanguage()));
			}
		}catch (NumberFormatException e) {
			log.error("Error :"+e);
			return false;
		}
		List<EnumText> EnumList = enumTextService.getEnumText(etq);
		if (EnumList.size() == 0)
			return false;
		return true;
	}
}
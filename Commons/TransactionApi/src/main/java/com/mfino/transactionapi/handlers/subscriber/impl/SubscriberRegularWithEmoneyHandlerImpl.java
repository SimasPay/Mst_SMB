package com.mfino.transactionapi.handlers.subscriber.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Groups;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SMSValues;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubRegularWithEMoney;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegularWithEmoneyHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ResetPinByOTPXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Hemanth
 * 
 */
@Service("SubscriberRegularWithEmoneyHandlerImpl")
public class SubscriberRegularWithEmoneyHandlerImpl extends FIXMessageHandler implements SubscriberRegularWithEmoneyHandler {
	private static Logger	log	= LoggerFactory.getLogger(SubscriberRegularWithEmoneyHandlerImpl.class);
	private boolean isHttps;
	
	 @Autowired
	 @Qualifier("MFAServiceImpl")
	 private MFAService mfaService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;	

	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	

	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;

	public Result handle(TransactionDetails transDetails) {
		
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
		
		CMSubRegularWithEMoney subRegularWithEMoney = new CMSubRegularWithEMoney();
		ChannelCode cc = transDetails.getCc();
		
		subRegularWithEMoney.setSourceMDN(transDetails.getSourceMDN());
		subRegularWithEMoney.setOTP(transDetails.getActivationOTP());
		subRegularWithEMoney.setSourceApplication((int)cc.getChannelsourceapplication());
		subRegularWithEMoney.setChannelCode(cc.getChannelcode());
		subRegularWithEMoney.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		isHttps = transDetails.isHttps();
		
		log.info("Handling Subscriber Regular With Emoney confirmation webapi request");
		XMLResult result = new ResetPinByOTPXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubRegularWithEMoney, subRegularWithEMoney.DumpFields());
		subRegularWithEMoney.setTransactionID(transactionLog.getId().longValue());

		result.setSourceMessage(subRegularWithEMoney);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId().longValue());
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(subRegularWithEMoney.getSourceMDN());
		if(srcSubscriberMDN!=null){
		
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);

		ServiceChargeTxnLog sctl = sctlService.getBySCTLID(transDetails.getSctlId());
		result.setSctlID(sctl.getId().longValue());

		//Validate OTP 
		if(mfaService.isMFATransaction(transDetails.getServiceName(), transDetails.getTransactionName(), cc.getId().longValue())){
			   
			   if(subRegularWithEMoney.getOTP() == null || !(mfaService.isValidOTP(subRegularWithEMoney.getOTP() ,sctl.getId().longValue(), srcSubscriberMDN.getMdn()))){
			    
			    result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
			    result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			    return result;
			   }
			  }
		log.info("OTP validation Successfull");
		
		Long groupID = null;
		
		Subscriber subscriber = srcSubscriberMDN.getSubscriber();
		GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
		Groups defaultGroup = groupDao.getSystemGroup();
		groupID = defaultGroup.getId();
		
		PocketTemplate emoneyPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKycLevel().getKyclevel(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		
		if (emoneyPocketTemplate == null) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound);
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			return result;
		}
		
		Pocket emoneyPocket = null;
		emoneyPocket = pocketService.createPocket(emoneyPocketTemplate,srcSubscriberMDN, CmFinoFIX.PocketStatus_Active, true, null);
		
		if(null != emoneyPocket) {
			
			emoneyPocket.setId(emoneyPocket.getId());
			
			String cardPan = null;
			
			try {
				
				cardPan = pocketService.generateSVAEMoney16DigitCardPAN(srcSubscriberMDN.getMdn());
				
			} catch (Exception e) {
				
				log.error("Cardpan creation failed", e);
			}
			
			emoneyPocket.setCardpan(cardPan);
			pocketDao.save(emoneyPocket);
			
			if (sctl != null) {
				transactionChargingService.completeTheTransaction(sctl);
			}
			
			sendSMS (srcSubscriberMDN) ;
				
			
			result.setNotificationCode(CmFinoFIX.NotificationCode_EmoneyPocketAdded);
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
			return result;
			
		}
		
		
	}
		log.error("Source subscriber with mdn : "+transDetails.getSourceMDN()+" not found");
		result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		return result;
	}

	private void sendSMS(SubscriberMdn srcSubscriberMDN) {
		
		NotificationWrapper smsNotificationWrapper =new NotificationWrapper();
		smsNotificationWrapper.setCode(CmFinoFIX.NotificationCode_EmoneyPocketAdded);
		smsNotificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);
		SMSValues smsValues= new SMSValues();
		smsValues.setDestinationMDN(srcSubscriberMDN.getMdn());
		smsValues.setMessage(smsMessage);
		smsValues.setNotificationCode(smsNotificationWrapper.getCode());
		smsService.asyncSendSMS(smsValues);
		
		log.info("sms sent successfully");
	}

}
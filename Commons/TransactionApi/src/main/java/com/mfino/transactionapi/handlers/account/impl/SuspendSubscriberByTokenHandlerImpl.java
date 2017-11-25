package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCloseSubscriberByToken;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.SuspendSubscriberByTokenHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

@Service("SuspendSubscriberByTokenHandlerImpl")
public class SuspendSubscriberByTokenHandlerImpl extends FIXMessageHandler
		implements SuspendSubscriberByTokenHandler {
	private static Logger log = LoggerFactory
			.getLogger(SuspendSubscriberByTokenHandlerImpl.class);
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserServiceImpl;
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParameterService;
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	public Result handle(TransactionDetails transactionDetails) {
		XMLResult result = new ChangeEmailXMLResult();
		JSONObject root = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
		
		String mdn = "";
		Date generatedDate = null;
		Date expiredDate = null;
		try {
			log.info("SuspendSubscriberByTokenHandlerImpl Begin");

			ChannelCode cc = transactionDetails.getCc();
			CMCloseSubscriberByToken request = new CMCloseSubscriberByToken();
			request.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
			request.setChannelCode(transactionDetails.getChannelCode());
			request.setMigrateToken(transactionDetails.getSecreteCode());
			request.setSourceApplication(cc.getChannelSourceApplication());

			String secreteCode = transactionDetails.getSecreteCode();

			String decryptedToken = validateToken(secreteCode);

			String[] splitedToken = StringUtils.split(decryptedToken, "#");
			if ((splitedToken != null) && (splitedToken.length == 2)) {
				mdn = splitedToken[0];
				generatedDate = sdf.parse(splitedToken[1]);
				Integer upgradeTokenTimeoutValue = ConfigurationUtil.getUpgradeTokenTimeoutValue();
				expiredDate = DateUtil.addMinutes(generatedDate, upgradeTokenTimeoutValue.intValue());
			} else {
				NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_InvalidMigrateSimobiPlusToken, null, null);
				String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
				throw new EmptyStringException(message);
			}
			
			if ((StringUtils.isBlank(mdn)) || (generatedDate == null)) {
				NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_InvalidMigrateSimobiPlusToken, null, null);
				String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
				throw new EmptyStringException(message);
			}
			
			request.setSourceMDN(mdn);
			TransactionsLog trxLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_CloseSubscriberByToken, request.DumpFields());
			
			log.info("SuspendSubscriberByTokenHandlerImpl For MDN: " + mdn + " Expired: " + sdf.format(expiredDate));
			if (!new Date().after(expiredDate)) {
				SubscriberMDN subscriberMDN = this.subscriberMdnService.getByMDN(mdn);
				Subscriber subscriber = subscriberMDN.getSubscriber();
				if(subscriberMDN.getIsMigrateableToSimobiPlus()){
					NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_SubscriberMigratedToSimobiPlus, subscriberMDN, subscriber);
					String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
					root.put("status", Integer.valueOf(201));
					root.put("message", message);
					result.setMessage(root.toString());
					return result;
				}
				
				if ((subscriberMDN != null) && (subscriber != null)) {
					Timestamp now = new Timestamp();
					
					if ((!subscriberMDN.getStatus().equals(CmFinoFIX.MDNStatus_Active))
							|| (!subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Active))) 
					{
						NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_MDNIsNotActive, 
								subscriberMDN, subscriber);
						String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
						throw new InvalidMDNException(message);
					}
					String cif = transactionDetails.getApplicationId();
					markSubscriberMigratedToSimobiPlus(subscriberMDN, now, secreteCode, cif);
					
					ServiceCharge sc = new ServiceCharge();
					sc.setSourceMDN(subscriberMDN.getMDN());
					sc.setDestMDN(null);
					sc.setChannelCodeId(StringUtils.isNotBlank(transactionDetails.getChannelCode()) ? Long.valueOf(transactionDetails.getChannelCode()) : null);
					sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
					sc.setTransactionTypeName(transactionDetails.getTransactionName());
					sc.setTransactionAmount(BigDecimal.ZERO);
					sc.setTransactionLogId(trxLog.getID());
					sc.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

					try{
						Transaction transactionCharge = transactionChargingService.getCharge(sc);
						ServiceChargeTransactionLog sctl = transactionCharge.getServiceChargeTransactionLog();
						if (sctl != null) {
							sctl.setCalculatedCharge(BigDecimal.ZERO);
							transactionChargingService.completeTheTransaction(sctl);
						}
					}catch (InvalidServiceException e) {
						log.error("Exception occured in getting charges",e);
						result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
						return result;
					} catch (InvalidChargeDefinitionException e) {
						log.error(e.getMessage());
						result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
						return result;
					}
					
					NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_SubscriberMigratedToSimobiPlus, subscriberMDN, subscriber);
					String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
					root.put("status", Integer.valueOf(200));
					root.put("message", message);
				} else {
					NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_InvalidMigrateSimobiPlusToken, null, null);
					String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
					root.put("status", Integer.valueOf(404));
					root.put("message", message);
				}
			} else {
				NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_TimeoutMigrateSimobiPlusToken, null, null);
				String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
				root.put("status", Integer.valueOf(408));
				root.put("message", message);
			}
		} catch (InvalidMDNException em) {
			root.put("status", Integer.valueOf(500));
			root.put("message", em.getMessage());
			log.error("ERROR", em);
		} catch (EmptyStringException ex) {
			root.put("status", Integer.valueOf(400));
			root.put("message", ex.getMessage());
			log.error("ERROR", ex);
		} catch (Exception e) {
			root.put("status", Integer.valueOf(500));
			root.put("message", "Simobi Server Error");
			log.error("ERROR", e);
		}
		result.setMessage(root.toString());
		return result;
	}

	private void markSubscriberMigratedToSimobiPlus(SubscriberMDN subscriberMDN, Timestamp now, String token, String cif) {
		log.info("SuspendSubscriberByTokenHandlerImpl :: markSubscriberMigratedToSimobiPlus BEGIN");
		if(StringUtils.isNotBlank(cif)){

			SubscriberMdnQuery query = new SubscriberMdnQuery();
			query.setApplicationId(cif);
			List<SubscriberMDN> relatedSubscriberByCIF = this.subscriberMdnService.getByQuery(query);
			if(relatedSubscriberByCIF != null && !relatedSubscriberByCIF.isEmpty()){
				for (SubscriberMDN relatedSubsMdn : relatedSubscriberByCIF) {
					relatedSubsMdn.setMigrateToSimobiPlus(BOOL_TRUE);
					relatedSubsMdn.setMigrateToken(token);
					relatedSubsMdn.setMigrateDate(now);
					this.subscriberMdnService.saveSubscriberMDN(relatedSubsMdn);
				}
			}	
		} else{
			subscriberMDN.setMigrateToSimobiPlus(BOOL_TRUE);
			subscriberMDN.setMigrateToken(token);
			subscriberMDN.setMigrateDate(now);
			this.subscriberMdnService.saveSubscriberMDN(subscriberMDN);
		}

		log.info("SuspendSubscriberByTokenHandlerImpl :: markSubscriberMigratedToSimobiPlus END");
	}

	private String validateToken(String secreteCode)
			throws EmptyStringException {
		String decryptedToken = "";
		if (StringUtils.isBlank(secreteCode)) {
			NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_InvalidMigrateSimobiPlusToken, null, null);
			throw new EmptyStringException(notificationMessageParserServiceImpl.buildMessage(wrapper, false));
		}
		try {
			decryptedToken = CryptographyService.decryptWithPrivateKey(secreteCode);
		} catch (Exception e) {
			NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_InvalidMigrateSimobiPlusToken, null, null);
			throw new EmptyStringException(notificationMessageParserServiceImpl.buildMessage(wrapper, false));
		}
		return decryptedToken;
	}

	private NotificationWrapper getNotificationWrapper(Integer notificationCode, SubscriberMDN subscriberMdn, 
			Subscriber subscriber){
		NotificationWrapper wrapper = new NotificationWrapper();
		wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_BankChannel);
		wrapper.setCode(notificationCode);
		
		if(subscriber != null){
			wrapper.setLanguage(subscriber.getLanguage());
			wrapper.setCompany(subscriber.getCompany());
		}
		
		if(subscriber == null || subscriber.getLanguage() == null){
			wrapper.setLanguage(systemParameterService.getSubscribersDefaultLanguage());
		}
		return wrapper;
	}
}

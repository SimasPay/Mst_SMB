package com.mfino.transactionapi.handlers.account.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberByToken;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.GetSubscriberByTokenHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

@Service("GetSubscriberByTokenHandlerImpl")
public class GetSubscriberByTokenHandlerImpl extends FIXMessageHandler
		implements GetSubscriberByTokenHandler {
	private static Logger log = LoggerFactory
			.getLogger(GetSubscriberByTokenHandlerImpl.class);
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserServiceImpl;
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParameterService;
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	public Result handle(TransactionDetails transactionDetails) {
		XMLResult result = new ChangeEmailXMLResult();
		JSONObject root = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss");

		String mdn = "";
		Date generatedDate = null;
		Date expiredDate = null;
		try {
			log.info("GetSubscriberByTokenHandlerImpl Begin");
			ChannelCode cc = transactionDetails.getCc();
			CMGetSubscriberByToken request = new CMGetSubscriberByToken();
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
				throw new EmptyStringException(notificationMessageParserServiceImpl.buildMessage(wrapper, false));
			}
			if ((StringUtils.isBlank(mdn)) || (generatedDate == null)) {
				NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_InvalidMigrateSimobiPlusToken, null, null);
				throw new EmptyStringException(notificationMessageParserServiceImpl.buildMessage(wrapper, false));
			}

			request.setSourceMDN(mdn);
			transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetSubscriberByToken, request.DumpFields());
			
			log.info("GetSubscriberByTokenHandlerImpl For MDN: " + mdn + " Expired: " + sdf.format(expiredDate));
			
			if (!new Date().after(expiredDate)) {
				
				SubscriberMDN subscriberMDN = this.subscriberMdnService.getByMDN(mdn);
				Subscriber subscriber = subscriberMDN.getSubscriber();
				Pocket bankPocket = this.pocketService.getDefaultPocket(subscriberMDN, "2");
				Set<Pocket> subscriberPockets = subscriberMDN.getPocketFromMDNID();
				if ((bankPocket == null) || (subscriberPockets.size() != 1)) {
					throw new InvalidMDNException("Subscriber is not Bank Account Type");
				}
				
				if ((subscriberMDN != null) && (subscriber != null)) {
					
					if ((!subscriberMDN.getStatus().equals(CmFinoFIX.MDNStatus_Active))
							|| (!subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Active))) 
					{
						NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_MDNIsNotActive, 
								subscriberMDN, subscriber);
						String message = notificationMessageParserServiceImpl.buildMessage(wrapper, false);
						throw new InvalidMDNException(message);
					}
					
					construnctSuccessResponse(root, mdn, subscriberMDN, subscriber, bankPocket);
				} else {
					NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_InvalidMigrateSimobiPlusToken, null, null);
					root.put("status", Integer.valueOf(404));
					root.put("message", notificationMessageParserServiceImpl.buildMessage(wrapper, false));
				}
			} else {
				NotificationWrapper wrapper = getNotificationWrapper(CmFinoFIX.NotificationCode_TimeoutMigrateSimobiPlusToken, null, null);
				root.put("status", Integer.valueOf(408));
				root.put("message", notificationMessageParserServiceImpl.buildMessage(wrapper, false));
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

	private void construnctSuccessResponse(JSONObject root, String mdn,
			SubscriberMDN subscriberMDN, Subscriber subscriber,
			Pocket bankPocket) {
		root.put("status", Integer.valueOf(200));
		root.put("message", "Success");

		JSONObject data = new JSONObject();
		data.put("firstName", subscriber.getFirstName());
		data.put("lastName", subscriber.getLastName());
		data.put("cif", subscriberMDN.getApplicationID());
		data.put("mdn", mdn);
		data.put("email", subscriber.getEmail());
		data.put("accountNo", bankPocket.getCardPAN());
		data.put("pocketType", "2");

		root.put("data", data);
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

package com.mfino.transactionapi.handlers.subscriber.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.ChannelSessionMgmt;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMWebApiLogoutRequest;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.service.ChannelSessionManagementService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.LogoutHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.LogoutXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
@Service("LogoutHandlerImpl")
public class LogoutHandlerImpl extends FIXMessageHandler implements LogoutHandler {

	private static Logger log = LoggerFactory.getLogger(LogoutHandlerImpl.class);
	
	@Autowired
	@Qualifier("ChannelSessionManagementServiceImpl")
	private ChannelSessionManagementService channelSessionManagementService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	public XMLResult handle(TransactionDetails transDetails) {
		CMWebApiLogoutRequest	request = new CMWebApiLogoutRequest();
		request.setSourceMDN(transDetails.getSourceMDN());
		request.setAuthMAC(transDetails.getAuthenticationString());		
		
		log.info("Handling Subscriber logout webapi request");
		LogoutXMLResult result = new LogoutXMLResult();

		TransactionLog tLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_WebApiLogoutRequest, request.DumpFields());

		result.setTransactionID(tLog.getId().longValue());
		result.setTransactionTime(tLog.getTransactiontime());
		result.setSourceMessage(request);
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(request.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+request.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}
		
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		
		ChannelSessionMgmt csm  = channelSessionManagementService.getChannelSessionManagemebtByMDNID(srcSubscriberMDN.getId().longValue());

		byte[] encryptedBytes = CryptographyService.hexToBin(request.getAuthMAC().toCharArray());
		byte[] key = CryptographyService.hexToBin((csm != null) ? csm.getSessionkey().toCharArray() : null);

		try {
			byte[] decryptedBytes = CryptographyService.decryptWithAES(key, encryptedBytes);
			String receivedZeroesString = new String(decryptedBytes, GeneralConstants.UTF_8);
			if (GeneralConstants.ZEROES_STRING.equals(receivedZeroesString)) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_WrongPINSpecified);
				return result;
			}

			csm.setLastlogintime(tLog.getTransactiontime());
			csm.setLastupdatetime(tLog.getTransactiontime());
			csm.setRequestcountafterlogin(Long.valueOf(0));
			csm.setSessionkey(null);
			channelSessionManagementService.saveCSM(csm);
			log.info("channelsessionmanagement data saved");

			result.setNotificationCode(CmFinoFIX.NotificationCode_WebapiLogoutSuccessful);
		}
		catch (Exception ex) {
			log.error("Exception occured while doing logout:", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_WebapiLogoutFailure);
		}
		return result;
	}

}

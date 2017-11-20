package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberByToken;
import com.mfino.result.Result;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.GenerateMigrateTokenHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.MigrateTokenXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("GenerateMigrateTokenHandlerImpl") 
public class GenerateMigrateTokenHandlerImpl implements GenerateMigrateTokenHandler {

	private static Logger log = LoggerFactory.getLogger(GenerateMigrateTokenHandlerImpl.class);
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		MigrateTokenXMLResult result = new MigrateTokenXMLResult();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
		
		try {
			ChannelCode cc = transactionDetails.getCc();
			CMGetSubscriberByToken request = new CMGetSubscriberByToken();

			String generateTokenDate = sdf.format(new Date());
			
			String encryptedToken = CryptographyService.encryptWithPublicKey(transactionDetails.getSourceMDN()+"#"+generateTokenDate);
			result.setMigrateToken(encryptedToken);
			
			request.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
			request.setChannelCode(transactionDetails.getChannelCode());
			request.setMigrateToken(encryptedToken);
			request.setSourceApplication(cc.getChannelSourceApplication());
			request.setSourceMDN(transactionDetails.getSourceMDN());
			
			TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GenerateSubscriberMigrateToken, request.DumpFields());
			
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(transactionDetails.getSourceMDN());
			sc.setDestMDN(null);
			sc.setChannelCodeId(StringUtils.isNotBlank(transactionDetails.getChannelCode()) ? Long.valueOf(transactionDetails.getChannelCode()) : null);
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
			sc.setTransactionTypeName(transactionDetails.getTransactionName());
			sc.setTransactionAmount(BigDecimal.ZERO);
			sc.setTransactionLogId(transactionsLog.getID());
			sc.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

			try{
				Transaction transactionCharge = transactionChargingService.getCharge(sc);
				ServiceChargeTransactionLog sctl = transactionCharge.getServiceChargeTransactionLog();
				if (sctl != null) {
					sctl.setCalculatedCharge(BigDecimal.ZERO);
					transactionChargingService.completeTheTransaction(sctl);
					result.setSctlID(sctl.getID());
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
		} catch (Exception e) {
			log.error("[ERROR] GenerateMigrateTokenHandlerImpl", e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_FailedGenerateSubscriberMigratedToken);
			return result;
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_GeneratedSubscriberMigratedToken);
		return result;
	}

}

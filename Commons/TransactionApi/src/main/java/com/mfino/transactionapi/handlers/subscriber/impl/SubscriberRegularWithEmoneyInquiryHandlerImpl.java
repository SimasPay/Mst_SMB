package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubRegularWithEMoneyInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegularWithEmoneyInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Srikanth
 * 
 */
@Service("SubscriberRegularWithEmoneyInquiryHandlerImpl")
public class SubscriberRegularWithEmoneyInquiryHandlerImpl extends FIXMessageHandler implements SubscriberRegularWithEmoneyInquiryHandler{
	private static Logger	log	= LoggerFactory.getLogger(SubscriberRegularWithEmoneyInquiryHandlerImpl.class);
    
	 @Autowired
	 @Qualifier("MFAServiceImpl")
	 private MFAService mfaService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService ;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	public Result handle(TransactionDetails transactionDetails) {
		ChannelCode cc = transactionDetails.getCc();
		CMSubRegularWithEMoneyInquiry SubRegularWithEMoneyInquiry= new CMSubRegularWithEMoneyInquiry();		
		SubRegularWithEMoneyInquiry.setSourceMDN(transactionDetails.getSourceMDN());			
		SubRegularWithEMoneyInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		SubRegularWithEMoneyInquiry.setChannelCode(cc.getChannelcode());
		SubRegularWithEMoneyInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());	
		
		log.info("Handling SubRegularWithEMoney inquiry webapi request for MDN: " +  SubRegularWithEMoneyInquiry.getSourceMDN());
		XMLResult result = new ChangeEmailXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);

		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubRegularWithEMoneyInquiry, 
				SubRegularWithEMoneyInquiry.DumpFields());
		SubRegularWithEMoneyInquiry.setTransactionID(transactionLog.getId().longValue());

		result.setSourceMessage(SubRegularWithEMoneyInquiry);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId().longValue());
		result.setNotificationCode(CmFinoFIX.NotificationCode_BSM_GeneralFailure);
		
		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(SubRegularWithEMoneyInquiry.getSourceMDN());
		
		
		
		
		if(subscriberMDN!=null){
			Integer validationResult = transactionApiValidationService.validatePin(subscriberMDN, transactionDetails.getSourcePIN());
			
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				result.setNotificationCode(validationResult);
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				return result;
			}
			
			log.info("creating the serviceCharge object....");
			Transaction transaction = null;
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(SubRegularWithEMoneyInquiry.getSourceMDN());
			sc.setDestMDN(null);
			sc.setChannelCodeId(cc.getId().longValue());
			sc.setServiceName(transactionDetails.getServiceName());
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUB_REGULAR_WITH_EMONEY);
			sc.setTransactionAmount(BigDecimal.ZERO);
			sc.setTransactionLogId(transactionLog.getId().longValue());
			sc.setTransactionIdentifier(SubRegularWithEMoneyInquiry.getTransactionIdentifier());
			
			

			try{
				transaction =transactionChargingService.getCharge(sc);
			}catch (InvalidServiceException e) {
				log.error("Exception occured in getting charges",e);
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			} catch (InvalidChargeDefinitionException e) {
				log.error(e.getMessage());
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
				return result;
			}
			
			 Pocket emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_SVA,
	            		CmFinoFIX.Commodity_Money);
			 
			 if(emoneyPocket != null && emoneyPocket.getStatus().intValue() == CmFinoFIX.PocketStatus_Active){
					result.setNotificationCode(CmFinoFIX.NotificationCode_EmoneyPocketAlreadyExists);
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
	     			 return result;
				}
			 
			 Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, 
	            		CmFinoFIX.Commodity_Money);
	            if(bankPocket ==null){
					result.setNotificationCode(CmFinoFIX.NotificationCode_BankPocketNotFound);
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
	     			 return result;
				}
	            ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
	            result.setNotificationCode(CmFinoFIX.NotificationCode_EmoneyPocketAddingInquiry);
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
				result.setSctlID(sctl.getId());
				if(mfaService.isMFATransaction(transactionDetails.getServiceName(),ServiceAndTransactionConstants.TRANSACTION_SUB_REGULAR_WITH_EMONEY, cc.getId().longValue()) == true){
					
					result.setMfaMode("OTP");
				 }
				return result;
		
		}
		log.error("Source subscriber with mdn : "+transactionDetails.getSourceMDN()+" not found");
		result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		return result;
	}
}
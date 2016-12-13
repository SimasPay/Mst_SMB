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
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMForgotPinInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.ForgotPinInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Srikanth
 * 
 */
@Service("ForgotPinInquiryHandlerImpl")
public class ForgotPinInquiryHandlerImpl extends FIXMessageHandler implements ForgotPinInquiryHandler{
	private static Logger	log	= LoggerFactory.getLogger(ForgotPinInquiryHandlerImpl.class);
    
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
		
		CMForgotPinInquiry forgotPinInquiry= new CMForgotPinInquiry();		
		forgotPinInquiry.setSourceMDN(transactionDetails.getSourceMDN());			
		forgotPinInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		forgotPinInquiry.setChannelCode(cc.getChannelcode());
		forgotPinInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());				
		log.info("Handling Forgot pin inquiry webapi request for MDN: " +  forgotPinInquiry.getSourceMDN());
		XMLResult result = new ChangeEmailXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);

		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ForgotPinInquiry, 
				forgotPinInquiry.DumpFields());
		forgotPinInquiry.setTransactionID(transactionLog.getId().longValue());

		result.setSourceMessage(forgotPinInquiry);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId().longValue());
		
		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(forgotPinInquiry.getSourceMDN());
		
		log.info("creating the serviceCharge object....");
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(forgotPinInquiry.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(transactionDetails.getServiceName());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_FORGOTPIN);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionLog.getId().longValue());
		sc.setTransactionIdentifier(forgotPinInquiry.getTransactionIdentifier());

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
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		if(subscriberMDN!=null){
			Subscriber subscriber = subscriberMDN.getSubscriber();
			
			if(transactionDetails.getSecurityQuestion().equalsIgnoreCase(subscriber.getSecurityquestion())){
				String answer = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), transactionDetails.getSecurityAnswer());
				if(answer.equalsIgnoreCase(subscriber.getSecurityanswer()))
				{
					result.setNotificationCode(CmFinoFIX.NotificationCode_ForgotPinInquiryCompleted);
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
					result.setSctlID(sctl.getId());
					
					if(mfaService.isMFATransaction(transactionDetails.getServiceName(),ServiceAndTransactionConstants.TRANSACTION_FORGOTPIN, cc.getId().longValue()) == true){
					
						result.setMfaMode("OTP");
					 }
					return result;
				}
				else{
					log.error("Subscriber with mdn : "+forgotPinInquiry.getSourceMDN()+" has wrong security answer");
					result.setNotificationCode(CmFinoFIX.NotificationCode_ForgotPinInquiryFailed);
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					return result;
				}
			}
			else{
				log.error("Subscriber with mdn : "+forgotPinInquiry.getSourceMDN()+" has wrong security question");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ForgotPinInquiryFailed);
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				return result;
			}
		}
		log.error("Source subscriber with mdn : "+forgotPinInquiry.getSourceMDN()+" has failed validations");
		result.setNotificationCode(CmFinoFIX.NotificationCode_ForgotPinInquiryFailed);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		return result;
	}
}
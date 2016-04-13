/**
 * 
 */
package com.mfino.transactionapi.handlers.account.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.errorcodes.Codes;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberClosing;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.MFAService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.SubscriberClosingHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ValidationUtil;

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
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		boolean isMfATransaction = false;
		
		ChannelCode channelCode = transactionDetails.getCc();
		
		if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_AGENT, ServiceAndTransactionConstants.TRANSACTION_CLOSE_ACCOUNT, channelCode.getID()) == true) {
			
			isMfATransaction = true;
			
		}
		
		CMJSSubscriberClosing subscriberClosing = new CMJSSubscriberClosing();
		subscriberClosing.setAgentMDN(transactionDetails.getSourceMDN());
		subscriberClosing.setDestMDN(transactionDetails.getDestMDN());
		
		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSSubscriberClosing,subscriberClosing.DumpFields());
		
		SubscriberMDN agentMDN = null;
		SubscriberMDN subMDN = subscriberMdnService.getByMDN(subscriberClosing.getDestMDN());
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		if(!transactionDetails.isSystemIntiatedTransaction()) {
			
			agentMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
			
			result.setCompany(agentMDN.getSubscriber().getCompany());
			
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
			
		} else {
			
			addCompanyANDLanguageToResult(subMDN, result);
			
		}
		
		if (subMDN != null) {
			
			boolean isHttps = transactionDetails.isHttps();
			boolean isHashedPin = transactionDetails.isHashedPin();
			
			String oneTimeOTP = transactionDetails.getActivationOTP();
			String mfaOneTimeOTP = transactionDetails.getTransactionOTP();
			Long parentTxnId = transactionDetails.getParentTxnId();
			
			ServiceChargeTransactionLog sctlForMFA = sctlService.getBySCTLID(parentTxnId);
			
			if(!transactionDetails.isSystemIntiatedTransaction() && isMfATransaction && !(mfaService.isValidOTP(mfaOneTimeOTP , sctlForMFA.getID(), agentMDN.getMDN()))){
				
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidData);
					
			} else {
					
				if(CmFinoFIX.NotificationCode_OTPValidationSuccessful.equals(ValidationUtil.validateOTP(subMDN, isHttps, isHashedPin, oneTimeOTP))) {
				
					int code = subscriberService.retireSubscriber(subMDN);
					
					if(code == Codes.SUCCESS) {
					
						subMDN.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
						subMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
						subMDN.setCloseComments(transactionDetails.getDescription());
						
						if(!transactionDetails.isSystemIntiatedTransaction()) {
							
							Partner partner = partnerService.getPartner(agentMDN);
							subMDN.setCloseAgent(String.valueOf(partner.getID()));
							
						} else {
							
							subMDN.setCloseUser(transactionDetails.getAuthorizedRepresentative());
						}
						
						Subscriber subscriber = subMDN.getSubscriber();
						subscriber.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
						
						SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
						subscriberDAO.save(subscriber);
						
						subMDN.setSubscriber(subscriber);
						
						SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
						subscriberMDNDAO.save(subMDN);
						
						subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
						
						result.setName(subscriber.getFirstName());
						result.setDestinationMDN(subMDN.getMDN());
						result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberClosingSuccess));
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
						result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingSuccess);
						
						log.debug("Subscriber state modifeid to retired....");
						
					} else {
						
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
						result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingFailed);
						
						log.debug("Subscriber state is not modified to retired due to some error....");
					}

					
				} else {
					
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingFailed);
					
					log.debug("Subscriber state is not modified to retired due to some error....");
				}
			}
			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			log.debug("Subscriber not found....");
		}		
		
		return result;
	}
}
/**
 * 
 */
package com.mfino.transactionapi.handlers.agent.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.domain.Partner;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAgentClosing;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.agent.AgentClosingHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ValidationUtil;

/**
 * @author Sunil
 *
 */
@Service("AgentClosingHandlerImpl")
public class AgentClosingHandlerImpl  extends FIXMessageHandler implements AgentClosingHandler {

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
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		CMJSAgentClosing agentClosing = new CMJSAgentClosing();
		agentClosing.setDestMDN(transactionDetails.getDestMDN());
		
		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSAgentClosing,agentClosing.DumpFields());
		
		SubscriberMdn subMDN = subscriberMdnService.getByMDN(agentClosing.getDestMDN());
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		Integer validationResult = transactionApiValidationService.validateSubscriberAsDestination(subMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return result;
		}
		
		Partner partner = partnerService.getPartner(subMDN);
		
		if (subMDN != null) {
			
			boolean isHttps = transactionDetails.isHttps();
			boolean isHashedPin = transactionDetails.isHashedPin();
			String oneTimeOTP = transactionDetails.getActivationOTP();
			
			if(CmFinoFIX.NotificationCode_OTPValidationSuccessful.equals(ValidationUtil.validateOTP(subMDN, isHttps, isHashedPin, oneTimeOTP))) {
				
				partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_Validated);
				
				result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingInquirySuccess));
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
				result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingInquirySuccess);

			} else {
				
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingInquiryFailed);
				
				partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_Failed);
				
				log.info("Agent state is not modified to retired due to otp validation failure....");
			}
			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_Failed);
			
			log.info("Agent not found....");
		}
		
		partner.setClosecomments(transactionDetails.getDescription());
		partnerService.savePartner(partner);
		
		return result;
	}
}
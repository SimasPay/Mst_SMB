/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFAAuthenticationDAO;
import com.mfino.dao.query.MFAAuthenticationQuery;
import com.mfino.domain.MFAAuthentication;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSResendMFAOTP;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.MFAService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.ResendMFAOTPHandler;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.ResendMFAOTPXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
@Service("ResendMFAOTPHandlerImpl")
public class ResendMFAOTPHandlerImpl  extends FIXMessageHandler implements ResendMFAOTPHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
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
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling Resend MFA OTP webapi request");
		ResendMFAOTPXMLResult result = new ResendMFAOTPXMLResult();
		
		CMJSResendMFAOTP resendMfaOtp = new CMJSResendMFAOTP();
		resendMfaOtp.setSourceMDN(transactionDetails.getSourceMDN());
		resendMfaOtp.setSctlId(transactionDetails.getParentTxnId());
		
		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSResendMFAOTP,resendMfaOtp.DumpFields());
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		SubscriberMDN sourceMDN = null;
		
		sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		result.setCompany(sourceMDN.getSubscriber().getCompany());
			
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(sourceMDN, transactionDetails.getSourcePIN());
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)-sourceMDN.getWrongPINCount());
			return result;
		}
		
		int noOfRetryAttemptsForMFAOTP = systemParametersService.getInteger(SystemParameterKeys.MAXIMUM_NO_OF_RETRIE_ATTEMPTS);
		Long sctlid = transactionDetails.getSctlId();
		
		MFAAuthenticationQuery query = new MFAAuthenticationQuery();
		query.setSctlId(sctlid);
		
		MFAAuthenticationDAO authDAO = DAOFactory.getInstance().getMfaAuthenticationDAO();
		List<MFAAuthentication> mfaResults = authDAO.get(query);
		
		if(CollectionUtils.isEmpty(mfaResults)) {
			
			mfaService.handleMFATransaction(sctlid, sourceMDN.getMDN());
		
		} else {
		
			MFAAuthentication mfaAuthentication = mfaResults.get(0);
			
			if(mfaAuthentication.getRetryAttempt() >= (noOfRetryAttemptsForMFAOTP - 1)) {
				
				result.setNotificationCode(CmFinoFIX.NotificationCode_MFAOTPResendMaxRetryAttempts);
				result.setCode(String.valueOf(CmFinoFIX.NotificationCode_MFAOTPResendMaxRetryAttempts));
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				
				return result;
			}
			
			mfaService.resendHandleMFATransaction(sctlid, resendMfaOtp.getSourceMDN(), mfaAuthentication.getRetryAttempt());
		}
		
		result.setNotificationCode(CmFinoFIX.NotificationCode_MFAOTPResendSuccessfullyCompleted);
		result.setCode(String.valueOf(CmFinoFIX.NotificationCode_MFAOTPResendSuccessfullyCompleted));
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		
		return result;
	}
}
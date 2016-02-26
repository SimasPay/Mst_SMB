package com.mfino.transactionapi.handlers.agent.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ProductReferralDAO;
import com.mfino.domain.ProductReferral;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRProductReferral;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.agent.ProductReferralHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ProductReferralXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author hari
 *
 */
@Service("ProductReferralHandlerImpl")
public class ProductReferralHandlerImpl extends FIXMessageHandler implements
		ProductReferralHandler {

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
	private SystemParametersService systemParametersService;

	@Override
	public Result handle(TransactionDetails transactionDetails) {

		log.info("Handling product referral services  webapi request");
		ProductReferralXMLResult result = new ProductReferralXMLResult();

		CRProductReferral pReferral = new CRProductReferral();
		pReferral.setAgentMDN(transactionDetails.getSourceMDN());
		pReferral.setFullName(transactionDetails.getFullName());
		pReferral.setSubscriberMDN(transactionDetails.getDestMDN());
		pReferral.setEmail(transactionDetails.getEmail());
		pReferral.setProductDesired(transactionDetails.getProductDesired());
		pReferral.setOthers(transactionDetails.getOthers());

		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ProductReferral, pReferral.DumpFields());
		
		
		SubscriberMDN agentMDN = subscriberMdnService.getByMDN(pReferral.getAgentMDN());
		
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
		
		
		SubscriberMDN subMDN = subscriberMdnService.getByMDN(pReferral.getSubscriberMDN());
		
		if(null == subMDN) {
			
			result.setCode(String.valueOf(CmFinoFIX.NotificationCode_ProductReferralFailed));
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ProductReferralFailed);	
			
			return result;
		}
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		ProductReferral productReferral = new ProductReferral();
		productReferral.setAgentMDN(transactionDetails.getSourceMDN());
		productReferral.setFullName(transactionDetails.getFullName());
		productReferral.setSubscriberMDN(transactionDetails.getDestMDN());
		productReferral.setEmail(transactionDetails.getEmail());
		productReferral.setProductDesired(transactionDetails.getProductDesired());
		productReferral.setOthers(transactionDetails.getOthers());						
		ProductReferralDAO productReferralDAO = DAOFactory.getInstance().getProductReferralDAO();
		
		try{
			
			productReferralDAO.save(productReferral);
			
		} catch(Exception ex) {
			
			result.setCode(String.valueOf(CmFinoFIX.NotificationCode_ProductReferralFailed));
			log.error("Exception occured in saving product referral ",ex);
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ProductReferralFailed);	
			
			return result;
		}
		
						
		result.setCode(String.valueOf(CmFinoFIX.NotificationCode_ProductReferralSuccess));
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		result.setNotificationCode(CmFinoFIX.NotificationCode_ProductReferralSuccess);						
						
		result.setAgentMDN(transactionDetails.getSourceMDN());
		result.setFullName(transactionDetails.getFullName());
		result.setSubscriberMDN(transactionDetails.getDestMDN());
		result.setEmail(transactionDetails.getEmail());
		result.setProductDesired(transactionDetails.getProductDesired());
		result.setOthers(transactionDetails.getOthers());
		result.setTransID(productReferral.getID().toString());
		return result;
										
	}		
}

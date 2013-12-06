/**
 * 
 */
package com.mfino.transactionapi.handlers.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.ChannelCodeService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.TransactionRequestHandler;
import com.mfino.transactionapi.result.xmlresulttypes.XMLError;
import com.mfino.transactionapi.service.AccountAPIServices;
import com.mfino.transactionapi.service.ActorChannelValidationService;
import com.mfino.transactionapi.service.AgentAPIServices;
import com.mfino.transactionapi.service.BankAPIService;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.BuyAPIService;
import com.mfino.transactionapi.service.MShoppingAPIService;
import com.mfino.transactionapi.service.PaymentAPIService;
import com.mfino.transactionapi.service.WalletAPIService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
@Service("TransactionRequestHandlerImpl")
public class TransactionRequestHandlerImpl implements TransactionRequestHandler{

	@Autowired
	@Qualifier("AccountAPIServicesImpl")
	private AccountAPIServices accountAPIServices ;
	
	@Autowired
	@Qualifier("WalletAPIServiceImpl")
	private WalletAPIService walletAPIService;

	@Autowired
	@Qualifier("AgentAPIServicesImpl")
	private AgentAPIServices agentAPIServices ;

	@Autowired	
	@Qualifier("BankAPIServiceImpl")
	private BankAPIService bankAPIService ;

	@Autowired
	@Qualifier("MShoppingAPIServiceImpl")
	private MShoppingAPIService mShoppingAPIService ;

	@Autowired
	@Qualifier("BuyAPIServiceImpl")
	BuyAPIService buyAPIService;

	@Autowired
	@Qualifier("PaymentAPIServiceImpl")
	PaymentAPIService paymentAPIService ;

	@Autowired
	@Qualifier("ActorChannelValidationServiceImpl")
	private ActorChannelValidationService actorChannelValidationService;

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public XMLResult process(TransactionDetails transactionDetails) {
		ChannelCode cc = channelCodeService.getChannelCodeByChannelCode(transactionDetails.getChannelCode());
		transactionDetails.setCc(cc);
		
		XMLResult xmlResult = null;
		BaseAPIService service = null ;
		try {
			if (transactionDetails != null) {
			 
				if (ServiceAndTransactionConstants.SERVICE_ACCOUNT.equals(transactionDetails.getServiceName())) {
					service = (BaseAPIService) accountAPIServices;
				}
				else if (ServiceAndTransactionConstants.SERVICE_WALLET.equals(transactionDetails.getServiceName())) {
				
					service = (BaseAPIService) walletAPIService;
				}
				else if (ServiceAndTransactionConstants.SERVICE_AGENT.equals(transactionDetails.getServiceName())) {
					
					service = (BaseAPIService) agentAPIServices;
				}
				else if (ServiceAndTransactionConstants.SERVICE_BANK.equals(transactionDetails.getServiceName())) {
					
					service = (BaseAPIService) bankAPIService;
				}
				else if (ServiceAndTransactionConstants.SERVICE_SHOPPING.equals(transactionDetails.getServiceName())){
					
					service = (BaseAPIService) mShoppingAPIService;
				}
				else if(ApiConstants.SERVICE_BUY.equals(transactionDetails.getServiceName()))
				{
					service = (BaseAPIService) buyAPIService;
				}
				else if(ApiConstants.SERVICE_PAYMENT.equals(transactionDetails.getServiceName()))
				{
					service = (BaseAPIService) paymentAPIService;
				}
				else{
					// Not a valid mode provided by the requester.
					throw new InvalidDataException("InvalidService", CmFinoFIX.NotificationCode_FeatureNotAvailable,transactionDetails.getServiceName());
				}
				boolean isTransactionApproved = actorChannelValidationService.validateTransaction(transactionDetails);
				
				if(isTransactionApproved){
					xmlResult = service.handleRequest(transactionDetails);
				}else if(!isTransactionApproved){
					throw new InvalidDataException("ActorChannelMapping", CmFinoFIX.NotificationCode_ActorChannelMapping,transactionDetails.getServiceName());
				}
		
			}
		} catch (InvalidDataException dataEx) {
			log.error(dataEx.getLogMessage());
			xmlResult = getXMLError(dataEx.getNotificationCode(), transactionDetails.getSourceMDN(), dataEx.getKeyValueMap());
		}

		return xmlResult;
	}

	public static XMLResult getXMLError(Integer notificationCode, String SourceMDN, String parameterName) {
		XMLResult result = new XMLError();
		result.setNotificationCode(notificationCode);
		result.setTransactionTime(new Timestamp());
		return result;
	}
	
	public static XMLResult getXMLError(Integer notificationCode, String SourceMDN, Map<Integer, Object> keyParameterMap) {
		XMLResult result = new XMLError();
		result.setNotificationCode(notificationCode);
		result.setTransactionTime(new Timestamp());
		
		if(keyParameterMap != null){
			for(Integer key: keyParameterMap.keySet()){
				if(CmFinoFIX.NotificationVariables_minAmount.equals(key)){
					result.setMinAmount((BigDecimal)keyParameterMap.get(key));
				}
				if(CmFinoFIX.NotificationVariables_maxAmount.equals(key)){
					result.setMaxAmount((BigDecimal)keyParameterMap.get(key));
				}
			}
		}
		return result;
	}
}

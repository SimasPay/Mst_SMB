/**
 * 
 */
package com.mfino.transactionapi.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.handlers.mobileshopping.PurchaseConfirmHandler;
import com.mfino.transactionapi.handlers.mobileshopping.PurchaseInquiryHandler;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.MShoppingAPIService;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * All Transactions Related to Shopping Service 
 * Purchase (Buy)
 * 
 * @author Bala Sunku
 *
 */
@Service("MShoppingAPIServiceImpl")
public class MShoppingAPIServiceImpl extends BaseAPIService implements MShoppingAPIService{
	
	@Autowired
	@Qualifier("PurchaseInquiryHandlerImpl")
	private PurchaseInquiryHandler purchaseInquiryHandler;
	
	@Autowired
	@Qualifier("PurchaseConfirmHandlerImpl")
	private PurchaseConfirmHandler purchaseConfirmHandler;

	@Autowired
	@Qualifier("TransactionRequestValidationServiceImpl")
	private TransactionRequestValidationService transactionRequestValidationService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	public XMLResult handleRequest(TransactionDetails transactionDetails) throws InvalidDataException {
		XMLResult xmlResult = null;

		String sourceMessage = transactionDetails.getSourceMessage();
		String transactionName = transactionDetails.getTransactionName();
		ChannelCode channelCode = transactionDetails.getCc();
	
		if (ServiceAndTransactionConstants.TRANSACTION_PURCHASE_INQUIRY.equalsIgnoreCase(transactionName)) {
		
			transactionRequestValidationService.validatePurchaseInquiryDetails(transactionDetails);
			if (StringUtils.isBlank(sourceMessage)) {
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_PURCHASE);
			}
		
			xmlResult = (XMLResult) purchaseInquiryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_PURCHASE.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validatePurchaseConfirmDetails(transactionDetails);
			transactionDetails.setCc(channelCode);

			xmlResult = (XMLResult) purchaseConfirmHandler.handle(transactionDetails);
		}
		else
		{
			xmlResult = new XMLResult();
			Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
			xmlResult.setLanguage(language);
			xmlResult.setTransactionTime(new Timestamp());
			xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
		}
		return xmlResult;
	}

}

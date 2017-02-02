/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberClosing;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberEMoneyClosingHandler;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceConfirmHandler;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
@Service("SubscriberEMoneyClosingHandlerImpl")
public class SubscriberEMoneyClosingHandlerImpl  extends FIXMessageHandler implements SubscriberEMoneyClosingHandler {

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
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("MoveBalanceInquiryHandlerImpl")
	private MoveBalanceInquiryHandler moveBalanceInquiryHandler;
	
	@Autowired
	@Qualifier("MoveBalanceConfirmHandlerImpl")
	private MoveBalanceConfirmHandler moveBalanceConfirmHandler;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	boolean isMoneyAvailable = false;
	boolean isBankPocketAvailable = false;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		CMJSSubscriberClosing subscriberClosing = new CMJSSubscriberClosing();
		subscriberClosing.setDestMDN(transactionDetails.getDestMDN());
		
		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSSubscriberClosing,subscriberClosing.DumpFields());
		
		SubscriberMdn subMDN = subscriberMdnService.getByMDN(String.valueOf(subscriberClosing.getDestMDN()));
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		if (subMDN != null) {
			
				try {
				
					transactionDetails.setCloseAccountStatus(String.valueOf(CmFinoFIX.CloseAcctStatus_Approve));
					
					if(moveMoneyToTreasuaryAndRetirePockets(transactionDetails, subMDN.getId().longValue())) {
						
						if(!isBankPocketAvailable) {
							
							Subscriber subscriber = subMDN.getSubscriber();
							subscriber.setStatus(CmFinoFIX.SubscriberStatus_Retired);
							
							SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
							subscriberDAO.save(subscriber);
							
							subMDN.setMdn(subMDN.getMdn() + "R");
							subMDN.setStatus(CmFinoFIX.SubscriberStatus_Retired);
							subMDN.setSubscriber(subscriber);
							
							SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
							subscriberMDNDAO.save(subMDN);
							
							subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
						}
						
						result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberClosingSuccess));
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
						result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingSuccess);
						
						log.info("Subscriber state modifeid to retired....");
						
					} else {
						
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
						result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingFailed);
						
						log.info("Failed due to Subscriber money movenent to Treasuary....");
					}
				} catch (Exception ex) {
					
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingFailed);
					
					log.info("Subscriber state is not modified to retired due to some error....");
				}
			} else {
				
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingFailed);
				
				log.info("Subscriber state is not modified to retired due to not valid state....");
			}
			
		return result;
	}
	
	private boolean moveMoneyToTreasuaryAndRetirePockets(TransactionDetails transactionDetails, Long mdnId) {
        
		boolean isMoneyMoved = true;
		
		isMoneyAvailable = false;
		
		Pocket destNationalTreasuryPocket = pocketService.getById(systemParametersService.getLong(SystemParameterKeys.NATIONAL_TREASURY_POCKET));
		SubscriberMdn destMDN = null;
		
		if(destNationalTreasuryPocket != null){
			
			destMDN = destNationalTreasuryPocket.getSubscriberMdn();
			
			if(destMDN == null){
				
				log.info("Failed to move money from system collector pocket to National Treasury as pocket code is not set for National Treasury in System Parameters for subscriber ID -->" + mdnId) ;
				return isMoneyMoved;
			}
		
		} else{
			
			log.info("Fail to move money from system collector pocket to National Treasury as pocket code is not set for National Treasury in System Parameters for subscriber ID -->" + mdnId) ;
			return isMoneyMoved;
		}
		
		transactionDetails.setSourceMDN(transactionDetails.getDestMDN());
		transactionDetails.setSourcePocketCode(String.valueOf(CmFinoFIX.PocketType_SVA));
		transactionDetails.setDestMDN(destMDN.getMdn());
		transactionDetails.setDestPocketCode(String.valueOf(destNationalTreasuryPocket.getPocketTemplateByPockettemplateid().getType()));
		transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY);
		transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		transactionDetails.setSourcePIN("1");
		
		// Here we need to get the Records from pocket table for MdnId.
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();			
        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setMdnIDSearch(mdnId);
        
        List<Pocket> resultantPockets = pocketDAO.get(pocketQuery);

        for (Pocket eachPocket : resultantPockets) {
        	
        	if(eachPocket.getPocketTemplateByPockettemplateid().getType() == (CmFinoFIX.PocketType_BankAccount.longValue())) {
        	
        		isBankPocketAvailable = true;
        		continue;
        	}

        	if(!(eachPocket.getCurrentbalance().compareTo(new BigDecimal(0)) == 0) && eachPocket.getCurrentbalance().compareTo(BigDecimal.valueOf(systemParametersService.getInteger(SystemParameterKeys.MAXIMUM_SUBSCRIBER_CLOSING_AMOUNT))) <= 0) {
        		
        		transactionDetails.setAmount(eachPocket.getCurrentbalance());
        		
	        	if(moveMoneyToNationalTreasury(transactionDetails, destMDN)) {
	        			
	        		isMoneyMoved = true;
	        		isMoneyAvailable = true;
	        		
        		} else {
        			
        			isMoneyMoved = false;
        			break;
        		}
	        	
        	} else {
        		
        		String cardPanStringToReplace = null;
                String cardPan = eachPocket.getCardpan();
                
                if (StringUtils.isNotBlank(cardPan)) {
                	
                	cardPanStringToReplace = cardPan + "R";
                }

                if (StringUtils.isBlank(cardPanStringToReplace)) {
                	
                	cardPanStringToReplace = cardPan;
                }

                eachPocket.setCardpan(cardPanStringToReplace);
                eachPocket.setStatus(CmFinoFIX.PocketStatus_Retired);
                eachPocket.setIsdefault(CmFinoFIX.Boolean_False);
                
                pocketDAO.save(eachPocket);
                
                isMoneyMoved = true;
        	}
        }
        
        return isMoneyMoved;
	}
	
	private XMLResult sendMoneyTransferInquiry(TransactionDetails txnDetails) {

		log.info("Subscriber Closure Confirm::sendMoneyTransferInquiry :Begin");
		
		XMLResult xmlResult = null;
		
		txnDetails.setSystemIntiatedTransaction(true);
		xmlResult = (XMLResult) moveBalanceInquiryHandler.handle(txnDetails);

		log.info("Inquiry Response for Retired Subscriber " + " got result: " + xmlResult);
		log.info("Subscriber Closure Confirm::sendMoneyTransferInquiry :End");
		
		return xmlResult;
	}
		
	private XMLResult sendMoneyTransferConfirm(TransactionDetails txnDetails) {
		
		log.info("Subscriber Closure Confirm::sendMoneyTransferConfirm :Begin");
		log.info("Sending TransferConfirm for Settlement of Retired subscriber ");
		
		XMLResult xmlResult = null;
		
		txnDetails.setSystemIntiatedTransaction(true);
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
		txnDetails.setCc(txnDetails.getCc());

		xmlResult = (XMLResult) moveBalanceConfirmHandler.handle(txnDetails);
		log.info("TransferConfirm return code for Settlement of Retired subscriber " + " and the result is: " + xmlResult);
		log.info("Subscriber Closure Confirm::sendMoneyTransferConfirm :End");
		
		return xmlResult;
	}
	
	private boolean moveMoneyToNationalTreasury(TransactionDetails txnDetails, SubscriberMdn mdn) {
		
		XMLResult inquiryResult;
		XMLResult confirmResult;
		
		log.info("Subscriber Closure Confirm::moveMoneyToNationalTreasury :Begin");
		log.info("Sending Money transfer Inquiry for subscriber MDN ID -->" + mdn.getId());
		
		//txnDetails.set
		inquiryResult = sendMoneyTransferInquiry(txnDetails);
		
		if (inquiryResult != null && !CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(inquiryResult.getCode())) {
			
			log.info("Inquiry for money transfer failed with notification code :" + inquiryResult.getNotificationCode());
			return false;
		}

		txnDetails.setConfirmString("true");
		txnDetails.setTransferId(inquiryResult.getTransferID());
		txnDetails.setParentTxnId(inquiryResult.getParentTransactionID());
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY);
		
		log.info("Sending Money transfer Confirm for subscriber MDN ID -->" + mdn.getId());
		
		confirmResult = sendMoneyTransferConfirm(txnDetails);

		if (confirmResult != null && !(CmFinoFIX.NotificationCode_BankAccountToBankAccountCompletedToSenderMDN.toString().equals(confirmResult.getCode())
				|| CmFinoFIX.NotificationCode_EMoneytoEMoneyCompleteToSender.toString().equals(confirmResult.getCode()))) {
			
			log.info("Confirm for money transfer failed with notification code :" + confirmResult.getCode() + " while moving to National Treasury for subscriber--> " + mdn.getId());
			
			return false;
		}

		log.info("Money transfer successful with notification code :" + confirmResult.getCode() + " for subscriber closure ");
		log.info("Subscriber Closure Confirm::moveMoneyToNationalTreasury :End");
		
		return true;

	}
}
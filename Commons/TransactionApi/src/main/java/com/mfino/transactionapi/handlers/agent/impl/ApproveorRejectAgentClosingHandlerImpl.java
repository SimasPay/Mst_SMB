/**
 * 
 */
package com.mfino.transactionapi.handlers.agent.impl;

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
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAgentCloseApproveReject;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.agent.ApproveorRejectAgentClosingHandler;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceConfirmHandler;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
@Service("ApproveorRejectAgentClosingHandlerImpl")
public class ApproveorRejectAgentClosingHandlerImpl  extends FIXMessageHandler implements ApproveorRejectAgentClosingHandler {

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
	@Qualifier("MoveBalanceInquiryHandlerImpl")
	private MoveBalanceInquiryHandler moveBalanceInquiryHandler;
	
	@Autowired
	@Qualifier("MoveBalanceConfirmHandlerImpl")
	private MoveBalanceConfirmHandler moveBalanceConfirmHandler;
	
	boolean isMoneyAvailable = false;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		CMJSAgentCloseApproveReject agentClosing = new CMJSAgentCloseApproveReject();
		agentClosing.setMDNID(Long.parseLong(transactionDetails.getDestMDN()));
		
		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSAgentCloseApproveReject,agentClosing.DumpFields());
		
		SubscriberMdn subMDN = subscriberMdnService.getByMDN(String.valueOf(agentClosing.getMDNID()));
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		Partner partner = partnerService.getPartner(subMDN);
		
		if (subMDN != null) {
			
			if(CmFinoFIX.CloseAcctStatus_Validated.equals(partner.getCloseAcctStatus())) {
			
				try {
				
					partner.setCloseaccttime(new Timestamp());
					partner.setCloseapprovercomments(transactionDetails.getDescription());
					partner.setCloseacctapprovedby(transactionDetails.getAuthorizedRepresentative());
					
					if(transactionDetails.getCloseAccountStatus().equals(String.valueOf(CmFinoFIX.CloseAcctStatus_Approve))) {
						
						partner.setCloseacctstatus(new BigDecimal(CmFinoFIX.CloseAcctStatus_Approve));
						transactionDetails.setCloseAccountStatus(String.valueOf(CmFinoFIX.CloseAcctStatus_Approve));
						
						if(moveMoneyToTreasuaryAndRetirePockets(transactionDetails, subMDN.getId())) {
							
							Subscriber subscriber = subMDN.getSubscriber();
							
							subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
							
							SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
							subscriberDAO.save(subscriber);
							
							result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingSuccess));
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
							result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingSuccess);
							
							log.info("Agent state modifeid to retired....");
							
						} else {
							
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
							result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingFailed);
							
							partner.setCloseacctstatus(new BigDecimal(CmFinoFIX.CloseAcctStatus_Failed));
							
							log.info("Failed due to Agent money movenent to Treasuary....");
						}
					} else {
					
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
						result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingRequestRejectedByApprover);
						result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingRequestRejectedByApprover));
						
						partner.setCloseacctstatus(new BigDecimal(CmFinoFIX.CloseAcctStatus_Reject));
						transactionDetails.setCloseAccountStatus(String.valueOf(CmFinoFIX.CloseAcctStatus_Reject));
						
						log.info("Agent closing rejected by the Approver....");
					}					
				} catch (Exception ex) {
					
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingFailed);
					
					partner.setCloseacctstatus(new BigDecimal(CmFinoFIX.CloseAcctStatus_Failed));
					
					log.info("Agent state is not modified to retired due to some error....");
				}
			} else {
				
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingFailed);
				
				partner.setCloseacctstatus(new BigDecimal(CmFinoFIX.CloseAcctStatus_Failed));
				
				log.info("Agent state is not modified to retired due to not valid state....");
			}
			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			partner.setCloseacctstatus(new BigDecimal(CmFinoFIX.CloseAcctStatus_Failed));
			
			log.info("Agent not found....");
		}
		
		if(!isMoneyAvailable) {
		
			partnerService.savePartner(partner);
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
		transactionDetails.setDestPocketCode(String.valueOf(destNationalTreasuryPocket.getPocketTemplate().getType()));
		transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY);
		transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		transactionDetails.setSourcePIN("1");
		
		// Here we need to get the Records from pocket table for MdnId.        
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();			
        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setMdnIDSearch(mdnId);
        
        List<Pocket> resultantPockets = pocketDAO.get(pocketQuery);

        for (Pocket eachPocket : resultantPockets) {
        	
        	if(eachPocket.getPocketTemplate().getType() == (CmFinoFIX.PocketType_BankAccount.longValue())) {
        		
        		continue;
        	}

        	if(!(new BigDecimal(eachPocket.getCurrentbalance()).compareTo(new BigDecimal(0)) == 0) && new BigDecimal(eachPocket.getCurrentbalance()).compareTo(BigDecimal.valueOf(systemParametersService.getInteger(SystemParameterKeys.MAXIMUM_AGENT_CLOSING_AMOUNT))) <= 0) {
        		
        		transactionDetails.setAmount(new BigDecimal(eachPocket.getCurrentbalance()));
        		
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
                eachPocket.setIsdefault(Short.valueOf("0"));
                
                pocketDAO.save(eachPocket);
                
                isMoneyMoved = true;
        	}
        }
        
        retirePartner(mdnId, transactionDetails);
               
        return isMoneyMoved;
	}
	
	private XMLResult sendMoneyTransferInquiry(TransactionDetails txnDetails) {

		log.info("Agent Closure Confirm::sendMoneyTransferInquiry :Begin");
		
		XMLResult xmlResult = null;
		
		txnDetails.setSystemIntiatedTransaction(true);
		xmlResult = (XMLResult) moveBalanceInquiryHandler.handle(txnDetails);

		log.info("Inquiry Response for Retired Subscriber " + " got result: " + xmlResult);
		log.info("Agent Closure Confirm::sendMoneyTransferInquiry :End");
		
		return xmlResult;
	}
		
	private XMLResult sendMoneyTransferConfirm(TransactionDetails txnDetails) {
		
		log.info("Agent Closure Confirm::sendMoneyTransferConfirm :Begin");
		log.info("Sending TransferConfirm for Settlement of Retired subscriber ");
		
		XMLResult xmlResult = null;
		
		txnDetails.setSystemIntiatedTransaction(true);
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
		txnDetails.setCc(txnDetails.getCc());

		xmlResult = (XMLResult) moveBalanceConfirmHandler.handle(txnDetails);
		log.info("TransferConfirm return code for Settlement of Retired subscriber " + " and the result is: " + xmlResult);
		log.info("Agent Closure Confirm::sendMoneyTransferConfirm :End");
		
		return xmlResult;
	}
	
	private boolean moveMoneyToNationalTreasury(TransactionDetails txnDetails, SubscriberMdn mdn) {
		
		XMLResult inquiryResult;
		XMLResult confirmResult;
		
		log.info("Agent Closure Confirm::moveMoneyToNationalTreasury :Begin");
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

		log.info("Money transfer successful with notification code :" + confirmResult.getCode() + " for agent closure ");
		log.info("Agent Closure Confirm::moveMoneyToNationalTreasury :End");
		
		return true;

	}
	
	public void retirePartner(Long subscriberId, TransactionDetails transactionDetails) {
		
		PartnerQuery partnerQuery = new PartnerQuery();
		partnerQuery.setSubscriberID(subscriberId);

		PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
		List<Partner> partnerLst = partnerDAO.get(partnerQuery);
		Partner partner = partnerLst.get(0);
		
		if(CmFinoFIX.SubscriberStatus_Active.equals(partner.getPartnerstatus())) {
		
			partner.setTradename(partner.getTradename() + "R");
			partner.setCloseapprovercomments(transactionDetails.getDescription());
			partner.setCloseacctapprovedby(transactionDetails.getAuthorizedRepresentative());
			partner.setCloseaccttime(new Timestamp());
			partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Retired);
			
			partnerDAO.save(partner);
		}
	}
}
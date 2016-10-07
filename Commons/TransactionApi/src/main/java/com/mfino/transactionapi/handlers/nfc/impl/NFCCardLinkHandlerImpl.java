package com.mfino.transactionapi.handlers.nfc.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMNFCCardLink;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.nfc.NFCCardLinkHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Hemanth
 *
 */
@Service("NFCCardLinkHandlerImpl")
public class NFCCardLinkHandlerImpl extends FIXMessageHandler implements NFCCardLinkHandler{

	private static Logger log = LoggerFactory.getLogger(NFCCardLinkHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
		
	
	public Result handle(TransactionDetails transactionDetails) {
		ChannelCode cc =transactionDetails.getCc();
		CMNFCCardLink nfcCardLink = new CMNFCCardLink();
		nfcCardLink.setPin(transactionDetails.getSourcePIN());
		nfcCardLink.setSourceMDN(transactionDetails.getSourceMDN());
		//nfcCardLink.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		nfcCardLink.setChannelCode(cc.getChannelcode());
		nfcCardLink.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		nfcCardLink.setCardAlias(transactionDetails.getCardAlias());
		nfcCardLink.setSourceCardPAN(transactionDetails.getCardPAN());
		//nfcCardLink.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		if(StringUtils.isNotBlank(transactionDetails.getTransID()))
		nfcCardLink.setTransID(Long.parseLong(transactionDetails.getTransID()));
		log.info("Handling Card Link WebAPI request");
		
		XMLResult result = new NFCXMLResult();
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_NFCCardLink, nfcCardLink.DumpFields());
		nfcCardLink.setSourceApplication((int)cc.getChannelsourceapplication());
		nfcCardLink.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(nfcCardLink);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		result.setCardAlias(transactionDetails.getCardAlias());
		result.setCardPan(transactionDetails.getCardPAN());
		SubscriberMdn smdn = subscriberMdnService.getByMDN(nfcCardLink.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(smdn);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+nfcCardLink.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		addCompanyANDLanguageToResult(smdn, result);
		//check for duplicate cardpan
		PocketQuery query = new PocketQuery();
		query.setCardPan(transactionDetails.getCardPAN());
		List<Pocket> pocketList = pocketService.get(query);
		if (!pocketList.isEmpty()) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_Invalid_CardPan);
			return result;
		}
		//check if the pocket can be created		
		Subscriber subscriber = smdn.getSubscriber();
		Long groupID = null;
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		List<SubscriberGroup> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(subscriber.getId());
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroupid();
		}
		PocketTemplate nfcPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKycLevel().getKyclevel().longValue(), true, CmFinoFIX.PocketType_NFC, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		if(nfcPocketTemplate == null)
         {
			result.setNotificationCode(CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound);
			return result;
         }
				
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(nfcCardLink.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_LINK);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(nfcCardLink.getTransactionIdentifier());

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
		nfcCardLink.setServiceChargeTransactionLogID(sctl.getId().longValue());
		if(!(CmFinoFIX.SourceApplication_CMS.toString().equalsIgnoreCase(nfcCardLink.getChannelCode()))){
			log.info("NFCCardLinkHandler :: handle() Going to backend as request received from mobile channel");
			CFIXMsg response = super.process(nfcCardLink);

			// Changing the Service_charge_transaction_log status based on the response from Core engine. 
			TransactionResponse transactionResponse = checkBackEndResponse(response);
			if (transactionResponse.isResult() && sctl!=null) {
				// create pocket here as response is success
				createNFCPocket(nfcPocketTemplate,smdn,nfcCardLink);
				sctl.setCalculatedcharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
				result.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardLinkSuccess);
			} else {
				String errorMsg = ((CMJSError) response).getErrorDescription();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				if(sctl!=null){
					transactionChargingService.failTheTransaction(sctl, errorMsg);
				}
				result.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardLinkFailed);
			}		
		result.setMessage(transactionResponse.getMessage());
		}else if(CmFinoFIX.SourceApplication_CMS.toString().equalsIgnoreCase(nfcCardLink.getChannelCode())){
			log.info("NFCCardLinkHandler :: handle() Not Going to backend as request received from CMS channel");
			if(StringUtils.isNotBlank(transactionDetails.getTransID()))
			{
			sctl.setIntegrationtransactionid(new BigDecimal(transactionDetails.getTransID()));
			}
			createNFCPocket(nfcPocketTemplate,smdn,nfcCardLink);
			sctl.setCalculatedcharge(BigDecimal.ZERO);
			transactionChargingService.completeTheTransaction(sctl);
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
			result.setTransID(transactionDetails.getTransID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardLinkSuccess);
		}
		result.setAmount(BigDecimal.ZERO);
		result.setSctlID(sctl.getId().longValue());
		result.setCardPan(transactionDetails.getCardPAN());
		result.setSourceMDN(transactionDetails.getSourceMDN());
		return result;
		}
	private void createNFCPocket(PocketTemplate nfcPocketTemplate,SubscriberMdn subscriberMDN,CMNFCCardLink nfcCardLink) {
         pocketService.createPocket(nfcPocketTemplate, subscriberMDN, CmFinoFIX.PocketStatus_Active, false, nfcCardLink.getSourceCardPAN(),nfcCardLink.getCardAlias());
	}
}
	

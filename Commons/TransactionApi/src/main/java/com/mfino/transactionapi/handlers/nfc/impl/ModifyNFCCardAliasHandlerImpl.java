package com.mfino.transactionapi.handlers.nfc.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMModifyNFCCardAlias;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.handlers.nfc.ModifyNFCCardAliasHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.ModifyNFCCardAliasXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Amar
 * 
 */
@Service("ModifyNFCCardAliasHandlerImpl")
public class ModifyNFCCardAliasHandlerImpl extends FIXMessageHandler implements ModifyNFCCardAliasHandler{
	private static Logger	log	= LoggerFactory.getLogger(ModifyNFCCardAliasHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService tcs;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;


	public XMLResult handle(TransactionDetails transactionDetails) {
		ChannelCode cc = transactionDetails.getCc();	 
		 
		CMModifyNFCCardAlias modifyNFCCardAlias = new CMModifyNFCCardAlias();
		modifyNFCCardAlias.setPin(transactionDetails.getSourcePIN());
		modifyNFCCardAlias.setSourceMDN(transactionDetails.getSourceMDN());
		modifyNFCCardAlias.setCardPAN(transactionDetails.getCardPAN());	
		modifyNFCCardAlias.setCardAlias(transactionDetails.getCardAlias());	
		modifyNFCCardAlias.setSourceApplication((int)cc.getChannelsourceapplication());
		modifyNFCCardAlias.setChannelCode(cc.getChannelcode());
		modifyNFCCardAlias.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		log.info("Handling Modify NFC Card Alias webapi request");
		XMLResult result = new ModifyNFCCardAliasXMLResult();
		TransactionLogServiceImpl transactionLogService = new TransactionLogServiceImpl();
		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ModifyNFCCardAlias, modifyNFCCardAlias.DumpFields());
		modifyNFCCardAlias.setTransactionID(transactionLog.getId().longValue());

		result.setSourceMessage(modifyNFCCardAlias);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId().longValue());
		result.setCardPan(modifyNFCCardAlias.getCardPAN());

		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(modifyNFCCardAlias.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(subscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+modifyNFCCardAlias.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(subscriberMDN, modifyNFCCardAlias.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: " + modifyNFCCardAlias.getSourceMDN());
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongpincount()));
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket pocket = pocketService.getByCardPan(modifyNFCCardAlias.getCardPAN());
		if(pocket == null){
			log.info("NFC Pocket with given cardPAN doesn't exist");
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);			
			return result;
		}
		if (!(pocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			log.info("NFC Pocket with Card Pan " + transactionDetails.getCardPAN() +" is already unlinked");
			result.setNotificationCode(CmFinoFIX.NotificationCode_PocketAlreadyUnlinked);
			result.setCardPan(transactionDetails.getCardPAN());
			return result;
		}
		
		PocketQuery query = new PocketQuery();
		query.setCardAlias(transactionDetails.getCardAlias());
		query.setMdnIDSearch(subscriberMDN.getId().longValue());
		List<Pocket> pocketsWithGivenAlias = pocketService.get(query);
		if(pocketsWithGivenAlias != null && !pocketsWithGivenAlias.isEmpty())
		{
			log.info("A Pocket with Card Alias " + transactionDetails.getCardAlias() +" already exists for the subscriber");
			result.setNotificationCode(CmFinoFIX.NotificationCode_CardAliasAlreadyExists);
			result.setCardPan(transactionDetails.getCardPAN());
			result.setCardAlias(transactionDetails.getCardAlias());
			return result;
		}

 		addCompanyANDLanguageToResult(subscriberMDN, result);

		Transaction transaction = null;
		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(modifyNFCCardAlias.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(StringUtils.isNotBlank(modifyNFCCardAlias.getChannelCode()) ? Long.valueOf(modifyNFCCardAlias.getChannelCode()) : null);
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_MODIFY_NFC_CARD_ALIAS);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(modifyNFCCardAlias.getTransactionID());
		serviceCharge.setTransactionIdentifier(modifyNFCCardAlias.getTransactionIdentifier());

		try{
			transaction =tcs.getCharge(serviceCharge);
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
		result.setSctlID(sctl.getId().longValue());
		try {	
			result.setOldCardAlias(pocket.getCardalias());
			pocket.setCardalias(modifyNFCCardAlias.getCardAlias());
			pocketService.save(pocket);
		}
		catch (Exception ex) {
			log.error("Exception occured while modifying the Card Alias", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ModifyNFCCardAliasFailed);
			if (sctl != null) {
				tcs.failTheTransaction(sctl, MessageText._("Modify NFC Card Alias transaction Falied"));
			}
 			return result;
		}
		if (sctl != null) {
			sctl.setCalculatedcharge(BigDecimal.ZERO);
			tcs.completeTheTransaction(sctl);
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_ModifyNFCCardAliasSuccessful);
		result.setCardAlias(modifyNFCCardAlias.getCardAlias());
 		return result;

	}
}
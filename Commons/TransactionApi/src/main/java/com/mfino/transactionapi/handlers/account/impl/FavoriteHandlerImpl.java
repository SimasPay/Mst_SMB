package com.mfino.transactionapi.handlers.account.impl;

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
import com.mfino.dao.query.SubscriberFavoriteQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.FavoriteCategory;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberFavorite;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMFavoriteMessage;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.FavoriteCategoryService;
import com.mfino.service.SubscriberFavoriteService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.handlers.account.FavoriteHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Srikanth
 * 
 */
@Service("FavoriteHandlerImpl")
public class FavoriteHandlerImpl extends FIXMessageHandler implements FavoriteHandler{
	private static Logger	log	= LoggerFactory.getLogger(FavoriteHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService tcs;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("SubscriberFavoriteServiceImpl")
	private SubscriberFavoriteService subscriberFavoriteService;
	
	@Autowired
	@Qualifier("FavoriteCategoryServiceImpl")
	private FavoriteCategoryService favoriteCategoryService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;


	public XMLResult handle(TransactionDetails transactionDetails) {
		String transactionName = transactionDetails.getTransactionName();
		log.info("Handling Favorite webapi request with transactionName " + transactionName);
		ChannelCode cc = transactionDetails.getCc();
		String favCategoryID = transactionDetails.getFavoriteCategoryID();
 		Long favoriteCategoryID = Long.valueOf(favCategoryID);
		
 		//Create message and dump fields in transaction log table
		CMFavoriteMessage favoriteMessage = new CMFavoriteMessage();
		favoriteMessage.setPin(transactionDetails.getSourcePIN());
		favoriteMessage.setSourceMDN(transactionDetails.getSourceMDN());
		favoriteMessage.setFavoriteCategoryID(favoriteCategoryID);
		favoriteMessage.setFavoriteCode(transactionDetails.getFavoriteCode());
		favoriteMessage.setFavoriteLabel(transactionDetails.getFavoriteLabel());
		favoriteMessage.setFavoriteValue(transactionDetails.getFavoriteValue());
		favoriteMessage.setSourceApplication(new Integer(String.valueOf(cc.getChannelsourceapplication())));
		favoriteMessage.setChannelCode(cc.getChannelcode());
		favoriteMessage.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());		
		XMLResult result = new ChangeEmailXMLResult();
		TransactionLogServiceImpl transactionLogService = new TransactionLogServiceImpl();
		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_FavoriteMessage, favoriteMessage.DumpFields());		

		result.setSourceMessage(favoriteMessage);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId().longValue());
		// Subscriber MDN validation
		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(subscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+ transactionDetails.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		//Pin validation
		validationResult = transactionApiValidationService.validatePin(subscriberMDN, transactionDetails.getSourcePIN());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: " + transactionDetails.getSourceMDN());
			result.setNumberOfTriesLeft(new Integer(String.valueOf(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongpincount())));
			result.setNotificationCode(validationResult);
			return result;
		}
 		addCompanyANDLanguageToResult(subscriberMDN, result);		
 		//Check if the favorite category exists
 		Long subscriberID = subscriberMDN.getSubscriber().getId().longValue();
 		FavoriteCategory favoriteCategory = favoriteCategoryService.getByID(favoriteCategoryID);
 		if(favoriteCategory == null) {
 			log.error("Fav category with the ID: " + favoriteCategoryID + " not exists");
 			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing); 			
 			return result;
 		}
 		//Check if max favorites per category count reached or not
 		if(transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_ADD_FAVORITE)) {
 			int favoriteCount = subscriberFavoriteService.getFavoriteCountUnderCategory(subscriberID, Long.valueOf(favoriteCategoryID));
 	 		log.info("Number of favorites existing under category " + favoriteCategoryID + " is " + favoriteCount);
 	 		if(favoriteCount == systemParametersService.getInteger(SystemParameterKeys.MAX_FAVORITES_PER_CATEGORY)) {
 	 			log.error("Cannot add favorites as max value " + favoriteCount + " reached for category " + favoriteCategoryID);
 	 			result.setNotificationCode(CmFinoFIX.NotificationCode_MaxFavoritesPerCategoryCountReached);
 	 			result.setMaxFavoriteCount(String.valueOf(favoriteCount));
 	 			return result;
 	 		}
 		}
 		
 		SubscriberFavorite existingFavorite = null;
 		if(transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_ADD_FAVORITE)) {
 			result = validateAddFavorite(result, subscriberID, transactionDetails);
 			if(result.getNotificationCode() != null) {
 				return result;
 			}
 		} else if(transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_EDIT_FAVORITE)) {
 			existingFavorite = checkIfFavoriteExists(subscriberID, transactionDetails);
 			if(existingFavorite == null) {
 				log.error("Edit favorite failed - Favorite not exists");
 	 			result.setNotificationCode(CmFinoFIX.NotificationCode_FavoriteNotFound);
 	 			result.setFavoriteValue(transactionDetails.getFavoriteValue());
 	 			return result;
 			}
// 			if(!existingFavorite.getFavoriteLabel().equalsIgnoreCase(transactionDetails.getFavoriteLabel())) {
// 				result = checkDuplicateFavoriteWithLabel(result, subscriberID, transactionDetails);
// 	 			if(result.getNotificationCode() != null) {
// 	 				return result;
// 	 			}
// 			} 			
 		} else if(transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_DELETE_FAVORITE)) {
 			existingFavorite = checkIfFavoriteExists(subscriberID, transactionDetails);
 			if(existingFavorite == null) {
 				log.error("Delete favorite failed - Favorite not exists");
 	 			result.setNotificationCode(CmFinoFIX.NotificationCode_FavoriteNotFound);
 	 			result.setFavoriteValue(transactionDetails.getFavoriteValue());
 	 			return result;
 			}
 		}
 		//SCTL creation
 		Transaction transaction = null;
		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(transactionDetails.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(StringUtils.isNotBlank(cc.getChannelcode()) ? Long.valueOf(cc.getChannelcode()) : null);
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(transactionDetails.getTransactionName());
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionLog.getId().longValue());
		serviceCharge.setTransactionIdentifier(favoriteMessage.getTransactionIdentifier());
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
		
		if(transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_ADD_FAVORITE)) { 
			try {	//Add the favorite
				log.info("Adding the favorite");
				SubscriberFavorite subscriberFavorite = new SubscriberFavorite();
				subscriberFavorite.setSubscriber(subscriberMDN.getSubscriber());
				subscriberFavorite.setFavoriteCategory(favoriteCategory);
				subscriberFavorite.setFavoritecode(transactionDetails.getFavoriteCode());
				subscriberFavorite.setFavoritelabel(transactionDetails.getFavoriteLabel());
				subscriberFavorite.setFavoritevalue(transactionDetails.getFavoriteValue());
				subscriberFavoriteService.saveSubscriberFavorite(subscriberFavorite);
				log.info("Favorite added for the subscriber");
				result.setNotificationCode(CmFinoFIX.NotificationCode_AddFavoriteSuccess);
			}
			catch (Exception ex) {
				log.error("Exception occured while adding subscriber favorite", ex);
				result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
				if (sctl != null) {
					tcs.failTheTransaction(sctl, MessageText._("Add Subscriber favorite failed"));
				}
	 			return result;
			}
		} else if(transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_EDIT_FAVORITE)) {
			try {	//Edit the favorite
				log.info("Editing the favorite");
				existingFavorite.setFavoritelabel(transactionDetails.getFavoriteLabel());
				subscriberFavoriteService.saveSubscriberFavorite(existingFavorite);
				log.info("Favorite edited for the subscriber");
				result.setNotificationCode(CmFinoFIX.NotificationCode_EditFavoriteSuccess);
			}
			catch (Exception ex) {
				log.error("Exception occured while editing subscriber favorite", ex);
				result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
				if (sctl != null) {
					tcs.failTheTransaction(sctl, MessageText._("Edit Subscriber favorite failed"));
				}
	 			return result;
			}
		} else if(transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_DELETE_FAVORITE)) {
			try {	//Delete the favorite
				log.info("Deleting the favorite");
				subscriberFavoriteService.deleteSubscriberFavorite(existingFavorite);
				log.info("Favorite deleted for the subscriber");
				result.setNotificationCode(CmFinoFIX.NotificationCode_DeleteFavoriteSuccess);
			}
			catch (Exception ex) {
				log.error("Exception occured while deleting subscriber favorite", ex);
				result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
				if (sctl != null) {
					tcs.failTheTransaction(sctl, MessageText._("Delete Subscriber favorite failed"));
				}
	 			return result;
			}
		}
		
		if (sctl != null) {			
			tcs.completeTheTransaction(sctl);
		}		
 		return result;
	}	
	
	/**
	 * Check if the duplicate favorite exists with value/label while adding
	 * 
	 * @param result
	 * @param subscriberID
	 * @param transactionDetails
	 * @return XMLResult
	 */
	private XMLResult validateAddFavorite(XMLResult result, Long subscriberID, TransactionDetails transactionDetails) {		
 		result = checkDuplicateFavoriteWithValue(result, subscriberID, transactionDetails);
 		if(result.getNotificationCode() != null) {
 			return result;
 		}
		return checkDuplicateFavoriteWithLabel(result, subscriberID, transactionDetails);
	}
	
	/**
	 *  Check if the duplicate favorite exits with the given value.
	 * 
	 * @param result
	 * @param subscriberID
	 * @param transactionDetails
	 * @return XMLResult
	 */
	private XMLResult checkDuplicateFavoriteWithValue(XMLResult result, Long subscriberID, TransactionDetails transactionDetails) {		
 		Long favoriteCategoryID = Long.valueOf(transactionDetails.getFavoriteCategoryID()); 		
		//check if duplicate favorite exists with value
 		SubscriberFavoriteQuery subscriberFavoriteQuery = new SubscriberFavoriteQuery();
		subscriberFavoriteQuery.setSubscriberID(subscriberID);
		subscriberFavoriteQuery.setFavoriteCategoryID(favoriteCategoryID);
		subscriberFavoriteQuery.setFavoriteValue(transactionDetails.getFavoriteValue());
		List<SubscriberFavorite> list = subscriberFavoriteService.getSubscriberFavoriteByQuery(subscriberFavoriteQuery);
		if(!list.isEmpty()) {
			log.error("Duplicate favorite exists with the Value " + transactionDetails.getFavoriteValue());
			result.setNotificationCode(CmFinoFIX.NotificationCode_DuplicateFavoriteValue);
			result.setFavoriteValue(transactionDetails.getFavoriteValue());
		}
		return result;
	}
	
	/**
	 *  Check if the duplicate favorite exits  with the given label.
	 * 
	 * @param result
	 * @param subscriberID
	 * @param transactionDetails
	 * @return XMLResult
	 */
	private XMLResult checkDuplicateFavoriteWithLabel(XMLResult result, Long subscriberID, TransactionDetails transactionDetails) {		
 		Long favoriteCategoryID = Long.valueOf(transactionDetails.getFavoriteCategoryID()); 		
		//check if duplicate favorite exists with Label
 		SubscriberFavoriteQuery subscriberFavoriteQuery = new SubscriberFavoriteQuery();
		subscriberFavoriteQuery.setSubscriberID(subscriberID);
		subscriberFavoriteQuery.setFavoriteCategoryID(favoriteCategoryID);
		subscriberFavoriteQuery.setFavoriteLabel(transactionDetails.getFavoriteLabel());
		List<SubscriberFavorite> list = subscriberFavoriteService.getSubscriberFavoriteByQuery(subscriberFavoriteQuery);
		if(!list.isEmpty()) {
			log.error("Duplicate favorite exists with the Label " + transactionDetails.getFavoriteLabel());
			result.setNotificationCode(CmFinoFIX.NotificationCode_DuplicateFavoriteLabel);
			result.setFavoriteLabel(transactionDetails.getFavoriteLabel());
		}
		return result;
	}
	
	
	/**
	 * Check if the favorite exists with given value and returns it.
	 * 
	 * @param subscriberID
	 * @param transactionDetails
	 * @return SubscriberFavorite
	 */
	private SubscriberFavorite checkIfFavoriteExists(Long subscriberID, TransactionDetails transactionDetails) {
		Long favoriteCategoryID = Long.valueOf(transactionDetails.getFavoriteCategoryID()); 	
		SubscriberFavoriteQuery subscriberFavoriteQuery = new SubscriberFavoriteQuery();
		subscriberFavoriteQuery.setSubscriberID(subscriberID);
		subscriberFavoriteQuery.setFavoriteCategoryID(favoriteCategoryID);
		subscriberFavoriteQuery.setFavoriteValue(transactionDetails.getFavoriteValue());
		List<SubscriberFavorite> list = subscriberFavoriteService.getSubscriberFavoriteByQuery(subscriberFavoriteQuery);
		if(!list.isEmpty()) {
			return list.get(0);
		} 
		return null;
	}
}
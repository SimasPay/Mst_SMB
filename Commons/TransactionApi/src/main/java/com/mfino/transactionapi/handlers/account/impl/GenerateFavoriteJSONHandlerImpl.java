package com.mfino.transactionapi.handlers.account.impl;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.SubscriberFavoriteQuery;
import com.mfino.domain.SubscriberFavorite;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberFavoriteService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.account.GenerateFavoriteJSONHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Srikanth
 * 
 */
@Service("GenerateFavoriteJSONHandlerImpl")
public class GenerateFavoriteJSONHandlerImpl extends FIXMessageHandler implements GenerateFavoriteJSONHandler {
	private static Logger	log	= LoggerFactory.getLogger(GenerateFavoriteJSONHandlerImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("SubscriberFavoriteServiceImpl")
	private SubscriberFavoriteService subscriberFavoriteService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;


	public XMLResult handle(TransactionDetails transactionDetails) {		
		log.info("Handling Generate Favorite JSON webapi request");		
		String favCategoryID = transactionDetails.getFavoriteCategoryID();
		String favoriteCode=transactionDetails.getFavoriteCode();
		String[] favCategories = favCategoryID.split(",");
 				
		XMLResult result = new ChangeEmailXMLResult();
		
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
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongpincount()));
			result.setNotificationCode(validationResult);
			return result;
		}
 		addCompanyANDLanguageToResult(subscriberMDN, result);
 		Long subscriberID = subscriberMDN.getSubscriber().getId().longValue();

 		JSONArray totalJsonFavoriteArray = new JSONArray();
 		Long fcId = 0l;
 		for (int i=0; i<favCategories.length; i++) {
 			try {
				fcId = Long.valueOf(favCategories[i]);
			} catch (NumberFormatException e) {
	 			log.error("Invalid Fav category with the ID: " + favCategories[i]);
	 			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing); 			
	 			return result;
			}
 	 		getSubFavByFavoriteCategory(fcId, subscriberID,favoriteCode, totalJsonFavoriteArray);
 		}
 		result.setMessage(totalJsonFavoriteArray.toString());
 		return result;
	}

	private void getSubFavByFavoriteCategory(Long favoriteCategoryID, Long subscriberID,String favoriteCode, JSONArray jsonFavoriteArray) {
		SubscriberFavoriteQuery subscriberFavoriteQuery = new SubscriberFavoriteQuery();
 		subscriberFavoriteQuery.setFavoriteCategoryID(favoriteCategoryID);
 		subscriberFavoriteQuery.setSubscriberID(subscriberID);
 		subscriberFavoriteQuery.setFavoriteCode(favoriteCode);
 		List<SubscriberFavorite> list = subscriberFavoriteService.getSubscriberFavoriteByQuery(subscriberFavoriteQuery);
 		for(SubscriberFavorite favorite: list) {
 			JSONObject jsonFavoriteNode = toJson(favorite, favoriteCategoryID);
 			jsonFavoriteArray.add(jsonFavoriteNode);
 		}
	}
	
	private JSONObject toJson(SubscriberFavorite favorite, Long favoriteCategoryID) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ApiConstants.PARAMETER_FAVORITE_CATEGORY_ID,	favoriteCategoryID);
		jsonObject.put(ApiConstants.PARAMETER_SUBSCRIBER_FAVORITE_ID, favorite.getId());
		jsonObject.put(ApiConstants.PARAMETER_FAVORITE_CODE, favorite.getFavoritecode());
		jsonObject.put(ApiConstants.PARAMETER_FAVORITE_LABEL, favorite.getFavoritelabel());
		jsonObject.put(ApiConstants.PARAMETER_FAVORITE_VALUE, favorite.getFavoritevalue());
		return jsonObject;
	}
}
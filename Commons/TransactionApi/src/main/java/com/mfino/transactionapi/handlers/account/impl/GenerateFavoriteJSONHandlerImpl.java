package com.mfino.transactionapi.handlers.account.impl;

import java.io.PrintWriter;
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
import com.mfino.domain.FavoriteCategory;
import com.mfino.domain.SubscriberFavorite;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.service.FavoriteCategoryService;
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
	@Qualifier("FavoriteCategoryServiceImpl")
	private FavoriteCategoryService favoriteCategoryService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;


	public XMLResult handle(TransactionDetails transactionDetails) {		
		log.info("Handling Generate Favorite JSON webapi request");		
		String favCategoryID = transactionDetails.getFavoriteCategoryID();
 		Long favoriteCategoryID = Long.valueOf(favCategoryID);		
 				
		XMLResult result = new ChangeEmailXMLResult();
		
		// Subscriber MDN validation
		SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
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
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongPINCount());
			result.setNotificationCode(validationResult);
			return result;
		}
 		addCompanyANDLanguageToResult(subscriberMDN, result);
 		Long subscriberID = subscriberMDN.getSubscriber().getID();
 		//Check if the favorite category exists 		
 		FavoriteCategory favoriteCategory = favoriteCategoryService.getByID(favoriteCategoryID);
 		if(favoriteCategory == null) {
 			log.error("Fav category with the ID: " + favoriteCategoryID + " not exists");
 			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing); 			
 			return result;
 		}
		
 		SubscriberFavoriteQuery subscriberFavoriteQuery = new SubscriberFavoriteQuery();
 		subscriberFavoriteQuery.setFavoriteCategoryID(favoriteCategoryID);
 		subscriberFavoriteQuery.setSubscriberID(subscriberID);
 		List<SubscriberFavorite> list = subscriberFavoriteService.getSubscriberFavoriteByQuery(subscriberFavoriteQuery);
 		JSONArray jsonFavoriteArray = new JSONArray();
 		for(SubscriberFavorite favorite: list) {
 			JSONObject jsonFavoriteNode = toJson(favorite);
 			jsonFavoriteArray.add(jsonFavoriteNode);
 		}
 		result.setMessage(jsonFavoriteArray.toString());
 		return result;
	}
	
	private JSONObject toJson(SubscriberFavorite favorite) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ApiConstants.PARAMETER_SUBSCRIBER_FAVORITE_ID, favorite.getID());
		jsonObject.put(ApiConstants.PARAMETER_FAVORITE_CODE, favorite.getFavoriteCode());
		jsonObject.put(ApiConstants.PARAMETER_FAVORITE_LABEL, favorite.getFavoriteLabel());
		jsonObject.put(ApiConstants.PARAMETER_FAVORITE_VALUE, favorite.getFavoriteValue());
		return jsonObject;
	}
}
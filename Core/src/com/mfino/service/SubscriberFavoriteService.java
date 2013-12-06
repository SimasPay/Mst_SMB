/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.SubscriberFavoriteQuery;
import com.mfino.domain.SubscriberFavorite;



/**
 * @author Srikanth
 *
 */
public interface SubscriberFavoriteService {
	
	public void saveSubscriberFavorite(SubscriberFavorite subscriberFavorite);
	
	public void deleteSubscriberFavorite(SubscriberFavorite subscriberFavorite);
	
	public List<SubscriberFavorite> getSubscriberFavoriteByQuery(SubscriberFavoriteQuery subscriberFavoriteQuery);
	
	public int getFavoriteCountUnderCategory(Long subscriberID, Long favoriteCategoryID);

}

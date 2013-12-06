package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberFavoriteDAO;
import com.mfino.dao.query.SubscriberFavoriteQuery;
import com.mfino.domain.SubscriberFavorite;
import com.mfino.service.SubscriberFavoriteService;

/**
 *
 * @author Srikanth
 */
@Service("SubscriberFavoriteServiceImpl")
public class SubscriberFavoriteServiceImpl implements SubscriberFavoriteService{

	public Logger log = LoggerFactory.getLogger(this.getClass());
	public static DAOFactory daoFactory = DAOFactory.getInstance();
	public static SubscriberFavoriteDAO subscriberFavoriteDAO = daoFactory.getSubscriberFavoriteDAO();

	@Override
	public void saveSubscriberFavorite(SubscriberFavorite subscriberFavorite) {
		subscriberFavoriteDAO.save(subscriberFavorite);		
	}

	@Override
	public void deleteSubscriberFavorite(SubscriberFavorite subscriberFavorite) {
		subscriberFavoriteDAO.delete(subscriberFavorite);	
	}

	@Override
	public List<SubscriberFavorite> getSubscriberFavoriteByQuery(
			SubscriberFavoriteQuery subscriberFavoriteQuery) {
		return subscriberFavoriteDAO.get(subscriberFavoriteQuery);
	}

	@Override
	public int getFavoriteCountUnderCategory(Long subscriberID, Long favoriteCategoryID) {		
		return subscriberFavoriteDAO.getFavoriteCountUnderCategory(subscriberID, favoriteCategoryID);
	}
}

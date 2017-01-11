package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SubscriberFavoriteQuery;
import com.mfino.domain.FavoriteCategory;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberFavorite;

/**
 * @author Srikanth
 *
 */
public class SubscriberFavoriteDAO extends BaseDAO<SubscriberFavorite> {
	
	public List<SubscriberFavorite> get(SubscriberFavoriteQuery query) {
		Criteria criteria = createCriteria();
		if (query.getSubscriberID() != null ) {			
			criteria.createAlias(SubscriberFavorite.FieldName_Subscriber, "sub");
			criteria.add(Restrictions.eq("sub." + Subscriber.FieldName_RecordID, query.getSubscriberID()));
		}
		if(query.getFavoriteCategoryID() != null) {
			criteria.createAlias(SubscriberFavorite.FieldName_FavoriteCategory, "fc");
			criteria.add(Restrictions.eq("fc." + FavoriteCategory.FieldName_RecordID, query.getFavoriteCategoryID()));
		}
		if(StringUtils.isNotBlank(query.getFavoriteLabel())) {			
			criteria.add(Restrictions.eq(SubscriberFavorite.FieldName_FavoriteLabel, query.getFavoriteLabel()).ignoreCase());
		}
		if(StringUtils.isNotBlank(query.getFavoriteValue())) {			
			criteria.add(Restrictions.eq(SubscriberFavorite.FieldName_FavoriteValue, query.getFavoriteValue()));
		}
		if(StringUtils.isNotBlank(query.getFavoriteCode())) {			
			criteria.add(Restrictions.eq(SubscriberFavorite.FieldName_FavoriteCode, query.getFavoriteCode()));
		}
		criteria.addOrder(Order.asc(SubscriberFavorite.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<SubscriberFavorite> lst = criteria.list();			
		return lst;
	}
	
	public int getFavoriteCountUnderCategory(Long subscriberID, Long favoriteCategoryID) {
		Criteria criteria = createCriteria();
		criteria.createAlias(SubscriberFavorite.FieldName_Subscriber, "sub");
		criteria.add(Restrictions.eq("sub." + Subscriber.FieldName_RecordID, subscriberID));
		criteria.createAlias(SubscriberFavorite.FieldName_FavoriteCategory, "fc");
		criteria.add(Restrictions.eq("fc." + FavoriteCategory.FieldName_RecordID, favoriteCategoryID));
		criteria.setProjection(Projections.rowCount());	
		return ((Long) criteria.uniqueResult()).intValue();
	}
}
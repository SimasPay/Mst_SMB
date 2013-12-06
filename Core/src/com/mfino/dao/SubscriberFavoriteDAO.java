package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SubscriberFavoriteQuery;
import com.mfino.domain.SubscriberFavorite;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Srikanth
 *
 */
public class SubscriberFavoriteDAO extends BaseDAO<SubscriberFavorite> {
	
	public List<SubscriberFavorite> get(SubscriberFavoriteQuery query) {
		Criteria criteria = createCriteria();
		if (query.getSubscriberID() != null ) {			
			criteria.createAlias(CmFinoFIX.CRSubscriberFavorite.FieldName_Subscriber, "sub");
			criteria.add(Restrictions.eq("sub." + CmFinoFIX.CRSubscriber.FieldName_RecordID, query.getSubscriberID()));
		}
		if(query.getFavoriteCategoryID() != null) {
			criteria.createAlias(CmFinoFIX.CRSubscriberFavorite.FieldName_FavoriteCategory, "fc");
			criteria.add(Restrictions.eq("fc." + CmFinoFIX.CRFavoriteCategory.FieldName_RecordID, query.getFavoriteCategoryID()));
		}
		if(StringUtils.isNotBlank(query.getFavoriteLabel())) {			
			criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriberFavorite.FieldName_FavoriteLabel, query.getFavoriteLabel()).ignoreCase());
		}
		if(StringUtils.isNotBlank(query.getFavoriteValue())) {			
			criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriberFavorite.FieldName_FavoriteValue, query.getFavoriteValue()));
		}
		criteria.addOrder(Order.asc(CmFinoFIX.CRSubscriberFavorite.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<SubscriberFavorite> lst = criteria.list();			
		return lst;
	}
	
	public int getFavoriteCountUnderCategory(Long subscriberID, Long favoriteCategoryID) {
		Criteria criteria = createCriteria();
		criteria.createAlias(CmFinoFIX.CRSubscriberFavorite.FieldName_Subscriber, "sub");
		criteria.add(Restrictions.eq("sub." + CmFinoFIX.CRSubscriber.FieldName_RecordID, subscriberID));
		criteria.createAlias(CmFinoFIX.CRSubscriberFavorite.FieldName_FavoriteCategory, "fc");
		criteria.add(Restrictions.eq("fc." + CmFinoFIX.CRFavoriteCategory.FieldName_RecordID, favoriteCategoryID));
		criteria.setProjection(Projections.rowCount());		
		return (Integer) criteria.uniqueResult();
	}
}

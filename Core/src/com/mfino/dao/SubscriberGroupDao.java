package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.SubscriberGroup;

/**
 * 
 * @author Sasi
 *
 */
public class SubscriberGroupDao extends BaseDAO<SubscriberGroup>{
	public SubscriberGroup getBySubscriberID(Long subscriberID) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(SubscriberGroup.FieldName_Subscriber, subscriberID));
        @SuppressWarnings("unchecked")
		List<SubscriberGroup> lst = criteria.list();
		if(criteria.list()==null||criteria.list().isEmpty())
			return null;
		return lst.get(0);
    }
	
	public List<SubscriberGroup> getAllBySubscriberID(BigDecimal subscriberID) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(SubscriberGroup.FieldName_Subscriber, subscriberID));
        @SuppressWarnings("unchecked")
		List<SubscriberGroup> lst = criteria.list();
		if(criteria.list()==null||criteria.list().isEmpty())
			return null;
		return lst;
    }
}

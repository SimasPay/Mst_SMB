package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.SubscriberGroups;

/**
 * 
 * @author Sasi
 *
 */
public class SubscriberGroupDao extends BaseDAO<SubscriberGroups>{
	public SubscriberGroups getBySubscriberID(Long subscriberID) {
        Criteria criteria = createCriteria();
        criteria.createAlias(SubscriberGroups.FieldName_Subscriber, "sub");
        criteria.add(Restrictions.eq("sub."+SubscriberGroups.FieldName_RecordID, subscriberID));
        @SuppressWarnings("unchecked")
		List<SubscriberGroups> lst = criteria.list();
		if(criteria.list()==null||criteria.list().isEmpty())
			return null;
		return lst.get(0);
    }
	
	public List<SubscriberGroups> getAllBySubscriberID(Long subscriberID) {
        Criteria criteria = createCriteria();
        criteria.createAlias(SubscriberGroups.FieldName_Subscriber, "sub");
        criteria.add(Restrictions.eq("sub."+SubscriberGroups.FieldName_RecordID, subscriberID));
        @SuppressWarnings("unchecked")
		List<SubscriberGroups> lst = criteria.list();
		if(criteria.list()==null||criteria.list().isEmpty())
			return null;
		return lst;
    }
}

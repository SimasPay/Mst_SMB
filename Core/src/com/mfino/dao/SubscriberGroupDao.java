package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;

/**
 * 
 * @author Sasi
 *
 */
public class SubscriberGroupDao extends BaseDAO<SubscriberGroup>{
	public SubscriberGroup getBySubscriberID(Long subscriberID) {
        Criteria criteria = createCriteria();
        criteria.createAlias(SubscriberGroup.FieldName_Subscriber, "sub");
        criteria.add(Restrictions.eq("sub." + Subscriber.FieldName_RecordID, subscriberID));
        @SuppressWarnings("unchecked")
		List<SubscriberGroup> lst = criteria.list();
		if(criteria.list()==null||criteria.list().isEmpty())
			return null;
		return lst.get(0);
    }
}

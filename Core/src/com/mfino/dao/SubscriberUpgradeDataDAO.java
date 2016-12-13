package com.mfino.dao;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.SubscriberUpgradeData;

public class SubscriberUpgradeDataDAO extends BaseDAO<SubscriberUpgradeData> {

	public SubscriberUpgradeData getByMdnId(Long mdnId) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(SubscriberUpgradeData.FieldName_MdnId, mdnId));
		criteria.addOrder(Order.desc(SubscriberUpgradeData.FieldName_RecordID));
		criteria.setMaxResults(1);
		SubscriberUpgradeData result = (SubscriberUpgradeData) criteria.uniqueResult();
		if(result != null)
			Hibernate.initialize(result.getAddress());
		return result;
	}
	

}

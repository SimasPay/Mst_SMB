package com.mfino.dao;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.fix.CmFinoFIX;

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
	public int getCountByMdnId(Long mdnId) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(SubscriberUpgradeData.FieldName_MdnId, mdnId));
		criteria.add(Restrictions.eq(SubscriberUpgradeData.FieldName_SubsActivityStatus,CmFinoFIX.SubscriberActivityStatus_Initialized));
		criteria.setProjection(Projections.rowCount());
        int count = ((Long) criteria.uniqueResult()).intValue();
		
		return count;
	}
	

}

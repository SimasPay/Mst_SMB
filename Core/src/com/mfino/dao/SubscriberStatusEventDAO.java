package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.SubscriberStatusEvent;
import com.mfino.fix.CmFinoFIX;

public class SubscriberStatusEventDAO extends BaseDAO<SubscriberStatusEvent> {

	public List<SubscriberStatusEvent> getSubscriberStatusEvent(boolean includeParnterInSLC, Integer[] statuses) {

		Criteria criteria = createCriteria();
		criteria.add(Restrictions.lt(
				SubscriberStatusEvent.FieldName_PickUpDateTime,
				new java.util.Date()));
		criteria.add(Restrictions.eq(
				SubscriberStatusEvent.FieldName_ProcessingStatus,
				false));

        if (statuses != null && statuses.length > 0) 
            criteria.add(Restrictions.in(SubscriberStatusEvent.FieldName_StatusOnPickup, statuses));

		if(!includeParnterInSLC){
			criteria.add(Restrictions.eq(
					SubscriberStatusEvent.FieldName_SubscriberType,
					CmFinoFIX.SubscriberType_Subscriber));
		}
		@SuppressWarnings("unchecked")
		List<SubscriberStatusEvent> statusEventList = criteria.list();

		return statusEventList;
	}

	public List<SubscriberStatusEvent> getAllBySubscriberId(BigDecimal id) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(SubscriberStatusEvent.FieldName_SubscriberId, id));
		return criteria.list();
	}

}

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.SubscriberStatusEvent;
import com.mfino.fix.CmFinoFIX;

public class SubscriberStatusEventDAO extends BaseDAO<SubscriberStatusEvent> {

	public List<SubscriberStatusEvent> getSubscriberStatusEvent(boolean includeParnterInSLC) {

		Criteria criteria = createCriteria();
		criteria.add(Restrictions.lt(
				CmFinoFIX.CRSubscriberStatusEvent.FieldName_PickUpDateTime,
				new java.util.Date()));
		criteria.add(Restrictions.eq(
				CmFinoFIX.CRSubscriberStatusEvent.FieldName_ProcessingStatus,
				false));
		if(!includeParnterInSLC){
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRSubscriberStatusEvent.FieldName_SubscriberType,
					CmFinoFIX.SubscriberType_Subscriber));
		}
		@SuppressWarnings("unchecked")
		List<SubscriberStatusEvent> statusEventList = criteria.list();

		return statusEventList;
	}

}

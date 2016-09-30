package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SubscribersAdditionalFieldsQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;

/**
 *
 * @author sanjeev
 */
public class SubscribersAdditionalFieldsDAO extends BaseDAO<SubscriberAddiInfo> {
	
	@SuppressWarnings("unchecked")
	public List<SubscriberAddiInfo> get (SubscribersAdditionalFieldsQuery query){
	
		Criteria criteria = createCriteria();

        if (query.getSubscriberID() != null) {
        	criteria.createAlias(SubscriberAddiInfo.FieldName_SubscriberID, "subscriber");
            criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_RecordID, query.getSubscriberID()));
        }
         processBaseQuery(query, criteria);
         List<SubscriberAddiInfo> results = criteria.list();
         return results;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getSubcriberIDsOfKinInfoAvailable() {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.isNotNull(SubscriberAddiInfo.FieldName_KinName));
		criteria.add(Restrictions.isNotNull(SubscriberAddiInfo.FieldName_KinMDN));
		List<SubscriberAddiInfo> results = criteria.list();
		 List<Long> subids = new ArrayList<Long>();
		if(results==null||results.isEmpty()){
			return subids;
		}
		for(SubscriberAddiInfo saf: results){
			subids.add(saf.getSubscriber().getId().longValue());
		}
		return subids;
	}
	
}

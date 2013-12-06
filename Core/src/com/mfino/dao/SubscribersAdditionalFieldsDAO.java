package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SubscribersAdditionalFieldsQuery;
import com.mfino.domain.SubscribersAdditionalFields;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author sanjeev
 */
public class SubscribersAdditionalFieldsDAO extends BaseDAO<SubscribersAdditionalFields> {
	
	@SuppressWarnings("unchecked")
	public List<SubscribersAdditionalFields> get (SubscribersAdditionalFieldsQuery query){
	
		Criteria criteria = createCriteria();

        if (query.getSubscriberID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSubscribersAdditionalFields.FieldName_SubscriberID, query.getSubscriberID()));
        }
         processBaseQuery(query, criteria);
         List<SubscribersAdditionalFields> results = criteria.list();
         return results;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getSubcriberIDsOfKinInfoAvailable() {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.isNotNull(CmFinoFIX.CRSubscribersAdditionalFields.FieldName_KinName));
		criteria.add(Restrictions.isNotNull(CmFinoFIX.CRSubscribersAdditionalFields.FieldName_KinMDN));
		List<SubscribersAdditionalFields> results = criteria.list();
		 List<Long> subids = new ArrayList<Long>();
		if(results==null||results.isEmpty()){
			return subids;
		}
		for(SubscribersAdditionalFields saf: results){
			subids.add(saf.getSubscriber().getID());
		}
		return subids;
	}
	
}

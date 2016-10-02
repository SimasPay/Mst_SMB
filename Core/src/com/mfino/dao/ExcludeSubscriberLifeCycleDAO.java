/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ExcludeSubscriberLifeCycleQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Base;
import com.mfino.domain.ExcludeSubscriberLifeCycle;
import com.mfino.domain.SubscriberMdn;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class ExcludeSubscriberLifeCycleDAO extends BaseDAO<ExcludeSubscriberLifeCycle> {

    public List<ExcludeSubscriberLifeCycle> get(ExcludeSubscriberLifeCycleQuery query){
        Criteria criteria = createCriteria();


        if(query.getMdnId() != null){
        	criteria.createAlias(ExcludeSubscriberLifeCycle.FieldName_MDN, "mdn");
            criteria.add(Restrictions.eq("mdn."+Base.FieldName_RecordID,
                    query.getMdnId()));
        }

        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(ActivitiesLog.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ExcludeSubscriberLifeCycle> results = criteria.list();

        return results;
    }

    public ExcludeSubscriberLifeCycle getBySubscriberMDN(SubscriberMdn subscriberMDN) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(ExcludeSubscriberLifeCycle.FieldName_SubscriberMDNByMDNID, subscriberMDN));
    	List<ExcludeSubscriberLifeCycle> excludeSubscriberMDNList = criteria.list();
    	
    	if((null != excludeSubscriberMDNList) && (excludeSubscriberMDNList.size() > 0)){
    		return excludeSubscriberMDNList.get(0);
    	}
    	
    	return null;
    }
    
}

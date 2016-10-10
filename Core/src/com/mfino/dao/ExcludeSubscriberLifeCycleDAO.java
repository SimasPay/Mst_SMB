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
import com.mfino.domain.ExcludeSubscriberLc;
import com.mfino.domain.SubscriberMdn;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class ExcludeSubscriberLifeCycleDAO extends BaseDAO<ExcludeSubscriberLc> {

    public List<ExcludeSubscriberLc> get(ExcludeSubscriberLifeCycleQuery query){
        Criteria criteria = createCriteria();


        if(query.getMdnId() != null){
        	criteria.createAlias(ExcludeSubscriberLc.FieldName_MDN, "mdn");
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
        List<ExcludeSubscriberLc> results = criteria.list();

        return results;
    }

    public ExcludeSubscriberLc getBySubscriberMDN(SubscriberMdn subscriberMDN) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(ExcludeSubscriberLc.FieldName_SubscriberMDNByMDNID, subscriberMDN));
    	List<ExcludeSubscriberLc> excludeSubscriberMDNList = criteria.list();
    	
    	if((null != excludeSubscriberMDNList) && (excludeSubscriberMDNList.size() > 0)){
    		return excludeSubscriberMDNList.get(0);
    	}
    	
    	return null;
    }
    
}

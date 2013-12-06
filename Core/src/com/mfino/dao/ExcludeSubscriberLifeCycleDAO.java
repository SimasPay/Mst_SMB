/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ActivitiesLogQuery;
import com.mfino.dao.query.ExcludeSubscriberLifeCycleQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Company;
import com.mfino.domain.ExcludeSubscriberLifeCycle;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.MfinoUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class ExcludeSubscriberLifeCycleDAO extends BaseDAO<ExcludeSubscriberLifeCycle> {

    public List<ExcludeSubscriberLifeCycle> get(ExcludeSubscriberLifeCycleQuery query){
        Criteria criteria = createCriteria();


        if(query.getMdnId() != null){
            criteria.add(Restrictions.eq(CmFinoFIX.CRExcludeSubscriberLifeCycle.FieldName_MDNID,
                    query.getMdnId()));
        }

        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(CmFinoFIX.CRActivitiesLog.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ExcludeSubscriberLifeCycle> results = criteria.list();

        return results;
    }

    public ExcludeSubscriberLifeCycle getBySubscriberMDN(SubscriberMDN subscriberMDN) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRExcludeSubscriberLifeCycle.FieldName_SubscriberMDNByMDNID, subscriberMDN));
    	List<ExcludeSubscriberLifeCycle> excludeSubscriberMDNList = criteria.list();
    	
    	if((null != excludeSubscriberMDNList) && (excludeSubscriberMDNList.size() > 0)){
    		return excludeSubscriberMDNList.get(0);
    	}
    	
    	return null;
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ZTEDataPushQuery;
import com.mfino.domain.ZTEDataPush;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class ZTEDataPushDAO extends BaseDAO<ZTEDataPush> {

    public List<ZTEDataPush> get(ZTEDataPushQuery query){
        Criteria criteria = createCriteria();

        if(query.getMsisdn() != null){
            criteria.add(Restrictions.eq(CmFinoFIX.CRZTEDataPush.FieldName_Msisdn,query.getMsisdn()));
        }

        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(CmFinoFIX.CRZTEDataPush.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ZTEDataPush> results = criteria.list();

        return results;
    }

    public ZTEDataPush getByMsisdn(String msisdn) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRZTEDataPush.FieldName_Msisdn, msisdn));
    	List<ZTEDataPush> zteDataPushList = criteria.list();
    	if((null != zteDataPushList) && (zteDataPushList.size() > 0)){
    		return zteDataPushList.get(0);
    	}
    	return null;
    }
    
}

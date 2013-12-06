/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SCTLSettlementMapQuery;
import com.mfino.domain.SCTLSettlementMap;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Hemanth
 *
 */
public class SCTLSettlementMapDAO extends BaseDAO<SCTLSettlementMap> {

    public List<SCTLSettlementMap> get(SCTLSettlementMapQuery query) {
        Criteria criteria = createCriteria();

        if (query.getSctlID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSCTLSettlementMap.FieldName_SctlId, query.getSctlID()));
        }
        if (query.getPartnerID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSCTLSettlementMap.FieldName_PartnerID, query.getPartnerID()));
        }
        if (query.getSettlementStatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSCTLSettlementMap.FieldName_SettlementStatus, query.getSettlementStatus()));
        }
        if (query.getServiceID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSCTLSettlementMap.FieldName_ServiceID, query.getServiceID()));
        }
		if (query.getStlID() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRSCTLSettlementMap.FieldName_StlID, query.getStlID()));
		}




        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
       // criteria.addOrder(Order.desc(CmFinoFIX.CRScheduleTemplate.FieldName_RecordID));
        //applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<SCTLSettlementMap> results = criteria.list();
        return results;
    }


}

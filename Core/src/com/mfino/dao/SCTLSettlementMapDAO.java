/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SCTLSettlementMapQuery;
import com.mfino.domain.Base;
import com.mfino.domain.Partner;
import com.mfino.domain.SctlSettlementMap;

/**
 * 
 * @author Hemanth
 *
 */
public class SCTLSettlementMapDAO extends BaseDAO<SctlSettlementMap> {

    public List<SctlSettlementMap> get(SCTLSettlementMapQuery query) {
        Criteria criteria = createCriteria();

        if (query.getSctlID() != null) {
        	criteria.createAlias(SctlSettlementMap.FieldName_SctlId, "sctl");
            criteria.add(Restrictions.eq("sctl."+Base.FieldName_RecordID, query.getSctlID()));
        }
        if (query.getPartnerID() != null) {
        	criteria.createAlias(SctlSettlementMap.FieldName_PartnerID, "partner");
            criteria.add(Restrictions.eq("partner."+Partner.FieldName_RecordID, query.getPartnerID()));
        }
        if (query.getSettlementStatus() != null) {
            criteria.add(Restrictions.eq(SctlSettlementMap.FieldName_SettlementStatus, query.getSettlementStatus()));
        }
        if (query.getServiceID() != null) {
        	criteria.createAlias(SctlSettlementMap.FieldName_ServiceID, "service");
            criteria.add(Restrictions.eq("partner."+Base.FieldName_RecordID, query.getServiceID()));
        }
		if (query.getStlID() != null) {
        	criteria.createAlias(SctlSettlementMap.FieldName_StlID, "stl");
			criteria.add(Restrictions.eq("stl."+Base.FieldName_RecordID, query.getStlID()));
		}




        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
       // criteria.addOrder(Order.desc(ScheduleTemplate.FieldName_RecordID));
        //applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<SctlSettlementMap> results = criteria.list();
        return results;
    }


}

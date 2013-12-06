/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SettlementTransactionSCTLMapQuery;
import com.mfino.domain.SettlementTransactionSCTLMap;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Hemanth
 *
 */
public class SettlementTransactionSCTLMapDAO extends BaseDAO<SettlementTransactionSCTLMap> {

    public List<SettlementTransactionSCTLMap> get(SettlementTransactionSCTLMapQuery query) {
        Criteria criteria = createCriteria();

        if (query.getStlID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSettlementTransactionSCTLMap.FieldName_StlID, query.getStlID()));
        }
        if (query.getSCTLID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSettlementTransactionSCTLMap.FieldName_SctlId, query.getSCTLID()));
        }
        if(query.getSettlementStatus() != null) {
        	criteria.add(Restrictions.eq(CmFinoFIX.CRSettlementTransactionSCTLMap.FieldName_SettlementStatus, query.getSettlementStatus()));
        }
        
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
       // criteria.addOrder(Order.desc(CmFinoFIX.CRSettlementTransactionSCTLMap.FieldName_RecordID));
      //  applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<SettlementTransactionSCTLMap> results = criteria.list();
        return results;
    }

    public SettlementTransactionSCTLMap getByStlID(Long stlID) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(SettlementTransactionSCTLMap.FieldName_StlID, stlID));
        return (SettlementTransactionSCTLMap) criteria.uniqueResult();
    }
    public SettlementTransactionSCTLMap getBySCTLID(Long sctlID) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(SettlementTransactionSCTLMap.FieldName_SctlId, sctlID));
        return (SettlementTransactionSCTLMap) criteria.uniqueResult();
    }

}

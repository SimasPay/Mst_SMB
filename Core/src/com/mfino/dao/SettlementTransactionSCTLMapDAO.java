/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SettlementTransactionSCTLMapQuery;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SettlementTxnLog;
import com.mfino.domain.SettlementTxnSctlMap;

/**
 * 
 * @author Hemanth
 *
 */
public class SettlementTransactionSCTLMapDAO extends BaseDAO<SettlementTxnSctlMap> {

    public List<SettlementTxnSctlMap> get(SettlementTransactionSCTLMapQuery query) {
        Criteria criteria = createCriteria();

        if (query.getStlID() != null) {
        	criteria.createAlias(SettlementTxnSctlMap.FieldName_StlID, "stl");
            criteria.add(Restrictions.eq("stl."+SettlementTxnLog.FieldName_RecordID, query.getStlID()));
        }
        if (query.getSCTLID() != null) {
        	criteria.createAlias(SettlementTxnSctlMap.FieldName_SctlId, "sctl");
            criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_RecordID, query.getSCTLID()));
        }
        if(query.getSettlementStatus() != null) {
        	criteria.add(Restrictions.eq(SettlementTxnSctlMap.FieldName_SettlementStatus, query.getSettlementStatus()));
        }
        
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
       // criteria.addOrder(Order.desc(SettlementTxnSctlMap.FieldName_RecordID));
      //  applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<SettlementTxnSctlMap> results = criteria.list();
        return results;
    }

    public SettlementTxnSctlMap getByStlID(Long stlID) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(SettlementTxnSctlMap.FieldName_StlID, stlID));
        return (SettlementTxnSctlMap) criteria.uniqueResult();
    }
    public SettlementTxnSctlMap getBySCTLID(Long sctlID) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(SettlementTxnSctlMap.FieldName_SctlId, sctlID));
        return (SettlementTxnSctlMap) criteria.uniqueResult();
    }

}

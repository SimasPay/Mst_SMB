package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ClosedAccountSettlementMDNQuery;
import com.mfino.domain.ClosedAccountSettlementMDN;
import com.mfino.domain.SubscriberMdn;
/**
 * @author Satya
 *
 */
public class ClosedAccountSettlementMDNDAO extends BaseDAO<ClosedAccountSettlementMDN>{
	public List<ClosedAccountSettlementMDN> get(ClosedAccountSettlementMDNQuery query){
		Criteria criteria = createCriteria();
		
		if(query.getMdnId() != null){
			criteria.createAlias(ClosedAccountSettlementMDN.FieldName_SubscriberMDNByMDNID, "SMDN");
			criteria.add(Restrictions.eq("SMDN." + SubscriberMdn.FieldName_RecordID, query.getMdnId()));
		}
		
		if(query.getSettlementMDN() != null){
			criteria.add(Restrictions.eq(ClosedAccountSettlementMDN.FieldName_SettlementMDN, query.getSettlementMDN()));
		}
		
		if(query.getSettlementAccountNumber() != null){
			criteria.add(Restrictions.eq(ClosedAccountSettlementMDN.FieldName_SettlementAccountNumber, query.getSettlementAccountNumber()));
		}
		
		processBaseQuery(query, criteria);
		
		// Paging
        processPaging(query, criteria);
        
        if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(ClosedAccountSettlementMDN.FieldName_RecordID));
          }
          
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ClosedAccountSettlementMDN> results = criteria.list();

        return results;
	}
}


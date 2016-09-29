package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ClosedAccountSettlementMDNQuery;
import com.mfino.domain.CloseAcctSetlMdn;
import com.mfino.domain.SubscriberMdn;
/**
 * @author Satya
 *
 */
public class ClosedAccountSettlementMDNDAO extends BaseDAO<CloseAcctSetlMdn>{
	public List<CloseAcctSetlMdn> get(ClosedAccountSettlementMDNQuery query){
		Criteria criteria = createCriteria();
		
		if(query.getMdnId() != null){
			criteria.createAlias(CloseAcctSetlMdn.FieldName_SubscriberMDNByMDNID, "SMDN");
			criteria.add(Restrictions.eq("SMDN." + SubscriberMdn.FieldName_RecordID, query.getMdnId()));
		}
		
		if(query.getSettlementMDN() != null){
			criteria.add(Restrictions.eq(CloseAcctSetlMdn.FieldName_SettlementMDN, query.getSettlementMDN()));
		}
		
		if(query.getSettlementAccountNumber() != null){
			criteria.add(Restrictions.eq(CloseAcctSetlMdn.FieldName_SettlementAccountNumber, query.getSettlementAccountNumber()));
		}
		
		processBaseQuery(query, criteria);
		
		// Paging
        processPaging(query, criteria);
        
        if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(CloseAcctSetlMdn.FieldName_RecordID));
          }
          
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<CloseAcctSetlMdn> results = criteria.list();

        return results;
	}
}


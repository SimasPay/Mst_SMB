package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.AirtimePurchaseQuery;
import com.mfino.domain.AirtimePurchase;
import com.mfino.domain.BillPayments;

/**
 * @author Sasi
 */
public class AirtimePurchaseDAO extends BaseDAO<AirtimePurchase> {
	
	 public List<AirtimePurchase> get(AirtimePurchaseQuery query) {
		 Criteria criteria = createCriteria();
		 if(query.getSctlID()!=null){
			 criteria.add(Restrictions.eq(BillPayments.FieldName_SctlId,query.getSctlID()));
		 }
		         
		 processBaseQuery(query, criteria);
		 processPaging(query, criteria);
       @SuppressWarnings("unchecked")
       List<AirtimePurchase> results = criteria.list();

       return results;
   }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BillPaymentTransactionQuery;
import com.mfino.domain.BillPaymentTxn;

/**
 *
 * @author Maruthi
 */
public class BillPaymentTransactionDAO extends BaseDAO<BillPaymentTxn> {
	
	 public List<BillPaymentTxn> get(BillPaymentTransactionQuery query) {
		 Criteria criteria = createCriteria();
		 if(query.getTransactionID()!=null){
			 criteria.add(Restrictions.eq(BillPaymentTxn.FieldName_TransactionID, query.getTransactionID()));
		 }
		 if(query.getBillerName()!=null){
			 criteria.add(Restrictions.eq(BillPaymentTxn.FieldName_BillerName, query.getBillerName()).ignoreCase());
		 }
		         
		 processBaseQuery(query, criteria);
		 processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BillPaymentTxn> results = criteria.list();

        return results;
    }
}

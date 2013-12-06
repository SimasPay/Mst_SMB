/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BillPaymentTransactionQuery;
import com.mfino.domain.BillPaymentTransaction;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Maruthi
 */
public class BillPaymentTransactionDAO extends BaseDAO<BillPaymentTransaction> {
	
	 public List<BillPaymentTransaction> get(BillPaymentTransactionQuery query) {
		 Criteria criteria = createCriteria();
		 if(query.getTransactionID()!=null){
			 criteria.add(Restrictions.eq(CmFinoFIX.CRBillPaymentTransaction.FieldName_TransactionID,query.getTransactionID()));
		 }
		 if(query.getBillerName()!=null){
			 criteria.add(Restrictions.eq(CmFinoFIX.CRBillPaymentTransaction.FieldName_BillerName, query.getBillerName()).ignoreCase());
		 }
		         
		 processBaseQuery(query, criteria);
		 processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BillPaymentTransaction> results = criteria.list();

        return results;
    }
}

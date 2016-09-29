package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.CreditCardTransactionQuery;
import com.mfino.domain.Company;
import com.mfino.domain.CreditCardTransaction;

public class CreditCardTransactionDAO  extends BaseDAO<CreditCardTransaction> {

@SuppressWarnings("unchecked")
public List<CreditCardTransaction> get(CreditCardTransactionQuery query) {

        Criteria criteria = createCriteria();
        if (query.getId() != null) {
            criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_RecordID, query.getId()));
        }
        if (query.getCcFailureReason() != null) {
            criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_CCFailureReason, query.getCcFailureReason()));
        }
        if (query.getDestMdn() != null) {
            criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_MDN, query.getDestMdn()).ignoreCase());
        }
        if (query.getStartDate() != null) {
            criteria.add(Restrictions.gt(CreditCardTransaction.FieldName_CreateTime, query.getStartDate()));
        }
        if (query.getEndDate() != null) {
            criteria.add(Restrictions.lt(CreditCardTransaction.FieldName_CreateTime, query.getEndDate()));
        }
        if(query.getTransactionId() != null) {
            criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_TransactionID, query.getTransactionId()));
        }
        if(query.getAuthId() != null) {
            criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_AuthID, query.getAuthId()).ignoreCase());
        }
        if(query.getBankReferenceNumber() != null) {
            criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_BankReference, query.getBankReferenceNumber()).ignoreCase());
        }
        if(query.getCompany()!=null){
        	criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_Company, query.getCompany()));
        }
        if(query.getTransStatus()!=null){
        	criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_TransStatus, query.getTransStatus()).ignoreCase());
        }
        
        if(query.getOperation()!=null)
        	criteria.add(Restrictions.eq(CreditCardTransaction.FieldName_Operation, query.getOperation()).ignoreCase());
        processBaseQuery(query, criteria);
        
        List<CreditCardTransaction> results;
        if(query.getNoOrder()!=null && query.getNoOrder()){
        	 results = criteria.list();
        }else{        
        // Paging
        processPaging(query, criteria);

        //applying Order
        criteria.addOrder(Order.desc(Company.FieldName_RecordID));
        applyOrder(query, criteria);
        results = criteria.list();
        }
        return results;
    }
}

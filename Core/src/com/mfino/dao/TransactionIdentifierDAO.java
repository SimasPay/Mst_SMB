package com.mfino.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import com.mfino.domain.TransactionIdentifier;


public class TransactionIdentifierDAO extends BaseDAO<TransactionIdentifier>{
	
    public TransactionIdentifier getByTransactionIdentifier(String transactionIdentifier) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(TransactionIdentifier.FieldName_TransactionIdentifier, transactionIdentifier).ignoreCase());
        return (TransactionIdentifier) criteria.uniqueResult();
    }

}

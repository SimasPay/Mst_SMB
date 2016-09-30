package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.RuleKeyQuery;
import com.mfino.domain.RuleKey;
import com.mfino.domain.Service;
import com.mfino.domain.TransactionType;

/**
 * @author Srikanth
 *
 */
public class RuleKeyDAO extends BaseDAO<RuleKey> {
	
	public List<RuleKey> get(RuleKeyQuery query) {
		Criteria criteria = createCriteria();
		if (query.getServiceID() != null) {			
			criteria.createAlias(RuleKey.FieldName_Service, "service");
			criteria.add(Restrictions.eq("service." + Service.FieldName_RecordID, query.getServiceID()));
		}
		if(query.getTransactionTypeID() != null) {
			criteria.createAlias(RuleKey.FieldName_TransactionType, "txn");
			criteria.add(Restrictions.eq("txn." + TransactionType.FieldName_RecordID, query.getTransactionTypeID()));
		}
		if (query.getTxnRuleKey() != null) {
			criteria.add(Restrictions.eq(RuleKey.FieldName_TxnRuleKey, query.getTxnRuleKey()));
		}
		if (query.getTxnRuleKeyType() != null) {
			criteria.add(Restrictions.eq(RuleKey.FieldName_TxnRuleKeyType, query.getTxnRuleKeyType()));
		}
		if (query.getTxnRuleKeyPriority() != null) {
			criteria.add(Restrictions.eq(RuleKey.FieldName_TxnRuleKeyPriority, query.getTxnRuleKeyPriority()));
		}
		if (query.getTxnRuleKeyComparision() != null) {
			criteria.add(Restrictions.eq(RuleKey.FieldName_TxnRuleKeyComparision, query.getTxnRuleKeyComparision()));
		}
		if(query.isSortByPriority() == true){
			criteria.addOrder(Order.desc(RuleKey.FieldName_TxnRuleKeyPriority));
		}
		else{
			criteria.addOrder(Order.asc(RuleKey.FieldName_RecordID));
		}
		
		processPaging(query, criteria);
		processBaseQuery(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<RuleKey> list = criteria.list();			
		return list;
	}
}

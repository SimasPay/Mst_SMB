package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.RuleKeyQuery;
import com.mfino.domain.RuleKey;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Srikanth
 *
 */
public class RuleKeyDAO extends BaseDAO<RuleKey> {
	
	public List<RuleKey> get(RuleKeyQuery query) {
		Criteria criteria = createCriteria();
		if (query.getServiceID() != null) {			
			criteria.createAlias(CmFinoFIX.CRRuleKey.FieldName_Service, "service");
			criteria.add(Restrictions.eq("service." + CmFinoFIX.CRService.FieldName_RecordID, query.getServiceID()));
		}
		if(query.getTransactionTypeID() != null) {
			criteria.createAlias(CmFinoFIX.CRRuleKey.FieldName_TransactionType, "txn");
			criteria.add(Restrictions.eq("txn." + CmFinoFIX.CRTransactionType.FieldName_RecordID, query.getTransactionTypeID()));
		}
		if (query.getTxnRuleKey() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRRuleKey.FieldName_TxnRuleKey, query.getTxnRuleKey()));
		}
		if (query.getTxnRuleKeyType() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRRuleKey.FieldName_TxnRuleKeyType, query.getTxnRuleKeyType()));
		}
		if (query.getTxnRuleKeyPriority() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRRuleKey.FieldName_TxnRuleKeyPriority, query.getTxnRuleKeyPriority()));
		}
		if (query.getTxnRuleKeyComparision() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRRuleKey.FieldName_TxnRuleKeyComparision, query.getTxnRuleKeyComparision()));
		}
		if(query.isSortByPriority() == true){
			criteria.addOrder(Order.desc(CmFinoFIX.CRRuleKey.FieldName_TxnRuleKeyPriority));
		}
		else{
			criteria.addOrder(Order.asc(CmFinoFIX.CRRuleKey.FieldName_RecordID));
		}
		
		processPaging(query, criteria);
		processBaseQuery(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<RuleKey> list = criteria.list();			
		return list;
	}
}

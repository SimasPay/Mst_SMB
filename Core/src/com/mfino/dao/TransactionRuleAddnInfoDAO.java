package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionRuleAddnInfoQuery;
import com.mfino.domain.TransactionRule;
import com.mfino.domain.TxnRuleAddnInfo;

/**
 * @author Srikanth
 *
 */
public class TransactionRuleAddnInfoDAO extends BaseDAO<TxnRuleAddnInfo> {
	
	public List<TxnRuleAddnInfo> get(TransactionRuleAddnInfoQuery query) {
		Criteria criteria = createCriteria();
		if (query.getTransactionRuleID() != null) {			
			criteria.createAlias(TxnRuleAddnInfo.FieldName_TransactionRule, "txnRule");
			criteria.add(Restrictions.eq("txnRule." + TransactionRule.FieldName_RecordID, query.getTransactionRuleID()));
		}		
		if (query.getTxnRuleKey() != null) {
			criteria.add(Restrictions.eq(TxnRuleAddnInfo.FieldName_TxnRuleKey, query.getTxnRuleKey()));
		}
		if (query.getTxnRuleValue() != null) {
			criteria.add(Restrictions.eq(TxnRuleAddnInfo.FieldName_TxnRuleValue, query.getTxnRuleValue()));
		}
		if (query.getTxnRuleComparator() != null) {
			criteria.add(Restrictions.eq(TxnRuleAddnInfo.FieldName_TxnRuleComparator, query.getTxnRuleComparator()));
		}
		criteria.addOrder(Order.asc(TxnRuleAddnInfo.FieldName_RecordID));
		processPaging(query, criteria);
		processBaseQuery(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<TxnRuleAddnInfo> list = criteria.list();			
		return list;
	}
}

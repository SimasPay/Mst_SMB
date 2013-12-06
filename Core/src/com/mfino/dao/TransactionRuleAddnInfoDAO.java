package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionRuleAddnInfoQuery;
import com.mfino.domain.TransactionRuleAddnInfo;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Srikanth
 *
 */
public class TransactionRuleAddnInfoDAO extends BaseDAO<TransactionRuleAddnInfo> {
	
	public List<TransactionRuleAddnInfo> get(TransactionRuleAddnInfoQuery query) {
		Criteria criteria = createCriteria();
		if (query.getTransactionRuleID() != null) {			
			criteria.createAlias(CmFinoFIX.CRTransactionRuleAddnInfo.FieldName_TransactionRule, "txnRule");
			criteria.add(Restrictions.eq("txnRule." + CmFinoFIX.CRTransactionRule.FieldName_RecordID, query.getTransactionRuleID()));
		}		
		if (query.getTxnRuleKey() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionRuleAddnInfo.FieldName_TxnRuleKey, query.getTxnRuleKey()));
		}
		if (query.getTxnRuleValue() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionRuleAddnInfo.FieldName_TxnRuleValue, query.getTxnRuleValue()));
		}
		if (query.getTxnRuleComparator() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionRuleAddnInfo.FieldName_TxnRuleComparator, query.getTxnRuleComparator()));
		}
		criteria.addOrder(Order.asc(CmFinoFIX.CRTransactionRuleAddnInfo.FieldName_RecordID));
		processPaging(query, criteria);
		processBaseQuery(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<TransactionRuleAddnInfo> list = criteria.list();			
		return list;
	}
}

/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionChargeQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.ChargeType;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionRule;

/**
 * @author Bala Sunku
 *
 */
public class TransactionChargeDAO extends BaseDAO<TransactionCharge> {
	
	public List<TransactionCharge> get(TransactionChargeQuery query) {
		Criteria criteria = createCriteria();

		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(TransactionCharge.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(TransactionCharge.FieldName_CreateTime, query.getEndDate()));
		}
		if (query.getTransactionRuleId() != null) {
			criteria.createAlias(TransactionCharge.FieldName_TransactionRule, "tr");
			criteria.add(Restrictions.eq("tr."+TransactionRule.FieldName_RecordID, query.getTransactionRuleId()));
		}
		if (query.getChargeTypeId() != null ) {
			criteria.createAlias(TransactionCharge.FieldName_ChargeType, "ct");
			criteria.add(Restrictions.eq("ct." + ChargeType.FieldName_RecordID, query.getChargeTypeId()));
		}
		if (query.getChargeDefinitionId() != null) {
			criteria.createAlias(TransactionCharge.FieldName_ChargeDefinition, "cd");
			criteria.add(Restrictions.eq("cd."+ChargeDefinition.FieldName_RecordID, query.getChargeDefinitionId()));
		}

		criteria.addOrder(Order.asc(TransactionCharge.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
			List<TransactionCharge> lst = criteria.list();
			
		return lst;
	}
	
	public TransactionCharge getTransactionCharge(Long transactionRuleId, Long chargeTypeId) {
		TransactionCharge tc = null;
		TransactionChargeQuery query = new TransactionChargeQuery();
		query.setTransactionRuleId(transactionRuleId);
		query.setChargeTypeId(chargeTypeId);
		List<TransactionCharge> lst = get(query);
		if (CollectionUtils.isNotEmpty(lst)) {
			tc = lst.get(0);
		}
		return tc;
	}
	
    @Override
    public void save(TransactionCharge tc) {
        if (tc.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            tc.setMfinoServiceProvider(msp);
        }
        super.save(tc);
    }

}

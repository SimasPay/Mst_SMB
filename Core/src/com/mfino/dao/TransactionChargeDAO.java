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
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class TransactionChargeDAO extends BaseDAO<TransactionCharge> {
	
	public List<TransactionCharge> get(TransactionChargeQuery query) {
		Criteria criteria = createCriteria();

		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(CmFinoFIX.CRTransactionCharge.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(CmFinoFIX.CRTransactionCharge.FieldName_CreateTime, query.getEndDate()));
		}
		if (query.getTransactionRuleId() != null) {
			criteria.createAlias(CmFinoFIX.CRTransactionCharge.FieldName_TransactionRule, "tr");
			criteria.add(Restrictions.eq("tr."+CmFinoFIX.CRTransactionRule.FieldName_RecordID, query.getTransactionRuleId()));
		}
		if (query.getChargeTypeId() != null ) {
			criteria.createAlias(CmFinoFIX.CRTransactionCharge.FieldName_ChargeType, "ct");
			criteria.add(Restrictions.eq("ct." + CmFinoFIX.CRChargeType.FieldName_RecordID, query.getChargeTypeId()));
		}
		if (query.getChargeDefinitionId() != null) {
			criteria.createAlias(CmFinoFIX.CRTransactionCharge.FieldName_ChargeDefinition, "cd");
			criteria.add(Restrictions.eq("cd."+CmFinoFIX.CRChargeDefinition.FieldName_RecordID, query.getChargeDefinitionId()));
		}

		criteria.addOrder(Order.asc(CmFinoFIX.CRTransactionCharge.FieldName_RecordID));
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
        if (tc.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            tc.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(tc);
    }

}

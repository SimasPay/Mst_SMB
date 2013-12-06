package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.AdjustmentsQuery;
import com.mfino.domain.Adjustments;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Srikanth
 *
 */
public class AdjustmentsDAO extends BaseDAO<Adjustments> {
	
	public List<Adjustments> get(AdjustmentsQuery query) {
		Criteria criteria = createCriteria();
		if (query.getSctlID() != null ) {			
			criteria.createAlias(CmFinoFIX.CRAdjustments.FieldName_ServiceChargeTransactionLogBySctlId, "sctl");
			criteria.add(Restrictions.eq("sctl." + CmFinoFIX.CRServiceChargeTransactionLog.FieldName_RecordID, query.getSctlID()));
		}
		if(query.getAdjustmentStatus() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRAdjustments.FieldName_AdjustmentStatus, query.getAdjustmentStatus()));
		}
		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(CmFinoFIX.CRAdjustments.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(CmFinoFIX.CRAdjustments.FieldName_CreateTime, query.getEndDate()));
		}
		criteria.addOrder(Order.asc(CmFinoFIX.CRAdjustments.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<Adjustments> lst = criteria.list();			
		return lst;
	}
}

package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.AgentCommissionFee;
import com.mfino.fix.CmFinoFIX;

public class AgentCommissionFeeDAO extends BaseDAO<AgentCommissionFee> {
	
	@SuppressWarnings("unchecked")
	public AgentCommissionFee getAgentCommissionFee(Long partnerId, String month, int year) {
		AgentCommissionFee result = null;
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRAgentCommissionFee.FieldName_PartnerID, partnerId));
		criteria.add(Restrictions.eq(CmFinoFIX.CRAgentCommissionFee.FieldName_Month, month));
		criteria.add(Restrictions.eq(CmFinoFIX.CRAgentCommissionFee.FieldName_Year, year));
		List<AgentCommissionFee> lst = criteria.list();
		if (CollectionUtils.isNotEmpty(lst)) {
			log.info("got the agent commission fee details for partner: " + partnerId + " for month: " + month + " and year: " + year
					+ " of size:" + lst.size());
			result = lst.get(0);
		}
		return result;
	}
}

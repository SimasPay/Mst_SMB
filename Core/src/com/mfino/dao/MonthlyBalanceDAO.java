package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.MonthlyBalance;
import com.mfino.domain.Pocket;

public class MonthlyBalanceDAO extends BaseDAO<MonthlyBalance> {
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getCommissionFeeDetails(String month, int year) {

		String sql= "select s.RegisteringPartnerID, sum(mb.AgentCommissionCalculated) " +
		 		"from MonthlyBalance as mb inner join mb.Pocket as p inner join p.SubscriberMDNByMDNID as sm inner join sm.Subscriber as s " + 
				"where mb.Month = :month and mb.Year = :year " +
				"group by s.RegisteringPartnerID"; 
		 
		Query queryObj = getSession().createQuery(sql);
		queryObj.setParameter("month", month);
		queryObj.setParameter("year", year);
		List<Object[]> results = queryObj.list();
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public MonthlyBalance getByDetails(Pocket pocket, String month, int year) {
		MonthlyBalance result = null;
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(MonthlyBalance.FieldName_Pocket, pocket));
		criteria.add(Restrictions.eq(MonthlyBalance.FieldName_Month, month));
		criteria.add(Restrictions.eq(MonthlyBalance.FieldName_Year, year));
		
		List<MonthlyBalance> lst = criteria.list();
		if (CollectionUtils.isNotEmpty(lst)) {
			result = lst.get(0);
		}
		return result;
	}
}

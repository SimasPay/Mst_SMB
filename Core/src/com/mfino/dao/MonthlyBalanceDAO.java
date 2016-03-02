package com.mfino.dao;

import java.util.List;

import org.hibernate.Query;

import com.mfino.domain.MonthlyBalance;

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
}

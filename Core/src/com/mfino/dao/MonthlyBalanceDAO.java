package com.mfino.dao;

import java.util.List;

import org.hibernate.Query;

import com.mfino.domain.MonthlyBalance;

public class MonthlyBalanceDAO extends BaseDAO<MonthlyBalance> {
	
	 
	 public List<Object[]> getCommissionFeeDetails(String month, int year) {

		 String sql= "select s.RegisteringPartnerID, sum(mb.AgentCommissionCalculated) " +
		 		"from MonthlyBalance mb , Pocket p, SubscriberMDN sm, Subscriber s " + 
				"where mb.PocketID = p.ID " +
		 		"and p.MDNID = sm.ID " +
				"and sm.SubscriberID = s.ID " +
		 		"and mb.Month = :month and mb.Year = :year " +
				"group by s.RegisteringPartnerID"; 
		 
		 Query queryObj = getSession().createQuery(sql);
		 queryObj.setParameter("month", month);
		 queryObj.setParameter("year", year);
		 List<Object[]> results = queryObj.list();
		 return results;
	}
}

/**
 * 
 */
package com.mfino.dao;

import java.util.List;
import java.util.TimeZone;

import org.hibernate.Query;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTxnLog;

/**
 * @author Srinivaas
 *
 */
public class TransactionMonitoringDAO extends BaseDAO<ServiceChargeTxnLog> {
	
	public List<Object> getRCFailedTransactions(ServiceChargeTransactionsLogQuery sctlQuery) 
	{
		Query query;
		String sqlQuery = null;
		sqlQuery = sctlQuery.getCustomQuery();
		sqlQuery = sqlQuery.replace("$(timeZone)", TimeZone.getDefault().getID());
		
		query = getSQLQuery(sqlQuery);
    	query.setInteger("status", sctlQuery.getStatus());  
    	query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
    	query.setInteger("maxLimit", sctlQuery.getLimit());
    	
    	@SuppressWarnings("unchecked")
		List<Object> sqlList1 =(List<Object>) query.list();
    	return sqlList1;
	}
	
	public List<Object> getSummaryTransactions(ServiceChargeTransactionsLogQuery sctlQuery) {
		
		Query query;
		query = getSQLQuery(sctlQuery.getCustomQuery());
    	query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
    	
    	@SuppressWarnings("unchecked")
		List<Object> summarList =(List<Object>) query.list();
    	
    	return summarList;
	}
	
	public List<Object> getPerTransactions(ServiceChargeTransactionsLogQuery sctlQuery) {
		
		Query query;
		query = getSQLQuery(sctlQuery.getCustomQuery());
    	query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
    	
    	@SuppressWarnings("unchecked")
		List<Object> summarList =(List<Object>) query.list();
    	
    	return summarList;
	}
	
	public List<Object> getRCCodeByTrnsIdList(ServiceChargeTransactionsLogQuery sctlQuery) 
	{
		Query query;	
		query = getSQLQuery(sctlQuery.getCustomQuery());
		query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
		
    	@SuppressWarnings("unchecked")
		List<Object> sqlList1 =(List<Object>) query.list();
    	return sqlList1;
    	
	}
}

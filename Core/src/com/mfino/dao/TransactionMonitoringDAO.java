/**
 * 
 */
package com.mfino.dao;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionMonitoringLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Srinivaas
 *
 */
public class TransactionMonitoringDAO extends BaseDAO<TransactionMonitoringLog> {
	
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

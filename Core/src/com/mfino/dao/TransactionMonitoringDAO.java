/**
 * 
 */
package com.mfino.dao;

import java.util.Date;
import java.util.List;

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
		StringBuffer sqlQuery = new StringBuffer();
		/*sqlQuery.append(" SELECT ID,SOURCEMDN,FAILUREREASON,CREATETIME,CHANNELNAME, TRANSACTIONNAME,TRANSACTIONAMOUNT,RCCODE FROM");
		sqlQuery.append(" (SELECT SCTL.ID ID,SCTL.SOURCEMDN SOURCEMDN,SCTL.FAILUREREASON FAILUREREASON,");
		sqlQuery.append(" SCTL.CREATETIME CREATETIME,CC.CHANNELNAME CHANNELNAME,"); 
		sqlQuery.append(" TT.TRANSACTIONNAME TRANSACTIONNAME,SCTL.TRANSACTIONAMOUNT TRANSACTIONAMOUNT,");
		sqlQuery.append(" COALESCE(PCT.OPERATORRESPONSECODE||'',PCT.BANKREJECTREASON,CT.OPERATORRESPONSECODE||'',CT.BANKREJECTREASON) AS RCCODE");
		sqlQuery.append(" FROM SERVICE_CHARGE_TXN_LOG SCTL, PENDING_COMMODITY_TRANSFER PCT, COMMODITY_TRANSFER CT,CHANNEL_CODE CC, TRANSACTION_TYPE TT");
		sqlQuery.append(" WHERE SCTL.STATUS = :status AND SCTL.CREATETIME >= :createtime");
		sqlQuery.append(" AND PCT.ID(+) = SCTL.ID AND CT.ID (+)= SCTL.ID");
		sqlQuery.append(" AND CC.ID = SCTL.CHANNELCODEID AND TT.ID = SCTL.TRANSACTIONTYPEID");	
		sqlQuery.append(" ORDER BY SCTL.ID DESC) WHERE RCCODE IS NOT NULL AND ROWNUM BETWEEN 1 AND :maxLimit");*/
    	
		query = getSQLQuery(sctlQuery.getCustomQuery());
    	query.setInteger("status", sctlQuery.getStatus());  
    	query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
    	query.setInteger("maxLimit", sctlQuery.getLimit());
    	
    	@SuppressWarnings("unchecked")
		List<Object> sqlList1 =(List<Object>) query.list();
    	return sqlList1;
	}
	
	public List<Object> getSummaryTransactions(ServiceChargeTransactionsLogQuery sctlQuery) {
		
		Query query;
		StringBuffer sqlQuery = new StringBuffer();
		/*sqlQuery.append(" SELECT STATUS,COUNT(STATUS) FROM SERVICE_CHARGE_TXN_LOG");
		sqlQuery.append(" WHERE CREATETIME >= :createtime GROUP BY STATUS");*/
    	
		
		query = getSQLQuery(sctlQuery.getCustomQuery());
    	query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
    	
    	@SuppressWarnings("unchecked")
		List<Object> summarList =(List<Object>) query.list();
    	
    	return summarList;
	}
	
	public List<Object> getPerTransactions(ServiceChargeTransactionsLogQuery sctlQuery) {
		
		Query query;
		StringBuffer sqlQuery = new StringBuffer();
		
		/*sqlQuery.append(" SELECT SCTL.STATUS,COUNT(SCTL.STATUS),TT.TRANSACTIONNAME,SCTL.TRANSACTIONTYPEID FROM SERVICE_CHARGE_TXN_LOG SCTL, TRANSACTION_TYPE TT");
		sqlQuery.append(" WHERE SCTL.CREATETIME >= :createtime AND SCTL.TRANSACTIONTYPEID IN (4,5,6,11,13,19)");
		sqlQuery.append(" AND TT.ID = SCTL.TRANSACTIONTYPEID AND SCTL.STATUS IN (0,1,2,3,4,5,6,16,17,18)");
		sqlQuery.append(" GROUP BY SCTL.STATUS,TT.TRANSACTIONNAME,SCTL.TRANSACTIONTYPEID ORDER BY TT.TRANSACTIONNAME");*/
		
    	
		query = getSQLQuery(sctlQuery.getCustomQuery());
    	query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
    	
    	@SuppressWarnings("unchecked")
		List<Object> summarList =(List<Object>) query.list();
    	
    	return summarList;
	}
	
	public List<Object> getRCCodeByTrnsIdList(ServiceChargeTransactionsLogQuery sctlQuery) 
	{
		Query query;	
		
		StringBuffer sqlQuery = new StringBuffer();
		/*sqlQuery.append(" SELECT COALESCE(ct.OperatorResponseCode||'', ct.bankrejectreason) as RcCode, count(*) as count");
		sqlQuery.append(" FROM commodity_transfer ct WHERE createtime >= :createTime group by ct.OperatorResponseCode||'', ct.bankrejectreason");
		sqlQuery.append(" UNION"); 
		sqlQuery.append(" SELECT COALESCE(pct.OperatorResponseCode||'', pct.bankrejectreason) as RcCode, count(*) as count");
		sqlQuery.append(" FROM pending_commodity_transfer pct WHERE	createtime >= :createTime group by pct.OperatorResponseCode||'', pct.bankrejectreason");*/

		query = getSQLQuery(sctlQuery.getCustomQuery());
		query.setTimestamp("createtime", sctlQuery.getCreateTimeGE());
    	@SuppressWarnings("unchecked")
		List<Object> sqlList1 =(List<Object>) query.list();
    	return sqlList1;
    	
	}
}

package com.mfino.monitor.service;

import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.monitor.constants.MonitorPeriodConstants;

public class TransactionService {
	
	private static ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
	
	public static int getSuccessfulCount(ServiceChargeTransactionsLogQuery sctlQuery) {		
		sctlQuery.setStatusList(MonitorPeriodConstants.SUCCESSFUL_SCTL);
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(sctlQuery);
		return sctlResults.size();
	}
	
	public static int getFailedCount(ServiceChargeTransactionsLogQuery sctlQuery) {		
		sctlQuery.setStatusList(MonitorPeriodConstants.FAILED_SCTL);
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(sctlQuery);
		return sctlResults.size();
	}
	
	public static int getPendingCount(ServiceChargeTransactionsLogQuery sctlQuery) {		
		sctlQuery.setStatusList(MonitorPeriodConstants.PENDING_SCTL);
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(sctlQuery);		
		return sctlResults.size();
	}
	
	public static int getProcessingCount(ServiceChargeTransactionsLogQuery sctlQuery) {		
		sctlQuery.setStatusList(MonitorPeriodConstants.PROCESSING_SCTL);		
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(sctlQuery);
		return sctlResults.size();
	}
	
	public static int getReversalsCount(ServiceChargeTransactionsLogQuery sctlQuery) {		
		sctlQuery.setStatusList(MonitorPeriodConstants.REVERSALS_SCTL);		
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(sctlQuery);
		return sctlResults.size();
	}
	
	public static int getIntermediateCount(ServiceChargeTransactionsLogQuery sctlQuery) {		
		sctlQuery.setStatusList(MonitorPeriodConstants.INTERMEDIATE_SCTL);
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(sctlQuery);
		return sctlResults.size();
	}
}

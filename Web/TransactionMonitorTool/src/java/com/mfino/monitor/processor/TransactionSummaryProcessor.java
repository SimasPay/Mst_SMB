package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.List;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.monitor.model.TransactionSummaryResult;
import com.mfino.monitor.service.TransactionService;

/**
 * @author Satya
 * 
 */

public class TransactionSummaryProcessor extends BaseProcessor {
	public List<TransactionSummaryResult> process() {
		List<TransactionSummaryResult> results = new ArrayList<TransactionSummaryResult>();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		int successCount, pendingCount, failedCount, processingCount, reversalsCount, intermediateCount;
		TransactionSummaryResult transactionSummaryResult = null;
		// set monitoringPeriod time
		sctlQuery.setLastUpdateTimeGE(lastUpdateTimeGE);

		// Get Total Successful Transactions
		successCount = TransactionService.getSuccessfulCount(sctlQuery);
		transactionSummaryResult = new TransactionSummaryResult(
				"Total Successful Transactions", successCount);
		results.add(transactionSummaryResult);

		// Get Total Failed Transactions
		failedCount = TransactionService.getFailedCount(sctlQuery);
		transactionSummaryResult = new TransactionSummaryResult(
				"Total Failed Transactions", failedCount);
		results.add(transactionSummaryResult);

		// Get Total Pending Transactions
		pendingCount = TransactionService.getPendingCount(sctlQuery);
		transactionSummaryResult = new TransactionSummaryResult(
				"Total Pending Transactions", pendingCount);
		results.add(transactionSummaryResult);
		
		// Get Total Processing Transactions			
		processingCount = TransactionService.getProcessingCount(sctlQuery);
		transactionSummaryResult = new TransactionSummaryResult(
				"Total Processing Transactions", processingCount);
		results.add(transactionSummaryResult);

		// Get Total Reversals Transactions			
		reversalsCount = TransactionService.getReversalsCount(sctlQuery);
		transactionSummaryResult = new TransactionSummaryResult(
				"Total Reversals Transactions", reversalsCount);
		results.add(transactionSummaryResult);

		// Get Total Intermediate Transactions
		intermediateCount = TransactionService.getIntermediateCount(sctlQuery);
		transactionSummaryResult = new TransactionSummaryResult(
				"Total Intermediate Transactions", intermediateCount);
		results.add(transactionSummaryResult);

		return results;
	}
}

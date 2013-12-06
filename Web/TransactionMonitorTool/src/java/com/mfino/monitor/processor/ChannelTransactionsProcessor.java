package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mfino.dao.query.ChannelCodeQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.monitor.model.ChannelTransactionsResult;
import com.mfino.monitor.service.TransactionService;

/**
 * @author Satya
 * 
 */

public class ChannelTransactionsProcessor extends BaseProcessor {
	public List<ChannelTransactionsResult> process() {
		List<ChannelTransactionsResult> results = new ArrayList<ChannelTransactionsResult>();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();

		ChannelCodeQuery ccQuery = new ChannelCodeQuery();
		List<ChannelCode> ccRes = ccDAO.get(ccQuery);
		Iterator<ChannelCode> iterator = ccRes.iterator();
		int successCount, pendingCount, failedCount, processingCount, reversalsCount, intermediateCount;
		// set monitoringPeriod time
		sctlQuery.setLastUpdateTimeGE(lastUpdateTimeGE);
		while (iterator.hasNext()) {
			ChannelCode channelCode = iterator.next();
			sctlQuery.setSourceChannelApplication(channelCode
					.getChannelSourceApplication());

			// Get Total Successful Transactions			
			successCount = TransactionService.getSuccessfulCount(sctlQuery);

			// Get Total Failed Transactions			
			failedCount = TransactionService.getFailedCount(sctlQuery);

			// Get Total Pending Transactions			
			pendingCount = TransactionService.getPendingCount(sctlQuery);

			// Get Total Processing Transactions			
			processingCount = TransactionService.getProcessingCount(sctlQuery);

			// Get Total Reversals Transactions			
			reversalsCount = TransactionService.getReversalsCount(sctlQuery);

			// Get Total Intermediate Transactions
			intermediateCount = TransactionService.getIntermediateCount(sctlQuery);

			ChannelTransactionsResult r = new ChannelTransactionsResult(
					channelCode, successCount, failedCount, pendingCount,
					processingCount, reversalsCount, intermediateCount);
			results.add(r);
		}
		return results;
	}
}

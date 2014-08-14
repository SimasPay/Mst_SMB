package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.dao.query.ServiceQuery;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.monitor.model.ServiceTransactionsResult;
import com.mfino.monitor.processor.Interface.ServiceTransactionsProcessorI;
import com.mfino.monitor.service.TransactionService;

/**
 * @author Satya
 * 
 */
@org.springframework.stereotype.Service("ServiceTransactionsProcessor")
public class ServiceTransactionsProcessor extends BaseProcessor implements ServiceTransactionsProcessorI{
	
	public List<ServiceTransactionsResult> process() {
		List<ServiceTransactionsResult> results = new ArrayList<ServiceTransactionsResult>();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();

		ServiceQuery serviceQuery = new ServiceQuery();
		List<Service> serviceRes = serviceDAO.get(serviceQuery);
		Iterator<Service> iterator = serviceRes.iterator();
		int count, successCount, pendingCount, failedCount, processingCount, reversalsCount, intermediateCount;
		// set monitoringPeriod time
		//sctlQuery.setLastUpdateTimeGE(lastUpdateTimeGE);
		sctlQuery.setCreateTimeGE(lastUpdateTimeGE);
		while (iterator.hasNext()) {
			Service service = iterator.next();			

			sctlQuery.setServiceID(service.getID());

			// Get Total Transactions
			sctlQuery.setStatus(null);
			sctlQuery.setStatusList(null);
			List<ServiceChargeTransactionLog> sctlResults = sctlDAO
					.get(sctlQuery);
			count = sctlResults.size();

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

			ServiceTransactionsResult r = new ServiceTransactionsResult(
					service, count, successCount, failedCount, pendingCount,
					processingCount, reversalsCount, intermediateCount);
			results.add(r);
		}
		return results;
	}
}

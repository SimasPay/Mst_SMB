package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.dao.query.TransactionTypeQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.monitor.model.FailedTransactionsResult;
import com.mfino.monitor.model.PerTransactionResults;
import com.mfino.monitor.model.ServiceTransactionsResult;
import com.mfino.monitor.model.TransactionSummaryResult;
import com.mfino.monitor.processor.Interface.PerTransactionsProcessorI;
import com.mfino.monitor.service.TransactionService;

@Service("PerTransactionsProcessor")
public class PerTransactionsProcessor extends BaseProcessor implements PerTransactionsProcessorI
{
	public List<PerTransactionResults> process() 
	{
		List<PerTransactionResults> results = new ArrayList<PerTransactionResults>();
		TransactionTypeQuery ttQuery = new TransactionTypeQuery();
		ServiceChargeTransactionsLogQuery sctlQuery = null;
		PerTransactionResults perTransactionResults = null;		
		Integer[] statusList = {0,1,2,3,4,5,6,16,17,18};		
		//long[] transactionTypeList = new long[] {4,5,6,11,13,19};
		
		List<TransactionType> ttList = ttDAO.get(ttQuery);		
		Iterator<TransactionType> iteratorTT = ttList.iterator();
		while (iteratorTT.hasNext()) 
		{
			TransactionType txType = iteratorTT.next();
			String transactionType = txType.getTransactionName();			
			long transactionTypeID = txType.getID();
			int txTypeId = txType.getID().intValue();
			if(txTypeId==4 || txTypeId==5 || txTypeId==6 || txTypeId==11 || txTypeId==13 || txTypeId==19)
			{
				perTransactionResults = new PerTransactionResults();
				sctlQuery = new ServiceChargeTransactionsLogQuery();
				
				int successCount=0;
				int failedCount=0;
				int pendingCount=0;			
				int processingCount=0;
				
				// set monitoringPeriod time
				//sctlQuery.setLastUpdateTimeGE(lastUpdateTimeGE);
				sctlQuery.setCreateTimeGE(lastUpdateTimeGE);

				sctlQuery.setTransactionTypeID(transactionTypeID);
				sctlQuery.setStatusList(statusList);
				
				List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(sctlQuery);	
				Iterator<ServiceChargeTransactionLog> iterator = sctlResults.iterator();
				while (iterator.hasNext()) 
				{
					ServiceChargeTransactionLog sctl = iterator.next();
					int status = sctl.getStatus().intValue();
					if(status == 2 || status == 3 || status == 4 || status == 6)
					{
						successCount++;
					}
					
					if(status == 5)
					{
						failedCount++;
					}
					
					if(status == 16 || status == 17 || status == 18)
					{
						pendingCount++;
					}
					
					if(status == 0 || status == 1)
					{
						processingCount++;
					}
				}
				
				if("BillPay".equals(transactionType))
				{
					perTransactionResults.setTxType("Payment(BillPay)");
				}
				else
				{
					perTransactionResults.setTxType(transactionType);
				}
				
				perTransactionResults.setSuccessful(successCount);
				perTransactionResults.setFailed(failedCount);
				perTransactionResults.setPending(pendingCount);
				perTransactionResults.setProcessing(processingCount);
				results.add(perTransactionResults);
			}
		}		
		return results;
	}	
	
}

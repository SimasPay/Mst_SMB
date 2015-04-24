package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.monitor.model.TransactionSummaryResult;
import com.mfino.monitor.processor.Interface.TransactionSummaryProcessorI;

/**
 * @author Satya
 * 
 */
@Service("TransactionSummaryProcessor")
public class TransactionSummaryProcessor extends BaseProcessor implements TransactionSummaryProcessorI{
	
	private Logger log = Logger.getLogger(TransactionSummaryProcessor.class);
	private String QUERY;
	
	public String getQUERY() {
		return QUERY;
	}

	public void setQUERY(String qUERY) {
		QUERY = qUERY;
	}
	
	public List<TransactionSummaryResult> process() {
		log.info("entered TransactionSummaryProcessor.process method()");
		List<TransactionSummaryResult> results = new ArrayList<TransactionSummaryResult>();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		TransactionSummaryResult transactionSummaryResult = null;
		sctlQuery.setCreateTimeGE(lastUpdateTimeGE);
		sctlQuery.setCustomQuery(getQUERY());
		List<Object> rcSummryTxnList = (List<Object>)tmDAO.getSummaryTransactions(sctlQuery);
		
		String successStr[] = {"2","3","4","6"};
		String failStr[] = {"5"};
		String pendingStr[] = {"16","17","18"};
		String inProgressStr[] = {"0","1"};
		
		List<String> successList = new ArrayList<String>();
		List<String> failList = new ArrayList<String>();
		List<String> pendingList = new ArrayList<String>();
		List<String> inProgressList = new ArrayList<String>();
		
		successList = Arrays.asList(successStr);
		failList = Arrays.asList(failStr);
		pendingList = Arrays.asList(pendingStr);
		inProgressList = Arrays.asList(inProgressStr);
		
		Hashtable<String, Integer> summaryHash = null;
		String status;
		
		if(rcSummryTxnList != null) {
			
			summaryHash = new Hashtable<String, Integer>();
			summaryHash.put("Successfull", 0);
			summaryHash.put("Failed", 0);
			summaryHash.put("Pending", 0);
			summaryHash.put("InProgress", 0);
			
			for (Iterator<Object> it = rcSummryTxnList.iterator(); it.hasNext();) {
	        	
				Object[] object = (Object[]) it.next();
	        	status = String.valueOf(object[0]);
	        	
	        	if(successList.contains(status)) {
	        		summaryHash.put("Successfull",summaryHash.get("Successfull") + Integer.parseInt(String.valueOf(object[1])));
	        	} else if(failList.contains(status)) {
	        		summaryHash.put("Failed",summaryHash.get("Failed") + Integer.parseInt(String.valueOf(object[1])));
	        	} else if(pendingList.contains(status)) {
	        		summaryHash.put("Pending",summaryHash.get("Pending") + Integer.parseInt(String.valueOf(object[1])));
	        	}else if(inProgressList.contains(status)) {
	        		summaryHash.put("InProgress",summaryHash.get("InProgress") + Integer.parseInt(String.valueOf(object[1])));
	        	}
	        }
		}
		
		transactionSummaryResult = new TransactionSummaryResult("Total Successful Transactions", summaryHash.get("Successfull"));
		results.add(transactionSummaryResult);
		
		transactionSummaryResult = new TransactionSummaryResult("Total Failed Transactions", summaryHash.get("Failed"));
		results.add(transactionSummaryResult);
		
		transactionSummaryResult = new TransactionSummaryResult("Total Pending Transactions", summaryHash.get("Pending"));
		results.add(transactionSummaryResult);
		
		transactionSummaryResult = new TransactionSummaryResult("Total InProgress Transactions", summaryHash.get("InProgress"));
		results.add(transactionSummaryResult);

		return results;
	}
}

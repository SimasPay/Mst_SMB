package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.monitor.model.PerTransactionResults;
import com.mfino.monitor.processor.Interface.PerTransactionsProcessorI;

@Service("PerTransactionsProcessor")
public class PerTransactionsProcessor extends BaseProcessor implements PerTransactionsProcessorI
{
	private Logger log = Logger.getLogger(PerTransactionsProcessor.class);
	private String QUERY;
	
	public String getQUERY() {
		return QUERY;
	}

	public void setQUERY(String qUERY) {
		QUERY = qUERY;
	}
	
	public List<PerTransactionResults> process() 
	{
		log.info("entered PerTransactionsProcessor.process method()");
		List<PerTransactionResults> results = new ArrayList<PerTransactionResults>();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		PerTransactionResults perTransactionResults = null;		
		sctlQuery.setCreateTimeGE(lastUpdateTimeGE);
		sctlQuery.setCustomQuery(getQUERY());
		
		List<Object> rcSummryTxnList = (List<Object>)tmDAO.getPerTransactions(sctlQuery);
		
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
		TreeMap<String, Hashtable<String,Integer>> transSummaryHash = null;
		Hashtable<String, Integer> tempSummaryHash = null;
		String status;
		String transactionName;
		String transactionTypeId;
		
		if(rcSummryTxnList != null) {
			
			transSummaryHash = new TreeMap<String, Hashtable<String,Integer>>();
			
			for (Iterator<Object> it = rcSummryTxnList.iterator(); it.hasNext();) {
	        	
				Object[] object = (Object[]) it.next();
				transactionName = String.valueOf(object[2]);
				status = String.valueOf(object[0]);
				transactionTypeId = String.valueOf(object[3]);
				
				if(!transSummaryHash.containsKey(transactionName+"#"+transactionTypeId)) {
					
					summaryHash = new Hashtable<String, Integer>();
					summaryHash.put("Successfull", 0);
					summaryHash.put("Failed", 0);
					summaryHash.put("Pending", 0);
					summaryHash.put("InProgress", 0);
					summaryHash.put("Count", 0);
					
					transSummaryHash.put(transactionName+"#"+transactionTypeId, summaryHash);
				}
				tempSummaryHash = transSummaryHash.get(transactionName+"#"+transactionTypeId);
					
	        	if(successList.contains(status)) {
	        		tempSummaryHash.put("Successfull",tempSummaryHash.get("Successfull") + Integer.parseInt(String.valueOf(object[1])));
	        	} else if(failList.contains(status)) {
	        		tempSummaryHash.put("Failed",tempSummaryHash.get("Failed") + Integer.parseInt(String.valueOf(object[1])));
	        	} else if(pendingList.contains(status)) {
	        		tempSummaryHash.put("Pending",tempSummaryHash.get("Pending") + Integer.parseInt(String.valueOf(object[1])));
	        	}else if(inProgressList.contains(status)) {
	        		tempSummaryHash.put("InProgress",tempSummaryHash.get("InProgress") + Integer.parseInt(String.valueOf(object[1])));
	        	}
	        	tempSummaryHash.put("Count", tempSummaryHash.get("Count") + Integer.parseInt(String.valueOf(object[1])));
	        	transSummaryHash.put(transactionName+"#"+transactionTypeId,tempSummaryHash);
	        }
			Set<String> transSet = transSummaryHash.keySet();
			for (Iterator iterator = transSet.iterator(); iterator.hasNext();) {
				String transTypeName = (String) iterator.next();
				tempSummaryHash = transSummaryHash.get(transTypeName);
				perTransactionResults = new PerTransactionResults();
				perTransactionResults.setCount(tempSummaryHash.get("Count"));
				perTransactionResults.setTxnTypeId(Integer.parseInt(transTypeName.substring(transTypeName.indexOf("#")+1)));
				perTransactionResults.setTxType(transTypeName.substring(0, transTypeName.indexOf("#")));
				perTransactionResults.setSuccessful(tempSummaryHash.get("Successfull"));
				perTransactionResults.setFailed(tempSummaryHash.get("Failed"));
				perTransactionResults.setPending(tempSummaryHash.get("Pending"));
				perTransactionResults.setProcessing(tempSummaryHash.get("InProgress"));
				results.add(perTransactionResults);
			}
		}
		return results;
	}	
}

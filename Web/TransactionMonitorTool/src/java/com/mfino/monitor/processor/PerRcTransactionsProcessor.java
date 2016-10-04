package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.monitor.model.PerRcTransactionResults;
import com.mfino.monitor.processor.Interface.PerRcTransactionsProcessorI;
import com.mfino.monitor.util.ExternalResponseCodeHolder;


@Service("PerRcTransactionsProcessor")
public class PerRcTransactionsProcessor extends BaseProcessor implements PerRcTransactionsProcessorI{
	
	private Logger log = Logger.getLogger(PerRcTransactionsProcessor.class);
	TreeMap<String, Integer> rcSumCount = null;
	int maxLimit = 990;
	private String QUERY;
	
	public String getQUERY() {
		return QUERY;
	}

	public void setQUERY(String qUERY) {
		QUERY = qUERY;
	}
	
	public List<PerRcTransactionResults> process() 
	{
		log.info("entered FailedTransactionsProcessor.process method()");
		PerRcTransactionResults prtr = null;
		List<PerRcTransactionResults> results = new ArrayList<PerRcTransactionResults>();
		getRcCodes();
		
		if(rcSumCount != null) {
			
			ExternalResponseCodeHolder erc = ExternalResponseCodeHolder.getInstance();
			for (String key : rcSumCount.keySet()) 
			{	
				prtr = new PerRcTransactionResults();
	 			prtr.setRcCode(key);
	 			prtr.setCount(rcSumCount.get(key).intValue());
	 			prtr.setRcDescription(erc.getDescription(key));
	 			results.add(prtr);
			}
		}
		return results;
	}

	private void getRcCodes()
	{	
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setCustomQuery(getQUERY());
		sctlQuery.setCreateTimeGE(lastUpdateTimeGE);
		
		List<Object> rcCodeList = (List<Object>)tmDAO.getRCCodeByTrnsIdList(sctlQuery);
		log.info("count of rc records received in PerRcTransactionsProcessor for given sctlList is: "+rcCodeList.size());
    	if(rcCodeList != null && rcCodeList.size() > 0)
    	{
    		rcSumCount = new TreeMap<String, Integer>();
    		
	        for (Iterator<Object> it = rcCodeList.iterator(); it.hasNext();) 
	        {
	        	String rcCode="";
	        	Object[] object = (Object[]) it.next();
	        	if(object[0] != null && object[0] != "")
		      	{
		    	  rcCode = String.valueOf(object[0]);
		    	  
		    	  if(rcCode.length() == 1) {
		    		  
		    		  rcCode = "0" + rcCode;
		    	  }
		      	}
		      	Integer rcCount = Integer.valueOf(object[1].toString());
				if(rcCode != null && !rcCode.isEmpty() && !rcCode.equals("00") && !rcCode.equals("0"))
				{
					if(rcSumCount != null && rcSumCount.containsKey(rcCode)) 
					{		 				
		 				rcSumCount.put(rcCode, rcSumCount.get(rcCode) + rcCount);		
		 				
		 			} else {
		 				
		 				rcSumCount.put(rcCode, rcCount);
		 			}
				}
	        }
    	}						
	}
}

package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.dao.query.TransactionTypeQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.monitor.model.FailedTransactionsResult;
import com.mfino.monitor.model.PerRcTransactionResults;
import com.mfino.monitor.model.PerTransactionResults;
import com.mfino.monitor.processor.Interface.FailedTransactionsProcessorI;
import com.mfino.monitor.processor.Interface.PerRcTransactionsProcessorI;

@Service("PerRcTransactionsProcessor")
public class PerRcTransactionsProcessor extends BaseProcessor implements PerRcTransactionsProcessorI{
	
	TreeMap<String, Integer> rcSumCount = null;
	int maxLimit = 990;
	
	public List<PerRcTransactionResults> process() 
	{
		List<PerRcTransactionResults> results = new ArrayList<PerRcTransactionResults>();
		ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
		
		// set monitoringPeriod time
		//query.setLastUpdateTimeGE(lastUpdateTimeGE);
		query.setCreateTimeGE(lastUpdateTimeGE);
		
		PerRcTransactionResults prtr = null;
		List<Long> sctlList = new ArrayList<Long>();
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(query);
		Iterator<ServiceChargeTransactionLog> iteratorSctl = sctlResults.iterator();
		while (iteratorSctl.hasNext()) 
		{
			ServiceChargeTransactionLog sctl = iteratorSctl.next();			
			Long sctlId = (Long)sctl.getTransactionID();
			sctlList.add(sctlId);
		}		
		if(null != sctlList && sctlList.size() > maxLimit) 
		{			
			rcSumCount = new TreeMap<String, Integer>();			
			int firstRec = 0;
			int lastRec = maxLimit;			
			for (int i = 0; i < sctlList.size() / maxLimit; i++) 
			{				
				if(i > 0 ) 
				{					
					firstRec = lastRec + 1;
					lastRec = firstRec + maxLimit;
				}
				List<Long> subRcCodeList = (List<Long>)sctlList.subList(firstRec, lastRec + 1);
				getRcCodes(subRcCodeList);				
			}			
			if(sctlList.size() % maxLimit != 0) 
			{					
				firstRec = lastRec + 1;
				lastRec = sctlList.size();				
				List<Long> subRcCodeList = (List<Long>)sctlList.subList(firstRec, lastRec);
				getRcCodes(subRcCodeList);					
			}			
			for (String key : rcSumCount.keySet()) 
			{	
				prtr = new PerRcTransactionResults();
	 			prtr.setRcCode(key);
	 			prtr.setCount(rcSumCount.get(key).intValue());
	 			results.add(prtr);
			}			
		}				
		return results;
	}
	
	private void getRcCodes(List<Long> scListt)
	{				
		List<Object> rcCodeList = (List<Object>)commodityTransferDAO.getRCCodeByTrnsIdList(scListt);		
    	if(rcCodeList.size() > 0)
    	{
	        for (Iterator<Object> it = rcCodeList.iterator(); it.hasNext();) 
	        {
	        	String rcCode="";
	        	Object[] object = (Object[]) it.next();
	        	if(object[0] != null && object[0] != "")
		      	{
		    	  rcCode = String.valueOf(object[0]);
		      	}
		      	Integer rcCount = Integer.valueOf(object[1].toString());
				if(rcCode != null && !rcCode.isEmpty() && !rcCode.equals("00") && !rcCode.equals("0"))
				{
					if(rcSumCount.containsKey(rcCode)) 
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

package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.fix.CmFinoFIX;
import com.mfino.monitor.model.FailedTransactionsResult;
import com.mfino.monitor.processor.Interface.FailedTransactionsProcessorI;

/**
 * @author Satya
 * 
 */
@Service("FailedTransactionsProcessor")
public class FailedTransactionsProcessor extends BaseProcessor implements FailedTransactionsProcessorI{
	
	private Logger log = Logger.getLogger(FailedTransactionsProcessor.class);
	
	private String QUERY;
	
	public String getQUERY() {
		return QUERY;
	}

	public void setQUERY(String qUERY) {
		QUERY = qUERY;
	}
	
	public int txnLimit=0;
	
	public int getTxnLimit() {
		return txnLimit;
	}

	public void setTxnLimit(int txnLimit) {
		this.txnLimit = txnLimit;
	}

	public List<FailedTransactionsResult> process() {
		log.info("entered FailedTransactionsProcessor.process method()");
		List<FailedTransactionsResult> results = new ArrayList<FailedTransactionsResult>();
		ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
		query.setCreateTimeGE(lastUpdateTimeGE);		
		query.setStatus(CmFinoFIX.SCTLStatus_Failed);
		query.setLimit(this.getTxnLimit());
		query.setCustomQuery(getQUERY());
		
		List<Object> rcFailedTxnList = (List<Object>)tmDAO.getRCFailedTransactions(query);
		String rcCode;
		
    	if(rcFailedTxnList != null && rcFailedTxnList.size() > 0)
    	{
	        for (Iterator<Object> it = rcFailedTxnList.iterator(); it.hasNext();) 
	        {
	        	Object[] object = (Object[]) it.next();

				FailedTransactionsResult ftr = new FailedTransactionsResult();
				ftr.setAmount(String.valueOf(object[6]));
				ftr.setMobileNumber(String.valueOf(object[1]));
				ftr.setReason(String.valueOf(object[2]));
				ftr.setRefID(String.valueOf(object[0]));
				ftr.setChannelName(String.valueOf(object[4]));
				ftr.setTransactionType(String.valueOf(object[5]));
				
				rcCode = String.valueOf(object[7]);
				
				if(StringUtils.isBlank(rcCode) || StringUtils.isEmpty(rcCode) || "null".equals(rcCode))
					ftr.setRcCode("-");
				else
					ftr.setRcCode((rcCode.length() == 1 ? "0"+rcCode : rcCode));
				
				ftr.setTxnDateTime(String.valueOf(object[3]));
				results.add(ftr);
	        }		
    	}
		return results;
	}
}

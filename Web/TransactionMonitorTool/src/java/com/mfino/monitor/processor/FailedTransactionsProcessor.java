package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.mfino.monitor.processor.Interface.FailedTransactionsProcessorI;

/**
 * @author Satya
 * 
 */
@Service("FailedTransactionsProcessor")
public class FailedTransactionsProcessor extends BaseProcessor implements FailedTransactionsProcessorI{
	
	//public Log log = LogFactory.getLog(this.getClass());
	
	public int txnLimit=0;
	
	public int getTxnLimit() {
		return txnLimit;
	}

	public void setTxnLimit(int txnLimit) {
		this.txnLimit = txnLimit;
	}

	public List<FailedTransactionsResult> process() {
		List<FailedTransactionsResult> results = new ArrayList<FailedTransactionsResult>();
		ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
		TransactionTypeQuery ttQuery = new TransactionTypeQuery();
		// set monitoringPeriod time
		//query.setLastUpdateTimeGE(lastUpdateTimeGE);
		query.setCreateTimeGE(lastUpdateTimeGE);		
		query.setStatus(CmFinoFIX.SCTLStatus_Failed);
		query.setIDOrdered(true);
		int i = 1;
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(query);
		//System.out.println("sctl records for failed transactions: "+sctlResults.size());
		Iterator<ServiceChargeTransactionLog> iterator = sctlResults.iterator();
		while (iterator.hasNext()) {
			ServiceChargeTransactionLog sctl = iterator.next();
			Long transactionTypeID = sctl.getTransactionTypeID();
			ttQuery.setId(transactionTypeID);
			List<TransactionType> ttRes = ttDAO.get(ttQuery);
			String transactionType = ttRes.get(0).getTransactionName();

			// Get Channel code name
			ChannelCode cc = ccDAO.getById(sctl.getChannelCodeID());
			String rcCode = commodityTransferDAO.getRCCodeByTrnsId(sctl.getTransactionID());
			if(rcCode != null && !rcCode.isEmpty() && !rcCode.equals("00") && !rcCode.equals("0"))
			{
				FailedTransactionsResult ftr = new FailedTransactionsResult();
				ftr.setAmount(sctl.getTransactionAmount());
				ftr.setMobileNumber(sctl.getSourceMDN());
				ftr.setReason(sctl.getFailureReason());
				ftr.setRefID(sctl.getID());
				ftr.setChannelName(cc != null ? cc.getChannelName() : "");
				ftr.setTransactionType(transactionType);
				ftr.setRcCode(rcCode);
				ftr.setTxnDateTime(sctl.getCreateTime().toString());
				results.add(ftr);
				
				i++;
			}			
			if(i == this.getTxnLimit())
				break;
		}
		return results;
	}
}

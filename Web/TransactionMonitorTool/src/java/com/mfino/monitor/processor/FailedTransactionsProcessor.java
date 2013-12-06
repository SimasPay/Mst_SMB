package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.dao.query.TransactionTypeQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.monitor.model.FailedTransactionsResult;

/**
 * @author Satya
 * 
 */

public class FailedTransactionsProcessor extends BaseProcessor {
	public List<FailedTransactionsResult> process() {
		List<FailedTransactionsResult> results = new ArrayList<FailedTransactionsResult>();
		ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
		TransactionTypeQuery ttQuery = new TransactionTypeQuery();		
		query.setStatus(CmFinoFIX.SCTLStatus_Failed);
		query.setIDOrdered(true);
		query.setStart(0);
		query.setLimit(5);
		List<ServiceChargeTransactionLog> sctlResults = sctlDAO.get(query);
		Iterator<ServiceChargeTransactionLog> iterator = sctlResults.iterator();
		while (iterator.hasNext()) {
			ServiceChargeTransactionLog sctl = iterator.next();
			// Get Transaction name
			Long transactionTypeID = sctl.getTransactionTypeID();
			ttQuery.setId(transactionTypeID);
			List<TransactionType> ttRes = ttDAO.get(ttQuery);
			String transactionType = ttRes.get(0).getTransactionName();

			// Get Channel code name
			ChannelCode cc = ccDAO.getById(sctl.getChannelCodeID());

			FailedTransactionsResult ftr = new FailedTransactionsResult();
			ftr.setAmount(sctl.getTransactionAmount());
			ftr.setMobileNumber(sctl.getSourceMDN());
			ftr.setReason(sctl.getFailureReason());
			ftr.setRefID(sctl.getID());
			ftr.setChannelName(cc != null ? cc.getChannelName() : "");
			ftr.setTransactionType(transactionType);
			results.add(ftr);
		}
		return results;
	}
}

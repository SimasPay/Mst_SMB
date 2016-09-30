package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.TransactionAmountDistributionQuery;
import com.mfino.domain.TxnAmountDstrbLog;

public interface TransactionAmountDistributionLogService {
	public void save(TxnAmountDstrbLog tadl);
	public List<TxnAmountDstrbLog> get(TransactionAmountDistributionQuery query);
}

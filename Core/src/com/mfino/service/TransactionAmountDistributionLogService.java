package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.TransactionAmountDistributionQuery;
import com.mfino.domain.TransactionAmountDistributionLog;

public interface TransactionAmountDistributionLogService {
	public void save(TransactionAmountDistributionLog tadl);
	public List<TransactionAmountDistributionLog> get(TransactionAmountDistributionQuery query);
}

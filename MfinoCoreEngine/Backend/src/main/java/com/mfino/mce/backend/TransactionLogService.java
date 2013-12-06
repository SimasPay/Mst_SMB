package com.mfino.mce.backend;

import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;

/**
 * @author sasidhar
 *
 */
public interface TransactionLogService {
	
	public TransactionsLog createTransactionLog(CFIXMsg fix);
}

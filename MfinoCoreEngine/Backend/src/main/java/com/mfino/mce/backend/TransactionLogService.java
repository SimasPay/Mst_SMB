package com.mfino.mce.backend;

import com.mfino.domain.TransactionLog;
import com.mfino.fix.CFIXMsg;

/**
 * @author sasidhar
 *
 */
public interface TransactionLogService {
	
	public TransactionLog createTransactionLog(CFIXMsg fix);
}

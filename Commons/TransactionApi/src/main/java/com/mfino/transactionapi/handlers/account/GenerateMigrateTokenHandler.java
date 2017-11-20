package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

public interface GenerateMigrateTokenHandler {
	Result handle(TransactionDetails transactionDetails);
}

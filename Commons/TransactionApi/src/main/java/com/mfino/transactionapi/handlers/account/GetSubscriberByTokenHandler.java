package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

public abstract interface GetSubscriberByTokenHandler {
	public abstract Result handle(TransactionDetails paramTransactionDetails);
}

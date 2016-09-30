package com.mfino.service;

import com.mfino.domain.PendingTxnsFile;
import com.mfino.exceptions.MfinoRuntimeException;

public interface PendingTransactionsService {
	
	public void savePendingTransactions(PendingTxnsFile fileToSave) throws MfinoRuntimeException;
}

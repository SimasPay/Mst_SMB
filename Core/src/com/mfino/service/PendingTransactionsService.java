package com.mfino.service;

import com.mfino.domain.PendingTransactionsFile;
import com.mfino.exceptions.MfinoRuntimeException;

public interface PendingTransactionsService {
	
	public void savePendingTransactions(PendingTransactionsFile fileToSave) throws MfinoRuntimeException;
}

package com.mfino.transactionapi.service;

import com.mfino.transactionapi.vo.TransactionDetails;

public interface ActorChannelValidationService {

	public boolean validateTransaction(TransactionDetails transactionDetails);

}

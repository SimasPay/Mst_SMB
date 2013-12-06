package com.mfino.transactionapi.handlers.nfc;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

public interface NFCCardLinkHandler {

	public Result handle(TransactionDetails transactionDetails);
}

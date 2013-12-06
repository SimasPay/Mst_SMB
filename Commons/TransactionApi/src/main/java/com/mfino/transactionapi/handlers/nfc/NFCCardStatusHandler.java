package com.mfino.transactionapi.handlers.nfc;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatus;
import com.mfino.result.Result;

public interface NFCCardStatusHandler {

	public CFIXMsg handle(CMNFCCardStatus nfcCardStatus);
}

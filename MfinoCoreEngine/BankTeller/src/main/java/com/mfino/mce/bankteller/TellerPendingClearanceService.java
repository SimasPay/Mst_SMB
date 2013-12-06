package com.mfino.mce.bankteller;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMTellerPendingCommodityTransferRequest;

public interface TellerPendingClearanceService {

	public CFIXMsg processMessage(CMTellerPendingCommodityTransferRequest fixPendingRequest);
}

package com.mfino.zenith.interbank;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMInterBankPendingCommodityTransferRequest;

/**
 * @author Sasi
 *
 */
public interface InterBankPendingClearanceService {
	public CFIXMsg processMessage(CMInterBankPendingCommodityTransferRequest fixPendingRequest);
}

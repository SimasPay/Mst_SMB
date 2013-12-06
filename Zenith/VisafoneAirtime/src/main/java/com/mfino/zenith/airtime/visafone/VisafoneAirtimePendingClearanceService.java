package com.mfino.zenith.airtime.visafone;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePendingCommodityTransferRequest;

public interface VisafoneAirtimePendingClearanceService {

	CFIXMsg processMessage(
			CMVisafoneAirtimePendingCommodityTransferRequest baseMessage);

}

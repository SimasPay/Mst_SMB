package com.mfino.zenith.dstv;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMDSTVPendingCommodityTransferRequest;

/**
 * 
 * @author Sasi
 *
 */
public interface DSTVPendingClearanceService {

	public CFIXMsg processMessage(CMDSTVPendingCommodityTransferRequest fixPendingRequest);
}

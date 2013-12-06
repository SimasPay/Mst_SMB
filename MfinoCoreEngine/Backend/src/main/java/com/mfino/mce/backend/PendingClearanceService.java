package com.mfino.mce.backend;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;

/**
 * 
 * @author POCHADRI
 *
 */
public interface PendingClearanceService 
{
	public CFIXMsg processMessage(CMPendingCommodityTransferRequest fixPendingRequest);
}

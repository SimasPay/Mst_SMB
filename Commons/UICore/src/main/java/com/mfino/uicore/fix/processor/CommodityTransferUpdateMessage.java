package com.mfino.uicore.fix.processor;

import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CRPendingCommodityTransfer;

public interface CommodityTransferUpdateMessage {
	 public void updateMessage(CommodityTransfer c,
	            PendingCommodityTransfer c2,
	            CMJSCommodityTransfer.CGEntries entry, CMJSCommodityTransfer realMsg) ;
}

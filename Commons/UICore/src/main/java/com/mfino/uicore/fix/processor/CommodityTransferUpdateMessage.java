package com.mfino.uicore.fix.processor;

import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CRPendingCommodityTransfer;

public interface CommodityTransferUpdateMessage {
	 public void updateMessage(CRCommodityTransfer c,
	            CRPendingCommodityTransfer pct,
	            CMJSCommodityTransfer.CGEntries entry, CMJSCommodityTransfer realMsg) ;
}

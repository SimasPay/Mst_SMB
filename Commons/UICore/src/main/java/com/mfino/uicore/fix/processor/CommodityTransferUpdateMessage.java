package com.mfino.uicore.fix.processor;

import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;

public interface CommodityTransferUpdateMessage {
	 public void updateMessage(CommodityTransfer ct,
	            CMJSCommodityTransfer.CGEntries entry, CMJSCommodityTransfer realMsg) ;
	 public void updateMessage(PendingCommodityTransfer pct,
	            CMJSCommodityTransfer.CGEntries entry, CMJSCommodityTransfer realMsg) ;
	 
}

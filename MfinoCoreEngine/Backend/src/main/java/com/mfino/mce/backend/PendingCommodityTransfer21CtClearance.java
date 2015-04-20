/**
 * 
 */
package com.mfino.mce.backend;

import java.util.List;

import com.mfino.domain.PendingCommodityTransfer;

/**
 * @author srinivaas
 *
 */
public interface PendingCommodityTransfer21CtClearance {
	
	public List<PendingCommodityTransfer> getAll21NonPendingTransfers();

	public void calculateFinalState(PendingCommodityTransfer pct);
}

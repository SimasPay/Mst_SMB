/**
 * 
 */
package com.mfino.mce.backend;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX;

/**
 * @author srinivaas
 *
 */
public class PendingCommodityTransfer21CtClearanceJob {

	private static Log log = LogFactory.getLog(PendingCommodityTransfer21CtClearanceJob.class);
	private PendingCommodityTransfer21CtClearance pct21ctClearance;
	
	public void movePendingToComplete() {
		log.info("PendingCommodityTransfer21CtClearanceImpl:: movePendingToComplete() Begin");

		List<PendingCommodityTransfer> lst = pct21ctClearance.getAll21NonPendingTransfers();
		for (PendingCommodityTransfer pct: lst) {
			try{
				if(pct.getSourcepockettype() == CmFinoFIX.PocketType_BankAccount && pct.getDestpockettype().intValue() == CmFinoFIX.PocketType_BankAccount){
					pct21ctClearance.calculateFinalState(pct);
				}
			}
			catch (Exception e) {
				log.error("Exception in movePendingToComplete of PCT ID: " + pct.getId(),e);
			}
		}
		
		log.info("PendingCommodityTransfer21CtClearanceImpl:: movePendingToComplete() End");
	}

	public PendingCommodityTransfer21CtClearance getPct21ctClearance() {
		return pct21ctClearance;
	}

	public void setPct21ctClearance(PendingCommodityTransfer21CtClearance pct21ctClearance) {
		this.pct21ctClearance = pct21ctClearance;
	}

}

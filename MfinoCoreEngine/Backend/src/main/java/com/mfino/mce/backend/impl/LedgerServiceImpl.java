package com.mfino.mce.backend.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.LockMode;

import com.mfino.constants.DAOConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Ledger;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.mce.backend.LedgerService;

public class LedgerServiceImpl extends BaseServiceImpl implements LedgerService {
	
	@Deprecated
	public Ledger createLedgerEntry(Pocket srcPocket, Pocket destPocket, CommodityTransfer ct, PendingCommodityTransfer pct, BigDecimal amount) {

		if (amount.compareTo(BigDecimal.ZERO) < 1 || srcPocket.getId().equals(destPocket.getId()))
			return null;

		Ledger ledger = new Ledger();
		ledger.setSourcepocketid(srcPocket.getId());
		ledger.setSourcemdn(srcPocket.getSubscriberMdn().getMdn());
		ledger.setSourcepocketbalance(srcPocket.getCurrentbalance());
		ledger.setDestmdn(destPocket.getSubscriberMdn().getMdn());
		ledger.setDestpocketid(destPocket.getId());
		ledger.setDestpocketbalance(destPocket.getCurrentbalance());
		ledger.setAmount(amount);
		if (ct != null) {
			ledger.setCommoditytransferid(ct.getId());
		}
		else if (pct != null) {
			ledger.setCommoditytransferid(pct.getId());
		}
		srcPocket.setCurrentbalance(String.valueOf(moneyService.subtract(new BigDecimal(srcPocket.getCurrentbalance()), amount)));
		destPocket.setCurrentbalance(String.valueOf(moneyService.add(new BigDecimal(destPocket.getCurrentbalance()), amount)));

		coreDataWrapper.save(ledger);
		return ledger;
	}
	
	/**
	 * Generates the List of Ledger entries required based on the given data
	 * @param sctlId
	 * @param ctID
	 * @param srcPocket
	 * @param destPocket
	 * @param chargesPocket
	 * @param amount
	 * @param charges
	 * @param isNettingOn
	 * @return
	 */
	public List<MFSLedger> createLedgerEntries(boolean isSettlement ,Long sctlId, Long ctID, Pocket srcPocket, Pocket destPocket, Pocket chargesPocket, BigDecimal amount, BigDecimal charges, 
			boolean isNettingOn) {
		List<MFSLedger> lstMfsLedgers = null;
		BigDecimal srcAmount = null;
		Pocket suspensePocket = coreDataWrapper.getSuspensePocket();
		if (srcPocket.getId().equals(destPocket.getId())) {
			return null;
		}
		
		if (BigDecimal.ZERO.compareTo(amount) >= 0 ) {
			return null;
		}
		
		lstMfsLedgers = new ArrayList<MFSLedger>();
		
		if (charges.compareTo(BigDecimal.ZERO) == 1) {
			srcAmount = amount.add(charges);
		} else {
			srcAmount = amount;
		}
		
		// Generates the Source Ledger entry.
		boolean isImmediateUpdateRequired = isImmediateUpdateRequiredForPocket(srcPocket) ;
		
		lstMfsLedgers.add(generateLedgerEntry(isSettlement,sctlId, ctID, srcPocket, srcAmount, true, isImmediateUpdateRequired));
		if (isSettlement || isImmediateUpdateRequired) {
			srcPocket.setCurrentbalance(String.valueOf(moneyService.subtract(new BigDecimal(srcPocket.getCurrentbalance()), srcAmount)));
		}
		
		// Generates the Suspense pocket ledger entries as the Netting is OFF.
		if ((! isNettingOn) && !(destPocket.getId().equals(suspensePocket.getId())) && !(srcPocket.getId().equals(suspensePocket.getId())) ) {
			lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, suspensePocket, srcAmount, false, false));
			lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, suspensePocket, amount, true, false));
		}
		// Generates the Destination Ledger entry.
		isImmediateUpdateRequired = isImmediateUpdateRequiredForPocket(destPocket) ;
		
		lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, destPocket, amount, false, isImmediateUpdateRequired));
		if (isImmediateUpdateRequired) {
			destPocket.setCurrentbalance(String.valueOf(moneyService.add(new BigDecimal(destPocket.getCurrentbalance()), amount)));
		}
		
		if (charges.compareTo(BigDecimal.ZERO) == 1) {
			// Generates the Suspense pocket ledger entries as the Netting is OFF.
			if ((! isNettingOn) && !(destPocket.getId().equals(suspensePocket.getId())) ) {
				lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, suspensePocket, charges, true, false));
			}
			// Generates the Charges pocket Ledger entry.
			lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, chargesPocket, charges, false, false));
		}
		
		return lstMfsLedgers;
	}

	/**
	 * Checks whether immediate update of balance for the given pocket is required or not
	 * @param pocket
	 * @return
	 */
	public boolean isImmediateUpdateRequiredForPocket(Pocket pocket) {
		boolean isImmediateUpdateRequired = false;
		Pocket globalSVAPocket = coreDataWrapper.getGlobalSVAPocket();
		Pocket taxPocket = coreDataWrapper.getPocket(SystemParameterKeys.TAX_POCKET_ID_KEY);
		
		if ((pocket.getPocketTemplate().getIscollectorpocket() != null) && pocket.getPocketTemplate().getIscollectorpocket()) {
			isImmediateUpdateRequired = false;
		}
		else if ((pocket.getPocketTemplate().getIssuspencepocket() != null) && pocket.getPocketTemplate().getIssuspencepocket()) {
			isImmediateUpdateRequired = false;
		}else if ((pocket.getPocketTemplate().getIssystempocket() != null) && pocket.getPocketTemplate().getIssystempocket()) {
			isImmediateUpdateRequired = false;
		}
		else if (pocket.getId().equals(globalSVAPocket.getId()) ) {
			isImmediateUpdateRequired = false;
		}
		else if (pocket.getId().equals(taxPocket.getId()) ) {
			isImmediateUpdateRequired = false;
		}
		else {
			isImmediateUpdateRequired = true;
		}
		
		return isImmediateUpdateRequired;
	}
	
	/**
	 * Generates the MFS Ledger object based on the given data
	 * @param sctlId
	 * @param ctID
	 * @param pocket
	 * @param amount
	 * @param isSource
	 * @param isImmediateUpdateRequired
	 * @return
	 */
	private MFSLedger generateLedgerEntry(boolean isSettlement,Long sctlId, Long ctID, Pocket pocket, BigDecimal amount, boolean isSource, boolean isImmediateUpdateRequired) {
		MFSLedger mfsLedger = new MFSLedger();
		mfsLedger.setSctlid(new BigDecimal(sctlId));
		mfsLedger.setCommoditytransferid(new BigDecimal(ctID));
		mfsLedger.setPocketid(pocket.getId());
		mfsLedger.setAmount(amount);
		
		if (isSource) {
			mfsLedger.setLedgertype(DAOConstants.DEBIT_LEDGER_TYPE);
		} else {
			mfsLedger.setLedgertype(DAOConstants.CREDIT_LEDGER_TYPE);
		}
		
		if (isSettlement || isImmediateUpdateRequired) {
			mfsLedger.setLedgerstatus(DAOConstants.LEDGER_STATUS_UPDATED);
		} else {
			mfsLedger.setLedgerstatus(DAOConstants.LEDGER_STATUS_DEFERED);
		}
		return mfsLedger;
	}
	
	/**
	 * Generates the Reverse Ledger entry for the given MFSLedger and updates the pocket balance if immediate update required
	 * @param mfsLedger
	 * @return
	 */
	public MFSLedger generateReverseLedgerEntry(MFSLedger mfsLedger) {
		Pocket suspensePocket = coreDataWrapper.getSuspensePocket();
		Pocket globalSVAPocket = coreDataWrapper.getGlobalSVAPocket();
		Pocket globalChargePocket = coreDataWrapper.getChargesPocket();
		boolean isImmediateUpdateRequired = false;
		
		Pocket reversePocket = null;
		if (suspensePocket.getId().equals(mfsLedger.getPocketid()) ) {
			reversePocket = suspensePocket;
		} 
		else if (globalSVAPocket.getId().equals(mfsLedger.getPocketid()) ) {
			reversePocket = globalSVAPocket;
		}
		else if (globalChargePocket.getId().equals(mfsLedger.getPocketid()) ) {
			reversePocket = globalChargePocket;
		} 
		else {
			reversePocket = coreDataWrapper.getPocketById(mfsLedger.getPocketid().longValue(), LockMode.UPGRADE);
			isImmediateUpdateRequired = true;
		}

		boolean isSource = DAOConstants.DEBIT_LEDGER_TYPE.equals(mfsLedger.getLedgertype()) ? false : true;
		
		MFSLedger reverseLedger = generateLedgerEntry(false,mfsLedger.getSctlid().longValue(), mfsLedger.getCommoditytransferid().longValue(), reversePocket,
				mfsLedger.getAmount(), isSource, isImmediateUpdateRequired);
		
		if (isImmediateUpdateRequired) {
			if (DAOConstants.DEBIT_LEDGER_TYPE.equals(mfsLedger.getLedgertype())) {
				reversePocket.setCurrentbalance(String.valueOf(moneyService.add(new BigDecimal(reversePocket.getCurrentbalance()), mfsLedger.getAmount())));
			}
			else if (DAOConstants.CREDIT_LEDGER_TYPE.equals(mfsLedger.getLedgertype())) {
				reversePocket.setCurrentbalance(String.valueOf(moneyService.subtract(new BigDecimal(reversePocket.getCurrentbalance()), mfsLedger.getAmount())));
			}
			coreDataWrapper.save(reversePocket);
		}
		
		return reverseLedger;
	}
	
}

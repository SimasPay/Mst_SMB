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

		if (amount.compareTo(BigDecimal.ZERO) < 1 || srcPocket.getID().equals(destPocket.getID()))
			return null;

		Ledger ledger = new Ledger();
		ledger.setSourcePocketID(srcPocket.getID());
		ledger.setSourceMDN(srcPocket.getSubscriberMDNByMDNID().getMDN());
		ledger.setSourcePocketBalance(srcPocket.getCurrentBalance());
		ledger.setDestMDN(destPocket.getSubscriberMDNByMDNID().getMDN());
		ledger.setDestPocketID(destPocket.getID());
		ledger.setDestPocketBalance(destPocket.getCurrentBalance());
		ledger.setAmount(amount);
		if (ct != null) {
			ledger.setCommodityTransferID(ct.getID());
		}
		else if (pct != null) {
			ledger.setCommodityTransferID(pct.getID());
		}
		srcPocket.setCurrentBalance(moneyService.subtract(srcPocket.getCurrentBalance(), amount));
		destPocket.setCurrentBalance(moneyService.add(destPocket.getCurrentBalance(), amount));

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
		if (srcPocket.getID().equals(destPocket.getID())) {
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
			srcPocket.setCurrentBalance(moneyService.subtract(srcPocket.getCurrentBalance(), srcAmount));
		}
		
		// Generates the Suspense pocket ledger entries as the Netting is OFF.
		if ((! isNettingOn) && !(destPocket.getID().equals(suspensePocket.getID())) && !(srcPocket.getID().equals(suspensePocket.getID())) ) {
			lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, suspensePocket, srcAmount, false, false));
			lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, suspensePocket, amount, true, false));
		}
		// Generates the Destination Ledger entry.
		isImmediateUpdateRequired = isImmediateUpdateRequiredForPocket(destPocket) ;
		
		lstMfsLedgers.add(generateLedgerEntry(false,sctlId, ctID, destPocket, amount, false, isImmediateUpdateRequired));
		if (isImmediateUpdateRequired) {
			destPocket.setCurrentBalance(moneyService.add(destPocket.getCurrentBalance(), amount));
		}
		
		if (charges.compareTo(BigDecimal.ZERO) == 1) {
			// Generates the Suspense pocket ledger entries as the Netting is OFF.
			if ((! isNettingOn) && !(destPocket.getID().equals(suspensePocket.getID())) ) {
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
		
		if ((pocket.getPocketTemplate().getIsCollectorPocket() != null) && pocket.getPocketTemplate().getIsCollectorPocket()) {
			isImmediateUpdateRequired = false;
		}
		else if ((pocket.getPocketTemplate().getIsSuspencePocket() != null) && pocket.getPocketTemplate().getIsSuspencePocket()) {
			isImmediateUpdateRequired = false;
		}else if ((pocket.getPocketTemplate().getIsSystemPocket() != null) && pocket.getPocketTemplate().getIsSystemPocket()) {
			isImmediateUpdateRequired = false;
		}
		else if (pocket.getID().equals(globalSVAPocket.getID()) ) {
			isImmediateUpdateRequired = false;
		}
		else if (pocket.getID().equals(taxPocket.getID()) ) {
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
		mfsLedger.setSctlId(sctlId);
		mfsLedger.setCommodityTransferID(ctID);
		mfsLedger.setPocketID(pocket.getID());
		mfsLedger.setAmount(amount);
		
		if (isSource) {
			mfsLedger.setLedgerType(DAOConstants.DEBIT_LEDGER_TYPE);
		} else {
			mfsLedger.setLedgerType(DAOConstants.CREDIT_LEDGER_TYPE);
		}
		
		if (isSettlement || isImmediateUpdateRequired) {
			mfsLedger.setLedgerStatus(DAOConstants.LEDGER_STATUS_UPDATED);
		} else {
			mfsLedger.setLedgerStatus(DAOConstants.LEDGER_STATUS_DEFERED);
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
		if (suspensePocket.getID().equals(mfsLedger.getPocketID()) ) {
			reversePocket = suspensePocket;
		} 
		else if (globalSVAPocket.getID().equals(mfsLedger.getPocketID()) ) {
			reversePocket = globalSVAPocket;
		}
		else if (globalChargePocket.getID().equals(mfsLedger.getPocketID()) ) {
			reversePocket = globalChargePocket;
		} 
		else {
			reversePocket = coreDataWrapper.getPocketById(mfsLedger.getPocketID(), LockMode.UPGRADE);
			isImmediateUpdateRequired = true;
		}

		boolean isSource = DAOConstants.DEBIT_LEDGER_TYPE.equals(mfsLedger.getLedgerType()) ? false : true;
		
		MFSLedger reverseLedger = generateLedgerEntry(false,mfsLedger.getSctlId(), mfsLedger.getCommodityTransferID(), reversePocket,
				mfsLedger.getAmount(), isSource, isImmediateUpdateRequired);
		
		if (isImmediateUpdateRequired) {
			if (DAOConstants.DEBIT_LEDGER_TYPE.equals(mfsLedger.getLedgerType())) {
				reversePocket.setCurrentBalance(moneyService.add(reversePocket.getCurrentBalance(), mfsLedger.getAmount()));
			}
			else if (DAOConstants.CREDIT_LEDGER_TYPE.equals(mfsLedger.getLedgerType())) {
				reversePocket.setCurrentBalance(moneyService.subtract(reversePocket.getCurrentBalance(), mfsLedger.getAmount()));
			}
			coreDataWrapper.save(reversePocket);
		}
		
		return reverseLedger;
	}
	
}

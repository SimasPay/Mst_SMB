package com.mfino.zenith.interbank;

import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferStatus;


/**
 * @author Sasi
 *
 */
public interface InteBankPendingResolutionService {
	
	public CMInterBankFundsTransferStatus movePendingToComplete();
}

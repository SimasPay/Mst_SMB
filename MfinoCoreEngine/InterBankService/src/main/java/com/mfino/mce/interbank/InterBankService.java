package com.mfino.mce.interbank;

import com.mfino.domain.InterbankCodes;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;

/**
 * 
 * @author Sasi
 *
 */
public interface InterBankService {

	public InterbankTransfer createInterBankTransfer(CMInterBankFundsTransferInquiry ibtInquiry, InterbankCodes interBankCode);
	
	public InterbankCodes getBankCode(String bankCode);
	
	public boolean isIBTRestricted(String bankCode);
	
	public InterbankTransfer getIBT(Long sctlId);
	
	public InterbankTransfer updateIBT(InterbankTransfer ibt);
	
	public Pocket getIBDestinationPocket();
}

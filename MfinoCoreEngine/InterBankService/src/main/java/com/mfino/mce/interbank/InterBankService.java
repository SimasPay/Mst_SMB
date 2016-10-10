package com.mfino.mce.interbank;

import com.mfino.domain.InterbankCodes;
import com.mfino.domain.InterbankTransfers;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;

/**
 * 
 * @author Sasi
 *
 */
public interface InterBankService {

	public InterbankTransfers createInterBankTransfer(CMInterBankFundsTransferInquiry ibtInquiry, InterbankCodes interBankCode);
	
	public InterbankCodes getBankCode(String bankCode);
	
	public boolean isIBTRestricted(String bankCode);
	
	public InterbankTransfers getIBT(Long sctlId);
	
	public InterbankTransfers updateIBT(InterbankTransfers ibt);
	
	public Pocket getIBDestinationPocket();
}

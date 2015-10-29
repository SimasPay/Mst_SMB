package com.mfino.mce.interbank;

import com.mfino.domain.InterBankCode;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;

/**
 * 
 * @author Sasi
 *
 */
public interface InterBankService {

	public InterbankTransfer createInterBankTransfer(CMInterBankFundsTransferInquiry ibtInquiry, InterBankCode interBankCode);
	
	public InterBankCode getBankCode(String bankCode);
	
	public boolean isIBTRestricted(String bankCode);
	
	public InterbankTransfer getIBT(Long sctlId);
	
	public InterbankTransfer updateIBT(InterbankTransfer ibt);
	
	public Pocket getIBDestinationPocket();
}

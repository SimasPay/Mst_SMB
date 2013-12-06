package com.mfino.zenith.interbank;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferStatus;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.mce.backend.BankService;
import com.mfino.zenith.interbank.impl.IBTBackendResponse;

/**
 * @author Sasi
 *
 */
public interface IBTBankService extends BankService{
	
	public CFIXMsg onTransferInquiry(CMInterBankFundsTransferInquiry inquiryToBank);
	
	public CFIXMsg onTransferConfirmation(CMInterBankFundsTransfer confirmationToBank);

	public CFIXMsg onResponseFromInterBankService(CMInterBankMoneyTransferToBank toBank, CMInterBankMoneyTransferFromBank fromBank);
	
//	public CFIXMsg onInterBankFundsTransferStatus(CMInterBankFundsTransferStatus fundsTransferStatus);
	
	public CFIXMsg onGetTxnStatusFromIBTService(CMInterBankFundsTransferStatus fundsTransferStatus, IBTBackendResponse response);
}

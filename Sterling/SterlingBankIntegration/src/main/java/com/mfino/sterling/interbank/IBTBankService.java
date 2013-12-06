package com.mfino.sterling.interbank;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.mce.backend.BankService;

/**
 * @author Amar
 *
 */
public interface IBTBankService extends BankService{
	
	public CFIXMsg onTransferInquiry(CMInterBankFundsTransferInquiry inquiryToBank);
	
	public CFIXMsg onTransferConfirmation(CMInterBankFundsTransfer confirmationToBank);
	
	public CFIXMsg onInterBankTransferInquiryFromBank(CMInterBankTransferInquiryToBank toBank, CMInterBankTransferInquiryFromBank fromBank);
	
	public CFIXMsg onResponseFromInterBankService(CMInterBankMoneyTransferToBank toBank, CMInterBankMoneyTransferFromBank fromBank);
	
}

package com.mfino.mce.bankteller;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashIn;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOut;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOutConfirm;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.mce.backend.BankService;

/**
 * 
 * @author Maruthi
 *
 */
public interface TellerBankService extends BankService{
	
	public CFIXMsg onTellerCashIn(CMBankTellerCashIn requestFix);
	
	public CFIXMsg onTellerTransferInquiryFromBank(CMBankTellerTransferInquiryToBank requestFix,CMBankTellerTransferInquiryFromBank responseFix);
	
	public CFIXMsg onTellerTransferConfirmationFromBank(CMBankTellerMoneyTransferToBank requestFix,	CMBankTellerMoneyTransferFromBank responseFix);
	
	public CFIXMsg onTellerCashInConfirm(CMBankTellerCashInConfirm requestFix);

	public CFIXMsg onTellerCashOut(CMBankTellerCashOut requestFix);

	public CFIXMsg onTellerCashOutConfirmation(CMBankTellerCashOutConfirm requestFix);


}

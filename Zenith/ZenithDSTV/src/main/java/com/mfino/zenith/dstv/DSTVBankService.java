package com.mfino.zenith.dstv;

import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVPayment;
import com.mfino.fix.CmFinoFIX.CMDSTVPaymentInquiry;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryToBank;
import com.mfino.mce.backend.BankService;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalToBank;

/**
 * 
 * @author POCHADRI
 *
 */
public interface DSTVBankService extends BankService{

	public CFIXMsg onDSTVTransferInquiryToBank(CMDSTVPaymentInquiry requestFix);
	
	public CFIXMsg onDSTVTransferInquiryFromBank(CMDSTVTransferInquiryToBank toBank, CMDSTVTransferInquiryFromBank fromBank);
	
	public CFIXMsg onDSTVTransferConfirmationToBank(CMDSTVPayment requestFix);
	
	public CFIXMsg onDSTVTransferConfirmationFromBank(CMDSTVMoneyTransferToBank toBank, CMDSTVMoneyTransferFromBank fromBank);

	public CFIXMsg onDSTVTransferReversalToBank(
			CMBankAccountToBankAccountConfirmation requestFix,
			CMDSTVMoneyTransferReversalToBank responseFix);

	public CFIXMsg onDSTVTransferReversalFromBank(
			CMDSTVMoneyTransferToBank requestFix,
			CMDSTVMoneyTransferReversalFromBank responseFix);
	
	public CFIXMsg onResolveCompleteOfTransfer(PendingCommodityTransfer pendingTransfer);
	
	public CFIXMsg onResolveOfIntegrationServiceStatus(PendingCommodityTransfer pct);
}

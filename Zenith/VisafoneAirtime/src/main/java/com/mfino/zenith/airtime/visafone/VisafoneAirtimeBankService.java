package com.mfino.zenith.airtime.visafone;

import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchase;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchaseInquiry;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryToBank;
import com.mfino.mce.backend.BankService;

/**
 * @author Sasi
 *
 */
public interface VisafoneAirtimeBankService extends BankService{
	
	public CFIXMsg onVisafoneAirtimeTransferInquiryToBank(CMVisafoneAirtimePurchaseInquiry requestFix);
	
	public CFIXMsg onVisafoneAirtimeTransferConfirmationToBank(CMVisafoneAirtimePurchase requestFix);
	
	public CFIXMsg onVisafoneAirtimeTransferInquiryFromBank(CMVisafoneAirtimeTransferInquiryToBank toBank, CMVisafoneAirtimeTransferInquiryFromBank fromBank);
	
	public CFIXMsg onVisafoneAirtimeTransferConfirmationFromBank(CMVisafoneAirtimeMoneyTransferToBank toBank, CMVisafoneAirtimeMoneyTransferFromBank fromBank);

	public CFIXMsg onVisafoneAirtimeTransferReversalToBank(CMVisafoneAirtimePurchase requestFix, CMVisafoneAirtimeMoneyTransferReversalToBank responseFix);

	public CFIXMsg onVisafoneAirtimeTransferReversalFromBank(CMVisafoneAirtimeMoneyTransferReversalToBank requestFix, CMVisafoneAirtimeMoneyTransferReversalFromBank responseFix);
	
	public CFIXMsg onResolveOfIntegrationService(PendingCommodityTransfer pct);
	
	public CFIXMsg onRevertOfIntegrationService(PendingCommodityTransfer pct);
}

package com.mfino.mce.backend;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMFundAllocationConfirm;
import com.mfino.fix.CmFinoFIX.CMFundAllocationInquiry;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalConfirm;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.impl.FundStorageServiceImpl;
import com.mfino.service.impl.FundValidationServiceImpl;

public interface FundService {

	public CFIXMsg onFundAllocationInquiry(CMFundAllocationInquiry fundAllocationInquiry);
	
	public CFIXMsg handleFundAllocationConfirm(CMFundAllocationConfirm fundAllocationConfirm);
	
	public CFIXMsg onFundAllocationConfirm(CMFundAllocationConfirm fundAllocationConfirm);

	public CFIXMsg onFundWithdrawalInquiry(CMFundWithdrawalInquiry fundWithdrawalInquiry);
	public CFIXMsg handleFundWithdrawalConfirm(CMFundWithdrawalConfirm fundWithdrawalConfirm);
	public CFIXMsg onFundWithdrawalConfirm(CMFundWithdrawalConfirm fundWithdrawalConfirm);

	public CFIXMsg handleFundWithdrawalInquiry(CMFundWithdrawalInquiry fundWithdrawalInquiry);
	
	public Integer getBankNotificationCode(BackendResponse returnFix);

	public void resolvePendingFunds(Integer csrAction, BackendResponse returnFix);
		

}

package com.mfino.service;

import com.mfino.domain.ChannelCode;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashIn;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm;

public interface TellerCashinService {

	/**
	 * Validates tellerCashinInquiry details by validating source mdn destination MDN and also 
	 * performs null checks on source pocket and teller pockets
	 * @param tellerCashinInquiry
	 * @param cc
	 * @return
	 */
	public Integer processInquiry(CMBankTellerCashIn tellerCashinInquiry,ChannelCode cc);
	
	/**
	 * Contains validations for subscriber mdn and teller mdn also validates pockets 
	 * and changes the status of sctl to processing if its in Inquiry state
	 * @param tellercashinconfirm
	 * @return status
	 */
	public Integer processConfirmation(CMBankTellerCashInConfirm tellercashinconfirm);

}

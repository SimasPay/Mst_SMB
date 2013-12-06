package com.mfino.fidelity.iso8583;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;

public class GetConstantCodes {


	public static final String  SUCCESS 		= "000";
	
	
	public static String getTransactionType(CFIXMsg msg) {

		if (CMBalanceInquiryToBank.class.equals(msg.getClass())) {
			return "31";
		}
		else if (CMTransferInquiryToBank.class.equals(msg.getClass())) {
			return "41";
		}
		else if (CMMoneyTransferToBank.class.equals(msg.getClass())) {
			return "40";
		}
		else if(CMGetLastTransactionsToBank.class.equals(msg.getClass())) {
			return "38";
		}
		else if(CMMoneyTransferReversalToBank.class.equals(msg.getClass()))
		{
			return "50";
		}
		return null;

	}

	
}

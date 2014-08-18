package com.mfino.bsm.uangku.iso8583;

import com.mfino.bsm.uangku.iso8583.utils.DateTimeFormatter;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;

public class GetConstantCodes {
	// *FindbugsChange*
	// Previous -- public static String SUCCESS = "00";
	public static final String SUCCESS = "00";
	
	public static String getTransactionType(CFIXMsg msg) {
		

		if (CMBalanceInquiryToBank.class.equals(msg.getClass())) {
			return "31";
		}
		else if(CMDSTVMoneyTransferToBank.class.equals(msg.getClass()))
		{
			return "50";
		}
		else if (CMTransferInquiryToBank.class.equals(msg.getClass())) {
			return "30";
		}
		else if (CMMoneyTransferToBank.class.equals(msg.getClass())) {
			return "50";
		}
		else if (CMBankTellerMoneyTransferToBank.class.equals(msg.getClass())) {
			return "50";
		}
		else if(CMGetLastTransactionsToBank.class.equals(msg.getClass())) {
			return "38";
		}
		else if(CMMoneyTransferReversalToBank.class.equals(msg.getClass()))
		{
			return "50";
		}
		else if(CMBankTellerTransferInquiryToBank.class.equals(msg.getClass())){
			return "30";			
		}

		return null;

	}

	public static String getBankAccountType() {

		return null;
	}
	
	/**
	 * Returns the next day time stamp
	 * @return
	 */
	public static String getDE14(Timestamp currentTime)
	{
		//get the next month date
		return DateTimeFormatter.getYYMM(new Timestamp(currentTime.getTime()+30*24*60*60*1000));
	}
	

}

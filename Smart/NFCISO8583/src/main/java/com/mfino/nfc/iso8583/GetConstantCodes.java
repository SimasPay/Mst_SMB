package com.mfino.nfc.iso8583;

import com.mfino.nfc.iso8583.utils.DateTimeFormatter;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.hibernate.Timestamp;

public class GetConstantCodes {
	// *FindbugsChange*
	// Previous -- public static String SUCCESS = "00";
	public static final String SUCCESS = "00";
	
	public static String getTransactionType(CFIXMsg msg) {
		

		if (CMBalanceInquiryToBank.class.equals(msg.getClass())) {
			return "30";
		}
		else if(CMGetLastTransactionsToBank.class.equals(msg.getClass())) {
			return "36";
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

package com.mfino.bsim.iso8583;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;

public class GetConstantCodes {
	// *FindbugsChange*
	// Previous -- public static String SUCCESS = "00";
	public static final String SUCCESS = "00";
	
	public static final String FAILURE = "06";

		//@Martin: change RC 13 to 63
	public static final String INVALID_AMOUNT = "61";
//	public static final String INVALID_AMOUNT = "13";
	
	public static final String REJECT = "99";
	
	public static final String CUSTOMER_ACCOUNT_BLOCKED = "80";
	
	public static final String DUPLICATE_TRANSMISSION = "94";
	
	public static final String SYSTEM_ERROR = "96";
	
	public static final String ATMCode_NewRegistrationSuccess = "9000";
	
	public static final String ATMCode_ChangePinSuccess = "9001";
	
	public static final String ATMCode_ChangeMDNFailure = "9002";
	
	public static final String ATMCode_RejectRequest = "9003";
	
	public static final String ATMCode_InternalFailure = "9004";
	
	public static String getTransactionType(CFIXMsg msg) {
		

		if (CMBalanceInquiryToBank.class.equals(msg.getClass())) {
			return "30";
		}
		else if(CMBillPaymentInquiryToBank.class.equals(msg.getClass()))
		{
			return "38";
		}
		else if(CMBillPaymentToBank.class.equals(msg.getClass()))
		{
			return "50";
		}
		else if(CMBillPaymentReversalToBank.class.equals(msg.getClass()))
		{
			return "50";
		}
		else if (CMTransferInquiryToBank.class.equals(msg.getClass())) {
			return "37";
		}
		else if (CMMoneyTransferToBank.class.equals(msg.getClass())) {
			return "49";
		}
		else if(CMGetLastTransactionsToBank.class.equals(msg.getClass())) {
			return "36";
		}
		else if(CMMoneyTransferReversalToBank.class.equals(msg.getClass()))
		{
			return "49";
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

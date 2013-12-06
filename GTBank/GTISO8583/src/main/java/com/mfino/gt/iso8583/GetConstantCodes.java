package com.mfino.gt.iso8583;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.gt.iso8583.utils.DateTimeFormatter;
import com.mfino.hibernate.Timestamp;

public class GetConstantCodes {

//	public static final String	ZENITH_SAVINGS_ACCOUNT	   = "10";
//	public static final String	ZENITH_CHECKING_ACCOUNT	   = "20";
//	public static final String	ZENITH_UNSPECIFIED_ACCOUNT	= "00";
//	public static final String	ZENITH_ZERO_AMOUNT	       = "000000000000";
//	public static final String	ZENITH_DE9	               = "00000000";
//	public static final String	ZENITH_DE22	               = "000";
//	public static final String	ZENITH_DE23	               = "001";//FIXME no idea?
//	public static final String	ZENITH_DE25	               = "00";
//	public static final String	ZENITH_DE26	               = "12";
//	public static final String	ZENITH_DE28	               = "C00000000";
//	public static final String	ZENITH_DE29	               = "C00000000";
//	public static final String	ZENITH_DE30	               = "C00000000";
//	public static final String	ZENITH_DE31	               = "C00000000";
//	public static final String	ZENITH_DE32	               = "057";
//	public static final String	ZENITH_DE33	               = "360360";
//	public static final String	ZENITH_DE41	               = "40570005";
//	public static final String	ZENITH_DE42	               = "ZIB405700100001";
//	public static final String	ZENITH_DE43	               = "Zenith                     eaZymoneyLANG";
//	public static final String	ZENITH_DE49	               = "566";
//	public static final String	ZENITH_DE56	               = "1510";
//	public static final String	ZENITH_DE98	               = "360360";
//	
//	public static final String	ZENITH_DE100	           = "360360";
//	public static final String	ZENITH_DE123	           = "610500613134021";
	public static final String	GT_DE127_0 	=  "";
	public static final String	GT_DE127_3	= "ZMobileSrc  ZMobileMeSnk470310000820VisaTG      ";
	public static final String  SUCCESS 		= "00";

	public static final String	GT_DE41_BALANCEINQUIRY = "40570009";
	public static final String	GT_DE42_BALANCEINQUIRY = "ZIB405700900001";
	
	public static final String	GT_DE41_MINISTMT	   = "40570009";
	public static final String	GT_DE42_MINISTMT	   = "ZIB405700900001";
	
	public static final String	GT_DE41_SVA_BANK_TRF   = "40570010";
	public static final String	GT_DE42_SVA_BANK_TRF   = "ZIBTRF405700101";
	
	public static final String	GT_DE41_BANK_SVA_TRF   = "40570011";
	public static final String	GT_DE42_BANK_SVA_TRF   = "ZIBTRF405700111";

	/**
	 * DSTV specific values
	 */
	public static final String	GT_DE41_SVA_DSTV_BILLPAY   = "40570012";
	public static final String	GT_DE42_SVA_DSTV_BILLPAY   = "ZIBPYT405700112";

	public static final String	GT_DE41_BANK_DSTV_BILLPAY   = "40570013";
	public static final String	GT_DE42_BANK_DSTV_BILLPAY   = "ZIBPYT405700113";
	
	public static final String	GT_DE98_BANK_DSTV_BILLPAY	   = "ZIBPYT405700112";
	public static final String	GT_DE98_SVA_DSTV_BILLPAY	   = "ZIBPYT405700112";
	
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

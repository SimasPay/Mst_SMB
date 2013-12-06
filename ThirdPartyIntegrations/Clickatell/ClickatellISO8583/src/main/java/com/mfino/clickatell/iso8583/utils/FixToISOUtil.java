package com.mfino.clickatell.iso8583.utils;

import com.mfino.fix.CmFinoFIX;

public class FixToISOUtil {
	
	public static String GetMerchantTypeBySourceApplication(Integer SourceApplication) {
		//FIXME for now return 6011 as sourceApplication not set
		/*if (SourceApplication.equals(CmFinoFIX.SourceApplication_Web))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Web_Channel;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_Phone))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_UTK_Channel;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_SMS))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_SMS_Channel;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_BankChannel))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Automated_Cash_Disbursements;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_WebService))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_H2H_Channel;
		else
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Other;*/
		return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Automated_Cash_Disbursements;

	}

}

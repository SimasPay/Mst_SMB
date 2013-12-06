package com.mfino.iso8583;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPaymentBankRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.DateTimeUtil;

public class TestUtils {

	private static String alphaNumeric="1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	
	public static String constructBitmap(int[] elements) throws Exception {

		int length = 64;
		if (elements[elements.length - 1] > 64)
			length = 128;
		Arrays.sort(elements);
		BitSet bs = null;
		bs = new BitSet(length);
		for (int i : elements)
			bs.set(i - 1);
		StringBuilder sb = new StringBuilder(length / 4);
		String hexChars = "0123456789ABCDEF";
		int num = 0;
		for (int i = 0; i < length; i = i + 1) {
			if (bs.get(i)) {
				if (i % 4 == 0)
					num = num + 8;
				else if ((i - 1) % 4 == 0)
					num = num + 4;
				else if ((i - 2) % 4 == 0)
					num = num + 2;
				else if ((i - 3) % 4 == 0)
					num = num + 1;
			}
			if ((i + 1) % 4 == 0) {
				sb.append(hexChars.charAt(num));
				num = 0;
			}
		}
		return sb.toString();
	}

	public static boolean validateISOBitmap(int[] elements, String isoString) throws Exception {
		Arrays.sort(elements);
		String eleBitmap = constructBitmap(elements);
		String isoBitmap = isoString.substring(4, 4 + eleBitmap.length());
		return eleBitmap.equals(isoBitmap);
	}

	public static void main(String[] args) throws Exception {
		int[] eles = { 2, 3, 7, 11, 12, 13, 15, 18, 24, 27, 32, 33, 35, 37, 42, 43, 47, 48 };
		//		     { 2, 3, 7, 11, 12, 13, 15, 18, 24, 27, 32, 33, 35, 37, 42, 43, 47, 48 };
		String str = constructBitmap(eles);
		System.out.println(str);
	}

	public static String newCardPAN() {
		Random rand = new Random();
		int length = 10 + rand.nextInt(10);
		String pan = String.valueOf(1 + rand.nextInt(9));
		for (int i = 0; i < length - 1; i++)
			pan = pan + String.valueOf(rand.nextInt(10));
		return pan;
	}

	public static String getStaticCardPAN() {
		return "6396873100109882";
	}

	public static BigDecimal newTransactionAmount() {
		Random rand = new Random();
//		int length = 12;
//		int zeroeslen = rand.nextInt(13);
//		String amount = null;
//		for (int i = 0; i < zeroeslen; i++)
//			amount = amount + "0";
//		for (int i = 0; i < length - zeroeslen; i++)
//			amount = amount + String.valueOf(rand.nextInt(10));
//		return amount;
		return new BigDecimal(rand.nextInt(999999999));
	}

	public static long getStaticTransactionAmount() {
		return 12345623l;
	}

	public static int newSTAN() {
		Random rand = new Random();
		return rand.nextInt(999999);
	}

	public static int getStaticSTAN() {
		return 169144;
	}

	public static String newTrack2Data() {
		return newCardPAN();
	}

	public static String getStaticTrack2Data() {
		return "6396873100109882";
	}

	public static String newRRN() {
		return String.valueOf(newTransactionAmount());
	}

	public static String getStaticRRN() {
		return "191679736190";
	}

	public static String newCardAcceptorIdentificationCode() {
		Random rand = new Random();
		int length = 15;
		String code = String.valueOf(1 + rand.nextInt(9));
		for (int i = 0; i < length - 1; i++)
			code = code + String.valueOf(rand.nextInt(10));
		return code;
	}

	public static String getStaticCardAcceptorIdentificationCode() {
		return "628812400656";
	}

	public static String newMDN() {
		Random rand = new Random();
		int length = 15;
		String code = String.valueOf(1 + rand.nextInt(9));
		for (int i = 0; i < length - 1; i++)
			code = code + String.valueOf(rand.nextInt(10));
		return code;
	}

	public static String getStaticMDN() {
		return "628812400656";
	}

	public static String newTrasactionID() {
		Random rand = new Random();
		int length = rand.nextInt(10);
		String code = String.valueOf(1 + rand.nextInt(9));
		for (int i = 0; i < length - 1; i++)
			code = code + String.valueOf(rand.nextInt(10));
		return code;
	}

	public static Long getStaticTransactionID() {
		return 191679736190l;
	}

	public static String newEncryptedPIN() {
		String charArr = "0123456789ABCDEF";
		int length = 16;
		Random rand = new Random();
		String pin = null;
		for (int i = 0; i < length; i++)
			pin = pin + charArr.charAt(rand.nextInt(16));
		return pin;
	}

	public static Integer newProcessingCode() {
		Random rand = new Random();
		if (rand.nextBoolean())
			return Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry1);
		else
			return Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry);
	}

	public static CMBase newBase() {
		CMBase base = new CMBase();

		base.setBillPaymentReferenceID(null);

		//ChannelCodeDAO ccdao = new ChannelCodeDAO();
		//List<ChannelCode> cc = ccdao.getAll();
		//Random rand = new Random();
		//base.setChannelCode(cc.get(rand.nextInt(cc.size())).getChannelCode());
		base.setChannelCode("2002");
		base.setLoginName(null);
		base.setMSPID(1L);
		base.setOperatorName(null);
		base.setParentTransactionID(Long.parseLong(newTrasactionID()));
		base.setPassword("123456");
		base.setPaymentInquiryDetails(null);
		base.setReceiveTime(DateTimeUtil.getLocalTime());
		base.setServiceName(null);
		base.setServiceNumber(null);
		base.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		base.setSourceApplication(CmFinoFIX.SourceApplication_WebAPI);
		base.setSourceIP(null);
		base.setSourceMDN(newMDN());
		base.setTransactionID(Long.parseLong(newTrasactionID()));
		base.setWebClientIP(null);

		return base;
	}

	public static CMBankRequest newBankRequest() {
		CMBase base = newBase();
		CMBankRequest br = new CMBankRequest();
		br.copy(base);
		br.setPin(newBankPIN());
		br.setSourceCardPAN(newCardPAN());
		br.setBankCode(153);
		br.setMerchantPrefixCode(null);
		return br;
	}
	
	public static String newBankPIN() {
		Random rand  = new Random();
		return String.valueOf(100000+rand.nextInt(99999));
	}
	
	public static CMBillPaymentBankRequest newBillPaymentBankRequest() {
		CMBase base = newBase();
		CMBillPaymentBankRequest br = new CMBillPaymentBankRequest();
		br.copy(base);
		br.setPin(newBankPIN());
		br.setSourceCardPAN(newCardPAN());
		br.setBankCode(153);
		br.setMerchantPrefixCode(null);
		br.setBankSystemTraceAuditNumber(newBankSTAN());
		br.setBankRetrievalReferenceNumber(newBankRRN());
		br.setProductIndicatorCode(null);
		br.setAmount(newTransactionAmount());
		br.setBillerName(null);
		br.setCustomerID(newCustomerID());
		br.setSourcePocketID(getStaticTopupAmount());
		
		return br;
	}
	
	public static String newCustomerID() {
		Random rand  = new Random();
		StringBuilder id = new StringBuilder();
		int length = 10+rand.nextInt(90);
		for(int i=0;i<length;i++)
			id.append(alphaNumeric.charAt(rand.nextInt(10)));
		return id.toString();
	}
	
	public static String getStaticCustomerID() {
		return "5498T7EGIHHT43W7TWGJHRJHFEWUF";
	}

	public static String getStaticEncryptedPin() {
		return "B08232216A6143AA";
	}

	public static Long getStaticTopupAmount() {
		return 11232l;
	}

	public static Long getStaticTransferID() {
		return 4235423442l;
	}

	public static Long newTopupAmount() {
		Random rand = new Random();
		return ((Integer) rand.nextInt(999999)).longValue();
	}
	

	public static Long getTransferID() {
		Random rand = new Random();
		return ((Integer) rand.nextInt(999999999)).longValue();
	}

	public static String getStaticBankSTAN() {
		return "34242";
	}

	public static String getStaticBankRRN() {
		return "5646643563";
	}

	public static String newBankSTAN() {
		Random rand = new Random();
		int length = rand.nextInt(7);
		String code = String.valueOf(1 + rand.nextInt(9));
		for (int i = 0; i < length - 1; i++)
			code = code + String.valueOf(rand.nextInt(10));
		return code;
	}

	public static String newBankRRN() {
		Random rand = new Random();
		int length = rand.nextInt(12);
		String code = String.valueOf(1 + rand.nextInt(9));
		for (int i = 0; i < length - 1; i++)
			code = code + String.valueOf(rand.nextInt(10));
		return code;
	}
	
	public static Integer getStaticUICategory() {
		return Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other);
	}
	
	public static Timestamp getStaticTimestamp() {
		Timestamp ts = null;
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(234556353443346332l);
		return new Timestamp(cal.getTime());
	}
}

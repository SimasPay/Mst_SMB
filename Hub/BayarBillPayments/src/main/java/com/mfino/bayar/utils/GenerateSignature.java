package com.mfino.bayar.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import com.mfino.bayar.utils.StringUtilities;
import com.mfino.hibernate.Timestamp;

public class GenerateSignature {
	
	public static String getRequestSign(String caID, String caPasscode, String rqID, Timestamp ts, String billID, Long amount){
		
		String keyA = null;
		String keyB = null;
		String keyC = null;
		Base64 bs64 = new Base64();
		
		keyA = StringUtilities.leftPadWithCharacter(caID, 8, "0") + 
					StringUtilities.leftPadWithCharacter(caPasscode, 8, "0");
		
		keyB= StringUtilities.leftPadWithCharacter(rqID.toString(), 6, "0") + 
						DateTimeFormatter.getMMDDHHMMSS(ts);
		
		keyC = StringUtilities.getLastNChars( billID, 8) + 
				StringUtilities.leftPadWithCharacter(amount.toString(), 8, "0");
		
		byte[] partA = hexToBin(keyA);
		byte[] partB = hexToBin(keyB);
		byte[] partC = hexToBin(keyC);
		for (int i = 0; i < 8; i++)
			partA[i] = (byte) (partA[i] ^ partB[i] ^ partC[i]);

		return bs64.encodeAsString(partA);
		
	}
	
	public static byte[] hexToBin(String hexStr) {
		return hexToBin(hexStr.toCharArray());
	}
	
	public static byte[] hexToBin(char[] hexChars) {
		int[] intArray = new int[hexChars.length / 2];
		int length = hexChars.length;
		if ((length & 0x1) == 0x1) {
			return null;
		}
		int size = 0;
		for (int i = 0; i < hexChars.length; i++) {
			char c = hexChars[i];
			if (!isHexaDigit(c))
				return null;
			int N = 0;
			if (c >= '0' && c <= '9')
				N = c - 0x30;
			else if (c >= 'A' && c <= 'F')
				N = c - 'A' + 10;
			else if (c >= 'a' && c <= 'f')
				N = c - 'a' + 10;
			else
				return null;
			if ((size & 0x1) == 0x1) // two HEX chars become one byte
				intArray[(size & 0xff) >> 1] += (N & 0xff);
			else
				intArray[(size & 0xff) >> 1] = (N & 0xff) << 4;// to avoid nasty
				                                               // surprises and
			// we are concerned with only the first 8 bits
			size++;
			length--;
		}
		return intArrayToByteArray(intArray);
	}
	
	private static boolean isHexaDigit(char c) {
		if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))
			return true;
		return false;
	}
	
	public static byte[] intArrayToByteArray(int[] intArr) {
		byte[] byteArr = new byte[intArr.length];
		for (int i = 0; i < intArr.length; i++) {
			byteArr[i] = (byte) intArr[i];
		}
		return byteArr;
	}
	
	public static String binToHex(byte[] byteArray) {

		char[] hexadecimalChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[byteArray.length * 2];// every byte becomes
		                                                 // two Hex characters
		for (int i = 0; i < hexChars.length / 2; i++) {
			hexChars[i * 2] = hexadecimalChars[(byteArray[i] & 0xff) >> 4];
			hexChars[i * 2 + 1] = hexadecimalChars[(byteArray[i] & 0xff) & 0xf];
		}
		return new String(hexChars);
	}
	
	public static void main(String[] args) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("mmddHHMMSS");
        Date paymentDate = sdf.parse("Mon Nov 11 09:23:57 IST 2013");
		System.out.println(getRequestSign("998765", "123456", "123456", new Timestamp(paymentDate), "6281243218765", Long.valueOf(50000)));
	}

}

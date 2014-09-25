package com.mfino.hsm.thales7.util;

public class PINBlockUtil 

{
	public static String padLeft(String data, int length, char padChar) 
	{
			int remaining = length - data.length();
			String newData = data;
			for (int i = 0; i < remaining; i++)
				newData = padChar + newData;
			return newData;
	}
	
	public static byte[] createPinBlock(String pan, String pin) throws Exception {
		String acct = extractAccountNumberPart(pan);
		String pinData = padLeft("" + pin.length(), 2, '0') + pin;
		pinData = padRight(pinData, 16, 'F');
		byte[] bPin = getBytes(pinData);
		String acctData = padLeft(acct, 16, '0');
		byte[] bAcct = getBytes(acctData);

		byte[] pinblock = new byte[8];
		for (int i = 0; i < 8; i++)
			pinblock[i] = (byte) (bPin[i] ^ bAcct[i]);

		return pinblock;
	}
	
		public static String getString(byte[] data) {
			StringBuffer sb = new StringBuffer();
			for (byte b : data) {
				byte highNibble = (byte) ((b & 0xF0) >> 4);
				byte lowNibble = (byte) (b & 0x0F);
				sb.append(Integer.toHexString(highNibble).toUpperCase());
				sb.append(Integer.toHexString(lowNibble).toUpperCase());
			}
	
			return sb.toString();
		}
	
	public static boolean isHex(String value) {
		byte[] valueBytes = value.getBytes();
		for (byte b : valueBytes)
			if (b < 48 || b > 57 && b < 65 || b > 70 && b < 97 || b > 102)
				return false;
		return true;
	}
	
	public static byte[] getBytes(String value) throws Exception {
				if (isHex(value))
					throw new Exception("Value \"" + value + "\" is not valid HEX");
		
				int length = value.length();
				if (length % 2 != 0) {
					length++;
					value = padLeft(value, length, '0');
				}
		
				int numberChars = value.length();
				byte[] bytes = new byte[numberChars / 2];
		
				for (int i = 0; i < numberChars; i += 2)
					bytes[i / 2] = (byte) Integer.valueOf(value.substring(i, i + 2), 16).intValue();
				return bytes;
			}
	
	public static String padRight(String data, int length, char padChar) {

		int remaining = length - data.length();

		String newData = data;

		for (int i = 0; i < remaining; i++)
			newData = newData + padChar;

		return newData;
	}
	
	public static String extractAccountNumberPart(String accountNumber) {
				String accountNumberPart = null;
				if (accountNumber.length() > 12)
					accountNumberPart = accountNumber.substring(accountNumber.length() - 13, accountNumber.length() - 1);
				else
					accountNumberPart = accountNumber;
				return accountNumberPart;
			}
}

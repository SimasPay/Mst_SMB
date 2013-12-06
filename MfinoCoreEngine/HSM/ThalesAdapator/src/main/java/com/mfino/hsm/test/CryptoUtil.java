package com.mfino.hsm.test;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.mfino.constants.SecurityConstants;

public class CryptoUtil {

	
	private static String buildPinBlock(String pin, String pan) {

		String len = "";
		if (pin.length() < 10)
			len = "" + pin.length();
		else if (pin.length() == 10)
			len = "A";
		else if (pin.length() == 11)
			len = "B";
		else if (pin.length() == 12)
			len = "C";

		String fpad = "FFFFFFFFFFFFFFFF";
		String first = "0" + len + pin;
		first = first + fpad.substring(first.length());

		String spad = "000000000000";
		String second = "0000";
		String tempPan = pan;
		if (pan.length() <= 12)
			tempPan = pan.substring(0, pan.length() - 1);
		else
			tempPan = pan.substring(pan.length() - 1 - 12, pan.length() - 1);
		if (tempPan.length() < 12)
			tempPan = spad.substring(tempPan.length()) + tempPan;
		second = second + tempPan;

		byte[] firstPart = hexToBin(first);
		byte[] secondPart = hexToBin(second);
		for (int i = 0; i < 8; i++)
			firstPart[i] = (byte) (firstPart[i] ^ secondPart[i]);

		first = binToHex(firstPart);

		return first;

	}

	public static byte[] tripleDESEncrypt(byte[] key, byte[] message) throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		
		if (key == null || key.length != 24)
			throw new IllegalArgumentException("Invalid Key");
		
		if (message == null || message.length % 8 != 0)
			throw new IllegalArgumentException("message is null or msg legnth is not a multiple of 8");
		
		DESedeKeySpec desKeySpec = new DESedeKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DESEDE);
		SecretKey skey = keyFactory.generateSecret(desKeySpec);
		Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);
		return encrypt(message, skey, cipher);
	}

	

	public static byte[] decrypt(byte[] input, SecretKey key, Cipher cipher) throws Exception {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		return cipher.doFinal(input);
	}

	public static byte[] encrypt(byte[] input, SecretKey key, Cipher cipher) throws Exception {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		return cipher.doFinal(input);
	}

	
	public static String binToHex(int[] byteArray) {

		char[] hexadecimalChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[byteArray.length * 2];// every byte becomes
		                                                 // two Hex characters
		for (int i = 0; i < hexChars.length / 2; i++) {
			hexChars[i * 2] = hexadecimalChars[(byteArray[i] & 0xff) >> 4];
			hexChars[i * 2 + 1] = hexadecimalChars[(byteArray[i] & 0xff) & 0xf];
		}
		return new String(hexChars);
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

	public static byte[] intArrayToByteArray(int[] intArr) {
		byte[] byteArr = new byte[intArr.length];
		for (int i = 0; i < intArr.length; i++) {
			byteArr[i] = (byte) intArr[i];
		}
		return byteArr;
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

}

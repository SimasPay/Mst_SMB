package com.mfino.bsim.iso8583.utils;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.bsim.iso8583.nm.ISOKeyStore;
import com.mfino.bsim.iso8583.nm.KeyExchangeHandler;
import com.mfino.bsim.iso8583.nm.exceptions.TripleDESDecryptionFailedException;
import com.mfino.bsim.iso8583.nm.exceptions.TripleDESEncryptionFailedException;
import com.mfino.constants.SecurityConstants;

public class CryptoUtil {
	
	public static String buildEncryptedPinBlock(String pin, String pan) throws Exception {
		Logger log = LoggerFactory.getLogger(com.mfino.bsim.iso8583.utils.CryptoUtil.class);
		String pinBlock = buildPinBlock(pin, pan);
		log.info("pin Block is :" + pinBlock);
//		ISOKeyStore ks = ISOKeyStore.getInstance();
//		String workingKey = ks.getWorkingKey();
//		String masterKey=ks.getMasterKey();
//		log.info("Hex MasterKey received from keyStore ----->"+masterKey);
//		log.info("Hex WorkingKey received from keyStore----->"+workingKey);
//		//return tripleDESEncrypt(workingKey, pinBlock);
//		return TripleDES.encrypt(workingKey, pinBlock);
		return null;
	}

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

	public static byte[] tripleDESEncrypt(byte[] key, byte[] message) throws TripleDESEncryptionFailedException {
		Security.addProvider(new BouncyCastleProvider());
		if (key == null || key.length != 24)
			throw new IllegalArgumentException("Invalid Key");
		if (message == null || message.length % 8 != 0)
			throw new IllegalArgumentException("message is null or msg legnth is not a multiple of 8");

		try {

			DESedeKeySpec desKeySpec = new DESedeKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DESEDE);
			SecretKey skey = keyFactory.generateSecret(desKeySpec);

			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);

			return encrypt(message, skey, cipher);
		}
		catch (Exception ex) {
			TripleDESEncryptionFailedException e = new TripleDESEncryptionFailedException(" 3 DES excryption failed");
			e.fillInStackTrace();
			throw e;
		}
	}

	public static byte[] tripleDESEncrypt(SecretKey key, byte[] message) throws TripleDESEncryptionFailedException {

		if (message == null || message.length % 8 != 0)
			throw new IllegalArgumentException("message is null or msg legnth is not a multiple of 8");
		if (key == null)
			throw new IllegalArgumentException("invalid key");
		try {
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);
			return encrypt(message, key, cipher);
		}
		catch (Exception ex) {
			TripleDESEncryptionFailedException e = new TripleDESEncryptionFailedException(" 3 DES excryption failed");
			e.fillInStackTrace();
			throw e;
		}

	}

	public static String tripleDESEncrypt(SecretKey key, String message) throws TripleDESEncryptionFailedException {

		if (message == null || message.length() % 8 != 0)
			throw new IllegalArgumentException("message is null or msg legnth is not a multiple of 8");
		if (key == null)
			throw new IllegalArgumentException("invalid key");

		try {
			byte[] bmsg = hexToBin(message);

			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);
			return binToHex(encrypt(bmsg, key, cipher));
		}
		catch (Exception ex) {
			TripleDESEncryptionFailedException e = new TripleDESEncryptionFailedException(" 3 DES excryption failed");
			e.fillInStackTrace();
			throw e;
		}

	}

	public static byte[] tripleDESDecrypt(SecretKey key, byte[] message) throws TripleDESDecryptionFailedException {

		if (message == null || message.length % 8 != 0)
			throw new IllegalArgumentException("message is null or msg legnth is not a multiple of 8");
		if (key == null)
			throw new IllegalArgumentException("invalid key");

		try {
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);
			return decrypt(message, key, cipher);
		}
		catch (Exception ex) {
			TripleDESDecryptionFailedException e = new TripleDESDecryptionFailedException(" 3 DES excryption failed");
			e.fillInStackTrace();
			throw e;
		}

	}

	public static String tripleDESDecrypt(SecretKey key, String message) throws TripleDESDecryptionFailedException {

		if (message == null || message.length() % 8 != 0)
			throw new IllegalArgumentException("message is null or msg legnth is not a multiple of 8");
		if (key == null)
			throw new IllegalArgumentException("invalid key");

		try {
			byte[] bmsg = hexToBin(message);

			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);
			return binToHex(decrypt(bmsg, key, cipher));
		}
		catch (Exception ex) {
			TripleDESDecryptionFailedException e = new TripleDESDecryptionFailedException(" 3 DES excryption failed");
			e.fillInStackTrace();
			throw e;
		}

	}

	public static byte[] tripleDESDecrypt(byte[] key, byte[] message) throws TripleDESDecryptionFailedException {
		if (message == null || message.length % 8 != 0)
			throw new IllegalArgumentException("message is null or msg legnth is not a multiple of 8");
		if (key == null || key.length != 24)
			throw new IllegalArgumentException("invalid key");

		try {
			Security.addProvider(new BouncyCastleProvider());
			DESedeKeySpec desKeySpec = new DESedeKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DESEDE);
			SecretKey skey = keyFactory.generateSecret(desKeySpec);

			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);
			return decrypt(message, skey, cipher);
		}
		catch (Exception ex) {
			TripleDESDecryptionFailedException e = new TripleDESDecryptionFailedException(" 3 DES excryption failed");
			e.fillInStackTrace();
			throw e;
		}

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

//	public static void main(String[] args) throws Exception {
//
//		String key = "FEDCBA9876543210FEDCBA9876543210FEDCBA9876543210";
//		String pkcv = "0000000000000000";
//		String workingKey = "935A342A1122B33B935A342A1122B33B";
//
//		List<String> list = new ArrayList<String>();
//		list.add("0123456789ABCDEF0123456789ABCDEF");
//		list.add("FEDCBA9876543210FEDCBA9876543210");
//		list.add("0123456789ABCDEF0123456789ABCDEF");
//		ISOKeyStore store = ISOKeyStore.newInstance("C:\\Users\\karthik\\Documents", "mFino260", list);
//
//		String ekcv = new String(binToHex(tripleDESEncrypt(hexToBin(key), hexToBin(pkcv))));
//		System.out.println("kcv-->" + ekcv);
//		
//		String eWorkingKey = new String(binToHex(tripleDESEncrypt(hexToBin(key), hexToBin(workingKey))));
//		System.out.println("Encrypted workingkey-->" + eWorkingKey);
//		
//		String de48 = eWorkingKey+ekcv;
//		System.out.println("DE48-->"+de48);
//		
//		KeyExchangeHandler handler = new KeyExchangeHandler(de48);
//		handler.handle();
//
//		String pinBlock = buildPinBlock("1234", "5973333334459");
//		System.out.println("pin block -->" + pinBlock);
//
//		System.out.println(buildEncryptedPinBlock("1234", "5973333334459"));
//		
//	}

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

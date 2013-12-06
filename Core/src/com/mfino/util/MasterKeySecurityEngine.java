package com.mfino.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MasterKeySecurityEngine {

	private static Logger log = LoggerFactory.getLogger(MasterKeySecurityEngine.class);
	
	public static void main(String[] args) throws  Exception{
		try {
			Cipher c = Cipher.getInstance("DESede");
		} catch (Exception ex) {
			log.error("Cryptography provider not available", ex);
//            Provider sunjce = new com.sun.
		}

		if (args.length < 4) {
			System.out.println("Wrong Usage: [option] [KeyFile] [MasterKeyFile] [EncryptedMasterKeyFile]");
			return;
		}
		File keyFile = new File(args[1]);
		File inputFile = new File(args[2]);
		if (args[0].equals("-e")) {
			//build Key from file
			SecretKey key = readKey(keyFile);

			//build MasterKeyfrom file
			String[] masterKeyParts = readMasterKeyFile(inputFile);
			byte[] byteMasterKey = combineMasterKeyParts(masterKeyParts);

			//encrypt the masterkey
			byte[] byteEncryptedMKey = encrypt(byteMasterKey,key);
			int[] intEncryptedMKey = new int[byteEncryptedMKey.length];
			for(int i=0;i<intEncryptedMKey.length;i++){
				intEncryptedMKey[i] = 0xff&byteEncryptedMKey[i];
			}
			char[] encryptedHexMKey = binToHex(intEncryptedMKey);
//			String encryptedMKey = new String(encryptedHexMKey);
			System.out.println(encryptedHexMKey);

			//decrypt master key
			intEncryptedMKey = hexToBin(encryptedHexMKey);
			for(int i=0;i<intEncryptedMKey.length;i++){
				byteEncryptedMKey[i] = (byte)intEncryptedMKey[i];
			}
			byte[] byteDecryptedMKey = decrypt(byteEncryptedMKey,key);
			int[] intDecryptedMKey = new int[byteDecryptedMKey.length];
			for(int i=0;i<byteDecryptedMKey.length;i++){
				intDecryptedMKey[i] = 0xff&byteDecryptedMKey[i];
			}
			char[] decryptedHexMKey = binToHex(intDecryptedMKey);
			System.out.println(decryptedHexMKey);
		}
	}

	public static byte[] combineMasterKeyParts(String[] keyParts){
		int[] temp = null;
		char[] part1 = keyParts[0].toCharArray();
		char[] part2 = keyParts[1].toCharArray();
		char[] part3 = keyParts[2].toCharArray();
		int[] ptr = hexToBin(part1);
		temp = hexToBin(part2);
		for(int i=0;i<temp.length;i++){
			ptr[i] =  (ptr[i]&0xff)^(temp[i]&0xff);
		}
		temp = hexToBin(part3);
		for(int i=0;i<temp.length;i++){
			ptr[i] =  (ptr[i]&0xff)^(temp[i]&0xff);
		}

		byte[] b =new byte[ptr.length];
		for(int i=0;i<b.length;i++){
			b[i] = (byte)ptr[i];
		}
		return b;
	}

	public static String[] readMasterKeyFile(File f) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(f));
		String[] input = null;
//		input[0] = br.readLine();
		input[0] = "5872345045abedfbaadfed3415872345";
		if(input[0]==null||input[0]=="" || input[0].length()!=32){
			throw  new Exception("Can not read valid text from the first line of the file");
		}
//		input[1] = br.readLine();
		input[1] =  "65837534756575437535abcedaabcdef";
		if(input[1]==null||input[1]==""|| input[1].length()!=32){
			throw  new Exception("Can not read valid text from the second line of the file");
		}
//		input[2] = br.readLine();
		input[2] =  "7654357386836defbadefa9879bd0ada";
		if(input[2]==null||input[2]==""|| input[2].length()!=32){
			throw  new Exception("Can not read valid text from the first line of the file");
		}
		return input;
	}

	public static byte[] encrypt(byte[] input,SecretKey key) throws  Exception{
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE,key);
		return cipher.doFinal(input);
	}

	public static byte[] decrypt(byte[] input,SecretKey key) throws Exception{
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE,key);
		return cipher.doFinal(input);
	}
	public static SecretKey readKey(File f) throws IOException, NoSuchAlgorithmException,
			InvalidKeyException, InvalidKeySpecException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		//String hexKey = br.readLine();
		String hexKey = "abcdef123456";
		hexKey = hexKey.trim();
		/*
		 * Code before using findbug tool
		 * 	if(hexKey==null||hexKey=="")
		 */
		/*
		 * Code after using findbug tool
		 * Using equals(object) function instead of  == or != for performance  gains.
		 * if(StringUtils.isBlank(hexKey))
		 */
		if(StringUtils.isBlank(hexKey)){
			throw new InvalidKeyException("Can not read key from file");
		}
		char[] hexChars = hexKey.toCharArray();
		int[] keyInInts = hexToBin(hexChars);
		byte[] keyInBytes = new byte[keyInInts.length];
		for(int i=0;i<keyInBytes.length;i++){
			keyInBytes[i] = (byte)keyInInts[i];
		}
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyInBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey 	key = keyFactory.generateSecret(desKeySpec);
		return key;
	}

	public static int[] hexToBin(char[] hexChars) {
		int[] byteArray = new int[hexChars.length/2];
		int length = hexChars.length;
		if ((length & 0x1) == 0x1) {
			return null;
		}
		int size=0;
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
			if ((size & 0x1) == 0x1) //two HEX chars become one byte
				byteArray[(size&0xff) >> 1] += (N&0xff);
			else
				byteArray[(size&0xff) >> 1] =(N&0xff) << 4;//to avoid nasty surprises and
													//we are concerned with only the first 8 bits
			size++;
			length--;
		}
//		byte[] b = new byte[byteArray.length];
//		for(int i=0;i<b.length;i++){
//			b[i]  = (byte)byteArray[i];
//		}
		return byteArray;
	}

	private static boolean isHexaDigit(char c) {
		if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))
			return true;
		return false;
	}

	public static char[] binToHex(int[] byteArray){

		char[]	hexadecimalChars	=	{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[byteArray.length*2];//every byte becomes two Hex characters
		for(int i=0;i<hexChars.length/2;i++){
			hexChars[i * 2] = hexadecimalChars[(byteArray[i]&0xff)>>4];
			hexChars[i * 2 +1] = hexadecimalChars[(byteArray[i]&0xff) & 0xf];
		}
		return hexChars;
	}
}
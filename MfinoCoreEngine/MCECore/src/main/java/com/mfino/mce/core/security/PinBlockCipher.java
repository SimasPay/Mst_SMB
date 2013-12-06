/**
 * 
 */
package com.mfino.mce.core.security;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.mfino.crypto.CryptographyService;
/**
 * @author Chaitanya
 *
 */
public class PinBlockCipher {

	private static String TRIPLE_DES_TRANSFORMATION = "DESede/ECB/Nopadding";
	private static String ALGORITHM = "DESede";
	private static String BOUNCY_CASTLE_PROVIDER = "BC";
	private Cipher encrypter;
	private Cipher decrypter;

	public PinBlockCipher(byte[] key) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
	InvalidKeyException {
		Security.addProvider(new BouncyCastleProvider());
		SecretKey keySpec = new SecretKeySpec(key, ALGORITHM);
		encrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);
		encrypter.init(Cipher.ENCRYPT_MODE, keySpec);
		decrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);
		decrypter.init(Cipher.DECRYPT_MODE, keySpec);
	}

	public PinBlockCipher(String key) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
	InvalidKeyException {
		byte[] keyBlock = CryptographyService.hexToBin(key.toCharArray());
		Security.addProvider(new BouncyCastleProvider());
		SecretKey keySpec = new SecretKeySpec(keyBlock, ALGORITHM);
		encrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);
		encrypter.init(Cipher.ENCRYPT_MODE, keySpec);
		decrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);
		decrypter.init(Cipher.DECRYPT_MODE, keySpec);
	}

	public byte[] encode(byte[] input) throws IllegalBlockSizeException, BadPaddingException {
		return encrypter.doFinal(input);
	}

	public byte[] decode(byte[] input) throws IllegalBlockSizeException, BadPaddingException {
		return decrypter.doFinal(input);
	}

	public char[] decode(String input) throws IllegalBlockSizeException, BadPaddingException {
		byte[] pinBlock = CryptographyService.hexToBin(input.toCharArray());
		byte[] res = decode(pinBlock);
		char[] sRes = CryptographyService.binToHex(res);
		return sRes;
	}

	public char[] encode(String input) throws IllegalBlockSizeException, BadPaddingException {
		byte[] pinBlock = CryptographyService.hexToBin(input.toCharArray());
		byte[] res = encode(pinBlock);
		char[] sRes = CryptographyService.binToHex(res);
		return sRes;
	}

	public static void main(String args[]) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		String encrypted = "94914AE91A1A2D5B";
		String key="2CA70EBF1C109201ADE60D9EB3E668AD";

		byte[] pinBlock = CryptographyService.hexToBin(encrypted.toCharArray());
		byte[] keyBlock = CryptographyService.hexToBin(key.toCharArray());
		PinBlockCipher cipher = new PinBlockCipher(keyBlock);
		byte[] res = cipher.decode(pinBlock);
		char[] sRes = CryptographyService.binToHex(res);
		String s = new String(sRes);
		System.out.println("res "+s);

		PinBlockCipher cipher1 = new PinBlockCipher(key);

		char[] sRes1 = cipher1.decode(encrypted);
		s = new String(sRes1);
		System.out.println("res "+s);
		String pin = cipher1.getPIN(FORMAT.ISO1, s);
		System.out.println("pin" + pin);
	}

	public String getPIN(FORMAT format, String pinBlock)
	{
		String pin = pinBlock;
		switch(format)
		{
			case ISO1:
				String pinLengthString = pinBlock.substring(1,2);
				int pinLength = Integer.parseInt(pinLengthString);
				if(pinLength>0 && (2+pinLength)<=pinBlock.length())
				{
					pin = pinBlock.substring(2, 2+pinLength);
				}
		}

		return pin;
	}

	public enum FORMAT{
		ISO1;

	}

}

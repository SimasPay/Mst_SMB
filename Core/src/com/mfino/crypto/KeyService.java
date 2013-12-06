package com.mfino.crypto;

import java.security.Security;

import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.mfino.constants.SecurityConstants;

public class KeyService {

	public static byte[] generateAESKey() throws Exception{
		Security.addProvider(new BouncyCastleProvider());
		KeyGenerator keyGen = KeyGenerator.getInstance(SecurityConstants.AES,SecurityConstants.BOUNCYCASTLE_PROVIDER);
		keyGen.init(192);
		return keyGen.generateKey().getEncoded();
	}

		
	public static void main(String[] args) throws Exception{
		
		byte[] salt = CryptographyService.generateSalt();
		String text = "I am plain text";
		String password="qwerty";
		
		byte[] cipher = CryptographyService.encryptWithPBE(text.getBytes(), password.toCharArray(), salt,20);
		
		byte[] pText = CryptographyService.decryptWithPBE(cipher, password.toCharArray(), salt,20);
		
		
		System.out.println(new String(pText));
		
	}
	
	

}

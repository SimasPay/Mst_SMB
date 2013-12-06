package com.mfino.sterling.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CryptoService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private byte[] key;
	private byte[] initializationVector;
	

	public String encrypt(String plainText) throws EncryptionException {
		try{
			byte[] plaintext = plainText.getBytes();
			Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			SecretKeySpec myKey = new SecretKeySpec(key, "DESede");
			IvParameterSpec ivspec = new IvParameterSpec(initializationVector);
			c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
			byte[] cipherText = c3des.doFinal(plaintext);		
			return Base64.encodeBase64String(cipherText);
		}catch (Exception e) {
			log.error("ERROR in encrypt():",e);
			throw new EncryptionException(e.getMessage());
		}
	}

	public String decrypt(String cipherText) throws DecryptionException {
		try{
			byte[] encData = Base64.decodeBase64(cipherText);
			Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			SecretKeySpec myKey = new SecretKeySpec(key, "DESede");
			IvParameterSpec ivspec = new IvParameterSpec(initializationVector);
			decipher.init(Cipher.DECRYPT_MODE, myKey, ivspec);
			byte[] plainText = decipher.doFinal(encData);
			return new String(plainText);
		}catch (Exception e) {
			log.error("ERROR in decrypt():",e);
			throw new DecryptionException(e.getMessage());
		}
	}

	public byte[] getKey() {
		return key;
	}
	
	public void setKey(byte[] key) {
		this.key = key;
	}

	public byte[] getInitializationVector() {
		return initializationVector;
	}

	public void setInitializationVector(byte[] initializationVector) {
		this.initializationVector = initializationVector;
	}
	
}
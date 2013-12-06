/**
 * 
 */
package com.mfino.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.jasypt.encryption.pbe.StandardPBEBigDecimalEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.salt.FixedStringSaltGenerator;

/**
 * @author Bala Sunku
 *
 */
public class EncryptionUtil {
	public static final String ALGORITHM = "PBEWITHMD5ANDTRIPLEDES";
	public static final String PASSWORD = "mfinozenith";
	public static final String SALT = "YJW9qh52RP8gfRDI";
	
	private static FixedStringSaltGenerator getFixedStringSaltGenerator() {
		FixedStringSaltGenerator salt = new FixedStringSaltGenerator();
		salt.setSalt(SALT);
		return salt;
	}
	
	private static EnvironmentStringPBEConfig getEnvironmentStringPBEConfig()
	{
		EnvironmentStringPBEConfig environmentStringPBEConfig = new EnvironmentStringPBEConfig();
		environmentStringPBEConfig.setPasswordEnvName("ENCRYPTION_KEY");
		//environmentStringPBEConfig.setPassword(PASSWORD);
		environmentStringPBEConfig.setAlgorithm(ALGORITHM);
		environmentStringPBEConfig.setSaltGenerator(getFixedStringSaltGenerator());
		return environmentStringPBEConfig;
	}
	
	
	public static StandardPBEStringEncryptor getDBPasswordEncryptor() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setConfig(getEnvironmentStringPBEConfig());
		return encryptor;
	}
	
	public static StandardPBEStringEncryptor getStringEncryptor() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(ALGORITHM);
		encryptor.setPassword(PASSWORD);
		encryptor.setSaltGenerator(getFixedStringSaltGenerator());
		return encryptor;
	}
	
	public static StandardPBEStringEncryptor getUniqueStringEncryptor() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(ALGORITHM);
		encryptor.setPassword(PASSWORD);
		encryptor.setSaltGenerator(getFixedStringSaltGenerator());
		return encryptor;
	}
	
	public static StandardPBEBigDecimalEncryptor getBigDecimalEncryptor() {
		StandardPBEBigDecimalEncryptor encryptor = new StandardPBEBigDecimalEncryptor();
		encryptor.setAlgorithm(ALGORITHM);
		encryptor.setPassword(PASSWORD);
		return encryptor;
	}
	
	/**
	 * Returns the Encrypted String of the given data.
	 * @param data
	 * @return
	 */
	public static String getEncryptedString(String data) {
		return getStringEncryptor().encrypt(data);
	}
	
	public static String getDecryptedString(String encryptData) {
		return getStringEncryptor().decrypt(encryptData);
	}
	
	private static final String ALGO = "AES";
	private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't','S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };
	
	public static byte[] encrypt(byte[] Data) throws Exception {
	    Key key = generateKey();
	    Cipher c = Cipher.getInstance(ALGO);
	    c.init(Cipher.ENCRYPT_MODE, key);
	    byte[] encVal = c.doFinal(Data);
	    //String encryptedValue = new BASE64Encoder().encode(encVal);
	    return encVal;
	}
	
	public static byte[] decrypt(byte[] encryptedData) throws Exception {
	    Key key = generateKey();
	    Cipher c = Cipher.getInstance(ALGO);
	    c.init(Cipher.DECRYPT_MODE, key);

	    byte[] decValue = c.doFinal(encryptedData);
	    return decValue;
	}

	private static Key generateKey() throws Exception {
	    Key key = new SecretKeySpec(keyValue, ALGO);
	    return key;
	}
	
	public static void main(String[] args) throws Exception {
		byte[] plain = {1,2};
		byte[] encrypted = encrypt(plain);
		
		System.out.println("plain byte array plain[] = "+plain);
		for (int i = 0; i < plain.length; i++) {
			System.out.println(plain[i]);
		}
		
		System.out.println("encrypted byte array encrypted[] = "+encrypted);
		for (int i = 0; i < encrypted.length; i++) {
			System.out.println(encrypted[i]);
		}
		
		byte[] decrypted = decrypt(encrypted);
		
		System.out.println("decrypted byte array decrypted[] = "+decrypted);
		for (int i = 0; i < decrypted.length; i++) {
			System.out.println(decrypted[i]);
		}
		
	}
}

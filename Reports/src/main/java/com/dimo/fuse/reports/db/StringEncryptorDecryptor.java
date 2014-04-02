package com.dimo.fuse.reports.db;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;

/**
 * 
 * @author Amar
 * 
 */
public class StringEncryptorDecryptor {
	private static final String ALGORITHM = "PBEWITHMD5ANDTRIPLEDES";
	private static final String SALT = "YJW9qh52RP8gfRDI";

	private static StandardPBEStringEncryptor stringEncryptor;

	static {
		stringEncryptor = getDBStringEncryptor(DBProperties
				.getDBEncryptionPassword());
	}

	private static FixedStringSaltGenerator getFixedStringSaltGenerator() {
		FixedStringSaltGenerator fixedStringSaltGenerator = new FixedStringSaltGenerator();
		fixedStringSaltGenerator.setSalt(SALT);
		return fixedStringSaltGenerator;
	}

	public static StandardPBEStringEncryptor getDBStringEncryptor(
			String password) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(ALGORITHM);
		encryptor.setPassword(password);
		encryptor.setSaltGenerator(getFixedStringSaltGenerator());
		return encryptor;
	}

	public static String encrypt(String str) {
		return stringEncryptor.encrypt(str);

	}

	public static String decrypt(String str) {
		return stringEncryptor.decrypt(str);
	}

}
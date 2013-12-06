package com.mfino.provision.tools.propertymanager;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;

public class CipherUtil
{

	public static final String ALGORITHM = "PBEWITHMD5ANDTRIPLEDES";
	public static final String SALT = "YJW9qh52RP8gfRDI";
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static String passwordDoubleCheck(String passwordPrompt) throws IOException
	{
		boolean condcheck = false;
		String Pass1 = null;
		while (condcheck == false) {
			Pass1 = readPassword("Enter " + passwordPrompt + ":");
			String Pass2 = readPassword("retype " + passwordPrompt + ":");
			if (Pass1.contentEquals(Pass2)) {
				condcheck = true;
			} else {
				// logger.log(Level.WARNING,
				System.out.println("password did not match Enter again !!");
				// System.out.println("password did not match retype again !!");
			}

		}
		return Pass1;
	}

	public static String readPassword(String passwordRelatedString) throws IOException
	{
		Console c = System.console();
		if (c == null) {
			System.out.print(passwordRelatedString);
			String s = in.readLine();
			return s;
		} else {
			char[] password1 = c.readPassword("[%s]", passwordRelatedString);
			if (password1 != null) {
				String password2 = null;
				password2 = String.valueOf(password1);
				java.util.Arrays.fill(password1, ' ');
				return password2;
			}
		}
		return null;
	}

	public static String encrypt(String str, String str1, boolean useSalt)
	{
		return getStringEncryptor(str, useSalt).encrypt(str1);

	}

	public static String decrypt(String str, String str1, boolean useSalt)
	{
		return getStringEncryptor(str, useSalt).decrypt(str1);

	}

	private static FixedStringSaltGenerator getFixedStringSaltGenerator()
	{
		FixedStringSaltGenerator salt = new FixedStringSaltGenerator();
		salt.setSalt(SALT);
		return salt;
	}

	public static StandardPBEStringEncryptor getStringEncryptor(String str, boolean useSalt)
	{
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(ALGORITHM);
		encryptor.setPassword(str);
		if (useSalt == true) {
			encryptor.setSaltGenerator(getFixedStringSaltGenerator());
		} else {
		}
		return encryptor;
	}

}

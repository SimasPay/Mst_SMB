package com.mfino.provision.tools.propertymanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang.StringUtils;

public class getEncryptionKey {
	//
	// private static Logger log = LoggerFactory
	// .getLogger(CryptographyService.class);

	public static char[] binToHex(int[] byteArray) {

		char[] hexadecimalChars = { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[byteArray.length * 2];// every byte becomes
															// two Hex
															// characters
		for (int i = 0; i < hexChars.length / 2; i++) {
			hexChars[i * 2] = hexadecimalChars[(byteArray[i] & 0xff) >> 4];
			hexChars[i * 2 + 1] = hexadecimalChars[(byteArray[i] & 0xff) & 0xf];
		}
		return hexChars;
	}

	public static char[] binToHex(byte[] byteArray) {

		char[] hexadecimalChars = { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[byteArray.length * 2];// every byte becomes
															// two Hex
															// characters
		for (int i = 0; i < hexChars.length / 2; i++) {
			hexChars[i * 2] = hexadecimalChars[(byteArray[i] & 0xff) >> 4];
			hexChars[i * 2 + 1] = hexadecimalChars[(byteArray[i] & 0xff) & 0xf];
		}
		return hexChars;
	}

	public static byte[] intArrayToByteArray(int[] intArr) {
		byte[] byteArr = new byte[intArr.length];
		for (int i = 0; i < intArr.length; i++) {
			byteArr[i] = (byte) intArr[i];
		}
		return byteArr;
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
			if (!isHexaDigit(c)) {
				return null;
			}
			int N = 0;
			if (c >= '0' && c <= '9') {
				N = c - 0x30;
			} else if (c >= 'A' && c <= 'F') {
				N = c - 'A' + 10;
			} else if (c >= 'a' && c <= 'f') {
				N = c - 'a' + 10;
			} else {
				return null;
			}
			if ((size & 0x1) == 0x1) {
				intArray[(size & 0xff) >> 1] += (N & 0xff);
			} else {
				intArray[(size & 0xff) >> 1] = (N & 0xff) << 4;// to avoid nasty
			}
			// surprises and
			// we are concerned with only the first 8 bits
			size++;
			length--;
		}
		return intArrayToByteArray(intArray);
	}

	private static boolean isHexaDigit(char c) {
		if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F')
				|| (c >= 'a' && c <= 'f')) {
			return true;
		}
		return false;
	}

	public static byte[] encrypt(byte[] input, SecretKey key, Cipher cipher)
			throws Exception {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		return cipher.doFinal(input);
	}

	public static byte[] encrypt_NoIV(byte[] input, SecretKey key, Cipher cipher)
			throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(input);
	}

	public static byte[] decrypt(byte[] input, SecretKey key, Cipher cipher)
			throws Exception {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		return cipher.doFinal(input);
	}

	public static byte[] decrypt_noIV(byte[] input, SecretKey key, Cipher cipher)
			throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(input);
	}

	public static int[] byteArrayToIntArray(byte[] byteArr) {
		int[] intArr = new int[byteArr.length];
		for (int i = 0; i < intArr.length; i++) {
			intArr[i] = byteArr[i] & 0xff;
		}
		return intArr;
	}

	/**
	 * Text is encrypted with DES EDE in CBC mode.The encrypted text is encoded
	 * with binToHex method and retunred as a String. Encoding is done with the
	 * methods binToHex and hexToBin. *
	 * 
	 * @param key
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String tripleDESEncrypt(String key, String message)
			throws Exception {

		if (StringUtils.isBlank(key) || key.length() != 48) {
			throw new Exception("Invalid Key");
		}
		if (StringUtils.isEmpty(message)) {
			return null;
		}
		byte[] keyInBytes = hexToBin(key.toCharArray());
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyInBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(PropertyManagerConstants.DESEDE);
		SecretKey skey = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher
				.getInstance(PropertyManagerConstants.DESEDE_ECB_NOPADDING);
		byte[] byteText = hexToBin(message.toCharArray());

		return new String(binToHex(encrypt_NoIV(byteText, skey, cipher)));
	}

	/**
	 * Text is decrypted with DES EDE in CBC mode.The decrypted text is encoded
	 * with binToHex method and retunred as a String. Encoding is done with the
	 * methods binToHex and hexToBin. * hexEncoded3Key is a 48 length String
	 * where 3 keys are each of 16 length
	 * 
	 * @param hexEncoded3Key
	 * @param hexEncodedTextToDecrypt
	 * @return
	 * @throws Exception
	 */
	public static String tripleDESDecrypt(String hexEncoded3Key,
			String hexEncodedTextToDecrypt) throws Exception {
		if (StringUtils.isBlank(hexEncoded3Key)
				|| hexEncoded3Key.length() != 48) {
			throw new Exception("Invalid Key");
		}
		if (StringUtils.isBlank(hexEncodedTextToDecrypt)) {
			return null;
		}

		byte[] keyInBytes = hexToBin(hexEncoded3Key.toCharArray());
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyInBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(PropertyManagerConstants.DESEDE);
		SecretKey key = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher
				.getInstance(PropertyManagerConstants.DESEDE_ECB_NOPADDING);
		byte[] byteText = hexToBin(hexEncodedTextToDecrypt.toCharArray());
		return new String(binToHex(decrypt_noIV(byteText, key, cipher)));

	}

	public static String getDecryptedString(Logger logger) throws Exception {

		String value = null;
		try {
			BufferedReader in1 = new BufferedReader(new InputStreamReader(
					System.in));

			String checkValueString = "0000000000000000";
			logger.log(Level.SEVERE, "Number of ZMK components :");
			// String k = in1.readLine();
			// int numberOfComponents = Integer.parseInt(k);

			Scanner in = new Scanner(System.in);
			int numberOfComponents = in.nextInt();
			// System.out.println(numberOfComponents);

			String[] hexString = new String[3];

			for (int i = 0; i < numberOfComponents; i++) {
				logger.log(Level.SEVERE, "Enter ZMK component" + (i + 1) + ":");
				hexString[i] = in1.readLine();
				hexString[i] = hexString[i].trim().replaceAll(" ", "");
			}
			byte[] key1 = hexToBin(hexString[0].toCharArray());
			if (numberOfComponents > 1) {
				byte[] key2 = hexToBin(hexString[1].toCharArray());
				for (int i = 0; i < key1.length; i++) {
					key1[i] = (byte) (key1[i] ^ key2[i]);
				}
			}
			if (numberOfComponents == 3) {
				byte[] key3 = hexToBin(hexString[2].toCharArray());
				for (int i = 0; i < key1.length; i++) {
					key1[i] = (byte) (key1[i] ^ key3[i]);
				}
			}
			char[] finalKey = binToHex(key1);
			String key = new String(finalKey);
			key = key.replaceAll(" ", "");

			key = key + key.substring(0, key.length() / 2);
			// System.out.println("Master key is: " + key);

			logger.log(Level.SEVERE, "Enter key check value for Master Key:");
			String keyCheckValue = in1.readLine();
			keyCheckValue = keyCheckValue.trim().replaceAll(" ", "");
			String kccnew = tripleDESEncrypt(key, checkValueString);

			if (!kccnew.startsWith(keyCheckValue)) {
				logger.log(Level.SEVERE,
						"Errror in Master Key. key check value doesn't satisfy.");
				System.exit(0);
			} else {

				logger.log(Level.SEVERE, "Master key is validated !");
			}

			String message;
			logger.log(Level.SEVERE, "Enter Encrypted KWP :");
			message = in1.readLine();
			message = message.replaceAll(" ", "");

			value = tripleDESDecrypt(key, message);

			logger.log(Level.SEVERE, "Enter key check value for decrypted KWP:");
			String keyCheckValue1 = in1.readLine();
			keyCheckValue1 = keyCheckValue1.trim().replaceAll(" ", "");

			String value1 = value + value.substring(0, 16);
			String kccnew1 = tripleDESEncrypt(value1, checkValueString);

			if (!kccnew1.startsWith(keyCheckValue1)) {
				logger.log(Level.SEVERE,
						"Errror in KWP. Key check value doesn't satisfy.");
				System.exit(0);
			} else {
				logger.log(Level.SEVERE, "KWP validated !");
			}
		} catch (NullPointerException ne) {
			System.exit(1);
		} catch (InvocationTargetException ite) {
			System.exit(1);
		}
		System.out.println(value);
		return value;
	}

	public static void keycheckvalue(Logger logger) throws Exception {
		BufferedReader in1 = new BufferedReader(
				new InputStreamReader(System.in));
		String checkValueString = "0000000000000000";

		while (true) {
			logger.log(Level.SEVERE, "Enter kek component");
			String hexString = in1.readLine();
			hexString = hexString.replaceAll(" ", "");
			byte[] key1 = hexToBin(hexString.toCharArray());
			char[] finalKey = binToHex(key1);
			String key = new String(finalKey);
			key = key.replaceAll(" ", "");
			key = key + key.substring(0, key.length() / 2);

			logger.log(Level.SEVERE, "Enter key check value:");
			String keyCheckValue = in1.readLine();
			keyCheckValue = keyCheckValue.replaceAll(" ", "");
			String kccnew = tripleDESEncrypt(key, checkValueString);

			if (!kccnew.contains(keyCheckValue)) {
				logger.log(Level.SEVERE,
						"errror in kek component. key check value doesnt satisfy.");
			} else {
				break;
			}
		}

	}
	//

}

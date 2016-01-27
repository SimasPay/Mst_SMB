package com.mfino.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SecurityConstants;
import com.mfino.util.ConfigurationUtil;

public class CryptographyService {

	private static Logger	log	= LoggerFactory.getLogger(CryptographyService.class);
	
	private static String KEY_FILE_LOCATION = "../webapps/webapi/WEB-INF/";
	
	//private static String KEY_FILE_LOCATION = "C:/Keys/";
	private static String OUTPUT_FILE_LOCATION =".\\keys\\";
	private static String PUBLIC_KEY_EXPONENT = "10001";
	private static Integer RSA_KEY_STRENGTH = 512;

	public static char[] binToHex(int[] byteArray) {

		char[] hexadecimalChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[byteArray.length * 2];// every byte becomes
		                                                 // two Hex characters
		for (int i = 0; i < hexChars.length / 2; i++) {
			hexChars[i * 2] = hexadecimalChars[(byteArray[i] & 0xff) >> 4];
			hexChars[i * 2 + 1] = hexadecimalChars[(byteArray[i] & 0xff) & 0xf];
		}
		return hexChars;
	}

	public static char[] binToHex(byte[] byteArray) {

		char[] hexadecimalChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[byteArray.length * 2];// every byte becomes
		                                                 // two Hex characters
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

	public static byte[] encrypt(byte[] input, SecretKey key, Cipher cipher) throws Exception {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		return cipher.doFinal(input);
	}

	public static byte[] decrypt(byte[] input, SecretKey key, Cipher cipher) throws Exception {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		return cipher.doFinal(input);
	}

	public static int[] byteArrayToIntArray(byte[] byteArr) {
		int[] intArr = new int[byteArr.length];
		for (int i = 0; i < intArr.length; i++) {
			intArr[i] = byteArr[i] & 0xff;
		}
		return intArr;
	}

	public static String buildEncryptedPINBlock16(String PIN, String PAN, SecretKey Key) {
		return "B08232216A6143AA";
	}

	public static String buildEncryptedPINBlock32(String PIN, String PAN, SecretKey Key) {
		return "B08232216A6143AAB08232216A6143AA";
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
	public static String tripleDESEncrypt(String key, String message) throws Exception {

		if (StringUtils.isBlank(key) || key.length() != 48)
			throw new Exception("Invalid Key");
		if (StringUtils.isEmpty(message))
			return null;

		byte[] keyInBytes = key.getBytes(GeneralConstants.UTF_8);
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyInBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DESEDE);
		SecretKey skey = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_PKCS5);
		byte[] byteText = hexToBin(message.toCharArray());

		return new String(binToHex(encrypt(byteText, skey, cipher)));
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
	public static String tripleDESDecrypt(String hexEncoded3Key, String hexEncodedTextToDecrypt) throws Exception {
		if (StringUtils.isBlank(hexEncoded3Key) || hexEncoded3Key.length() != 48)
			throw new Exception("Invalid Key");
		if (StringUtils.isBlank(hexEncodedTextToDecrypt))
			return null;

		byte[] keyInBytes = hexToBin(hexEncoded3Key.toCharArray());
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyInBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DESEDE);
		SecretKey key = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_PKCS5);
		byte[] byteText = hexToBin(hexEncodedTextToDecrypt.toCharArray());
		return new String(binToHex(decrypt(byteText, key, cipher)));

	}

	public static byte[] tripleDESDecrypt(byte[] key, byte[] message) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		if (key == null || key.length != 24)
			throw new Exception("Invalid Key");
		if (message == null)
			return null;

		DESedeKeySpec desKeySpec = new DESedeKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DESEDE);
		SecretKey skey = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_PKCS7, SecurityConstants.BOUNCYCASTLE_PROVIDER);
		return decrypt(message, skey, cipher);

	}

	public static byte[] tripleDESEncrypt(byte[] key, byte[] message) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		if (key == null || key.length != 24)
			throw new Exception("Invalid Key");
		if (message == null)
			return null;

		DESedeKeySpec desKeySpec = new DESedeKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DESEDE);
		SecretKey skey = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_CBC_PKCS7, SecurityConstants.BOUNCYCASTLE_PROVIDER);

		return encrypt(message, skey, cipher);
	}

	public static String desEncrypt(String hexEncodedKey, String message) throws Exception {

		if (StringUtils.isBlank(hexEncodedKey) || hexEncodedKey.length() != 16)
			throw new Exception("Invalid Key");
		if (StringUtils.isEmpty(message))
			return null;

		byte[] keyInBytes = hexToBin(hexEncodedKey.toCharArray());
		DESKeySpec desKeySpec = new DESKeySpec(keyInBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SecurityConstants.DES);
		SecretKey key = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher.getInstance(SecurityConstants.DES_CBC_NOPADDING);
		return null;
	}

	public static String caluclateDBSMAC(String hexEncodedKey, String message) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Mac mac = Mac.getInstance(SecurityConstants.DES, SecurityConstants.BOUNCYCASTLE_PROVIDER);
		Key macKey = new SecretKeySpec(hexToBin(hexEncodedKey.toCharArray()), SecurityConstants.DES);
		mac.init(macKey);
		byte[] output = mac.doFinal(message.getBytes(GeneralConstants.UTF_8));
		return new String(binToHex(output));
	}

	public static byte[] decryptWithPBE(final byte[] cipherText, final char[] password, final byte[] salt, int iterationCount) throws DataLengthException, IllegalStateException, InvalidCipherTextException, InvalidKeyException,
	        NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		Security.addProvider(new BouncyCastleProvider());
		final PBEParametersGenerator keyGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
		keyGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(password), salt, iterationCount);
		final CipherParameters keyParams = keyGenerator.generateDerivedParameters(256, 128);

		final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
		cipher.init(false, keyParams);

		final byte[] processed = new byte[cipher.getOutputSize(cipherText.length)];
		int outputLength = cipher.processBytes(cipherText, 0, cipherText.length, processed, 0);
		outputLength += cipher.doFinal(processed, outputLength);

		final byte[] results = new byte[outputLength];
		System.arraycopy(processed, 0, results, 0, outputLength);
		return results;
	}

	public static byte[] generateSalt() throws NoSuchAlgorithmException {
		byte salt[] = new byte[8];
		SecureRandom saltGen = SecureRandom.getInstance(SecurityConstants.SHA1PRNG);
		saltGen.nextBytes(salt);
		return salt;
	}

	public static byte[] encryptWithPBE(final byte[] plainText, final char[] password, final byte[] salt, int iterationCount) throws DataLengthException, IllegalStateException, InvalidCipherTextException, InvalidKeyException,
	        NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		Security.addProvider(new BouncyCastleProvider());
		final PBEParametersGenerator keyGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
		keyGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(password), salt, iterationCount);
		final CipherParameters keyParams = keyGenerator.generateDerivedParameters(256, 128);

		final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
		cipher.init(true, keyParams);

		final byte[] processed = new byte[cipher.getOutputSize(plainText.length)];
		int outputLength = cipher.processBytes(plainText, 0, plainText.length, processed, 0);
		outputLength += cipher.doFinal(processed, outputLength);

		final byte[] results = new byte[outputLength];
		System.arraycopy(processed, 0, results, 0, outputLength);
		return results;
	}

	public static char[] generateSHA256Hash(String mdn, String pin) {

		//		MessageDigest md;
		//		try {
		//			md = MessageDigest.getInstance("SHA-256");
		//			md.update(pin.getBytes(GeneralConstants.UTF_8));
		//			md.update(mdn.getBytes(GeneralConstants.UTF_8));
		//			byte[] bytes = md.digest();
		//			return binToHex(bytes);
		//		}
		//		catch (Exception ex) {
		//			ex.printStackTrace();
		//		}5ad19a83a866f8315cb57189f84cd9a96216cfd9
		try {

			SHA256Digest digest = new SHA256Digest();
			digest.update(pin.getBytes(GeneralConstants.UTF_8), 0, pin.getBytes(GeneralConstants.UTF_8).length);
			digest.update(mdn.getBytes(GeneralConstants.UTF_8), 0, mdn.getBytes(GeneralConstants.UTF_8).length);

			byte[] res = new byte[digest.getDigestSize()];
			digest.doFinal(res, 0);
			return binToHex(res);
		}
		catch (Exception ex) {
			log.error("generateSHA256Hash failed: " + ex);
		}
		return null;
	}

	public static char[] generateSHAHash(String mdn, String pin) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(pin.getBytes(GeneralConstants.UTF_8));
			md.update(mdn.getBytes(GeneralConstants.UTF_8));
			byte[] bytes = md.digest();
			return binToHex(bytes);
		}
		catch (Exception ex) {
			log.error("generateSHAHash failed: " + ex);
		}
		return null;

	}

	public static void main(String[] args) throws Exception {

		//		Map<String, String> env = System.getenv();
		//        for (String envName : env.keySet()) {
		//            System.out.format("%s=%s%n", envName, env.get(envName));
		//        }

		//		System.out.println(System.getenv("ENABLEWEBAPISECURITY"));

		//		Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
		//		System.out.println(caluclateDBSMAC("0123456789123456", "asdfasf3eweadse"));
		//		System.out.println(desEncrypt("1234567890123456", "twesfdg3w4trsgcfwaer4we"));
		//		System.out.println(generateSHAHash("987654321", "123456"));//2b6e40ec1702f914477724f614dff2824b059554

		//		String message = "123456781234567812345678123456781";
		//		byte[] key = KeyService.generateAESKey();
		//		//		byte[] key = "123456781234567812345678".getBytes(GeneralConstants.UTF_8);
		//		byte[] cipherText = encryptWithAES(key, message.getBytes(GeneralConstants.UTF_8));
		//		byte[] out = decryptWithAES(key, cipherText);
		//
		//		System.out.println("DESEDE result=" + new String(out, GeneralConstants.UTF_8));
		//		char[] pbeKey = "asffafafasdfdf".toCharArray();
		//		byte[] salt = CryptographyService.generateSalt();

		//		sourceMDN=987654321
		//				  salt=6C5544797A91115D
		//				  authenticationString=9507F8EA5D9EFE1710060A8392636DBC
		//		byte[] salt = hexToBin("6C5544797A91115D".toCharArray());
		//		byte[] authString = hexToBin("9507F8EA5D9EFE1710060A8392636DBC".toCharArray());
		//
		//		System.out.println(MfinoUtil.calculateDigestPin("234123", "1234"));
		//		char[] password = generateSHA256Hash("234123", "1234");
		//		System.out.println(new String(password));
		//		password = generateSHAHash("234123", "1234");
		//		System.out.println(new String(password));
		//		
		//		byte[] salt = hexToBin("6C5544797A91115D".toCharArray());
		//		byte[] cipherText = hexToBin("DD2ACF95853D753DB35F9CDFA29AF22C".toCharArray());
		//		char[] password = "96C48BEB572198CD2D01E5CFD0DC305840DF3497325CB5F19FD3674383886EB5".toCharArray();
		//		byte[] text= decryptWithPBE(cipherText, password, salt, 20);
		//		String encedAESkey = "0C062BF39A4AF4F42B645F37BAE6CBF78D79A291F3A8B687EFC37BBA0DAFA20E";
		//		String authString = "CF1E76A27A9D50AEF46EC8736FBA333E";
		//		String salt = "5A498418C7FBF834";
		//		byte[] e = hexToBin(encedAESkey.toCharArray());
		//		byte[] a = hexToBin(authString.toCharArray());
		//		byte[] s = hexToBin(salt.toCharArray());
		//		
		//		
		//		String epin = "45E9DB50BBA27E9C067C50534BF38DF2";
		//		String etid = "C0965EC70BF86350090D2348FBF892DF0E8301342C17438D94CCA9EA501AE379E1998739B0BA4CB86D28B9A692A321B150D358A2B4828262414072FEE6102FB668D078C92F35CDC90FA8756D2CF95093D7355EF022D403B9F0071008A4C841455B01E062DB9A1660CFA770D707F18A6BFE39F4CD184D86579A4A54AC5586CA4A98FB9C5089813DB402AED0529CF066273297F9591793368B6BBD7DFC7EBA9CF229739E58C40CF54E2C2F9EC171DAD8B4";
		////		String msg = "abcd:/ -,.8767/ /";
		//		String SessionKey = "E50F0CEDB78918EFFFCEB84B900790FF948FDEF065F87FE6";
		//		String msg = "Sorry, transaction on 06/11/11 01:37 failed. Your number is not registered in M-Commerce Service.Please do M-Commerce  Activation";// to get this service. Info, call 837. REF: ;
		//		
		////		Base6
		//		
//				byte[] keyBytes = hexToBin("6A31E07EB5320E0B0383B07346583EED".toCharArray());
//				byte[] sessionBytes = hexToBin("A38A8134D5603761F010D2BDD5E47063049370CF0C909DA0".toCharArray());
//		
//				byte[] decMsg = decryptWithAES(new KeyParameter(sessionBytes), keyBytes);
//				
//				System.out.println(new String(decMsg,GeneralConstants.UTF_8));
//
//				byte[] spin = decryptWithAES(new KeyParameter( skBytes), epinBytes);
//				byte[] txin =decryptWithAES(new KeyParameter( skBytes)	, etidBytes);
//
//		//		System.out.println(new String(txin));
//
//			System.out.println(new String( generateSHA256Hash("2349989315635", "1234")));
//		
		//		System.out.println();
		//		
		//		
		//		String password = "B572198CD2D";
		//		
		//		
		//		decryptWithPBE(e, password.toCharArray(), s,20);

		//"http://localhost:8080/webapi/dynamic?channelID=7&txnName=Login&service=Account&sourceMDN=234123&salt=6C5544797A91115D&authenticationString=DD2ACF95853D753DB35F9CDFA29AF22C&sourcePIN=abcd"

		//		byte[] encZeroes = encryptWithPBE(GeneralConstants.ZEROES_STRING.getBytes("UTF-8"), password, salt, 20);
		//		
		//		String str = new String(binToHex(encZeroes));
		//		System.out.println(str);

		//		char[] pbeKey = "2B6E40EC1702F914477724F614DFF2824B059554".toCharArray();
		//		String message = GeneralConstants.ZEROES_STRING;
		//		byte[] pbeEncrypted = encryptWithPBE(message.getBytes(GeneralConstants.UTF_8), pbeKey, salt, 20);
		//		for (int i = 0; i < pbeEncrypted.length; i++)
		//			System.out.print(pbeEncrypted[i] + " ");
		//		System.out.println();
		//		byte[] shit = { 116, 99, -110, 4, 77, -107, 18, 18, 45, 23, -73, 10, 46, 7, -54, -13 };
		//		//[-82, -74, -94, 107, -77, -40, 16, -2, 113, 64, -121, 10, 40, -122, 123, 21]
		//		byte[] pbeDecrypted = decryptWithPBE(pbeEncrypted, pbeKey, salt, 20);
		//		System.out.println("PBE Result= " + new String(pbeDecrypted, GeneralConstants.UTF_8));
		//		//		byte[] abcd1 = org.bouncycastle.util.encoders.Hex.encode(message.getBytes(GeneralConstants.UTF_8));
		//		//		System.out.println(new String(abcd1, GeneralConstants.UTF_8));
		//		//		byte[] abcd2 = org.bouncycastle.util.encoders.Hex.decode(abcd1);
		//		//		System.out.println(new String(abcd2, GeneralConstants.UTF_8));
		//		String b = "6B5544797A91115D";
		//		salt = hexToBin(b.toCharArray());
		//		int a = 10;
		
//		if(args.length==0){
//			System.out.println("Please enter the needed arguments.For commands enter argument: help");
//			System.exit(0);
//		}
//		else if("help".equals(args[0])){
//			System.out.println("Usage Commands:\n"+
//					"For generating RSA keys: give 1st argument as 'genRSAKeys',2nd argument is the <keyFilesLocation>(mandatory),3rd argument is RSA key Strenth(optional: give in powers of 2),4th argument is Public key exponent(optional: 5 digit number with only 1's and 0's)\n"+
//					"For encrypting a text: give 1st argumnet as 'encWithPublicKey',2nd argument is the text to be encrypted(mandatory),3rd argument is the <keyFilesLocation>(mandatory)\n"+
//					"For decrypting an encrypted text: give 1st argumnet as 'decWithPrivateKey',2nd argument is the text to be decrypted(mandatory),3rd argument is the <keyFilesLocation>(mandatory)\n"+
//					"<keyFilesLocation> should end with a / or \\");
//		}
//		else if("genRSAKeys".equals(args[0])){
//			if(args.length<2 || "dummy".equals(args[1]))
//				System.out.println("No ouput file location is entered.Keys will be generated in default location: "+OUTPUT_FILE_LOCATION);
//			else
//				OUTPUT_FILE_LOCATION = args[1]+"/keys/";
//			if(args.length<3 || "dummy".equals(args[2]))
//				System.out.println("RSA key strength not given.The default value is: "+RSA_KEY_STRENGTH);
//			else{
//				try{
//					RSA_KEY_STRENGTH = Integer.parseInt(args[3]);
//				}
//				catch(Exception e){
//					System.out.println("Please enter a valid integer key strength");
//					System.exit(0);
//				}
//			}
//			if(args.length<4 || "dummy".equals(args[3]))
//				System.out.println("Public key exponent not given.Default value: "+PUBLIC_KEY_EXPONENT+" will be used");
//			else{
//				PUBLIC_KEY_EXPONENT = args[2];
//			}			
//			generateRSAKeys();
//		}
//		else if("encWithPublicKey".equals(args[0])){
//			if(args.length<2){
//				System.out.println("Please enter a second argument for data to be encrypted");
//				System.exit(0);
//			}
//			if(args.length<3){
//				System.out.println("Please enter the key files location.");
//				System.exit(0);
//			}
//			else{
//				KEY_FILE_LOCATION = args[2];
//			}
//			System.out.println("clear text entered: "+args[1]);
//			System.out.println("encrypted Text: "+encryptWithPublicKey(args[1]));				
//		}
//		else if("decWithPrivateKey".equals(args[0])){
//			if(args.length<2){
//				System.out.println("Please enter a second argument for data to be decrypted");
//				System.exit(0);
//			}
//			if(args.length<3){
//				System.out.println("Please enter the key files location.");
//				System.exit(0);
//			}
//			else{
//				KEY_FILE_LOCATION = args[2];
//			}
//			System.out.println("encrypted text entered: "+args[1]);
//			System.out.println("decrypted Text: "+decryptWithPrivateKey(args[1]));				
//		}
//		else{
//			System.out.println("Invalid command entered");
//			System.exit(0);
//		}		
		
		System.out.println(generateSHA256Hash("Super", "123"));

	}

	public static String encryptWithOTP(String str) {
		if (str == null) {
			return null;
		}
		try {
			byte[] strBytes = str.getBytes(GeneralConstants.UTF_8);
			char[] pwd = CryptographyService.generateSHA256Hash("2349989315635", "5876");
			byte[] salt = { 0, 0, 0, 0, 0, 0, 0, 0 };
			String authString =new String(CryptographyService.binToHex(CryptographyService.encryptWithPBE(strBytes, pwd, salt, 20)));
			return authString;
		}
		catch (Exception ex) {
		}
		return null;
	}

	public static String decryptWIthOTP(String str) {
		String originalOTP = new String(CryptographyService.generateSHA256Hash("2349989315635", "5876"));
		System.out.println("original OTP: "+originalOTP);
		String authStr = encryptWithOTP("000");
		System.out.println(str+" :encrypted String= "+authStr);
		byte[] authBytes = CryptographyService.hexToBin(authStr.toCharArray());
		byte[] salt = { 0, 0, 0, 0, 0, 0, 0, 0 };
		String newpin = null;
		try {
			byte[] decStr = CryptographyService.decryptWithPBE(authBytes, originalOTP.toCharArray(), salt, 20);
			String zstr = new String(decStr, GeneralConstants.UTF_8);
			if (!GeneralConstants.ZEROES_STRING.equals(zstr))
				return "false";

			String encedNewPin = encryptWithOTP("1234");
			System.out.println( "encrypted 1234: "+encedNewPin);
			byte[] hexNewPin = CryptographyService.hexToBin(encedNewPin.toCharArray());
			byte[] decNewPin = CryptographyService.decryptWithPBE(hexNewPin, originalOTP.toCharArray(), salt, 20);
			newpin = new String(decNewPin, GeneralConstants.UTF_8);
			
			return newpin;
		}
		catch (Exception ex) {
		}
		return "false";

	}

	public static byte[] encryptWithAES(byte[] key, byte[] plainText) throws Exception {
		return cipheringWithAES(key, plainText, true);
	}

	private static byte[] cipheringWithAES(byte[] key, byte[] plainText, boolean isEncryption) throws Exception {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
		KeyParameter kp = new KeyParameter(key);
		cipher.init(isEncryption, kp);
		byte[] temp = new byte[cipher.getOutputSize(plainText.length)];
		int noOfOutBytes = -1;
		noOfOutBytes = cipher.processBytes(plainText, 0, plainText.length, temp, 0);
		noOfOutBytes = noOfOutBytes + cipher.doFinal(temp, noOfOutBytes);
		byte[] out = new byte[noOfOutBytes];
		for (int i = 0; i < noOfOutBytes; i++)
			out[i] = temp[i];
		return out;
	}

	public static byte[] decryptWithAES(byte[] key, byte[] plainText) throws Exception {
		return cipheringWithAES(key, plainText, false);
	}

	private static byte[] cipheringWithAES(KeyParameter keyParameter, byte[] plainText, boolean isEncryption) throws Exception {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
		cipher.init(isEncryption, keyParameter);
		byte[] temp = new byte[cipher.getOutputSize(plainText.length)];
		int noOfOutBytes = -1;
		noOfOutBytes = cipher.processBytes(plainText, 0, plainText.length, temp, 0);
		noOfOutBytes = noOfOutBytes + cipher.doFinal(temp, noOfOutBytes);
		byte[] out = new byte[noOfOutBytes];
		for (int i = 0; i < noOfOutBytes; i++) {
			out[i] = temp[i];
		}
		return out;
	}

	public static byte[] decryptWithAES(KeyParameter keyParameter, byte[] plainText) throws Exception {
		return cipheringWithAES(keyParameter, plainText, false);
	}

	public static byte[] encryptWithAES(KeyParameter keyParameter, byte[] plainText) throws Exception {
		return cipheringWithAES(keyParameter, plainText, true);
	}
	
	
	/**
	 * Checks if RSA encryption is enabled.If enabled
	 * decrypts the encryptedText to clearText with the private Key file created
	 * else returns the input text as it is.
	 * @param encryptedText is the encrypt
	 * @return the decrypted clearText
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
    public static String decryptWithPrivateKey(String encryptedText) throws FileNotFoundException, IOException, Exception
    {
    	if(ConfigurationUtil.getuseRSA()){
	    	byte[] clearTextBytes = hexToBin(encryptedText.toCharArray());
	        RSAPrivateCrtKeyParameters privateKey = getPrivateKey();
	        byte[] result = decrypt(clearTextBytes, privateKey);
	        return new String(result);
    	}
    	return encryptedText;
    }
    
    /**
     * Generates the private key files from the .dat files
     * @return RSAPrivateCrtKeyParameters
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static RSAPrivateCrtKeyParameters getPrivateKey() throws FileNotFoundException, IOException
    {
    	log.info("Getting the private key related dat files from: "+KEY_FILE_LOCATION);
        BigInteger RSAmod = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSAmod.dat"));
        BigInteger RSAprivExp = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSAprivExp.dat"));
        BigInteger RSApubExp = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSApubExp.dat"));
        BigInteger RSAdp = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSAdp.dat"));
        BigInteger RSAdq = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSAdq.dat"));
        BigInteger RSAp = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSAp.dat"));
        BigInteger RSAq = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSAq.dat"));
        BigInteger RSAqInv = new BigInteger(readFromFile(KEY_FILE_LOCATION+"RSAqInv.dat"));
        RSAPrivateCrtKeyParameters RSAprivKey = new RSAPrivateCrtKeyParameters(

                               RSAmod, RSApubExp, RSAprivExp, RSAp,

                               RSAq, RSAdp, RSAdq, RSAqInv);
        return RSAprivKey;

    }
    
    /**
     * Reads a files and returns the data in byte array format
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static byte[] readFromFile(String file) throws FileNotFoundException, IOException 
    {
    	log.info("Reading the file: "+file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream f = new FileInputStream(file);

         byte [] b = new byte[1];

         while ( f.read(b) != -1 ) {

         baos.write(b);

       }

       byte[] result = baos.toByteArray();

       baos.close();
       f.close();

       return result;
    }
    
    /**
     * Decrypts the byte array with the private key
     * @param toDecrypt
     * @param RSAprivKey
     * @return decrypted byte array
     * @throws Exception
     */
    public static byte [] decrypt (byte [] toDecrypt, RSAPrivateCrtKeyParameters RSAprivKey ) throws Exception 
    {
         if (RSAprivKey == null)

           throw new Exception("Generate RSA keys first!");



         AsymmetricBlockCipher eng = new RSAEngine();

         eng = new PKCS1Encoding(eng);

         eng.init(false, RSAprivKey);

         return eng.processBlock(toDecrypt, 0, toDecrypt.length);

    }
    
    /**
     * Generated the public key from the modulus and exponent Strings.
     * @return
     * @throws Exception
     */
    private static RSAKeyParameters getPubKey() throws Exception {
    	String[] rsaPubKey = getPubKeyStrings();
        
//        CryptographyService.hexToBin(str.toCharArray());
        
        BigInteger RSAmod = new BigInteger(CryptographyService.hexToBin(rsaPubKey[0].toCharArray()));
        BigInteger RSApubExp = new BigInteger(CryptographyService.hexToBin(rsaPubKey[1].toCharArray()));
        
        RSAKeyParameters   RSApubKey = new RSAKeyParameters(false, RSAmod, RSApubExp);
        return RSApubKey;
    }
    
    /**
     * Reads the public key modulus and exponent files and returns them in String data
     * @return String[2] where 1st value is modulus and 2nd one is the exponent
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String[] getPubKeyStrings() throws FileNotFoundException, IOException {
        byte [] rsamod_array = readFromFile(KEY_FILE_LOCATION+"RSAmod.dat");
        String rsamod = new String(CryptographyService.binToHex(rsamod_array));
        
        byte [] rsapubexp_array = readFromFile(KEY_FILE_LOCATION+"RSApubExp.dat");
        String rsapubexp = new String(CryptographyService.binToHex(rsapubexp_array));

        String[] rsaParameterStrings = new String[2];
        rsaParameterStrings[0] = rsamod;
        rsaParameterStrings[1] = rsapubexp;
        
        return rsaParameterStrings;
    }
    
    /**
     * Encrypts the clearText with the public key file generated
     * @param clearText
     * @return the encryptedText
     * @throws Exception
     */
    private static String encryptWithPublicKey(String clearText) throws Exception{
		RSAKeyParameters rsaKeyParameters = getPubKey();	
        AsymmetricBlockCipher eng = new RSAEngine();
        eng = new PKCS1Encoding(eng);
        eng.init(true, rsaKeyParameters);        
        byte[] toEncrypt = clearText.getBytes();
        byte[] cipherText = eng.processBlock(toEncrypt, 0, toEncrypt.length);
		String encryptedText = new String(binToHex(cipherText));
		return encryptedText;
    }
    
    /**
     * Generates the RSA keys related .dat files in the OUTPUT_FILE_LOCATION
     * @throws Exception
     */
    private static void generateRSAKeys() throws Exception{
        System.out.println("starting key generation");
    	AsymmetricCipherKeyPair keyPair = generateRSAKeyPair();
    	RSAPrivateCrtKeyParameters RSAprivKey = (RSAPrivateCrtKeyParameters) keyPair.getPrivate();
    	RSAKeyParameters RSApubKey = (RSAKeyParameters) keyPair.getPublic();
    	serialize(RSAprivKey, RSApubKey);    	
    }
    
    /**
     * Genrates the RSA KeyPair and returns them
     * @return AsymmetricCipherKeyPair
     * @throws Exception
     */
    private static AsymmetricCipherKeyPair generateRSAKeyPair() throws Exception {
        SecureRandom sr = new SecureRandom();
        BigInteger pubExp = new BigInteger(PUBLIC_KEY_EXPONENT, 16);
        RSAKeyGenerationParameters RSAKeyGenPara = new RSAKeyGenerationParameters(pubExp, sr, RSA_KEY_STRENGTH, 80);
        RSAKeyPairGenerator RSAKeyPairGen = new RSAKeyPairGenerator();
        RSAKeyPairGen.init(RSAKeyGenPara);
        AsymmetricCipherKeyPair keyPair = RSAKeyPairGen.generateKeyPair();
        return keyPair;
    }
   
    /**
     * Writes down the genrated RSA keys into .dat files needed for recreation
     * @param RSAprivKey
     * @param RSApubKey
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception
     */
   private static void serialize(RSAPrivateCrtKeyParameters RSAprivKey, RSAKeyParameters RSApubKey) throws FileNotFoundException, IOException, Exception
   {
	   FileOutputStream out;
	   BigInteger pubExp;
       BigInteger mod = RSAprivKey.getModulus();
       File f = new File(OUTPUT_FILE_LOCATION);
       if(!f.exists()) f.mkdirs();
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSAmod.dat");
        out.write(mod.toByteArray());
        out.flush(); out.close();

        BigInteger privExp = RSAprivKey.getExponent();
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSAprivExp.dat");
        out.write(privExp.toByteArray());
        out.flush(); out.close();

        pubExp = RSAprivKey.getPublicExponent();
        if ( !pubExp.equals(new BigInteger(PUBLIC_KEY_EXPONENT, 16)) )
          throw new Exception("wrong public exponent");
        
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSApubExp.dat");
        out.write(pubExp.toByteArray());
        out.flush(); out.close();

        BigInteger dp = RSAprivKey.getDP();
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSAdp.dat");
        out.write(dp.toByteArray());
        out.flush(); out.close();

        BigInteger dq = RSAprivKey.getDQ();
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSAdq.dat");
        out.write(dq.toByteArray());
        out.flush(); out.close();

        BigInteger p = RSAprivKey.getP();
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSAp.dat");
        out.write(p.toByteArray());
        out.flush(); out.close();

        BigInteger q = RSAprivKey.getQ();
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSAq.dat");
        out.write(q.toByteArray());
        out.flush(); out.close();

        BigInteger qInv = RSAprivKey.getQInv();
        out = new FileOutputStream(OUTPUT_FILE_LOCATION + "RSAqInv.dat");
        out.write(qInv.toByteArray());
        out.flush(); out.close();
        
        System.out.println("Keys generated in location: "+OUTPUT_FILE_LOCATION);

   }

}

/**
 * 
 */
package com.mfino.stk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.Security;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.SecurityConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.stk.vo.STKRequest;

/**
 * Encryption and Decryption class for visafone request.
 * 
 * @author Bala Sunku
 * 
 */
public class VisafoneEncryptionDecryption {

	private static Logger	         log	      = LoggerFactory.getLogger(VisafoneEncryptionDecryption.class);
	// *FindbugsChange*
	// Previous -- public static String	rootKey	  = "70E9BEA697723DF83605EBBCB7C2C7C4";
	//			   public static String	factorKey	= "00000000";
	public static final String	rootKey	  = "70E9BEA697723DF83605EBBCB7C2C7C4";
	public static final String	factorKey	= "00000000";

	public static byte[] to8Bit(byte[] ba) {
		int len = ba.length;
		byte[] out = new byte[(len * 7) / 8];
		int i = 0;
		int inputOffset = 0;
		int outputOffset = 0;
		while (inputOffset < len - 1) {
			out[outputOffset++] = (byte) (((ba[inputOffset] & 0xff) >>> i) | (((ba[++inputOffset] & 0xff) << (7 - i))));
			if (++i == 7) {
				i = 0;
				++inputOffset;
			}
		}
		return out;
	}

	public static byte[] to7Bit(byte[] ba) {
		String str = "";
		for (int i = 0; i < ba.length; i++) {
			String str1 = Integer.toBinaryString(ba[i]);
			if (str1.length() > 8)
				str = str + str1.substring(str1.length() - 8);
			else
				str = str + str1;
		}

		byte[] sevenBitBytes = null;
		if (str.length() % 7 == 0)
			sevenBitBytes = new byte[str.length() / 7];
		else
			sevenBitBytes = new byte[str.length() / 7 + 1];

		for (int i = 0; i / 7 < sevenBitBytes.length - 1; i = i + 7) {
			sevenBitBytes[i / 7] = Byte.parseByte(str.substring(i, i + 7), 2);
		}
		sevenBitBytes[sevenBitBytes.length - 1] = Byte.parseByte(str.substring(str.length() - 1 - (str.length() % 7)), 2);
		return sevenBitBytes;
	}

	public static byte[] getBytes(String str) {
		byte[] b = new byte[str.length()];
		for (int i = 0; i < b.length; i++)
			b[i] = (byte) (str.charAt(i) & 0xff);
		return b;
	}
	
	public static void main(String[] args) throws Exception {

		
		File file = new File("C:\\Users\\karthik\\Desktop\\x");
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		
		VisafoneEncryptionDecryption ed = new VisafoneEncryptionDecryption();
		STKRequest request = new STKRequest();
		request.setRequestMsg("aaa");

		String str="";
		int j=0;
		while((str=br.readLine())!=null){
			str = str.trim();
			
			if(StringUtils.isBlank(str))
				continue;
			
//			System.out.println(str);
			String[] s = str.split(",");

			int[] ir = new int[s.length];
			for(int i=0;i<ir.length;i++){
				ir[i] = Integer.parseInt(s[i]);
			}
			request.setRequestAsInts(ir);
			ed.process(request);
			
			System.out.println(++j +" ---->"+request.getSecuredDecryptedRequestMsg());
		}

		
		
//		request.setRequestBytes(CryptographyService.hexToBin("3672c26798c32a2b0c9139bbb010ae2e076a58595d546b472fde5acf".toCharArray()));
//		ed.process(request);
//		System.out.println(request.getDecryptedRequestMsg());
//
//		request.setRequestBytes(CryptographyService.hexToBin("81b072d0209154d6dae34b0aee7e1da800e57f3f7ebf907e34e19f49".toCharArray()));
//		ed.process(request);
//		System.out.println(request.getDecryptedRequestMsg());
//
//		request.setRequestBytes(CryptographyService.hexToBin("0356658024f6681ba34e20a7d111b4398edfdce258e0147cd17dd19e".toCharArray()));
//		ed.process(request);
//		System.out.println(request.getDecryptedRequestMsg());
//
//		request.setRequestBytes(CryptographyService.hexToBin("5a258650c43a12c06c107223632d6be31a2c5d721b7a87c30a15f0b7".toCharArray()));
//		ed.process(request);
//		System.out.println(request.getDecryptedRequestMsg());

	}

	public static String convertTo8BitString(byte[] byteArray) {
		String str = "";
		for (int i = 0; i < byteArray.length; i++) {
			String str1 = Integer.toBinaryString(byteArray[i]);
			if (str1.length() > 8)
				str = str + str1.substring(str1.length() - 8);
			else
				str = str + str1;
//			str = str+" ";
		}
		return str;
	}

	public static String convertTo32BitString(byte[] byteArray) {
		String str = "";
		for (int i = 0; i < byteArray.length; i++) {
			String str1 = Integer.toBinaryString(byteArray[i]);
			str = str +str1;
		}
		return str;
	}

	/**
	 * Process the received encrypted request message to get the plain text.
	 * 
	 * @param stkRequest
	 * @return
	 * @throws Exception
	 */
	public STKRequest process(STKRequest stkRequest) throws Exception {
		if (stkRequest != null && !StringUtils.isBlank(stkRequest.getRequestMsg())) {

			int[] receivedBytes = stkRequest.getRequestAsInts();
			String data = new String(CryptographyService.binToHex(receivedBytes));
			
			log.info("stk request data received in hex --> "+data);
			
			String randomFactor = data.substring(0, 8);
			String requestMsg = data.substring(8);
			String factor = randomFactor + factorKey;

			// Get Sub key for decrypt the request message
			String subKey = getSubKeyLeft(factor, rootKey) + getSubKeyRight(factor, rootKey);

			// Decrypt the received request message
			String decryptedData = decrypt(requestMsg, subKey);
			
			log.info("decrypted data-->"+decryptedData);
			
			int lastIndex = decryptedData.lastIndexOf("80");
			// Removes the padding data '8000000000' from the end of plain text
			if(lastIndex!=-1)			
				decryptedData = decryptedData.substring(0, decryptedData.lastIndexOf("80"));
			// Converts to ASC format
			decryptedData = new String(CryptographyService.hexToBin(decryptedData.toCharArray()));

			stkRequest.setDecryptedRequestMsg(decryptedData);
			log.info("Actual Message --> " + stkRequest.getSecuredDecryptedRequestMsg());
		}
		return stkRequest;
	}

	private String encrypt(byte[] plainValue, String key) throws Exception {
		log.debug("Plain text --> " + new String(plainValue));
		String result = null;
		if (plainValue.length > 0) {
			Security.addProvider(new BouncyCastleProvider());
			byte[] keyInBytes = CryptographyService.hexToBin(key.toCharArray());
			SecretKey skey = new SecretKeySpec(keyInBytes, SecurityConstants.DESEDE);
			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_ECB_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);

			cipher.init(Cipher.ENCRYPT_MODE, skey);
			result = new String(CryptographyService.binToHex(cipher.doFinal(plainValue)));
		}
		log.debug("Encrypted text --> " + result);
		return result;
	}

	private String decrypt(String encryptedValue, String key) throws Exception {
		log.debug("Encrypted text --> " + encryptedValue);
		String result = null;
		if (StringUtils.isNotBlank(encryptedValue)) {
			Security.addProvider(new BouncyCastleProvider());
			byte[] keyInBytes = CryptographyService.hexToBin(key.toCharArray());
			SecretKey skey = new SecretKeySpec(keyInBytes, SecurityConstants.DESEDE);
			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_ECB_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);

			cipher.init(Cipher.DECRYPT_MODE, skey);
			byte[] finalText = cipher.doFinal(CryptographyService.hexToBin(encryptedValue.toCharArray()));
			result = new String(CryptographyService.binToHex(finalText));
		}
		log.debug("Decrypted text --> " + result);
		return result;
	}

	private String encrypt(String plainText, String key) throws Exception {
		log.debug("Encrypted text --> " + plainText);
		String result = null;
		if (StringUtils.isNotBlank(plainText)) {
			Security.addProvider(new BouncyCastleProvider());
			byte[] keyInBytes = CryptographyService.hexToBin(key.toCharArray());
			SecretKey skey = new SecretKeySpec(keyInBytes, SecurityConstants.DESEDE);
			Cipher cipher = Cipher.getInstance(SecurityConstants.DESEDE_ECB_NOPADDING, SecurityConstants.BOUNCYCASTLE_PROVIDER);

			cipher.init(Cipher.ENCRYPT_MODE, skey);
			byte[] finalText = cipher.doFinal(CryptographyService.hexToBin(plainText.toCharArray()));
			result = new String(CryptographyService.binToHex(finalText));
		}
		log.debug("Decrypted text --> " + result);
		return result;
	}

	private String getSubKeyLeft(String factor, String rootKey) throws Exception {
		String result = null;
		if (StringUtils.isNotBlank(factor)) {
			byte[] byteText = CryptographyService.hexToBin(factor.toCharArray());
			result = encrypt(byteText, rootKey);
		}
		log.debug("Sub key left --> " + result);
		return result;
	}

	private String getSubKeyRight(String factor, String rootKey) throws Exception {
		String result = null;
		String xOrKey = "FFFFFFFFFFFFFFFF";

		if (StringUtils.isNotBlank(factor)) {
			byte[] f1b = CryptographyService.hexToBin(factor.toCharArray());
			byte[] f2b = CryptographyService.hexToBin(xOrKey.toCharArray());
			byte[] rb = new byte[f1b.length];

			for (int i = 0; i < f1b.length && i < f2b.length; i++) {
				rb[i] = (byte) (f1b[i] ^ f2b[i]);
			}
			result = encrypt(rb, rootKey);
		}
		log.debug("Sub key right --> " + result);
		return result;
	}

	private static String hexChartoBin(char c) {
		switch (c) {
			case '0':
				return "0000";
			case '1':
				return "0001";
			case '2':
				return "0010";
			case '3':
				return "0011";
			case '4':
				return "0100";
			case '5':
				return "0101";
			case '6':
				return "0110";
			case '7':
				return "0111";
			case '8':
				return "1000";
			case '9':
				return "1001";
			case 'A':
				return "1010";
			case 'B':
				return "1011";
			case 'C':
				return "1100";
			case 'D':
				return "1101";
			case 'E':
				return "1110";
			case 'F':
				return "1111";

		}
		return null;
	}

}

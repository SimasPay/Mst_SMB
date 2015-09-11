/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import java.io.File;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author xchen
 */
public class MfinoUtil {

	private static Logger	                 log	           = LoggerFactory.getLogger(MfinoUtil.class);
	//before correcting the findbugs reported error 
	/*private static final Map<String, String>	currCodetoName	= new HashMap<String, String>(20);
	public static final String	                 countryCode;*/
	
	//Corrected the non final fields.After correcting the findbugs reported error:
	private static final Map<String, String>	currCodetoName	= new HashMap<String, String>(20);
	public static final String	                 countryCode;

	static {
		String currencyCodes[] = ConfigurationUtil.getCurrencyCodes().split(",");
		String currencyNames[] = ConfigurationUtil.getCurrencyNames().split(",");
		for (int i = 0; i < currencyCodes.length; i++) {
			currCodetoName.put(currencyCodes[i], currencyNames[i]);
		}
		countryCode = ConfigurationUtil.getCountryCode();
	}

	public static String getCurrencyName(String currencyCode) {
		return currCodetoName.get(currencyCode);
	}

	public static String translateToAbsolutePath(String pathToTranslate, String baseDir) {
		if (pathToTranslate == null) {
			return null;
		}

		File tempFile = new File(pathToTranslate);
		if (tempFile.isAbsolute() == false) {
			tempFile = new File(baseDir, pathToTranslate);
		}

		return tempFile.getAbsolutePath();
	}

	public static String dumpHttpRequest(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();

		sb.append("URL: " + request.getRequestURI());
		sb.append("\n");
		sb.append("Remote Host: " + request.getRemoteHost());
		sb.append("\n");
		sb.append("Remote Address: " + request.getRemoteAddr());
		sb.append("\n");
		sb.append("Headers:");
		sb.append("\n");
		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			String n = (String) e.nextElement();
			sb.append(n + ": " + request.getHeader(n));
			sb.append("\n");
		}
		sb.append("Cookies");
		sb.append("\n");
		for (Cookie c : request.getCookies()) {
			sb.append(c.getName() + ": " + c.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static String replaceCommasWithSemicolons(String inputString) {

		if (StringUtils.isNotEmpty(inputString) && inputString.contains(GeneralConstants.COMMA_STRING)) {
			inputString = inputString.replaceAll(GeneralConstants.COMMA_STRING, GeneralConstants.SEMI_COLON_STRING);
		}
		return inputString;
	}

	public static Long generateRandomMdn() {
		Random rand = new Random();
		long drand = (long) (rand.nextDouble() * 10000000000L);
		return drand;
	}

	public static String generateOTP(int OTPLength) {
		return generateRandomNumber(OTPLength);
	}

	public static String generateRandomNumber(int length) {
		int otpLength = 6; //Default value
		
		if (length != -1) {
			otpLength = length;
		}
		String oneTimePin = generateRandomMdn().toString().substring(0, otpLength);
		
		while(!isPinStrongEnough(oneTimePin)) {
			oneTimePin = generateRandomMdn().toString().substring(0, otpLength);
		}
		
		return oneTimePin;
	}

	public static String GetMerchantTypeBySourceApplication(Integer SourceApplication) {

		if (SourceApplication.equals(CmFinoFIX.SourceApplication_Web))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Web_Channel;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_Phone))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_UTK_Channel;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_SMS))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_SMS_Channel;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_BankChannel))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Automated_Cash_Disbursements;
		if (SourceApplication.equals(CmFinoFIX.SourceApplication_WebService))
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_H2H_Channel;
		else
			return CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Other;

	}

	public static void main(String... args) {

		//System.out.println(MfinoUtil.normalizeMDN("008811234567"));
		//System.out.println(generateComplexPin());
	/*	String username = "Approver";
		String password = "User123";
		PasswordEncoder encoder2 = new ShaPasswordEncoder(1);
    	String encPassword2 = encoder2.encodePassword(password, username);
    	System.out.println(encPassword2);*/
	}

	public static String calculateDigestPin(String mdn, String pin) {
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(pin.getBytes());
			md.update(mdn.getBytes());
			byte[] bytes = md.digest();
			char[] encodeHex = Hex.encodeHex(bytes);
			String calcPIN = new String(encodeHex);
			calcPIN = calcPIN.toUpperCase();
			return calcPIN;
		}
		catch (Exception e) {
			log.error("Exception in generate onetime pin", e);
		}
		return null;
	}
	/**
	 * Three times hashed for generating the authorization token 
	 * if hsm is not enabled then it is single hashed(this is for backward compatibility with old versions)
	 * @param mdn
	 * @param pin
	 * @return
	 */
	public static String calculateAuthorizationToken(String mdn, String pin) 
	{
		String firstPass = calculateDigestPin(mdn, pin);
		if(!ConfigurationUtil.getuseHSM())
		{
			return firstPass;
		}
		//when hashed pin is used then already first pass is done.
		if(ConfigurationUtil.getuseHashedPIN())
		{
			firstPass = pin;
		}
		String secondPass =  calculateDigestPin(mdn, firstPass);
		return secondPass;
	}
	/**
	 * Constructs the display text as pocket template description followed by
	 * card pan
	 * 
	 * @param pocket
	 * @return
	 */
	public static String getPocketDisplayText(Pocket pocket) {
		String result = "";
		if (pocket != null) {
			if (StringUtils.isNotBlank(pocket.getCardPAN()) && pocket.getPocketTemplate() != null) {
				String cPan = pocket.getCardPAN();
				int cardpanSuffixlength = pocket.getPocketTemplate().getCardPANSuffixLength() != null ? pocket.getPocketTemplate().getCardPANSuffixLength() : 6;
				if (cPan.length() > cardpanSuffixlength) {
					cPan = cPan.substring(cPan.length() - cardpanSuffixlength);
				}
				result = pocket.getPocketTemplate().getDescription() + " - " + cPan;
			}
			else if (pocket.getPocketTemplate() != null) {
				result = pocket.getPocketTemplate().getDescription();
			}
		}
		return result;
	}

	public static String generateForgotPasswordMail(String username, String code) {
		String msg = String.format(" Dear %s \n To Reset your password click the following link\n" + ConfigurationUtil.getAppURL() + "/resetpassword.htm?username=%s&code=%s \n" + "The link is valid for 24 hours only", username, username, code);
		return msg;
	}



	public static boolean isPinStrongEnough(String pin) {
		if (StringUtils.isBlank(pin))
			return false;
		char[] arr = pin.toCharArray();
		if ((arr[0] + arr[2] == (arr[1] * 2)) && Math.abs(arr[0] - arr[1]) == 1)
			return false;
		if ((arr[1] + arr[3] == (arr[2] * 2)) && Math.abs(arr[2] - arr[3]) == 1)
			return false;

		Arrays.sort(arr);
		if (arr[0] == arr[2] || arr[1] == arr[3])
			return false;

		return true;
	}
	
	public static String leftPadWithCharacter(String str, int totalLength, String padCharacter){
		if((str == null) || ("".equals(str))) return str;
		
		if(str.length() < totalLength){
			int strLen = str.length();
			for(int i = 0;i < (totalLength - strLen);i++ ){
				str = padCharacter + str;
			}
		}
		
		return str;
	}
	
	public static String CheckDigitCalculation(String mdn){
		int sum=0;
		int mulpro;
		String mdnapp = "5" + leftPadWithCharacter(mdn, 14, "0");
		String strgen;
		int[] num = new int[mdnapp.length()];
		for (int i = 0; i < mdnapp.length(); i++){
	        num[i] = mdnapp.charAt(i) - '0';
	    }
		 for (int i : num) {
		       sum=sum + i;
		  }
		mulpro = sum*9;
        char[] chars = ("" + mulpro).toCharArray();
        strgen=mdnapp+chars[chars.length-3];
        return strgen;
		
	}
	
	public static NumberFormat getNumberFormat() {
        Locale locale = new Locale(ConfigurationUtil.getCurrencyFormatLocale());
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return numberFormat;
	}
	
	/*public static boolean validatePIN(String mdn, String pin, String offset) throws Exception
	{
		if(!ConfigurationUtil.getuseHSM())
		{
			log.info("performing validation using digested pin");
			return offset.equalsIgnoreCase(calculateDigestPin(mdn,pin));
		}
		else
		{
			log.info("performing validation using hsm");
			HSMHandler handler = new HSMHandler();
			return handler.validatePIN(mdn, pin,offset);
		}
	}*/
}

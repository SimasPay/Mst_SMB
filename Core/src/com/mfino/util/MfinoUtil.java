/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import java.io.File;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Clob;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
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
//		String additionalInfo = "08PAYMENT    : Telkom Fix Line (PSTN)   IDPEL      : 02188874874              NAME       :  WARINO                  BILLING AMT: RP. 51.480               ADMIN BANK : RP. 0                    PAYMENT AMT: RP. 51.480                                                                                           02188874874     020008          11                000000000000000000000000000000000000                000000000000000000000000000000000000                000000000000000000000000000000000000701A            000000051480000000000000000000000000 WARINO                                         ";
		String additionalInfo = "08PAYMENT    : Telkom Fix Line (PSTN)   IDPEL      : 02188874874              NAME       :  WARINO                  BILLING AMT: RP. 51.480               ADMIN BANK : RP. 0                    TOTAL TAGIHAN : RP. 51.480                                                                                        02188874874     020008          11                000000000000000000000000000000000000                000000000000000000000000000000000000                000000000000000000000000000000000000701A            000000051480000000000000000000000000 WARINO                                         ";
		BigDecimal charges = new BigDecimal(2500);
		BigDecimal amount = new BigDecimal(10000);
//		String prefixWording = "ADMIN BANK : RP.";
		String prefixWording = ConfigurationUtil.getPrefixWordingForTotalTagihan();
		System.out.println(replaceWordingValue(additionalInfo, charges, amount));
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
			if (StringUtils.isNotBlank(pocket.getCardpan()) && pocket.getPocketTemplateByPockettemplateid() != null) {
				String cPan = pocket.getCardpan();
				Long temp = pocket.getPocketTemplateByPockettemplateid().getCardpansuffixlength() != null ? pocket.getPocketTemplateByPockettemplateid().getCardpansuffixlength().longValue() : 6;
				int cardpanSuffixlength = temp.intValue();
				if (cPan.length() > cardpanSuffixlength) {
					cPan = cPan.substring(cPan.length() - cardpanSuffixlength);
				}
				result = pocket.getPocketTemplateByPockettemplateid().getDescription() + " - " + cPan;
			}
			else if (pocket.getPocketTemplateByPockettemplateid() != null) {
				result = pocket.getPocketTemplateByPockettemplateid().getDescription();
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
		
		/*char[] arr = pin.toCharArray();
		if ((arr[0] + arr[2] == (arr[1] * 2)) && Math.abs(arr[0] - arr[1]) == 1)
			return false;
		if ((arr[1] + arr[3] == (arr[2] * 2)) && Math.abs(arr[2] - arr[3]) == 1)
			return false;
		
		if(pin.length() > 4) {
		
			if ((arr[2] + arr[4] == (arr[3] * 2)) && Math.abs(arr[3] - arr[4]) == 1)
				return false;
		}

		Arrays.sort(arr);
		if (arr[0] == arr[2] || arr[1] == arr[3])
			return false;
		
		if(pin.length() > 4) {
			
			if (arr[2] == arr[4])
				return false;
		}

		return true;*/
		
		boolean isPinStrongEnough = false;
		
		if(!containsRepetitiveDigits(pin)) {
			
			isPinStrongEnough = true;
			
			if(!containsSequenceOfDigits(pin)) {
			
				isPinStrongEnough = true;
				
			} else {
				
				isPinStrongEnough = false;
			}
		}
		
		return isPinStrongEnough;
	}
	
	public static boolean containsRepetitiveDigits(String tpin) {
		
	    char firstChar = tpin.charAt(0);
	    for (int i = 1; i < tpin.length(); i++) {
	        char nextChar = tpin.charAt(i);
	        if ((Character.valueOf(nextChar)).compareTo(Character.valueOf(firstChar)) != 0) {
	            return false;
	        }
	    }
	    log.info("Error:TPIN contains repetitive digits");
	    return true;
	}
	
	public static boolean containsSequenceOfDigits(String tpin) {
		
	    String firstChar = String.valueOf(tpin.charAt(0));
	    StringBuffer sb = new StringBuffer();
	    
	    for (int i = 0; i < tpin.length(); i++) {
	    	
	        sb.append(Integer.parseInt(firstChar));
	        firstChar = String.valueOf(Integer.parseInt(firstChar) + 1);
	    }
	    
	    if(tpin.equals(sb.toString())) {
	    
	    	log.info("Error:TPIN contains sequence digits");
		    return true;
		    
	    } else {
	    	
	    	return false;
	    }
	}
	
	public static boolean containsDateOfBirthAsPin(String tpin) {
		
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
	
	public static Clob stringToClob(String text){
		if (StringUtils.isNotBlank(text)) {
			try {
				Clob clob = new SerialClob(text.toCharArray());
				clob.setString(1, text);
				return clob;
			} catch (Exception e) {
				return null;
			} 
		}
    	return null;
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
	
	/**
	 * this method is used for replacing ADMIN BANK value with configured charges.<br/>
	 * 
	 * @param additionalInfo <i>Ex: |PAYMENT : Telkom Fix Line (PSTN) |IDPEL : 02188874874 |NAME : WARINO |BILLING AMT: RP. 51.480 |ADMIN BANK : RP. 0 |PAYMENT AMT: RP. 51.480 | |</i>
	 * @param charges
	 * @param prefixWording <i>ADMIN BANK : RP.</i>
	 * @return
	 */
	public static String replaceFormatedWordingValue(String additionalInfo, BigDecimal charges, BigDecimal amount){
		if(charges == null) charges = BigDecimal.ZERO;
		if(amount == null) amount = BigDecimal.ZERO;
		
		if(StringUtils.contains(additionalInfo, ConfigurationUtil.getPrefixWordingForAdminBank()) || 
				StringUtils.contains(additionalInfo, ConfigurationUtil.getPrefixWordingForTotalTagihan())){
			
			String[] splitedAdditionalInfo = StringUtils.split(additionalInfo, "|");
			
			for (String addInfo : splitedAdditionalInfo) {

				String oldWording = addInfo;
				String newWording = "";
				
				if(StringUtils.startsWith(addInfo, ConfigurationUtil.getPrefixWordingForAdminBank())){
					String addInfoValue = StringUtils.substring(addInfo, StringUtils.length(ConfigurationUtil.getPrefixWordingForAdminBank()));
					
					int length = StringUtils.length(getNumberFormat().format(charges));
					String replacedValue = StringUtils.replace(addInfoValue, StringUtils.substring(addInfoValue, 1, length+1), 
								getNumberFormat().format(charges));
					
					newWording = ConfigurationUtil.getPrefixWordingForAdminBank()+replacedValue;
					additionalInfo = StringUtils.replace(additionalInfo, oldWording, newWording);
				}
				
				if(StringUtils.startsWith(addInfo, ConfigurationUtil.getPrefixWordingForTotalTagihan())){
					String addInfoValue = StringUtils.substring(addInfo, StringUtils.length(ConfigurationUtil.getPrefixWordingForTotalTagihan()));
					BigDecimal totalAmount = amount.add(charges);
					int length = StringUtils.length(getNumberFormat().format(totalAmount));
					String replacedValue = StringUtils.replace(addInfoValue, StringUtils.substring(addInfoValue, 1, length+1), 
							getNumberFormat().format(totalAmount));
					
					newWording = ConfigurationUtil.getPrefixWordingForTotalTagihan()+replacedValue;
					additionalInfo = StringUtils.replace(additionalInfo, oldWording, newWording);
				}
			}
		}
		return additionalInfo;
	}
	
	public static String replaceWordingValue(String additionalInfo, BigDecimal charges, BigDecimal amount){
		if(charges == null) charges = BigDecimal.ZERO;
		if(amount == null) amount = BigDecimal.ZERO;
		
		if(( StringUtils.contains(additionalInfo, ConfigurationUtil.getPrefixWordingForAdminBank()) 
				|| StringUtils.contains(additionalInfo, ConfigurationUtil.getPrefixWordingForTotalTagihan()) )
				&& StringUtils.length(additionalInfo) >= 38){

			String dataElement62 = StringUtils.substring(additionalInfo, 2);
			String oldAdminBankWording = "";
			String newAdminBankWording = "";
			Iterable<String> records = Splitter.fixedLength(38).split(dataElement62);
			
			for (String addInfo : records) {
				if (StringUtils.startsWith(addInfo, ConfigurationUtil.getPrefixWordingForAdminBank())) {
					oldAdminBankWording = addInfo;
					String prefixWording = ConfigurationUtil.getPrefixWordingForAdminBank();
					String addInfoValue = StringUtils.substring(addInfo, prefixWording.length());
					int length = StringUtils.length(getNumberFormat().format(charges));
					
					String replacedValue  = StringUtils.replace(addInfoValue, StringUtils.substring(addInfoValue, 1, length+1), 
								getNumberFormat().format(charges));
					
					newAdminBankWording = prefixWording+replacedValue;
					additionalInfo = StringUtils.replace(additionalInfo, oldAdminBankWording, newAdminBankWording);
				}
				
				if (StringUtils.startsWith(addInfo, ConfigurationUtil.getPrefixWordingForTotalTagihan())) {
					oldAdminBankWording = addInfo;
					String prefixWording = ConfigurationUtil.getPrefixWordingForTotalTagihan();
					String addInfoValue = StringUtils.substring(addInfo, prefixWording.length());
					
					BigDecimal totalAmount = amount.add(charges);
					int length = StringUtils.length(getNumberFormat().format(totalAmount));
					String replacedValue = StringUtils.replace(addInfoValue, StringUtils.substring(addInfoValue, 1, length+1), 
								getNumberFormat().format(totalAmount));
					
					newAdminBankWording = prefixWording+replacedValue;
					additionalInfo = StringUtils.replace(additionalInfo, oldAdminBankWording, newAdminBankWording);
				}
			}
		}
		
		return additionalInfo;
	}
}

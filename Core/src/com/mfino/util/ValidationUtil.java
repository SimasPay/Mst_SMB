/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author xchen
 */
public class ValidationUtil {
	
	private static Logger log = LoggerFactory.getLogger(ValidationUtil.class);

    public static boolean ValidateMDN(String mdn) {
        if (mdn.length() > 16 || mdn.length() < 5) {
            return false;
        }
//        if (!(mdn.startsWith("62881")) && !(mdn.startsWith("62882"))) {
//            return false;
//        }
        
        return true;
    }

    public static boolean ValidateBankAccount(String card) {
        if (card.length() > 19) {
            return false;
        }

        return isValidCardNumber(card);
    }

    /**
     * Checks whether a string of digits is a valid credit card number according
     * to the Luhn algorithm.
     *
     * 1. Starting with the second to last digit and moving left, double the
     *    value of all the alternating digits. For any digits that thus become
     *    10 or more, add their digits together. For example, 1111 becomes 2121,
     *    while 8763 becomes 7733 (from (1+6)7(1+2)3).
     *
     * 2. Add all these digits together. For example, 1111 becomes 2121, then
     *    2+1+2+1 is 6; while 8763 becomes 7733, then 7+7+3+3 is 20.
     *
     * 3. If the total ends in 0 (put another way, if the total modulus 10 is
     *    0), then the number is valid according to the Luhn formula, else it is
     *    not valid. So, 1111 is not valid (as shown above, it comes out to 6),
     *    while 8763 is valid (as shown above, it comes out to 20).
     *
     * @param number the credit card number to validate.
     * @return true if the number is valid, false otherwise.
     */
    private static boolean isValidCardNumber(String number) {
        int sum = 0;

        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
    /***
     *
     * @param number
     * @param regExp
     * @return
     * It is used to check whether cardpan is valid according to the give regular expression.
     * It returns true if it matches and false otherwise.
     */
    public static boolean validateRegularExpression(String cardpan,String regExp) throws PatternSyntaxException{
        if(regExp==null){
            return false;
        }
        return Pattern.matches(regExp, cardpan);
    }
    public static boolean isValidEmail(String emailId) {
    	//Set the email pattern string
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        //Match the given string with the pattern
        Matcher m = p.matcher(emailId);

        //check whether match is found 
        boolean matchFound = m.matches();

        if (matchFound)
          return true;
        else
          return false;

    }
    
    public static boolean isValidEmail2(String email)	
    {
	try
	{
	    if(email.matches("[^@]+@[^\\.]+[A-Za-z0-9\\.]+"))
		return true;
	}
	catch(Exception ex)
	{
	    log.error("Invalid email address", ex);
	}
	return false;
    }
    
    public static Integer validateOTP(SubscriberMdn subscriberMDN, boolean isHttps, boolean isHashedPin, String oneTimeOTP) {

		if (subscriberMDN == null) {
			
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		Long tempType = subscriberMDN.getSubscriber().getType();
		int int_subscriberType=tempType.intValue();

		if (!(CmFinoFIX.SubscriberType_Subscriber.equals(int_subscriberType)
				||CmFinoFIX.SubscriberType_Partner.equals(int_subscriberType))) {
			
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		
		if (subscriberMDN.getOtpexpirationtime().before(new Date())) {
			
			log.info("OTP Expired failed for the subscriber "+ subscriberMDN.getMdn());
			return CmFinoFIX.NotificationCode_OTPExpired;
		}

		String originalOTP =subscriberMDN.getOtp();

		if (!isHttps) {
			
		try {
				String authStr = oneTimeOTP;
				byte[] authBytes = CryptographyService.hexToBin(authStr.toCharArray());
				byte[] salt = { 0, 0, 0, 0, 0, 0, 0, 0 };
				byte[] decStr = CryptographyService.decryptWithPBE(authBytes, originalOTP.toCharArray(), salt, 20);
				String str = new String(decStr, GeneralConstants.UTF_8);
				
				if (!GeneralConstants.ZEROES_STRING.equals(str))
					return CmFinoFIX.NotificationCode_OTPInvalid;
				
			} catch (Exception ex) {
				log.info("OTP Check failed for the subscriber " + subscriberMDN.getMdn());
				
				return CmFinoFIX.NotificationCode_OTPInvalid;
			}
		
		} else {
			String receivedOTP = oneTimeOTP;
			if (CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN.getStatus())) {
				
				/*String prefix = systemParametersService.getString(SystemParameterKeys.FAC_PREFIX_VALUE);
				prefix = (prefix == null) ? StringUtils.EMPTY : prefix;
				int otpLength = Integer.parseInt(systemParametersService.getString(SystemParameterKeys.OTP_LENGTH));
				
				String receivedFAC = receivedOTP;
				
				if (receivedOTP.length() < (otpLength + prefix.length()))
					receivedFAC = prefix + receivedOTP;

				String receivedFACDigest = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), receivedFAC);*/
			
			} else {
				receivedOTP = new String(CryptographyService.generateSHA256Hash(subscriberMDN.getMdn(), receivedOTP));
				
				if (!originalOTP.equals(receivedOTP)) {
					
					log.info("OTP Check failed for the subscriber "+ subscriberMDN.getMdn());
					return CmFinoFIX.NotificationCode_OTPInvalid;
				}
			}
		}
	
		return CmFinoFIX.NotificationCode_OTPValidationSuccessful;
	}
}

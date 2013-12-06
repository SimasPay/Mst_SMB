/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}

package com.mfino.handset.subscriber.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Srinu
 */
public class ConfigurationUtil {
    
    public static final String CHANNEL_ID = "7";
    public static final String POCKET_CODE_EMONEY = "1";
    public static final String POCKET_CODE_BANK = "2";
    
    private static String smsUrl = "http://app.m-campaigner.in/SendIndividualSMS.aspx?UserID=bWZpbm8=&Key=bWZpbm9eKiQ=&SenderName=mfino&SenderID=47&MobileNo=";

    public static String getSmsUrl() {
        return smsUrl;
    }

    public static String replace(String oldStr, String newStr, String inString) {
        int start = inString.indexOf(oldStr);
        if (start == -1) {
            return inString;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(inString.substring(0, start));
        sb.append(newStr);
        sb.append(inString.substring(start + oldStr.length()));
        return sb.toString();
    }
}

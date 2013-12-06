package com.mfino.handset.agent.util;

import com.mfino.handset.agent.constants.Constants;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Srinu
 */
public class StringUtil {

    private static String url = "http://41.203.113.122:8080/webapi/dynamic";
    public static final String CHANNEL_ID = "1";
    public static final String POCKET_CODE_EMONEY = "3";
    public static final String POCKET_CODE_BANK = "6";
    private static String smsUrl = "http://app.m-campaigner.in/SendIndividualSMS.aspx?UserID=bWZpbm8=&Key=bWZpbm9eKiQ=&SenderName=mfino&SenderID=47&MobileNo=";

    public static String getURL() {
        return url;
    }

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

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        if (Constants.EMPTY_STRING.equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static String normalizeMDN(String MDN) {

        int start = 0;
        if (StringUtil.isBlank(MDN)) {
            return "";
        }

        MDN = MDN.trim();

        while (start < MDN.length()) {
            if ('0' == MDN.charAt(start)) {
                start++;
            } else {
                break;
            }
        }

        if (MDN.startsWith("234", start)) {
            start += "234".length();
        }

        return "234" + MDN.substring(start);
    }

    public static boolean isValidDOB(String dob) {

        try {
            if (isBlank(dob)) {
                return false;
            }
            if (dob.length() != 10) {
                return false;
            }
            if (dob.indexOf('/') != 2) {
                return false;
            }
            if (dob.lastIndexOf('/') != 5) {
                return false;
            }

            String d1 = dob.substring(0, 2);
            int date = Integer.parseInt(d1);
            if (date < 0 || date > 31) {
                return false;
            }

            d1 = dob.substring(3, 5);
            int month = Integer.parseInt(d1);
            if (month < 0 || month > 12) {
                return false;
            }

            d1 = dob.substring(6, 10);
            int year = Integer.parseInt(d1);
            if (year < 0 || year > 9999) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }
}

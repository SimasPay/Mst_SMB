package com.mfino.handset.subscriber.util;

import com.mfino.handset.subscriber.constants.Constants;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Srinu
 */
public class ConfigurationUtil {


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
        if (ConfigurationUtil.isBlank(MDN)) {
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
        
        if(MDN.startsWith("234", start)) {
            start += "234".length();
        }
     
        return "234" + MDN.substring(start);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.i18n;

import com.mfino.enums.FormatType;

/**
 *
 * @author xchen
 */
public class Formatter {
    public static String format(Object o, FormatType type){
        //based on the specific format type and locale, return the correct formated data
        //locale is especially important for dates and money
        return o.toString();
    }

}

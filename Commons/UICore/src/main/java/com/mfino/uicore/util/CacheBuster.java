/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.util;

import com.mfino.constants.GeneralConstants;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author sandeepjs
 */
public class CacheBuster {


    public static String getTimeStamp()
    {
        String retValue = GeneralConstants.EMPTY_STRING;
        retValue = retValue + System.currentTimeMillis();
        return retValue;
    }


    public static String getVersion()
    {
        String retValue = GeneralConstants.EMPTY_STRING;
        retValue = retValue + ConfigurationUtil.getVersion();
        return retValue;
    }

}

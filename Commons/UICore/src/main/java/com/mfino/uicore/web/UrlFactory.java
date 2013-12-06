/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import com.mfino.uicore.util.CacheBuster;

/**
 *
 * @author xchen
 */
public class UrlFactory {

    public static String getIndex() {
        return "index.htm?" + CacheBuster.getTimeStamp();
    }

    public static String getChangePassword(){
        return "changepassword.htm";
    }
}

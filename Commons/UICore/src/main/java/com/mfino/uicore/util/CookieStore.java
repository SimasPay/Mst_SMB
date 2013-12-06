/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;

/**
 *
 * @author sandeepjs
 */
public class CookieStore {

    private static Logger log = LoggerFactory.getLogger(CookieStore.class);

    public CookieStore() {
    }

    public static void set(HttpServletResponse response, String enumKey, String enumValue) {
        try {
            Cookie cookie = new Cookie(enumKey, enumValue);
            response.addCookie(cookie);

        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
    }

    public static String get(HttpServletRequest request, String enumKey) {
        String retValue = GeneralConstants.EMPTY_STRING;

        try {
            Cookie[] cookies = request.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals(enumKey)) {
                    retValue = cookie.getValue();
                    break;
                }
            }
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }

        return retValue;
    }
}

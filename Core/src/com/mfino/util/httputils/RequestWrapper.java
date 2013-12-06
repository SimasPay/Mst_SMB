/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util.httputils;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author sunil
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private HashMap<String, String> parameters;

    @SuppressWarnings("unchecked")
    public RequestWrapper(HttpServletRequest nested) {
        super(nested);
        parameters = new HashMap<String, String>(nested.getParameterMap());
    }
    
    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }
    @Override
    public String getParameter(String name) {
        String val = "";
        Object param = parameters.get(name);
        if (param != null) {
            if (param instanceof String[]) {
                String str[] = (String[]) param;
                val = str[0];
            } else {
                val = param.toString();
            }

        }
        return val;
    }
}

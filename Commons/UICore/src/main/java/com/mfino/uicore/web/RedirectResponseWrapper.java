/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author xchen
 */
public class RedirectResponseWrapper
        extends HttpServletResponseWrapper {

    private String redirect;

    public RedirectResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
    }

    public String getRedirect() {
        //TODO: currently the serverside does not know the current page's
        //URL before timeout. Let's always return to the main page for now.

        //TODO: need to figure out how to get the application path
        //return ContextHolder.getCurrentApplicationContext().getId();

        return UrlFactory.getIndex();
//      return redirect;
    }

    public boolean isError(){
        if(this.redirect.contains("login_error=1")){
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public void sendRedirect(String string) throws IOException {
        this.redirect = string;
    }
}



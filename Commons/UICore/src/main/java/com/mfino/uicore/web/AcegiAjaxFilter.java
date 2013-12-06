package com.mfino.uicore.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
/**
 *
 * @author xchen
 */
public class AcegiAjaxFilter
        extends OncePerRequestFilter {

    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!isAjaxRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(response);

        filterChain.doFilter(request, redirectResponseWrapper);

        if (redirectResponseWrapper.getRedirect() != null) {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");

            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma", "no-cache");

            //Object changePassword=request.getSession().getAttribute("changePassword");
            String redirectURL="";
            redirectURL = redirectResponseWrapper.getRedirect();
            
            /*
             * removed as part of #2311
             * if (changePassword != null) {
                if (Boolean.parseBoolean(changePassword.toString())) {
                    redirectURL = "changepassword.htm";
                }
            }*/
            redirectResponseWrapper.sendRedirect(redirectURL);
            String content;
            if (redirectResponseWrapper.isError() == false) {  
                        content = "{\"success\": true, \"url\":\"" + redirectURL + "\"}";
            } else {
                AuthenticationException lastException = ((AuthenticationException) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION ));
                String lastExceptionMessage = lastException.getMessage();
                lastExceptionMessage.replace("\"", "\\\"");
                content = "{\"success\": false, \"message\": \"" + lastExceptionMessage + "\"}";
            }
            response.getOutputStream().write(content.getBytes("UTF-8"));
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestWith = request.getHeader("x-requested-with");

        return "XMLHttpRequest".equalsIgnoreCase(requestWith);
    }
}




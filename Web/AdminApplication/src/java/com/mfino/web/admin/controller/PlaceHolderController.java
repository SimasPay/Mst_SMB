/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.util.CacheBuster;
import com.mfino.uicore.util.CookieStore;

/**
 *
 * @author xchen
 */
@Controller
public class PlaceHolderController {
	private Logger log = LoggerFactory.getLogger(this.getClass());
   

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@RequestMapping("/index.htm")
    public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {

        String userString = StringUtils.EMPTY;
        String i18nJSPath = StringUtils.EMPTY;
        String authFileName = StringUtils.EMPTY;
        String userBranchCode=StringUtils.EMPTY;
        try {
            userString = userService.getUserString();
            userBranchCode=userService.getUserBranchCodeString();
            i18nJSPath = "/js/message/msg." + userService.getUserLanguageCode() + ".js";
            authFileName = "authorization.jsx?" + CacheBuster.getTimeStamp();
        } catch (Exception ex) {
        	log.error("Error in index controller", ex);
        } 
        
        HashMap<String,String> map = new HashMap<String,String>();
        HttpSession session = request.getSession(); //added for #2311
        if(session!= null && session.getAttribute("changePassword")!= null){
        	map.put("changePassword", "true");
        }
        String promptPin = CookieStore.get(request, "promptPin");  
        map.put("promptPin", promptPin);
        map.put("userString", userString);
        map.put("userBranchCodeString", userBranchCode);
        map.put("i18nJSPath", i18nJSPath);
        map.put("authFileName", authFileName);
        map.put("ip", request.getRemoteAddr());
        return new ModelAndView("index", map);
    }

    @RequestMapping("/secure/debug.htm")
    public ModelAndView deubg() {
        return new ModelAndView("secure/debug");
    }
}

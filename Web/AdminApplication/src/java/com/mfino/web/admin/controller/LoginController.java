/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.mfino.i18n.MessageText;
import com.mfino.uicore.web.JSONView;

/**
 *
 * @author xchen
 */
@Controller
public class LoginController {

    @RequestMapping("/login.htm")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response){
    	HttpSession session = request.getSession();    	
    	if(session!= null) { //added for #2311
    		session.invalidate();
    	}
        return new ModelAndView("login");
    }
    
    @RequestMapping("/sessionExpired.htm")
    public View sessionExpiryHandler(HttpServletRequest request, HttpServletResponse response){
    	HashMap<String,Object> map = new HashMap<String, Object>();
    	map.put("success", true);
    	map.put("Error", MessageText._("Session Expired"));
    	return new JSONView(map);
    }
}

package com.mfino.monitor.controllers;

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

@Controller
public class LoginController {

    @RequestMapping("/login.htm")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response){
    	HttpSession session = request.getSession();    	
    	if(session!= null) { 
    		session.invalidate();
    	}
    	HashMap<String,Object> model = new HashMap<String, Object>(); 
    	if(request.getParameter("unauthorized") != null){
    		model.put("Error", MessageText._("Unauthorized access"));
    	}    	
        return new ModelAndView("login","model",model);
    }
    
    @RequestMapping("/sessionExpired.htm")
    public View sessionExpiryHandler(HttpServletRequest request, HttpServletResponse response){
    	HashMap<String,Object> map = new HashMap<String, Object>();
    	map.put("success", true);
    	map.put("Error", MessageText._("Session Expired"));
    	return new JSONView(map);
    }
    
}

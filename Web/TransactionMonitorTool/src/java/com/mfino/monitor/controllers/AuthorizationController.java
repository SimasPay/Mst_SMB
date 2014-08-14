package com.mfino.monitor.controllers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.mfino.fix.CmFinoFIX;
import com.mfino.service.AuthorizationService;
import com.mfino.service.JSAuthGeneratorService;
import com.mfino.uicore.util.CacheBuster;
import com.mfino.uicore.web.TextView;

/**
 * @author Srikanth
 * 
 */

@Controller("AuthorizationController")
public class AuthorizationController {	
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier("JSAuthGeneratorServiceImpl")
	private JSAuthGeneratorService jsAuthGenerator;
	
    @RequestMapping("/authorization.jsx")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response){
        View textView;
        try{
            textView = new TextView(jsAuthGenerator.generateScript());
            return textView;
        }catch (Throwable throwable) {
        	log.error(throwable.getMessage(), throwable);
            return null;
        }
    }
	
	@RequestMapping("/index.htm")
	public ModelAndView activation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HashMap<String,String> map = new HashMap<String,String>();
		boolean isAuth = authorizationService.isAuthorized(CmFinoFIX.Permission_TransactionMonitor);
		String authFileName = "authorization.jsx?" + CacheBuster.getTimeStamp();
		String viewName = null;
		if(isAuth) {
			viewName = "dashboard";
			map.put("authFileName", authFileName);
		} else {
			viewName = "unauthorizedPage";
		}
		return new ModelAndView(viewName, map);
	}
}

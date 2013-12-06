package com.mfino.monitor.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mfino.fix.CmFinoFIX;
import com.mfino.service.AuthorizationService;

/**
 * @author Srikanth
 * 
 */

@Controller("AuthorizationController")
public class AuthorizationController {	
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@RequestMapping("/index.htm")
	public ModelAndView activation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		boolean isAuth = authorizationService.isAuthorized(CmFinoFIX.Permission_TransactionMonitor);
		String viewName = null;
		if(isAuth) {
			viewName = "dashboard";
		} else {
			viewName = "unauthorizedPage";
		}
		return new ModelAndView(viewName);
	}
}

package com.mfino.web.admin.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;
import com.mfino.i18n.MessageText;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.web.JSONView;

/**
 * 
 * @author Maruthi
 */
@Controller
public class ForgotPasswordController {


	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/forgotpassword.htm")
	public View forgotPasswordRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		return forgotPassword(request, response);
	}

	@RequestMapping("/resetpassword.htm")
	public ModelAndView resetPasswordRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		return new ModelAndView("result", resetPassword(request, response));
	}
	
	
	View forgotPassword(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("username");
		log.info("Forgot Password link requested for user:"+username+" by:"+username);
		HashMap map = new HashMap();
		if (username == null) {
			map.put("success", false);
			map.put("Error", MessageText._("Sorry, Username cannot be Null"));
			return new JSONView(map);
		}
		try {
			userService.sendForgotPasswordLink(username);
			map.put("success", true);
			map.put("Error",MessageText._("Reset Password Link sent to your mail"));
			log.info("Forgot Password link sent to user:"+username+" by:"+username);
		} catch (Exception exp) {
			log.error("Error  " + username, exp);
			map.put("success", false);
			map.put("message", MessageText._("  Unable to process your request please try again "));
		} 

		return new JSONView(map);
	}
	
	HashMap resetPassword(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("username");
		String code = request.getParameter("code");
		log.info("Reset Password requested for user:"+username+" by:"+username);
		
		HashMap map = new HashMap();
		if (StringUtils.isBlank(username)||StringUtils.isBlank(code)) {
			map.put("success", false);
			map.put("Error", MessageText._("Sorry, Invalid Request"));
			return map;
		}
		
		try {
			CMErrorNotification errorMsg=userService.resetPassword(username,code);
			 if (errorMsg.getErrorCode() == CmFinoFIX.ErrorCode_NoError) {
				 map.put("success", true);
				 map.put("Error","Your password has been reset successfully. New Password sent to your maild id");
				 log.info("Reset Password success for user:"+username+" by:"+username);
			    }else{
			    	map.put("success", false);
			    	if(StringUtils.isBlank(errorMsg.getErrorDescription())){
			    		 map.put("Error","Unable to process your request please try again");
			    	}else{
			    		 map.put("Error",errorMsg.getErrorDescription());
			    	}
			    }			
		} catch (Exception exp) {
			
			log.error("Error  " + username, exp);
			map.put("success", false);
			map.put("message", MessageText._("  Unable to process your request please try again "));
		} 

		return map;
	}
}

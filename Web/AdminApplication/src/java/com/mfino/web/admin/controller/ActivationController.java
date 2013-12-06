/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberService;

/**
 *
 * @author Maruthi
 */
@Controller
public class ActivationController {

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/activation.htm")
	public ModelAndView activation(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String service=request.getParameter("service");
		String userName = request.getParameter("userid");
		String otp = request.getParameter("otp");
		log.info("Activation requested for user:"+userName);
		if("activation".equals(service)&&StringUtils.isNotBlank(userName)&&StringUtils.isNotBlank(otp)){
			HashMap map= activate(request, response, userName, otp);
			return new ModelAndView("result", map);
		}
		return new ModelAndView("login"); 
	}
	
	@RequestMapping("/emailVerification.htm")
	public ModelAndView emailVerification(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {		
		String subscriberID = request.getParameter("subscriberID");
		String email = request.getParameter("email");
		
		HashMap map = new HashMap();
		log.info("Email verification requested for subscriber with ID"+subscriberID);
		if(StringUtils.isNotBlank(subscriberID)&& StringUtils.isNotBlank(email) && StringUtils.isNumeric(subscriberID)){
			Long subID = Long.valueOf(subscriberID);
			try {
				subscriberService.verifyEmail(subID, email);
				map.put("success", true);
				map.put("Error", "Your email is verified successfully");
				return new ModelAndView("result", map);
			} catch (Exception e) {
				map.put("success", false);
				map.put("Error", "Unable to process your email verification request\n" + e.getMessage());
				return new ModelAndView("result", map);
			}			
		} else {
			map.put("success", false);
			map.put("Error", "Unable to process your email verification request\n" +
					"Please contact customercare for more info.");
			return new ModelAndView("result", map);
		}		
	}

	@SuppressWarnings("unchecked")
	HashMap activate(HttpServletRequest request,
			HttpServletResponse response,
			String username,String otp) {
		HashMap map = new HashMap();
		
		try {
			String responsetext=partnerService.processActivation(username, otp) ;          
			map.put("success", true);
			map.put("Error", responsetext);
			log.info("Activation response for user:"+username+" response:"+responsetext);
		} catch (Exception e) {
			log.error("Partner Activation exception",e);
			map.put("success", false);
			map.put("Error", "Unable to process your activation request\n" +
			"Please contact customercare for more info.");
		}

		return map;
	}
}

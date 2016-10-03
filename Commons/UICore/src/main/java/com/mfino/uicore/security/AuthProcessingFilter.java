/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.security;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.service.AuthorizationService;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.web.UrlFactory;

/**
 * 
 * @author Venkat
 */
public class AuthProcessingFilter extends UsernamePasswordAuthenticationFilter implements AuthProcessingFilterInterface{
	public static Hashtable loggedInHash = new Hashtable();
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	@Override
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, Authentication authResult)
			throws IOException, ServletException {
		log.debug("Start of successfulAuthentication method");
		super.successfulAuthentication(request, response, authResult);

		boolean isChangePassword = false;
		boolean promptPin = false;
		User userObj = null;
		String userName = authResult.getName();
		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		UserQuery query = new UserQuery();
		query.setUserName(userName);
		List<User> results = (List<User>) userDAO.get(query);
		if (results.size() > 0) {
			userObj = results.get(0);
			isChangePassword = Boolean.valueOf(userObj.getFirsttimelogin().toString());
			if (!isChangePassword) {
				isChangePassword = subscriberService.isPasswordExpired(userObj);
			}
			promptPin = authorizationService.enablePinPrompt(userObj);
			log.debug("promptPin->" + promptPin);
		}
		log.debug("isChangePassword->" + isChangePassword);
		if (isChangePassword) {
			Cookie userCookie = new Cookie("username", userName);
			Cookie promptPinCookie = new Cookie("promptPin",
					String.valueOf(promptPin));
			response.addCookie(userCookie);
			response.addCookie(promptPinCookie);
			request.getSession().setAttribute("changePassword", true);
		}
		response.sendRedirect(UrlFactory.getIndex());
	}
}

package com.mfino.uicore.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.mfino.uicore.web.UrlFactory;

/**
 * @author Srikanth
 */
public class CustomSessionManagementFilter extends GenericFilterBean {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession(false);
		if (session != null) { // added for the enhancement 2255
			if (checkIfSessionsOverwrite(request)) {
				log.debug("There is a chance for session overwriting , hence session is invalidated");
				session.invalidate();
				response.sendRedirect(UrlFactory.getIndex());
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * Method to check Proper session management and segregation should be
	 * implemented such that multiple roles opened on the same browser does not
	 * cause session overwriting.
	 * 
	 * @param request
	 * @return
	 */
	private boolean checkIfSessionsOverwrite(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth != null) {
			String loggedUserName = auth.getName();
			log.debug("loggedUserName->" + loggedUserName
					+ ";userName in request params->"
					+ request.getParameter("userName"));
			if (request.getParameter("userName") != null) {
				String userName = request.getParameter("userName");
				if (!userName.isEmpty() && !userName.equals(loggedUserName)) {
					return true;
				}
			}
		}
		return false;
	}
}

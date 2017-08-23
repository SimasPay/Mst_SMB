/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.MfinoUser;
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
		MfinoUser userObj = null;
		String userName = authResult.getName();
		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		UserQuery query = new UserQuery();
		query.setUserName(userName);
		List<MfinoUser> results = (List<MfinoUser>) userDAO.get(query);
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
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		String encodedPassword = request.getParameter("j_password");
		String encodedUsername = request.getParameter("j_username");
		String decodedPassword = decode(encodedPassword);
		String decodedUsername = decode(encodedUsername);
		Map<String, String[]> extraParams = new TreeMap<String, String[]>();
		extraParams.put("j_password", new String[]{decodedPassword});
		extraParams.put("j_username", new String[]{decodedUsername});
		HttpServletRequest updatedRequest = new PrettyFacesWrappedRequest(request, extraParams);
		return super.attemptAuthentication(updatedRequest, response);
	}
	
	private String decode(String password){
		StringBuilder decoded = new StringBuilder();
		for (int i = 0; i < password.length(); i++) {
			int ord = (int) password.charAt(i);
			int x = (ord ^ 2591);
			decoded.append(Character.toChars(x));
		}
		return decoded.toString();
	}
	
	/**
	 * http://www.ocpsoft.org/opensource/how-to-safely-add-modify-servlet-request-parameter-values/
	 * @author dimo
	 *
	 */
	public class PrettyFacesWrappedRequest extends HttpServletRequestWrapper
	{
	    private final Map<String, String[]> modifiableParameters;
	    private Map<String, String[]> allParameters = null;

	    /**
	     * Create a new request wrapper that will merge additional parameters into
	     * the request object without prematurely reading parameters from the
	     * original request.
	     * 
	     * @param request
	     * @param additionalParams
	     */
	    public PrettyFacesWrappedRequest(final HttpServletRequest request, 
	                                                    final Map<String, String[]> additionalParams)
	    {
	        super(request);
	        modifiableParameters = new TreeMap<String, String[]>();
	        modifiableParameters.putAll(additionalParams);
	    }

	    @Override
	    public String getParameter(final String name)
	    {
	        String[] strings = getParameterMap().get(name);
	        if (strings != null)
	        {
	            return strings[0];
	        }
	        return super.getParameter(name);
	    }

	    @Override
	    public Map<String, String[]> getParameterMap()
	    {
	        if (allParameters == null)
	        {
	            allParameters = new TreeMap<String, String[]>();
	            allParameters.putAll(super.getParameterMap());
	            allParameters.putAll(modifiableParameters);
	        }
	        //Return an unmodifiable collection because we need to uphold the interface contract.
	        return Collections.unmodifiableMap(allParameters);
	    }

	    @Override
	    public Enumeration<String> getParameterNames()
	    {
	        return Collections.enumeration(getParameterMap().keySet());
	    }

	    @Override
	    public String[] getParameterValues(final String name)
	    {
	        return getParameterMap().get(name);
	    }
	}
}

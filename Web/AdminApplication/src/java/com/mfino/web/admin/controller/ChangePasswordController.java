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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.mfino.exceptions.InvalidPasswordException;
import com.mfino.i18n.MessageText;
import com.mfino.service.PartnerService;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.web.JSONView;

/**
 *
 * @author sunil
 */
@Controller
public class ChangePasswordController {

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
   /* 
    * Commented as part of enhancement #2311
    * 
    * @RequestMapping("/changepassword.htm")
    public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = CookieStore.get(request, "username");
        String promptPin = CookieStore.get(request, "promptPin");        
        if (userName.equals("") || userName.equals(null)) {
            return new ModelAndView("login");
        }
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("promptPin", getScript(promptPin));
        return new ModelAndView("changePassword",map);
    }
    
    private String getScript(String promptPin) {
    	 StringBuffer jsBuf =new StringBuffer("Ext.ns(\"" +"mFino.changeParams"+"\");");
    	 jsBuf.append("\n" + "mFino.changeParams" + "." + "getPinPrompt" + " = function(){\n");
         jsBuf.append("return " + Boolean.parseBoolean(promptPin) + ";\n");
         jsBuf.append("};");
         return jsBuf.toString();
	}

	@RequestMapping("/userchangepassword.htm")
    public ModelAndView userchangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
     try {
    	String userName = UserService.getCurrentUser().getUsername();
        CookieStore.set(response, "username", userName);
        if (userName.equals("") || userName.equals(null)) {
        	return new ModelAndView("login");
        }
        
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("oldPasswordRequired", "Ext.ns(\"" +"mFino.oldPassWordEnabled"+"\");");
        return new ModelAndView("changePassword",map);
     }catch (Exception e) {
		log.error("Error in User Change Password :",e);
	}
     
     return new ModelAndView("login");
    }*/
    
    @RequestMapping("/changepasswordrequest.htm")
    public View changePasswordRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");

        String userName = userService.getCurrentUser().getUsername(); 
		// removed code to retrieve username from cookies as we no longer require
		// cookies as long as we are in same page without redirection unlike
		// before where we used to redirect to changepassword.jsp page
		// Removed code : CookieStore.get(request, "username"); 

        if (type != null) {
            if (type.equals("passwordsave")) {
                return setpassword(request, response, userName);
            }
        }
        return null;
    }

    View setpassword(HttpServletRequest request,
            HttpServletResponse response,
            String username) {
    	log.info("User:"+username+" requested for password change");
        String password = request.getParameter("newpassword");
        String oldpassword = request.getParameter("oldpassword");
        String transactionPin = request.getParameter("transactionPin");
        boolean oldpasswordRequired = "true".equalsIgnoreCase(request.getParameter("oldpasswordRequired"));
        boolean transactionPinRequired = "true".equalsIgnoreCase(request.getParameter("transactionPinRequired"));
        HashMap map = new HashMap();
        //This Code Should never get called as long as user enters username
        if (username == null || password == null) {
            map.put("success", false);
            map.put("Error", String.format(MessageText._("Sorry, Username or Password cannot be Null")));
            return new JSONView(map);
        }
        try {
            //This needs to be changed to username,oldPassword, newPassword
            //Once oldpassword needs to confirmed
            
        	userService.changePassword(username, oldpassword, password, Boolean.FALSE,oldpasswordRequired);
            if(transactionPinRequired){
            	partnerService.changePin(username, transactionPin);
            }
            //once pwd is changed, remove session attribute to not display change pwd window further
            request.getSession().removeAttribute("changePassword");
            map.put("success", true);
            map.put("Error", String.format(" %s : " + MessageText._("Your Password has been sucessfully set"), username));
            log.info("User:"+username+" password changed");
        } catch (Exception exp) {
            if (exp instanceof UsernameNotFoundException) {
                throw (UsernameNotFoundException) exp;
            }if (exp instanceof InvalidPasswordException) {
            	 map.put("success", false);
                 map.put("message", MessageText._(exp.getMessage()));                 
            }  else {
            	log.error("Error getting user " + username, exp);
                throw new DataRetrievalFailureException("error getting user", exp);
            }
        } 
        
        return new JSONView(map);
    }
}

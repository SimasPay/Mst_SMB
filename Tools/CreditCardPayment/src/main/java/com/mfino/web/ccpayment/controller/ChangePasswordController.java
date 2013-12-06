/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.ccpayment.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.mfino.i18n.MessageText;
import com.mfino.service.UserService;
import com.mfino.util.CookieStore;
import com.mfino.util.HibernateUtil;
import com.mfino.web.JSONView;

/**
 *
 * @author admin
 */
@Controller
public class ChangePasswordController {

	private Logger log = LoggerFactory.getLogger(ChangePasswordController.class);
    @RequestMapping("/changepassword.htm")
    public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = CookieStore.get(request, "username");
        if (userName.equals("") || userName.equals(null)) {
            return new ModelAndView("login");
        }
        return new ModelAndView("index");
    }

    @RequestMapping("/changepasswordrequest.htm")
    public View changePasswordRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");

        String userName = CookieStore.get(request, "username");

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
        String password = request.getParameter("newpassword");
        HashMap map = new HashMap();
        //This Code Should never get called as long as user enters username
        if (username == null || password == null) {
            map.put("success", false);
            map.put("Error", String.format(MessageText._("Sorry, Username or Password cannot be Null")));
            return new JSONView(map);
        }
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            //This needs to be changed to username,oldPassword, newPassword
            //Once oldpassword needs to confirmed
            UserService.changePassword(username, password, password, Boolean.FALSE);
            map.put("success", true);
            map.put("Error", String.format(" %s : " + MessageText._("Your Password has been sucessfully set"), username));
            HibernateUtil.getCurrentSession().getTransaction().commit();
        } catch (Exception e) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            if (e instanceof UsernameNotFoundException) {
                throw (UsernameNotFoundException) e;
            } else {
                log.error("Error getting user " + username, e);
                throw new DataRetrievalFailureException("error getting user", e);
            }
        }

        return new JSONView(map);
    }
}

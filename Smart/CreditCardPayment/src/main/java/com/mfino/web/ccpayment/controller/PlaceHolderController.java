/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.ccpayment.controller;

import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author admin
 */
@Controller
public class PlaceHolderController {

    @RequestMapping("/index.htm")
    public ModelAndView index() {

        String userString = StringUtils.EMPTY;
        String i18nJSPath = StringUtils.EMPTY;
        String authFileName = StringUtils.EMPTY;
        /*     try {
        HibernateUtil.getCurrentSession().beginTransaction();
        userString = UserService.getUserString();
        i18nJSPath = "/js/message/msg." + UserService.getUserLanguageCode() + ".js";
        authFileName = "authorization.jsx?" + CacheBuster.getTimeStamp();
        HibernateUtil.getCurrentTransaction().commit();
        } catch (Exception ex) {
        HibernateUtil.getCurrentTransaction().rollback();
        DefaultLogger.error("Error in index controller", ex);
        }*/

        HashMap map = new HashMap();
        map.put("userString", userString);
        map.put("i18nJSPath", i18nJSPath);
        map.put("authFileName", authFileName);
        return new ModelAndView("index", map);
    }

    @RequestMapping("/secure/debug.htm")
    public ModelAndView deubg() {
        return new ModelAndView("secure/debug");
    }
}

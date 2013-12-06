/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.web.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.mfino.service.JSAuthGeneratorService;
import com.mfino.uicore.web.TextView;

/**
 *
 * @author Siddhartha Chinthapally
 */
@Controller
public class AuthorizationController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
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
}

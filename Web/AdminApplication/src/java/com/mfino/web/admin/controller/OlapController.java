/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.mfino.report.olap.processor.UpdateReportDbUser;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author Pradeep
 */
@Controller
public class OlapController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @RequestMapping("/olap.htm")
    public View changePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        String olapUrl = ConfigurationUtil.olapUrl();
     	String username = request.getParameter("userName");
    	UpdateReportDbUser ub = new UpdateReportDbUser();
		String password = ub.update(username);
		String url = olapUrl+"/home.html?username="+username+"&dynamictoken="+password;
		//String url = "http://olapserver:8080/saiku/home.html?username="+username+"&dynamictoken="+password;
    	HashMap map = new HashMap();
	    map.put("success", true);
	    map.put("url", url);
	    return new JSONView(map);
    }
    
    public static void main(String args[]) throws FileNotFoundException, IOException{
    	Properties prop = new Properties();     
    	prop.load(new FileInputStream("olap.properties"));
        String olapUrl = prop.getProperty("olapUrl");
        System.out.println(olapUrl);
    }
    

	
   
}

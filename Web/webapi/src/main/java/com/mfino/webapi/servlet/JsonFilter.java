package com.mfino.webapi.servlet;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.webapi.json.JsonResponseWrapper;

/**
 * Converts xml to json if query parameter callback is available.
 */

public class JsonFilter implements Filter {
   
	private static Logger	log = LoggerFactory.getLogger(JsonFilter.class);
	
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
	            throws IOException, ServletException {
	        if (res instanceof HttpServletResponse) {
	            HttpServletResponse response = (HttpServletResponse) res;
	            if (req.getParameter("callback")!=null) {
	            	log.info("Converting xml to json");
	                JsonResponseWrapper wrappedResponse = new JsonResponseWrapper(response);
	                chain.doFilter(req, wrappedResponse);
	                wrappedResponse.convertToJson();
	            } else {
	                chain.doFilter(req, res);
	            }
	        }
    }
 
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
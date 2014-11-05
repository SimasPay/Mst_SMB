package com.mfino.mock.bsimsms;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Amar
 *
 */
public class NewBSIMSMSServiceMock extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public NewBSIMSMSServiceMock() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println("NewBSIMSMSServiceMock GET method called");
				
//		String[] tokens = parameterString.split("/");
		String[] tokens = request.getPathInfo().substring(1).split("/");
		String systemId = tokens[0];
		String tokenId = tokens[1];
		String mdn = tokens[2];
		String encodedMessage = tokens[3];
		String message = null;
		if(encodedMessage != null)
		{
			message = URLDecoder.decode(encodedMessage, "UTF-8" );
		}
		
		
		
		System.out.println("**systemId="+systemId+", tokenId="+tokenId+", to="+mdn+", message="+message);
		
		String code = "100";
		String text = "";
		
		if((null == systemId) || ("".equals(systemId))){
			code = "101";
			text = "systemId is not set";
		}
		else if((null == tokenId) || ("".equals(tokenId))){
			code = "102";
			text = "tokenId is not set";
		}
		else if((null == mdn) || ("".equals(mdn))){
			code = "104";
			text = "mdn is not set";
		}
		else{
			code = "00";
			text = "null";
		}
		
		
		String jsonString = "{\"errorMessage\":" + text + ",\"responseCode\":\"" + code + "\"}";		
		System.out.println("Writing response, jsonString=" + jsonString);
		
		response.getWriter().print(jsonString);
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("NewBSIMSMSServiceMock POST method called");
		doGet(request, response);
	}

}

package com.mfino.mock.bsimsms;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Amar
 *
 */
public class BSIMSMSServiceMock extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public BSIMSMSServiceMock() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("BSIMSMSServiceMock GET method called");
		
		String partnerID = request.getParameter("partnerID");
		String apiToken = request.getParameter("apiToken");
		String shortcode = request.getParameter("shortcode");
		String to = request.getParameter("to");
		String encodedMessage = request.getParameter("message");
		String message = null;
		if(encodedMessage != null)
		{
			message = URLDecoder.decode(encodedMessage, "UTF-8" );
		}
		
		System.out.println("**partnerID="+partnerID+", apiToken="+apiToken+", shortcode="+shortcode+", to="+to+", message="+message);
		
		String code = "100";
		String text = "";
		
		if((null == partnerID) || ("".equals(partnerID))){
			code = "101";
			text = "partnerID is not set";
		}
		else if((null == apiToken) || ("".equals(apiToken))){
			code = "102";
			text = "apiToken is not set";
		}
		else if((null == shortcode) || ("".equals(shortcode))){
			code = "103";
			text = "shortcode is not set";
		}
		else if((null == to) || ("".equals(to))){
			code = "104";
			text = "toAddress is not set";
		}
		else if(!partnerID.equals("11")){
			code = "105";
			text = "partnerID is not valid";
		}
		else if(!apiToken.equals("qwerty12345")){
			code = "106";
			text = "apiToken is not valid";
		}
		else if(!shortcode.equals("988012")){
			code = "107";
			text = "shortCode is not valid";
		}
		else{
			code = "202";
			text = "0: Accepted for delivery ";
		}
		
		
		//String xmlString = "<response><message code=\""+ code +"\">"+ text +"</message><transactionid>123456</transactionid></response>";	
		String xmlString = "<response><message>"+ message +"</message><responseCode>" + code + "</responseCode><transactionid>123456</transactionid></response>";		
		
		System.out.println("Writing response, xmlString=" + xmlString);
		
		//response.getOutputStream().print(xmlString);
		response.getWriter().print(xmlString);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("VisafoneAirtimePurchaseService POST method called");
		doGet(request, response);
	}

}

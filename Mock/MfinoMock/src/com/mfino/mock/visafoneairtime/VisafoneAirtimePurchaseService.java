package com.mfino.mock.visafoneairtime;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class VisafoneAirtimePurchaseMockServlet
 */
public class VisafoneAirtimePurchaseService extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public VisafoneAirtimePurchaseService() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("VisafoneAirtimePurchaseService GET method called");
		
//		msisdn=A&account=B&value=C&transid=D
		String msisdn = request.getParameter("msisdn");
		String account = request.getParameter("account");
		String value = request.getParameter("value");
		String transid = request.getParameter("transid");
		String vendorid = request.getParameter("vendorid");
		String password = request.getParameter("password");
		
		System.out.println("**msisdn="+msisdn+", account="+account+", value="+value+", transid="+transid+", vendorid="+vendorid+", password="+password);
		
		String code = "1000";
		
		if((null == transid) || ("".equals(transid))){
			code = "1001";
		}
		else if((msisdn.startsWith("9866"))){
			code = "1002";
		}
		else if(((msisdn.startsWith("8888"))) && (account.equals("2000"))){
			code = "1003";
		}
		else if(((msisdn.startsWith("9999"))) && (account.equals("2000"))){
			code = "1003";
		}
		else if(((msisdn.startsWith("9999"))) && (account.equals("3000"))){
			code = "1004";
		}
		else{
			code = "1000";
		}
		
		System.out.println("Writing response, code="+code);
		
		response.getOutputStream().print(code);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("VisafoneAirtimePurchaseService POST method called");
		doGet(request, response);
	}

}

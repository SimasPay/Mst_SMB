package com.mfino.ccpayment.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;

/**
 * Servlet implementation class CancellationServlet
 */
public class CancellationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());  
	private static final String success="0";
	private static final String fail="1";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CancellationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/paymentResponse.jsp");
		String responseString;
		try{
		HibernateUtil.getCurrentSession().beginTransaction();
		Long cctransactionid = Long.parseLong(request.getParameter("TRANSIDMERCHANT"));
		CreditCardTransactionDAO dao = new CreditCardTransactionDAO();
		CreditCardTransaction record = dao.getById(cctransactionid);
		log.info("In Cancellation Servlet");
		log.info("Credit Card Transaction ID" +  cctransactionid);
		if(record!=null && CmFinoFIX.TransStatus_Verified.equals(record.getTransStatus())){			
		log.info("Request for cancellation from nsia with cctransaction id "+  cctransactionid);
	    record.setTransStatus(CmFinoFIX.TransStatus_Cancelled);
		record.setCCFailureReason(CmFinoFIX.CCFailureReason_User_Cancelled_The_Transaction);
		dao.save(record);
		HibernateUtil.getCurrentTransaction().commit();
		responseString = "You have successfully cancelled you transaction.";
		request.setAttribute("resultMsg", responseString);
		request.setAttribute("resultCode", success);		
		dispatcher.forward(request, response);
		}
		else {
			log.info("Invalid Request for cancellation from nsia with cctransaction id "+  cctransactionid);
		}		
		}catch (Exception err) {
			HibernateUtil.getCurrentTransaction().rollback();
            log.error("Exception Occured in cancellation servlet", err);
            responseString = "Error occured while cancelling your transaction.";
    		request.setAttribute("resultMsg", responseString);
    		request.setAttribute("resultCode", fail);
            dispatcher.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

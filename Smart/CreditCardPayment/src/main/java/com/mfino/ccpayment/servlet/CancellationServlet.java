package com.mfino.ccpayment.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.session.HibernateSessionHolder;

/**
 * Servlet implementation class CancellationServlet
 */
public class CancellationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(CancellationServlet.class);  
	private static final String success="0";
	private static final String fail="1";

	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}

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

		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

		try{
			cancelTransaction(request, response, dispatcher);		
		}catch (Exception e) {
			log.info("Exception Occured in cancellation servlet"+ e);
			responseString = "Error occured while cancelling your transaction.";
			request.setAttribute("resultMsg", responseString);
			request.setAttribute("resultCode", fail);
			dispatcher.forward(request, response);
		}
		finally
		{
			if(session!=null)
			{
				session.close();
			}
		}
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void cancelTransaction(HttpServletRequest request,
			HttpServletResponse response, RequestDispatcher dispatcher)
			throws ServletException, IOException {
		String responseString;
		Long cctransactionid = Long.parseLong(request.getParameter("TRANSIDMERCHANT"));
		CreditCardTransactionDAO dao = DAOFactory.getInstance().getCreditCardTransactionDAO();
		CreditCardTransaction record = dao.getById(cctransactionid);
		log.info("In Cancellation Servlet");
		log.info("Credit Card Transaction ID" +  cctransactionid);
		if(record!=null && CmFinoFIX.TransStatus_Verified.equals(record.getTransStatus())){			
			log.info("Request for cancellation from nsia with cctransaction id "+  cctransactionid);
			record.setTransStatus(CmFinoFIX.TransStatus_Cancelled);
			record.setCCFailureReason(CmFinoFIX.CCFailureReason_User_Cancelled_The_Transaction);
			dao.save(record);
			responseString = "You have successfully cancelled you transaction.";
			request.setAttribute("resultMsg", responseString);
			request.setAttribute("resultCode", success);		
			dispatcher.forward(request, response);
		}
		else {
			log.info("Invalid Request for cancellation from nsia with cctransaction id "+  cctransactionid);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.ccpayment.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.ccpayment.util.IPFilterting;
import com.mfino.ccpayment.util.MessageDigestEncoder;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoCreditCardUtil;
/**
 *
 * @author Maruthi
 */
public class NSIANotifyEduServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	private static Logger log = LoggerFactory.getLogger(NSIANotifyEduServlet.class);

	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		if(!IPFilterting.validip(request.getRemoteAddr())){
			out.println("Stop");
			log.info("IP Check Failed sending stop request to the NSIA. Request came from=" +request.getRemoteAddr());
			return;
		}
		try
		{
			Long invoiceNo=Long.parseLong(request.getParameter("invoiceNo"));
			String reason = request.getParameter("reason");
			String ref = request.getParameter("ref");
			String status = request.getParameter("status"); 
			log.info("Parameters: invoiceno="+invoiceNo + " reason="+reason+" ref="+ref+" status="+status);
			String merchantId = ConfigurationUtil.getCreditcardMerchantid();
			String sharedKey = ConfigurationUtil.getCreditcardTransactionPassword();

			String signature = merchantId + sharedKey + invoiceNo;
			try {
				signature = MessageDigestEncoder.SHA1(signature);
			} catch (NoSuchAlgorithmException e) {
				log.info("could not get signature", e);
				log.info("Sending stop response to NSIA");
				out.println("Stop");
				return;
			}
			if(StringUtils.isBlank(reason)){
				log.info("reason is null");
				log.info("Sending stop response to NSIA");
				out.println("Stop");
				return;                	
			}
			if(!signature.equals(ref)) {
				log.info("Invalid Signature " + signature);
				log.info("Sending stop response to NSIA");
				out.println("Stop");
				return;
			}

			Session session = sessionFactory.openSession();
			hibernateSessionHolder.setSession(session);		
			DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);
			try
			{
				if(status!=null && status.equals("VOID")){
					processVoidRequest(out, invoiceNo, reason, ref, status);
				}
				else if(status!=null && status.equals("PASS")){
					processPassRequest(out, invoiceNo, reason, ref, status);

				}else{
					log.info("Invalid request from NSIA with invioceNO="+invoiceNo+" reason="+reason+" status="+status+" ref="+ref);
					log.info("Sending Stop to the NSIA");
					out.println("Stop");                	
				}
			}
			finally
			{
				if(session!=null)
				{
					session.close();
				}
			}
		}catch(Exception e){
			log.info("Exception occured so");
			log.info("Sending Stop to the NSIA");
			out.println("Stop");
			log.info("Exception is" , e);        	
		}

	}


	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void processPassRequest(PrintWriter out, Long invoiceNo,
			String reason, String ref, String status) {
		CreditCardTransactionDAO dao = DAOFactory.getInstance().getCreditCardTransactionDAO();
		CreditCardTransaction record =dao.getById(invoiceNo);
		if(record==null){
			log.info("Record not found for request from NSIA with invioceNO= "+invoiceNo+" reason="+reason+" status="+status+" ref="+ref);
			out.println("Stop");
		}                    
		else if(record.getTransStatus().equals(CmFinoFIX.TransStatus_NSIA_EDU_Data_Sent)){
			record.setTransStatus(CmFinoFIX.TransStatus_NSIA_EDU_Confirmed); 
			record.setNSIATransCompletionTime(new Timestamp());  
			record.setIsVoid(Boolean.FALSE);
			dao.save(record);
			log.info("Updating the status to NSIA EDU Confirmed for" + invoiceNo);
			out.println("Continue");
			log.info("Sending continue to NSIAPay for invoiceno = "+invoiceNo);
		}
		else{
			log.info("For status PASS");
			log.info("transactionStatus  is not edu datasent"+record.getID());	
			out.println("Stop");
			log.info("Sending stop to the NSIA for invoiceno ="+ invoiceNo);
		}
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void processVoidRequest(PrintWriter out, Long invoiceNo,
			String reason, String ref, String status) {
		CreditCardTransactionDAO dao = DAOFactory.getInstance().getCreditCardTransactionDAO();
		CreditCardTransaction record =dao.getById(invoiceNo);
		if(record==null){
			log.info("Record not found for request from NSIA with invioceNO="+invoiceNo+" reason="+reason+" status="+status+" ref="+ref);
			out.println("Stop");
			return;
		}else if(record.getTransStatus().equals(CmFinoFIX.TransStatus_NSIA_EDU_Data_Sent)){
			out.println("Continue");
			log.info("Failing the tranaction as void request came from NSIA");
			log.info("updating the failure reason to nsiaeduvoidrequest");
			record.setTransStatus(CmFinoFIX.TransStatus_Failed);
			record.setCCFailureReason(CmFinoFIX.CCFailureReason_NSIAEDU_VOID_Request);
			record.setIsVoid(Boolean.TRUE);
			record.setVoidBy(ConfigurationUtil.getCreditcardGatewayName());
			dao.save(record);
			log.info("sending void notification to user via sms");
			SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
			Subscriber subscriber= subscriberDAO.getById(record.getSubscriber().getID());
			Set<SubscriberMDN> subscriberMDN = subscriber.getSubscriberMDNFromSubscriberID();
			if(!subscriberMDN.isEmpty()){
				String mdn = subscriberMDN.iterator().next().getMDN();
				String msg = MfinoCreditCardUtil.generateSMSMessage(record, invoiceNo.toString(), CmFinoFIX.CCFailureReason_NSIAEDU_VOID_Request);
				MfinoCreditCardUtil.sendSMS(mdn, msg);
			}
			String emailMsg = MfinoCreditCardUtil.generateMessageBody(record, CmFinoFIX.TransStatus_Failed);
			if(subscriber.getUserBySubscriberUserID()!=null)
				MfinoCreditCardUtil.sendTransferMail(subscriber.getUserBySubscriberUserID().getEmail(), emailMsg);
		}
		else {
			//if transaction is not data sent what to do
			log.info("For status VOID");
			log.info("transactionStatus is not NSIA_EDU_Data_Sent"+record.getID());
			log.info("Sending stop response to NSIA for invoiceno ="+ invoiceNo);
			out.println("Stop");            			
		}
	}


	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void  doGet  (HttpServletRequest request, HttpServletResponse response)
			throws ServletException,
			IOException
			{
		processRequest(request, response);
			}
	/**
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,
			IOException
			{
		processRequest(request, response);
			}
	/**
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}

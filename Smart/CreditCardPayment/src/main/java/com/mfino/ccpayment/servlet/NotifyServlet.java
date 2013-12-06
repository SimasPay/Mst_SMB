/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import com.mfino.cc.message.CCNotifyMessage;
import com.mfino.cc.message.CCPaymentInput;
import com.mfino.ccpayment.util.IPFilterting;
import com.mfino.ccpayment.util.MessageDigestEncoder;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.SMSService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoCreditCardUtil;

/**
 *
 * @author raju
 */
public class NotifyServlet extends HttpServlet {

	/**
	 * 
	 */	
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(NotifyServlet.class);
	private static ExecutorService threadPool = Executors.newFixedThreadPool(10);  

	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}


	/**
	 * Processes requests for both HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

		CreditCardTransactionDAO dao = DAOFactory.getInstance().getCreditCardTransactionDAO();
		CreditCardTransaction record = new CreditCardTransaction();
		CCNotifyMessage notifyMessage = new CCNotifyMessage();
		try{            
			notifyMessage = processRequest(request, response, record, dao);
		} catch (Exception ex) {
			if(record!=null){
				//change failure reason
				record.setTransStatus(CmFinoFIX.TransStatus_Failed);
				record.setCCFailureReason(CmFinoFIX.CCFailureReason_Failed_Other);
				notifyMessage.setIsSmsRequired(true);
				notifyMessage.setFailedAt(CmFinoFIX.CCFailureReason_Failed_Other);
			}
			log.info("Exception Occured", ex);
			log.info("Sending failed because of exception at our end");
			PrintWriter out = response.getWriter();
			out.println("Stop");            
		}
		finally{
			try{
				processMessage(dao, record, notifyMessage);
			}
			finally
			{
				if(session!=null)
				{
					session.close();
				}
			}
		}
	}


	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void processMessage(CreditCardTransactionDAO dao,
			CreditCardTransaction record, CCNotifyMessage notifyMessage) {
		if(record!=null && !notifyMessage.getOriginalStatus().equalsIgnoreCase(CmFinoFIX.TransStatus_Failed)){
			//no need to update failed transactions
			dao.save(record);
			if(notifyMessage.getIsSmsRequired()){
				log.info("sending fail notification email to user");  
				String emailMsg = MfinoCreditCardUtil.generateMessageBody(record, CmFinoFIX.TransStatus_Failed);
				MfinoCreditCardUtil.sendTransferMail(notifyMessage.getEmail(),emailMsg);

				log.info("sending fail notification sms to user");        		
				String msg =  MfinoCreditCardUtil.generateSMSMessage(record, notifyMessage.getTransactionId(),notifyMessage.getFailedAt());        		
				MfinoCreditCardUtil.sendSMS(notifyMessage.getMdn(), msg);
			}
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
			throws ServletException, IOException {
		//     processRequest(request, response);
	}

	/** 
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

	private void asyncSendSMS(final SMSService service){ 	
		threadPool.execute(new Runnable() {			
			@Override
			public void run(){
				log.info("ThreadPool ID: " + threadPool.toString());		
				service.send();			
			}
		});

	}
	@Override
	public void destroy() {
		super.destroy();
		log.info("ThreadPool ID: " + threadPool.toString());		
		threadPool.shutdown();    	
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	private CCNotifyMessage processRequest(HttpServletRequest request, HttpServletResponse response, CreditCardTransaction record, CreditCardTransactionDAO dao) throws IOException
	{
		CCNotifyMessage notifyMessage = new CCNotifyMessage();


		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		//commit in finally so strated session here

		if(!IPFilterting.validip(request.getRemoteAddr())){
			out.println("Stop");
			log.info("IP check failed sending stop request to the NSIA. Request came from= " +request.getRemoteAddr());
			return notifyMessage;
		}

		Long ccTransactionID = Long.parseLong(request.getParameter("OrderNumber"));
		String responseCode = request.getParameter("RESPONSECODE");
		String approvalCode = request.getParameter("APPROVALCODE");
		String sessionID = request.getParameter("SESSIONID");
		String cardNo = request.getParameter("CARDNUMBER");
		String bank = request.getParameter("BANK");
		String result = request.getParameter("RESULT");
		String words = request.getParameter("WORDS");
		CCPaymentInput ccPaymentInput = new CCPaymentInput();

		String signature = null;

		log.info("ccTransactionID/OrderNumber:" + ccTransactionID + " responseCode:" + responseCode + " approvalCode:" + approvalCode + " sessionID " + sessionID
				+"cardNo:" + cardNo + " bank:" + bank + " result:" + result + " words:" + words);

		record = dao.getById(ccTransactionID);
		String cardNumber=null;
		if(record!=null){
			cardNumber = record.getCardNoPartial();
			cardNumber = cardNumber.charAt(0) + "***********" + cardNumber.substring(12, 16);
			notifyMessage.setTransactionId(record.getID().toString());
			SubscriberDAO subdao = DAOFactory.getInstance().getSubscriberDAO();
			Subscriber sub = subdao.getById(record.getSubscriber().getID());
			notifyMessage.setEmail(sub.getUserBySubscriberUserID().getEmail());
			User user = sub.getUserBySubscriberUserID();
			notifyMessage.setMdn(user.getUsername());
			notifyMessage.setOriginalStatus(record.getTransStatus());
		}
		if (record == null) {
			log.info("Invalid credit card transaction id=" + ccTransactionID);
			log.info("Sending stop Response to the NSIA gateway");
			out.println("Stop");
			return notifyMessage;               
		}else if (!CmFinoFIX.TransStatus_Verified.equalsIgnoreCase(record.getTransStatus())) {
			log.info("Notify for transactionid  " + record.getID() + " with invalid status " +record.getTransStatus());
			log.info("Sending stop response to the NSIA gateway");
			record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_TransferStatus);
			record.setTransStatus(CmFinoFIX.TransStatus_Failed);
			//               dao.save(record);
			notifyMessage.setIsSmsRequired(true);      
			notifyMessage.setFailedAt(CmFinoFIX.CCFailureReason_Invalid_CreditCard_TransferStatus);
			out.println("Stop");
			return notifyMessage;
		} else if (!cardNo.equals(cardNumber)) {
			log.info("Invalid card for cc record id" + ccTransactionID+"DB card pan:"+ cardNumber + " NSIA card no" + cardNo);
			record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_CardNumber);
			record.setTransStatus(CmFinoFIX.TransStatus_Failed);
			//               dao.save(record);
			notifyMessage.setIsSmsRequired(true);
			notifyMessage.setFailedAt(CmFinoFIX.CCFailureReason_Invalid_CreditCard_CardNumber);
			log.info("Sending stop response to the NSIA gateway");
			out.println("Stop");
			return notifyMessage;
		}else if(!sessionID.equals(record.getSessionID())){
			record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_SessionID);
			record.setTransStatus(CmFinoFIX.TransStatus_Failed);
			out.println("Stop");
			log.info("Invalid session id " + sessionID + " for cc record id" + record.getID());
			//               dao.save(record);
			notifyMessage.setIsSmsRequired(true);
			notifyMessage.setFailedAt(CmFinoFIX.CCFailureReason_Invalid_SessionID);
			log.info("Sending stop response to the NSIA gateway");
			return notifyMessage;
		}else {
			signature = record.getAmount() + ".00" + ccPaymentInput.getMerchantID() + ccPaymentInput.getTransactionPwd() + ccTransactionID + result;
			try {
				signature = MessageDigestEncoder.SHA1(signature);
			} catch (NoSuchAlgorithmException e) {
				log.info("could not get signature", e);                    
				log.info("Sending stop response as it failed at sha calucation for transactionid "+record.getID());
				record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature);
				record.setTransStatus(CmFinoFIX.TransStatus_Failed);
				notifyMessage.setIsSmsRequired(true);
				notifyMessage.setFailedAt(CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature);
				out.println("Stop");
				return notifyMessage;
			}
			if (!words.equals(signature)) {
				record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature);
				record.setTransStatus(CmFinoFIX.TransStatus_Failed);
				out.println("Stop");
				log.info("Invalid signature found " + words + "instead of " + signature+" for transactionid "+record.getID());
				//                   dao.save(record);
				notifyMessage.setIsSmsRequired(true);
				notifyMessage.setFailedAt(CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature);
				log.info("Sending Stop Response to the NSIA Gateway");
				return notifyMessage;
			}
		}

		record.setAcquirerBank(bank);
		record.setBankResCode(responseCode);
		record.setAuthID(approvalCode); //authid is approval code from the bank
		record.setBankResMsg(result);
		if (result.equalsIgnoreCase("Success")) {
			record.setTransStatus(CmFinoFIX.TransStatus_Notified);
			record.setNSIATransCompletionTime(new Timestamp());
			String messageToSend = null;
			log.info("Updating the  Transaction  with Notified for record id " + ccTransactionID);
			if (record.getAmount().compareTo(new BigDecimal(ConfigurationUtil.getCreditcardMaximumAmountLimit())) > 0) {
				log.info("Alerting the subscriber about the tranasction Amount");
				SMSService service = new SMSService();
				messageToSend = ConfigurationUtil.getCreditcardMaximumAmountAlertMessage();
				messageToSend = StringUtils.replace(messageToSend, "${Amount}",	record.getAmount().toString());
				messageToSend = StringUtils.replace(messageToSend, "${CCTransactionID}",record.getID().toString());
				try {
					//send SMS using runnable interface
					SubscriberMDN submdn = (SubscriberMDN) record.getSubscriber().getSubscriberMDNFromSubscriberID().toArray()[0];
					service.setDestinationMDN(submdn.getMDN());
					service.setSourceMDN(ConfigurationUtil.getCCCodeNotificationSource());
					service.setSmsc(ConfigurationUtil.getCCCodeNotificationSMSC());
					service.setMessage(messageToSend);
					asyncSendSMS(service);
				} catch (Exception ee) {
					log.error("Failed to send SMS", ee);
				}
				log.info("Saving the records as NSIA_EDU_Pending as amount is greater than maximumamountlimit" + ccTransactionID);    				
				record.setTransStatus(CmFinoFIX.TransStatus_NSIA_EDU_Pending);
			}
			//               dao.save(record);
			out.println("Continue");                  
		} else {
			log.info("Failed result code recieved for cc record id" + ccTransactionID);
			log.info("Updating the transaction status to failed");
			log.info("Sending Stop response to the NSIA gateway");
			record.setTransStatus(CmFinoFIX.TransStatus_Failed);
			record.setCCFailureReason(CmFinoFIX.CCFailureReason_Failed_At_NSIA);
			//               dao.save(record);
			notifyMessage.setIsSmsRequired(true);
			notifyMessage.setFailedAt(CmFinoFIX.CCFailureReason_Failed_At_NSIA);

			out.println("Stop");                
		}
		return notifyMessage;
	}


}

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.cc.message.CCPaymentInput;
import com.mfino.ccpayment.util.IPFilterting;
import com.mfino.ccpayment.util.MessageDigestEncoder;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.SMSService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
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
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static ExecutorService threadPool = Executors.newFixedThreadPool(10);  
	
    /**
     * Processes requests for both HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	 CreditCardTransactionDAO dao = new CreditCardTransactionDAO();
    	 Boolean isSmsRequired = false; 
    	 Integer failedAt = null;
    	 CreditCardTransaction record = null;
    	 String transactionId = null;
    	 String mdn = null;
    	 String email = null;
    	 String originalStatus = CmFinoFIX.TransStatus_Failed;
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //commit in finally so strated session here
        HibernateUtil.getCurrentSession().beginTransaction();
         
        if(!IPFilterting.validip(request.getRemoteAddr())){
        	out.println("Stop");
        	log.info("IP check failed sending stop request to the NSIA. Request came from= " +request.getRemoteAddr());
        	return;
        }
        try {
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
            	transactionId = record.getID().toString();
            	SubscriberDAO subdao = new SubscriberDAO();
        		Subscriber sub = subdao.getById(record.getSubscriber().getID());
        		email = sub.getUserBySubscriberUserID().getEmail();
        		User user = sub.getUserBySubscriberUserID();
        		mdn = user.getUsername();
        		originalStatus = record.getTransStatus();
            }
            if (record == null) {
				log.info("Invalid credit card transaction id=" + ccTransactionID);
				log.info("Sending stop Response to the NSIA gateway");
				out.println("Stop");
				return;               
            }else if (!CmFinoFIX.TransStatus_Verified.equalsIgnoreCase(record.getTransStatus())) {
            	log.info("Notify for transactionid  " + record.getID() + " with invalid status " +record.getTransStatus());
                log.info("Sending stop response to the NSIA gateway");
            	record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_TransferStatus);
                record.setTransStatus(CmFinoFIX.TransStatus_Failed);
//                dao.save(record);
                isSmsRequired = true;      
                failedAt = CmFinoFIX.CCFailureReason_Invalid_CreditCard_TransferStatus;
            	out.println("Stop");
                return;
            } else if (!cardNo.equals(cardNumber)) {
            	 log.info("Invalid card for cc record id" + ccTransactionID+"DB card pan:"+ cardNumber + " NSIA card no" + cardNo);
                record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_CardNumber);
                record.setTransStatus(CmFinoFIX.TransStatus_Failed);
//                dao.save(record);
                isSmsRequired = true;
                failedAt = CmFinoFIX.CCFailureReason_Invalid_CreditCard_CardNumber;
                log.info("Sending stop response to the NSIA gateway");
                out.println("Stop");
                return;
            }else if(!sessionID.equals(record.getSessionID())){
            	record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_SessionID);
                record.setTransStatus(CmFinoFIX.TransStatus_Failed);
                out.println("Stop");
                log.info("Invalid session id " + sessionID + " for cc record id" + record.getID());
//                dao.save(record);
                isSmsRequired = true;
                failedAt = CmFinoFIX.CCFailureReason_Invalid_SessionID;
                log.info("Sending stop response to the NSIA gateway");
                return;
            }else {
                signature = record.getAmount() + ".00" + ccPaymentInput.getMerchantID() + ccPaymentInput.getTransactionPwd() + ccTransactionID + result;
                try {
                    signature = MessageDigestEncoder.SHA1(signature);
                } catch (NoSuchAlgorithmException e) {
                    log.error("could not get signature", e);                    
                    log.info("Sending stop response as it failed at sha calucation for transactionid "+record.getID());
                    record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature);
                    record.setTransStatus(CmFinoFIX.TransStatus_Failed);
                    isSmsRequired = true;
                    failedAt = CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature;
                    out.println("Stop");
                    return;
                }
                if (!words.equals(signature)) {
                    record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature);
                    record.setTransStatus(CmFinoFIX.TransStatus_Failed);
                    out.println("Stop");
                    log.info("Invalid signature found " + words + "instead of " + signature+" for transactionid "+record.getID());
//                    dao.save(record);
                    isSmsRequired = true;
                    failedAt = CmFinoFIX.CCFailureReason_Invalid_CreditCard_Signature;
                    log.info("Sending Stop Response to the NSIA Gateway");
                    return;
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
//                if (record.getAmount() > Long.parseLong(ConfigurationUtil.getCreditcardMaximumAmountLimit())) {
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
//                dao.save(record);
                out.println("Continue");                  
            } else {
            	log.info("Failed result code recieved for cc record id" + ccTransactionID);
            	log.info("Updating the transaction status to failed");
            	log.info("Sending Stop response to the NSIA gateway");
                record.setTransStatus(CmFinoFIX.TransStatus_Failed);
                record.setCCFailureReason(CmFinoFIX.CCFailureReason_Failed_At_NSIA);
//                dao.save(record);
                isSmsRequired = true;
                failedAt = CmFinoFIX.CCFailureReason_Failed_At_NSIA;
                out.println("Stop");                
            }            
        } catch (Exception ex) {
        	if(record!=null){
        		//change failure reason
        		record.setTransStatus(CmFinoFIX.TransStatus_Failed);
                record.setCCFailureReason(CmFinoFIX.CCFailureReason_Failed_Other);
                isSmsRequired = true;
                failedAt = CmFinoFIX.CCFailureReason_Failed_Other;
        	}
            log.error("Exception Occured", ex);
            log.info("Sending failed because of exception at our end");
            out.println("Stop");            
        }
        finally{
        	if(record!=null && !originalStatus.equalsIgnoreCase(CmFinoFIX.TransStatus_Failed)){
        		//no need to update failed transactions
        		dao.save(record);
        	if(isSmsRequired){
        		log.info("sending fail notification email to user");  
        		String emailMsg = MfinoCreditCardUtil.generateMessageBody(record, CmFinoFIX.TransStatus_Failed);
        		MfinoCreditCardUtil.sendTransferMail(email,emailMsg);
        		
        		log.info("sending fail notification sms to user");        		
        		String msg =  MfinoCreditCardUtil.generateSMSMessage(record, transactionId,failedAt);        		
        		MfinoCreditCardUtil.sendSMS(mdn, msg);
        	}
        	}
        	HibernateUtil.getCurrentTransaction().commit();        	
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
//    public static void main(String[] args) {
//		for(int i=0;i<100;i++){
//			asyncSendSMS(i);
//		}
//	}

    
}

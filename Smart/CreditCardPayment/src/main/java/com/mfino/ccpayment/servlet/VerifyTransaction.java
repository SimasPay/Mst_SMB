/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.ccpayment.util.IPFilterting;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author deva
 */
public class VerifyTransaction extends HttpServlet {
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(VerifyTransaction.class);
//	 private String transactionId = null;
//	    private String mdn = null;
//	    private String email = null;
//	    private Boolean isSmsRequired = false;
//	    private CreditCardTransaction record = null;
	
    /** 
     * Processes requests for both HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //commit in finally so strated session here
        HibernateUtil.getCurrentSession().beginTransaction();
        if(!IPFilterting.validip(request.getRemoteAddr())){
        	out.println("Stop");
        	log.info("IP check failed sending stop request to the NSIA. Request came from IP= " +request.getRemoteAddr());
        	return;
        }
        try {
           Long cctransacionid  = Long.parseLong(request.getParameter("TRANSIDMERCHANT"));
           Double amt  = Double.parseDouble(request.getParameter("AMOUNT"));
           String currencycode = request.getParameter("CURRENCY");
           String sessionid = request.getParameter("SESSIONID");
           log.info("Parameters send for verify are CCTransacionid=" + cctransacionid + " amount=" + amt + " CurrencyCode="+currencycode + " SessionID=" + sessionid);
           CreditCardTransactionDAO dao = new CreditCardTransactionDAO();           
           CreditCardTransaction record =dao.getById(cctransacionid);
           if(record!=null&& amt.equals(record.getAmount()) &&CmFinoFIX.TransStatus_Requested.equals(record.getTransStatus()) && currencycode.equals(record.getCurrCode()) && sessionid.equals(record.getSessionID())){
               log.info("Valid transaction responding with CONTINUE");
               log.info("Updating the status to Verified for tranasctionid= " +  cctransacionid+" from "+record.getTransStatus());
               record.setTransStatus(CmFinoFIX.TransStatus_Verified);
               dao.save(record);
               out.println("Continue");
               return;
           }else if(record!=null){
        	   log.info("received parameters are not matching with the DB params for tranasctionid= " +  cctransacionid);
        	   if(!record.getTransStatus().equals(CmFinoFIX.TransStatus_Failed)){        	   
        	   log.info("Updating the status to failed for tranasctionid= " +  cctransacionid+ " from " + record.getTransStatus());
        	   record.setCCFailureReason(CmFinoFIX.CCFailureReason_Invalid_Paramerters_At_Verify);
        	   record.setTransStatus(CmFinoFIX.TransStatus_Failed);
               dao.save(record);
        	   }else{
        		   //no need to update failed transaction 
        		   log.info("verify request for transactionid "+cctransacionid+" with status "+record.getTransStatus());
        	   }
               out.println("Stop");
               return;
           }else{           
        	   log.info("No record found with transactionid "+cctransacionid+" Responding with Stop");
               out.println("Stop");
           }        
        } catch(Exception e) {
           log.info("Exception occured while processing verify transaction ",e);
           log.info("Sending stop to NSIA because of exception at our end");
           out.println("Stop");
        }finally{
//        	no need to send sms or email if verify fails as  transaction not started for user
//        	if(isSmsRequired){
//        		log.info("sending fail notification email to user");        		   		
//        		MfinoCreditCardUtil.sendTransferMail(email,record,transactionId);
//        		
//        		log.info("sending fail notification sms to user");        		
//        		String msg =  MfinoCreditCardUtil.generateMessageBody(record, transactionId);        		
//        		MfinoCreditCardUtil.sendSMS(mdn, msg);
//        	}
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
       // processRequest(request, response);
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

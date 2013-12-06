/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.ccpayment.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mfino.cc.message.CCPaymentInput;
import com.mfino.ccpayment.util.GetTransactionDetails;
import com.mfino.ccpayment.util.MessageDigestEncoder;
import com.mfino.dao.DAOFactory;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.util.MfinoUtil;
/**
 *
 * @author deva
 */
public class NSIACCPaymentServlet extends HttpServlet {

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
    private static Logger log = LoggerFactory.getLogger(NSIACCPaymentServlet.class);
    
	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            try
        {
            // Get the signature. Append the Amount value. Encode it with SHA1
            // algorithm and then return the same
            CCPaymentInput ccPaymentInput = new CCPaymentInput();
            String amount = request.getParameter("AMOUNT");
            String mdn = request.getParameter("MDN");
            mdn = MfinoUtil.normalizeMDN(mdn);
            Long subscriberid = Long.parseLong(request.getParameter("SUBSCRIBERID"));
            Long pocketid = Long.parseLong(request.getParameter("POCKETID"));
            String description = request.getParameter("DESCRIPTION");
            String operation = request.getParameter("OPERATION");
            String billReferenceNumber = request.getParameter("BILLREFERENCENUMBER");
            String CCRechargeType = request.getParameter("PACKAGE");
            String piCode = request.getParameter("PICODE");
            String pd = request.getParameter("PRODUCTDESC");
            String sourceIP = request.getRemoteAddr();
            GetTransactionDetails getTransactionDetails = new GetTransactionDetails();
            log.info("Credit Card Payment Servlet : amount " + amount +" mdn "+ mdn +" subscriberid "+ subscriberid +" pocketid "+ pocketid +" description "+ description + " operation " + operation +" billReferenceNumber "+ billReferenceNumber+" sourceIP "+sourceIP+" Product Indicator Code:  " + piCode + "Product Description: " + pd);
            ccPaymentInput.setSourceIP(sourceIP);
            ccPaymentInput.setMdn(mdn);
            ccPaymentInput.setSubscriberid(subscriberid);
            ccPaymentInput.setPocketid(pocketid);
            ccPaymentInput.setAmount(new BigDecimal(amount));
            ccPaymentInput.setOperation(operation);            
            ccPaymentInput.setDescription(description);
            ccPaymentInput.setBillReferenceNumber(billReferenceNumber);    
            ccPaymentInput.setSessionID(request.getSession().getId());
            ccPaymentInput.setCCBucketType(CCRechargeType);
            ccPaymentInput.setPICode(piCode);
            

    		Session session = sessionFactory.openSession();
    		hibernateSessionHolder.setSession(session);		
    		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);
    		boolean result = false;
    		try
    		{
    			result = getTransactionDetails.process(ccPaymentInput);
    		}
    		finally
    		{
    			if(session!=null)
    			{
    				session.close();
    			}
    		}    		
    		
            log.info("Inserted into CC record? "+ result + " and the id is" + ccPaymentInput.getMerchantTxnID());
            //appending decimal point as it is required.
            String amt = amount + ".00";
            String signature = null;
            String transactionID = ccPaymentInput.getMerchantTxnID();
            //Append amount and transactionID to the signature
            signature = amt + ccPaymentInput.getMerchantID() + ccPaymentInput.getTransactionPwd() + transactionID;
            // Now hash the signature
            try {
                signature = MessageDigestEncoder.SHA1(signature);
            } catch (NoSuchAlgorithmException e) {
                log.info("could not get signature", e);
            }
            PrintWriter out = response.getWriter();
            log.info("output of ajax call, "+signature + "***" + ccPaymentInput.getMerchantTxnID()+"sessionid" + ccPaymentInput.getSessionID());
            // Return signature sessionid and merchant transaction id
            out.println(signature + "***" + ccPaymentInput.getMerchantTxnID()+"sessionid" +ccPaymentInput.getSessionID());
            }catch(Exception e){
                log.info("Exception Occured" , e);
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

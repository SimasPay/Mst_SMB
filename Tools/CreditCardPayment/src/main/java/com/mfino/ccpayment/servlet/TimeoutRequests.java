package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.ccpayment.util.IPFilterting;
import com.mfino.ccpayment.util.MessageDigestEncoder;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoCreditCardUtil;

/**
 * Servlet implementation class TimeoutRequests
 */
public class TimeoutRequests extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	private Logger log = LoggerFactory.getLogger(this.getClass());
    public TimeoutRequests() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("In TimeoutRequest Servlet");
		
		if(!IPFilterting.validip(request.getRemoteAddr())){
         	log.info("IP Check Failed sending stop request to the NSIA. Request Came from" +request.getRemoteAddr());
         	return;
         }
		try{
		
		HibernateUtil.getCurrentSession().beginTransaction();		
		Long cctransactionid = Long.parseLong(request.getParameter("TRANSIDMERCHANT"));
//		Double amt  = Double.parseDouble(request.getParameter("AMOUNT"));
		BigDecimal amt  = new BigDecimal(request.getParameter("AMOUNT"));
		String currencycode = request.getParameter("CURRENCY");
		String sessionid = request.getParameter("SESSIONID");
		String words = request.getParameter("WORDS");
		CreditCardTransactionDAO dao = new CreditCardTransactionDAO();
		CreditCardTransaction record = dao.getById(cctransactionid);
		log.info("cctransactiond=" +cctransactionid + "amount="+amt + "currenctcode=" + currencycode + "sessionid=" + sessionid + "words="+ words);
		if(record==null){
				log.info("Invalid Transaction ID" + cctransactionid);
				return;
        }
		if(!record.getSessionID().equals(sessionid)){
				log.info("Invalid Session Id" + sessionid);
				return;
		}
		if(!record.getCurrCode().equals(currencycode)){
			log.info("Invalid Currency Code" + currencycode);
	        return;				
		}
//		if(!amt.equals((double)record.getAmount())){
		if(amt.compareTo(record.getAmount()) != 0){		
			log.info("Invalid Amount" + amt);
	        return;				
		}
		String signature = record.getAmount() + ".00" + ConfigurationUtil.getCreditcardMerchantid() + ConfigurationUtil.getCreditcardTransactionPassword() + cctransactionid;
        try {
            signature = MessageDigestEncoder.SHA1(signature);
        } catch (NoSuchAlgorithmException e) {
            log.error("could not get signature", e);
        }
        if (!words.equals(signature)) {
            log.info("Invalid Signature found" + words + "Instead of " + signature);
            return;
        }
        if(!(CmFinoFIX.CCFailureReason_Transaction_Timedout.equals(record.getCCFailureReason())) && (CmFinoFIX.TransStatus_Verified.equals(record.getTransStatus()) ||CmFinoFIX.TransStatus_Notified.equals(record.getTransStatus()))){
            record.setCCFailureReason(CmFinoFIX.CCFailureReason_Transaction_Timedout);
            record.setTransStatus(CmFinoFIX.TransStatus_Timedout);
            String msg=MfinoCreditCardUtil.generateSMSMessage(record, record.getID().toString(), CmFinoFIX.CCFailureReason_Transaction_Timedout);
        	String emailMsg = MfinoCreditCardUtil.generateMessageBody(record,CmFinoFIX.TransStatus_Timedout );
            dao.save(record);
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            Subscriber subscriber = subscriberDAO.getById(record.getSubscriber().getID());
            Set<SubscriberMDN> subscriberMDN = subscriber.getSubscriberMDNFromSubscriberID();
            if(!subscriberMDN.isEmpty())
            MfinoCreditCardUtil.sendSMS(subscriberMDN.iterator().next().getMDN(), msg);
            if(subscriber.getUserBySubscriberUserID()!=null)
            MfinoCreditCardUtil.sendTransferMail(subscriber.getUserBySubscriberUserID().getEmail(), emailMsg);
        }
		}catch(Exception e){
			log.error("Exception occured in timeoutRequest" + e);
		}finally{
			HibernateUtil.getCurrentTransaction().commit();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

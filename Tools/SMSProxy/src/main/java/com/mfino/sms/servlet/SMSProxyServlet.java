package com.mfino.sms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.sms.SMSCodeHandler;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoUtil;

/**
 * Servlet implementation class SMSProxyServlet
 */
public class SMSProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Default constructor. 
     */
    public SMSProxyServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("Handling SMS Message");
		log.info(request.getRequestURI());
		HibernateUtil.getCurrentSession().beginTransaction();
		String sender = request.getParameter("sender");
		String destination = request.getParameter("destn");
		String message = request.getParameter("message");
		String smsc = request.getParameter("smsc");	
			
		if (sender == null) {
			// Invalid message from Kannel
			log.warn("Sender MDN is null So Invalid message from Kannel");
			return;
		}
		if (destination == null) {
			// Don't know in which scenario this will happen
			log.warn("Destination MDN is null So Invalid message from Kannel");
			return;
		}
		if (message == null || message.length() == 0) {
			// send a message back to Kannel saying that it is a invalid Request
			log.warn("Message is null So Invalid message from Kannel");
			return;
		}
		StringBuilder sb = new StringBuilder();
		char [] senderCharArray = sender.toCharArray();
		for (int i=0; i<senderCharArray.length;++i) {
			if(Character.isDigit(senderCharArray[i])) {
				sb.append(senderCharArray[i]);
			}
		}
		sender = MfinoUtil.normalizeMDN(sb.toString());
		sb = new StringBuilder();
		char [] destnCharArray = destination.toCharArray();
		for (int i=0; i<destnCharArray.length;++i) {
			if(Character.isDigit(destnCharArray[i])) {
				sb.append(destnCharArray[i]);
			}
		}
		destination = sb.toString();
		SMSCodeHandler smsCodeHandler = new SMSCodeHandler(sender, destination, message.trim(),smsc);
		log.info(" Got SMS " + message.trim() + " from " + sender + " to " + destination + " smsc = " + smsc);
		try {
			smsCodeHandler.handleSMS();
			HibernateUtil.getCurrentTransaction().commit();
		} catch (Exception err) {
			log.error("Got Exception So rolling back the transaction", err);
			HibernateUtil.getCurrentTransaction().rollback();
		} 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}

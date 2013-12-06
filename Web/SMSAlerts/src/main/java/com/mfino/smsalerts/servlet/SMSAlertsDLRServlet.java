package com.mfino.smsalerts.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.SMSTransactionsLogDAO;
import com.mfino.domain.SMSTransactionsLog;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.SMSService;

/**
 * Servlet implementation class DLRServlet
 * 
 * Receives the Delivery report of the SMS Alerts.
 */
public class SMSAlertsDLRServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());   
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SMSAlertsDLRServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getQueryString();
		String transactionid = null;
		String status = null;
		String dlr_time = null;
		String fieldid = null;

		log.info("Delivery query: "+query);

		String[] queryParamValues = query.split("&");
		for(String paramValue:queryParamValues){
			String[] paramValuePair = paramValue.split("=");
			if(paramValuePair.length!=2){
				//Sometimes the values might be empty then the length is just 1
				continue;
			}
			if(SMSService.KANNEL_DLR_ID_QUERYNAME.equals(paramValuePair[0])){
				transactionid = paramValuePair[1]; 
			}else if(SMSService.KANNEL_DLR_STATUS_QUERYNAME.equals(paramValuePair[0])){
				status = paramValuePair[1];
			}else if(SMSService.KANNEL_DLR_TIME_QUERYNAME.equals(paramValuePair[0])){
				dlr_time = paramValuePair[1];
			}else if(SMSService.KANNEL_DLR_FID_QUERYNAME.equals(paramValuePair[0])){
				fieldid = paramValuePair[1];
			}
		}

		SMSTransactionsLogDAO logDAO = new SMSTransactionsLogDAO();
		if (transactionid != null) {
			SMSTransactionsLog smsdlrlog = logDAO.getById(Long.parseLong(transactionid));
			if(smsdlrlog!=null){
				if(status!=null){
					smsdlrlog.setDeliveryStatus(status);
				}
				if(dlr_time!=null){
					if(dlr_time.length()==10){
						//The time provided by kannel is in seconds and not milli seconds so appending 000 as Date expects the time to be in milliseconds.
						dlr_time=dlr_time.concat("000");
					}
					smsdlrlog.setTransactionTime(new Timestamp(Long.valueOf(dlr_time)));
				}
				if(fieldid!=null){
					smsdlrlog.setFieldID(fieldid);
				}
				logDAO.save(smsdlrlog);
			}else{
				log.error("Unable to find SMS Transaction Log for transaction id: "+transactionid);
			}
		} else {
			log.error("Transaction id is null in the DLR request " + transactionid);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}

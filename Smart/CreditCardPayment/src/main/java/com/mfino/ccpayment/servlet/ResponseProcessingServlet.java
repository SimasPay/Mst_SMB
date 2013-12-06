package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.RequestDispatcher;
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

import com.mfino.cc.message.CCPaymentOutput;
import com.mfino.ccpayment.util.PaymentUtil;
import com.mfino.dao.DAOFactory;
import com.mfino.hibernate.session.HibernateSessionHolder;

/**
 * Servlet implementation class ResponseProcessingServlet
 */
public class ResponseProcessingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int topup =2;
    private static final int postpaid =1;
    private static Logger log = LoggerFactory.getLogger(ResponseProcessingServlet.class);
    private static final String errCodeSuccess = "0";
    private static final String bankResSuccess = "00";
    private static final String userCodeSuccess = "101";
    private static final String transStatusSuccess = "S";

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
    public ResponseProcessingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
            HttpServletResponse response) {
        // Response string to be sent back to displayResults.jsp
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/paymentResponse.jsp");
        String responseString = "";
        int operation = topup;
    	Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);
		
        try {
            log.info("Response Processing Servlet");
            // Parse the response and build response object
            CCPaymentOutput responseObj = buildRespObj(request);
            log.info("Response obatined is " + responseString);
            storeCreditCardTransaction(responseObj);
            String authID = request.getParameter("AUTH_ID");
            if(responseObj.getOperation().equals("2")){
                operation = postpaid;
            }
            // Check for error. If no error, update billing system
            // Build the appropriate response string
            PaymentUtil paymentUtil = new PaymentUtil();
            if (StringUtils.isNotEmpty(authID) && errCodeSuccess.equals(responseObj.getErrorCode())
                    && bankResSuccess.equals(responseObj.getBankResCode()) && transStatusSuccess.equalsIgnoreCase(responseObj.getTxnStatus())
                    && userCodeSuccess.equals(responseObj.getUsrCode())) {
                if (operation == postpaid) {
                    responseString = paymentUtil.processTopUp(responseObj);
                } else {
                    responseString = paymentUtil.processPostPaid(responseObj);
                }

            } else {
                    responseString = buildResponseString(request);
                    paymentUtil.sendTransferMail(null,responseObj, PaymentUtil.status.couldnotstart, operation, StringUtils.EMPTY);
            }
            request.setAttribute("resultMsg", responseString);
            dispatcher.forward(request, response);
        } catch (Exception e) {
           log.info("exception in response processing servlet ", e);
        }
        finally
        {
        	if(session!=null)
        	{
        		session.close();
        	}
        }

    }

    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    private boolean storeCreditCardTransaction(CCPaymentOutput responseObj) {
    	try
    	{
    	PaymentUtil paymentUtil = new PaymentUtil();
        paymentUtil.saveCCTransactionInfo(responseObj);
        } catch (Exception e) {
            log.error("Rolling back of Transaction " + e.getMessage());
        }
  	
        return true;
    }

    private CCPaymentOutput buildRespObj(HttpServletRequest request) {
        CCPaymentOutput responseObj = new CCPaymentOutput();
        responseObj.setPaymentMethod(request.getParameter("PAYMENT_METHOD"));
        responseObj.setErrorCode(request.getParameter("ERR_CODE"));
        responseObj.setUsrCode(request.getParameter("USR_CODE"));
        responseObj.setTxnStatus(request.getParameter("TXN_STATUS"));
        responseObj.setDescription(request.getParameter("DESCRIPTION"));
        responseObj.setCurrencyCode(request.getParameter("CURRENCYCODE"));
        responseObj.setAmount(new BigDecimal(request.getParameter("AMOUNT")));
        responseObj.setEUI(request.getParameter("EUI"));
        responseObj.setTransactionID((long) (Double.parseDouble(request.getParameter("TRANSACTIONID"))));
        responseObj.setTransDate(request.getParameter("TRANDATE"));
        responseObj.setTransType(request.getParameter("TRANSACTIONTYPE"));
        responseObj.setIsBlackListed(request.getParameter("IS_BLACKLISTED"));
        if (request.getParameter("FRAUDRISKLEVEL") != null) {
            responseObj.setFraudRiskLevel(Integer.parseInt(request.getParameter("FRAUDRISKLEVEL")));
        }
        if (request.getParameter("FRAUDRISKSCORE") != null) {
            responseObj.setFraudRisksCore(new BigDecimal(request.getParameter("FRAUDRISKSCORE")));
        }
        responseObj.setExceedHighRisk(request.getParameter("EXCEED_HIGH_RISK"));
        responseObj.setCardType(request.getParameter("CARDTYPE"));
        responseObj.setCardNoPartial(request.getParameter("CARD_NO_PARTIAL"));
        responseObj.setCardName(request.getParameter("CARDNAME"));
        responseObj.setAcquirerBank(request.getParameter("ACQUIRING_BANK"));
        responseObj.setBankResCode(request.getParameter("BANK_RES_CODE"));
        responseObj.setBankResMsg(request.getParameter("BANK_RES_MSG"));
        responseObj.setAuthId(request.getParameter("AUTH_ID"));
        responseObj.setBankreference(request.getParameter("BANK_REFERENCE"));
        responseObj.setWhiteListCard(request.getParameter("WHITELIST_CARD"));
        responseObj.setMerchantTransactionID(Long.parseLong(request.getParameter("MERCHANT_TRANID")));

        return responseObj;
    }

    /**
     * This method will build a response string to be displayed to the user when
     * the transaction fails at the payment gateway system. Return parameters
     * from request will be used as part of the response string. Parameters to
     * be used are
     * <ul>
     * <li>ERR_CODE</li>
     * <li>ERR_MSG</li>
     * <li>USR_CODE</li>
     * <li>USR_MSG</li>
     *
     * @param response
     * @return
     */
    private String buildResponseString(HttpServletRequest request) {
        // TODO: This is just a sample response message. Please modify as
        // required
        StringBuffer responseString = new StringBuffer();
//        responseString.append("There has been an error in your transaction. Please see below for further details:");
//        responseString.append("<br>");
//        responseString.append("Error Code: ");
//        responseString.append("&nbsp;&nbsp;");
//        responseString.append(request.getParameter("ERR_CODE"));
//        responseString.append("<br>");
//        responseString.append("Error Message: ");
//        responseString.append("&nbsp;");
//        responseString.append(request.getParameter("ERR_DESC"));
//        responseString.append("<br>");
//        responseString.append("User Code: ");
//        responseString.append("&nbsp;");
//        responseString.append(request.getParameter("USR_CODE"));
//        responseString.append("<br>");
//        responseString.append("User Message: ");
//        responseString.append("&nbsp;");
//        responseString.append(request.getParameter("USR_MSG"));
        responseString.append("&nbsp;(");
        responseString.append(request.getParameter("USR_CODE"));
        responseString.append(")&nbsp;");
        responseString.append(request.getParameter("USR_MSG"));

        return responseString.toString();
    }
}

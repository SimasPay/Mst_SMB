package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.ccpayment.util.GetPaymentAmount;
import com.mfino.ccpayment.util.RegistrationUtil;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.Pocket;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.mailer.NotificationMessageParser;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.validators.PocketLimitsValidator;

/**
 * This servlet gets the postpaid bill amount from the billing system
 */
public class BillingDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    static {
        // this is required before start decoding fix messages
        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BillingDetailsServlet() {
        super();        
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {        
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displaySummary.jsp");
        log.info("Billing Details Servlet ");
        /*URI uri = new URI(request.getRequestURL().toString());

        URL toSentURL = new URL(uri.getScheme(), uri.getHost(), uri.getPort(),
                    uri.getPath().substring(uri.getPath().indexOf('/'), uri.getPath().lastIndexOf('/')+1) + "ResponseProcessingServlet");
        String path = toSentURL.toString();*/
        String path = ConfigurationUtil.getCCPaymentDeploymentURL();
        request.setAttribute("PATH", path);
        log.info("Path = " + path);

        try {
            Long SubscriberId = Long.parseLong(request.getParameter("SUBSCRIBERID"));
            String mdn = (String) request.getParameter("MDN");
            Long pocketID = Long.parseLong(request.getParameter("POCKETID"));
            String amount1 =request.getParameter("AMOUNT");
            String language = request.getParameter("language");
            String operation = (String) request.getParameter("OPERATION");
            if (operation.equals("1")) {
            	amount1 = "";
            }
            mdn = MfinoUtil.normalizeMDN(mdn);
            log.info("subscriberid " + SubscriberId +" mdn " + mdn);
            if(!RegistrationUtil.checkCompany(SubscriberId, mdn)) {
                setRequestAttributes(request, amount1,CmFinoFIX.ErrorCode_Generic,"You cannot pay for this mdn as it as of other company.", "true", "");
            }else{
            
            log.info(" operation " + operation);
            if (operation.equals("1")) {
                GetPaymentAmount paymentAmount = new GetPaymentAmount();
                CMJSError returnMsg = paymentAmount.processAmount(mdn);
                if (returnMsg.getErrorCode() == 0) {
                    String amount = returnMsg.getErrorDescription();
                    String[] inp = amount.split(",");
                    String amt = inp[0];
                    amount1 = amt.trim();
                    Long payment = Long.parseLong(amt.trim());
                    String billReferenceNumber = inp[1];
                    if(payment>0)
                    {
                        setRequestAttributes(request, amount1,CmFinoFIX.ErrorCode_NoError,"", "false", billReferenceNumber);
                    }
                    else
                    {
                        setRequestAttributes(request, "", CmFinoFIX.ErrorCode_Generic,"You do not have any due amount.", "true", "");
                    }
                } else if (returnMsg.getErrorCode().equals(CmFinoFIX.ErrorCode_Generic)){
                    setRequestAttributes(request, "", returnMsg.getErrorCode() , "Could not retrieve the amount, please contact system admin.", "true", "");
                }
                else
                {
                    setRequestAttributes(request, "", returnMsg.getErrorCode(),returnMsg.getErrorDescription(), "true", "");
                }
            } else {
                setRequestAttributes(request,request.getParameter("AMOUNT"),CmFinoFIX.ErrorCode_NoError, "", "false", "");
            }
            
           	if(pocketID!= null && StringUtils.isNotBlank(amount1)&&StringUtils.isNumeric(amount1)){
           		HibernateUtil.getCurrentSession().beginTransaction();
           		PocketDAO pocketDAO = new PocketDAO();
           		Pocket pocket = pocketDAO.getById(pocketID);
            PocketLimitsValidator pocketLimitsValidator = new PocketLimitsValidator(new BigDecimal(amount1), pocket);	
            Integer validationResult = pocketLimitsValidator.validate();	
            if(!validationResult.equals(CmFinoFIX.ResponseCode_Success)){
            	Integer errorCode = 3;
            	String errorMsg = "Pocket Limits failed Notificationcode:"+validationResult;
            	NotificationWrapper notificationWrapper = new NotificationWrapper();
            	if(!validationResult.equals(CmFinoFIX.ResponseCode_Failure))
            	notificationWrapper.setCode(validationResult);    
            	else
            		notificationWrapper.setCode(CmFinoFIX.ResponseCode_Success);
            	if(language!=null && language.equalsIgnoreCase("1"))
            		notificationWrapper.setLanguage(CmFinoFIX.Language_Bahasa);
            	notificationWrapper.setSourcePocket(pocket);
            	NotificationMessageParser notificationMessageParser = new NotificationMessageParser(notificationWrapper);
            	errorMsg=notificationMessageParser.buildMessage(CmFinoFIX.NotificationMethod_Web);
            	setRequestAttributes(request, amount1, errorCode,errorMsg, "true", "");          	
            } 
            HibernateUtil.getCurrentTransaction().rollback();
            }
            }
           } catch (Exception err) {
            log.error("Error in billing details Servlet", err);
            setRequestAttributes(request, "", 6,"Could not process your request please try again", "true", ""); 
             }
           dispatcher.forward(request, response); 
    }

    /*
     * This function would set the required vairables Amount, errorMsg and disabling of submit button
     * as the request attributes which can be accessed in jsp page.
     */
    private void setRequestAttributes(HttpServletRequest request, String Amount,Integer errorCode, String errorMsg, String disable, String billReferenceNumber) {
    	request.setAttribute("ERROR_CODE", errorCode);
    	request.setAttribute("AMOUNT", Amount);
        request.setAttribute("ERROR_MSG", errorMsg);
        request.setAttribute("DISABLE", disable);
        request.setAttribute("BILLREFERENCENUMBER", billReferenceNumber);
        request.setAttribute("PACKAGE", request.getParameter("PACKAGE"));
    }
}

package com.mfino.smsalerts.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CompanyDAO;
import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.smsalerts.SMSAlertHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.xmlresulttypes.smsalerts.SMSAlertXMLResult;
import com.mfino.util.MfinoUtil;

/**
 * Servlet implementation class DispatcherServlet
 */
public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DispatcherServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String shortCode = request.getParameter("shortcode");
        String destMDN = request.getParameter("to");
        String message = request.getParameter("message");
        String partnerId = request.getParameter("partnerID");
        String apiToken = request.getParameter("apiToken");

        ServletOutputStream writer = response.getOutputStream();
        try {
            log.info("Short Code =" + shortCode);
            log.info("Dest mdn =" + destMDN);
            log.info("Message ="+message);
            log.info("Partner id =" + partnerId);
            if (StringUtils.isBlank(shortCode) || !StringUtils.isNumeric(shortCode)) {
                log.info("Short Code is null or not numeric " + shortCode);
                sendError(CmFinoFIX.NotificationCode_InvalidSMSAlertRequest_ParameterMissing, writer);
                return;
            }
            shortCode = shortCode.trim();

            if (StringUtils.isBlank(destMDN)) {
                log.info("Destinamtion mdn is null " + destMDN);
                sendError(CmFinoFIX.NotificationCode_InvalidSMSAlertRequest_ParameterMissing, writer);
                return;
            }
            destMDN = MfinoUtil.normalizeMDN(destMDN);
            
            if (StringUtils.isBlank(message)) {
                log.info("Message is null " + message);
                sendError(CmFinoFIX.NotificationCode_InvalidSMSAlertRequest_ParameterMissing, writer);
                return;
            }
            
            if (StringUtils.isBlank(partnerId) || !StringUtils.isNumeric(partnerId)) {
                log.info("Partner id is null or not numeric " + partnerId);
                sendError(CmFinoFIX.NotificationCode_InvalidSMSAlertRequest_ParameterMissing, writer);
                return;
            }
            
            if (StringUtils.isBlank(apiToken)) {
                log.info("Api token is null");
                sendError(CmFinoFIX.NotificationCode_InvalidSMSAlertRequest_ParameterMissing, writer);
                return;
            }
            SMSAlertXMLResult xmlResult = null;

            SMSAlertHandler result = new SMSAlertHandler(partnerId, shortCode, destMDN, apiToken, message, request.getRemoteAddr());

            xmlResult = (SMSAlertXMLResult) result.handle();
            
            xmlResult.setWriter(writer);
            try {
                xmlResult.render();
            } catch (Exception error) {
//                e.printStackTrace();
                log.error("Error=" + error.getMessage());
                sendError(CmFinoFIX.NotificationCode_Failure, writer);
                return;
            }
            
        } catch (NullPointerException ex) {
            log.error("Invalid WebAPI Request Parameter Missing", ex);
            sendError(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, writer);
            return;
        } catch (Exception ex) {
            log.error("Error=", ex);
            sendError(CmFinoFIX.NotificationCode_Failure, writer);
            return;
        }
    }

    private void sendError(Integer notificationCode, ServletOutputStream writer) {
        SMSAlertXMLResult xmlResult = new SMSAlertXMLResult();
        CompanyDAO dao = new CompanyDAO();

        xmlResult.setLanguage(CmFinoFIX.Language_English);
        xmlResult.setNotificationCode(notificationCode);
        xmlResult.setWriter(writer);
        xmlResult.setCompany(dao.getById(1l));
        xmlResult.setTransationId(null);
        xmlResult.setTransactionTime(new Timestamp());

        try {
            xmlResult.render();
        } catch (Exception error) {
        	 log.error("Error rendering result", error);
        } finally {
        }
    }
}

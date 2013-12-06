package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.cc.message.CCPaymentInput;
import com.mfino.ccpayment.util.GetTransactionDetails;
import com.mfino.ccpayment.util.MessageDigestEncoder;
import com.mfino.util.MfinoUtil;

/**
 * Servlet implementation class CCPaymentServlet
 */
public class CCPaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CCPaymentServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
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
            GetTransactionDetails getTransactionDetails = new GetTransactionDetails();
            log.info("Credit Card Payment Servlet : amount " + amount +" mdn "+ mdn +" subscriberid "+ subscriberid +" pocketid "+ pocketid +" description "+ description + " operation " + operation +" billReferenceNumber "+ billReferenceNumber);
            ccPaymentInput.setMdn(mdn);
            ccPaymentInput.setSubscriberid(subscriberid);
            ccPaymentInput.setPocketid(pocketid);
            ccPaymentInput.setAmount(new BigDecimal(amount));
            ccPaymentInput.setOperation(operation);
            ccPaymentInput.setDescription(description);
            ccPaymentInput.setBillReferenceNumber(billReferenceNumber);
            boolean result = getTransactionDetails.process(ccPaymentInput);
            //appending decimal point sa it is required.
            String amt = amount + ".00";
            String signature = ccPaymentInput.getSignature();
            String transactionID = ccPaymentInput.getTransactionID();
            //Append amount and transactionID to the signature
            signature = signature + amt + "##" + transactionID + "##";

            // Now hash the signature
            try {
                signature = MessageDigestEncoder.SHA1(signature);
            } catch (NoSuchAlgorithmException e) {
                log.error("could nor get signature", e);
            }
            PrintWriter out = response.getWriter();
            log.info("output of ajax call, "+signature + "***" + ccPaymentInput.getMerchantTxnID());
            // Return signature and merchant transaction id
            out.println(signature + "***" + ccPaymentInput.getMerchantTxnID());
        }
        catch(Exception exception)
        {
            log.info("Number format exception", exception);
        }
    }
}

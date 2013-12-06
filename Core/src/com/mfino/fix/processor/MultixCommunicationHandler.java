/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;
import com.mfino.fix.CmFinoFIX.CMH2HResponse;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMSubscriberNotification;
import com.mfino.fix.serialization.FixMessageSerializer;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.impl.NotificationServiceImpl;
import com.mfino.util.ConfigurationUtil;

/**
 * 
 * @author sunil
 */
public class MultixCommunicationHandler implements IFixProcessor {

/*	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;*/
	public void test(){
		
	}

    private static final String DEFAULT_URL = ConfigurationUtil.getBackendURL();
    static int msgCounter = 0;
    protected Logger log = LoggerFactory.getLogger(this.getClass());
	private String loggedUserName; 
	private String ipAddress;
	protected static final String NOBACKEND_RESPONSE="Your request is queued. Please check after sometime.";
    public CFIXMsg handleRequestResponse(CMBase msg) {
        return handleRequestResponse(msg, DEFAULT_URL, CmFinoFIX.SourceApplication_Web);
    }

    public CFIXMsg handleRequestResponse(CMBase msg, String URL) {
        return handleRequestResponse(msg, URL, CmFinoFIX.SourceApplication_Web);
    }
    
    public CFIXMsg handleRequestResponse(CMBase msg, String URL, Integer sourceApplication) {
    	CFIXMsg pMsg = sendRequest(msg, URL, sourceApplication);
    	
    	/*
    	 * TODO if instance of cmfixresponse, convert this to cm-js error.
    	 */
    	
        return handleResponse(pMsg);
    }
    
    public CFIXMsg sendRequest(CMBase msg) {
        return sendRequest(msg, DEFAULT_URL, CmFinoFIX.SourceApplication_Web);
    }

    public CFIXMsg sendRequest(CMBase msg, String URL, Integer sourceApplication) {
        FixMessageSerializer fms = new FixMessageSerializer(URL);

        msg.m_pHeader.setMsgSeqNum(msgCounter++);
        msg.m_pHeader.setSendingTime(new Timestamp());
        msg.setSourceApplication(sourceApplication);
        msg.setReceiveTime(new Timestamp());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            msg.setOperatorName(auth.getName());
            WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
            if (details != null) {
                msg.setWebClientIP(details.getRemoteAddress());

            } else {
                msg.setWebClientIP("");
            }
        }
        // This message is used by H2H and is for Just logging
        // msg.setServiceNumber();
        try {
            InetAddress ownIP = InetAddress.getLocalHost();
            msg.setSourceIP(ownIP.toString());
        } catch (UnknownHostException ex) {
            log.error(ex.getMessage(), ex);
        }

        CFIXMsg pMsg = fms.send(msg);
        return pMsg;
    }
    
	/**
	 * Checks the Core Engine Response after confirmation of the Transactions and Returns True / False based 
	 * on the response message. 
	 * 
	 * @param backEndResponse
	 * @return
	 */
	public TransactionResponse checkBackEndResponse(CFIXMsg backEndResponse) {
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setResult(false);
		String status = "";
		String code = "0"; 
		if (backEndResponse != null) {
			CMJSError response = (CMJSError) backEndResponse;
			String message = response.getErrorDescription();
			log.info("Core Engine message = " + message);
			message = message.trim();
			status = response.getErrorCode().toString();
			transactionResponse.setMessage(message);
			if (response.getParentTransactionID() != null) {
				transactionResponse.setTransactionId(response.getParentTransactionID());				
			}
			if (response.getTransferID() != null) {
				transactionResponse.setTransferId(response.getTransferID());
			}
			if(StringUtils.isNotBlank(response.getPaymentInquiryDetails()))
			{
				transactionResponse.setPaymentInquiryDetails(response.getPaymentInquiryDetails());
			}
			if(StringUtils.isNotBlank(response.getDestinationType()))
			{
				transactionResponse.setDestinationType(response.getDestinationType());
			}
			if(StringUtils.isNotBlank(response.getBankName()))
			{
				transactionResponse.setBankName(response.getBankName());
			}
			if(StringUtils.isNotBlank(response.getAdditionalInfo()))
			{
				transactionResponse.setAdditionalInfo(response.getAdditionalInfo());
			}
			if(StringUtils.isNotBlank(response.getDestinationUserName()))
			{
				transactionResponse.setDestinationUserName(response.getDestinationUserName());
			}
			if (response.getAmount() != null) {
				transactionResponse.setAmount(response.getAmount());
			}
			log.info("Core Engine Response status = " + status);
			if(response.getCode() != null)
			{
				code = response.getCode().toString();
				transactionResponse.setCode(code);
				log.info("Core Engine Response Code = " + code);
			}
			transactionResponse.setSourceCardPAN(response.getSourceCardPAN());
		}
		
		if ((CmFinoFIX.ResponseCode_Success.toString()).equals(status) && !("0".equals(code))) {
			transactionResponse.setResult(true);
		}
		return transactionResponse;
	}

    public CFIXMsg handleResponse(CFIXMsg pMsg) {
    	if(pMsg != null) {
    		log.info(pMsg.DumpFields());
    	}
      //  Integer language = UserService.getNativeLanguageCode();
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        NotificationServiceImpl notificationService = new NotificationServiceImpl();
        if (pMsg != null) {
            if (pMsg instanceof CmFinoFIX.CMErrorNotification) {
                CMErrorNotification errMsg = (CMErrorNotification) pMsg;
                if (CmFinoFIX.ErrorCode_NoError.equals(errMsg.getErrorCode())) {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
                } else {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                }
                errorMsg.setErrorDescription(errMsg.getErrorDescription());
            } else if (pMsg instanceof CMH2HResponse) {
                CMH2HResponse errMsg = (CMH2HResponse) pMsg;
                if (CmFinoFIX.ResponseCode_Success.equals(errMsg.getErrorCode())) {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
                } else {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                }
                errorMsg.setErrorDescription(errMsg.getErrorDescription());
            } else if (pMsg instanceof CmFinoFIX.CMSubscriberNotification) {
                CMSubscriberNotification errMsg = (CMSubscriberNotification) pMsg;
                if (CmFinoFIX.ResponseCode_Success.equals(errMsg.getResult())) {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
                } else {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                }
                if(null!=((CmFinoFIX.CMSubscriberNotification) pMsg).getDestinationType()){
                	errorMsg.setDestinationType(((CmFinoFIX.CMSubscriberNotification) pMsg).getDestinationType());
                }
                if(null!=((CmFinoFIX.CMSubscriberNotification) pMsg).getBankName()){
                	errorMsg.setDestinationType(((CmFinoFIX.CMSubscriberNotification) pMsg).getBankName());
                }
                errorMsg.setCode(errMsg.getCode());
                errorMsg.setAmount(errMsg.getAmount());   
                errorMsg.setErrorDescription(errMsg.getCode()+"-" + errMsg.getText());
                errorMsg.setParentTransactionID(errMsg.getParentTransactionID());
                errorMsg.setTransferID(((CmFinoFIX.CMSubscriberNotification) pMsg).getTransactionID());
            } else if(pMsg instanceof CmFinoFIX.CMBankChannelResponse)
            {
                CmFinoFIX.CMBankChannelResponse rsp = (CmFinoFIX.CMBankChannelResponse)pMsg;
                
                if(rsp.getISO8583_ResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success)||
                        rsp.getISO8583_ResponseCode().equals("00"))
                {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
                    errorMsg.setErrorDescription(rsp.getTotalBillDebts().toString() + "," + rsp.getBillReferenceNumber());
                }
                else
                {
                    try
                    {
                        errorMsg.setErrorCode(Integer.parseInt(rsp.getISO8583_ResponseCode()));
                    }
                    catch(Exception error){
                    	log.error("Error parsing response code:",error);
                    }
                    errorMsg.setErrorDescription("Failed");
                }
            }

            if (errorMsg.getErrorCode() == null) {
                errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                if ((errorMsg.getErrorDescription()) == null) {
                    errorMsg.setErrorDescription(notificationService.getNotificationText(
                            CmFinoFIX.NotificationCode_Multix_Comm_Unknown_Error,
                            CmFinoFIX.Language_English));
                }
            } else if (CmFinoFIX.ResponseCode_Success.equals(errorMsg.getErrorCode()) && (errorMsg.getErrorDescription()) == null) {
                errorMsg.setErrorDescription(notificationService.getNotificationText(
                        CmFinoFIX.NotificationCode_Multix_Comm_Completed_Susccesfully,
                        CmFinoFIX.Language_English));
            } else if (!CmFinoFIX.ResponseCode_Success.equals(errorMsg.getErrorCode()) && (errorMsg.getErrorDescription()) == null) {
                errorMsg.setErrorDescription(notificationService.getNotificationText(
                        CmFinoFIX.NotificationCode_Multix_Comm_Failure_Message,
                        CmFinoFIX.Language_English));
            }
        } else {
            log.info(MessageText._("No response from backend server"));
            //Hack
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            errorMsg.setErrorDescription(NOBACKEND_RESPONSE);
        }
        return errorMsg;
    }
    
	@Override
	public void setLoggedUserName(String loggedUserName) {
		this.loggedUserName = loggedUserName;		
	}

	@Override
	public String getLoggedUserName() {
		return loggedUserName;
	}

	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLoggedUserNameWithIP() {
		return loggedUserName+" [IP:"+ipAddress+"]";
	}

	@Override
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}
}
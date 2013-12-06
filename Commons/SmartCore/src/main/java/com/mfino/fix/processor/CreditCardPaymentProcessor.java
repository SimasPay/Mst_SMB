/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.Company;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCreditCardRequest;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;
import com.mfino.fix.CmFinoFIX.CMSubscriberNotification;
import com.mfino.i18n.MessageText;
import com.mfino.service.NotificationService;
import com.mfino.uicore.service.UserService;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author admin
 */
public class CreditCardPaymentProcessor extends MultixCommunicationHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Long ccTransactionID;
    static {
	    // this is required before start decoding fix messages
	    CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
	  }
    @Override
    public CFIXMsg process(CFIXMsg msg) {
        CMCreditCardRequest realMsg = (CMCreditCardRequest) msg;
        realMsg.setServletPath(CmFinoFIX.ServletPath_Subscribers);
        ccTransactionID = realMsg.getCreditCardTransactionID();
        return handleRequestResponse(realMsg);
    }
    
    @Override
    public CFIXMsg handleResponse(CFIXMsg pMsg) {
		if (pMsg != null) {
			log.info(pMsg.DumpFields());			
		}
                int language=CmFinoFIX.Language_English;
		User user = UserService.getCurrentUser();
                Company company = null;
                if(null != user){
                    company = user.getCompany();
                }
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		if (pMsg == null) {			
				log.info(MessageText._("No response from backend server"));
//				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
//				errorMsg.setErrorDescription(getMultixGenericDescriptionText(ccTransactionID, company, language));
				return pMsg;			
		}
	
		if (pMsg instanceof CmFinoFIX.CMErrorNotification) {
			//This is when the backend does a reply with error
			// Msg.Reply(mFinoFIX_Error_PocketDoesNotExist
			CMErrorNotification errMsg = (CMErrorNotification) pMsg;
			if (errMsg.getErrorCode() == CmFinoFIX.ErrorCode_NoError) {
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			} else {
				errorMsg.setErrorCode(errMsg.getErrorCode());
			}
			errorMsg.setErrorDescription(errMsg.getErrorDescription());			
		} else if (pMsg instanceof CmFinoFIX.CMSubscriberNotification) {
			CMSubscriberNotification notifMsg = (CMSubscriberNotification) pMsg;
			if (CmFinoFIX.ResponseCode_Success.equals(notifMsg.getResult())) {
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			} else {
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			}
			errorMsg.setErrorDescription(errorMsg.getErrorCode()+ notifMsg.getText());
		}

//		String errorDescription = errorMsg.getErrorDescription();
//		
//		if(StringUtils.isBlank(errorDescription)) {
//			if(errorMsg.getErrorCode() == null) {
//				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
//				errorDescription = getMultixGenericDescriptionText(ccTransactionID, company, language);
//			} else if (CmFinoFIX.ResponseCode_Success.equals(errorMsg.getErrorCode())) {
//				errorDescription = NotificationService.getNotificationText(CmFinoFIX.NotificationCode_Multix_Comm_Completed_Susccesfully,language,CmFinoFIX.NotificationMethod_Web, company);
//			} else {
//				errorDescription = getMultixGenericDescriptionText(ccTransactionID, company, language);
//			}
//			errorMsg.setErrorDescription(errorDescription);
//		}
			 
		return errorMsg;
	}
    private String getMultixGenericDescriptionText(Long ccTransactionID, Company company, int language){
        HibernateUtil.getCurrentSession().beginTransaction();
        String errorDescription = NotificationService.getNotificationText(CmFinoFIX.NotificationCode_MultixGenericCCResponse,language,CmFinoFIX.NotificationMethod_Web, company);
        if(null != ccTransactionID){
            errorDescription = StringUtils.replace(errorDescription, "$(CCTransID)", ccTransactionID.toString());
        }
        HibernateUtil.getCurrentTransaction().rollback();
        return errorDescription;
    }
    
//    public static void main(String args[]){
//    	HibernateUtil.getCurrentSession().beginTransaction();
//    	CMCreditCardTopupRequest realMsg = new CMCreditCardTopupRequest();
//    	realMsg.setAuthID("123");
//    	realMsg.setCCBucketType(CmFinoFIX.CCBucketType_Data);
//    	realMsg.setAmount(15000l);
//    	realMsg.setSourceMDN("6288112149091");
//    	realMsg.setDestMDN("6288112149091");
//    	realMsg.setCreditCardTransactionID(1665l);
//    	realMsg.setPocketID(1644l);
//    	CreditCardPaymentProcessor proc = new CreditCardPaymentProcessor();
//    	proc.process(realMsg);
//    	HibernateUtil.getCurrentTransaction().commit();
//
//    }
}

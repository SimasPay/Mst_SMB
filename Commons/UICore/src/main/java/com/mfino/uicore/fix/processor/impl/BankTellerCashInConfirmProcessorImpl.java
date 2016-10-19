/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.AuthorizationService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.TellerCashinService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankTellerCashInConfirmProcessor;


/**
 * 
 * @author Maruthi
 */
@Service("BankTellerCashInConfirmProcessorImpl")
public class BankTellerCashInConfirmProcessorImpl extends MultixCommunicationHandler implements BankTellerCashInConfirmProcessor{
   
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("TellerCashinServiceImpl")
    private TellerCashinService tellerCashinService;
    
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;

	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
    public CFIXMsg process(CFIXMsg msg) {
    	log.info("creating the local transaction for processing the bank teller cashin confirm");
    	
    	CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        if (!authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Cashin)) {
            log.info("You are not authorized to perform this operation");
            errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
    	MfinoUser user=userService.getCurrentUser();
    	Set<Partner> partners = user.getPartners();
    	if(partners==null||partners.isEmpty()){
    	  errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
          errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
          return errorMsg;	
    	}
    	Partner partner = partners.iterator().next();
    	Subscriber sourcesubScriber = partner.getSubscriber();
    	SubscriberMdn sourceSubMdn = sourcesubScriber.getSubscriberMdns().iterator().next();
    	 CMJSBankTellerCashInConfirm realMsg = (CMJSBankTellerCashInConfirm) msg;
    	 if (realMsg.getServiceChargeTransactionLogID()==null) {
             log.info("ServiceChargeTransactionLogID is null");
             errorMsg.setErrorDescription(MessageText._("Invalid request"));
             errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
             return errorMsg;
         }
    	ServiceChargeTxnLog sctl = serviceChargeTransactionLogService.getById(realMsg.getServiceChargeTransactionLogID());
    	if (sctl==null) {
            log.info("ServiceChargeTransactionLogID is null");
            errorMsg.setErrorDescription(MessageText._("Invalid request"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
    	ChannelCode cc = channelCodeService.getChannelCodeByChannelId(sctl.getChannelcodeid().longValue());
        if (StringUtils.isBlank(realMsg.getDestMDN())||!sctl.getDestmdn().equals(realMsg.getDestMDN())) {
            log.info("DestMDN is null");
            errorMsg.setErrorDescription(MessageText._("Invalid Destination MDN"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        if (realMsg.getAmount() == null) { //||sctl.getTransactionAmount().equals(realMsg.getAmount())) { //Commented by Bala
            log.info("Amount is null");
            errorMsg.setErrorDescription(MessageText._("Invalid Amount"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        if (realMsg.getTransferID() == null) {
            log.info("TransferID is null");
            errorMsg.setErrorDescription(MessageText._("Invalid TransferID"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
       
        CMBankTellerCashInConfirm tellerCashInconfirm = new CMBankTellerCashInConfirm();
        tellerCashInconfirm.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
        tellerCashInconfirm.setSourceApplication((cc.getChannelsourceapplication()).intValue());
        tellerCashInconfirm.setSourceMDN(sourceSubMdn.getMdn());
        tellerCashInconfirm.setDestMDN(realMsg.getDestMDN());
        tellerCashInconfirm.setChannelCode(cc.getChannelcode());
        tellerCashInconfirm.setServletPath(CmFinoFIX.ServletPath_BankAccount);
        tellerCashInconfirm.setParentTransactionID(sctl.getTransactionid().longValue());        
        tellerCashInconfirm.setServiceChargeTransactionLogID(sctl.getId().longValue());
        tellerCashInconfirm.setIsInDirectCashIn(true);
        tellerCashInconfirm.setTransferID(realMsg.getTransferID());
        tellerCashInconfirm.setConfirmed(true);
        tellerCashInconfirm.setPin(realMsg.getPin());
        

        Integer resultCode = tellerCashinService.processConfirmation(tellerCashInconfirm);
        
        log.info("comitting the local transaction\n");
        

        sctl = serviceChargeTransactionLogService.getById(realMsg.getServiceChargeTransactionLogID());
        if(CmFinoFIX.ResponseCode_Success.equals(resultCode)){
       	 realMsg.setsuccess(Boolean.TRUE);
       	 
       	 errorMsg= (CMJSError) handleRequestResponse(tellerCashInconfirm);
       	   if(CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
       		   transactionChargingService.confirmTheTransaction(sctl);
       		   realMsg.setsuccess(Boolean.FALSE);
            }else if(CmFinoFIX.ErrorCode_LinkDisconnected.equals(errorMsg.getErrorCode())){
            	//no response from backend 
            	log.debug("BankTellerCashInConfirmProcessor :: No response from backend.");
            }else if(errorMsg.getTransferID()!=null){
            	if(errorMsg.getTransferID().equals(realMsg.getTransferID())){
            		//Bank to EMoney failed.
            		log.debug("BankTellerCashInConfirmProcessor :: Bank2EMoneyFailed confirmation failed");
            		transactionChargingService.failTheTransaction(sctl, "Bank2EMoneyFailed");
            	}
            	else{
            		log.debug("BankTellerCashInConfirmProcessor :: EMoney2EMoney failed");
            		String reverseReason = MessageText._("Transfer to Subscriber confirmation failed");
            		autoReverse(sctl, tellerCashInconfirm,  reverseReason);
//            		tcs.changeStatusToPendingResolved(sctl);
            	}
            }else{
            	log.debug("BankTellerCashInConfirmProcessor :: EMoney2EMoney inquiry failed");
            	String reverseReason = MessageText._("Transfer to Subscriber inquiry failed");
        		autoReverse(sctl, tellerCashInconfirm,  reverseReason);
//            	tcs.changeStatusToPendingResolved(sctl);
            }
       	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
       	return errorMsg;       
       }else{
       	realMsg.setsuccess(Boolean.FALSE);
       	NotificationWrapper notificationWrapper = new NotificationWrapper();
       	notificationWrapper.setCode(resultCode);
       	notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
       	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
       	errorMsg.setErrorDescription(notificationMessageParserService.buildMessage(notificationWrapper,true));
        return errorMsg;        	
       } 
    }

    // Do the Autoreversal if Transfer to Subscriber is fails.
	private void autoReverse(ServiceChargeTxnLog sctl, CMBankTellerCashInConfirm tellerCashInconfirm, 
			String reverseReason) {
		log.info("constructing autoreversal object");
		CMAutoReversal reversal = new CMAutoReversal();
		reversal.setSourcePocketID(tellerCashInconfirm.getSourcePocketID());
		reversal.setDestPocketID(tellerCashInconfirm.getDestPocketID());
		reversal.setServiceChargeTransactionLogID(sctl.getId().longValue());
		reversal.setAmount(sctl.getTransactionamount());
		reversal.setCharges(BigDecimal.ZERO);
		CMJSError emsg = (CMJSError) handleRequestResponse(reversal);
		log.info("Auto Reversal Staus : " + emsg.getErrorDescription());
		transactionChargingService.failTheTransaction(sctl, reverseReason);
	}
   
    @Override
    public CFIXMsg handleResponse(CFIXMsg pMsg) {
    	if(pMsg != null) {
    		return super.handleResponse(pMsg);
    	}else{
    		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
    		log.info(MessageText._("No response from backend server"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_LinkDisconnected);
            errorMsg.setErrorDescription("Your request is queued. Please check after sometime.");  
            return errorMsg;
        }
    	
    }
}

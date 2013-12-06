/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashIn;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.AuthorizationService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TellerCashinService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankTellerCashInInquiryProcessor;

/**
 * 
 * @author Maruthi
 */
@Service("BankTellerCashInInquiryProcessorImpl")
public class BankTellerCashInInquiryProcessorImpl extends MultixCommunicationHandler implements BankTellerCashInInquiryProcessor{
   
    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    @Qualifier("TellerCashinServiceImpl")
    private TellerCashinService tellerCashinService;
    
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService ;

	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
    //@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
    	log.info("creating the local transaction for processing the bank teller cashin inquiry");
    	CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
    	errorMsg.setsuccess(Boolean.FALSE);
        if (!authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Cashin)) {
            log.info("You are not authorized to perform this operation");
            errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
    	User user=userService.getCurrentUser();
    	Set<Partner> partners = user.getPartnerFromUserID();
    	if(partners==null||partners.isEmpty()){
    	  errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
          errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
          return errorMsg;	
    	}
    	Partner partner = partners.iterator().next();
    	Subscriber sourcesubScriber = partner.getSubscriber();
    	SubscriberMDN sourceSubMdn = sourcesubScriber.getSubscriberMDNFromSubscriberID().iterator().next();
    	
    	ChannelCode cc = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);
        CMJSBankTellerCashInInquiry realMsg = (CMJSBankTellerCashInInquiry) msg;
        log.info("Procesing the cashin Request for " + realMsg.getDestMDN());
        if (StringUtils.isBlank(realMsg.getDestMDN())) {
            log.info("DestMDN is null");
            errorMsg.setErrorDescription(MessageText._("Invalid Destination MDN"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        if (realMsg.getAmount() == null ) {
            log.info("Amount is null");
            errorMsg.setErrorDescription(MessageText._("Invalid Amount"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        if (realMsg.getDestMDNID() == null ) {
            log.info("Mdn id is null");
            errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        if (StringUtils.isBlank(realMsg.getPin())) {
            log.info("Pin is empty");
            errorMsg.setErrorDescription(MessageText._("Invalid Pin"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }   
        TransactionsLog transactionsLog = new TransactionsLog();
         transactionsLog.setMessageCode(CmFinoFIX.MsgType_JSBankTellerCashInInquiry);
        transactionsLog.setMessageData(realMsg.DumpFields());
        transactionsLog.setTransactionTime(new Timestamp(new Date()));
        transactionLogService.save(transactionsLog);
        
       
        CMBankTellerCashIn tellerCashinInquiry = new CMBankTellerCashIn();
        tellerCashinInquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
        tellerCashinInquiry.setSourceApplication(cc.getChannelSourceApplication());
        tellerCashinInquiry.setSourceMDN(sourceSubMdn.getMDN());
        tellerCashinInquiry.setDestMDN(realMsg.getDestMDN());
        tellerCashinInquiry.setChannelCode(cc.getChannelCode());
        tellerCashinInquiry.setServletPath(CmFinoFIX.ServletPath_BankAccount);
        tellerCashinInquiry.setIsInDirectCashIn(true);
        tellerCashinInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashin_SelfTransfer);
        tellerCashinInquiry.setPin(realMsg.getPin());
        tellerCashinInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_TELLER_CASHIN_BANKTOEMONEY);
        tellerCashinInquiry.setAmount(realMsg.getAmount());
        tellerCashinInquiry.setTransactionID(transactionsLog.getID());        

        Integer resultCode = tellerCashinService.processInquiry(tellerCashinInquiry, cc);
        log.info("comitting the local transaction\n");
        
        //session.close();
       if(CmFinoFIX.ResponseCode_Success.equals(resultCode)){
        	 realMsg.setsuccess(Boolean.TRUE);
        	 errorMsg= (CMJSError) handleRequestResponse(tellerCashinInquiry);
        	 
        	 if(CmFinoFIX.ErrorCode_Generic.equals(errorMsg.getErrorCode())||errorMsg.getTransferID()==null){

                ServiceChargeTransactionLog sctl = serviceChargeTransactionLogService.getById(tellerCashinInquiry.getServiceChargeTransactionLogID());
                transactionChargingService.failTheTransaction(sctl, errorMsg.getErrorDescription());
              	realMsg.setsuccess(Boolean.FALSE);
              	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
              	return errorMsg;
              }            
        	  
        	 realMsg.setServiceChargeTransactionLogID(tellerCashinInquiry.getServiceChargeTransactionLogID());
        	 realMsg.setCharges(tellerCashinInquiry.getCharges());
        	 realMsg.setTransferID(errorMsg.getTransferID());
        	 return realMsg;        
        }else{
        	realMsg.setsuccess(Boolean.FALSE);
        	NotificationWrapper notificationWrapper = new NotificationWrapper();
        	notificationWrapper.setCode(resultCode);
        	notificationWrapper.setDestMDN(realMsg.getDestMDN());
        	SubscriberMDN smdn = subscriberMdnService.getByMDN(realMsg.getDestMDN());
            if(smdn != null)
            {
            	notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
            	notificationWrapper.setLastName(smdn.getSubscriber().getLastName());					
            }
        	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
        	notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
        	errorMsg.setErrorDescription(notificationMessageParserService.buildMessage(notificationWrapper,true));
            return errorMsg;        	
        }
    }
   
}

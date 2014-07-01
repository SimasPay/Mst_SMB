package com.mfino.uicore.fix.processor.impl;

import static com.mfino.constants.ServiceAndTransactionConstants.TRANSACTION_REVERSE_CHARGE;
import static com.mfino.constants.ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSReverseTransactionApproveReject;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SCTLService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionTypeService;
import com.mfino.transactionapi.handlers.ReverseTransactionHandler;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ReverseTransactionApproveRejectProcessor;

@Service("ReverseTransactionApproveRejectProcessorImpl")
public class ReverseTransactionApproveRejectProcessorImpl extends BaseFixProcessor implements ReverseTransactionApproveRejectProcessor{

	private DAOFactory daoFactory = DAOFactory.getInstance();
	private ServiceChargeTransactionLogDAO sctlDao =daoFactory.getServiceChargeTransactionLogDAO();
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("ReverseTransactionHandlerImpl")
	private ReverseTransactionHandler reverseTransactionHandler;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSReverseTransactionApproveReject realMsg = (CMJSReverseTransactionApproveReject) msg;
		CMJSError err=new CMJSError();

		log.info("Approve / Reject the Reverse Transaction --> " + realMsg.getServiceChargeTransactionLogID() );
		if (realMsg.getServiceChargeTransactionLogID() != null) {
			ServiceChargeTransactionLog sctl = sctlService.getBySCTLID(realMsg.getServiceChargeTransactionLogID());

			Long transactionTypeId = sctl.getTransactionTypeID();
			TransactionType transactionType = transactionTypeService.getTransactionTypeById(transactionTypeId);
			String transactionTypeName = null;

			if((null != transactionType) && (TRANSACTION_REVERSE_TRANSACTION.equals(transactionType.getTransactionName()))){
				transactionTypeName = TRANSACTION_REVERSE_TRANSACTION;
			}
			else if((null != transactionType) && (TRANSACTION_REVERSE_CHARGE.equals(transactionType.getTransactionName()))){
				transactionTypeName = TRANSACTION_REVERSE_CHARGE;
			}
			else{
				throw new RuntimeException("ReverseTransactionApproveRejectProcessor-transactionType could not be identified");
			}

			if (sctl != null && sctl.getParentSCTLID() != null) {
				ServiceChargeTransactionLog parentSCTL = sctlService.getBySCTLID(sctl.getParentSCTLID());

				err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				sctl.setFailureReason(realMsg.getAdminComment());

				if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
					log.info("Reverse Transaction Request is Approved.");
					sctl.setStatus(CmFinoFIX.SCTLStatus_Reverse_Start);
					sctl.setFailureReason(realMsg.getAdminComment());

					if(TRANSACTION_REVERSE_TRANSACTION.equals(transactionTypeName)){
						parentSCTL.setAmtRevStatus(CmFinoFIX.SCTLStatus_Reverse_Approved);
					}
					else{
						parentSCTL.setChrgRevStatus(CmFinoFIX.SCTLStatus_Reverse_Approved);
					}

					parentSCTL.setFailureReason(realMsg.getAdminComment());
					sctlService.saveSCTL(parentSCTL);
					sctlService.saveSCTL(sctl);
					err.setErrorDescription(MessageText._("Successfully Approved the Reverse Transaction."));

					// Send the  Reverse request to Backend for processing
					reverseTransactionHandler.processReverseRequest(sctl, parentSCTL);

				}
				else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
					log.info("Reverse Transaction Request is Rejected.");
					sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
					sctl.setFailureReason(realMsg.getAdminComment());

					if(TRANSACTION_REVERSE_TRANSACTION.equals(transactionTypeName)){
						parentSCTL.setAmtRevStatus(CmFinoFIX.SCTLStatus_Reverse_Rejected);
					}
					else{
						parentSCTL.setChrgRevStatus(CmFinoFIX.SCTLStatus_Reverse_Rejected);
					}

					parentSCTL.setFailureReason(realMsg.getAdminComment());
					sctlService.saveSCTL(parentSCTL);
					sctlService.saveSCTL(sctl);
					sendNotification(parentSCTL, CmFinoFIX.NotificationCode_ReverseTransactionRequestRejected);
					err.setErrorDescription(MessageText._("Rejected the Reverse Transaction."));
				}
				else {
					log.info("Approve / Reject failed because of invalid action");
		            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		            err.setErrorDescription(MessageText._("Inavlid Admin Action"));
		            return err;
				}
			} else {
				log.info("Approve / Reject failed because of null value");
	            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
	            err.setErrorDescription(MessageText._("Approve / Reject of the Transaction is failed please try again after some time."));
			}

		} else {
			log.info("Approve / Reject failed because of null value");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Approve / Reject of the Transaction is failed please try again after some time"));
		}
		return err;
	}

	private void sendNotification(ServiceChargeTransactionLog parentSCTL, Integer notificationCode) {
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		notificationWrapper.setLanguage(language);
		notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notificationWrapper.setCode(notificationCode);
		notificationWrapper.setOriginalTransferID(parentSCTL.getID());
		SubscriberMDN smdn = subscriberMdnService.getByMDN(parentSCTL.getSourceMDN());
		if(smdn != null)
		{
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastName());
		}

        String message = notificationMessageParserService.buildMessage(notificationWrapper,true);

        smsService.setDestinationMDN(parentSCTL.getSourceMDN());
        smsService.setMessage(message);
        smsService.setNotificationCode(notificationWrapper.getCode());
        smsService.asyncSendSMS();
	}

}

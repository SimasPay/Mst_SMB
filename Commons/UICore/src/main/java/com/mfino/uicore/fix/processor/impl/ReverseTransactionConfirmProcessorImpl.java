package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSReverseTransactionConfirm;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ReverseTransactionConfirmProcessor;

@Service("ReverseTransactionConfirmProcessorImpl")
public class ReverseTransactionConfirmProcessorImpl extends BaseFixProcessor implements ReverseTransactionConfirmProcessor{

	private DAOFactory daoFactory = DAOFactory.getInstance();
	private ServiceChargeTransactionLogDAO sctlDao =daoFactory.getServiceChargeTransactionLogDAO();

	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSReverseTransactionConfirm realMsg = (CMJSReverseTransactionConfirm) msg;
		CMJSError err=new CMJSError();

		Long amtRevSctlId = realMsg.getAmountReversalSCTLID();
		Long chrgRevSctlId = realMsg.getChargeReversalSCTLID();

		log.info("ReverseTransactionConfirmProcessor :: amtRevSctlId=" + amtRevSctlId);
		log.info("ReverseTransactionConfirmProcessor :: chrgRevSctlId="+chrgRevSctlId);

		if((null != amtRevSctlId) || (null != chrgRevSctlId)){
			ServiceChargeTransactionLog amountReversalSCTL = (amtRevSctlId != null) ? sctlDao.getById(amtRevSctlId) : null;
			ServiceChargeTransactionLog chargeReversalSCTL = (chrgRevSctlId != null) ? sctlDao.getById(chrgRevSctlId) : null;
			ServiceChargeTransactionLog parentSCTL = null;

			if((amountReversalSCTL != null) || (chargeReversalSCTL != null)){
				if(amountReversalSCTL != null){
					parentSCTL = sctlDao.getById(amountReversalSCTL.getParentSCTLID());
				}
				else{
					parentSCTL = sctlDao.getById(chargeReversalSCTL.getParentSCTLID());
				}

				if(parentSCTL != null){
					parentSCTL.setReversalReason(realMsg.getReversalReason());

					BigDecimal reversalAmount = BigDecimal.ZERO;
					BigDecimal serviceCharge = BigDecimal.ZERO;

					if(amountReversalSCTL != null){
						if((null != realMsg.getIsReverseAmount()) && (realMsg.getIsReverseAmount())){
							parentSCTL.setAmtRevStatus(CmFinoFIX.SCTLStatus_Reverse_Initiated);
							amountReversalSCTL.setStatus(CmFinoFIX.SCTLStatus_Reverse_Initiated);
							sctlDao.save(amountReversalSCTL);
							
							//Before Correcting errors reported by Findbugs:
								//reversalAmount.add(amountReversalSCTL.getTransactionAmount());
								//serviceCharge.add(amountReversalSCTL.getCalculatedCharge());
						
						    //After Correcting the errors reported by Findbugs:reassigned the variables to themselves.
							reversalAmount = reversalAmount.add(amountReversalSCTL.getTransactionAmount());
							serviceCharge = serviceCharge.add(amountReversalSCTL.getCalculatedCharge());
						}

					}
					if(chargeReversalSCTL != null){
						if((null != realMsg.getIsReverseCharges()) && (realMsg.getIsReverseCharges())){
							parentSCTL.setChrgRevStatus(CmFinoFIX.SCTLStatus_Reverse_Initiated);
							chargeReversalSCTL.setStatus(CmFinoFIX.SCTLStatus_Reverse_Initiated);
							sctlDao.save(chargeReversalSCTL);

							reversalAmount = reversalAmount.add(chargeReversalSCTL.getTransactionAmount());
							serviceCharge = serviceCharge.add(chargeReversalSCTL.getCalculatedCharge());
						}

					}

					sendNotification(parentSCTL, reversalAmount, serviceCharge, CmFinoFIX.NotificationCode_ReverseTransactionRequestInitiated);
					sctlDao.save(parentSCTL);

					err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				}
				else{
					log.info("Transaction failed because of null value");
		            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		            err.setErrorDescription(MessageText._("Reversal of the Transaction is failed please try again after some time."));
				}
			}
		}
		else {
			log.info("Transaction failed because of null value");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Reversal of the Transaction is failed please try again after some time."));
		}

		return err;
	}
	
	private void sendNotification(ServiceChargeTransactionLog parentSCTL, BigDecimal reversalAmount, BigDecimal serviceCharge, Integer notificationCode) {

		NotificationWrapper notificationWrapper = new NotificationWrapper();
		Integer language = CmFinoFIX.Language_English;
		SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(parentSCTL.getSourceMDN());
		if(smdn != null)
		{
			language = smdn.getSubscriber().getLanguage();
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastName());
		}
		notificationWrapper.setLanguage(language);
		notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notificationWrapper.setCode(notificationCode);
		notificationWrapper.setOriginalTransferID(parentSCTL.getID());
		notificationWrapper.setTransactionAmount(reversalAmount);
		notificationWrapper.setServiceCharge(serviceCharge);

        String message = notificationMessageParserService.buildMessage(notificationWrapper,true);

        smsService.setDestinationMDN(parentSCTL.getSourceMDN());
        smsService.setMessage(message);
        smsService.setNotificationCode(notificationWrapper.getCode());
        smsService.asyncSendSMS();
	}
}

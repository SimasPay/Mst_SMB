package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSReverseTransactionConfirm;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.SystemParametersService;
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
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
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
			ServiceChargeTxnLog amountReversalSCTL = (amtRevSctlId != null) ? sctlDao.getById(amtRevSctlId) : null;
			ServiceChargeTxnLog chargeReversalSCTL = (chrgRevSctlId != null) ? sctlDao.getById(chrgRevSctlId) : null;
			ServiceChargeTxnLog parentSCTL = null;

			if((amountReversalSCTL != null) || (chargeReversalSCTL != null)){
				if(amountReversalSCTL != null){
					parentSCTL = sctlDao.getById(amountReversalSCTL.getParentsctlid().longValue());
				}
				else{
					parentSCTL = sctlDao.getById(chargeReversalSCTL.getParentsctlid().longValue());
				}

				if(parentSCTL != null){
					parentSCTL.setReversalreason(realMsg.getReversalReason());

					BigDecimal reversalAmount = BigDecimal.ZERO;
					BigDecimal serviceCharge = BigDecimal.ZERO;

					if(amountReversalSCTL != null){
						if((null != realMsg.getIsReverseAmount()) && (realMsg.getIsReverseAmount())){
							parentSCTL.setAmtrevstatus(CmFinoFIX.SCTLStatus_Reverse_Initiated.longValue());
							amountReversalSCTL.setStatus(CmFinoFIX.SCTLStatus_Reverse_Initiated);
							sctlDao.save(amountReversalSCTL);
							
							//Before Correcting errors reported by Findbugs:
								//reversalAmount.add(amountReversalSCTL.getTransactionAmount());
								//serviceCharge.add(amountReversalSCTL.getCalculatedCharge());
						
						    //After Correcting the errors reported by Findbugs:reassigned the variables to themselves.
							reversalAmount = reversalAmount.add(amountReversalSCTL.getTransactionamount());
							serviceCharge = serviceCharge.add(amountReversalSCTL.getCalculatedcharge());
						}

					}
					if(chargeReversalSCTL != null){
						if((null != realMsg.getIsReverseCharges()) && (realMsg.getIsReverseCharges())){
							parentSCTL.setChrgrevstatus(CmFinoFIX.SCTLStatus_Reverse_Initiated.longValue());
							chargeReversalSCTL.setStatus(CmFinoFIX.SCTLStatus_Reverse_Initiated);
							sctlDao.save(chargeReversalSCTL);

							reversalAmount = reversalAmount.add(chargeReversalSCTL.getTransactionamount());
							serviceCharge = serviceCharge.add(chargeReversalSCTL.getCalculatedcharge());
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
	
	private void sendNotification(ServiceChargeTxnLog parentSCTL, BigDecimal reversalAmount, BigDecimal serviceCharge, Integer notificationCode) {

		NotificationWrapper notificationWrapper = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(parentSCTL.getSourcemdn());
		if(smdn != null)
		{
			language = (int) smdn.getSubscriber().getLanguage();
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstname());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastname());
		}
		notificationWrapper.setLanguage(language);
		notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notificationWrapper.setCode(notificationCode);
		notificationWrapper.setOriginalTransferID(parentSCTL.getId().longValue());
		notificationWrapper.setTransactionAmount(reversalAmount);
		notificationWrapper.setServiceCharge(serviceCharge);

        String message = notificationMessageParserService.buildMessage(notificationWrapper,true);

        smsService.setDestinationMDN(parentSCTL.getSourcemdn());
        smsService.setMessage(message);
        smsService.setNotificationCode(notificationWrapper.getCode());
        smsService.asyncSendSMS();
	}
}

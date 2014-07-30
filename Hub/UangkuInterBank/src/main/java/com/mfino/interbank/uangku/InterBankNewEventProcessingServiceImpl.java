package com.mfino.interbank.uangku;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.BillPayConstants;
import com.mfino.billpayments.service.BillPayEventProcessingService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;

/**
 * 
 * @author HemanthKumar
 *
 */
public class InterBankNewEventProcessingServiceImpl extends BillPaymentsBaseServiceImpl implements BillPayEventProcessingService
{

	private Log log = LogFactory.getLog(this.getClass());
	
	private BillPaymentsService billPaymentsService;

	private SystemParametersService systemParametersService ;

	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}
	public void setSystemParametersService(
			SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}

	private SubscriberService subscriberService ;
		
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}
	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage processEvent(@Body MCEMessage mceMessage, @Header("eventContext") String eventContext) {
		log.info("InterBankNewEventProcessingServiceImpl : processEvent BEGIN eventContext=" + eventContext + "mceMessage="+mceMessage);
		boolean sendNotification = false;
		if(mceMessage.getResponse() instanceof BackendResponse){
			BackendResponse backendResponse = (BackendResponse)mceMessage.getResponse();
			
			if(BillPayConstants.SRC_SUSPENSE_INQ_FAILED.equals(eventContext)){
				sendNotification = true;
			}
			else if(BillPayConstants.SRC_SUSPENSE_CONFIRMATION_FAILED.equals(eventContext)){
				sendNotification = true;
			}
			else if(BillPayConstants.SRC_SUSPENSE_INQ_SUCCESS.equals(eventContext)){
				sendNotification = true;
				if(CmFinoFIX.ResponseCode_Success.equals(backendResponse.getResult())){
					backendResponse.setInternalErrorCode(NotificationCodes.IBTInquiry.getInternalErrorCode());
				}
				else{
					backendResponse.setInternalErrorCode(NotificationCodes.IBTFailed.getInternalErrorCode());
				}
			}
			else if(BillPayConstants.BILLER_INQUIRY_COMPLETED.equals(eventContext)){
				sendNotification = true;
				if(CmFinoFIX.ResponseCode_Success.equals(backendResponse.getResult())){
					backendResponse.setInternalErrorCode(NotificationCodes.IBTInquiry.getInternalErrorCode());
				}
				else{
					backendResponse.setInternalErrorCode(NotificationCodes.IBTFailed.getInternalErrorCode());
				}
			}
			else if(BillPayConstants.BILLER_CONFIRMATION_FAILED.equals(eventContext)){
				sendNotification = true;
				backendResponse.setInternalErrorCode(NotificationCodes.IBTFailed.getInternalErrorCode());
			}
			
			else if(BillPayConstants.BILLER_CONFIRMATION_SUCCESSFUL.equals(eventContext)){
				sendNotification = true;
				backendResponse.setInternalErrorCode(NotificationCodes.IBTConfirmation.getInternalErrorCode());
			}
			else if(BillPayConstants.SUSPENSE_DEST_CONFIRMATION_FAILED.equals(eventContext)){
				//sendNotification = true;
				//backendResponse.setInternalErrorCode(NotificationCodes.BillpaymentFailed.getInternalErrorCode());
			}
			else if(BillPayConstants.SUSPENSE_DEST_CONFIRMATION_SUCCESS.equals(eventContext)){
//				sendNotification = true;
//				backendResponse.setInternalErrorCode(NotificationCodes.BillpaymentConfirmationSuccessful.getInternalErrorCode());
			}
			else if(BillPayConstants.BILLER_CONFIRMATION_PENDING.equals(eventContext)){
				//TODO update bill pay status to BILLER_CONFIRMATION_PENDING
			}
			else if(BillPayConstants.BILLER_INQUIRY_PENDING.equals(eventContext)){
				//TODO update bill pay status BILLER_INQUIRY_PENDING
			}
		}
		
		if(!sendNotification){
			mceMessage.setResponse(null);
		}
		else{
			constructResponse(mceMessage);
		}
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage constructResponse(MCEMessage mceMessage){
		log.info("InterBankNewEventProcessingServiceImpl :: constructResponse mceMessage$$$="+mceMessage);

		Long sctlId = ((CMBase)mceMessage.getRequest()).getServiceChargeTransactionLogID();
		BillPayments billPaymentsRecord = billPaymentsService.getBillPaymentsRecord(sctlId);

		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
		
		if((null != billPaymentsRecord) && (null != sctl)){
			BackendResponse response = (BackendResponse)mceMessage.getResponse();
			response.setCurrency(systemParametersService.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
			response.setAmount(billPaymentsRecord.getAmount());
			response.setCharges(billPaymentsRecord.getCharges());
			response.setBillerCode(billPaymentsRecord.getBillerCode());
			response.setSenderMDN(sctl.getSourceMDN());
			SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(sctl.getSourceMDN());
			if(smdn != null)
			{
				response.setFirstName(smdn.getSubscriber().getFirstName());
				response.setLastName(smdn.getSubscriber().getLastName());
			}
			
			response.setSourceMDN(sctl.getSourceMDN());
			response.setReceiverMDN(billPaymentsRecord.getInvoiceNumber());

			if(!(subscriberService.normalizeMDN(sctl.getSourceMDN()).equals(subscriberService.normalizeMDN(billPaymentsRecord.getInvoiceNumber())))){
				response.setOnBehalfOfMDN(billPaymentsRecord.getInvoiceNumber());	
			}
			
			response.setTransferID(response.getTransferID());
			response.setParentTransactionID(response.getParentTransactionID());
			response.setServiceChargeTransactionLogID(sctl.getID());
		}

		return mceMessage;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage SRC_SUSPENSE_INQ_FAILED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "SRC_SUSPENSE_INQ_FAILED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage SRC_SUSPENSE_CONFIRMATION_FAILED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "SRC_SUSPENSE_CONFIRMATION_FAILED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage BILLER_INQUIRY_COMPLETED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "BILLER_INQUIRY_COMPLETED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage BILLER_CONFIRMATION_FAILED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "BILLER_CONFIRMATION_FAILED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage BILLER_CONFIRMATION_SUCCESSFUL(MCEMessage mceMessage) {
		return processEvent(mceMessage, "BILLER_CONFIRMATION_SUCCESSFUL");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public MCEMessage SRC_SUSPENSE_INQ_SUCCESS(MCEMessage mceMessage) {
	    return processEvent(mceMessage, "SRC_SUSPENSE_INQ_SUCCESS");
    }

	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage BILLER_CONFIRMATION_PENDING(MCEMessage mceMessage){
		return processEvent(mceMessage, "BILLER_CONFIRMATION_PENDING");
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage BILLER_INQUIRY_PENDING(MCEMessage mceMessage){
		return processEvent(mceMessage, "BILLER_INQUIRY_PENDING");
	}
}

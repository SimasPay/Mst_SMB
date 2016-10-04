package com.mfino.billpayments.flashiz;

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
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;

public class BillPayEventProcessingServiceFlashizImpl extends BillPaymentsBaseServiceImpl implements BillPayEventProcessingService
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
		log.info("BillPayEventProcessingServiceImpl : processEvent BEGIN eventContext=" + eventContext + "mceMessage="+mceMessage);
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
					backendResponse.setInternalErrorCode(NotificationCodes.QRpaymentInquirySuccessful.getInternalErrorCode());
				}
				else{
					backendResponse.setInternalErrorCode(NotificationCodes.QRpaymentFailed.getInternalErrorCode());
				}
			}
			else if(BillPayConstants.BILLER_INQUIRY_COMPLETED.equals(eventContext)){
				sendNotification = true;
				if(CmFinoFIX.ResponseCode_Success.equals(backendResponse.getResult())){
					backendResponse.setInternalErrorCode(NotificationCodes.QRpaymentInquirySuccessful.getInternalErrorCode());
				}
				else{
					backendResponse.setInternalErrorCode(NotificationCodes.QRpaymentFailed.getInternalErrorCode());
				}
			}
			else if(BillPayConstants.BILLER_CONFIRMATION_FAILED.equals(eventContext)){
				sendNotification = true;
				backendResponse.setInternalErrorCode(NotificationCodes.QRpaymentFailed.getInternalErrorCode());
			}
			
			else if(BillPayConstants.BILLER_CONFIRMATION_SUCCESSFUL.equals(eventContext)){
				sendNotification = true;
				backendResponse.setInternalErrorCode(NotificationCodes.QRpaymentConfirmationSuccessful.getInternalErrorCode());
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
		log.info("BillPayEventProcessingServiceImpl :: constructResponse mceMessage$$$="+mceMessage);

		Long sctlId = ((CMBase)mceMessage.getRequest()).getServiceChargeTransactionLogID();
		BillPayments billPaymentsRecord = billPaymentsService.getBillPaymentsRecord(sctlId);

		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		if((null != billPaymentsRecord) && (null != sctl)){
			BackendResponse response = (BackendResponse)mceMessage.getResponse();
			response.setCurrency(systemParametersService.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
			response.setAmount(billPaymentsRecord.getAmount());
			response.setCharges(billPaymentsRecord.getCharges());
			response.setBillerCode(billPaymentsRecord.getBillercode());
			response.setSenderMDN(sctl.getSourcemdn());
			SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(sctl.getSourcemdn());
			if(smdn != null)
			{
				response.setFirstName(smdn.getSubscriber().getFirstname());
				response.setLastName(smdn.getSubscriber().getLastname());
			}
			
			response.setSourceMDN(sctl.getSourcemdn());
			response.setReceiverMDN(sctl.getDestmdn());

//			if(!(subscriberService.normalizeMDN(sctl.getSourceMDN()).equals(subscriberService.normalizeMDN(billPaymentsRecord.getInvoiceNumber())))){
//				response.setOnBehalfOfMDN(billPaymentsRecord.getInvoiceNumber());	
//			}
			
			response.setTransferID(response.getTransferID());
			response.setParentTransactionID(response.getParentTransactionID());
			response.setServiceChargeTransactionLogID(sctl.getId().longValue());
			if(billPaymentsRecord.getAmount()!=null)
				response.setNominalAmount(billPaymentsRecord.getAmount());
			if(billPaymentsRecord.getInfo3()!=null)
				response.setAdditionalInfo(billPaymentsRecord.getInfo3());
			if(billPaymentsRecord.getOperatorcharges() !=null)
				response.setCharges(sctl.getCalculatedcharge().add(billPaymentsRecord.getOperatorcharges()));
			if(billPaymentsRecord.getInvoicenumber() !=null)
				response.setInvoiceNumber(billPaymentsRecord.getInvoicenumber());
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

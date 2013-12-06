package com.mfino.fortis.frsc;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.BillPayConstants;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.billpayments.service.impl.BillPayEventProcessingServiceImpl;
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
import com.mfino.service.SystemParametersService;
import com.mfino.util.MfinoUtil;
import com.mfino.util.SystemParametersUtil;

/**
 * @author Bala Sunku
 *
 */
public class FRSCPaymentEventProcessingServiceImpl extends BillPayEventProcessingServiceImpl {

	private Log log = LogFactory.getLog(this.getClass());
	
	private BillPaymentsService billPaymentsService;
	
	private SystemParametersService systemParametersService;

		
	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}
	public void setSystemParametersService(
			SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage processEvent(@Body MCEMessage mceMessage, @Header("eventContext") String eventContext) {
		log.info("FRSCPaymentEventProcessingServiceImpl : processEvent BEGIN eventContext=" + eventContext + "mceMessage="+mceMessage);
		boolean sendNotification = false;
		
		if(mceMessage.getResponse() instanceof BackendResponse){
			BackendResponse backendResponse = (BackendResponse)mceMessage.getResponse();
			
			if(BillPayConstants.SRC_SUSPENSE_INQ_FAILED.equals(eventContext)){
				sendNotification = true;
			}
			else if(BillPayConstants.SRC_SUSPENSE_CONFIRMATION_FAILED.equals(eventContext)){
				sendNotification = true;
			}
			else if(BillPayConstants.BILLER_INQUIRY_COMPLETED.equals(eventContext)){
				sendNotification = true;
				if(CmFinoFIX.ResponseCode_Success.equals(backendResponse.getResult())){
					backendResponse.setInternalErrorCode(NotificationCodes.FRSCPaymentInquirySuccessful.getInternalErrorCode());
				}
				else{
					backendResponse.setInternalErrorCode(NotificationCodes.FRSCPaymentFailed.getInternalErrorCode());
				}
			}
			else if(BillPayConstants.BILLER_CONFIRMATION_FAILED.equals(eventContext)){
				sendNotification = true;
				backendResponse.setInternalErrorCode(NotificationCodes.FRSCPaymentFailed.getInternalErrorCode());
			}
			else if(BillPayConstants.BILLER_CONFIRMATION_SUCCESSFUL.equals(eventContext)){
				sendNotification = true;
				backendResponse.setInternalErrorCode(NotificationCodes.FRSCPaymentConfirmationSuccessful.getInternalErrorCode());
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
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage constructResponse(MCEMessage mceMessage){
		log.info("FRSCPaymentEventProcessingServiceImpl :: constructResponse mceMessage$$$="+mceMessage);

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
			response.setOnBehalfOfMDN(billPaymentsRecord.getInfo1());// Getting the OnBehalfOfMDN from BillPayments record
			response.setTransferID(response.getTransferID());
			response.setParentTransactionID(response.getParentTransactionID());
			response.setServiceChargeTransactionLogID(sctl.getID());
			response.setInvoiceNumber(billPaymentsRecord.getInvoiceNumber());
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
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage SRC_SUSPENSE_INQ_FAILED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "SRC_SUSPENSE_INQ_FAILED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage SRC_SUSPENSE_CONFIRMATION_FAILED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "SRC_SUSPENSE_CONFIRMATION_FAILED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage BILLER_INQUIRY_COMPLETED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "BILLER_INQUIRY_COMPLETED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage BILLER_CONFIRMATION_FAILED(MCEMessage mceMessage) {
		return processEvent(mceMessage, "BILLER_CONFIRMATION_FAILED");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage BILLER_CONFIRMATION_SUCCESSFUL(MCEMessage mceMessage) {
		return processEvent(mceMessage, "BILLER_CONFIRMATION_SUCCESSFUL");
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
    public MCEMessage SRC_SUSPENSE_INQ_SUCCESS(MCEMessage mceMessage) {
	    // TODO Auto-generated method stub
	    return null;
    }

	
	public MCEMessage BILLER_CONFIRMATION_PENDING(MCEMessage mceMessage){
		return processEvent(mceMessage, "BILLER_CONFIRMATION_PENDING");
	}
	
	public MCEMessage BILLER_INQUIRY_PENDING(MCEMessage mceMessage){
		return processEvent(mceMessage, "BILLER_INQUIRY_PENDING");
	}
}

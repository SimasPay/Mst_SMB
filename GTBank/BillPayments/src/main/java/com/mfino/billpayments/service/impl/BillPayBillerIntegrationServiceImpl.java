package com.mfino.billpayments.service.impl;

import static com.mfino.mce.core.util.MCEUtil.SERVICE_TIME_OUT;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPayBillerIntegrationService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.service.ServiceChargeTransactionLogService;

/**
 * @author Sasi
 *
 */
public class BillPayBillerIntegrationServiceImpl extends BillPaymentsBaseServiceImpl implements BillPayBillerIntegrationService {
	
	protected BillPaymentsService billPaymentsService;
	
	protected ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	public ServiceChargeTransactionLogService getServiceChargeTransactionLogService() {
		return serviceChargeTransactionLogService;
	}

	public void setServiceChargeTransactionLogService(
			ServiceChargeTransactionLogService serviceChargeTransactionLogService) {
		this.serviceChargeTransactionLogService = serviceChargeTransactionLogService;
	}

	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage preBillerInquiry(MCEMessage mceMessage) {
		log.info("BillPayBillerIntegrationServiceImpl :: preBillerInquiry : mceMessage="+mceMessage);

		CMBase response = (CMBase)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_INQUIRY_PENDING);
		billPaymentsService.saveBillPayment(billPayments);
		
		return mceMessage;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage preBillerConfirmation(MCEMessage mceMessage) {
		log.info("BillPayBillerIntegrationServiceImpl :: preBillerConfirmation : mceMessage="+mceMessage);

		CMBase response = (CMBase)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_PENDING);
		billPaymentsService.saveBillPayment(billPayments);
		
		return mceMessage;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage handleBillPayInquiryResponse(MCEMessage mceMessage) {
		log.info("BillPayBillerIntegrationServiceImpl :: handleBillPayInquiryResponse : mceMessage="+mceMessage);

		BillPayResponse response = (BillPayResponse)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);

		if(SERVICE_TIME_OUT.equals(response.getInResponseCode())){
			billPayments.setINResponseCode(SERVICE_TIME_OUT);
			billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_INQUIRY_PENDING);
		}
		else
		{
			if(CmFinoFIX.ResponseCode_Success.equals(response.getResponse())){
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_INQUIRY_COMPLETED);
			}
			else{
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_INQUIRY_FAILED);
			}
			billPayments.setINTxnId(response.getInTxnId());
			billPayments.setINResponseCode(response.getInResponseCode());
			
			billPayments.setResponseCode(response.getResponse());
			if((null != response.getInfo3()) && !("".equalsIgnoreCase(response.getInfo3()))){
				billPayments.setInfo3(response.getInfo3());
			}
		}
		
		billPaymentsService.saveBillPayment(billPayments);
		
		mceMessage.setResponse(response);
		
		return mceMessage;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage handleBillPayConfirmationResponse(MCEMessage mceMessage) {
		log.info("BillPayBillerIntegrationServiceImpl :: handleBillPayConfirmationResponse : mceMessage="+mceMessage);

		BillPayResponse response = (BillPayResponse)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		if(SERVICE_TIME_OUT.equals(response.getInResponseCode())){
			billPayments.setINResponseCode(SERVICE_TIME_OUT);
			billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_PENDING);
		}
		else{
			if(CmFinoFIX.ResponseCode_Success.equals(response.getResponse())){
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_COMPLETED);
			}
			else{
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_FAILED);
			}
			billPayments.setINTxnId(response.getInTxnId());
			billPayments.setINResponseCode(response.getInResponseCode());
			billPayments.setResponseCode(response.getResponse());
		}
		
		billPaymentsService.saveBillPayment(billPayments);
		
		mceMessage.setResponse(response);
		
		return mceMessage;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

}

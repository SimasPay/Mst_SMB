package com.mfino.billpayments.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.billpayments.service.BillPaymentsSuspenceClearanceService;
import com.mfino.dao.AutoReversalsDao;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;
import com.mfino.service.TransactionChargingService;


/**
 * @author Sasi
 *
 */
public class BillPaymentsSuspenceClearanceServiceImpl extends BillPaymentsBaseServiceImpl implements BillPaymentsSuspenceClearanceService{
	
	private BillPaymentsService billPaymentsService;
	protected ServiceChargeTxnLog sctl;
	
	protected TransactionChargingService transactionChargingService;

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}


	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

		protected String suspenseToDestinationInquiryQueue = "jms:moneyTransferInquirySuspenseToDestination";
	protected String suspenseAndChargesToSourceReversalQueue = "jms:suspenseAndChargesToSourceReversal";
	protected Long timeoutForAutoReversalInMinutes=24*60l;
	
	public void setTimeoutForAutoReversalInMinutes(Long timeoutForAutoReversalInMinutes) {
		this.timeoutForAutoReversalInMinutes = timeoutForAutoReversalInMinutes;
	}


	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<MCEMessage> clearSuspenceAccounts() {
		log.info("BillPaymentsSuspenseClearanceServiceImpl : clearSuspenseAccounts() BEGIN");

		List<MCEMessage> mceMessageList = new ArrayList<MCEMessage>();
		
		Collection<Integer> suspenseToDestFailedStatuses = new HashSet<Integer>();
		addValidForClearanceStates(suspenseToDestFailedStatuses);
		
		Collection<BillPayments> billPayments = billPaymentsService.getBillPaymentsWithStatus(suspenseToDestFailedStatuses); 
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();


		for(BillPayments billPayment : billPayments){
			MCEMessage mceMessage = new MCEMessage();

			CMBillPay cmBillPay = new CMBillPay();
			cmBillPay.setServiceChargeTransactionLogID(billPayment.getSctlId());
			cmBillPay.setIntegrationCode(billPayment.getIntegrationcode());
			mceMessage.setRequest(cmBillPay);
			sctl = sctlDao.getById(billPayment.getSctlId());
			handleClearanceStates(mceMessageList, billPayment, mceMessage);
		}
		
		log.info("BillPaymentsSuspenseClearanceServiceImpl : clearSuspenseAccounts() END");
		
		return mceMessageList;
	}

	public void handleClearanceStates(List<MCEMessage> mceMessageList, BillPayments billPayment, MCEMessage mceMessage) {
	    if((billPayment.getBillpaystatus().equals(CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_INQ_FAILED)) ||
	    		(billPayment.getBillpaystatus().equals(CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_FAILED))){
			log.info("Adding to moneyTransferInquirySuspenseToDestinationqueue Bill Payment with SCTLID: "+ billPayment.getSctlId() + 
						" and billpay status: " + billPayment.getBillpaystatus());
	    	setSuspenseToDestinationQueue(mceMessage);
	    	mceMessageList.add(mceMessage);
			transactionChargingService.confirmTheTransaction(sctl);
	    }
	    else if(billPayment.getBillpaystatus().equals(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_FAILED)){
	    	handleConfirmationFailedState(mceMessageList, billPayment, mceMessage);
	    }
    }

	public void handleConfirmationFailedState(List<MCEMessage> mceMessageList, BillPayments billPayment, MCEMessage mceMessage) {
	    
	    AutoReversalsDao autoRevDao = DAOFactory.getInstance().getAutoReversalsDao();
	    AutoReversals autoRev = autoRevDao.getBySctlId(billPayment.getSctlId());
	    billPayment.setBillpaystatus(CmFinoFIX.BillPayStatus_BILLPAY_FAILED.longValue());
	    billPaymentsService.saveBillPayment(billPayment);
	    setSuspenseAndChargesToReversalQueue(mceMessage);
	    
	    Timestamp now = new Timestamp();
	    if((autoRev == null) && ((now.getTime() - sctl.getCreatetime().getTime()) > timeoutForAutoReversalInMinutes* 60 * 1000)){
			log.info("Adding to suspenseAndChargesToSourceReversalqueue Bill Payment with SCTLID: "+ billPayment.getSctlId() + 
						" and billpay status: " + billPayment.getBillpaystatus());
		
	    	mceMessageList.add(mceMessage);// add this message to reversal queue only if its 24 hour old
			transactionChargingService.failTheTransaction(sctl, "Fails the transaction as Biller confirmation failed");
	    }
    }
	
	public void addValidForClearanceStates(Collection<Integer> suspenseToDestFailedStatuses){

		suspenseToDestFailedStatuses.add(CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_INQ_FAILED);
		suspenseToDestFailedStatuses.add(CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_FAILED);
		suspenseToDestFailedStatuses.add(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_FAILED);

	}
	
	public void setSuspenseToDestinationQueue(MCEMessage mceMessage){
		mceMessage.setDestinationQueue(suspenseToDestinationInquiryQueue);
	}
	
	public void setSuspenseAndChargesToReversalQueue(MCEMessage mceMessage){
		mceMessage.setDestinationQueue(suspenseAndChargesToSourceReversalQueue);
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}
	
		public String getSuspenseToDestinationInquiryQueue() {
		return suspenseToDestinationInquiryQueue;
	}

	public void setSuspenseToDestinationInquiryQueue(
			String suspenseToDestinationInquiryQueue) {
		this.suspenseToDestinationInquiryQueue = suspenseToDestinationInquiryQueue;
	}

	public String getSuspenseAndChargesToSourceReversalQueue() {
		return suspenseAndChargesToSourceReversalQueue;
	}

	public void setSuspenseAndChargesToSourceReversalQueue(
			String suspenseAndChargesToSourceReversalQueue) {
		this.suspenseAndChargesToSourceReversalQueue = suspenseAndChargesToSourceReversalQueue;
	}
}

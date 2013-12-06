package com.mfino.billpayments.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;

/**
 * @author Sasi
 * This Service is for any financial transactions during bill payments.
 * Service Activator(Pattern) with some additional business logic for Bank Service.
 */
public interface BillPayMoneyTransferService {
	
	/**
	 * Do Transfer inquiry from source pocket to suspense/destination of partner.
	 * @param billPayInquiry
	 * @return
	 */
	public MCEMessage billPayMoneyTransferInquiry(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferInquirySourceToSuspense(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferInquirySuspenseToDestination(MCEMessage mceMessage);
	
	/**
	 * After inquiry is completed, call this method (calls onInquiryFromBank in bank service)
	 * @param mceMessage
	 * @return
	 */
	public MCEMessage billPayMoneyTransferInquiryCompleted(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferInquirySourceToSuspenseCompleted(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferInquirySuspenceToDestinationCompleted(MCEMessage mceMessage);
	
	/**
	 * Based on sctlid, confirm transfer of funds, subscriber (source) -> suspense/destination (partner)
	 * @param billPayConfirmation
	 * @return
	 */
	public MCEMessage billPayMoneyTransferConfirmation(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferConfirmationSourceToSuspense(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferConfirmationSuspenseToDestination(MCEMessage mceMessage);
	
	/**
	 * Confirmation complete. (calls onConfirmationFromBank in bank service)
	 * @param mceMessage
	 * @return
	 */
	public MCEMessage billPayMoneyTransferConfirmationCompleted(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferConfirmationSourceToSuspenseCompleted(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyTransferConfirmationSuspenseToDestinationCompleted(MCEMessage mceMessage);
	
	public MCEMessage billPayMoneyBillerInquiryFail(MCEMessage mceMessage);

	public MCEMessage suspenseAndChargesToSourceReversal(MCEMessage mceMessage);
	
	public void setReversalResponseQueue(String reversalResponseQueue);
	public void setSourceToSuspenseBankResponseQueue(String sourceToSuspenseBankResponseQueue);
	public void setSuspenseToDestBankResponseQueue(String suspenseToDestBankResponseQueue);

	public void setBankService(BankService bankService) ;
	public void setBillPaymentsService(BillPaymentsService billPaymentsService);
	

}

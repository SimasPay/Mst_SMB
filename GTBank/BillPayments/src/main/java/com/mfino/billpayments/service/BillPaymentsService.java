package com.mfino.billpayments.service;

import java.util.Collection;

import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public interface BillPaymentsService {

	public BillPayments createBillPayments(CMBillPayInquiry billPayInquiry);
	
	public BillPayments getBillPaymentsRecord(Long sctlID);
	
	public BillPayments saveBillPayment(BillPayments billPayments);

	public void updateBillPayStatus(Long sctlId, Integer billPayStatus);
	
	public void updateBillPayStatus(Long sctlId, Integer billPayStatus, Timestamp ts);
	
	public void updateBillPayStatus(MCEMessage mceMessage, String transferType);
	
	public BillPayments getBillPaymentsRecordWrapInTxn(Long sctlId);
	
	public Collection<BillPayments> getBillPaymentsWithStatus(Collection<Integer> billPayStatus);
}

package com.mfino.billpayments.service.activator;

import org.apache.camel.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public class BillPaymentsServiceActivator {
	
	public Log log = LogFactory.getLog(this.getClass());
	
	private BillPaymentsService billPaymentsService;
	
	public void createBillPayments(MCEMessage mceMessage){
		log.info("BillPaymentsServiceActivator :: createBillPayments");
		CMBillPayInquiry billPayInquiry = (CMBillPayInquiry)mceMessage.getRequest();
		billPaymentsService.createBillPayments(billPayInquiry);
	}
	
	public void updateBillPayStatus(MCEMessage mceMessage, @Header("sctlId") Long sctlId, @Header("billPayStatus") Integer billPayStatus){
		log.info("BillPaymentsServiceActivator :: updateBillPayStatus sctlId="+sctlId+", billPayStatus="+billPayStatus);
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		billPayments.setBillPayStatus(billPayStatus);
		billPaymentsService.saveBillPayment(billPayments);
	}
}

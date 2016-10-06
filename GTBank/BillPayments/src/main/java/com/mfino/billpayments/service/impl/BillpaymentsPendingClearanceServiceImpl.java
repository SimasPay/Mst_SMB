package com.mfino.billpayments.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.billpayments.service.BillpaymentsPendingClearanceService;
import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPayment;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public class BillpaymentsPendingClearanceServiceImpl extends BillPaymentsBaseServiceImpl implements BillpaymentsPendingClearanceService{
	
	private BillPaymentsService billPaymentsService;
	private String integrationCode;
	private static long TIME_TO_RESEND = 15*60*1000;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<MCEMessage> clearBillerConfirmationPendingTransactions(){
		log.info("BillpaymentsPendingClearanceServiceImpl getBillerConfirmationPendingTransactions() BEGIN");
		Timestamp now = new Timestamp();
		Collection<Integer> billPayPendingStatuses = new ArrayList<Integer>();
		billPayPendingStatuses.add(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_PENDING);
		
		List<MCEMessage> pendingMessages = new ArrayList<MCEMessage>();
		
		BillPaymentsDAO billPaymentsDao = DAOFactory.getInstance().getBillPaymentDAO();
		BillPaymentsQuery billPayQuery = new BillPaymentsQuery();
		billPayQuery.setBillPayStatuses(billPayPendingStatuses);
		billPayQuery.setIntegrationCode(getIntegrationCode());
		
		Collection<BillPayments> pendingBillPayments = billPaymentsDao.get(billPayQuery);
		
		for(BillPayments billPayment : pendingBillPayments){
			/* changes done to accomdate clickatell transaction to avoid clashes between running transactions and pending transaction -shashank*/
			if(now.getTime() - billPayment.getCreatetime().getTime() > TIME_TO_RESEND){
				MCEMessage mceMessage = new MCEMessage();
				CMBillPayment cmBillPay = new CMBillPayment();
				cmBillPay.setServiceChargeTransactionLogID(billPayment.getServiceChargeTxnLog().getId().longValue());
				if(billPayment.getInfo4() != null)
					cmBillPay.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Payment);
				else 
					cmBillPay.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Payment_Topup);

				mceMessage.setRequest(cmBillPay);
				pendingMessages.add(mceMessage);
			}
		}
		
		return pendingMessages;
	}

 	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public String getIntegrationCode() {
		return integrationCode;
	}

	public void setIntegrationCode(String integrationCode) {
		this.integrationCode = integrationCode;
	}
}

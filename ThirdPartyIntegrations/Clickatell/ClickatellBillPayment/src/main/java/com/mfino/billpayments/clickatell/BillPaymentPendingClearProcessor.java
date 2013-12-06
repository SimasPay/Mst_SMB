package com.mfino.billpayments.clickatell;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.mce.core.MCEMessage;
import com.mfino.service.SubscriberService;

/**
 * 
 * @author shashank
 *
 */

public class BillPaymentPendingClearProcessor implements ClickatellProcessor {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private BillPaymentsService billPaymentsService;
	private SubscriberService subscriberService ;
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}
	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
	@Override
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("BillPaymentProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage(); 
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecordWrapInTxn(sctlId);

		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
		sctl.setStatus(CmFinoFIX.SCTLStatus_Processing);
		sctlDao.save(sctl);
		
		CMCommodityTransferToOperator toOperator = new CMCommodityTransferToOperator();
		toOperator.setSourceMDN(billPayments.getSourceMDN());

		toOperator.setDestMDN(subscriberService.normalizeMDN(billPayments.getInvoiceNumber()));
		toOperator.setPaymentInquiryDetails(requestFix.getPaymentInquiryDetails());
		Integer billerPartnerType = CmFinoFIX.BillerPartnerType_Payment_Full;
		//FIXME set this for postpaid and topup properly
		
		if(CmFinoFIX.BillerPartnerType_Payment_Full.equals(billerPartnerType)
				||CmFinoFIX.BillerPartnerType_Payment_Partial.equals(billerPartnerType))
			toOperator.setSourceTransactionUICategory(CmFinoFIX.TransactionUICategory_Bank_Channel_Payment);//for PostPaid
		else
			toOperator.setSourceTransactionUICategory(CmFinoFIX.TransactionUICategory_Bank_Channel_Topup);//for Prepaid
		log.info("BillPaymentPendingClearProcessor :: constructRequestMessage() BEGIN mceMessage"+billPayments.getAmount());
		toOperator.setAmount(billPayments.getAmount());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		toOperator.setTransferID(sctlId);
		toOperator.setTransactionID(sctlId);
		toOperator.setParentTransactionID(sctlId);
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);//routing code for clickatell
		toOperator.setBillPaymentReferenceID(billPayments.getInvoiceNumber());//FIXME use getBillPaymentReferenceID()
		toOperator.setProductIndicatorCode(billPayments.getPartnerBillerCode()); // Identify the product for which bill payment is being done. 
		toOperator.setSourceMsgType(201);

		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:clickatellBillPaymentResponseQueue?disableReplyTo=true");
		log.info("BillPaymentPendingClearProcessor :: constructRequestMessage() END");
		return replyMessage;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	@Override
	public MCEMessage constructReplyMessage(MCEMessage mceMessage) {
		// TODO Auto-generated method stub
		return null;
	}

}

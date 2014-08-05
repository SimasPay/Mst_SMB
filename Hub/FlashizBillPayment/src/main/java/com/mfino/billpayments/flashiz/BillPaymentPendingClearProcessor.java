package com.mfino.billpayments.flashiz;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMQRPayment;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;

/**
 * 
 * @author HemanthKumar
 *
 */

public class BillPaymentPendingClearProcessor implements FlashizProcessor {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private BillPaymentsService billPaymentsService;
	private SubscriberService subscriberService ;
	private SubscriberMdnService subscriberMdnService ;
	
	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}
	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}
	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("BillPaymentPendingClearProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage(); 
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
		sctl.setStatus(CmFinoFIX.SCTLStatus_Processing);
		sctlDao.save(sctl);
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecordWrapInTxn(sctlId);
		CMPaymentAcknowledgementToBank toOperator = new CMPaymentAcknowledgementToBank();
		toOperator.setSourceMDN(billPayments.getSourceMDN());
		toOperator.setDestMDN(subscriberService.normalizeMDN(billPayments.getInvoiceNumber()));
		toOperator.setPaymentInquiryDetails(requestFix.getPaymentInquiryDetails());
		Integer billerPartnerType = CmFinoFIX.BillerPartnerType_Payment_Full;
		toOperator.setAmount(billPayments.getAmount());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		toOperator.setTransactionID(sctlId);
		toOperator.setParentTransactionID(sctlId);
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_FLASHIZ);//routing code for flashiz
		toOperator.setBillPaymentReferenceID(billPayments.getInvoiceNumber());
		toOperator.setProductIndicatorCode(billPayments.getPartnerBillerCode());
		SubscriberMDN smdn = subscriberMdnService.getByMDN(toOperator.getSourceMDN());
		toOperator.setUserAPIKey(smdn.getUserAPIKey());
		toOperator.setMerchantData(billPayments.getInfo1());
		toOperator.setInvoiceNo(billPayments.getInvoiceNumber());
		toOperator.setBillerCode(billPayments.getBillerCode());
		toOperator.setIsAdvice(true);
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:flashizBillPaymentResponseQueue?disableReplyTo=true");
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

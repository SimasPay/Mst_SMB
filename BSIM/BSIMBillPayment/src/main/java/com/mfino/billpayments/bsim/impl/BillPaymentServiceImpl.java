package com.mfino.billpayments.bsim.impl;


import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.bsim.BillPaymentService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionChargeLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChargeType;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.NoISOResponseMsg;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountFromBiller;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMQRPayment;
import com.mfino.fix.CmFinoFIX.CMQRPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.ExternalResponseCodeHolder;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.mce.core.util.ResponseCodes;
import com.mfino.mce.core.util.StringUtilities;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class BillPaymentServiceImpl extends BillPaymentsBaseServiceImpl implements BillPaymentService{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	protected BankService bankService;
	protected BillPaymentsService billPaymentsService;
	protected SubscriberService subscriberService;
	private TransactionChargingService transactionChargingService ;
	private Set<String> plnPrepaidBillers;
	private Set<String> plnPostpaidBillers;
	private Set<String> plnNonTaglisBillers;
	
	private Map<String,String> plnPrepaidSMSKeyValues;
	private Map<String,String> plnpostPaidSMSKeyValues;
	private Map<String,String> plnNonTaglisSMSKeyValues;
	
	
	private Set<String> keywords;
	
	public Set<String> getKeywords() {
		return keywords;
	}
		
	private List<String> plnPrepaidSMSKeyWords;
	private List<String> plnPostpaidSMSKeyWords;
	private List<String> plnNonTaglisSMSKeyWords;

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}
	
	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	protected  CMMoneyTransferToBank inquiryResponse;
	
	
	protected String sourceToDestInquiryQueue = "jms:bsimsourceToDestInquiryQueue?disableReplyTo=true";
	protected String sourceToDestQueue = "jms:bsimsourceToDestQueue?disableReplyTo=true";
	protected String reversalQueue = "jms:bsimreversalQueue";
	protected String AmountFromBankQueue = "jms:bsimAmountFromBankQueue";
	
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayMoneyTransferInquirySourceToDestination(MCEMessage mceMessage)
	{
		log.info("BillPaymentServiceImpl :: billPayMoneyTransferInquirySourceToDestination mceMessage="+mceMessage);
		
        CMBillPayInquiry billPayInquiry = (CMBillPayInquiry)mceMessage.getRequest();
		CFIXMsg bankInqres;
		try{
			bankInqres = bankService.onTransferInquiryToBank(billPayInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			bankInqres = new BackendResponse();
			((BackendResponse) bankInqres).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) bankInqres).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if(bankInqres instanceof CMTransferInquiryToBank){
		mceMessage.setRequest(billPayInquiry);
		CMTransferInquiryToBank inquiryResponse = (CMTransferInquiryToBank)bankInqres;
		mceMessage.setDestinationQueue(sourceToDestInquiryQueue);
		CMBSIMBillPaymentInquiryToBank response = new CMBSIMBillPaymentInquiryToBank();
		response.setAmount(inquiryResponse.getAmount());
		String mdn = StringUtilities.leftPadWithCharacter(billPayInquiry.getSourceMDN(), 13, "0");
		String mdngen = MfinoUtil.CheckDigitCalculation(mdn);
		
		Long sctlID = inquiryResponse.getServiceChargeTransactionLogID();
		TransactionChargeLogDAO tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();
		BigDecimal serviceCharge = new BigDecimal(0);
		BigDecimal tax = new BigDecimal(0);
		
		if(sctlID != null){
			List <TransactionChargeLog> tclList = tclDAO.getBySCTLID(sctlID);
			if(CollectionUtils.isNotEmpty(tclList)){
				for(Iterator<TransactionChargeLog> it = tclList.iterator();it.hasNext();){
					TransactionChargeLog tcl = it.next();
					TransactionCharge txnCharge=tcl.getTransactionCharge();
					ChargeType chargeType = txnCharge.getChargeType();
					String chargeTypeName = chargeType.getName();
					if(chargeTypeName.equalsIgnoreCase("charge")){
						serviceCharge = tcl.getCalculatedCharge();
					}
					if(chargeTypeName.equalsIgnoreCase("tax")){
						tax = tcl.getCalculatedCharge();
					}				
				}
			}
		}
		
		
		MFSBillerPartnerDAO mfsbpDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		MFSBillerPartnerQuery mbpquery = new MFSBillerPartnerQuery();
		mbpquery.setBillerCode(billPayInquiry.getBillerCode());
		List<MFSBillerPartner> results = mfsbpDAO.get(mbpquery);
		if(results.size() > 0){
			if(CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Denomination);
			}else if(CmFinoFIX.BillerPartnerType_Topup_Free.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Free);
			}else {
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
			}
			
		}
		else{
			response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
		}
		//billerPartnertype = response.getBillerPartnerType();
		response.setSourceMDN(billPayInquiry.getSourceMDN());
		response.setInfo2(mdngen);
		response.setBankCode(inquiryResponse.getBankCode());
		response.setDestMDN(inquiryResponse.getDestMDN());
		response.setParentTransactionID(inquiryResponse.getParentTransactionID());
		response.setUICategory(inquiryResponse.getUICategory());
		response.setSourcePocketID(inquiryResponse.getSourcePocketID());
		response.setDestPocketID(inquiryResponse.getDestPocketID());
		response.setSourceCardPAN(inquiryResponse.getSourceCardPAN());
		response.setDestCardPAN(inquiryResponse.getDestCardPAN());
		response.setDestinationBankAccountNo(inquiryResponse.getDestinationBankAccountNo());
		response.setTransferID(inquiryResponse.getTransferID());
		response.setTransactionID(inquiryResponse.getTransactionID());
		response.setParentTransactionID(inquiryResponse.getParentTransactionID());
		response.setSourceBankAccountType(inquiryResponse.getSourceBankAccountType());
		response.setDestinationBankAccountType(inquiryResponse.getDestinationBankAccountType());
		response.setServiceChargeTransactionLogID(inquiryResponse.getServiceChargeTransactionLogID());
		response.setPin(inquiryResponse.getPin());
		response.setLanguage(inquiryResponse.getLanguage());
		response.setTransferID(inquiryResponse.getTransferID());
        response.setBillerCode(billPayInquiry.getBillerCode());
        response.setInvoiceNo(billPayInquiry.getInvoiceNumber());
        response.setPaymentMode(billPayInquiry.getPaymentMode());
        response.setServiceChargeAmount(serviceCharge);
        response.setTaxAmount(tax);
		mceMessage.setResponse(response);
		}
		else if(bankInqres instanceof BackendResponse){
			mceMessage.setResponse(bankInqres);
	    }
		return mceMessage;

	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayMoneyTransferInquiryCompletionSourceToDestination(MCEMessage mceMessage)
	{
		log.info("BillPaymentServiceImpl :: billPayMoneyTransferInquiryCompletionSourceToDestination mceMessage="+mceMessage);
		CMBSIMBillPaymentInquiryToBank billPayInquiryToBank = (CMBSIMBillPaymentInquiryToBank)mceMessage.getRequest();
		CMBSIMBillPaymentInquiryFromBank billPayInquiryfromBank = (CMBSIMBillPaymentInquiryFromBank)mceMessage.getResponse();
		BackendResponse inquiryResponse;
		try{
			inquiryResponse= (BackendResponse)bankService.onTransferInquiryFromBank(billPayInquiryToBank,billPayInquiryfromBank);
		}catch(Exception e){
			log.error(e.getMessage());
			inquiryResponse = new BackendResponse();
			((BackendResponse) inquiryResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) inquiryResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		CFIXMsg request = (CMTransferInquiryFromBank)billPayInquiryfromBank;
		inquiryResponse.setBillerCode(billPayInquiryToBank.getBillerCode());
		inquiryResponse.setReceiverMDN(billPayInquiryToBank.getInvoiceNo());
		//inquiryResponse.setAdditionalInfo(billPayInquiryfromBank.getInfo3());
		if(inquiryResponse.getResult().equals(CmFinoFIX.ResponseCode_Success))
		{

			ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
			ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
			sctlQuery.setId(inquiryResponse.getServiceChargeTransactionLogID());
			List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
			Long ttID = null;
			if(CollectionUtils.isNotEmpty(list))
			{
			ttID=list.get(0).getTransactionTypeID();
			}
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			String txnName=null;
			if(null!=ttDAO.getById(ttID))
			{
			txnName = ttDAO.getById(ttID).getTransactionName();
			}
			if((StringUtils.isNotBlank(txnName)) && (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(txnName) || ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(txnName)))
			{
				inquiryResponse.setInternalErrorCode(NotificationCodes.AirtimePurchaseInquiry.getInternalErrorCode());
			}else{
				inquiryResponse.setInternalErrorCode(NotificationCodes.BillpaymentInquirySuccessful.getInternalErrorCode());
			}			
		}
		
		// Handle Notifications for PLN Billers separately, setting only for failure case
		
		//if(billPayInquiryToBank.getBillerCode() != null && plnBillers.contains(billPayInquiryToBank.getBillerCode())){
		if(billPayInquiryToBank.getBillerCode() != null && isPlnBiller(billPayInquiryToBank.getBillerCode())){
			if(inquiryResponse.getResult().equals(CmFinoFIX.ResponseCode_Failure)){
				inquiryResponse.setInternalErrorCode(getPLNErrorCode(inquiryResponse.getResult(), billPayInquiryfromBank.getResponseCode()));
			}
			inquiryResponse.setExternalResponseCode(null);//Setting ExternalResponseCode to null to compulsorily use InternalErrorCode
		}
		
		mceMessage.setRequest(request);
		mceMessage.setResponse(inquiryResponse);

		return mceMessage;

	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage qrPaymentMoneyTransferInquirySourceToDestination(MCEMessage mceMessage)
	{
		log.info("BillPaymentServiceImpl :: qrPaymentMoneyTransferInquirySourceToDestination mceMessage="+mceMessage);
		
        CMBillPayInquiry billPayInquiry = (CMBillPayInquiry)mceMessage.getRequest();
        billPaymentsService.createBillPayments(billPayInquiry);
		CFIXMsg bankInqres;
		try{
			bankInqres = bankService.onTransferInquiryToBank(billPayInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			bankInqres = new BackendResponse();
			((BackendResponse) bankInqres).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) bankInqres).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if(bankInqres instanceof CMTransferInquiryToBank){
		mceMessage.setRequest(billPayInquiry);
		CMTransferInquiryToBank inquiryResponse = (CMTransferInquiryToBank)bankInqres;
		mceMessage.setDestinationQueue(sourceToDestInquiryQueue);
		CMBSIMBillPaymentInquiryToBank response = new CMBSIMBillPaymentInquiryToBank();
		response.setAmount(inquiryResponse.getAmount());
		String mdn = StringUtilities.leftPadWithCharacter(billPayInquiry.getSourceMDN(), 13, "0");
		String mdngen = MfinoUtil.CheckDigitCalculation(mdn);
		MFSBillerPartnerDAO mfsbpDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		MFSBillerPartnerQuery mbpquery = new MFSBillerPartnerQuery();
		mbpquery.setBillerCode(billPayInquiry.getBillerCode());
		List<MFSBillerPartner> results = mfsbpDAO.get(mbpquery);
		if(results.size() > 0){
			if(CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Denomination);
			}else if(CmFinoFIX.BillerPartnerType_Topup_Free.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Free);
			}else {
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
			}
			
		}
		else{
			response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
		}
		//billerPartnertype = response.getBillerPartnerType();
		response.setSourceMDN(billPayInquiry.getSourceMDN());
		response.setInfo2(mdngen);
		response.setBankCode(inquiryResponse.getBankCode());
		response.setDestMDN(inquiryResponse.getDestMDN());
		response.setParentTransactionID(inquiryResponse.getParentTransactionID());
		response.setUICategory(inquiryResponse.getUICategory());
		response.setSourcePocketID(inquiryResponse.getSourcePocketID());
		response.setDestPocketID(inquiryResponse.getDestPocketID());
		response.setSourceCardPAN(inquiryResponse.getSourceCardPAN());
		response.setDestCardPAN(inquiryResponse.getDestCardPAN());
		response.setDestinationBankAccountNo(inquiryResponse.getDestinationBankAccountNo());
		response.setTransferID(inquiryResponse.getTransferID());
		response.setTransactionID(inquiryResponse.getTransactionID());
		response.setParentTransactionID(inquiryResponse.getParentTransactionID());
		response.setSourceBankAccountType(inquiryResponse.getSourceBankAccountType());
		response.setDestinationBankAccountType(inquiryResponse.getDestinationBankAccountType());
		response.setServiceChargeTransactionLogID(inquiryResponse.getServiceChargeTransactionLogID());
		response.setPin(inquiryResponse.getPin());
		response.setLanguage(inquiryResponse.getLanguage());
		response.setTransferID(inquiryResponse.getTransferID());
        response.setBillerCode(billPayInquiry.getBillerCode());
        response.setInvoiceNo(billPayInquiry.getInvoiceNumber());
        response.setPaymentMode(billPayInquiry.getPaymentMode());
		mceMessage.setResponse(response);
		}
		else if(bankInqres instanceof BackendResponse){
			mceMessage.setResponse(bankInqres);
	    }
		return mceMessage;

	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage qrPaymentMoneyTransferInquiryCompletionSourceToDestination(MCEMessage mceMessage)
	{
		log.info("BillPaymentServiceImpl :: qrPaymentMoneyTransferInquiryCompletionSourceToDestination mceMessage="+mceMessage);
		CMBSIMBillPaymentInquiryToBank billPayInquiryToBank = (CMBSIMBillPaymentInquiryToBank)mceMessage.getRequest();
		CMBSIMBillPaymentInquiryFromBank billPayInquiryfromBank = (CMBSIMBillPaymentInquiryFromBank)mceMessage.getResponse();
		BackendResponse inquiryResponse;
		try{
			inquiryResponse= (BackendResponse)bankService.onTransferInquiryFromBank(billPayInquiryToBank,billPayInquiryfromBank);
		}catch(Exception e){
			log.error(e.getMessage());
			inquiryResponse = new BackendResponse();
			((BackendResponse) inquiryResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) inquiryResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		CFIXMsg request = (CMTransferInquiryFromBank)billPayInquiryfromBank;
		inquiryResponse.setBillerCode(billPayInquiryToBank.getBillerCode());
		inquiryResponse.setReceiverMDN(billPayInquiryToBank.getInvoiceNo());
		//inquiryResponse.setAdditionalInfo(billPayInquiryfromBank.getInfo3());
		if(inquiryResponse.getResult().equals(CmFinoFIX.ResponseCode_Success))
		{

			ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
			ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
			sctlQuery.setId(inquiryResponse.getServiceChargeTransactionLogID());
			List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
			Long ttID = null;
			if(CollectionUtils.isNotEmpty(list))
			{
			ttID=list.get(0).getTransactionTypeID();
			}
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			String txnName=null;
			if(null!=ttDAO.getById(ttID))
			{
			txnName = ttDAO.getById(ttID).getTransactionName();
			}
			if((StringUtils.isNotBlank(txnName)) && (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(txnName) || ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(txnName)))
			{
				inquiryResponse.setInternalErrorCode(NotificationCodes.AirtimePurchaseInquiry.getInternalErrorCode());
			}else{
				inquiryResponse.setInternalErrorCode(NotificationCodes.BillpaymentInquirySuccessful.getInternalErrorCode());
			}			
		}
		if(((BackendResponse)inquiryResponse).getResult() == CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(inquiryResponse.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_INQUIRY_COMPLETED);
		}else {
			billPaymentsService.updateBillPayStatus(inquiryResponse.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_INQUIRY_FAILED);
		}
		mceMessage.setRequest(request);
		mceMessage.setResponse(inquiryResponse);

		return mceMessage;

	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayMoneyTransferSourceToDestination(MCEMessage mceMessage)
	{
		log.info("BillPaymentServiceImpl :: billPayMoneyTransferSourceToDestination mceMessage="+mceMessage);
		CMBillPay billPay = (CMBillPay)mceMessage.getRequest();
		try {
		inquiryResponse = (CMMoneyTransferToBank)bankService.onTransferConfirmationToBank(billPay);
		} catch(Exception e){
		log.error(e.getMessage());
		//handle exception here <---
		}
		CMBSIMBillPaymentToBank response = new CMBSIMBillPaymentToBank();
		response.setAmount(inquiryResponse.getAmount());
		response.setBankCode(inquiryResponse.getBankCode());
		response.setDestCardPAN(inquiryResponse.getDestCardPAN());
		response.setDestMDN(inquiryResponse.getDestMDN());
		String mdn = StringUtilities.leftPadWithCharacter(billPay.getSourceMDN(), 13, "0");
		String mdngen = MfinoUtil.CheckDigitCalculation(mdn);
		response.setProcessingCodeDE3(inquiryResponse.getProcessingCode());
		response.setSourceMDN(billPay.getSourceMDN());
		response.setInfo2(mdngen);
		MFSBillerPartnerDAO mfsbpDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		MFSBillerPartnerQuery mbpquery = new MFSBillerPartnerQuery();
		mbpquery.setBillerCode(billPay.getBillerCode());
		List<MFSBillerPartner> results = mfsbpDAO.get(mbpquery);
		if(results.size() > 0){
			if(CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Denomination);
			}else if(CmFinoFIX.BillerPartnerType_Topup_Free.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Free);
			}else {
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
			}
			
		}
		else{
			response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
		}
		
		response.setParentTransactionID(inquiryResponse.getParentTransactionID());
		response.setUICategory(inquiryResponse.getUICategory());
		response.setSourcePocketID(inquiryResponse.getSourcePocketID());
		response.setDestPocketID(inquiryResponse.getDestPocketID());
		response.setPin(inquiryResponse.getPin());
		response.setSourceCardPAN(inquiryResponse.getSourceCardPAN());
		response.setTransferID(inquiryResponse.getTransferID());
		response.setTransactionID(inquiryResponse.getTransactionID());
		//response.setTransferTime(inquiryResponse.getTransferTime());
		Timestamp ts = DateTimeUtil.getGMTTime();
		response.setTransferTime(ts);
		response.setDestCardPAN(inquiryResponse.getDestCardPAN());
		response.setDestinationBankAccountType(inquiryResponse.getDestinationBankAccountType());
		response.setOriginalReferenceID(inquiryResponse.getOriginalReferenceID());
		response.setSourceBankAccountType(inquiryResponse.getSourceBankAccountType());
		response.setDestinationBankAccountType(inquiryResponse.getDestinationBankAccountType());
		response.setServiceChargeTransactionLogID(inquiryResponse.getServiceChargeTransactionLogID());
		response.setLanguage(inquiryResponse.getLanguage());
		response.setBillerCode(billPay.getBillerCode());
		response.setInvoiceNo(billPay.getInvoiceNumber());
		response.setPaymentMode(billPay.getPaymentMode());
		mceMessage.setRequest(billPay);
		mceMessage.setDestinationQueue(sourceToDestQueue);
		response.setInfo1(billPay.getInvoiceNumber());
		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery query = new IntegrationSummaryQuery();
		query.setSctlID(inquiryResponse.getServiceChargeTransactionLogID());
		List<IntegrationSummary> iSummaryList = integrationSummaryDao.get(query);
		IntegrationSummary iSummary = null;
		if((null != iSummaryList)&&(iSummaryList.size() > 0)){
			iSummary = iSummaryList.get(0);
			log.info("Processing code"+iSummary.getReconcilationID1());
			response.setInfo3(iSummary.getReconcilationID2());
		}
		mceMessage.setResponse(response);

		return mceMessage;

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayMoneyTransferCompletionSourceToDestination(MCEMessage mceMessage)
	{

		log.info("BillPaymentServiceImpl :: billPayMoneyTransferCompletionSourceToDestination mceMessage="+mceMessage);
		CMBSIMBillPaymentToBank billPayToBank = (CMBSIMBillPaymentToBank)mceMessage.getRequest();
		CMBSIMBillPaymentFromBank billPayfromBank = (CMBSIMBillPaymentFromBank)mceMessage.getResponse();
		BackendResponse Response;
		try {
		Response = (BackendResponse)bankService.onTransferConfirmationFromBank((CMMoneyTransferToBank)billPayToBank,billPayfromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			Response = createResponseObject();
			((BackendResponse) Response).copy(inquiryResponse);
			((BackendResponse) Response).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) Response).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		Response.setAdditionalInfo(billPayfromBank.getInfo1());
		CFIXMsg request = (CMMoneyTransferFromBank)billPayfromBank;
		
		Response.setBillerCode(billPayToBank.getBillerCode());
		Response.setInvoiceNumber(billPayToBank.getInvoiceNo());
		if(Response.getResult().equals(CmFinoFIX.ResponseCode_Success)){
			ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
			ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
			sctlQuery.setId(Response.getServiceChargeTransactionLogID());
			List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
			Long ttID = null;
			if(CollectionUtils.isNotEmpty(list))
			{
			ttID=list.get(0).getTransactionTypeID();
			}
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			String txnName=null;
			if(null!=ttDAO.getById(ttID))
			{
			txnName = ttDAO.getById(ttID).getTransactionName();
			}
			if((StringUtils.isNotBlank(txnName)) && (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(txnName) || ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(txnName)))
			{
				Response.setInternalErrorCode(NotificationCodes.BillpaymentConfirmationSuccess.getInternalErrorCode());
			}else{
				Response.setInternalErrorCode(NotificationCodes.BillPayCompletedToSender.getInternalErrorCode());
			}
		
			}
		
		// Handle Notifications for PLN Billers separately
		//if(billPayToBank.getBillerCode() != null && plnBillers.contains(billPayToBank.getBillerCode())){
		if(billPayToBank.getBillerCode() != null && isPlnBiller(billPayToBank.getBillerCode())){
					//Setting the parameters as per PLN format
			if(Response.getResult().equals(CmFinoFIX.ResponseCode_Success)){
				String webNotification = "Transaction Date time " + billPayToBank.getTransferTime().toString();
				String[] DE62 = billPayfromBank.getInfo1().split("\\|");
				
				for(String string : DE62){
					for (String substring : keywords) {
						if(string.matches("(?i).*"+substring+".*")){
							webNotification = webNotification + "|" + string;
							break;
						}
					}
				}
				
				String smsNotification = billPayToBank.getTransferTime().toString();
				
				if(plnPrepaidBillers.contains(billPayToBank.getBillerCode()))
				{
					for (String key : plnPrepaidSMSKeyWords) {
						for(String string : DE62){
							if(string.matches("(?i).*"+key+".*")){
								String value = string.substring(string.indexOf(":") + 1).trim();
								smsNotification = smsNotification + "|" + plnPrepaidSMSKeyValues.get(key) + value;
								break;
							}
						}	
					}
				} 
				else if(plnPostpaidBillers.contains(billPayToBank.getBillerCode()))
				{
					for (String key : plnPostpaidSMSKeyWords) {
						for(String string : DE62){
							if(string.matches("(?i).*"+key+".*")){
								String value = string.substring(string.indexOf(":") + 1).trim();
								smsNotification = smsNotification + "|" + plnpostPaidSMSKeyValues.get(key) + value;
								break;
							}
						}	
					}
				}
				else if(plnNonTaglisBillers.contains(billPayToBank.getBillerCode()))
				{
					for (String key : plnNonTaglisSMSKeyWords) {
						for(String string : DE62){
							if(string.matches("(?i).*"+key+".*")){
								String value = string.substring(string.indexOf(":") + 1).trim();
								smsNotification = smsNotification + "|" + plnNonTaglisSMSKeyValues.get(key) + value;
								break;
							}
						}	
					}
				}
	
				Response.setAdditionalInfo(webNotification);
				Response.setAdditionalInfoAsSMS(smsNotification + "|" +  "SUKSES");
				Response.setInvoiceNumber(billPayToBank.getInvoiceNo());
			}
			else {	
				Response.setAdditionalInfo(billPayfromBank.getInfo1());
				Response.setAdditionalInfoAsSMS(billPayfromBank.getInfo1());
			}
			Response.setInternalErrorCode(getPLNErrorCode(Response.getResult(), billPayfromBank.getResponseCode()));
			Response.setExternalResponseCode(null);//Setting ExternalResponseCode to null to compulsorily use InternalErrorCode
		}
				
		mceMessage.setRequest(request);
		mceMessage.setResponse(Response);
        return mceMessage;

	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage qrPaymentMoneyTransferSourceToDestination(MCEMessage mceMessage)
	{
		log.info("BillPaymentServiceImpl :: qrPaymentMoneyTransferSourceToDestination mceMessage="+mceMessage);
		CMQRPayment qrPayment = (CMQRPayment)mceMessage.getRequest();
		try {
		inquiryResponse = (CMMoneyTransferToBank)bankService.onTransferConfirmationToBank(qrPayment);
		} catch(Exception e){
		log.error(e.getMessage());
		//handle exception here <---
		}
		CMQRPaymentToBank response = new CMQRPaymentToBank();
		response.setAmount(inquiryResponse.getAmount());
		response.setBankCode(inquiryResponse.getBankCode());
		response.setDestCardPAN(inquiryResponse.getDestCardPAN());
		response.setDestMDN(inquiryResponse.getDestMDN());
		String mdn = StringUtilities.leftPadWithCharacter(qrPayment.getSourceMDN(), 13, "0");
		String mdngen = MfinoUtil.CheckDigitCalculation(mdn);
		response.setProcessingCodeDE3(inquiryResponse.getProcessingCode());
		response.setSourceMDN(qrPayment.getSourceMDN());
		response.setInfo2(mdngen);
		MFSBillerPartnerDAO mfsbpDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		MFSBillerPartnerQuery mbpquery = new MFSBillerPartnerQuery();
		mbpquery.setBillerCode(qrPayment.getBillerCode());
		List<MFSBillerPartner> results = mfsbpDAO.get(mbpquery);
		if(results.size() > 0){
			if(CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Denomination);
			}else if(CmFinoFIX.BillerPartnerType_Topup_Free.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Free);
			}else {
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
			}
			
		}
		else{
			response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
		}
		
		Long sctlID = inquiryResponse.getServiceChargeTransactionLogID();
		TransactionChargeLogDAO tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();
		
		BigDecimal serviceCharge = new BigDecimal(0);
		BigDecimal tax = new BigDecimal(0);
		
		List <TransactionChargeLog> tclList = tclDAO.getBySCTLID(sctlID);
		if(CollectionUtils.isNotEmpty(tclList)){
			for(Iterator<TransactionChargeLog> it = tclList.iterator();it.hasNext();){
				TransactionChargeLog tcl = it.next();
				if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("charge")){
					serviceCharge = tcl.getCalculatedCharge();
				}
				if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("tax")){
					tax = tcl.getCalculatedCharge();
				}				
			}
		}
		
		response.setServiceChargeAmount(serviceCharge);
		response.setTaxAmount(tax);
		response.setParentTransactionID(inquiryResponse.getParentTransactionID());
		response.setUICategory(inquiryResponse.getUICategory());
		response.setSourcePocketID(inquiryResponse.getSourcePocketID());
		response.setDestPocketID(inquiryResponse.getDestPocketID());
		response.setPin(inquiryResponse.getPin());
		response.setSourceCardPAN(inquiryResponse.getSourceCardPAN());
		response.setTransferID(inquiryResponse.getTransferID());
		response.setTransactionID(inquiryResponse.getTransactionID());
		//response.setTransferTime(inquiryResponse.getTransferTime());
		Timestamp ts = DateTimeUtil.getGMTTime();
		response.setTransferTime(ts);
		response.setDestCardPAN(inquiryResponse.getDestCardPAN());
		response.setDestinationBankAccountType(inquiryResponse.getDestinationBankAccountType());
		response.setOriginalReferenceID(inquiryResponse.getOriginalReferenceID());
		response.setSourceBankAccountType(inquiryResponse.getSourceBankAccountType());
		response.setDestinationBankAccountType(inquiryResponse.getDestinationBankAccountType());
		response.setServiceChargeTransactionLogID(inquiryResponse.getServiceChargeTransactionLogID());
		response.setLanguage(inquiryResponse.getLanguage());
		response.setBillerCode(qrPayment.getBillerCode());
		response.setInvoiceNo(qrPayment.getInvoiceNumber());
		response.setPaymentMode(qrPayment.getPaymentMode());
		response.setUserAPIKey(qrPayment.getUserAPIKey());
		response.setMerchantData(qrPayment.getMerchantData());
		mceMessage.setRequest(qrPayment);
		mceMessage.setDestinationQueue(sourceToDestQueue);
		response.setInfo1(qrPayment.getInvoiceNumber());
		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery query = new IntegrationSummaryQuery();
		query.setSctlID(inquiryResponse.getServiceChargeTransactionLogID());
		List<IntegrationSummary> iSummaryList = integrationSummaryDao.get(query);
		IntegrationSummary iSummary = null;
		if((null != iSummaryList)&&(iSummaryList.size() > 0)){
			iSummary = iSummaryList.get(0);
			log.info("Processing code"+iSummary.getReconcilationID1());
			response.setInfo3(iSummary.getReconcilationID2());
		}
		billPaymentsService.updateBillPayStatus(inquiryResponse.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_PAYMENT_REQUESTED);
		mceMessage.setResponse(response);

		return mceMessage;

	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage qrPaymentMoneyTransferCompletionSourceToDestination(MCEMessage mceMessage)
	{

		log.info("BillPaymentServiceImpl :: qrPaymentMoneyTransferCompletionSourceToDestination mceMessage="+mceMessage);
		CMQRPaymentToBank qrPayToBank = (CMQRPaymentToBank)mceMessage.getRequest();
		CMQRPaymentFromBank qrPayFromBank = (CMQRPaymentFromBank)mceMessage.getResponse();
		BackendResponse Response;

		//make destination queue null so that it will be picked up from class based dynamic router ***
		mceMessage.setDestinationQueue(null);
		try {
		Response = (BackendResponse)bankService.onTransferConfirmationFromBank((CMMoneyTransferToBank)qrPayToBank,qrPayFromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			Response = createResponseObject();
			((BackendResponse) Response).copy(inquiryResponse);
			((BackendResponse) Response).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) Response).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		Response.setAdditionalInfo(qrPayFromBank.getInfo1());
		CFIXMsg request = (CMMoneyTransferFromBank)qrPayFromBank;
		
		Response.setBillerCode(qrPayToBank.getBillerCode());
		Response.setInvoiceNumber(qrPayToBank.getInvoiceNo());
		if(Response.getResult().equals(CmFinoFIX.ResponseCode_Success)){
			ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
			ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
			sctlQuery.setId(Response.getServiceChargeTransactionLogID());
			List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
			Long ttID = null;
			if(CollectionUtils.isNotEmpty(list))
			{
			ttID=list.get(0).getTransactionTypeID();
			}
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			String txnName=null;
			if(null!=ttDAO.getById(ttID))
			{
			txnName = ttDAO.getById(ttID).getTransactionName();
			}
			if((StringUtils.isNotBlank(txnName)) && (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(txnName) || ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(txnName)))
			{
				Response.setInternalErrorCode(NotificationCodes.BillpaymentConfirmationSuccess.getInternalErrorCode());
			}else{
				Response.setInternalErrorCode(NotificationCodes.BillPayCompletedToSender.getInternalErrorCode());
			}
			}
		if(((BackendResponse)Response).getResult() == CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(Response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_PAYMENT_COMPLETED);
		}else {
			billPaymentsService.updateBillPayStatus(Response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_PAYMENT_FAILED);
		}
		mceMessage.setRequest(request);
		mceMessage.setResponse(Response);
        return mceMessage;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayReversalToBank(MCEMessage mceMessage){
		CMBSIMBillPaymentToBank billPayrevToBank = (CMBSIMBillPaymentToBank)mceMessage.getRequest();
		NoISOResponseMsg NoISORes = (NoISOResponseMsg)mceMessage.getResponse();
		CMMoneyTransferReversalToBank billPayrev = (CMMoneyTransferReversalToBank)bankService.onTransferReversalToBank(billPayrevToBank,NoISORes);
		mceMessage.setRequest(NoISORes);
		CMBSIMBillPaymentReversalToBank billPayrevtobank = new CMBSIMBillPaymentReversalToBank();
		billPayrevtobank.setBankSystemTraceAuditNumber(billPayrev.getBankSystemTraceAuditNumber());
		billPayrevtobank.setTransactionID(NoISORes.getTransactionID());
		billPayrevtobank.setBillerCode(billPayrevToBank.getBillerCode());
		billPayrevtobank.setBankRetrievalReferenceNumber(billPayrev.getBankRetrievalReferenceNumber());
		billPayrevtobank.setBankCode(inquiryResponse.getBankCode());
		billPayrevtobank.setAmount(billPayrev.getAmount());
		billPayrevtobank.setSourceMDN(billPayrev.getSourceMDN());
		billPayrevtobank.setTransferID(billPayrev.getTransferID());
		billPayrevtobank.setTransferTime(billPayrev.getTransferTime());
		billPayrevtobank.setLanguage(inquiryResponse.getLanguage());
		if(billPayrevToBank.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Denomination) || billPayrevToBank.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Free)){
			billPayrevtobank.setProcessingCode("56");
		}else{
			billPayrevtobank.setProcessingCode("50");
		}
		billPayrevtobank.setSourceCardPAN(billPayrevToBank.getSourceCardPAN());
		constructAndSetDE3(billPayrevtobank);
		log.info("BillPaymentServiceImpl :: set prefix processing code :" + billPayrevtobank.getProcessingCode());
		mceMessage.setResponse(billPayrevtobank);
		mceMessage.setDestinationQueue(reversalQueue);
		return mceMessage;

	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private void constructAndSetDE3(CMBSIMBillPaymentReversalToBank billPayrevtobank){
		String defaultDE3=CmFinoFIX.ISO8583_ProcessingCode_XLink_Payment0;
		Pocket sourcePocket = subscriberService.getDefaultPocket(billPayrevtobank.getSourceMDN(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		PocketTemplate pocketTemplate = null;
		Integer pocketTempType = null;
		String processingCodePrefix = billPayrevtobank.getProcessingCode();
		log.info("BillPaymentReversalToBankProcessor :: process appending processing Code Prefix as :"+processingCodePrefix);
		if(sourcePocket!=null){
			pocketTemplate = sourcePocket.getPocketTemplate();
			pocketTempType = pocketTemplate.getBankAccountCardType();
			if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
				defaultDE3=processingCodePrefix+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+"00"; ;
			}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
				defaultDE3=processingCodePrefix+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+"00";
			}
		}
		billPayrevtobank.setProcessingCodeDE3(defaultDE3);//default de-3 will be overwritten depending on dest a/c type
		log.info("BillPaymentReversalToBankProcessor :: process default "+ defaultDE3);
		String processingCode=null;
		IntegrationSummaryDao isDAO  = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery isQuery = new IntegrationSummaryQuery();
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		ServiceChargeTransactionLog sctl;
		Long transferID = billPayrevtobank.getTransferID();
		Long sctlID;
		String reconciliationID1 = null;
		if(transferID!=null){
			sctlQuery.setTransferID(transferID);
			log.info("BillPaymentReversalToBankProcessor :: process Transfer ID :"+transferID);
			List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
			if(CollectionUtils.isNotEmpty(list)){
				sctl = list.get(0);
				sctlID = sctl.getID();
				log.info("BillPaymentReversalToBankProcessor :: process Sctl ID :"+sctlID);
				isQuery.setSctlID(sctlID);
				List<IntegrationSummary> isList = isDAO.get(isQuery);
				if(CollectionUtils.isNotEmpty(isList)){
					IntegrationSummary iSummary = isList.get(0);
					reconciliationID1 = iSummary.getReconcilationID1();
					log.info("BillPaymentReversalToBankProcessor :: process ReconciliationID1 :"+reconciliationID1);
					log.info("BillPaymentReversalToBankProcessor :: Dumping message fields :" + billPayrevtobank.DumpFields());
					log.info("BillPaymentReversalToBankProcessor :: SourceMDN " + billPayrevtobank.getSourceMDN());
					log.info("BillPaymentReversalToBankProcessor :: source Pocket" + sourcePocket.DumpFields());

					if(sourcePocket!=null && StringUtils.isNotBlank(reconciliationID1))
					{
						log.info("pocketTemplate.getBankAccountCardType() "  + pocketTemplate.getBankAccountCardType());
						log.info("dumping pocketemplate fields " + pocketTemplate.DumpFields());

						if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
							processingCode=processingCodePrefix+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+reconciliationID1 ;
						}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
							processingCode=processingCodePrefix+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+reconciliationID1;
						}
						log.info("BillPaymentReversalToBankProcessor :: process Setting ProcessingCode :"+processingCode+" in DE-3");
						billPayrevtobank.setProcessingCodeDE3(processingCode);
					}

				}	   		
			}
		}
	}
	
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayReversalFromBank(MCEMessage mceMessage){
		CMBSIMBillPaymentReversalToBank billPayrevToBank = (CMBSIMBillPaymentReversalToBank)mceMessage.getRequest();
		CMBSIMBillPaymentReversalFromBank billPayrevFromBank = (CMBSIMBillPaymentReversalFromBank)mceMessage.getResponse();
		BackendResponse Response = (BackendResponse)bankService.onTransferReversalFromBank(billPayrevToBank,billPayrevFromBank);
		mceMessage.setRequest(billPayrevFromBank);
		mceMessage.setResponse(Response);
		return mceMessage;
		
	}
	

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage qrPaymentReversalToBank(MCEMessage mceMessage){
		CMQRPaymentToBank billPayrevToBank = (CMQRPaymentToBank)mceMessage.getRequest();
		NoISOResponseMsg NoISORes = (NoISOResponseMsg)mceMessage.getResponse();
		CMMoneyTransferReversalToBank billPayrev = (CMMoneyTransferReversalToBank)bankService.onTransferReversalToBank(billPayrevToBank,NoISORes);
		mceMessage.setRequest(NoISORes);
		CMQRPaymentReversalToBank billPayrevtobank = new CMQRPaymentReversalToBank();
		billPayrevtobank.setBankSystemTraceAuditNumber(billPayrev.getBankSystemTraceAuditNumber());
		billPayrevtobank.setTransactionID(NoISORes.getTransactionID());
		billPayrevtobank.setBillerCode(billPayrevToBank.getBillerCode());
		billPayrevtobank.setBankRetrievalReferenceNumber(billPayrev.getBankRetrievalReferenceNumber());
		billPayrevtobank.setBankCode(inquiryResponse.getBankCode());
		billPayrevtobank.setAmount(billPayrev.getAmount());
		billPayrevtobank.setSourceMDN(billPayrev.getSourceMDN());
		billPayrevtobank.setTransferID(billPayrev.getTransferID());
		billPayrevtobank.setTransferTime(billPayrev.getTransferTime());
		billPayrevtobank.setLanguage(inquiryResponse.getLanguage());
		if(billPayrevToBank.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Denomination) || billPayrevToBank.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Free)){
			billPayrevtobank.setProcessingCode("56");
		}else{
			billPayrevtobank.setProcessingCode("50");
		}
		billPayrevtobank.setSourceCardPAN(billPayrevToBank.getSourceCardPAN());
		constructAndSetDe63ForQr(billPayrevtobank);
		log.info("BillPaymentServiceImpl :: set prefix processing code :" + billPayrevtobank.getProcessingCode());
		billPaymentsService.updateBillPayStatus(billPayrev.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_BILLER_REVERSAL_REQUESTED);
		mceMessage.setResponse(billPayrevtobank);
		mceMessage.setDestinationQueue(reversalQueue);
		return mceMessage;

	}
	
	public void constructAndSetDe63ForQr(CMQRPaymentReversalToBank billPayrevtobank){
		String defaultDE3=CmFinoFIX.ISO8583_ProcessingCode_XLink_Payment0;
		Pocket sourcePocket = subscriberService.getDefaultPocket(billPayrevtobank.getSourceMDN(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		PocketTemplate pocketTemplate = null;
		Integer pocketTempType = null;
		String processingCodePrefix = "50";
		log.info("QRPaymentReversalToBankProcessor :: process appending processing Code Prefix as :"+processingCodePrefix);
		if(sourcePocket!=null){
			pocketTemplate = sourcePocket.getPocketTemplate();
			pocketTempType = pocketTemplate.getBankAccountCardType();
			if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
				defaultDE3=processingCodePrefix+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+"00"; ;
			}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
				defaultDE3=processingCodePrefix+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+"00";
			}
		}
		billPayrevtobank.setProcessingCodeDE3(defaultDE3);//default de-3 will be overwritten depending on dest a/c type
		log.info("QRPaymentReversalToBankProcessor :: process default "+ defaultDE3);
		String processingCode=null;
		IntegrationSummaryDao isDAO  = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery isQuery = new IntegrationSummaryQuery();
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		ServiceChargeTransactionLog sctl;
		Long transferID = billPayrevtobank.getTransferID();
		Long sctlID;
		String reconciliationID1 = null;
		if(transferID!=null){
			sctlQuery.setTransferID(transferID);
			log.info("QRPaymentReversalToBankProcessor :: process Transfer ID :"+transferID);
			List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
			if(CollectionUtils.isNotEmpty(list)){
				sctl = list.get(0);
				sctlID = sctl.getID();
				log.info("QRPaymentReversalToBankProcessor :: process Sctl ID :"+sctlID);
				isQuery.setSctlID(sctlID);
				List<IntegrationSummary> isList = isDAO.get(isQuery);
				if(CollectionUtils.isNotEmpty(isList)){
					IntegrationSummary iSummary = isList.get(0);
					reconciliationID1 = iSummary.getReconcilationID1();
					log.info("QRPaymentReversalToBankProcessor :: process ReconciliationID1 :"+reconciliationID1);
					log.info("QRPaymentReversalToBankProcessor :: Dumping message fields :" + billPayrevtobank.DumpFields());
					log.info("QRPaymentReversalToBankProcessor :: SourceMDN " + billPayrevtobank.getSourceMDN());
					log.info("QRPaymentReversalToBankProcessor :: source Pocket" + sourcePocket.DumpFields());

					if(sourcePocket!=null && StringUtils.isNotBlank(reconciliationID1))
					{
						log.info("pocketTemplate.getBankAccountCardType() "  + pocketTemplate.getBankAccountCardType());
						log.info("dumping pocketemplate fields " + pocketTemplate.DumpFields());

						if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
							processingCode=processingCodePrefix+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+reconciliationID1 ;
						}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
							processingCode=processingCodePrefix+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+reconciliationID1;
						}
						log.info("QRPaymentReversalToBankProcessor :: process Setting ProcessingCode :"+processingCode+" in DE-3");
						billPayrevtobank.setProcessingCodeDE3(processingCode);
					}

				}	   		
			}
		}
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage qrPaymentReversalFromBank(MCEMessage mceMessage){
		CMQRPaymentReversalToBank billPayrevToBank = (CMQRPaymentReversalToBank)mceMessage.getRequest();
		CMQRPaymentReversalFromBank billPayrevFromBank = (CMQRPaymentReversalFromBank)mceMessage.getResponse();
		BackendResponse Response = (BackendResponse)bankService.onTransferReversalFromBank(billPayrevToBank,billPayrevFromBank);
		if(((BackendResponse)Response).getResult() == CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(Response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_BILLER_REVERSAL_COMPLETED);
		}else {
			billPaymentsService.updateBillPayStatus(Response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_BILLER_REVERSAL_FAILED);
		}
		mceMessage.setRequest(billPayrevFromBank);
		mceMessage.setResponse(Response);
		return mceMessage;

	}

	public BankService getBankService() {
		return bankService;
	}


	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayAmountInquiry(MCEMessage mceMessage) {
		CMBSIMBillPayInquiry billPayInquiry = (CMBSIMBillPayInquiry)mceMessage.getRequest();
		mceMessage.setDestinationQueue(AmountFromBankQueue);
		String mdn = StringUtilities.leftPadWithCharacter(billPayInquiry.getSourceMDN(), 13, "0");
		CMBSIMGetAmountToBiller response = new CMBSIMGetAmountToBiller();
		String mdngen = MfinoUtil.CheckDigitCalculation(mdn);
		response.setSourceMDN(billPayInquiry.getSourceMDN());
		MFSBillerPartnerDAO mfsbpDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		MFSBillerPartnerQuery mbpquery = new MFSBillerPartnerQuery();
		mbpquery.setBillerCode(billPayInquiry.getBillerCode());
		List<MFSBillerPartner> results = mfsbpDAO.get(mbpquery);
		if(results.size() > 0){
			if(CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Denomination);
			}else if(CmFinoFIX.BillerPartnerType_Topup_Free.equals(results.get(0).getBillerPartnerType()) ){
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Topup_Free);
			}else {
				response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
			}
			
		}
		else{
			response.setBillerPartnerType(CmFinoFIX.BillerPartnerType_Payment_Full);
		}
		response.setInfo2(mdngen);
		response.setDestMDN(billPayInquiry.getDestMDN());
		response.setDestPocketID(billPayInquiry.getDestPocketID());
		response.setSourceCardPAN(billPayInquiry.getSourceBankAccountNo());
		response.setTransactionID(billPayInquiry.getTransactionID());
		response.setTransactionIdentifier(billPayInquiry.getTransactionIdentifier());
		response.setSourceApplication(billPayInquiry.getSourceApplication());
		response.setPin(billPayInquiry.getPin());
		response.setSourcePocketID(billPayInquiry.getSourcePocketID());
		response.setDestPocketID(billPayInquiry.getDestPocketID());
		response.setBillerCode(billPayInquiry.getBillerCode());
        response.setInvoiceNo(billPayInquiry.getInvoiceNumber());
        response.setServiceChargeTransactionLogID(billPayInquiry.getServiceChargeTransactionLogID());
        response.setPaymentMode(billPayInquiry.getPaymentMode());
        if(billPayInquiry.getAmount()!=null)
        	response.setAmount(billPayInquiry.getAmount());
        if(StringUtils.isNotBlank(billPayInquiry.getAmount().toString()) && CmFinoFIX.PaymentMode_PackageType.equalsIgnoreCase(billPayInquiry.getPaymentMode()))
        {
        response.setAmount(billPayInquiry.getAmount());
        }
        response.setCharges(billPayInquiry.getCharges());
        mceMessage.setRequest(billPayInquiry);
        mceMessage.setResponse(response);
		return mceMessage;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage billPayAmountInquiryFromBank(MCEMessage mceMessage) {
		log.info("BillPaymentServiceImpl :: billPayAmountInquiryFromBank mceMessage="+mceMessage);
		CMBSIMGetAmountToBiller billPayInquiryToBank = (CMBSIMGetAmountToBiller)mceMessage.getRequest();
		CMBSIMGetAmountFromBiller billPayInquiryfromBank = (CMBSIMGetAmountFromBiller)mceMessage.getResponse();
		/*PartnerValidator partnerMdnValidator = new PartnerValidator();
		partnerMdnValidator.setBillerCode(billPayInquiryToBank.getBillerCode());
		partnerMdnValidator.setMdn(billPayInquiryToBank.getDestMDN());
		BillerService billerService = new BillerService();
		Partner partner = billerService.getPartner(billPayInquiryToBank.getBillerCode());
		
		Subscriber agentsub = partner.getSubscriber();
		SubscriberMDN agentmdn = agentsub.getSubscriberMDNFromSubscriberID().iterator().next();	*/
		
		/*
		 * for pln billers amount returned by ISO message will be zero and hence we get it from billPayInquiryToBank
		 */
		//if(plnBillers.contains(billPayInquiryToBank.getBillerCode())){
		if(isPlnBiller(billPayInquiryToBank.getBillerCode())){
			billPayInquiryfromBank.setAmount(billPayInquiryToBank.getAmount());
		}
		
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(billPayInquiryToBank.getTransactionID());
		sc.setDestMDN(billPayInquiryToBank.getDestMDN());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_PAYMENT);//change to service
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
		sc.setSourceMDN(billPayInquiryToBank.getSourceMDN());
		sc.setTransactionAmount(billPayInquiryfromBank.getAmount());
		sc.setMfsBillerCode(billPayInquiryToBank.getBillerCode());
		sc.setTransactionLogId(billPayInquiryToBank.getTransactionID());
		sc.setInvoiceNo(billPayInquiryToBank.getInvoiceNo());
		sc.setTransactionIdentifier(billPayInquiryToBank.getTransactionIdentifier());
		sc.setSctlId(billPayInquiryToBank.getServiceChargeTransactionLogID());
		Long sctlID = billPayInquiryToBank.getServiceChargeTransactionLogID();
		//Updating amount and charges after getting pending amount
		Transaction transaction=null;
		BigDecimal charges = BigDecimal.ZERO;
		try{
			transaction = transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
		}

		if(transaction != null && transaction.getAmountTowardsCharges() != null)
			charges = transaction.getAmountTowardsCharges();
		log.info("BillPaymentServiceImpl :: billPayAmountInquiryFromBank updating 'online' payment amount="+sc.getTransactionAmount()+" charges="+charges);
		updateAmount(sctlID,sc.getTransactionAmount());
		updateCharges(sctlID, charges);
		CMBillPayInquiry billPayInquiry = new CMBillPayInquiry();
		billPayInquiry.setCharges(billPayInquiryToBank.getCharges());
		billPayInquiry.setAmount(billPayInquiryfromBank.getAmount());
		billPayInquiry.setSourceMDN(billPayInquiryToBank.getSourceMDN());
		billPayInquiry.setBillerCode(billPayInquiryToBank.getBillerCode());
		billPayInquiry.setInvoiceNumber(billPayInquiryToBank.getInvoiceNo());
		billPayInquiry.setPin(billPayInquiryToBank.getPin());
		billPayInquiry.setParentTransactionID(0L);
		billPayInquiry.setTransactionID(billPayInquiryToBank.getTransactionID());
		billPayInquiry.setTransactionIdentifier(billPayInquiryToBank.getTransactionIdentifier());
		billPayInquiry.setMessageType(CmFinoFIX.MessageType_BillPayInquiry);
		billPayInquiry.setServiceChargeTransactionLogID(sctlID);
		billPayInquiry.setSourcePocketID(billPayInquiryToBank.getSourcePocketID());
		billPayInquiry.setDestMDN(billPayInquiryToBank.getDestMDN());
		billPayInquiry.setDestPocketID(billPayInquiryToBank.getDestPocketID());
		billPayInquiry.setSourceApplication(billPayInquiryToBank.getSourceApplication());
		
		if(billPayInquiryfromBank.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success)) {
			
			CFIXMsg inquiryResponse;
			try {
				inquiryResponse= bankService.onTransferInquiryToBank(billPayInquiry);
			} catch (Exception e) {
				log.error(e.getMessage());
				inquiryResponse = createResponseObject();
				((BackendResponse) inquiryResponse).setResult(CmFinoFIX.ResponseCode_Failure);
				((BackendResponse) inquiryResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
			
			if(inquiryResponse instanceof CMTransferInquiryToBank){
				BackendResponse amountResponse;
				try {
					amountResponse = (BackendResponse)bankService.onTransferInquiryFromBank((CMTransferInquiryToBank)inquiryResponse,billPayInquiryfromBank);
				} catch (Exception e) {
					log.error(e.getMessage());
					amountResponse = createResponseObject();
					((BackendResponse) amountResponse).setResult(CmFinoFIX.ResponseCode_Failure);
					((BackendResponse) amountResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
				}
				amountResponse.setPaymentInquiryDetails(sctlID.toString());
				amountResponse.setAdditionalInfo(billPayInquiryfromBank.getInfo3());
				amountResponse.setBillerCode(billPayInquiryToBank.getBillerCode());
				amountResponse.setInvoiceNumber(billPayInquiryToBank.getInvoiceNo());
				
				if(amountResponse.getResult().equals(CmFinoFIX.ResponseCode_Success))
				{
					amountResponse.setInternalErrorCode(NotificationCodes.BillpaymentInquirySuccessful.getInternalErrorCode());
				}
				
				mceMessage.setRequest(billPayInquiryfromBank);
				mceMessage.setResponse(amountResponse);
				
				return mceMessage;
			}
			else if(inquiryResponse instanceof BackendResponse){
			   ((BackendResponse) inquiryResponse).setPaymentInquiryDetails(sctlID.toString());
				mceMessage.setRequest(billPayInquiryfromBank);
				mceMessage.setResponse(inquiryResponse);
				return mceMessage;
			}
		}
		else{
			log.info("BillPaymentServiceImpl - Unable to get bill amount(+)");
			
			BackendResponse response = new BackendResponse();
			ResponseCodes rs = ResponseCodes.getResponseCodes(1, billPayInquiryfromBank.getResponseCode());

			response.setDescription(ExternalResponseCodeHolder.getNotificationText(billPayInquiryfromBank.getResponseCode()));
			response.setExternalResponseCode(rs.getExternalResponseCode());
			response.setInternalErrorCode(rs.getInternalErrorCode());
			response.setPaymentInquiryDetails(sctlID.toString());
			
			// Handle Notifications for PLN Billers separately, setting only for failure case
			//if(billPayInquiryToBank.getBillerCode() != null && plnBillers.contains(billPayInquiryToBank.getBillerCode())){
			if(billPayInquiryToBank.getBillerCode() != null && isPlnBiller(billPayInquiryToBank.getBillerCode())){
				response.setInternalErrorCode(getPLNErrorCode(CmFinoFIX.ResponseCode_Failure, billPayInquiryfromBank.getResponseCode()));
				response.setExternalResponseCode(null);
			}
			
			mceMessage.setRequest(billPayInquiryfromBank);
			mceMessage.setResponse(response);
		}
		
		return mceMessage;
	}
	
	
	private void updateCharges(Long sctlID, BigDecimal charges) {
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setId(sctlID);
		List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
		if(CollectionUtils.isNotEmpty(list))
		{
		ServiceChargeTransactionLog sctl = list.get(0);
		sctl.setCalculatedCharge(charges);
		sctlDAO.save(sctl);
		}
		
	}

	private void updateAmount(Long sctlID, BigDecimal transactionAmount) {
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setId(sctlID);
		List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
		if(CollectionUtils.isNotEmpty(list))
		{
			ServiceChargeTransactionLog sctl = list.get(0);
			sctl.setTransactionAmount(transactionAmount);
			sctlDAO.save(sctl);
		}
		
	}
	
	public boolean isPlnBiller(String billerCode){
		if(plnPrepaidBillers.contains(billerCode) || plnPostpaidBillers.contains(billerCode) || plnNonTaglisBillers.contains(billerCode)){	
			return true;
		}
		return false;
	}
	
	private Integer getPLNErrorCode(Integer result, String responseCode) {
		if(result.equals(CmFinoFIX.ResponseCode_Success)){
			return NotificationCodes.PLNSuccess.getInternalErrorCode();
		}else if(result.equals(CmFinoFIX.ResponseCode_Failure)){
			if(responseCode.equals("14"))
				return NotificationCodes.PLNWrongIDPELID.getInternalErrorCode();
			else if(responseCode.equals("15"))
				return NotificationCodes.PLNWrongMeterID.getInternalErrorCode();
			else if(responseCode.equals("16"))
				return NotificationCodes.PLNBillIsPending.getInternalErrorCode();
			else if(responseCode.equals("41"))
				return NotificationCodes.PLNUnderPurchase.getInternalErrorCode();
			else if(responseCode.equals("47"))
				return NotificationCodes.PLNOverPurchase.getInternalErrorCode();
			else if(responseCode.equals("48"))
				return NotificationCodes.PLNRegNoExpired.getInternalErrorCode();
			else if(responseCode.equals("88"))
				return NotificationCodes.PLNBillAlreadyPaid.getInternalErrorCode();
			else if(responseCode.equals("89"))
				return NotificationCodes.PLNLatestBillNA.getInternalErrorCode();
			else if(responseCode.equals("98"))
				return NotificationCodes.PLNTimeout.getInternalErrorCode();
		}
		return NotificationCodes.BankAccountToBankAccountFailed.getInternalErrorCode(); // return some default
	}

	public Set<String> getPlnNonTaglisBillers() {
		return plnNonTaglisBillers;
	}

	public void setPlnNonTaglisBillers(Set<String> plnNonTaglisBillers) {
		this.plnNonTaglisBillers = plnNonTaglisBillers;
	}

	public Set<String> getPlnPostpaidBillers() {
		return plnPostpaidBillers;
	}

	public void setPlnPostpaidBillers(Set<String> plnPostpaidBillers) {
		this.plnPostpaidBillers = plnPostpaidBillers;
	}

	public Set<String> getPlnPrepaidBillers() {
		return plnPrepaidBillers;
	}

	public void setPlnPrepaidBillers(Set<String> plnPrepaidBillers) {
		this.plnPrepaidBillers = plnPrepaidBillers;
	}

	public Map<String, String> getPlnPrepaidSMSKeyValues() {
		return plnPrepaidSMSKeyValues;
	}

	public void setPlnPrepaidSMSKeyValues(Map<String, String> plnPrepaidSMSKeyValues) {
		this.plnPrepaidSMSKeyValues = plnPrepaidSMSKeyValues;
	}

	public Map<String, String> getPlnpostPaidSMSKeyValues() {
		return plnpostPaidSMSKeyValues;
	}

	public void setPlnpostPaidSMSKeyValues(
			Map<String, String> plnpostPaidSMSKeyValues) {
		this.plnpostPaidSMSKeyValues = plnpostPaidSMSKeyValues;
	}

	public Map<String, String> getPlnNonTaglisSMSKeyValues() {
		return plnNonTaglisSMSKeyValues;
	}

	public void setPlnNonTaglisSMSKeyValues(
			Map<String, String> plnNonTaglisSMSKeyValues) {
		this.plnNonTaglisSMSKeyValues = plnNonTaglisSMSKeyValues;
	}

	public List<String> getPlnPrepaidSMSKeyWords() {
		return plnPrepaidSMSKeyWords;
	}

	public void setPlnPrepaidSMSKeyWords(List<String> plnPrepaidSMSKeyWords) {
		this.plnPrepaidSMSKeyWords = plnPrepaidSMSKeyWords;
	}

	public List<String> getPlnPostpaidSMSKeyWords() {
		return plnPostpaidSMSKeyWords;
	}

	public void setPlnPostpaidSMSKeyWords(List<String> plnPostpaidSMSKeyWords) {
		this.plnPostpaidSMSKeyWords = plnPostpaidSMSKeyWords;
	}

	public List<String> getPlnNonTaglisSMSKeyWords() {
		return plnNonTaglisSMSKeyWords;
	}

	public void setPlnNonTaglisSMSKeyWords(List<String> plnNonTaglisSMSKeyWords) {
		this.plnNonTaglisSMSKeyWords = plnNonTaglisSMSKeyWords;
	}

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
}

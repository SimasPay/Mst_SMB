package com.mfino.billpayments.bsim.impl;


import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.bsim.BillPaymentService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.NoISOResponseMsg;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
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
import com.mfino.service.TransactionChargingService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class BillPaymentServiceImpl extends BillPaymentsBaseServiceImpl implements BillPaymentService{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	protected BankService bankService;
	protected  CMMoneyTransferToBank inquiryResponse;
	protected TransactionChargingService transactionChargingService;

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	protected String sourceToDestInquiryQueue = "jms:bsimsourceToDestInquiryQueue?disableReplyTo=true";
	protected String sourceToDestQueue = "jms:bsimsourceToDestQueue?disableReplyTo=true";
	protected String reversalQueue = "jms:bsimreversalQueue";
	protected String AmountFromBankQueue = "jms:bsimAmountFromBankQueue";

	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
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
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
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
		mceMessage.setRequest(request);
		mceMessage.setResponse(inquiryResponse);

		return mceMessage;

	}
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
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

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferCompletionSourceToDestination(MCEMessage mceMessage)
	{

		log.info("BillPaymentServiceImpl :: billPayMoneyTransferCompletionSourceToDestination mceMessage="+mceMessage);
		CMBSIMBillPaymentToBank billPayToBank = (CMBSIMBillPaymentToBank)mceMessage.getRequest();
		CMBSIMBillPaymentFromBank billPayfromBank = (CMBSIMBillPaymentFromBank)mceMessage.getResponse();
		BackendResponse Response;
		try {
		Response = (BackendResponse)bankService.onTransferConfirmationFromBank((CMMoneyTransferToBank)inquiryResponse,billPayfromBank);
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
		mceMessage.setRequest(request);
		mceMessage.setResponse(Response);
        return mceMessage;

	}
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
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
		log.info("BillPaymentServiceImpl :: set prefix processing code :" + billPayrevtobank.getProcessingCode());
		mceMessage.setResponse(billPayrevtobank);
		mceMessage.setDestinationQueue(reversalQueue);
		return mceMessage;

	}
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayReversalFromBank(MCEMessage mceMessage){
		CMBSIMBillPaymentReversalToBank billPayrevToBank = (CMBSIMBillPaymentReversalToBank)mceMessage.getRequest();
		CMBSIMBillPaymentReversalFromBank billPayrevFromBank = (CMBSIMBillPaymentReversalFromBank)mceMessage.getResponse();
		BackendResponse Response = (BackendResponse)bankService.onTransferReversalFromBank(billPayrevToBank,billPayrevFromBank);
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
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
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
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
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
		Long sctlID = billPayInquiryToBank.getServiceChargeTransactionLogID();
		updateAmount(sctlID,sc.getTransactionAmount());
		billPayInquiryToBank.setServiceChargeTransactionLogID(sctlID);
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
}

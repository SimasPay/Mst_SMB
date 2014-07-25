/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterbankCodesDao;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.dao.query.InterBankCodesQuery;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.InterBankCode;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.MFSDenominations;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillerService;
import com.mfino.service.MFAService;
import com.mfino.service.MFSBillerPartnerMapService;
import com.mfino.service.MFSBillerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.InterBankTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author HemanthKumar
 *
 */
@Service("NewInterBankTransferInquiryHandlerImpl")
public class NewInterBankTransferInquiryHandlerImpl extends FIXMessageHandler implements InterBankTransferInquiryHandler{

	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MFSBillerServiceImpl")
	private MFSBillerService mfsBillerService;
	
	@Autowired
	@Qualifier("MFSBillerPartnerMapServiceImpl")
	private MFSBillerPartnerMapService mfsBillerPartnerMapService;
	

	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	public Result handle(TransactionDetails transactionDetails) {
		CFIXMsg response = null;
		TransactionResponse transactionResponse = null;
		BigDecimal operatorChgs = BigDecimal.ZERO;
		String srcpocketcode;
		CMBillPayInquiry ibtInquiry= new CMBillPayInquiry();
		ChannelCode cc = transactionDetails.getCc();
		//set biller code from sys parameters ibt
		String ibtPartnerBillerCode = systemParametersService.getString(SystemParameterKeys.INTERBANK_PARTNER_MDN_KEY);
		transactionDetails.setBillerCode(ibtPartnerBillerCode);

		ibtInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		//set invoice as dest acc no
		ibtInquiry.setInvoiceNumber(transactionDetails.getDestAccountNumber());
		ibtInquiry.setDestinationBankAccountNo(transactionDetails.getDestAccountNumber());
		//set bank code
		ibtInquiry.setBenOpCode(transactionDetails.getDestBankCode());
		ibtInquiry.setPin(transactionDetails.getSourcePIN());
		ibtInquiry.setBillerCode(transactionDetails.getBillerCode());
		ibtInquiry.setAmount(transactionDetails.getAmount());
		ibtInquiry.setSourceApplication(cc.getChannelSourceApplication());
		ibtInquiry.setChannelCode(cc.getChannelCode());
		ibtInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		ibtInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		ibtInquiry.setMessageType(CmFinoFIX.MessageType_InterBankFundsTransferInquiry);
		ibtInquiry.setParentTransactionID(0L);
		ibtInquiry.setUICategory(CmFinoFIX.TransactionUICategory_InterBank_Transfer);
		
		
		srcpocketcode=transactionDetails.getSourcePocketCode();
        ibtInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
        log.info("Handling Subscriber IBT Inquiry webapi request");
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_InterBankFundsTransferInquiry, ibtInquiry.DumpFields());
		ibtInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(ibtInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());

		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(ibtInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+ibtInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		MFSBiller mfsBiller = mfsBillerService.getByBillerCode(ibtInquiry.getBillerCode());
		if (mfsBiller == null) {
			result.setBillerCode(ibtInquiry.getBillerCode());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidBillerCode);
			return result;
		}
		String bankCode = transactionDetails.getDestBankCode();
		if(!isIBTRestricted(bankCode,ibtInquiry)){
			log.info("Inter Bank transfer restricted bank code="+bankCode);
			result.setNotificationCode(CmFinoFIX.NotificationCode_IBTRestricted);
			return result;
		}
		//For Integration Code
		MFSBillerPartner mfsBillerPartner = mfsBillerPartnerMapService.getByBillerCode(ibtInquiry.getBillerCode());
		if (mfsBillerPartner != null){
			ibtInquiry.setIntegrationCode(mfsBillerPartner.getIntegrationCode());
		}
		
		Pocket subPocket = pocketService.getDefaultPocket(sourceMDN, srcpocketcode);
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		ibtInquiry.setSourceBankAccountNo(subPocket.getCardPAN()); // Storing SourceCardPAN in SourceBankAccNo to be used later while sending request to BSM
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(subPocket);
		result.setPocketList(pocketList);
		Partner partner = billerService.getPartner(ibtInquiry.getBillerCode());

		SubscriberMDN partnerMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		ibtInquiry.setEmail(sourceMDN.getSubscriber().getEmail());
		
		MFSBillerPartner results = mfsBiller.getMFSBillerPartnerFromMFSBillerId().iterator().next();
		if(results != null){
			ibtInquiry.setIntegrationCode(results.getIntegrationCode());
			ibtInquiry.setPartnerBillerCode(results.getPartnerBillerCode());
			ibtInquiry.setChargesIncluded(results.getChargesIncluded());
		}
		addCompanyANDLanguageToResult(sourceMDN, result);
		
		ServiceCharge serviceCharge=new ServiceCharge();
		serviceCharge.setChannelCodeId(cc.getID());
		serviceCharge.setDestMDN(partnerMDN.getMDN());
		serviceCharge.setServiceName(transactionDetails.getServiceName());//change to service
		serviceCharge.setTransactionTypeName(transactionDetails.getTransactionTypeName());
		serviceCharge.setSourceMDN(sourceMDN.getMDN());
		serviceCharge.setTransactionAmount(ibtInquiry.getAmount()!=null ? ibtInquiry.getAmount() : BigDecimal.ZERO);
		serviceCharge.setMfsBillerCode(ibtInquiry.getBillerCode());
		serviceCharge.setTransactionLogId(ibtInquiry.getTransactionID());
		serviceCharge.setInvoiceNo(ibtInquiry.getInvoiceNumber());
		serviceCharge.setOnBeHalfOfMDN(StringUtils.isNotBlank(ibtInquiry.getOnBeHalfOfMDN()) ? ibtInquiry.getOnBeHalfOfMDN() : StringUtils.EMPTY);
		serviceCharge.setTransactionIdentifier(ibtInquiry.getTransactionIdentifier());
		
		Pocket agentPocket;
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(serviceCharge.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(partner.getID(), servicePartnerId, serviceId);
			if (partnerService == null) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			agentPocket = partnerService.getPocketByDestPocketID();
			if(agentPocket==null){
				result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
				return result;
			}
			if (!agentPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
				return result;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(serviceCharge);
			ibtInquiry.setAmount(transaction.getAmountToCredit());
			ibtInquiry.setCharges(transaction.getAmountTowardsCharges());

		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);			
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		sctl.setIntegrationCode(ibtInquiry.getIntegrationCode());
	
		ibtInquiry.setServiceChargeTransactionLogID(sctl.getID());
		ibtInquiry.setDestMDN(partnerMDN.getMDN());
		ibtInquiry.setCharges(transaction.getAmountTowardsCharges());
		ibtInquiry.setChannelCode(cc.getChannelCode());
		ibtInquiry.setSourcePocketID(subPocket.getID());
		ibtInquiry.setDestPocketID(agentPocket.getID());
		ibtInquiry.setSourceApplication(cc.getChannelSourceApplication());
		
		response = super.process(ibtInquiry);

			// Saves the Transaction Id returned from Back End		
			transactionResponse = checkBackEndResponse(response);
			if (transactionResponse.getTransactionId()!=null) {
				
				sctl.setTransactionID(transactionResponse.getTransactionId());
				sctl.setCommodityTransferID(transactionResponse.getTransferId());
				ibtInquiry.setTransactionID(transactionResponse.getTransactionId());
				result.setTransactionID(transactionResponse.getTransactionId());
				transactionChargingService.saveServiceTransactionLog(sctl);
			}
			if (!transactionResponse.isResult() && sctl!=null){
				
				String errorMsg = transactionResponse.getMessage();
				transactionChargingService.failTheTransaction(sctl, errorMsg);	
			}
		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		result.setInvoiceNo(ibtInquiry.getInvoiceNumber());
		result.setSctlID(sctl.getID());
		result.setMultixResponse(response);
		result.setDebitAmount(sctl.getTransactionAmount());
		result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
		result.setServiceCharge(sctl.getCalculatedCharge().add(operatorChgs));
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setNominalAmount(ibtInquiry.getNominalAmount());

		//For 2 factor authentication
		if(transactionResponse.isResult() == true){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionDetails.getTransactionTypeName(), cc.getID()) == true){
				result.setMfaMode("OTP");
				mfaService.handleMFATransaction(sctl.getID(), sourceMDN.getMDN());
			}
		}
		return result;
	}
	
	public boolean isIBTRestricted(String bankCode, CMBillPayInquiry ibtInquiry){
		InterBankCode interBankCode = getBankCode(bankCode);
		boolean isIBTAllowed = ((interBankCode != null) && (interBankCode.getibAllowed())) ? true : false;
		//for interbank bankName is stored in info2
		ibtInquiry.setNarration(interBankCode.getBankName());
		return isIBTAllowed;
	}
	
	public InterBankCode getBankCode(String bankCode){
		InterBankCode nbCode = null;
		InterbankCodesDao nbDao = DAOFactory.getInstance().getInterbankCodesDao();
		InterBankCodesQuery query = new InterBankCodesQuery();
		query.setBankCode(bankCode);
		List<InterBankCode> nbCodeList = nbDao.get(query);
		if(nbCodeList.size() >0){
			nbCode = nbCodeList.get(0);
		}
		return nbCode;
	}
}

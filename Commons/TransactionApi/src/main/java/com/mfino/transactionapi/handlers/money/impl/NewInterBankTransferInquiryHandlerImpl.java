/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterbankCodesDao;
import com.mfino.dao.query.InterBankCodesQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.InterbankCodes;
import com.mfino.domain.MfsbillerPartnerMap;
import com.mfino.domain.MfsBiller;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
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
		String ibtPartnerBillerCode = systemParametersService.getString(SystemParameterKeys.UANGKU_IBT_BILLER_CODE);
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
		ibtInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		ibtInquiry.setChannelCode(cc.getChannelcode());
		ibtInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		ibtInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		ibtInquiry.setMessageType(CmFinoFIX.MessageType_InterBankFundsTransferInquiry);
		ibtInquiry.setParentTransactionID(0L);
		ibtInquiry.setUICategory(CmFinoFIX.TransactionUICategory_InterBank_Transfer);
		ibtInquiry.setOnBeHalfOfMDN(transactionDetails.getDestBankCode());
		
		srcpocketcode=transactionDetails.getSourcePocketCode();
        ibtInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
        log.info("Handling Subscriber IBT Inquiry webapi request");
		XMLResult result = new TransferInquiryXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_InterBankFundsTransferInquiry, ibtInquiry.DumpFields());
		ibtInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(ibtInquiry);
		result.setTransactionTime(transactionsLog.getTransactiontime());

		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(ibtInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+ibtInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		MfsBiller mfsBiller = mfsBillerService.getByBillerCode(ibtInquiry.getBillerCode());
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
		MfsbillerPartnerMap mfsBillerPartner = mfsBillerPartnerMapService.getByBillerCode(ibtInquiry.getBillerCode());
		if (mfsBillerPartner != null){
			ibtInquiry.setIntegrationCode(mfsBillerPartner.getIntegrationcode());
		}
		
		Pocket subPocket = pocketService.getDefaultPocket(sourceMDN, srcpocketcode);
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		ibtInquiry.setSourceBankAccountNo(subPocket.getCardpan()); // Storing SourceCardPAN in SourceBankAccNo to be used later while sending request to BSM
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(subPocket);
		result.setPocketList(pocketList);
		Partner partner = billerService.getPartner(ibtInquiry.getBillerCode());

		SubscriberMdn partnerMDN = partner.getSubscriber().getSubscriberMdns().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		ibtInquiry.setEmail(sourceMDN.getSubscriber().getEmail());
		
		MfsbillerPartnerMap results = mfsBiller.getMfsbillerPartnerMaps().iterator().next();
		if(results != null){
			ibtInquiry.setIntegrationCode(results.getIntegrationcode());
			ibtInquiry.setPartnerBillerCode(results.getPartnerbillercode());
			ibtInquiry.setChargesIncluded(results.getChargesincluded()==1?true:false);
		}
		addCompanyANDLanguageToResult(sourceMDN, result);
		
		ServiceCharge serviceCharge=new ServiceCharge();
		serviceCharge.setChannelCodeId(cc.getId().longValue());
		serviceCharge.setDestMDN(partnerMDN.getMdn());
		serviceCharge.setServiceName(transactionDetails.getServiceName());//change to service
		serviceCharge.setTransactionTypeName(transactionDetails.getTransactionTypeName());
		serviceCharge.setSourceMDN(sourceMDN.getMdn());
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
			PartnerServices partnerService = transactionChargingService.getPartnerService(partner.getId().longValue(), servicePartnerId, serviceId);
			if (partnerService == null) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			agentPocket = partnerService.getPocketByDestpocketid();
			if(agentPocket==null){
				result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
				return result;
			}
			if (!(agentPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
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
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		sctl.setIntegrationcode(ibtInquiry.getIntegrationCode());
	
		ibtInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		ibtInquiry.setDestMDN(partnerMDN.getMdn());
		ibtInquiry.setCharges(transaction.getAmountTowardsCharges());
		ibtInquiry.setChannelCode(cc.getChannelcode());
		ibtInquiry.setSourcePocketID(subPocket.getId().longValue());
		ibtInquiry.setDestPocketID(agentPocket.getId().longValue());
		ibtInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		
		response = super.process(ibtInquiry);

			// Saves the Transaction Id returned from Back End		
			transactionResponse = checkBackEndResponse(response);
			if (transactionResponse.getTransactionId()!=null) {
				
				sctl.setTransactionid(transactionResponse.getTransactionId());
				sctl.setCommoditytransferid(transactionResponse.getTransferId());
				ibtInquiry.setTransactionID(transactionResponse.getTransactionId());
				result.setTransactionID(transactionResponse.getTransactionId());
				transactionChargingService.saveServiceTransactionLog(sctl);
			}
			if (!transactionResponse.isResult() && sctl!=null){
				
				String errorMsg = transactionResponse.getMessage();
				transactionChargingService.failTheTransaction(sctl, errorMsg);	
			}
		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		result.setSctlID(sctl.getId().longValue());
		result.setMultixResponse(response);
		result.setDebitAmount(sctl.getTransactionamount());
		result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
		result.setServiceCharge(sctl.getCalculatedcharge().add(operatorChgs));
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setNominalAmount(ibtInquiry.getNominalAmount());
		result.setDestinationName(transactionResponse.getDestinationUserName());
		result.setDestinationAccountNumber(transactionDetails.getDestAccountNumber());
		result.setBankName(transactionResponse.getBankName());
		//For 2 factor authentication
		if(transactionResponse.isResult() == true){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionDetails.getTransactionTypeName(), cc.getId().longValue()) == true){
				result.setMfaMode("OTP");
				//mfaService.handleMFATransaction(sctl.getID(), sourceMDN.getMDN());
			}
		}
		return result;
	}
	
	public boolean isIBTRestricted(String bankCode, CMBillPayInquiry ibtInquiry){
		InterbankCodes interBankCode = getBankCode(bankCode);
		boolean isIBTAllowed = ((interBankCode.getIballowed()==1)) ? true : false;
		//for interbank bankName is stored in info2
		ibtInquiry.setNarration(interBankCode.getBankname());
		return isIBTAllowed;
	}
	
	public InterbankCodes getBankCode(String bankCode){
		InterbankCodes nbCode = null;
		InterbankCodesDao nbDao = DAOFactory.getInstance().getInterbankCodesDao();
		InterBankCodesQuery query = new InterBankCodesQuery();
		query.setBankCode(bankCode);
		List<InterbankCodes> nbCodeList = nbDao.get(query);
		if(nbCodeList.size() >0){
			nbCode = nbCodeList.get(0);
		}
		return nbCode;
	}
}

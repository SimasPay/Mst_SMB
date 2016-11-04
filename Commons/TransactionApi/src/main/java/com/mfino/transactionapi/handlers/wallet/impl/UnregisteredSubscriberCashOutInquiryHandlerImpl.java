/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.wallet.impl;

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
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiryForNonRegistered;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.transactionapi.handlers.wallet.UnregisteredSubscriberCashOutInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.MfinoUtil;

/**
 *
 * @author Maruthi
 */
@Service("UnregisteredSubscriberCashOutInquiryHandlerImpl")
public class UnregisteredSubscriberCashOutInquiryHandlerImpl extends FIXMessageHandler implements UnregisteredSubscriberCashOutInquiryHandler{

	private static Logger log = LoggerFactory.getLogger(UnregisteredSubscriberCashOutInquiryHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
 	
 	@Autowired
 	@Qualifier("UnRegisteredTxnInfoServiceImpl")
 	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Extracting data from transactionDetails in UnregisteredSubscriberCashOutInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		CMCashOutInquiryForNonRegistered unregisteredSubscriberCashOutInquiry = new CMCashOutInquiryForNonRegistered();
		ChannelCode cc = transactionDetails.getCc();
		unregisteredSubscriberCashOutInquiry.setSourceMDN(transactionDetails.getDestMDN());
		unregisteredSubscriberCashOutInquiry.setDestMDN(transactionDetails.getSourceMDN());
		unregisteredSubscriberCashOutInquiry.setPin(transactionDetails.getSourcePIN());
		unregisteredSubscriberCashOutInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		unregisteredSubscriberCashOutInquiry.setChannelCode(cc.getChannelcode());
		unregisteredSubscriberCashOutInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		unregisteredSubscriberCashOutInquiry.setSourceMessage(transactionDetails.getSourcePIN());
		//unregisteredSubscriberCashOutInquiry.setTransferID(transactionDetails.getTransferId());
		Long transferId = transactionDetails.getTransferId();
		String secreteCode = transactionDetails.getSecreteCode();
		unregisteredSubscriberCashOutInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling UnRegistered Subscriber CashOut Inquiry webapi request:: From agent " + unregisteredSubscriberCashOutInquiry.getDestMDN()
				+ "to unregistered Subscriber = " + unregisteredSubscriberCashOutInquiry.getSourceMDN()+" TransferID:"+ transferId);
		XMLResult result = new TransferInquiryXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_CashOutInquiryForNonRegistered, unregisteredSubscriberCashOutInquiry.DumpFields());
		unregisteredSubscriberCashOutInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(unregisteredSubscriberCashOutInquiry);
		result.setTransactionTime(transactionsLog.getTransactiontime());

		//Agent Validation
		SubscriberMdn destAgentMDN = subscriberMdnService.getByMDN(unregisteredSubscriberCashOutInquiry.getDestMDN());

		Integer validationResult = transactionApiValidationService.validateAgentMDN(destAgentMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Agent with mdn : "+unregisteredSubscriberCashOutInquiry.getSourceMDN()+" has failed validations");
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		Partner destAgent = partnerService.getPartner(destAgentMDN);
		unregisteredSubscriberCashOutInquiry.setPartnerCode(destAgent.getPartnercode());
		
		if(!(cc.getChannelsourceapplication()==(CmFinoFIX.SourceApplication_SMS))){
			validationResult = transactionApiValidationService.validatePin(destAgentMDN, unregisteredSubscriberCashOutInquiry.getPin());
		}else{
			validationResult = CmFinoFIX.ResponseCode_Success;
		}
		if(!validationResult.equals(CmFinoFIX.ResponseCode_Success)){
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)-destAgentMDN.getWrongpincount()));
			result.setNotificationCode(validationResult);
			return result;
		}
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(unregisteredSubscriberCashOutInquiry.getSourceMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		//Cashout allowed for Unregistered subscriber also
		if(!(CmFinoFIX.ResponseCode_Success.equals(validationResult)||
				CmFinoFIX.NotificationCode_SubscriberNotRegistered.equals(validationResult))){
			log.error("Source subscriber with mdn : "+unregisteredSubscriberCashOutInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcSubscriberPocket = subscriberService.getDefaultPocket(srcSubscriberMDN.getId().longValue(),systemParametersService.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED));		
		validationResult = transactionApiValidationService.validateSourcePocketForUnregistered(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);

		UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
		query.setTransferSctlId(transferId);
		query.setSubscriberMDNID(srcSubscriberMDN.getId().longValue());

		List<UnregisteredTxnInfo> unRegisteredTxnInfo = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(query);
		if(unRegisteredTxnInfo==null||unRegisteredTxnInfo.isEmpty()){
			log.error("Could not find the Unregistered trxn info record with sctlID: "+transferId+"and subscriberMDNID: "+srcSubscriberMDN.getId());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}
		UnregisteredTxnInfo unTxnInfo = unRegisteredTxnInfo.get(0);
		if(!(CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED.equals(unTxnInfo.getUnregisteredtxnstatus())
				||CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(unTxnInfo.getUnregisteredtxnstatus()))){
			if(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED.equals(unTxnInfo.getUnregisteredtxnstatus())){
				result.setNotificationCode(CmFinoFIX.NotificationCode_CashOutAlreadyRequested);				
			}else{
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
			}
			return result;
		}
		
		String prefix = systemParametersService.getString(SystemParameterKeys.FAC_PREFIX_VALUE);
		prefix = (prefix == null) ? StringUtils.EMPTY : prefix;
		int otpLength = Integer.parseInt(systemParametersService.getString(SystemParameterKeys.OTP_LENGTH));
		if(secreteCode.length() < (otpLength+prefix.length()))
			secreteCode = prefix + secreteCode;
		
		String securecode = MfinoUtil.calculateDigestPin(srcSubscriberMDN.getMdn(), secreteCode);
		if(!securecode.equals(unTxnInfo.getDigestedpin())){
			log.error("Invalid FundAccessCode: "+secreteCode);
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidFundAccessCode);
			return result;
		}

		CommodityTransfer ct = commodityTransferService.getCommodityTransferById(unTxnInfo.getTransferctid().longValue());
		// add service charge to amount

		log.info("creating serviceCharge object..." );
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setDestMDN(destAgentMDN.getMdn());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
		sc.setSourceMDN(srcSubscriberMDN.getMdn());
		sc.setTransactionAmount(ct.getAmount());
		sc.setMfsBillerCode(destAgent.getPartnercode());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
		sc.setTransactionLogId(unregisteredSubscriberCashOutInquiry.getTransactionID());
		sc.setTransactionIdentifier(unregisteredSubscriberCashOutInquiry.getTransactionIdentifier());
		
		Pocket destAgentPocket;
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(sc.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(destAgent.getId().longValue(), servicePartnerId, serviceId);
			if (partnerService == null) {
				log.error("PartnerService obtained null ");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			destAgentPocket = partnerService.getPocketByDestpocketid();
			validationResult = transactionApiValidationService.validateDestinationPocket(destAgentPocket);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Destination pocket with id "+(destAgentPocket!=null? destAgentPocket.getId():null)+" has failed validations");
				result.setNotificationCode(validationResult);
				return result;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		
		//Hierarchy service to check if this agent can do transaction with subscriber.
		validationResult = hierarchyService.validate(destAgentMDN.getSubscriber(), srcSubscriberMDN.getSubscriber(), ServiceAndTransactionConstants.SERVICE_AGENT, ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			unregisteredSubscriberCashOutInquiry.setAmount(transaction.getAmountToCredit());
			unregisteredSubscriberCashOutInquiry.setCharges(transaction.getAmountTowardsCharges());

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

		unregisteredSubscriberCashOutInquiry.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		unregisteredSubscriberCashOutInquiry.setDestPocketID(destAgentPocket.getId().longValue());
		unregisteredSubscriberCashOutInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		unTxnInfo.setCashoutsctlid(sctl.getId());
		unTxnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED);
		unRegisteredTxnInfoService.save(unTxnInfo);
		unregisteredSubscriberCashOutInquiry.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		log.info("sending the unregisteredSubscriberCashOutInquiry request to backend for processing");
		CFIXMsg response = super.process(unregisteredSubscriberCashOutInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!transactionResponse.isResult() && sctl!=null) 
		{
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}
		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionid(transactionResponse.getTransactionId());
			unregisteredSubscriberCashOutInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

		result.setSctlID(sctl.getId().longValue());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}

package com.mfino.transactionapi.handlers.wallet.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalConfirm;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.FundValidationService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.ReverseFundsHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
@Service("ReverseFundsHandlerImpl")
public class ReverseFundsHandlerImpl extends FIXMessageHandler implements ReverseFundsHandler{
	private static Logger log = LoggerFactory.getLogger(FundWithdrawalInquiryHandlerImpl.class);
	public static final String DUMMY_VALUE = "Dummy";
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("FundValidationServiceImpl")
	private FundValidationService fundValidationService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	public Result handle(UnRegisteredTxnInfo unRegisteredTxnInfo) {
		log.info("Creating CMFundWithdrawalInquiry message...");
		CMFundWithdrawalInquiry fundWithdrawalInquiry = new CMFundWithdrawalInquiry();
		ServiceChargeTransactionLog sctl = sctlService.getBySCTLID(unRegisteredTxnInfo.getTransferctid().longValue());
		fundWithdrawalInquiry.setSourceMDN(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		fundWithdrawalInquiry.setDestMDN(sctl.getSourceMDN());
		fundWithdrawalInquiry.setAmount(unRegisteredTxnInfo.getAvailableamount());
		fundWithdrawalInquiry.setSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		
		log.info("Handling Fund Reversal Inquiry request:: " + fundWithdrawalInquiry.getSourceMDN() + 
				 " For Amount = "+fundWithdrawalInquiry.getAmount() );
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_FundWithdrawalInquiry,fundWithdrawalInquiry.DumpFields());
		fundWithdrawalInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(fundWithdrawalInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());
				
		//Source partner(Third part suspence)--------------------------------------------------------------------------------
		String thirdPartyPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		log.info("doPost: Begin for destMDN:"+thirdPartyPartnerMDN);
		if (StringUtils.isBlank(thirdPartyPartnerMDN)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			log.error("Third Party Partner MDN Value in System Parameters is Null");
			return result;
		}

		SubscriberMdn  srcPartnerMDN = subscriberMdnService.getByMDN(thirdPartyPartnerMDN);

		Integer validationResult = transactionApiValidationService.validatePartnerMDN(srcPartnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Third party partner has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcPocket= pocketService.getSuspencePocket(partnerService.getPartner(srcPartnerMDN));
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		

		SubscriberMdn destinationMDN = subscriberMdnService.getByMDN(fundWithdrawalInquiry.getDestMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsDestination(destinationMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			result.setNotificationCode(validationResult);
			log.error("Destination subscriber with mdn : "+fundWithdrawalInquiry.getDestMDN()+" has failed validations");
			return result;
		}

		Pocket destPocket = pocketService.getDefaultPocket(destinationMDN, ServiceAndTransactionConstants.EMONEY_POCKET_CODE);
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		

		log.info("Csreating service charge object....");
		ServiceCharge sc=new ServiceCharge();
		sc.setDestMDN(destinationMDN.getMdn());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_FUND_REVERSAL);
		sc.setSourceMDN(srcPartnerMDN.getMdn());
		sc.setTransactionAmount(fundWithdrawalInquiry.getAmount());
		sc.setTransactionLogId(fundWithdrawalInquiry.getTransactionID());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		sc.setChannelCodeId(CmFinoFIX.SourceApplication_BackEnd);
		
		fundWithdrawalInquiry.setSourceMDN(thirdPartyPartnerMDN);
		fundWithdrawalInquiry.setSourcePocketID(srcPocket.getId().longValue());
		fundWithdrawalInquiry.setDestMDN(destinationMDN.getMdn());
		fundWithdrawalInquiry.setDestPocketID(destPocket.getId().longValue());
		fundWithdrawalInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		fundWithdrawalInquiry.setOneTimePassCode(DUMMY_VALUE);
		fundWithdrawalInquiry.setWithdrawalMDN(unRegisteredTxnInfo.getWithdrawalmdn());
		fundWithdrawalInquiry.setPin(DUMMY_VALUE);
		fundWithdrawalInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_FUND_REVERSAL);


		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			fundWithdrawalInquiry.setAmount(transaction.getAmountToCredit());
			fundWithdrawalInquiry.setCharges(transaction.getAmountTowardsCharges());

		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		sctl = transaction.getServiceChargeTransactionLog();

		fundWithdrawalInquiry.setServiceChargeTransactionLogID(sctl.getID());
		fundWithdrawalInquiry.setDistributionType(CmFinoFIX.DistributionType_Reversal);
		fundWithdrawalInquiry.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, fundWithdrawalInquiry, CmFinoFIX.Boolean_True, fundWithdrawalInquiry.getAmount());
		
		log.info("Sending the Fund Reversal  Inquiry Object to Backend for Processing...");
		result.setSourceMessage(fundWithdrawalInquiry);
		CFIXMsg response = super.process(fundWithdrawalInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse != null && CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString()
				.equals(transactionResponse.getCode())) {

			transactionChargingService.chnageStatusToProcessing(sctl);
			sctl.setParentSCTLID(unRegisteredTxnInfo.getTransferctid().longValue());
			sctl.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
			
			CMFundWithdrawalConfirm	fundWithdrawalConfirm = new CMFundWithdrawalConfirm();
			fundWithdrawalConfirm.setSourceMDN(thirdPartyPartnerMDN);
			fundWithdrawalConfirm.setDestMDN(destinationMDN.getMdn());
			fundWithdrawalConfirm.setIsSystemIntiatedTransaction(true);
			fundWithdrawalConfirm.setSourcePocketID(srcPocket.getId().longValue());
			fundWithdrawalConfirm.setDestPocketID(destPocket.getId().longValue());
			fundWithdrawalConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			fundWithdrawalConfirm.setSourceApplication(fundWithdrawalInquiry.getSourceApplication());
			fundWithdrawalConfirm.setParentTransactionID(transactionResponse.getTransactionId());
			fundWithdrawalConfirm.setTransferID(transactionResponse.getTransferId());
			fundWithdrawalConfirm.setConfirmed(CmFinoFIX.Boolean_True);
			fundWithdrawalConfirm.setServiceChargeTransactionLogID(sctl.getID());
			fundWithdrawalConfirm.setWithdrawalMDN(fundWithdrawalInquiry.getWithdrawalMDN());
			fundWithdrawalConfirm.setDistributionType(CmFinoFIX.DistributionType_Reversal);
			fundWithdrawalConfirm.setAmount(fundWithdrawalInquiry.getAmount());

			
			log.info("Sending the Fund Reversal Object to Backend for Processing...");
			result.setSourceMessage(fundWithdrawalConfirm);
			response = super.process(fundWithdrawalConfirm);
			transactionResponse = checkBackEndResponse(response);
			log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

			if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
				if (transactionResponse.isResult()) {
					transactionChargingService.confirmTheTransaction(sctl, fundWithdrawalConfirm.getTransferID());
					log.info("changing parent sctl status to expired");
					ServiceChargeTransactionLog parentSctl = DAOFactory.getInstance().getServiceChargeTransactionLogDAO().getById(sctl.getParentSCTLID());
					parentSctl.setStatus(CmFinoFIX.SCTLStatus_Expired);
					parentSctl.setFailureReason(unRegisteredTxnInfo.getReversalreason());
					transactionChargingService.saveServiceTransactionLog(parentSctl);
					commodityTransferService.addCommodityTransferToResult(result, transactionResponse.getTransferId());
					log.info("Fund Reversal has been Successfully Completed from "+fundWithdrawalConfirm.getSourceMDN()+"with amount "+fundWithdrawalConfirm.getAmount()+
																	"to "+fundWithdrawalConfirm.getDestMDN());

				}
				else {
					log.info("Fund Reversal failed from "+fundWithdrawalConfirm.getSourceMDN()+"with amount "+fundWithdrawalConfirm.getAmount()+
							"to "+fundWithdrawalConfirm.getDestMDN());
					result.setNotificationCode(CmFinoFIX.NotificationCode_FailedReversalFromATM);

				}
			} 
		}
		else { // Inquiry fails
			log.info("Fund Reversal Inquiry failed from "+fundWithdrawalInquiry.getSourceMDN()+"with amount "+fundWithdrawalInquiry.getAmount()+
					"to "+fundWithdrawalInquiry.getDestMDN());
			result.setNotificationCode(CmFinoFIX.NotificationCode_FailedReversalFromATM);
			
		}
		return result;

	}
}

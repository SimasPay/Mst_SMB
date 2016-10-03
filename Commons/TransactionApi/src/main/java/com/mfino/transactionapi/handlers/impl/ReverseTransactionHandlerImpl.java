/**
 *
 */
package com.mfino.transactionapi.handlers.impl;

import static com.mfino.constants.ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION;
import static com.mfino.constants.SystemParameterKeys.CHARGE_REVERSAL_FUNDING_POCKET;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMReverseTransaction;
import com.mfino.fix.CmFinoFIX.CMReverseTransactionInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.ChargeTxnCommodityTransferMapService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerServicesService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionTypeService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.transactionapi.handlers.ReverseTransactionHandler;

/**
 * @author Bala Sunku
 *
 */
@Service("ReverseTransactionHandlerImpl")
public class ReverseTransactionHandlerImpl extends FIXMessageHandler implements ReverseTransactionHandler{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService;
	
	@Autowired
	@Qualifier("ChargeTxnCommodityTransferMapServiceImpl")
	private ChargeTxnCommodityTransferMapService chargeTxnCommodityTransferMapService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private BulkUploadEntryService bulkUploadEntryService;
	
 	@Autowired
 	@Qualifier("UnRegisteredTxnInfoServiceImpl")
 	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
 	
	@Autowired
	@Qualifier("PartnerServicesServiceImpl")
	private PartnerServicesService partnerServicesService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;

	public void processReverseRequest(ServiceChargeTransactionLog sctl, ServiceChargeTransactionLog parentSCTL) {

		log.info("Processing the Reverse request for Reference Id --> " + parentSCTL.getID() + " And Reverse SCTLID is -->" + sctl.getID());
		if (sctl != null && parentSCTL != null && CmFinoFIX.SCTLStatus_Reverse_Start.equals(sctl.getStatus())) {
			ChannelCode cc = channelCodeService.getChannelCodeByChannelId(sctl.getChannelCodeID());

			Long chrgRevFundingPocketId = systemParametersService.getLong(CHARGE_REVERSAL_FUNDING_POCKET);
			Pocket reversalFundingPocket = pocketService.getById(chrgRevFundingPocketId);

			Long transactionTypeId = sctl.getTransactionTypeID();
			TransactionType transactionType = transactionTypeService.getTransactionTypeById(transactionTypeId);
			String transactionTypeName = null;

			if((null != transactionType) && (ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION.equals(transactionType.getTransactionname()))){
				transactionTypeName = ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION;
			}
			else if((null != transactionType) && (ServiceAndTransactionConstants.TRANSACTION_REVERSE_CHARGE.equals(transactionType.getTransactionname()))){
				transactionTypeName = ServiceAndTransactionConstants.TRANSACTION_REVERSE_CHARGE;
			}
			else{
				throw new RuntimeException("ReverseTransactionServiceImpl-transactionType could not be identified");
			}

			CMReverseTransactionInquiry reverseTransactionInquiry = new CMReverseTransactionInquiry();
			reverseTransactionInquiry.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
			reverseTransactionInquiry.setSourceMDN(sctl.getSourceMDN());
			reverseTransactionInquiry.setDestMDN(sctl.getDestMDN());
			reverseTransactionInquiry.setAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
			reverseTransactionInquiry.setCharges(sctl.getCalculatedCharge());
			reverseTransactionInquiry.setChannelCode(cc.getChannelcode());
			reverseTransactionInquiry.setSourceApplication(new Integer(String.valueOf(cc.getChannelsourceapplication())));
			reverseTransactionInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			reverseTransactionInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_REVERSE_TRANSACTION);
			reverseTransactionInquiry.setServiceChargeTransactionLogID(sctl.getID());
			reverseTransactionInquiry.setPin("dummy");
			reverseTransactionInquiry.setOriginalReferenceID(parentSCTL.getID());
			reverseTransactionInquiry.setSourceBankAccountNo(parentSCTL.getOnBeHalfOfMDN());
			
			// Get the Min CT and Max CT Records for the Transaction
			CommodityTransfer minCT = null;
			CommodityTransfer maxCT = null;
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(parentSCTL.getID());
			List<ChargeTxnCommodityTransferMap> lstTxnCommodityTransferMaps = chargeTxnCommodityTransferMapService.getChargeTxnCommodityTransferMapByQuery(query);
			if (CollectionUtils.isNotEmpty(lstTxnCommodityTransferMaps)) {
				for (ChargeTxnCommodityTransferMap ctmap: lstTxnCommodityTransferMaps) {
					CommodityTransfer ct = commodityTransferService.getCommodityTransferById(ctmap.getCommodityTransferID());
					if (! CmFinoFIX.TransactionUICategory_Charge_Distribution.equals(ct.getUicategory())) {
						if (minCT == null) {
							minCT = ct;
						} 
						else if (ct.getId().longValue() < minCT.getId().longValue()) {
							minCT = ct;						
						}
						
						if (maxCT == null) {
							maxCT = ct;
						}
						else if (ct.getId().longValue() > maxCT.getId().longValue()) {
							maxCT = ct;
						}
					}

				}
			}
			
			if(ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION.equals(transactionTypeName)) {
				// Set Source pocket as partner outgoing pocket in case of Purchase or bill_pay transactions
				Long parent_transactionTypeId = parentSCTL.getTransactionTypeID();
				TransactionType parent_transactionType = transactionTypeService.getTransactionTypeById(parent_transactionTypeId);
				if (parent_transactionType != null && 
						((ServiceAndTransactionConstants.TRANSACTION_PURCHASE.equalsIgnoreCase(parent_transactionType.getTransactionname())) || 
						(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY.equalsIgnoreCase(parent_transactionType.getTransactionname()))) ) {
					List<PartnerServices> lst = partnerServicesService.getPartnerServicesList(parentSCTL.getDestPartnerID(), parentSCTL.getServiceProviderID(), parentSCTL.getServiceID());
					if (CollectionUtils.isNotEmpty(lst)) {
						PartnerServices ps = lst.get(0);
						reverseTransactionInquiry.setSourcePocketID(ps.getPocketBySourcepocket().getId().longValue());
					}
				}
				else {
					reverseTransactionInquiry.setSourcePocketID(maxCT.getDestpocketid().longValue());
				}
				reverseTransactionInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Reverse_Transaction);
			}
			else{
				reverseTransactionInquiry.setSourcePocketID(reversalFundingPocket.getId().longValue());
				reverseTransactionInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Reverse_Charge);
			}

			reverseTransactionInquiry.setDestPocketID(minCT.getPocket().getId().longValue());
			
			log.debug("Send Inquiry Reverse Request to ServiceMix for processing.......");
			CFIXMsg response = super.process(reverseTransactionInquiry);
			TransactionResponse transactionResponse = checkBackEndResponse(response);
			log.debug("Response of ServiceMix for Inquiry Reverse Request --> "+ transactionResponse.isResult());
			if (transactionResponse != null && transactionResponse.isResult()) {  //Confirm message for the Reverse Transaction
				log.info("Transaction Log id for Reverse Request --> " + transactionResponse.getTransactionId() + " And Notificationcode --> " +  transactionResponse.getCode());
				sctl.setTransactionID(transactionResponse.getTransactionId());
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(transactionResponse.getCode())) {
					sctl.setStatus(CmFinoFIX.SCTLStatus_Reverse_Processing);
					sctlService.saveSCTL(sctl);

					CMReverseTransaction reverseTransaction = new CMReverseTransaction();
					reverseTransaction.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
					reverseTransaction.setSourceMDN(sctl.getSourceMDN());
					reverseTransaction.setDestMDN(sctl.getDestMDN());
					reverseTransaction.setChannelCode(cc.getChannelcode());
					reverseTransaction.setSourceApplication(new Integer(String.valueOf(cc.getChannelsourceapplication())));
					reverseTransaction.setServletPath(CmFinoFIX.ServletPath_Subscribers);
					reverseTransaction.setServiceChargeTransactionLogID(sctl.getID());
					reverseTransaction.setSourcePocketID(reverseTransactionInquiry.getSourcePocketID());
					reverseTransaction.setDestPocketID(reverseTransactionInquiry.getDestPocketID());
					reverseTransaction.setParentTransactionID(transactionResponse.getTransactionId());
					reverseTransaction.setTransferID(transactionResponse.getTransferId());
					reverseTransaction.setConfirmed(CmFinoFIX.Boolean_True);
					reverseTransaction.setServiceChargeTransactionLogID(sctl.getID());
					reverseTransaction.setOriginalReferenceID(parentSCTL.getID());
					reverseTransaction.setSourceBankAccountNo(parentSCTL.getOnBeHalfOfMDN());
					reverseTransaction.setUICategory(reverseTransactionInquiry.getUICategory());

					log.debug("Send Confirm Reverse Request to ServiceMix for processing.......");
					response = super.process(reverseTransaction);
					log.debug("Response of ServiceMix for confirm Reverse Request --> "+ transactionResponse.isResult());
					transactionResponse = checkBackEndResponse(response);
					if (transactionResponse != null && transactionResponse.isResult()) {
						log.info("Commodity Transafer Id for the Reverse Request is --> " + reverseTransaction.getTransferID());
						sctl.setCommodityTransferID(reverseTransaction.getTransferID());
						sctl.setStatus(CmFinoFIX.SCTLStatus_Reverse_Success);

						if(TRANSACTION_REVERSE_TRANSACTION.equals(transactionTypeName)){
							parentSCTL.setAmtRevStatus(CmFinoFIX.SCTLStatus_Reversed);
						}
						else{
							parentSCTL.setChrgRevStatus(CmFinoFIX.SCTLStatus_Reversed);
						}

						parentSCTL.setIsTransactionReversed(CmFinoFIX.Boolean_True);
						sctlService.saveSCTL(sctl);
						sctlService.saveSCTL(parentSCTL);
						// Check if the Parent Transaction is Sub Bulk Transfer
						checkIsSubBulkTransfer(parentSCTL);
						checkIsUnRegisteredTxn(parentSCTL);
						log.info("Reverse of the Transaction " + parentSCTL.getCommodityTransferID() + " is completed. And SCTLID -->" + sctl.getID());
					} else {
						failTheReverseRequest(sctl, parentSCTL, transactionResponse.getMessage());
					}
				} else {
					failTheReverseRequest(sctl, parentSCTL, transactionResponse.getMessage());
				}
			} else {
				failTheReverseRequest(sctl, parentSCTL, transactionResponse.getMessage());
			}
		} else {
			log.info("Invalid Reverse Request .......................");
		}
	}

	/**
	 * Check if the Parent Transaction is Sub Bulk Transfer then change the status in bulk upload entry to Expired.
	 * So that the money will be reversed from suspense pocket to Source pocket.
	 * @param parentSCTL
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	private void checkIsSubBulkTransfer(ServiceChargeTransactionLog parentSCTL) {
		Long transactionTypeId = parentSCTL.getTransactionTypeID();
		TransactionType transactionType = transactionTypeService.getTransactionTypeById(transactionTypeId);

		if (ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER.equals(transactionType.getTransactionname())) {
			BulkUploadEntry bue = bulkUploadEntryService.getBulkUploadEntryBySctlID(parentSCTL.getID());
			if (bue != null) {
				bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Expired);
				bue.setFailurereason(MessageText._("Transaction was Reversed manually"));
				bulkUploadEntryService.saveBulkUploadEntry(bue);
			}
		}
	}
	
	/**
	 * Check if the Parent Transaction is related to UnRegistered Subscriber then change the status in UnRegisteredTxnInfo
	 * table to 'Expired'.
	 * @param parentSCTL
	 */
	private void checkIsUnRegisteredTxn(ServiceChargeTransactionLog parentSCTL) {
		long sctlId = parentSCTL.getID();
		UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
		urtiQuery.setTransferSctlId(sctlId);
		List<UnregisteredTxnInfo> urtiList = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
		if (CollectionUtils.isNotEmpty(urtiList)) {
			UnregisteredTxnInfo urti = urtiList.get(0);
			if (CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED.equals(urti.getUnregisteredtxnstatus()) || 
					CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(urti.getUnregisteredtxnstatus())) {
				urti.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_EXPIRED.longValue());
				urti.setFailurereason(MessageText._("Transaction is Reversed."));
				unRegisteredTxnInfoService.save(urti);
				log.info("UnRegisteredTxnInfo status chnaged to expired for Transfer SCTLId:" + sctlId);
			}
		}
	}
	
	private void failTheReverseRequest(ServiceChargeTransactionLog sctl, ServiceChargeTransactionLog parentSCTL, String failureReason) {
		log.info("Reverse of the Transaction " + parentSCTL.getCommodityTransferID() + " is Failed. And SCTLID -->" + sctl.getID());
		if (StringUtils.isNotBlank(failureReason) && failureReason.length() > 255) {
			failureReason = failureReason.substring(0,  255);
		}
		sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
		sctl.setFailureReason(failureReason);

		//parentSCTL.setStatus(CmFinoFIX.SCTLStatus_Reverse_Failed);
		Long transactionTypeId = sctl.getTransactionTypeID();
		TransactionType transactionType = transactionTypeService.getTransactionTypeById(transactionTypeId);
		String transactionTypeName = null;

		if((null != transactionType) && (ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION.equals(transactionType.getTransactionname()))){
			transactionTypeName = ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION;
		}
		else if((null != transactionType) && (ServiceAndTransactionConstants.TRANSACTION_REVERSE_CHARGE.equals(transactionType.getTransactionname()))){
			transactionTypeName = ServiceAndTransactionConstants.TRANSACTION_REVERSE_CHARGE;
		}
		else{
			throw new RuntimeException("ReverseTransactionServiceImpl-transactionType could not be identified");
		}

		if(TRANSACTION_REVERSE_TRANSACTION.equals(transactionTypeName)){
			parentSCTL.setAmtRevStatus(CmFinoFIX.SCTLStatus_Reverse_Failed);
		}
		else{
			parentSCTL.setChrgRevStatus(CmFinoFIX.SCTLStatus_Reverse_Failed);
		}

		parentSCTL.setFailureReason(failureReason);
		parentSCTL.setIsTransactionReversed(CmFinoFIX.Boolean_False);

		sctlService.saveSCTL(sctl);
		sctlService.saveSCTL(parentSCTL);
		sendNotification(parentSCTL, CmFinoFIX.NotificationCode_ReverseTransactionRequestFailed);
	}

	private void sendNotification(ServiceChargeTransactionLog parentSCTL, Integer notificationCode) {

		NotificationWrapper notificationWrapper = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		String mdn = parentSCTL.getSourceMDN();
		SubscriberMdn smdn = subscriberMdnService.getByMDN(mdn);
		if(smdn != null)
		{
			language = new Integer(String.valueOf(smdn.getSubscriber().getLanguage()));
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstname());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastname());
		}
		notificationWrapper.setLanguage(language);
		notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notificationWrapper.setCode(notificationCode);
		notificationWrapper.setOriginalTransferID(parentSCTL.getID());

        String message = notificationMessageParserService.buildMessage(notificationWrapper,true);

        smsService.setDestinationMDN(parentSCTL.getSourceMDN());
        smsService.setMessage(message);
        smsService.setNotificationCode(notificationWrapper.getCode());
        smsService.setSctlId(parentSCTL.getID());
        smsService.asyncSendSMS();
	}
}

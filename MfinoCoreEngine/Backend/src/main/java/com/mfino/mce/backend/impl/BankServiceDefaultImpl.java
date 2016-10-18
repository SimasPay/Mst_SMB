package com.mfino.mce.backend.impl;

import static com.mfino.mce.backend.util.BackendUtil.setPocketLimits;
import static com.mfino.mce.core.util.MCEUtil.isNullOrEmpty;
import static com.mfino.mce.core.util.MCEUtil.isNullorZero;
import static com.mfino.mce.core.util.MCEUtil.safeString;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.SCTLSettlementMapDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.MfsLedger;
import com.mfino.domain.NoISOResponseMsg;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SctlSettlementMap;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SystemParameters;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransfer;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBulkDistribution;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOut;
import com.mfino.fix.CmFinoFIX.CMCashOutAtATM;
import com.mfino.fix.CmFinoFIX.CMCashOutAtATMInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOutForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiryForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivation;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationFromBank;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationToBank;
import com.mfino.fix.CmFinoFIX.CMGetBankAccountTransactions;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivation;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationFromBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.fix.CmFinoFIX.CMPinLessInquiryLessTransfer;
import com.mfino.fix.CmFinoFIX.CMPurchase;
import com.mfino.fix.CmFinoFIX.CMPurchaseInquiry;
import com.mfino.fix.CmFinoFIX.CMReverseTransaction;
import com.mfino.fix.CmFinoFIX.CMReverseTransactionInquiry;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationCashIn;
import com.mfino.fix.CmFinoFIX.CMTransactionReversal;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToNonRegistered;
import com.mfino.fix.CmFinoFIX.CMTransferToNonRegistered;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATM;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATMInquiry;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.backend.CommodityTransferService;
import com.mfino.mce.backend.ConversionToReversalRequestProcessor;
import com.mfino.mce.backend.IntegrationSummaryService;
import com.mfino.mce.backend.LedgerService;
import com.mfino.mce.backend.ValidationService;
import com.mfino.mce.backend.util.BackendUtil;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.ExternalResponseCodeHolder;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.mce.core.util.ResponseCodes;
import com.mfino.mce.core.util.StringUtilities;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author sasidhar
 * 
 */
public class BankServiceDefaultImpl extends BaseServiceImpl implements
		BankService {

	protected ValidationService validationService;
	protected CommodityTransferService commodityTransferService;
	protected LedgerService ledgerService;
	protected IntegrationSummaryService integrationSummaryService;
	protected SystemParametersService systemParametersService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onTransferConfirmationFromBank(CMMoneyTransferToBank toBank,
			CMMoneyTransferFromBank fromBank) {

		log.info("BankServiceDefaultImpl:: onTransferConfirmationFromBank() Begin");

		log.debug("BankServiceDefaultImpl :: onTransferConfirmationFromBank() toBank.DumpFields() :*: "
				+ toBank.DumpFields());
		log.debug("BankServiceDefaultImpl :: onTransferConfirmationFromBank() fromBank.DumpFields() :*: "
				+ fromBank.DumpFields());
		
		BackendResponse returnFix = createResponseObject();
		returnFix.copy(toBank);

		Subscriber objSourceSubscriber = null;
		Subscriber objDestSubscriber = null;
		SystemParameters dummySubMdnParam = coreDataWrapper.getSystemParameterByName(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
		String dummySubMdn = (dummySubMdnParam != null) ? dummySubMdnParam.getParametervalue() : null;
		SubscriberMdn objSrcSubMdn = coreDataWrapper.getSubscriberMdn(toBank.getSourceMDN());
		SubscriberMdn objDestSubMdn = coreDataWrapper.getSubscriberMdn(toBank.getDestMDN());
		if (objSrcSubMdn != null) {
			objSourceSubscriber = objSrcSubMdn.getSubscriber();
		}
		if (objDestSubMdn != null) {
			objDestSubscriber = objDestSubMdn.getSubscriber();
		}

		Pocket objSrcPocket = coreDataWrapper.getPocketById(
				toBank.getSourcePocketID(), LockMode.UPGRADE);
	
		Pocket objDestPocket = null;
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubscriber.getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			objDestPocket = coreDataWrapper.getPocketById(
					toBank.getDestPocketID(), LockMode.UPGRADE);
		} else {
			objDestPocket = coreDataWrapper.getPocketById(toBank.getDestPocketID());
		}		

//		Pocket suspensePocket = coreDataWrapper.getSuspensePocketWithLock();
//		Pocket chargesPocket = coreDataWrapper.getChargesPocketWithLock();
		// The amount received from bank is deducted in this account to note the
		// liability
//		Pocket globalSVAPocket = coreDataWrapper.getGlobalSVAPocketWithLock();
		returnFix.setRemarks(toBank.getRemarks());
		returnFix.setSourceMDN(objSrcSubMdn.getMdn());
		returnFix.setSenderMDN(objSrcSubMdn.getMdn());
		returnFix.setLanguage((int)objSourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(objDestSubMdn.getMdn());
		returnFix.setTransactionID(toBank.getTransactionID());
		returnFix.setParentTransactionID(toBank.getTransactionID());
		returnFix.setReceiveTime(fromBank.getReceiveTime());
		returnFix.setDestBankAccountNumber(toBank.getDestinationBankAccountNo());
		returnFix.setSourceBankAccountNumber(toBank.getSourceBankAccountNo());
		returnFix.setReceiverAccountNo(objDestPocket.getCardpan());
		returnFix.setSourcePocketId(objSrcPocket.getId().longValue());
		returnFix.setDestPocketId(objDestPocket.getId().longValue());
		returnFix.setOnBehalfOfMDN(toBank.getDestCardPAN());
		
		PendingCommodityTransfer pct = coreDataWrapper.getPCTById(toBank
				.getTransferID());
		returnFix.setTransferID(toBank.getTransferID());
		// returnFix.setTransferID(toBank.getServiceChargeTransactionLogID());
		if (pct != null) {
			pct.setBankresponsecode(Long.valueOf(fromBank.getResponseCode().equals(
					CmFinoFIX.ISO8583_ResponseCode_Success) ? CmFinoFIX.ResponseCode_Success
					: CmFinoFIX.ResponseCode_Failure));
			pct.setOperatorresponsecode(Long.valueOf((fromBank.getResponseCode())));
			pct.setBankresponsetime(fromBank.getReceiveTime());

			if (fromBank.getAIR() != null)
				pct.setBankauthorizationcode(fromBank.getAIR());
			if (fromBank.getErrorText() != null)
				pct.setBankerrortext(fromBank.getErrorText());
			if (fromBank.getResponseCode().equals(
					CmFinoFIX.ISO8583_ResponseCode_Success))
				pct.setBankrejectreason(fromBank.getResponseCode());

			pct.setEndtime(fromBank.getReceiveTime());

			BigDecimal amount = pct.getAmount();
			BigDecimal charges = pct.getCharges();

			if (fromBank.getResponseCode().equals(
					CmFinoFIX.ISO8583_ResponseCode_Success)) {
				// pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
				// pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
				pct.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountToBankAccountCompletedToSenderMDN
						.getNotificationCode()));

				// FIXME: add this code to a new method and extend this in the
				// child bank serivce implementation classes.
				if (null != pct.getUicategory()) {
					final Integer uiCategory = pct.getUicategory().intValue();

					if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_EMoney_CashIn
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.CashInToEMoneyCompletedToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountToEMoneyCompletedToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_EMoney_CashOut
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.CashOutFromEMoneyCompletedToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_EMoney_EMoney_Trf
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.EMoneytoEMoneyCompleteToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.EMoneyToBankAccountCompletedToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.EMoneyToBankAccountMerchantCompletedToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Teller_Cashin_SelfTransfer
							.intValue()) {
						// FIXME: change the notification code
						pct.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountToEMoneyCompletedToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank
							.intValue()) {
						// FIXME: change the notification code
						pct.setNotificationcode(Long.valueOf(NotificationCodes.EMoneyToBankAccountCompletedToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Reverse_Transaction
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.ReverseTransactionCompleteToSender
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Bulk_Transfer
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.BulkTransferCompletedToPartner
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.BulkTransferCompletedToSubscriber_Dummy
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.BulkTransferReverseCompletedToPartner
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Auto_Reverse
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.AutoReverseSuccess
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Fund_Allocation
							.intValue()) {
						pct.setNotificationcode(Long.valueOf(NotificationCodes.FundAllocationConfirmationPrompt
								.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Transfer_To_Uangku.intValue()) {
							pct.setNotificationcode(Long.valueOf(NotificationCodes.TransferToUangkuToSender.getNotificationCode()));
					} else if (uiCategory.intValue() == CmFinoFIX.TransactionUICategory_Fund_Withdrawal
							.intValue()) {
						FundServiceImpl fundServiceImpl = new FundServiceImpl();
						fundServiceImpl.setCoreDataWrapper(coreDataWrapper);
						pct.setNotificationcode(Long.valueOf(fundServiceImpl
								.getBankNotificationCode(returnFix)));
					}

				}

				// If the Destination Bank account number is not null then
				// change the Notification Message
				if (StringUtils
						.isNotBlank(toBank.getDestinationBankAccountNo())) {
					pct.setNotificationcode(Long.valueOf(NotificationCodes.TransferToBankAccountCompletedToSender
							.getNotificationCode()));
				}

				if ((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
						==(CmFinoFIX.PocketType_SVA) || isLakupandaiPocketType(objSrcPocket))
						&& (objDestPocket.getPocketTemplateByPockettemplateid().getType()
								==(CmFinoFIX.PocketType_BankAccount))) {

//					ledgerService.createLedgerEntry(suspensePocket,
//							chargesPocket, null, pct, charges);
//					ledgerService.createLedgerEntry(suspensePocket,
//							globalSVAPocket, null, pct, amount);
//					coreDataWrapper.save(suspensePocket);
//					if (pct.getCharges().compareTo(BigDecimal.valueOf(0)) == 1) {
//						coreDataWrapper.save(chargesPocket);
//					}
//					coreDataWrapper.save(globalSVAPocket);
					List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,toBank.getServiceChargeTransactionLogID(), pct.getId().longValue(), 
							coreDataWrapper.getSuspensePocket(), coreDataWrapper.getGlobalSVAPocket(), coreDataWrapper.getChargesPocket(), 
							amount, charges, ConfigurationUtil.getMfinoNettingLedgerEntries());
					coreDataWrapper.save(lstMfsLedgers);

					returnFix.setSourceMDNBalance(moneyService.round(new BigDecimal(objSrcPocket
							.getCurrentbalance())));
					returnFix.setDestinationMDNBalance(BigDecimal.valueOf(-1));
				}
				if ((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
						==(CmFinoFIX.PocketType_BankAccount))
						&& (objDestPocket.getPocketTemplateByPockettemplateid().getType()
								==(CmFinoFIX.PocketType_SVA) || isLakupandaiPocketType(objDestPocket))) {
					pct.setDestpocketbalance(objDestPocket.getCurrentbalance());
//					ledgerService.createLedgerEntry(suspensePocket,
//							chargesPocket, null, pct, charges);
//					ledgerService.createLedgerEntry(suspensePocket,
//							objDestPocket, null, pct, amount);
//					coreDataWrapper.save(suspensePocket);
//					if (pct.getCharges().compareTo(BigDecimal.valueOf(0)) == 1) {
//						coreDataWrapper.save(chargesPocket);
//					}
					List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,toBank.getServiceChargeTransactionLogID(), pct.getId().longValue(), 
							coreDataWrapper.getSuspensePocket(), objDestPocket, coreDataWrapper.getChargesPocket(), amount, charges, 
							ConfigurationUtil.getMfinoNettingLedgerEntries());
					coreDataWrapper.save(lstMfsLedgers);
					if(ledgerService.isImmediateUpdateRequiredForPocket(objDestPocket)){
					coreDataWrapper.save(objDestPocket);
					}

					returnFix.setSourceMDNBalance(BigDecimal.valueOf(-1));
					returnFix.setDestinationMDNBalance(moneyService.round(new BigDecimal(objDestPocket
							.getCurrentbalance())));
				}
				if ((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
						==(CmFinoFIX.PocketType_BankAccount))
						&& (objDestPocket.getPocketTemplateByPockettemplateid().getType()
								==(CmFinoFIX.PocketType_BankAccount))) {
					returnFix.setSourceMDNBalance(BigDecimal.valueOf(-1));
					returnFix.setDestinationMDNBalance(BigDecimal.valueOf(-1));
				}

				returnFix.setResult(CmFinoFIX.ResponseCode_Success);
				returnFix.setInternalErrorCode(NotificationCodes
						.getInternalErrorCodeFromNotificationCode(pct
								.getNotificationcode().intValue()));
				returnFix.setCurrency(pct.getCurrency());
				returnFix.setAmount(moneyService.round(pct.getAmount()));
				returnFix.setCharges(moneyService.round(charges));
				returnFix.setOriginalReferenceID(toBank
						.getOriginalReferenceID());
				coreDataWrapper.save(pct);

				handlePCTonSuccess(pct);
			} else {
				/* Transfer failed, bank rejected the request. */
				// Reverting Pocket Limits
				log.info("Bank rejected the requested\n");
				onRevertOfTransferConfirmation(pct, false);

				pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);

				ResponseCodes rs = ResponseCodes.getResponseCodes(1,
						fromBank.getResponseCode());
				if (rs == ResponseCodes.bank_Failure) {
					returnFix.setDescription(ExternalResponseCodeHolder
							.getNotificationText(fromBank.getResponseCode()));
				}
				returnFix.setExternalResponseCode(rs.getExternalResponseCode());

				Integer internalErrorCode = rs.getInternalErrorCode();
				pct.setNotificationcode(Long.valueOf(NotificationCodes
						.getNotificationCodeFromInternalCode(internalErrorCode)));
				pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountToBankAccountRejectedByBank));

				returnFix.setResult(CmFinoFIX.ResponseCode_Failure);
				returnFix.setInternalErrorCode(internalErrorCode);

				handlePCTonFailure(pct);
			}
//			integrationSummaryService.logIntegrationSummary(toBank.getServiceChargeTransactionLogID(), pct.getID(), "BANK", fromBank.getProcessingCode(), null, fromBank.getBankAccountName(), null,toBank.getReceiveTime());
			if(fromBank.getProcessingCode()!=null){
				if (fromBank.getBankAccountName() != null){
					returnFix.setBankName(fromBank.getBankAccountName().trim());
				}
			}
		} else {
			log.error("Could not find PCT with transfer id "
					+ toBank.getTransferID());
			returnFix
					.setInternalErrorCode(NotificationCodes.DBGetPendingCommodityTransferFailed
							.getInternalErrorCode());
		}
// [Bala] Commented the Activities log creation
		/* Activity Log */
//		ActivitiesLog activitiesLog = coreDataWrapper
//				.getActivitiesLogByParentTransactionId(toBank
//						.getParentTransactionID());
//		if (activitiesLog != null) {
//			activitiesLog.setIsSuccessful(CmFinoFIX.Boolean_True);
//			activitiesLog.setNotificationCode(pct.getNotificationCode());
//			coreDataWrapper.save(activitiesLog);
//		}
		returnFix.setServiceChargeTransactionLogID(toBank
				.getServiceChargeTransactionLogID());
		return returnFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onTransferInquiryFromBank(CMTransferInquiryToBank toBank,
			CMTransferInquiryFromBank fromBank) {

		log.info("BankServiceDefaultImpl :: onTransferInquiryFromBank() begin**");
		log.info("BankServiceDefaultImpl :: onTransferInquiryFromBank() toBank.DumpFields() :: "
				+ toBank.DumpFields());
		log.info("BankServiceDefaultImpl :: onTransferInquiryFromBank() fromBank.DumpFields() :: "
				+ fromBank.DumpFields());


		BackendResponse returnFix = createResponseObject();
		returnFix.setRemarks(toBank.getRemarks());

		PendingCommodityTransfer pct = coreDataWrapper.getPCTById(toBank
				.getTransferID());

		if (pct != null) {
			String externalResponseCode = fromBank.getResponseCode();

			if (ResponseCodes.ISO_ResponseCode_Success
					.getExternalResponseCode().equals(externalResponseCode)) {
				pct.setBankresponsecode(Long.valueOf(CmFinoFIX.ResponseCode_Success));
			} else {
				pct.setBankresponsecode(Long.valueOf(CmFinoFIX.ResponseCode_Failure));
			}
            
			pct.setBankresponsetime(fromBank.getReceiveTime());

			if (!isNullOrEmpty(fromBank.getAIR())) {
				pct.setBankauthorizationcode(fromBank.getAIR());
			}

			if (!isNullOrEmpty(fromBank.getErrorText())) {
				pct.setBankerrortext(fromBank.getErrorText());
			}

			if (fromBank.getResponseCode() != CmFinoFIX.ISO8583_ResponseCode_Success) {
				pct.setBankrejectreason(fromBank.getResponseCode());
			}

			log.info("BankServiceDefaultImpl: onTransferInquiryFromBank : response code from bank is "
					+ fromBank.getResponseCode());
          
			if(fromBank.getProcessingCode()!=null){
				if (fromBank.getBankAccountName() != null){
					returnFix.setBankName(fromBank.getBankAccountName().trim());
				}
				
				if((fromBank.getDestinationUserName() != null)){
					returnFix.setDestinationUserName(fromBank.getDestinationUserName().trim());
				}
			
				integrationSummaryService.logIntegrationSummary(toBank.getServiceChargeTransactionLogID(), pct.getId().longValue(), "BANK", fromBank.getProcessingCode(), fromBank.getAdditionalInfo(), fromBank.getBankAccountName(), fromBank.getDestinationUserName(),toBank.getReceiveTime());
			}
			if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(fromBank.getResponseCode())) {
				/* success response from bank */
				
				if(StringUtils.isNotBlank(fromBank.getBankName())){
					returnFix.setBankName(fromBank.getBankName());
				}
				
				pct.setTransferstatus(CmFinoFIX.TransferStatus_ConfirmationPromptSentToSubscriber);
				pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_Inititalized));
				if ((fromBank.getBankAccountName() != null)
						&& pct.getDestpockettype().intValue() == CmFinoFIX.PocketType_BankAccount
								.intValue()) {
					pct.setDestbankaccountname(fromBank.getBankAccountName());
					
				}
				returnFix.setResult(CmFinoFIX.ResponseCode_Success);
				returnFix
						.setInternalErrorCode(NotificationCodes.BankAccountToBankAccountConfirmationPrompt
								.getInternalErrorCode());
				
				coreDataWrapper.save(pct);
			} else {
				/* bank rejected the request */
				// Reverting Pocket Limits\

				onRevertOfTransferInquiry(pct, false);
				pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
				pct.setEndtime(fromBank.getReceiveTime());
				pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountTransferInquiryRejectedByBank));
				ResponseCodes responseCode = ResponseCodes.getResponseCodes(
						fromBank.getMessageType(), fromBank.getResponseCode());
				if (responseCode == ResponseCodes.bank_Failure) {
					returnFix.setDescription(ExternalResponseCodeHolder
							.getNotificationText(fromBank.getResponseCode()));
				}
				pct.setNotificationcode(Long.valueOf(NotificationCodes
						.getNotificationCodeFromInternalCode(responseCode
								.getInternalErrorCode())));

				returnFix.setInternalErrorCode(responseCode
						.getInternalErrorCode());
				returnFix.setResult(CmFinoFIX.ResponseCode_Failure);
				returnFix.setExternalResponseCode(fromBank.getResponseCode());

				handlePCTonFailure(pct);
			}

			returnFix
					.setSourcePocketId(pct.getPocket().getId().longValue());
			returnFix.setDestPocketId(pct.getDestpocketid().longValue());
			returnFix.setTransferID(pct.getId().longValue());
			returnFix.setTransactionID(pct.getId().longValue());
			returnFix.setParentTransactionID(pct
					.getTransactionLog().getId().longValue());
			returnFix.setAmount(moneyService.round(pct.getAmount()));
			returnFix.setCharges(moneyService.round(pct.getCharges()));
			returnFix.setCurrency(pct.getCurrency());
			returnFix.setSenderMDN(pct.getSourcemdn());
			returnFix.setReceiverMDN(pct.getDestmdn());
			returnFix.setReceiverName(pct.getDestsubscribername());
			if (StringUtils.isNotBlank(toBank.getDestinationBankAccountNo())) {
				returnFix.setReceiverMDN(toBank.getDestinationBankAccountNo());
				returnFix.setDestinationType("Account");
			} else {
				returnFix.setDestinationType(StringUtils.EMPTY);
			}
			/* Update PCT */

		} else {
			log.error("Could not find PCT with transfer id "
					+ toBank.getTransferID());
			returnFix
					.setInternalErrorCode(NotificationCodes.DBGetPendingCommodityTransferFailed
							.getInternalErrorCode());
		}
		returnFix.setServiceChargeTransactionLogID(toBank
				.getServiceChargeTransactionLogID());
		return returnFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onTransferInquiryToBank(CMBankAccountToBankAccount requestFix) {
		log.info("BankServiceDefaultImpl::onTransferInquiryToBank() Begin@");
		CFIXMsg isoFix = null;
		BackendResponse returnFix = createResponseObject();

		ActivitiesLog activitiesLog = new ActivitiesLog();
		PendingCommodityTransfer pct = null;

		Subscriber objSourceSubscriber = null;
		Subscriber objDestSubscriber = null;
		SystemParameters dummySubMdnParam = coreDataWrapper.getSystemParameterByName(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
		String dummySubMdn = (dummySubMdnParam != null) ? dummySubMdnParam.getParametervalue() : null;
		SubscriberMdn objSrcSubMdn = coreDataWrapper.getSubscriberMdn(
				requestFix.getSourceMDN(), LockMode.UPGRADE);
		SubscriberMdn objDestSubMdn = coreDataWrapper.getSubscriberMdn(requestFix.getDestMDN());
		if (objSrcSubMdn != null) {
			objSourceSubscriber = objSrcSubMdn.getSubscriber();
		}
		if (objDestSubMdn != null) {
			objDestSubscriber = objDestSubMdn.getSubscriber();
		}

		Pocket objSrcPocket = coreDataWrapper.getPocketById(
				requestFix.getSourcePocketID(), LockMode.UPGRADE);
		Pocket objDestPocket = null;
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubscriber.getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			objDestPocket = coreDataWrapper.getPocketById(
					requestFix.getDestPocketID(), LockMode.UPGRADE);
		} else {
			objDestPocket = coreDataWrapper.getPocketById(requestFix.getDestPocketID());
		}

		boolean isSystemInitiatedTransaction = false;
		if (requestFix.getIsSystemIntiatedTransaction() != null
				&& requestFix.getIsSystemIntiatedTransaction().booleanValue()) {
			isSystemInitiatedTransaction = requestFix
					.getIsSystemIntiatedTransaction().booleanValue();
		}

		log.debug("BankServiceDefaultImpl :: onTransferInquiryToBank objSourceSubscriber="
				+ objSourceSubscriber
				+ ", objDestSubscriber="
				+ objDestSubscriber.getMdnbrand()
				+ ", objSrcSubMdn="
				+ objSrcSubMdn.getMdn()
				+ ", objDestSubMdn="
				+ objDestSubMdn.getMdn()
				+ ", objSrcPocket="
				+ objSrcPocket.getId()
				+ ", objDestPocket="
				+ objDestPocket.getId() + ", amount=" + requestFix.getAmount());

		returnFix = validationService.validateBankAccountSubscriber(
				objSourceSubscriber, objSrcSubMdn, objSrcPocket,
				requestFix.getPin(), true, false, false, false,
				isSystemInitiatedTransaction);

		log.info("BankServiceDefaultImpl :: after validating source subscriber returnFix.getInternalErrorCode()="
				+ returnFix.getInternalErrorCode());

		if (isNullorZero(returnFix.getInternalErrorCode())) {
			returnFix = validationService.validateBankAccountSubscriber(
					objDestSubscriber, objDestSubMdn, objDestPocket, "", false,
					false, false, false, isSystemInitiatedTransaction);

			log.info("BankServiceDefaultImpl :: after validating destination subscriber returnFix.getInternalErrorCode#()="
					+ returnFix.getInternalErrorCode());

			if (isNullorZero(returnFix.getInternalErrorCode())) {

				if (!(objSrcPocket.getId().equals(objDestPocket.getId()))) {

					pct = commodityTransferService.createPCT(requestFix,
							objSourceSubscriber, objDestSubscriber,
							objSrcPocket, objDestPocket, objSrcSubMdn,
							objDestSubMdn, requestFix.getSourceMessage(),
							requestFix.getAmount(), requestFix.getCharges(),
							null, CmFinoFIX.BucketType_Special_Bank_Account,
							CmFinoFIX.BillingType_None,
							CmFinoFIX.TransferStatus_Initialized,
							requestFix.getDestinationBankAccountNo(),
							requestFix.getSourceBankAccountNo());

					BigDecimal charges = requestFix.getCharges();
					if (charges == null)
						charges = BigDecimal.valueOf(0);

					BigDecimal totalTransactionAmount = requestFix.getAmount()
							.add(charges);

					// Skip the Tranasaction amount / count limit validation for
					// Bulk Transfer Transactions
					if (!(CmFinoFIX.TransactionUICategory_Bulk_Transfer
							.equals(pct.getUicategory())
							|| CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer
									.equals(pct.getUicategory()) || CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer
								.equals(pct.getUicategory()))) {

						returnFix = validationService.validateRisksAndLimits(
								objSrcPocket, objDestPocket,
								totalTransactionAmount, requestFix.getAmount(),objSrcSubMdn, objDestSubMdn);
						log.info("After ValidateRisksAndLimits returnFix.getInternalErrorCode()="
								+ returnFix.getInternalErrorCode());
					} else {
						returnFix = validationService
								.validatePocketsForBulkTransfer(objSrcPocket,
										objDestPocket, totalTransactionAmount);
						log.info("After validatePocketsForBulkTransfer returnFix.getInternalErrorCode()="
								+ returnFix.getInternalErrorCode());
					}

					if (isNullorZero(returnFix.getInternalErrorCode())) {
						setPocketLimits(objSrcPocket, totalTransactionAmount);

						if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubscriber.getType()) &&
								!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
							setPocketLimits(objDestPocket, requestFix.getAmount());
						}						
						
						//objSrcPocket.setLastTransactionTime(requestFix.getReceiveTime());
						//objDestPocket.setLastTransactionTime(requestFix.getReceiveTime());
//						if  ((!CmFinoFIX.SubscriberType_Partner.equals(objDestSubMdn.getSubscriber().getType())) &&
//								!(objDestSubMdn.getMDN().equals(dummySubMdn)) ){
//						      objDestSubMdn.setLastTransactionID(requestFix.getTransactionID());
//						      objDestSubMdn.setLastTransactionTime(requestFix.getReceiveTime());
//						 }
						pct.setLocalrevertrequired((short)1);
						if (requestFix.getChannelCode() != null) {
							pct.setIso8583Merchanttype(requestFix
									.getChannelCode());
						} else if (pct.getIso8583Merchanttype() == null) {
							pct.setIso8583Merchanttype(CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Other);
						}


//						if(!CmFinoFIX.SubscriberType_Partner.equals(objSrcSubMdn.getSubscriber().getType())){
//							objSrcSubMdn.setLastTransactionID(requestFix.getTransactionID());
//							objSrcSubMdn.setLastTransactionTime(requestFix.getReceiveTime());
//							coreDataWrapper.save(objSrcSubMdn);
//						}
						
							
							if(ledgerService.isImmediateUpdateRequiredForPocket(objSrcPocket)){
								coreDataWrapper.save(objSrcPocket);
							}
							
							if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubscriber.getType()) &&
									!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
								if(ledgerService.isImmediateUpdateRequiredForPocket(objDestPocket)){
									coreDataWrapper.save(objDestPocket);
								}
							}
					
							coreDataWrapper.save(pct);
					

						if (isNullorZero(returnFix.getInternalErrorCode())) {

							if ((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
									 == CmFinoFIX.PocketType_SVA
									.intValue() || isNFCPocketType(objSrcPocket) || isLakupandaiPocketType(objSrcPocket))
									&& (objDestPocket.getPocketTemplateByPockettemplateid()
											.getType() == CmFinoFIX.PocketType_SVA
											.intValue() || isNFCPocketType(objDestPocket) || isLakupandaiPocketType(objDestPocket))) {
								returnFix.setSourceMDN(objSrcSubMdn.getMdn());
								returnFix
										.setReceiverMDN(objDestSubMdn.getMdn());
								if(requestFix.getUICategory() != null && requestFix.getUICategory().equals(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup)){
									returnFix
									.setInternalErrorCode(NotificationCodes.NFCPocketTopupInquirySuccess
											.getInternalErrorCode());
								}else{
									returnFix
									.setInternalErrorCode(NotificationCodes.BankAccountToBankAccountConfirmationPrompt
											.getInternalErrorCode());
								}
								
								returnFix.setAmount(requestFix.getAmount());
								returnFix.setCharges(requestFix.getCharges());
								returnFix.setLanguage((int)objSourceSubscriber
										.getLanguage());
								returnFix.setMessageType(requestFix
										.getMessageType());
								returnFix
										.setResult(CmFinoFIX.ResponseCode_Success);
								returnFix.setTransferID(pct.getId().longValue());
								returnFix.setTransactionID(pct.getId().longValue());
								returnFix.setReceiverAccountNo(objDestPocket
										.getCardpan());
								returnFix
										.setReceiverName(safeString(objDestSubscriber
												.getFirstname())
												+ safeString(objDestSubscriber
														.getLastname()));
								returnFix.setCurrency(objSourceSubscriber
										.getCurrency());
								returnFix.setParentTransactionID(pct
										.getTransactionLog()
										.getId().longValue());
								returnFix.setSourcePocketId(objSrcPocket
										.getId().longValue());
								returnFix
										.setDestPocketId(objDestPocket.getId().longValue());
								returnFix.setRemarks(requestFix.getRemarks());

								pct.setTransferstatus(CmFinoFIX.TransferStatus_ConfirmationPromptSentToSubscriber);
								pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_Inititalized));
								pct.setNotificationcode(Long.valueOf(NotificationCodes
										.getNotificationCodeFromInternalCode(returnFix
												.getInternalErrorCode())));
								coreDataWrapper.save(pct);
							} else {
								CMTransferInquiryToBank inquiryToBank = new CMTransferInquiryToBank();

								inquiryToBank.copy(requestFix);
                                inquiryToBank.setSourceMDN(requestFix.getSourceMDN());
								inquiryToBank.setAmount(totalTransactionAmount);
								inquiryToBank.setBankCode(pct.getBankcode().intValue());
								inquiryToBank.setDestMDN(pct.getDestmdn());
								inquiryToBank.setLanguage((int)objSourceSubscriber.getLanguage());
								inquiryToBank.setRemarks(requestFix.getRemarks());
								inquiryToBank.setParentTransactionID(pct
										.getTransactionLog()
										.getId().longValue());
								inquiryToBank
										.setUICategory(pct.getUicategory().intValue());
								inquiryToBank.setSourcePocketID(pct
										.getPocket().getId().longValue());
								inquiryToBank.setDestPocketID(pct
										.getDestpocketid().longValue());

								inquiryToBank.setSourceCardPAN(pct
										.getSourcecardpan());
								inquiryToBank.setDestCardPAN(pct
										.getDestcardpan());
								inquiryToBank
										.setDestinationBankAccountNo(requestFix
												.getDestinationBankAccountNo());

								inquiryToBank.setTransferID(pct.getId().longValue());
								inquiryToBank.setTransactionID(pct
										.getTransactionLog()
										.getId().longValue());
								inquiryToBank.setParentTransactionID(pct
										.getTransactionLog()
										.getId().longValue());

								if (CmFinoFIX.BankAccountCardType_SavingsAccount
										.equals(objSrcPocket
												.getPocketTemplateByPockettemplateid()
												.getBankaccountcardtype())) {
									inquiryToBank.setSourceBankAccountType(""
											+ CmFinoFIX.BankAccountType_Saving);
								} else {
									inquiryToBank
											.setSourceBankAccountType(""
													+ CmFinoFIX.BankAccountType_Checking);
								}

								if (CmFinoFIX.BankAccountCardType_SavingsAccount
										.equals(objDestPocket
												.getPocketTemplateByPockettemplateid()
												.getBankaccountcardtype())) {
									inquiryToBank
											.setDestinationBankAccountType(""
													+ CmFinoFIX.BankAccountType_Saving);
								} else {
									inquiryToBank
											.setDestinationBankAccountType(""
													+ CmFinoFIX.BankAccountType_Checking);
								}

								if (CmFinoFIX.PocketType_SVA
										.equals(objSrcPocket
												.getPocketTemplateByPockettemplateid().getType()) || isLakupandaiPocketType(objSrcPocket)) {
									inquiryToBank
											.setSourceCardPAN(coreDataWrapper
													.getGlobalAccountNumber());
									inquiryToBank
											.setSourceBankAccountType(""
													+ CmFinoFIX.BankAccountType_Lakupandai);
								}

								if (CmFinoFIX.PocketType_SVA
										.equals(objDestPocket
												.getPocketTemplateByPockettemplateid().getType()) || isLakupandaiPocketType(objDestPocket)) {
									inquiryToBank
											.setDestCardPAN(coreDataWrapper
													.getGlobalAccountNumber());
									inquiryToBank
											.setDestinationBankAccountType(""
													+ CmFinoFIX.BankAccountType_Lakupandai);
								}

								// this part is not required as we dont have pin
								if (objSrcPocket.getPocketTemplateByPockettemplateid().getType()
										 == CmFinoFIX.PocketType_BankAccount
										.intValue()) {
									inquiryToBank.setPin(requestFix.getPin());
								} else {
									inquiryToBank
											.setPin(MCEUtil.FAKE_PIN_FOR_OMB);

								}

								inquiryToBank.setTransferID(pct.getId().longValue());

								isoFix = inquiryToBank;
							}
						}
					} else {
						pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountToBankAccountSourcePocketLimits));
						pct.setNotificationcode(Long.valueOf(NotificationCodes
								.getNotificationCodeFromInternalCode(returnFix
										.getInternalErrorCode())));
						pct.setEndtime(pct.getStarttime());
						pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
						pct.setLocalrevertrequired((short)0);
						pct.setLocalbalancerevertrequired((short)0);
						handlePCTonFailure(pct);
					}
				} else {
					returnFix
							.setInternalErrorCode(NotificationCodes.EMoneytoEMoneyFailed_SelfTransfer
									.getInternalErrorCode());
				}
			}
		}

		log.info("objSourceSubscriber.getCompany().getCustomerServiceNumber()="
				+ objSourceSubscriber.getCompany().getCustomerservicenumber());
		returnFix.setCustomerServiceShortCode(objSourceSubscriber.getCompany()
				.getCustomerservicenumber());

		if (pct == null) {
			returnFix.setTransferID(requestFix.getTransactionID());
		}
		returnFix.setTransactionID(requestFix.getTransactionID());

		activitiesLog
				.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountBalanceInquiryPending
						.getNotificationCode()));
		// write Activity record
		activitiesLog.setMsgtype(Long.valueOf(returnFix.getMessageType()));
		activitiesLog.setParenttransactionid(new BigDecimal(requestFix
				.getParentTransactionID()));
		activitiesLog.setServletpath(requestFix.getServletPath());
		activitiesLog.setSourceapplication(Long.valueOf(requestFix.getSourceApplication()));
		activitiesLog.setSourcemdn(objSrcSubMdn.getMdn());
		activitiesLog.setSourcemdnid(objSrcSubMdn.getId());
		activitiesLog.setSourcesubscriberid(objSrcSubMdn.getId());
		// activitiesLog.setCompany(objSourceSubscriber.getCompany().getID());
		activitiesLog.setSourcepocketid(objSrcPocket.getId());
		activitiesLog.setSourcepockettype(objSrcPocket.getPocketTemplateByPockettemplateid()
				.getType());
		if (pct != null) {
			activitiesLog.setTransferid(pct.getId());
		} else {
			activitiesLog.setTransferid(requestFix.getTransactionID());
		}
		activitiesLog.setCompany(objSourceSubscriber.getCompany());
		if (objSrcPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(objSrcPocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}
// [Bala] Commented the Activities log creation
//		coreDataWrapper.save(activitiesLog);

		if (isoFix != null) {
			return isoFix;
		}
		returnFix.setServiceChargeTransactionLogID(requestFix
				.getServiceChargeTransactionLogID());
		returnFix.setSourceMDN(requestFix.getSourceMDN());
		return returnFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onBalanceInquiryFromBank(CMBalanceInquiryToBank toBank,
			CMBalanceInquiryFromBank fromBank) {
		log.info("BankServiceDefaultImpl::onBalanceInquiryFromBank() Begin");

		// TODO delete
		log.info("toBank.getParentTransactionId()="
				+ toBank.getParentTransactionID());

		BackendResponse returnFix = createResponseObject();

		Subscriber subscriber = coreDataWrapper.getSubscriberByMdn(
				toBank.getSourceMDN());
//		SubscriberMDN subscriberMdn = coreDataWrapper.getSubscriberMdn(
//				toBank.getSourceMDN(), LockMode.UPGRADE);

		Pocket toBankPocket = coreDataWrapper.getPocketById(
				toBank.getPocketID());

		returnFix.setSourceMDN(toBank.getSourceMDN());
		returnFix.setLanguage((int)subscriber.getLanguage());
// [Bala] As no need to do subscriber validation again in Check balance .		
//		if(!toBank.getTransactionTypeName().equals(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_BALANCE))
//		{
//			returnFix = validationService.validateBankAccountSubscriber(subscriber,
//					subscriberMdn, toBankPocket, toBank.getPin(), true, false,
//					false, false);
//
//			log.info("BankServiceDefaultServiceImpl : onBalanceInquiryFromBank : validateBankAccountSubscriber "
//					+ returnFix.getInternalErrorCode());
//
//			if (!isNullorZero(returnFix.getInternalErrorCode())) {
//				return returnFix;
//			}
//		}

// [Bala] Commented the Activities log creation
//		ActivitiesLog activitiesLog = coreDataWrapper
//				.getActivitiesLogByParentTransactionId(toBank
//						.getParentTransactionID());

		if ((toBankPocket != null)
				&& (!isNullOrEmpty(toBankPocket.getCardpan()))
				&& !((toBank.getSourceCardPAN().equals(toBankPocket
						.getCardpan())))) {
			// error only log
			log.info("BankServiceDefaultImpl : onBalanceInquiryFromBank : account number mismatch");
			returnFix
					.setInternalErrorCode(NotificationCodes.InternalSystemError
							.getInternalErrorCode());
			returnFix
					.setDescription("OnBalanceInquiryFromBank - Response Account number does not match default pocket details");
			return returnFix;
		} else {
			log.info("BankServiceDefaultImpl : onBalanceInquiryFromBank : Response from bank="
					+ fromBank.getResponseCode());
			if (!isNullOrEmpty(fromBank.getResponseCode())
					&& "00".equals(fromBank.getResponseCode())) {

				CGEntries[] entries = fromBank.getEntries();
				BigDecimal amount = null;

				if(entries != null)
				{
					if (entries.length == 2) {
						amount = entries[1].getAmount();
					} else {
						amount = entries[0].getAmount();
					}
				}

				// set in the fix response and return
				returnFix.setInternalErrorCode(NotificationCodes.BankAccountBalanceDetails.getInternalErrorCode());
				if (CmFinoFIX.SourceApplication_Web.equals(toBank.getSourceApplication())) {
					returnFix.setInternalErrorCode(NotificationCodes.GetAvialableBalance.getInternalErrorCode());
				}
				returnFix.setAmount(amount);
				returnFix.setCurrency(subscriber.getCurrency());
				returnFix.setResult(CmFinoFIX.ResponseCode_Success);
				returnFix.setNfcCardBalanceCGEntries(entries);
//				activitiesLog.setIsSuccessful(Boolean.TRUE);
//				activitiesLog.setNotificationCode(NotificationCodes.BalanceInquiryCompleted.getNotificationCode());

			} else {
				ResponseCodes rs = ResponseCodes.getResponseCodes(1,
						fromBank.getResponseCode());
				if (rs == ResponseCodes.bank_Failure) {
					returnFix.setDescription(ExternalResponseCodeHolder
							.getNotificationText(fromBank.getResponseCode()));
				}
				returnFix.setExternalResponseCode(rs.getExternalResponseCode());
				returnFix.setResult(CmFinoFIX.ResponseCode_Failure);

//				activitiesLog.setIsSuccessful(Boolean.FALSE);
//				activitiesLog.setNotificationCode(rs.getInternalErrorCode());
			}
		}

//		coreDataWrapper.save(activitiesLog);

		returnFix.setSourceMDN(toBank.getSourceMDN());
		returnFix.setLanguage((int)subscriber.getLanguage());
		returnFix.setServiceChargeTransactionLogID(toBank
				.getServiceChargeTransactionLogID());
		return returnFix;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CFIXMsg onGetSubscriberDetailsFromBank(CMGetSubscriberDetailsToBank toBank, CMGetSubscriberDetailsFromBank fromBank){
		log.info("BankServiceDefaultImpl::onGetSubscriberDetailsFromBank() Begin");

		BackendResponse returnFix = createResponseObject();

		log.info("BankServiceDefaultImpl : onGetSubscriberDetailsFromBank : Response from bank="
				+ fromBank.getResponseCode());
		if (!isNullOrEmpty(fromBank.getResponseCode())
				&& "00".equals(fromBank.getResponseCode())) {

			// set in the fix response and return
			returnFix.setInternalErrorCode(NotificationCodes.SubscriberDetailsSuccessMessage.getInternalErrorCode());
			returnFix.setResult(CmFinoFIX.ResponseCode_Success);
			returnFix.setFirstName(fromBank.getFirstName());
			returnFix.setLastName(fromBank.getLastName());
			returnFix.setAdditionalInfo(fromBank.getEmail());// Storing Email ID in Additional Info
			
		} else {			
			ResponseCodes rs = ResponseCodes.getResponseCodes(1,
					fromBank.getResponseCode());
			if (rs == ResponseCodes.bank_Failure) {
				returnFix.setDescription(ExternalResponseCodeHolder
						.getNotificationText(fromBank.getResponseCode()));
			}
			returnFix.setExternalResponseCode(rs.getExternalResponseCode());
			returnFix.setResult(CmFinoFIX.ResponseCode_Failure);
		}

		return returnFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onNewSubscriberActivation(
			CMNewSubscriberActivation requestFix) {
		log.info("BankServiceDefaultImpl :: onNewSubscriberActivation() Begin");
		CFIXMsg isoFix = null;
		BackendResponse responseFix = createResponseObject();
		responseFix.copy(requestFix);
		
		// TODO: implement ISO communication with bank
		CMNewSubscriberActivationToBank toBankFix = new CMNewSubscriberActivationToBank();
		toBankFix.copy(requestFix);
		String mdn = StringUtilities.leftPadWithCharacter(requestFix.getSourceMDN(), 13, "0");
		String mdngen = MfinoUtil.CheckDigitCalculation(mdn);
	    toBankFix.setSourceMDN(requestFix.getSourceMDN());
	    toBankFix.setSourceCardPAN(requestFix.getSourceCardPAN());
	    toBankFix.setInfo2(mdngen);
		log.info("BankServiceDefaultIpl : onNewSubscriberActivation with transactionid="
				+ requestFix.getTransactionID());
		toBankFix.setTransactionID(requestFix.getTransactionID());
		toBankFix.setParentTransactionID(requestFix.getParentTransactionID());
		toBankFix.setBankCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);
		isoFix = toBankFix;

		responseFix.setSourceMDN(requestFix.getSourceMDN());
		responseFix.setTransactionID(requestFix.getTransactionID());

		responseFix.setServiceChargeTransactionLogID(requestFix
				.getServiceChargeTransactionLogID());

		return isoFix;

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onNewSubscriberActivationFromBank(
			CMNewSubscriberActivationToBank toBank,
			CMNewSubscriberActivationFromBank fromBank) {
		log.info("BankServiceDefaultImpl::onNewSubscriberActivationFromBank::Begin");
		BackendResponse returnFix = createResponseObject();
		log.info("BankServiceDefaultImpl : onNewSubscriberActivationFromBank  : Response from bank="
				+ fromBank.getResponseCode());
		// ActivitiesLog activitiesLog =
		// coreDataWrapper.getActivitiesLogByParentTransactionId(toBank.getParentTransactionID());
		if (!isNullOrEmpty(fromBank.getResponseCode())
				&& ResponseCodes.ISO_ResponseCode_Success
						.getExternalResponseCode().equals(
								fromBank.getResponseCode())) {
			returnFix
					.setInternalErrorCode(NotificationCodes.NewSubscriberActivation
							.getInternalErrorCode());
			log.info("BankServiceDefaultImpl : onNewSubscriberActivationFromBank  : Account number="
					+ fromBank.getAccountNumber());
			returnFix.setSourceCardPAN(fromBank.getAccountNumber());
			// activitiesLog.setIsSuccessful(Boolean.TRUE);
			// activitiesLog.setNotificationCode(NotificationCodes.BankAccountTransactionDetails.getNotificationCode());
			// returnFix.setExternalResponseCode(ResponseCodes.ISO_ResponseCode_Success.getExternalResponseCode());
			returnFix.setResult(CmFinoFIX.ResponseCode_Success);
			log.info("BankServiceDefaultImpl : onNewSubscriberActivationFromBank  : Response from bank Response Code="
					+ ResponseCodes.ISO_ResponseCode_Success
							.getExternalResponseCode());
		} else {
			ResponseCodes rs = ResponseCodes.getResponseCodes(1,
					fromBank.getResponseCode());
			if (rs == ResponseCodes.bank_Failure) {
				returnFix.setDescription(ExternalResponseCodeHolder
						.getNotificationText(fromBank.getResponseCode()));
			}
			returnFix.setExternalResponseCode(rs.getExternalResponseCode());
			returnFix.setResult(CmFinoFIX.ResponseCode_Failure);

			// activitiesLog.setIsSuccessful(Boolean.FALSE);
			// activitiesLog.setNotificationCode(rs.getInternalErrorCode());
		}

		// coreDataWrapper.save(activitiesLog);

		returnFix.setSourceMDN(toBank.getSourceMDN());
		// returnFix.setLanguage(subscriber.getLanguage());
		return returnFix;
	}

	public CFIXMsg onExistingSubscriberReactivationFromBank(CMExistingSubscriberReactivationToBank toBank, CMExistingSubscriberReactivationFromBank fromBank){
				log.info("BankServiceDefaultImpl::onNewSubscriberActivationFromBank::Begin");
				BackendResponse returnFix = createResponseObject();
				Subscriber subscriber = coreDataWrapper.getSubscriberByMdn(toBank.getSourceMDN(),LockMode.UPGRADE);
				log.info("BankServiceDefaultImpl : onExistingSubscriberReactivationFromBank  : Response from bank="+fromBank.getResponseCode());
			    ActivitiesLog activitiesLog = coreDataWrapper.getActivitiesLogByParentTransactionId(toBank.getParentTransactionID());
			   // returnFix=validationService.validateSubscriber(subscriber, subscriberMdn, true, false, false, false, true);
			   // log.info("BankServiceDefaultImpl : onExistingSubscriberReactivationFromBank  : Response from bank="+fromBank.getResponseCode());
				if(!isNullOrEmpty(fromBank.getResponseCode()) && ResponseCodes.ISO_ResponseCode_Success.getExternalResponseCode().equals(fromBank.getResponseCode())){
		            returnFix.setInternalErrorCode(NotificationCodes.NewSubscriberActivation.getInternalErrorCode());
		            log.info("BankServiceDefaultImpl : onNewSubscriberActivationFromBank  : Account number="+fromBank.getAccountNumber());
					returnFix.setSourceCardPAN(fromBank.getAccountNumber());
					activitiesLog.setIssuccessful((short)1);
					activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes.Success.getNotificationCode()));
					returnFix.setResult(CmFinoFIX.ResolveAs_success);
				}
				else{
					ResponseCodes rs  = ResponseCodes.getResponseCodes(1,fromBank.getResponseCode());
					if(rs == ResponseCodes.bank_Failure){
						
						returnFix.setDescription(ExternalResponseCodeHolder.getNotificationText(fromBank.getResponseCode()));
					}
					returnFix.setExternalResponseCode(rs.getExternalResponseCode());
					returnFix.setResult(CmFinoFIX.ResponseCode_Failure);
					
					activitiesLog.setIssuccessful((short)1);
					activitiesLog.setNotificationcode(Long.valueOf(rs.getInternalErrorCode()));
				}
		
				coreDataWrapper.save(activitiesLog);
		
				returnFix.setSourceMDN(toBank.getSourceMDN());
				returnFix.setLanguage((int)subscriber.getLanguage());
		
				return returnFix;
		    }

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onExistingSubscriberReactivation(
			CMExistingSubscriberReactivation requestFix) {
		log.info("BankServiceDefaultImpl :: onExistingSubscriberReactivation() Begin");
		CFIXMsg isoFix = null;
		BackendResponse responseFix = createResponseObject();
		responseFix.copy(requestFix);

		ActivitiesLog activitiesLog = new ActivitiesLog();
		Subscriber subscriber = coreDataWrapper.getSubscriberByMdn(
				requestFix.getSourceMDN(), LockMode.UPGRADE);
		SubscriberMdn subscriberMdn = coreDataWrapper.getSubscriberMdn(
				requestFix.getSourceMDN(), LockMode.UPGRADE);
		// Pocket moneyPocket =
		// coreDataWrapper.getPocketById(requestFix.getPocketID());
		// TODO: implement ISO communication with bank
		Pocket objSrcPocket = coreDataWrapper.getPocketById(
				requestFix.getSourcePocketID(), LockMode.UPGRADE);
		CMExistingSubscriberReactivationToBank toBankFix = new CMExistingSubscriberReactivationToBank();
		if (CmFinoFIX.BankAccountCardType_SavingsAccount
				.equals(objSrcPocket
						.getPocketTemplateByPockettemplateid()
						.getBankaccountcardtype())) {
			toBankFix.setSourceBankAccountType(""
							+ CmFinoFIX.BankAccountType_Saving);
		} else {
			toBankFix.setSourceBankAccountType(""
							+ CmFinoFIX.BankAccountType_Checking);
		}
		toBankFix.copy(requestFix);
		String mdn = StringUtilities.leftPadWithCharacter(requestFix.getSourceMDN(), 13, "0");
		String mdngen = MfinoUtil.CheckDigitCalculation(mdn);
	    toBankFix.setSourceMDN(requestFix.getSourceMDN());
	    toBankFix.setInfo2(mdngen);
	    toBankFix.setPin(requestFix.getPin());
		log.info("BankServiceDefaultIpl : onExistingSubscriberReactivation with transactionid="
				+ requestFix.getTransactionID());
		toBankFix.setTransactionID(requestFix.getTransactionID());
		toBankFix.setSourceCardPAN(requestFix.getSourceCardPAN());
		toBankFix.setBankCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);
		toBankFix.setParentTransactionID(requestFix.getParentTransactionID());
		isoFix = toBankFix;
		
		responseFix.setSourceMDN(requestFix.getSourceMDN());
		responseFix.setLanguage((int)subscriber.getLanguage());
		responseFix.setCustomerServiceShortCode(subscriber.getCompany()
				.getCustomerservicenumber());
		responseFix.setCurrency(subscriber.getCurrency());
		responseFix.setTransactionID(requestFix.getTransactionID());

		// write Activity record
		activitiesLog.setMsgtype(Long.valueOf(responseFix.getMessageType()));
		activitiesLog.setParenttransactionid(new BigDecimal(requestFix
				.getParentTransactionID()));
		activitiesLog.setServletpath(requestFix.getServletPath());
		activitiesLog.setSourceapplication(Long.valueOf(requestFix.getSourceApplication()));
		activitiesLog.setSourcemdn(subscriberMdn.getMdn());
		activitiesLog.setSourcemdnid(subscriberMdn.getId());
		activitiesLog.setSourcesubscriberid(subscriber.getId());
		activitiesLog.setSourcesubscriberid(subscriber.getCompany().getId());
		activitiesLog.setCompany(subscriber.getCompany());

		coreDataWrapper.save(activitiesLog);

		responseFix.setServiceChargeTransactionLogID(requestFix
				.getServiceChargeTransactionLogID());
		return isoFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onBalanceInquiry(CMBankAccountBalanceInquiry requestFix) {
		log.info("BankServiceDefaultImpl::onBalanceInquiry() Begin");

		CFIXMsg isoFix = null;
		BackendResponse responseFix = createResponseObject();
		responseFix.copy(requestFix);

		ActivitiesLog activitiesLog = new ActivitiesLog();
		Subscriber subscriber = null;
		SubscriberMdn subscriberMdn = coreDataWrapper.getSubscriberMdn(
				requestFix.getSourceMDN(), LockMode.UPGRADE);
		if (subscriberMdn != null) {
			subscriber = subscriberMdn.getSubscriber();
		}

		Pocket moneyPocket = coreDataWrapper.getPocketById(requestFix
				.getPocketID());

		boolean isSystemInitiatedTransaction = false;
		if (requestFix.getIsSystemIntiatedTransaction() != null
				&& requestFix.getIsSystemIntiatedTransaction().booleanValue()) {
			isSystemInitiatedTransaction = requestFix
					.getIsSystemIntiatedTransaction().booleanValue();
		}
		/*
		 * In case of NFC Card Balance, CardPan can be null and hence this pocket can be null.
		 */
		if(moneyPocket != null)
		{
			responseFix = validationService.validateBankAccountSubscriber(
					subscriber, subscriberMdn, moneyPocket, requestFix.getPin(),
					true, false, false, false, isSystemInitiatedTransaction);

			log.info("BankServiceDefaultImpl:onBalanceInquiry(): Source subscriber validation ErrorCode="
					+ responseFix.getInternalErrorCode());
			log.debug("BankServiceDefaultImpl:onBalanceInquiry(): Source Subscriber Pocket Type="
					+ moneyPocket.getPocketTemplateByPockettemplateid().getType());
		}
		else
		{
			responseFix = validationService.validateSubscriberPin(subscriber, subscriberMdn, requestFix.getPin(), true, isSystemInitiatedTransaction);
			log.info("BankServiceDefaultImpl:onBalanceInquiry(): Source subscriber pin validation ErrorCode="
					+ responseFix.getInternalErrorCode());
		}
		if (isNullorZero(responseFix.getInternalErrorCode())) {
			// NFC Card Balance
			if (/*moneyPocket.getPocketTemplateByPockettemplateid().getType().equals(CmFinoFIX.PocketType_NFC) 
					&& */ requestFix.getTransactionTypeName().equals(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_BALANCE)) {
				CMBalanceInquiryToBank toBankFix = new CMBalanceInquiryToBank();
				toBankFix.copy(requestFix);
				toBankFix.setSourceMDN(subscriberMdn.getMdn());
				toBankFix.setLanguage((int)subscriber.getLanguage());
				toBankFix.setPin(requestFix.getPin());
				toBankFix.setTransactionTypeName(requestFix.getTransactionTypeName());
				if(moneyPocket != null)
				{
					toBankFix.setBankCode(moneyPocket.getPocketTemplateByPockettemplateid().getBankcode().intValue());
					toBankFix.setSourceCardPAN(moneyPocket.getCardpan());
					toBankFix.setCardAlias(moneyPocket.getCardalias());
				}
				else
				{			
					List<PocketTemplate> nfcPocketTemplates = DAOFactory.getInstance().getPocketTemplateDao().getByPocketType(CmFinoFIX.PocketType_NFC);
					if(nfcPocketTemplates != null && nfcPocketTemplates.size() > 0)
					{
						PocketTemplate nfcPocketTemplate = nfcPocketTemplates.iterator().next();
						toBankFix.setBankCode(nfcPocketTemplate.getBankcode().intValue());
					}
				}
				toBankFix.setPocketID(requestFix.getPocketID());
				toBankFix.setTransactionID(requestFix.getTransactionID());
				toBankFix.setParentTransactionID(requestFix.getParentTransactionID());
				isoFix = toBankFix;
				activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountBalanceInquiryPending.getNotificationCode()));

				log.info("NFC Card Balance: toBankFix.getParentTransactionId()=" + toBankFix.getParentTransactionID());
			}

			else if (moneyPocket.getPocketTemplateByPockettemplateid().getType()
					==(CmFinoFIX.PocketType_BankAccount)) {
				// Bank pocket
				CMBalanceInquiryToBank toBankFix = new CMBalanceInquiryToBank();
				toBankFix.copy(requestFix);
				toBankFix.setSourceMDN(subscriberMdn.getMdn());
				toBankFix.setBankCode(moneyPocket.getPocketTemplateByPockettemplateid()
						.getBankcode().intValue());
				toBankFix.setLanguage((int)subscriber.getLanguage());
				toBankFix.setPin(requestFix.getPin());
				toBankFix.setTransactionTypeName(requestFix.getTransactionTypeName());
				toBankFix.setSourceCardPAN(moneyPocket.getCardpan());
				toBankFix.setPocketID(requestFix.getPocketID());
				log.info("BankServiceDefaultIpl : balanceinquiry :* transactionid="
						+ requestFix.getTransactionID());
				toBankFix.setTransactionID(requestFix.getTransactionID());

				if (moneyPocket.getPocketTemplateByPockettemplateid().getBankaccountcardtype()
						.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)) {
					toBankFix.setSourceBankAccountType(""
							+ CmFinoFIX.BankAccountType_Saving);
				} else {
					toBankFix.setSourceBankAccountType(""
							+ CmFinoFIX.BankAccountType_Checking);
				}

				if (CmFinoFIX.PocketType_SVA.equals(moneyPocket
						.getPocketTemplateByPockettemplateid().getType())) {
					toBankFix.setSourceCardPAN(coreDataWrapper
							.getGlobalAccountNumber());
					toBankFix.setSourceBankAccountType(""
							+ CmFinoFIX.BankAccountType_Checking);
				}

				toBankFix.setParentTransactionID(requestFix
						.getParentTransactionID());
				isoFix = toBankFix;
				activitiesLog
						.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountBalanceInquiryPending
								.getNotificationCode()));

				log.info("toBankFix.getParentTransactionId()="
						+ toBankFix.getParentTransactionID());

			} else {
				// E-money pocket or NFC Pocket
				responseFix
						.setInternalErrorCode(NotificationCodes.E_Money_CheckBalance
								.getInternalErrorCode());
				if (CmFinoFIX.SourceApplication_Web.equals(requestFix
						.getSourceApplication())) {
					responseFix
							.setInternalErrorCode(NotificationCodes.GetAvialableBalance
									.getInternalErrorCode());
				}
				responseFix.setSourceMDNBalance(moneyService.round(new BigDecimal(moneyPocket
						.getCurrentbalance())));
				responseFix
						.setAmount(moneyService.round(new BigDecimal(moneyPocket.getCurrentbalance())));
				responseFix.setResult(CmFinoFIX.ResponseCode_Success);

				activitiesLog
						.setNotificationcode(Long.valueOf(NotificationCodes.E_Money_CheckBalance
								.getNotificationCode()));
				if (CmFinoFIX.PocketType_NFC.equals(moneyPocket.getPocketTemplateByPockettemplateid().getType())) {
					responseFix.setInternalErrorCode(NotificationCodes.NFC_Pocket_Balance.getInternalErrorCode());
					activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes.NFC_Pocket_Balance.getNotificationCode()));
				}
			}
		} else {
			activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes
					.getNotificationCodeFromInternalCode(responseFix
							.getInternalErrorCode())));
		}

		log.info("BankServiceDefaultImpl:onBalanceInquiry() **");

		responseFix.setSourceMDN(requestFix.getSourceMDN());
		responseFix.setLanguage((int)subscriber.getLanguage());
		responseFix.setCustomerServiceShortCode(subscriber.getCompany()
				.getCustomerservicenumber());
		responseFix.setCurrency(subscriber.getCurrency());
		responseFix.setTransactionID(requestFix.getTransactionID());

		// write Activity record
		activitiesLog.setMsgtype(null!=responseFix.getMessageType()?Long.valueOf(responseFix.getMessageType()):null);
		activitiesLog.setParenttransactionid(new BigDecimal(requestFix
				.getParentTransactionID()));
		activitiesLog.setServletpath(requestFix.getServletPath());
		activitiesLog.setSourceapplication(null!=requestFix.getSourceApplication()?Long.valueOf(requestFix.getSourceApplication()):null);
		activitiesLog.setSourcemdn(subscriberMdn.getMdn());
		activitiesLog.setSourcemdnid(subscriberMdn.getId());
		activitiesLog.setSourcesubscriberid(subscriber.getId());
		activitiesLog.setSourcesubscriberid(subscriber.getCompany().getId());
		if(moneyPocket != null)
		{
			activitiesLog.setSourcepocketid(moneyPocket.getId());
			activitiesLog.setSourcepockettype(moneyPocket.getPocketTemplateByPockettemplateid().getType());
			if (moneyPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
				activitiesLog.setIso8583Acquiringinstidcode(moneyPocket.getPocketTemplateByPockettemplateid().getBankcode());
			}
		}
		activitiesLog.setCompany(subscriber.getCompany());		

		log.debug("before save activities log BankServiceDefaultImpl:onBalanceInquiry():: isoFix==null="
				+ (isoFix == null));

// [Bala] Commented the Activities log creation
//		coreDataWrapper.save(activitiesLog);

		log.debug("BankServiceDefaultImpl:onBalanceInquiry():: isoFix==null="
				+ (isoFix == null));
//		log.info("activitiesLog.getParentTransactionId()="
//				+ activitiesLog.getParentTransactionID());

		if (isoFix != null) {
			return isoFix;
		}
		responseFix.setServiceChargeTransactionLogID(requestFix
				.getServiceChargeTransactionLogID());
		return responseFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onTransferConfirmationToBank(
			CMBankAccountToBankAccountConfirmation confirmationToBank) {

		log.info("BankServiceDefaultImpl : onTransferConfirmationToBank begin");

		CFIXMsg isoFix = null;
		BackendResponse returnFix = createResponseObject();

		ActivitiesLog activitiesLog = new ActivitiesLog();
		PendingCommodityTransfer pct = null;

		Subscriber objSourceSubscriber = null;
		Subscriber objDestSubscriber = null;
		SystemParameters dummySubMdnParam = coreDataWrapper.getSystemParameterByName(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
		String dummySubMdn = (dummySubMdnParam != null) ? dummySubMdnParam.getParametervalue() : null;
		SubscriberMdn objSrcSubMdn = coreDataWrapper.getSubscriberMdn(confirmationToBank.getSourceMDN());
		SubscriberMdn objDestSubMdn = coreDataWrapper.getSubscriberMdn(confirmationToBank.getDestMDN());
		if (objSrcSubMdn != null) {
			objSourceSubscriber = objSrcSubMdn.getSubscriber();
		}
		if (objDestSubMdn != null) {
			objDestSubscriber = objDestSubMdn.getSubscriber();
		}

		Pocket objSrcPocket = coreDataWrapper.getPocketById(
				confirmationToBank.getSourcePocketID(), LockMode.UPGRADE);
		Pocket objDestPocket = null;
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubscriber.getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			objDestPocket = coreDataWrapper.getPocketById(
					confirmationToBank.getDestPocketID(), LockMode.UPGRADE);
		} else {
			objDestPocket = coreDataWrapper.getPocketById(confirmationToBank.getDestPocketID());
		}		
				
//		Pocket suspensePocket = coreDataWrapper.getSuspensePocketWithLock();
//		Pocket chargesPocket = coreDataWrapper.getChargesPocketWithLock();
//		Pocket globalSVAPocket = coreDataWrapper.getGlobalSVAPocketWithLock();
        
		boolean isSystemInitiatedTransaction = false;
		if (confirmationToBank.getIsSystemIntiatedTransaction() != null
				&& confirmationToBank.getIsSystemIntiatedTransaction()
						.booleanValue()) {
			isSystemInitiatedTransaction = confirmationToBank
					.getIsSystemIntiatedTransaction().booleanValue();
		}

//		if (null == suspensePocket.getCurrentBalance()) {
//			suspensePocket.setCurrentBalance(BigDecimal.ZERO);
//		}
//		if (null == chargesPocket.getCurrentBalance()) {
//			chargesPocket.setCurrentBalance(BigDecimal.ZERO);
//		}

		log.debug("BankServiceDefaultImpl :: onTransferConfirmationToBank objSourceSubscriber="
				+ objSourceSubscriber
				+ ", objDestSubscriber="
				+ objDestSubscriber.getMdnbrand()
				+ ", objSrcSubMdn="
				+ objSrcSubMdn.getMdn()
				+ ", objDestSubMdn="
				+ objDestSubMdn.getMdn()
				+ ", objSrcPocket="
				+ objSrcPocket.getId()
				+ ", objDestPocket="
				+ objDestPocket.getId());

		returnFix = validationService.validateBankAccountSubscriber(
				objSourceSubscriber, objSrcSubMdn, objSrcPocket, "mFino260",
				true, false, false, false, isSystemInitiatedTransaction);

		log.info("BankServiceDefaultImpl:onTransferConfirmationToBank: Source subcriber validated ErrorCode="
				+ returnFix.getInternalErrorCode());

		if (isNullorZero(returnFix.getInternalErrorCode())) {
			returnFix = validationService.validateBankAccountSubscriber(
					objDestSubscriber, objDestSubMdn, objDestPocket, "", false,
					false, false, false, isSystemInitiatedTransaction);

			log.info("BankServiceDefaultImpl:onTransferConfirmationToBank: Dest subcriber validated ErrorCode="
					+ returnFix.getInternalErrorCode());

			if (isNullorZero(returnFix.getInternalErrorCode())) {
				pct = coreDataWrapper.getPCTById(confirmationToBank
						.getTransferID());

				if (pct != null) {
					// Skip validate pct in case of charge reversal or cash in
					// to agent
					if (!(CmFinoFIX.TransactionUICategory_Reverse_Charge
							.equals(pct.getUicategory()) || CmFinoFIX.TransactionUICategory_Cash_In_To_Agent
							.equals(pct.getUicategory()))) {
						// Skip the Tranasaction amount / count limit validation
						// for Bulk Transfer Transactions
						if (!(CmFinoFIX.TransactionUICategory_Bulk_Transfer
								.equals(pct.getUicategory())
								|| CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer
										.equals(pct.getUicategory()) || CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer
									.equals(pct.getUicategory()))) {
							returnFix = validationService.validatePct(pct,
									objSrcPocket, objDestPocket, objSrcSubMdn,
									objDestSubMdn);
							log.info("after validatePct in transfer confirmation returnFix.getInternalErrorCode() --> "
									+ returnFix.getInternalErrorCode());
						} else {
							returnFix = validationService
									.validatePctForSourcePocketBalance(pct,
											objSrcPocket);
							log.info("after validatePctForSourcePocketBalance in transfer confirmation returnFix.getInternalErrorCode() --> "
									+ returnFix.getInternalErrorCode());
						}
					} else {
						// no validations on source pocket so, seting source
						// pocket as null.
						returnFix = validationService.validatePct(pct, null,
								objDestPocket, objSrcSubMdn, objDestSubMdn);
						log.info("#after validatePct in transfer confirmation returnFix.getInternalErrorCode() --> "
								+ returnFix.getInternalErrorCode());
					}

					if (isNullorZero(returnFix.getInternalErrorCode())) {
						TransactionLog transactionLog = pct
								.getTransactionLog();

						if (transactionLog != null) {
							String bankPin = "0000";// toBankInquiryMessage.getPin();
							BigDecimal transferAmountWithCharges = moneyService.add(
									pct.getAmount(), pct.getCharges());
							BigDecimal amount = moneyService.round(pct.getAmount());
							returnFix.setAmount(amount);
							returnFix.setCharges(moneyService.round(pct.getCharges()));
							returnFix.setCurrency(pct.getCurrency());
							returnFix.setTransactionID(confirmationToBank
									.getTransactionID());
							returnFix.setTransferID(confirmationToBank
									.getTransferID());

							if ((CmFinoFIX.TransferStatus_ConfirmationPromptSentToSubscriber
									.equals(pct.getTransferstatus()))
									|| (CmFinoFIX.TransferStatus_TransferInquirySentToBank
											.equals(pct.getTransferstatus()))) {
								if (confirmationToBank.getConfirmed()) {

									log.debug("Source Pocket Type="
											+ objSrcPocket.getPocketTemplateByPockettemplateid()
													.getType()
											+ ", Destination Pocket Type="
											+ objDestPocket.getPocketTemplateByPockettemplateid()
													.getType());

									if ((objSrcPocket.getPocketTemplateByPockettemplateid()
											.getType() == CmFinoFIX.PocketType_SVA
											.intValue() || isNFCPocketType(objSrcPocket) || isLakupandaiPocketType(objSrcPocket))
											&& (objDestPocket
													.getPocketTemplateByPockettemplateid()
													.getType() == CmFinoFIX.PocketType_SVA
													.intValue() || isNFCPocketType(objDestPocket) || isLakupandaiPocketType(objDestPocket))) {
										returnFix.setSourcePocketId(objSrcPocket.getId().longValue());
										returnFix.setDestPocketId(objDestPocket.getId().longValue());
										returnFix.setRemarks(confirmationToBank.getRemarks());
										pct.setDestpocketbalance(objDestPocket
												.getCurrentbalance());
										pct.setSourcepocketbalance(objSrcPocket
												.getCurrentbalance());
//										ledgerService.createLedgerEntry(
//												objSrcPocket, suspensePocket,
//												null, pct,
//												transferAmountWithCharges);
//										ledgerService.createLedgerEntry(
//												suspensePocket, objDestPocket,
//												null, pct, pct.getAmount());
//										ledgerService.createLedgerEntry(
//												suspensePocket, chargesPocket,
//												null, pct, pct.getCharges());
										
										List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,confirmationToBank.getServiceChargeTransactionLogID(), 
												pct.getId().longValue(), objSrcPocket, objDestPocket, coreDataWrapper.getChargesPocket(), pct.getAmount(), pct.getCharges(), 
												ConfigurationUtil.getMfinoNettingLedgerEntries());

										/*
										 * Update pockets, all the updates
										 * should be in same transaction
										 */

											if(CmFinoFIX.IsRetirePartner_True.equals(confirmationToBank.getIsRetirePartner())) {
												
												String cardPanStringToReplace = null;
							                    String cardPan = objSrcPocket.getCardpan();
							                    
							                    if (StringUtils.isNotBlank(cardPan)) {
							                    	
							                    	cardPanStringToReplace = cardPan + "R";
							                    }
							
							                    if (StringUtils.isBlank(cardPanStringToReplace)) {
							                    	
							                    	cardPanStringToReplace = cardPan;
							                    }
							
							                    objSrcPocket.setCardpan(cardPanStringToReplace);
							                    objSrcPocket.setStatus(CmFinoFIX.PocketStatus_Retired);
							                    objSrcPocket.setIsdefault((short)0);
											}
										
											// pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
											// pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
											if(confirmationToBank.getUICategory() != null && confirmationToBank.getUICategory().equals(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup)){
												pct.setNotificationcode(Long.valueOf(NotificationCodes.NFCPocketTopupSuccess
														.getNotificationCode()));
											}
											else if(confirmationToBank.getUICategory() != null && confirmationToBank.getUICategory().equals(CmFinoFIX.TransactionUICategory_Donation)){
												pct.setNotificationcode(Long.valueOf(NotificationCodes.DonationCompleteToSender
														.getNotificationCode()));
											}
											else if (confirmationToBank.getUICategory() != null && confirmationToBank.getUICategory().equals(CmFinoFIX.TransactionUICategory_Cashin_At_Agent)) {
												pct.setNotificationcode(Long.valueOf(NotificationCodes.AgentSubscriberCashinSuccessToSender.getInternalErrorCode()));
											}
											else{
												pct.setNotificationcode(Long.valueOf(NotificationCodes.EMoneytoEMoneyCompleteToSender
														.getNotificationCode()));
											}
											pct.setLocalbalancerevertrequired((short)1);
											coreDataWrapper.save(pct);
//											coreDataWrapper
//													.save(suspensePocket);
											if(ledgerService.isImmediateUpdateRequiredForPocket(objSrcPocket)){
											coreDataWrapper.save(objSrcPocket);
											}
											if(ledgerService.isImmediateUpdateRequiredForPocket(objDestPocket)){
											coreDataWrapper.save(objDestPocket);
											}
											coreDataWrapper.save(lstMfsLedgers);
//											if (pct.getCharges().compareTo(
//													BigDecimal.valueOf(0)) == 1) {
//												coreDataWrapper
//														.save(chargesPocket);
//											}

											returnFix
													.setResult(CmFinoFIX.ResponseCode_Success);
											
											handlePCTonSuccess(pct);
										
										// TODO: BUG, in case of exception we
										// need to send the failure response to
										// webapi
										
										if(confirmationToBank.getUICategory() != null && confirmationToBank.getUICategory().equals(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup)){
											returnFix.setInternalErrorCode(NotificationCodes.NFCPocketTopupSuccess.getInternalErrorCode());
										} else if (confirmationToBank.getUICategory() != null && confirmationToBank.getUICategory().equals(CmFinoFIX.TransactionUICategory_Cashin_At_Agent)) {
											returnFix.setInternalErrorCode(NotificationCodes.AgentSubscriberCashinSuccessToSender.getInternalErrorCode());
										} else{
											returnFix.setInternalErrorCode(NotificationCodes.EMoneytoEMoneyCompleteToSender.getInternalErrorCode());
										}
										

										returnFix.setSourceMDNBalance(moneyService.round(new BigDecimal(objSrcPocket.getCurrentbalance())));
										returnFix.setDestinationMDNBalance(moneyService.round(new BigDecimal(objDestPocket.getCurrentbalance())));

										// Changing the Notification code to
										// show the Biller Code
										if (CmFinoFIX.TransactionUICategory_Bill_Payment_Emoney
												.equals(pct.getUicategory())) {
											returnFix
													.setInternalErrorCode(NotificationCodes.BillPayCompletedToSender
															.getInternalErrorCode());
										} else if (CmFinoFIX.TransactionUICategory_Cash_In_To_Agent
												.equals(pct.getUicategory())) {
											returnFix
													.setInternalErrorCode(NotificationCodes.FundingOfAgentSuccessToSender
															.getInternalErrorCode());
										}
										else if (CmFinoFIX.TransactionUICategory_Donation.equals(pct.getUicategory())) {
											returnFix.setInternalErrorCode(NotificationCodes.DonationCompleteToSender.getInternalErrorCode());
										}

										// Changing the Notification code in
										// case of Bulk transfer.
										if (CmFinoFIX.TransactionUICategory_Bulk_Transfer
												.equals(pct.getUicategory())) {
											returnFix
													.setInternalErrorCode(NotificationCodes.BulkTransferCompletedToPartner
															.getInternalErrorCode());
										} else if (CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer
												.equals(pct.getUicategory())) {
											returnFix
													.setInternalErrorCode(NotificationCodes.BulkTransferCompletedToSubscriber_Dummy
															.getInternalErrorCode());
										} else if (CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer
												.equals(pct.getUicategory())) {
											returnFix
													.setInternalErrorCode(NotificationCodes.BulkTransferReverseCompletedToPartner
															.getInternalErrorCode());
										}
										activitiesLog
												.setIssuccessful((short)1);

									} else if ((objSrcPocket
											.getPocketTemplateByPockettemplateid().getType()
											 == CmFinoFIX.PocketType_SVA
											 || isLakupandaiPocketType(objSrcPocket))
											&& (objDestPocket
													.getPocketTemplateByPockettemplateid()
													.getType() == CmFinoFIX.PocketType_BankAccount
													)) {
										pct.setSourcepocketbalance(objSrcPocket
												.getCurrentbalance());
//										ledgerService.createLedgerEntry(
//												objSrcPocket, suspensePocket,
//												null, pct,
//												transferAmountWithCharges);
										List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,confirmationToBank.getServiceChargeTransactionLogID(), 
												pct.getId().longValue(), objSrcPocket, coreDataWrapper.getSuspensePocket(), coreDataWrapper.getChargesPocket(), 
												transferAmountWithCharges, BigDecimal.ZERO, ConfigurationUtil.getMfinoNettingLedgerEntries());
										pct.setTransferstatus(CmFinoFIX.TransferStatus_MoneyTransaferSentToBank);

										pct.setBanksystemtraceauditnumber(""
												+ confirmationToBank
														.getTransactionID());
										pct.setBankretrievalreferencenumber(""
												+ confirmationToBank
														.getTransactionID());
										pct.setLocalbalancerevertrequired((short)1);
										pct.setBankreversalrequired((short)1);
										
											coreDataWrapper.save(pct);

											coreDataWrapper.save(objSrcPocket);
//											coreDataWrapper
//													.save(suspensePocket);
											if (ledgerService.isImmediateUpdateRequiredForPocket(objSrcPocket)) {
												coreDataWrapper.save(objSrcPocket);
											}
											
											coreDataWrapper.save(lstMfsLedgers);
											
											/*
											 * Send ISO message to transfer
											 * amount, and rest of the logic in
											 * on confirmation from bank
											 */
											CMMoneyTransferToBank moneyTransferToBank = new CMMoneyTransferToBank();
											IntegrationSummary iSummary = integrationSummaryService.getIntegrationSummary(confirmationToBank.getServiceChargeTransactionLogID(),pct.getId().longValue());
											if(iSummary != null){
												log.info("Processing code"+iSummary.getReconcilationid1());
												moneyTransferToBank.setProcessingCode(iSummary.getReconcilationid1());
												moneyTransferToBank.setAdditionalInfo(iSummary.getReconcilationid2());
											}
											Timestamp ts = DateTimeUtil.getGMTTime();
											
											moneyTransferToBank
													.copy(confirmationToBank);
											moneyTransferToBank.setSourceMDN(confirmationToBank.getSourceMDN());
											moneyTransferToBank.setRemarks(confirmationToBank.getRemarks());
											moneyTransferToBank
													.setSourceMDNToUseForBank(coreDataWrapper
															.getPlatformMdn());
											moneyTransferToBank.setAmount(pct
													.getAmount());
											moneyTransferToBank.setBankCode(pct
													.getBankcode().intValue());
											moneyTransferToBank
													.setDestCardPAN(pct
															.getDestcardpan());
											moneyTransferToBank.setDestMDN(pct
													.getDestmdn());
											moneyTransferToBank
													.setParentTransactionID(pct
															.getTransactionLog()
															.getId().longValue());
											moneyTransferToBank
													.setUICategory(pct
															.getUicategory().intValue());
											moneyTransferToBank
													.setSourcePocketID(objSrcPocket
															.getId().longValue());
											moneyTransferToBank
													.setDestPocketID(objDestPocket
															.getId().longValue());
											moneyTransferToBank
													.setPin(MCEUtil.FAKE_PIN_FOR_OMB);
											moneyTransferToBank
													.setTransferID(pct.getId().longValue());
											moneyTransferToBank
													.setTransferTime(ts);
											moneyTransferToBank
													.setDestinationBankAccountNo(confirmationToBank
															.getDestinationBankAccountNo());

											moneyTransferToBank
													.setSourceBankAccountType(""
															+ CmFinoFIX.BankAccountType_Lakupandai);
											moneyTransferToBank.setLanguage((int)objSourceSubscriber.getLanguage());
											moneyTransferToBank
													.setSourceCardPAN(coreDataWrapper
															.getGlobalAccountNumber());
											moneyTransferToBank
													.setOriginalReferenceID(confirmationToBank
															.getOriginalReferenceID());
											if(StringUtils.isNotBlank(confirmationToBank.getINTxnId())){
												moneyTransferToBank.setINTxnId(confirmationToBank.getINTxnId());
											}
//											if (CmFinoFIX.BankAccountCardType_SavingsAccount
//													.equals(objSrcPocket
//															.getPocketTemplateByPockettemplateid()
//															.getBankAccountCardType())) {
//												moneyTransferToBank
//														.setSourceBankAccountType(""
//																+ CmFinoFIX.BankAccountType_Saving);
//											} else {
//												moneyTransferToBank
//														.setSourceBankAccountType(""
//																+ CmFinoFIX.BankAccountType_Checking);
//											}

											if (CmFinoFIX.BankAccountCardType_SavingsAccount
													.equals(objDestPocket
															.getPocketTemplateByPockettemplateid()
															.getBankaccountcardtype())) {
												moneyTransferToBank
														.setDestinationBankAccountType(""
																+ CmFinoFIX.BankAccountType_Saving);
											} else {
												moneyTransferToBank
														.setDestinationBankAccountType(""
																+ CmFinoFIX.BankAccountType_Checking);
											}

											isoFix = moneyTransferToBank;
										
									} else if ((objSrcPocket
											.getPocketTemplateByPockettemplateid().getType()
											 == CmFinoFIX.PocketType_BankAccount
											)
											&& (objDestPocket
													.getPocketTemplateByPockettemplateid()
													.getType() == CmFinoFIX.PocketType_SVA
													|| isLakupandaiPocketType(objDestPocket))) {
										log.info("BankServiceDefaultImpl :: onTransferConfirmationToBank : Bank 2 E-Money");

//										ledgerService.createLedgerEntry(
//												globalSVAPocket,
//												suspensePocket, null, pct,
//												transferAmountWithCharges);
										List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,confirmationToBank.getServiceChargeTransactionLogID(),
												pct.getId().longValue(), coreDataWrapper.getGlobalSVAPocket(), coreDataWrapper.getSuspensePocket(), coreDataWrapper.getChargesPocket(), 
												transferAmountWithCharges, BigDecimal.ZERO, ConfigurationUtil.getMfinoNettingLedgerEntries());
										pct.setTransferstatus(CmFinoFIX.TransferStatus_MoneyTransaferSentToBank);
										pct.setBanksystemtraceauditnumber(""
												+ confirmationToBank
														.getTransactionID());
										pct.setBankretrievalreferencenumber(""
												+ confirmationToBank
														.getTransactionID());
										pct.setLocalbalancerevertrequired((short)1);
										pct.setBankreversalrequired((short)1);
											coreDataWrapper.save(pct);
//											coreDataWrapper
//													.save(suspensePocket);
//											coreDataWrapper
//													.save(globalSVAPocket);
											coreDataWrapper.save(lstMfsLedgers);

											/*
											 * Send ISO message to transfer
											 * amount, and rest of the logic in
											 * on confirmation from bank
											 */
											CMMoneyTransferToBank moneyTransferToBank = new CMMoneyTransferToBank();
											IntegrationSummary iSummary = integrationSummaryService.getIntegrationSummary(confirmationToBank.getServiceChargeTransactionLogID(),pct.getId().longValue());
											if(iSummary != null){
												log.info("Processing code"+iSummary.getReconcilationid1());
												moneyTransferToBank.setProcessingCode(iSummary.getReconcilationid1());
												moneyTransferToBank.setAdditionalInfo(iSummary.getReconcilationid2());
											}
											Timestamp ts = DateTimeUtil.getGMTTime();

											moneyTransferToBank
													.copy(confirmationToBank);
											moneyTransferToBank.setRemarks(confirmationToBank.getRemarks());
                                            moneyTransferToBank.setLanguage((int)objSourceSubscriber.getLanguage());
											moneyTransferToBank
													.setAmount(transferAmountWithCharges);
											moneyTransferToBank.setBankCode(pct
													.getBankcode().intValue());
											moneyTransferToBank
													.setDestCardPAN(pct
															.getDestcardpan());
											moneyTransferToBank.setDestMDN(pct
													.getDestmdn());
											moneyTransferToBank
													.setParentTransactionID(pct
															.getTransactionLog()
															.getId().longValue());
											moneyTransferToBank
													.setUICategory(pct
															.getUicategory().intValue());
											moneyTransferToBank
													.setSourcePocketID(objSrcPocket
															.getId().longValue());
											moneyTransferToBank
													.setDestPocketID(objDestPocket
															.getId().longValue());
											moneyTransferToBank.setPin(bankPin);
											moneyTransferToBank
													.setSourceCardPAN(pct
															.getSourcecardpan());
											moneyTransferToBank
													.setTransferID(pct.getId().longValue());
											moneyTransferToBank
													.setTransferTime(ts);

											moneyTransferToBank
													.setDestCardPAN(coreDataWrapper
															.getGlobalAccountNumber());
											moneyTransferToBank
													.setDestinationBankAccountType(""
															+ CmFinoFIX.BankAccountType_Lakupandai);
											moneyTransferToBank
													.setOriginalReferenceID(confirmationToBank
															.getOriginalReferenceID());
											if(StringUtils.isNotBlank(confirmationToBank.getINTxnId())){
												moneyTransferToBank.setINTxnId(confirmationToBank.getINTxnId());
											}
											if (CmFinoFIX.BankAccountCardType_SavingsAccount
													.equals(objSrcPocket
															.getPocketTemplateByPockettemplateid()
															.getBankaccountcardtype())) {
												moneyTransferToBank
														.setSourceBankAccountType(""
																+ CmFinoFIX.BankAccountType_Saving);
											} else {
												moneyTransferToBank
														.setSourceBankAccountType(""
																+ CmFinoFIX.BankAccountType_Checking);
											}

//											if (CmFinoFIX.BankAccountCardType_SavingsAccount
//													.equals(objDestPocket
//															.getPocketTemplateByPockettemplateid()
//															.getBankAccountCardType())) {
//												moneyTransferToBank
//														.setDestinationBankAccountType(""
//																+ CmFinoFIX.BankAccountType_Saving);
//											} else {
//												moneyTransferToBank
//														.setDestinationBankAccountType(""
//																+ CmFinoFIX.BankAccountType_Checking);
//											}

											isoFix = moneyTransferToBank;
										
									} else if ((objSrcPocket
											.getPocketTemplateByPockettemplateid().getType()
											 == CmFinoFIX.PocketType_BankAccount
											)
											&& (objDestPocket
													.getPocketTemplateByPockettemplateid()
													.getType() == CmFinoFIX.PocketType_BankAccount
													)) {

										pct.setTransferstatus(CmFinoFIX.TransferStatus_MoneyTransaferSentToBank);
										pct.setBanksystemtraceauditnumber(""
												+ confirmationToBank
														.getTransactionID());
										pct.setBankretrievalreferencenumber(""
												+ confirmationToBank
														.getTransactionID());
										pct.setBankreversalrequired((short)1);

										/*
										 * Send ISO message to transfer amount,
										 * and rest of the logic in on
										 * confirmation from bank
										 */
										CMMoneyTransferToBank moneyTransferToBank = new CMMoneyTransferToBank();
										IntegrationSummary iSummary = integrationSummaryService.getIntegrationSummary(confirmationToBank.getServiceChargeTransactionLogID(),pct.getId().longValue());
										if(iSummary != null){
											log.info("Processing code"+iSummary.getReconcilationid1());
											moneyTransferToBank.setProcessingCode(iSummary.getReconcilationid1());
											moneyTransferToBank.setAdditionalInfo(iSummary.getReconcilationid2());
										}
										Timestamp ts = DateTimeUtil.getGMTTime();

										moneyTransferToBank
												.copy(confirmationToBank);
										moneyTransferToBank.setRemarks(confirmationToBank.getRemarks());
                                        moneyTransferToBank.setLanguage((int)objSourceSubscriber.getLanguage());
										moneyTransferToBank.setAmount(pct
												.getAmount());
										moneyTransferToBank.setBankCode(pct
												.getBankcode().intValue());
										moneyTransferToBank.setDestCardPAN(pct
												.getDestcardpan());
										moneyTransferToBank.setDestMDN(pct
												.getDestmdn());
										moneyTransferToBank
												.setParentTransactionID(pct
														.getTransactionLog()
														.getId().longValue());
										moneyTransferToBank.setUICategory(pct
												.getUicategory().intValue());
										moneyTransferToBank
												.setSourcePocketID(objSrcPocket
														.getId().longValue());
										moneyTransferToBank
												.setDestPocketID(objDestPocket
														.getId().longValue());
										moneyTransferToBank.setPin(bankPin);
										moneyTransferToBank
												.setSourceCardPAN(pct
														.getSourcecardpan());
										moneyTransferToBank.setTransferID(pct
												.getId().longValue());
										moneyTransferToBank.setTransferTime(ts);
										moneyTransferToBank
												.setOriginalReferenceID(confirmationToBank
														.getOriginalReferenceID());
										moneyTransferToBank
												.setDestinationBankAccountNo(confirmationToBank
														.getDestinationBankAccountNo());
										if(StringUtils.isNotBlank(confirmationToBank.getINTxnId())){
											moneyTransferToBank.setINTxnId(confirmationToBank.getINTxnId());
										}
										if (CmFinoFIX.BankAccountCardType_SavingsAccount
												.equals(objSrcPocket
														.getPocketTemplateByPockettemplateid()
														.getBankaccountcardtype())) {
											moneyTransferToBank
													.setSourceBankAccountType(""
															+ CmFinoFIX.BankAccountType_Saving);
										} else {
											moneyTransferToBank
													.setSourceBankAccountType(""
															+ CmFinoFIX.BankAccountType_Checking);
										}

										if (CmFinoFIX.BankAccountCardType_SavingsAccount
												.equals(objDestPocket
														.getPocketTemplateByPockettemplateid()
														.getBankaccountcardtype())) {
											moneyTransferToBank
													.setDestinationBankAccountType(""
															+ CmFinoFIX.BankAccountType_Saving);
										} else {
											moneyTransferToBank
													.setDestinationBankAccountType(""
															+ CmFinoFIX.BankAccountType_Checking);
										}

										isoFix = moneyTransferToBank;
									}
								} else {
									log.info("BankServiceDefaultImpl :: onTransferConfirmationToBank() : BankAccountToBankAccountCancelledBySubscriber");
									// user cancelled the confirmation
									returnFix
											.setInternalErrorCode(NotificationCodes.BankAccountToBankAccountCancelledBySubscriber
													.getInternalErrorCode());
									onRevertOfTransferInquiry(pct, false);
									pct.setTransferstatus((int)CmFinoFIX.TransferStatus_Failed);
									pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountToBankAccountCanceledBySubscriber));
									pct.setNotificationcode(Long.valueOf(CmFinoFIX.NotificationCode_BankAccountToBankAccountCancelledBySubscriber));

									handlePCTonFailure(pct);
								}
							} else {
								// handle, bug, transfer status changed.
								log.error("BankServiceDefaultImpl :: onTransferConfirmationToBank() :: PCT status changed****"
										+ pct.getId());
								returnFix
										.setInternalErrorCode(NotificationCodes.TransferRecordChangedStatus
												.getInternalErrorCode());
							}
						} else {
							log.error("BankServiceDefaultImpl :: onTransferConfirmationToBank() :: Transaction Log Missing");
							returnFix
									.setInternalErrorCode(NotificationCodes.RequiredParametersMissing
											.getInternalErrorCode());
						}
					} else {
						log.error("BankServiceDefaultImpl :: onTransferConfirmationToBank() ::Pct Validation failed");
						/* PCT validation failed. */
						onRevertOfTransferInquiry(pct, false);
						pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
						handlePCTonFailure(pct);
					}
				} else {
					log.error("BankServiceDefaultImpl :: onTransferConfirmationToBank() :: Transfer Record Not found");
					/* PCT not found, may be it expired and got moved to CT. */
					returnFix
							.setInternalErrorCode(NotificationCodes.TransferRecordNotFound
									.getInternalErrorCode());
				}
			}
		}

		returnFix.setSourceMDN(objSrcSubMdn.getMdn());
		returnFix.setSenderMDN(objDestSubMdn.getMdn());
		returnFix.setLanguage((int)objSourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(objDestSubMdn.getMdn());
		returnFix.setReceiveTime(confirmationToBank.getReceiveTime());

		if (objSourceSubscriber.getCompany() != null) {
			activitiesLog.setCompany(objSrcPocket.getCompany());
		}

		activitiesLog.setMsgtype(Long.valueOf(confirmationToBank.getMessageType()));
		activitiesLog.setParenttransactionid(new BigDecimal(confirmationToBank
				.getParentTransactionID()));
		activitiesLog.setServletpath(confirmationToBank.getServletPath());
		activitiesLog.setSourceapplication(Long.valueOf(confirmationToBank
				.getSourceApplication()));
		activitiesLog.setSourcemdn(confirmationToBank.getSourceMDN());
		activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes
				.getNotificationCodeFromInternalCode(returnFix
						.getInternalErrorCode())));
		activitiesLog.setSourcemdnid(objSrcSubMdn.getId());
		activitiesLog.setSourcesubscriberid(objSourceSubscriber.getId());
		activitiesLog.setSourcesubscriberid(objSrcPocket.getId());
		activitiesLog.setSourcepockettype(objSrcPocket.getPocketTemplateByPockettemplateid()
				.getType());

		if (objSrcPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(objSrcPocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}

		if (pct != null)
			activitiesLog.setTransferid(pct.getId());
// [Bala] Commented the Activities log creation
//		coreDataWrapper.save(activitiesLog);

		if (isoFix != null) {
			return isoFix;
		}
		returnFix.setServiceChargeTransactionLogID(confirmationToBank
				.getServiceChargeTransactionLogID());
		return returnFix;
	}
	
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED, rollbackFor=Throwable.class)
	public CFIXMsg onBulkDistribution(CMBulkDistribution bulkDistribution) {
		log.info("BankServiceDefaultImpl :: onBulkDistribution BEGIN");

		BackendResponse returnFix = createResponseObject();
		Subscriber sourceSubscriber = null;
		Subscriber destinationSubscriber = null;

		BigDecimal amount = bulkDistribution.getAmount();
		BigDecimal charges = bulkDistribution.getCharges();
		BigDecimal totalTransactionAmount = amount.add(charges);
		String sourceMdnStr = bulkDistribution.getSourceMDN();

		SubscriberMdn sourceSubcriberMdn = coreDataWrapper.getSubscriberMdn(sourceMdnStr);
		SubscriberMdn destinationSubcriberMdn = coreDataWrapper.getSubscriberMdn(bulkDistribution.getDestMDN());
		if (sourceSubcriberMdn != null) {
			sourceSubscriber = sourceSubcriberMdn.getSubscriber();
		}
		if (destinationSubcriberMdn != null) {
			destinationSubscriber = destinationSubcriberMdn.getSubscriber();
		}
		// Here the Source pocket is always suspense pocket of from agent, so no need to acquire the lock.
		Pocket sourcePocket = coreDataWrapper.getPocketById(bulkDistribution.getSourcePocketID());
		Pocket destinationPocket = coreDataWrapper
				.getPocketById(bulkDistribution.getDestPocketID(), LockMode.UPGRADE);
		boolean isSystemInitiatedTransaction = true;

		PendingCommodityTransfer pct = null;

		log.info("BankServiceDefaultImpl :: onBulkDistribution sourceSubscriber="
				+ sourceSubscriber
				+ ", destinationSubscriber="
				+ destinationSubscriber
				+ ", sourceSubcriberMdn="
				+ sourceSubcriberMdn
				+ ", destinationSubcriberMdn="
				+ destinationSubcriberMdn
				+ ", sourcePocket="
				+ sourcePocket
				+ ", destinationPocket=" + destinationPocket);
		
		if (CmFinoFIX.PocketType_SVA.intValue() != sourcePocket.getPocketTemplateByPockettemplateid().getType()) {
			returnFix.setInternalErrorCode(NotificationCodes.SourceMoneyPocketNotFound.getInternalErrorCode());
		} else if ((CmFinoFIX.PocketType_SVA.intValue() != destinationPocket.getPocketTemplateByPockettemplateid().getType()) &&
				!isLakupandaiPocketType(destinationPocket)) {
			returnFix.setInternalErrorCode(NotificationCodes.DestinationEMoneyPocketNotFound.getInternalErrorCode());
		} else if (isNullorZero(returnFix.getInternalErrorCode())) {
//			returnFix = validationService.validateBankAccountSubscriber(sourceSubscriber, sourceSubcriberMdn, sourcePocket,
//					"mFino260", true, false, false, false);

			log.info("BankServiceDefaultImpl:onBulkDistribution: Source subcriber validated ErrorCode=" + returnFix.getInternalErrorCode());
	
			if (isNullorZero(returnFix.getInternalErrorCode())) {
				returnFix = validationService.validateBankAccountSubscriber(destinationSubscriber, destinationSubcriberMdn,
							destinationPocket, "", false, false, false, false);
	
				log.info("BankServiceDefaultImpl:onBulkDistribution: Dest subcriber validated ErrorCode="	+ returnFix.getInternalErrorCode());
	
				if (isNullorZero(returnFix.getInternalErrorCode())) {
					if (!(sourcePocket.getId().equals(destinationPocket.getId()))) {
	
						pct = commodityTransferService.createPCT(bulkDistribution, sourceSubscriber, destinationSubscriber, sourcePocket,
								destinationPocket, sourceSubcriberMdn, destinationSubcriberMdn, bulkDistribution.getSourceMessage(),
								amount, charges, null, CmFinoFIX.BucketType_Special_Bank_Account, CmFinoFIX.BillingType_None,
								CmFinoFIX.TransferStatus_Initialized);
	
						returnFix = validationService.validatePocketsForBulkTransfer(sourcePocket, destinationPocket, totalTransactionAmount);
						log.info("After validatePocketsForBulkTransfer returnFix.getInternalErrorCode()=" + returnFix.getInternalErrorCode());
						if (returnFix != null && isNullorZero(returnFix.getInternalErrorCode())) {
							setPocketLimits(sourcePocket, amount);
							setPocketLimits(destinationPocket, amount);
	
							if (bulkDistribution.getChannelCode() != null) {
								pct.setIso8583Merchanttype(bulkDistribution.getChannelCode());
							} else if (pct.getIso8583Merchanttype() == null) {
								pct.setIso8583Merchanttype(CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Other);
							}
	
							if(ledgerService.isImmediateUpdateRequiredForPocket(destinationPocket)){
								coreDataWrapper.save(destinationPocket);
							}								
							coreDataWrapper.save(pct);
								
							pct.setSourcepocketbalance(sourcePocket.getCurrentbalance());
							pct.setDestpocketbalance(destinationPocket.getCurrentbalance());
							List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,bulkDistribution.getServiceChargeTransactionLogID(), 
									pct.getId().longValue(),sourcePocket, destinationPocket, coreDataWrapper.getChargesPocket(),
									amount, charges, ConfigurationUtil.getMfinoNettingLedgerEntries());
							coreDataWrapper.save(lstMfsLedgers);
							
							returnFix.setInternalErrorCode(NotificationCodes.BulkTransferCompletedToSubscriber_Dummy.getInternalErrorCode());
							pct.setNotificationcode(Long.valueOf(NotificationCodes.BulkTransferCompletedToSubscriber_Dummy.getNotificationCode()));
							pct.setLocalbalancerevertrequired((short)1);
							coreDataWrapper.save(pct);
							if(ledgerService.isImmediateUpdateRequiredForPocket(destinationPocket)){
								coreDataWrapper.save(destinationPocket);
							}
							handlePCTonSuccess(pct);
							returnFix.setTransactionID(pct.getId().longValue());
							returnFix.setTransferID(pct.getId().longValue());
							returnFix.setAmount(pct.getAmount());
							returnFix.setCurrency(pct.getCurrency());
							returnFix.setResult(CmFinoFIX.ResponseCode_Success);
							
						} else {
							pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountToBankAccountSourcePocketLimits));
							pct.setNotificationcode(Long.valueOf(NotificationCodes.getNotificationCodeFromInternalCode(returnFix.getInternalErrorCode())));
							pct.setEndtime(pct.getStarttime());
							pct.setTransferstatus((short)2);
							pct.setLocalrevertrequired((short)0);
							pct.setLocalbalancerevertrequired((short)0);
							coreDataWrapper.save(pct);
							handlePCTonFailure(pct);
						}
					} else {
							returnFix.setInternalErrorCode(NotificationCodes.EMoneytoEMoneyFailed_SelfTransfer.getInternalErrorCode());
					}
				}
			}
		}

		returnFix.setSourceMDN(sourceSubcriberMdn.getMdn());
		returnFix.setSenderMDN(sourceSubcriberMdn.getMdn());
		returnFix.setLanguage((int)sourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(destinationSubcriberMdn.getMdn());
		returnFix.setReceiveTime(bulkDistribution.getReceiveTime());
		returnFix.setCustomerServiceShortCode(sourceSubscriber.getCompany().getCustomerservicenumber());
		returnFix.setMSPID(bulkDistribution.getMSPID());
		return returnFix;
	}
	
	
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	public CFIXMsg onChargeDistribution(CMChargeDistribution chargeDistribution) {
		log.info("BankServiceDefaultImpl :: onChargeDistribution BEGIN");


		BackendResponse returnFix = createResponseObject();

		BigDecimal amount = chargeDistribution.getAmount();
		BigDecimal taxAmount = chargeDistribution.getTaxAmount();

		String sourceMdnStr = chargeDistribution.getSourceMDN();// coreDataWrapper.getPlatformMdn();

		Subscriber sourceSubscriber = coreDataWrapper.getSubscriberByMdn(
				sourceMdnStr, LockMode.UPGRADE);
		Subscriber destinationSubscriber = coreDataWrapper.getSubscriberByMdn(
				chargeDistribution.getDestMDN(), LockMode.UPGRADE);
		SubscriberMdn sourceSubcriberMdn = coreDataWrapper.getSubscriberMdn(
				sourceMdnStr, LockMode.UPGRADE);
		SubscriberMdn destinationSubcriberMdn = coreDataWrapper
				.getSubscriberMdn(chargeDistribution.getDestMDN(),
						LockMode.UPGRADE);
		Pocket sourcePocket = coreDataWrapper.getPocketById(chargeDistribution
				.getSourcePocketID(),LockMode.UPGRADE);
		Pocket destinationPocket = coreDataWrapper
				.getPocketById(chargeDistribution.getDestPocketID(), LockMode.UPGRADE);
//		Pocket suspensePocket = coreDataWrapper.getSuspensePocketWithLock();
		Pocket taxPocket = coreDataWrapper.getPocket(SystemParameterKeys.TAX_POCKET_ID_KEY);
		ActivitiesLog activitiesLog = new ActivitiesLog();

		// For settlement
		SctlSettlementMap sctlStlmtMap = new SctlSettlementMap();
		SCTLSettlementMapDAO sctlStlmtDAO = new SCTLSettlementMapDAO();

		ServiceChargeTransactionLogDAO sctlDAO = new ServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDAO.getById(chargeDistribution
				.getServiceChargeTransactionLogID());
		ServiceDAO serviceDAO=new ServiceDAO();
         Service service=serviceDAO.getById(sctl.getServiceid().longValue());
		if (sctl != null) {
			sctlStlmtMap.setServiceChargeTxnLog(sctl);
			
			sctlStlmtMap.setService(service);
		}
		PartnerDAO partnerDAO=new PartnerDAO();
		Partner partner=partnerDAO.getById(chargeDistribution.getPartnerID());
		sctlStlmtMap.setPartner(partner);
		sctlStlmtMap.setAmount(chargeDistribution.getAmount());
		sctlStlmtMap.setStatus(CmFinoFIX.SettlementStatus_Initiated);
		MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance()
				.getMfinoServiceProviderDAO();
		sctlStlmtMap.setMspid(mspDAO.getById(1L).getId());

		sctlStlmtDAO.save(sctlStlmtMap);

		Long transactionChargeID = chargeDistribution.getTransactionChargeID();
		Boolean isPartOfSharedUpChain = chargeDistribution
				.getIsPartOfSharedUpChain();

		PendingCommodityTransfer pct = null;

//		if (taxPocket.getCurrentBalance() == null) {
//			taxPocket.setCurrentBalance(BigDecimal.ZERO);
//		}

		log.info("BankServiceDefaultImpl :: onChargeDistribution sourceSubscriber="
				+ sourceSubscriber
				+ ", destinationSubscriber="
				+ destinationSubscriber
				+ ", sourceSubcriberMdn="
				+ sourceSubcriberMdn
				+ ", destinationSubcriberMdn="
				+ destinationSubcriberMdn
				+ ", sourcePocket="
				+ sourcePocket
				+ ", destinationPocket=" + destinationPocket);

		if (CmFinoFIX.PocketType_SVA.intValue() != sourcePocket
				.getPocketTemplateByPockettemplateid().getType()) {
			returnFix
					.setInternalErrorCode(NotificationCodes.DefaultBankAccountPocketNotFound
							.getInternalErrorCode());
		} else if (CmFinoFIX.PocketType_SVA.intValue() != destinationPocket
				.getPocketTemplateByPockettemplateid().getType()) {
			returnFix
					.setInternalErrorCode(NotificationCodes.DestinationEMoneyPocketNotFound
							.getInternalErrorCode());
		} else if (isNullorZero(returnFix.getInternalErrorCode())) {
			returnFix = validationService.validateBankAccountSubscriber(
					sourceSubscriber, sourceSubcriberMdn, sourcePocket,
					"mFino260", true, false, false, false);

			log.info("BankServiceDefaultImpl:onChargeDistribution: Source subcriber validated ErrorCode="
					+ returnFix.getInternalErrorCode());

			if (isNullorZero(returnFix.getInternalErrorCode())) {
				returnFix = validationService.validateBankAccountSubscriber(
						destinationSubscriber, destinationSubcriberMdn,
						destinationPocket, "", false, false, false, false);

				log.info("BankServiceDefaultImpl:onChargeDistribution: Dest subcriber validated ErrorCode="
						+ returnFix.getInternalErrorCode());

				if (isNullorZero(returnFix.getInternalErrorCode())) {
					if (!(sourcePocket.getId()
							.equals(destinationPocket.getId()))) {

						pct = commodityTransferService.createPCT(
								chargeDistribution, sourceSubscriber,
								destinationSubscriber, sourcePocket,
								destinationPocket, sourceSubcriberMdn,
								destinationSubcriberMdn, "Charge Distribution",
								amount, null, taxAmount,
								CmFinoFIX.BucketType_Special_Bank_Account,
								CmFinoFIX.BillingType_None,
								CmFinoFIX.TransferStatus_Initialized);

						// returnFix =
						// validationService.validateRisksAndLimits(sourcePocket,
						// destinationPocket, amount, amount);

						returnFix = validationService
								.validatePocketsForChargeDistribution(
										sourcePocket, amount, true);
						if (returnFix != null
								&& isNullorZero(returnFix
										.getInternalErrorCode())) {
							returnFix = validationService
									.validatePocketsForChargeDistribution(
											destinationPocket, amount, false);
						}

						log.info("After ValidateRisksAndLimits returnFix.getInternalErrorCode()="
								+ returnFix.getInternalErrorCode());

						if (isNullorZero(returnFix.getInternalErrorCode())) {

							sourcePocket
									.setLasttransactiontime(chargeDistribution
											.getReceiveTime());
							setPocketLimits(sourcePocket, amount);
							destinationPocket
									.setLasttransactiontime(chargeDistribution
											.getReceiveTime());
							setPocketLimits(destinationPocket, amount);

//							sourceSubcriberMdn.setLastTransactionID(chargeDistribution.getTransactionID());
//							sourceSubcriberMdn.setLastTransactionTime(chargeDistribution.getReceiveTime());

							pct.setTransactionchargeid(BigDecimal.valueOf(transactionChargeID));
							pct.setIspartofsharedupchain((short) (isPartOfSharedUpChain?1:0));
							if (chargeDistribution.getChannelCode() != null) {
								pct.setIso8583Merchanttype(chargeDistribution
										.getChannelCode());
							} else if (pct.getIso8583Merchanttype() == null) {
								pct.setIso8583Merchanttype(CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Other);
							}
								coreDataWrapper.save(sourceSubcriberMdn);
								
								if(ledgerService.isImmediateUpdateRequiredForPocket(sourcePocket)){
									coreDataWrapper.save(sourcePocket);
								}
								if(ledgerService.isImmediateUpdateRequiredForPocket(destinationPocket)){
									coreDataWrapper.save(destinationPocket);
								}								
							
								coreDataWrapper.save(pct);
							
								if (isNullorZero(returnFix.getInternalErrorCode())) {
								pct.setSourcepocketbalance(sourcePocket
										.getCurrentbalance());
								pct.setDestpocketbalance(destinationPocket
										.getCurrentbalance());
//								ledgerService.createLedgerEntry(sourcePocket,
//										suspensePocket, null, pct,
//										ms.add(amount, taxAmount));
//								ledgerService.createLedgerEntry(suspensePocket,
//										destinationPocket, null, pct, amount);
//								ledgerService.createLedgerEntry(suspensePocket,
//										taxPocket, null, pct, taxAmount);
								List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,chargeDistribution.getServiceChargeTransactionLogID(), pct.getId().longValue(),
										sourcePocket, destinationPocket, taxPocket, amount, taxAmount, ConfigurationUtil.getMfinoNettingLedgerEntries());
								coreDataWrapper.save(lstMfsLedgers);

									// pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
									// pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
									pct.setNotificationcode(Long.valueOf(NotificationCodes.ChargeDistributionCompleted
											.getNotificationCode()));

									coreDataWrapper.save(pct);
									if(ledgerService.isImmediateUpdateRequiredForPocket(sourcePocket)){
										coreDataWrapper.save(sourcePocket);
									}
									if(ledgerService.isImmediateUpdateRequiredForPocket(destinationPocket)){
										coreDataWrapper.save(destinationPocket);
									}
//									coreDataWrapper.save(suspensePocket);
//									coreDataWrapper.save(taxPocket);
									handlePCTonSuccess(pct);
									sctlStlmtMap
											.setStatus(CmFinoFIX.SettlementStatus_Completed);
								

								returnFix
										.setInternalErrorCode(NotificationCodes.ChargeDistributionCompleted
												.getInternalErrorCode());
								returnFix.setTransactionID(pct.getId().longValue());
								returnFix.setAmount(pct.getAmount());
								returnFix.setCurrency(pct.getCurrency());
								returnFix
										.setResult(CmFinoFIX.ResponseCode_Success);
								activitiesLog.setIssuccessful((short)1);
							}

						} else {
							pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountToBankAccountSourcePocketLimits));
							pct.setNotificationcode(Long.valueOf(NotificationCodes
									.getNotificationCodeFromInternalCode(returnFix
											.getInternalErrorCode())));
							pct.setEndtime(pct.getStarttime());
							pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
							coreDataWrapper.save(pct);

							handlePCTonFailure(pct);
							sctlStlmtMap
									.setStatus(CmFinoFIX.SettlementStatus_Failed);
						}
					} else {
						returnFix
								.setInternalErrorCode(NotificationCodes.EMoneytoEMoneyFailed_SelfTransfer
										.getInternalErrorCode());
					}
				}
			}
		}

		sctlStlmtDAO.save(sctlStlmtMap);

		returnFix.setSourceMDN(sourceSubcriberMdn.getMdn());
		returnFix.setSenderMDN(destinationSubcriberMdn.getMdn());
		returnFix.setLanguage((int)sourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(destinationSubcriberMdn.getMdn());
		returnFix.setReceiveTime(chargeDistribution.getReceiveTime());
		returnFix.setCustomerServiceShortCode(sourceSubscriber.getCompany()
				.getCustomerservicenumber());

		// write activities log
		if (sourceSubscriber.getCompany() != null) {
			activitiesLog.setCompany(sourcePocket.getCompany());
		}

		activitiesLog.setMsgtype(Long.valueOf(chargeDistribution.getMessageType()));
		activitiesLog.setParenttransactionid(BigDecimal.valueOf(chargeDistribution
				.getParentTransactionID()));
		activitiesLog.setServletpath(chargeDistribution.getServletPath());
		activitiesLog.setSourceapplication(Long.valueOf(chargeDistribution
				.getSourceApplication()));
		activitiesLog.setSourcemdn(chargeDistribution.getSourceMDN());
		activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes
				.getNotificationCodeFromInternalCode(returnFix
						.getInternalErrorCode())));
		activitiesLog.setSourcemdnid(sourceSubcriberMdn.getId());
		activitiesLog.setSourcesubscriberid(sourceSubscriber.getId());
		activitiesLog.setSourcepocketid(sourcePocket.getId());
		activitiesLog.setSourcepockettype(sourcePocket.getPocketTemplateByPockettemplateid()
				.getType());

		if (sourcePocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(sourcePocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}

		if (pct != null)
			activitiesLog.setTransferid(pct.getId());

		coreDataWrapper.save(activitiesLog);

		return returnFix;
	}
	

	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	public CFIXMsg onSettlementOfCharge(CMSettlementOfCharge settlementOfCharge) {

		log.info("BankServiceDefaultImpl :: onSettlementOfCharge BEGIN");

		BackendResponse returnFix = createResponseObject();

		BigDecimal amount = settlementOfCharge.getAmount();
//		Pocket suspensePocket = coreDataWrapper.getSuspensePocketWithLock();

		// locking not required for subscriber since it is not saved during
		// charge settlement
		Subscriber sourceSubscriber = coreDataWrapper
				.getSubscriberByMdn(settlementOfCharge.getSourceMDN());
		Subscriber destinationSubscriber = sourceSubscriber;
		SubscriberMdn sourceSubcriberMdn = coreDataWrapper
				.getSubscriberMdn(settlementOfCharge.getSourceMDN());
		SubscriberMdn destinationSubcriberMdn = sourceSubcriberMdn;
		Pocket sourcePocket = coreDataWrapper.getPocketById(
				settlementOfCharge.getSourcePocketID(), LockMode.UPGRADE);
		Pocket destinationPocket = coreDataWrapper.getPocketById(
				settlementOfCharge.getDestPocketID(), LockMode.UPGRADE);
		ActivitiesLog activitiesLog = new ActivitiesLog();

		PendingCommodityTransfer pct = null;
		CFIXMsg isoFix = null;

		log.info("BankServiceDefaultImpl :: onSettlementOfCharge sourceSubscriber="
				+ sourceSubscriber
				+ ", destinationSubscriber="
				+ destinationSubscriber
				+ ", sourceSubcriberMdn="
				+ sourceSubcriberMdn
				+ ", destinationSubcriberMdn="
				+ destinationSubcriberMdn
				+ ", sourcePocket="
				+ sourcePocket
				+ ", destinationPocket=" + destinationPocket);

		if (CmFinoFIX.PocketType_SVA.intValue() != sourcePocket
				.getPocketTemplateByPockettemplateid().getType()) {
			returnFix
					.setInternalErrorCode(NotificationCodes.DefaultBankAccountPocketNotFound
							.getInternalErrorCode());
		}

		if (isNullorZero(returnFix.getInternalErrorCode())) {
			returnFix = validationService.validateBankAccountSubscriber(
					sourceSubscriber, sourceSubcriberMdn, sourcePocket,
					"mFino260", true, false, false, false);

			log.info("BankServiceDefaultImpl:onSettlementOfCharge: Source subcriber validated ErrorCode="
					+ returnFix.getInternalErrorCode());

			if (isNullorZero(returnFix.getInternalErrorCode())) {
				returnFix = validationService.validateBankAccountSubscriber(
						destinationSubscriber, destinationSubcriberMdn,
						destinationPocket, "", false, false, false, false);

				log.info("BankServiceDefaultImpl:onSettlementOfCharge: Dest subcriber validated ErrorCode="
						+ returnFix.getInternalErrorCode());

				if (isNullorZero(returnFix.getInternalErrorCode())) {
					if (!(sourcePocket.getId()
							.equals(destinationPocket.getId()))) {

						pct = commodityTransferService
								.createPCT(
										settlementOfCharge,
										sourceSubscriber,
										destinationSubscriber,
										sourcePocket,
										destinationPocket,
										sourceSubcriberMdn,
										destinationSubcriberMdn,
										ServiceAndTransactionConstants.MESSAGE_CHARGE_SETTLEMENT,
										amount,
										null,
										CmFinoFIX.BucketType_Special_Bank_Account,
										CmFinoFIX.BillingType_None,
										CmFinoFIX.TransferStatus_Initialized);

						returnFix = validationService
								.validateRisksAndLimits(sourcePocket,
										destinationPocket, amount, amount, sourceSubcriberMdn, destinationSubcriberMdn);

						log.info("After ValidateRisksAndLimits returnFix.getInternalErrorCode()="
								+ returnFix.getInternalErrorCode());

						if (isNullorZero(returnFix.getInternalErrorCode())) {
//							sourcePocket
//									.setLastTransactionTime(settlementOfCharge
//											.getReceiveTime());
							setPocketLimits(sourcePocket, amount);
//							destinationPocket
//									.setLastTransactionTime(settlementOfCharge
//											.getReceiveTime());
							setPocketLimits(destinationPocket, amount);

//							sourceSubcriberMdn.setLastTransactionID(settlementOfCharge.getTransactionID());
//							sourceSubcriberMdn.setLastTransactionTime(settlementOfCharge.getReceiveTime());

							if (settlementOfCharge.getChannelCode() != null) {
								pct.setIso8583Merchanttype(settlementOfCharge
										.getChannelCode());
							} else if (pct.getIso8583Merchanttype() == null) {
								pct.setIso8583Merchanttype(CmFinoFIX.ISO8583_Mobile_Operator_Merchant_Type_Other);
							}
//								coreDataWrapper.save(sourceSubcriberMdn);

								if(ledgerService.isImmediateUpdateRequiredForPocket(sourcePocket)){
									coreDataWrapper.save(sourcePocket);
								}
								if(ledgerService.isImmediateUpdateRequiredForPocket(destinationPocket)){
									coreDataWrapper.save(destinationPocket);
								}							
							
								coreDataWrapper.save(pct);
							

							if (isNullorZero(returnFix.getInternalErrorCode())) {
								if ((sourcePocket.getPocketTemplateByPockettemplateid().getType()
										 == CmFinoFIX.PocketType_SVA
										.intValue())
										&& (destinationPocket
												.getPocketTemplateByPockettemplateid().getType()
												 == CmFinoFIX.PocketType_SVA
												.intValue())) {
									pct.setSourcepocketbalance(sourcePocket
											.getCurrentbalance());
									pct.setDestpocketbalance(destinationPocket
											.getCurrentbalance());
//									ledgerService.createLedgerEntry(
//											sourcePocket, suspensePocket, null,
//											pct, amount);
//									ledgerService.createLedgerEntry(
//											suspensePocket, destinationPocket,
//											null, pct, amount);
									List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(true,settlementOfCharge.getServiceChargeTransactionLogID(), 
											pct.getId().longValue(), sourcePocket, destinationPocket, null, amount, BigDecimal.ZERO, ConfigurationUtil.getMfinoNettingLedgerEntries());
									coreDataWrapper.save(lstMfsLedgers);
										// pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
										// pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
										pct.setNotificationcode(Long.valueOf(NotificationCodes.EMoneytoEMoneyCompleteToSender
												.getNotificationCode()));

										coreDataWrapper.save(pct);
										if(ledgerService.isImmediateUpdateRequiredForPocket(sourcePocket)){
											coreDataWrapper.save(sourcePocket);
										}
										if(ledgerService.isImmediateUpdateRequiredForPocket(destinationPocket)){
											coreDataWrapper.save(destinationPocket);
										}										

										returnFix
												.setResult(CmFinoFIX.ResponseCode_Success);

										handlePCTonSuccess(pct);
									
									returnFix.setTransferID(pct.getId().longValue());
									returnFix.setSourceMDNBalance(moneyService.round(new BigDecimal(sourcePocket.getCurrentbalance())));
									returnFix.setDestinationMDNBalance(moneyService.round(new BigDecimal(destinationPocket.getCurrentbalance())));
									returnFix.setInternalErrorCode(NotificationCodes.EMoneytoEMoneyCompleteToSender.getInternalErrorCode());
									activitiesLog.setIssuccessful((short)1);

								} else if ((sourcePocket.getPocketTemplateByPockettemplateid()
										.getType() == CmFinoFIX.PocketType_SVA
										.intValue())
										&& (destinationPocket
												.getPocketTemplateByPockettemplateid().getType()
												 == CmFinoFIX.PocketType_BankAccount
												.intValue())) {

									// objSrcPocket.setCurrentBalance(objSrcPocket.getCurrentBalance().subtract(transferAmountWithCharges));
									// suspensePocket.setCurrentBalance(suspensePocket.getCurrentBalance().add(transferAmountWithCharges));
									pct.setSourcepocketbalance(sourcePocket
											.getCurrentbalance());
//									ledgerService.createLedgerEntry(
//											sourcePocket, suspensePocket, null,
//											pct, amount);
									List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(true,settlementOfCharge.getServiceChargeTransactionLogID(),
											pct.getId().longValue(), sourcePocket, coreDataWrapper.getSuspensePocket(), null, amount, BigDecimal.ZERO, 
											ConfigurationUtil.getMfinoNettingLedgerEntries());
									coreDataWrapper.save(lstMfsLedgers);
									pct.setTransferstatus(CmFinoFIX.TransferStatus_MoneyTransaferSentToBank);
									pct.setBanksystemtraceauditnumber(""
											+ settlementOfCharge
													.getTransactionID());
									pct.setBankretrievalreferencenumber(""
											+ settlementOfCharge
													.getTransactionID());

									integrationSummaryService.logIntegrationSummary(settlementOfCharge.getServiceChargeTransactionLogID(), pct.getId().longValue(), "BANK", settlementOfCharge.getTransactionID().toString(), null, null, null,settlementOfCharge.getReceiveTime());

										coreDataWrapper.save(pct);
										if(ledgerService.isImmediateUpdateRequiredForPocket(sourcePocket)){
											coreDataWrapper.save(sourcePocket);
										}										

										/*
										 * Send ISO message to transfer amount,
										 * and rest of the logic in on
										 * confirmation from bank
										 */
										CMMoneyTransferToBank moneyTransferToBank = new CMMoneyTransferToBank();
										Timestamp ts = DateTimeUtil.getGMTTime();

										// moneyTransferToBank.copy(confirmationToBank);
										moneyTransferToBank.setSourceMDN(settlementOfCharge.getSourceMDN());
										moneyTransferToBank.setSourceMDNToUseForBank(coreDataWrapper.getPlatformMdn());
										moneyTransferToBank
												.setSourceApplication(settlementOfCharge
														.getSourceApplication());
										moneyTransferToBank.setAmount(pct
												.getAmount());
										moneyTransferToBank.setBankCode(pct
												.getBankcode().intValue());
										moneyTransferToBank.setDestCardPAN(pct
												.getDestcardpan());
										moneyTransferToBank.setDestMDN(pct
												.getDestmdn());
										moneyTransferToBank
												.setParentTransactionID(settlementOfCharge
														.getTransactionID());
										moneyTransferToBank.setUICategory(pct
												.getUicategory().intValue());
										moneyTransferToBank
												.setSourcePocketID(sourcePocket
														.getId().longValue());
										moneyTransferToBank
												.setDestPocketID(destinationPocket
														.getId().longValue());
										moneyTransferToBank
												.setPin(MCEUtil.FAKE_PIN_FOR_OMB);
										moneyTransferToBank.setTransferID(pct
												.getId().longValue());
										moneyTransferToBank.setTransferTime(ts);
										moneyTransferToBank
												.setTransactionID(settlementOfCharge
														.getTransactionID());
										moneyTransferToBank
												.setSourceBankAccountType(""
														+ CmFinoFIX.BankAccountType_Lakupandai);
										moneyTransferToBank
												.setSourceCardPAN(coreDataWrapper
														.getGlobalAccountNumber());
									    moneyTransferToBank
											.setMessageType(CmFinoFIX.MessageType_SettlementOfCharge);
									    moneyTransferToBank
											.setServiceChargeTransactionLogID(settlementOfCharge
													.getServiceChargeTransactionLogID());

//										if (CmFinoFIX.BankAccountCardType_SavingsAccount
//												.equals(sourcePocket
//														.getPocketTemplateByPockettemplateid()
//														.getBankAccountCardType())) {
//											moneyTransferToBank
//													.setSourceBankAccountType(""
//															+ CmFinoFIX.BankAccountType_Saving);
//										} else {
//											moneyTransferToBank
//													.setSourceBankAccountType(""
//															+ CmFinoFIX.BankAccountType_Checking);
//										}

										if (CmFinoFIX.BankAccountCardType_SavingsAccount
												.equals(destinationPocket
														.getPocketTemplateByPockettemplateid()
														.getBankaccountcardtype())) {
											moneyTransferToBank
													.setDestinationBankAccountType(""
															+ CmFinoFIX.BankAccountType_Saving);
										} else {
											moneyTransferToBank
													.setDestinationBankAccountType(""
															+ CmFinoFIX.BankAccountType_Checking);
										}

										isoFix = moneyTransferToBank;
									
								} else {
									// Revert Pocket Limits
									BackendUtil.revertPocketLimits(
											sourcePocket, amount, pct);
									BackendUtil.revertPocketLimits(
											destinationPocket, amount, pct);

									coreDataWrapper.save(destinationPocket);
									coreDataWrapper.save(sourcePocket);

									pct.setNotificationcode(Long.valueOf(NotificationCodes
											.getNotificationCodeFromInternalCode(returnFix
													.getInternalErrorCode())));
									pct.setEndtime(pct.getStarttime());
									pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);

									handlePCTonFailure(pct);
								}
							} else {
								// Revert Pocket Limits
								BackendUtil.revertPocketLimits(sourcePocket,
										amount, pct);
								BackendUtil.revertPocketLimits(
										destinationPocket, amount, pct);

								coreDataWrapper.save(destinationPocket);
								coreDataWrapper.save(sourcePocket);

								pct.setNotificationcode(Long.valueOf(NotificationCodes
										.getNotificationCodeFromInternalCode(returnFix
												.getInternalErrorCode())));
								pct.setEndtime(pct.getStarttime());
								pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);

								handlePCTonFailure(pct);
							}
						} else {
							pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountToBankAccountSourcePocketLimits));
							pct.setNotificationcode(Long.valueOf(NotificationCodes
									.getNotificationCodeFromInternalCode(returnFix
											.getInternalErrorCode())));
							pct.setEndtime(pct.getStarttime());
							pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);

							handlePCTonFailure(pct);
						}
					} else {
						returnFix
								.setInternalErrorCode(NotificationCodes.EMoneytoEMoneyFailed_SelfTransfer
										.getInternalErrorCode());
					}
				}
			}
		}

		returnFix.setSourceMDN(settlementOfCharge.getSourceMDN());
		// returnFix.setSenderMDN(destinationSubcriberMdn.getMDN());
		returnFix.setLanguage((int)sourceSubscriber.getLanguage());
		// returnFix.setReceiverMDN(destinationSubcriberMdn.getMDN());
		returnFix.setReceiveTime(settlementOfCharge.getReceiveTime());
		returnFix.setAmount(amount);
		returnFix.setSenderMDN(settlementOfCharge.getSourceMDN());
		returnFix.setReceiverMDN(settlementOfCharge.getSourceMDN());
		returnFix.setServiceChargeTransactionLogID(settlementOfCharge
				.getServiceChargeTransactionLogID());

		// write activities log
		if (sourceSubscriber.getCompany() != null) {
			activitiesLog.setCompany(sourcePocket.getCompany());
		}

		activitiesLog.setMsgtype(Long.valueOf(settlementOfCharge.getMessageType()));
		activitiesLog.setParenttransactionid(BigDecimal.valueOf(settlementOfCharge
				.getParentTransactionID()));
		activitiesLog.setServletpath(settlementOfCharge.getServletPath());
		activitiesLog.setSourceapplication(Long.valueOf(settlementOfCharge
				.getSourceApplication()));
		activitiesLog.setSourcemdn(settlementOfCharge.getSourceMDN());
		activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes
				.getNotificationCodeFromInternalCode(returnFix
						.getInternalErrorCode())));
		activitiesLog.setSourcemdnid(sourceSubcriberMdn.getId());
		activitiesLog.setSourcesubscriberid(sourceSubscriber.getId());
		activitiesLog.setSourcepocketid(sourcePocket.getId());
		activitiesLog.setSourcepockettype(sourcePocket.getPocketTemplateByPockettemplateid()
				.getType());

		if (sourcePocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(sourcePocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}

		if (pct != null)
			activitiesLog.setTransferid(pct.getId());

		coreDataWrapper.save(activitiesLog);

		if (isoFix != null) {
			return isoFix;
		}

		return returnFix;
	}
	
	public ValidationService getValidationService() {
		return validationService;
	}

	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}

	public CommodityTransferService getCommodityTransferService() {
		return commodityTransferService;
	}

	public void setCommodityTransferService(
			CommodityTransferService commodityTransferService) {
		this.commodityTransferService = commodityTransferService;
	}

	public LedgerService getLedgerService() {
		return ledgerService;
	}

	public void setLedgerService(LedgerService ledgerService) {
		this.ledgerService = ledgerService;
	}

	public IntegrationSummaryService getIntegrationSummaryService() {
		return integrationSummaryService;
	}

	public void setIntegrationSummaryService(
			IntegrationSummaryService integrationSummaryService) {
		this.integrationSummaryService = integrationSummaryService;
	}

	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}

	public void setSystemParametersService(
			SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}

	public CFIXMsg onCashInInquiry(CMCashInInquiry cashInInquiry) {
		log.info("BankServiceDefaultImpl :: onCashInInquiry BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(cashInInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			if(returnFix instanceof BackendResponse)
			{
			((BackendResponse) returnFix).copy(cashInInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
			return returnFix;
		}
		/*
		 * if(returnFix instanceof BackendResponse){ ((BackendResponse)
		 * returnFix
		 * ).setInternalErrorCode(NotificationCodes.CashInToEMoneyCompletedToSender
		 * .getInternalErrorCode());
		 * 
		 * if required you can write code here to update ct/pct notification
		 * code.
		 * 
		 * }
		 */

		return returnFix;
	}

	public CFIXMsg onCashIn(CMCashIn cashIn) {
		log.info("BankServiceDefaultImpl :: onCashIn BEGIN");
		CFIXMsg returnFix = onTransferConfirmationToBank(cashIn);

		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.CashInToEMoneyCompletedToSender
								.getInternalErrorCode());
			}
			/*
			 * if required you can write code here to update ct/pct notification
			 * code.
			 */
		}

		return returnFix;

	}

	public CFIXMsg onCashOutInquiry(CMCashOutInquiry cashOutInquiry) {
		log.info("BankServiceDefaultImpl :: onCashOutInquiry BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(cashOutInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			if(returnFix instanceof BackendResponse)
			((BackendResponse) returnFix).copy(cashOutInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		return returnFix;
	}

	public CFIXMsg onCashOut(CMCashOut cashOut) {
		log.info("BankServiceDefaultImpl :: onCashOut BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(cashOut);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.CashOutFromEMoneyCompletedToSender
								.getInternalErrorCode());
				((BackendResponse) returnFix).setPartnerCode(cashOut
						.getPartnerCode());
			}
			/*
			 * if required you can write code here to update ct/pct notification
			 * code.
			 */
		}

		return returnFix;
	}

	public CFIXMsg onCashOutInquiryForUnRegistered(
			CMCashOutInquiryForNonRegistered cashOutInquiry) {
		log.info("BankServiceDefaultImpl :: onCashOutInquiryForUnregistered BEGIN");
		cashOutInquiry
				.setUICategory(CmFinoFIX.TransactionUICategory_Cashout_To_UnRegistered);
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(cashOutInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(cashOutInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.CashOutToUnRegisteredConfirmationPrompt
								.getInternalErrorCode());
			}
		}

		return returnFix;
	}

	public CFIXMsg onCashOutForUnRegistered(CMCashOutForNonRegistered cashOut) {
		log.info("BankServiceDefaultImpl :: onCashOutForUnRegistered BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(cashOut);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.CashOutToUnRegisteredCompletedToSender
								.getInternalErrorCode());
				((BackendResponse) returnFix).setPartnerCode(cashOut
						.getPartnerCode());
			}
		}

		return returnFix;
	}

	public CFIXMsg onCashOutAtATMInquiry(CMCashOutAtATMInquiry cashOutInquiry) {
		log.info("BankServiceDefaultImpl :: onCashOutAtATMInquiry BEGIN");
		cashOutInquiry
				.setUICategory(CmFinoFIX.TransactionUICategory_Cashout_At_ATM);
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(cashOutInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(cashOutInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.CashOutAtATMConfirmationPrompt
								.getInternalErrorCode());
			}
		}

		return returnFix;
	}

	public CFIXMsg onCashOutAtATM(CMCashOutAtATM cashOut) {
		log.info("BankServiceDefaultImpl :: onCashOutAtATM BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(cashOut);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.CashOutAtATMConfirmedToSender
								.getInternalErrorCode());
			}
		}

		return returnFix;
	}

	public CFIXMsg onwithdrawFromATMInquiry(
			CMWithdrawFromATMInquiry withdrawInquiry) {
		log.info("BankServiceDefaultImpl :: onwithdrawFromATMInquiry BEGIN");
		withdrawInquiry
				.setUICategory(CmFinoFIX.TransactionUICategory_Withdraw_From_ATM);
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(withdrawInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(withdrawInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.BankAccountToBankAccountConfirmationPrompt
								.getInternalErrorCode());
			}
		}

		return returnFix;
	}

	public CFIXMsg onwithdrawFromATM(CMWithdrawFromATM withdraw) {
		log.info("BankServiceDefaultImpl :: onwithdrawFromATM BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(withdraw);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.SuccessfulCashOutFromATM
								.getInternalErrorCode());
			}
		}

		return returnFix;
	}

	public CFIXMsg onTransferInquiryForUnRegistered(
			CMTransferInquiryToNonRegistered tansferInquiry) {
		log.info("BankServiceDefaultImpl :: onTransferInquiryForUnRegistered BEGIN");
		Integer uiCategory = CmFinoFIX.TransactionUICategory_EMoney_Trf_To_UnRegistered;
		if (ServiceAndTransactionConstants.MESSAGE_SUB_BULK_TRANSFER
				.equals(tansferInquiry.getSourceMessage())) {
			uiCategory = CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer;
		}
		log.debug("Transactio UI category = " + uiCategory);
		tansferInquiry.setUICategory(uiCategory);
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(tansferInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(tansferInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.TransferToUnRegisteredConfirmationPrompt
								.getInternalErrorCode());
			}
		}

		return returnFix;
	}

	public CFIXMsg onTransferConfirmationForUnRegistered(
			CMTransferToNonRegistered transferConfirmation) {
		log.info("BankServiceDefaultImpl :: onTransferConfirmationForUnRegistered BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(transferConfirmation);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())
					&& !NotificationCodes.BulkTransferCompletedToSubscriber_Dummy
							.getInternalErrorCode().equals(
									((BackendResponse) returnFix)
											.getInternalErrorCode())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.TransferToUnRegisteredCompletedToSender
								.getInternalErrorCode());
			}
		}

		return returnFix;
	}

	@Override
	public CFIXMsg onPurchaseInquiry(CMPurchaseInquiry purchaseInquiry) {
		log.info("BankServiceDefaultImpl :: onPurchaseInquiry BEGIN");
		// change this based on source pocket type
		purchaseInquiry
				.setUICategory(CmFinoFIX.TransactionUICategory_EMoney_Purchase);
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(purchaseInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(purchaseInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		return returnFix;
	}

	@Override
	public CFIXMsg onPurchase(CMPurchase purchase) {
		log.info("BankServiceDefaultImpl :: onPurchase BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(purchase);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.PurchaseFromEMoneyCompletedToSender
								.getInternalErrorCode());
				((BackendResponse) returnFix).setPartnerCode(purchase
						.getPartnerCode());

				ServiceChargeTxnLog sctl = coreDataWrapper
						.getSCTLById(((BackendResponse) returnFix)
								.getServiceChargeTransactionLogID());
				((BackendResponse) returnFix).setInvoiceNumber(sctl
						.getInvoiceno());
			}
			/*
			 * if required you can write code here to update ct/pct notification
			 * code.
			 */
		}

		return returnFix;
	}

	@Override
	public CFIXMsg onReverseTransactionInquiry(
			CMReverseTransactionInquiry reverseTransactionInquiry) {
		log.info("BankServiceDefaultImpl :: onReverseTransactionInquiry BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(reverseTransactionInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(reverseTransactionInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		return returnFix;
	}

	@Override
	public CFIXMsg onReverseTransaction(CMReverseTransaction reverseTransaction) {
		log.info("BankServiceDefaultImpl :: onReverseTransaction BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(reverseTransaction);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.ReverseTransactionCompleteToSender
								.getInternalErrorCode());
				((BackendResponse) returnFix)
						.setOriginalReferenceID(reverseTransaction
								.getOriginalReferenceID());
			}
			/*
			 * if required you can write code here to update ct/pct notification
			 * code.
			 */
		}

		return returnFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onGetBankHistoryToBank(
			CMGetBankAccountTransactions getBankAccountTransations) {
		log.info("BankServiceDefaultImpl::onBalanceInquiry() Begin");
		CFIXMsg isoFix = null;
		BackendResponse responseFix = createResponseObject();
		responseFix.copy(getBankAccountTransations);

		ActivitiesLog activitiesLog = new ActivitiesLog();
		Subscriber subscriber = null;
		SubscriberMdn subscriberMdn = coreDataWrapper.getSubscriberMdn(
				getBankAccountTransations.getSourceMDN(), LockMode.UPGRADE);
		if (subscriberMdn != null) {
			subscriber = subscriberMdn.getSubscriber();
		}

		Pocket moneyPocket = coreDataWrapper
				.getPocketById(getBankAccountTransations.getPocketID());

		responseFix = validationService.validateBankAccountSubscriber(
				subscriber, subscriberMdn, moneyPocket,
				getBankAccountTransations.getPin(), true, false, false, false);

		log.info("BankServiceDefaultImpl:onGetBankHistory(): Source subscriber validation ErrorCode="
				+ responseFix.getInternalErrorCode());
		log.debug("BankServiceDefaultImpl:onGetBankHistory(): Source Subscriber Pocket Type="
				+ moneyPocket.getPocketTemplateByPockettemplateid().getType());

		if (isNullorZero(responseFix.getInternalErrorCode())) {
			// assuming that this pocket is a bank pocket or NFC Pocket
			if (moneyPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount) || 
				moneyPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_NFC)) {
				// Bank pocket or NFC Pocket
				CMGetLastTransactionsToBank toBankFix = new CMGetLastTransactionsToBank();
				toBankFix.copy(getBankAccountTransations);
				toBankFix.setLanguage((int)subscriber.getLanguage());
				toBankFix.setSourceMDN(subscriberMdn.getMdn());
				toBankFix.setPin(getBankAccountTransations.getPin());
				toBankFix.setBankCode(moneyPocket.getPocketTemplateByPockettemplateid()
						.getBankcode().intValue());
				toBankFix.setSourceCardPAN(moneyPocket.getCardpan());
				toBankFix.setCardAlias(moneyPocket.getCardalias());
				toBankFix.setPocketID(getBankAccountTransations.getPocketID());
				toBankFix.setMaxCount(getBankAccountTransations.getMaxCount());
				toBankFix.setFromDate(getBankAccountTransations.getFromDate());
				toBankFix.setToDate(getBankAccountTransations.getToDate());
				toBankFix.setPageNumber(getBankAccountTransations.getPageNumber());
				
				log.debug("BankServiceDefaultIpl : balanceinquiry :* transactionid="
						+ getBankAccountTransations.getTransactionID());
				toBankFix.setTransactionID(getBankAccountTransations
						.getTransactionID());
				toBankFix.setParentTransactionID(getBankAccountTransations
						.getParentTransactionID());
				if (CmFinoFIX.BankAccountCardType_SavingsAccount
						.equals(moneyPocket.getPocketTemplateByPockettemplateid()
								.getBankaccountcardtype())) {
					toBankFix.setSourceBankAccountType(""
							+ CmFinoFIX.BankAccountType_Saving);
				} else {
					toBankFix.setSourceBankAccountType(""
							+ CmFinoFIX.BankAccountType_Checking);
				}
				isoFix = toBankFix;
				activitiesLog
						.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountGetTransactionsPending
								.getNotificationCode()));
				log.info("toBankFix.getParentTransactionId()="
						+ toBankFix.getParentTransactionID());
			} else {
				responseFix
						.setInternalErrorCode(NotificationCodes.BankAccountGetTransactionsFailed
								.getInternalErrorCode());
				responseFix.setResult(CmFinoFIX.ResponseCode_Failure);
				activitiesLog
						.setNotificationcode(Long.valueOf(NotificationCodes.BankAccountGetTransactionsFailed
								.getNotificationCode()));
			}
		} else {
			activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes
					.getNotificationCodeFromInternalCode(responseFix
							.getInternalErrorCode())));
		}

		log.info("BankServiceDefaultImpl:onGetBankHistory **");

		responseFix.setSourceMDN(getBankAccountTransations.getSourceMDN());
		responseFix.setLanguage((int)subscriber.getLanguage());
		responseFix.setCustomerServiceShortCode(subscriber.getCompany()
				.getCustomerservicenumber());
		responseFix.setCurrency(subscriber.getCurrency());
		responseFix.setTransactionID(getBankAccountTransations
				.getTransactionID());

		// write Activity record
		activitiesLog.setMsgtype(Long.valueOf(responseFix.getMessageType()));
		activitiesLog.setParenttransactionid(BigDecimal.valueOf(getBankAccountTransations
				.getParentTransactionID()));
		activitiesLog
				.setServletpath(getBankAccountTransations.getServletPath());
		activitiesLog.setSourceapplication(Long.valueOf(getBankAccountTransations
				.getSourceApplication()));
		activitiesLog.setSourcemdn(subscriberMdn.getMdn());
		activitiesLog.setSourcemdnid(subscriberMdn.getId());
		activitiesLog.setSourcesubscriberid(subscriber.getId());
		activitiesLog.setSourcesubscriberid(subscriber.getCompany().getId());
		activitiesLog.setSourcepocketid(moneyPocket.getId());
		activitiesLog.setSourcepockettype(moneyPocket.getPocketTemplateByPockettemplateid()
				.getType());
		activitiesLog.setCompany(subscriber.getCompany());

		if (moneyPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(moneyPocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}

		log.debug("before save activities log BankServiceDefaultImpl:onGetBankHistory:: isoFix==null="
				+ (isoFix == null));
// [Bala] Commented the Activities log creation
//		coreDataWrapper.save(activitiesLog);

		log.debug("BankServiceDefaultImpl:onGetBankHistory():: isoFix==null="
				+ (isoFix == null));
//		log.info("activitiesLog.getParentTransactionId()="
//				+ activitiesLog.getParentTransactionID());

		if (isoFix != null) {
			return isoFix;
		}
		responseFix.setServiceChargeTransactionLogID(getBankAccountTransations
				.getServiceChargeTransactionLogID());
		return responseFix;

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onGetBankHistoryFromBank(CMGetLastTransactionsToBank toBank,
			CMGetLastTransactionsFromBank fromBank) {
		log.info("BankServiceDefaultImpl::onBalanceInquiryFromBank() Begin");

		log.info("toBank.getParentTransactionId()="
				+ toBank.getParentTransactionID());

		BackendResponse returnFix = createResponseObject();

		Subscriber subscriber = coreDataWrapper.getSubscriberByMdn(
				toBank.getSourceMDN());
//		SubscriberMDN subscriberMdn = coreDataWrapper.getSubscriberMdn(
//				toBank.getSourceMDN(), LockMode.UPGRADE);

		Pocket toBankPocket = coreDataWrapper.getPocketById(toBank
				.getPocketID());

		returnFix.setSourceMDN(toBank.getSourceMDN());
		returnFix.setLanguage((int)subscriber.getLanguage());
//[Bala] Commented because of no need to validate the subscriber again after getting the response from thirdparty.
//		returnFix = validationService.validateBankAccountSubscriber(subscriber,
//				subscriberMdn, toBankPocket, toBank.getPin(), true, false,
//				false, false);
//
//		log.info("BankServiceDefaultServiceImpl : onGetBankHistoryFromBank : validateBankAccountSubscriber "
//				+ returnFix.getInternalErrorCode());
//
//		if (!isNullorZero(returnFix.getInternalErrorCode())) {
//			return returnFix;
//		}

		if ((toBankPocket != null)
				&& (!isNullOrEmpty(toBankPocket.getCardpan()))
				&& !((toBank.getSourceCardPAN().equals(toBankPocket
						.getCardpan())))) {
			// error only log
			log.info("BankServiceDefaultImpl : onGetBankHistoryFromBank : account number mismatch");
			returnFix
					.setInternalErrorCode(NotificationCodes.InternalSystemError
							.getInternalErrorCode());
			returnFix
					.setDescription("OnBalanceInquiryFromBank - Response Account number does not match default pocket details");
			return returnFix;
		}
		log.info("BankServiceDefaultImpl : onGetBankHistoryFromBank  : Response from bank="
				+ fromBank.getResponseCode());
// [Bala] Commented the Activities log creation
//		ActivitiesLog activitiesLog = coreDataWrapper
//				.getActivitiesLogByParentTransactionId(toBank
//						.getParentTransactionID());
		if (!isNullOrEmpty(fromBank.getResponseCode())
				&& ResponseCodes.ISO_ResponseCode_Success
						.getExternalResponseCode().equals(
								fromBank.getResponseCode())) {

			returnFix
					.setInternalErrorCode(NotificationCodes.BankAccountTransactionDetails
							.getInternalErrorCode());

//			activitiesLog.setIsSuccessful(Boolean.TRUE);
//			activitiesLog
//					.setNotificationCode(NotificationCodes.BankAccountTransactionDetails
//							.getNotificationCode());

		} else {
			ResponseCodes rs = ResponseCodes.getResponseCodes(1,
					fromBank.getResponseCode());
			if (rs == ResponseCodes.bank_Failure) {
				returnFix.setDescription(ExternalResponseCodeHolder
						.getNotificationText(fromBank.getResponseCode()));
			}
			returnFix.setExternalResponseCode(rs.getExternalResponseCode());
			returnFix.setResult(CmFinoFIX.ResponseCode_Failure);

//			activitiesLog.setIsSuccessful(Boolean.FALSE);
//			activitiesLog.setNotificationCode(rs.getInternalErrorCode());
		}

//		coreDataWrapper.save(activitiesLog);

		returnFix.setSourceMDN(toBank.getSourceMDN());
		returnFix.setLanguage((int)subscriber.getLanguage());

		return returnFix;

	}

	/**
	 * For the case where moneytransfer was sent to bank and there was no
	 * response frontend would send the reversal to bank and there was a
	 * response to that request
	 */
	@Override
	public CFIXMsg onTransferReversalFromBank(CMMoneyTransferToBank requestFix,
			CMMoneyTransferReversalFromBank responseFix) {
		log.info(":: onTransferReversalFromBank() Begin");

		log.debug(":: onTransferReversalFromBank() request fix: "
				+ requestFix.DumpFields());
		log.debug(":: onTransferReversalFromBank() request fix: "
				+ responseFix.DumpFields());
		String responseCode = responseFix.getResponseCode();
		int response = CmFinoFIX.ISO8583_ResponseCode_Success
				.equals(responseCode) ? CmFinoFIX.ResponseCode_Success
				: CmFinoFIX.ResponseCode_Failure;

		BackendResponse returnFix = createResponseObject();
		returnFix.copy(requestFix);

		PendingCommodityTransfer pct = coreDataWrapper.getPCTById(requestFix
				.getTransferID());
		returnFix.setTransferID(requestFix.getTransferID());

		pct.setBankreversalresponsetime(responseFix.getReceiveTime());
		pct.setBankreversalresponsecode(Long.valueOf(Integer.getInteger(responseCode)));

		if (responseFix.getErrorText() != null) {
			pct.setBankreversalerrortext(responseFix.getErrorText());
		}

		if (response != CmFinoFIX.ResponseCode_Success) {
			pct.setBankreversalrejectreason(responseCode);
		}

		if (responseFix.getAIR() != null) {
			pct.setBankreversalauthorizationcode(responseFix.getAIR());
		}

		if (response == CmFinoFIX.ResponseCode_Success) {
			// Revert the transactions
			returnFix = (BackendResponse) onRevertOfTransferConfirmation(pct,
					true);
			returnFix.copy(requestFix);
		} else {

			returnFix.setResult(CmFinoFIX.ResponseCode_Failure);
			ResponseCodes rs = ResponseCodes.getResponseCodes(1, responseCode);
			if (rs == ResponseCodes.bank_Failure) {
				returnFix.setDescription(ExternalResponseCodeHolder
						.getNotificationText(responseCode));
			}
			returnFix.setExternalResponseCode(rs.getExternalResponseCode());
			returnFix.setResult(CmFinoFIX.ResponseCode_Failure);

			// Keep the transaction in pending state
			returnFix
					.setInternalErrorCode(NotificationCodes.BankAccountToBankAccountPending
							.getInternalErrorCode());
		}
		returnFix.setServiceChargeTransactionLogID(requestFix
				.getServiceChargeTransactionLogID());
		return returnFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onRevertOfTransferInquiry(
			PendingCommodityTransfer pendingTransfer, boolean updatePCT) {
		log.info(" :: OnRevertOfTransferInquiry() Begin");
		if (pendingTransfer == null) {
			return null;
		}
		if (pendingTransfer.getPocket() == null) {
			return null;
		}
		/*
		Pocket objSrcPocket = pendingTransfer.getPocketBySourcePocketID();
		SubscriberMDN objSrcSubMdn = pendingTransfer
				.getSubscriberMDNBySourceMDNID();
		Subscriber objSourceSubscriber = pendingTransfer
				.getSubscriberBySourceSubscriberID();
		 */
		
		/*Fetching the record before modifying it and commit when all updates are done, to avoid Stale Object exceptions 
		 when cleaning up of pending transfers,in which same subscriber is involved in multiple transactions*/
		SystemParameters dummySubMdnParam = coreDataWrapper.getSystemParameterByName(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
		String dummySubMdn = (dummySubMdnParam != null) ? dummySubMdnParam.getParametervalue() : null;		
		Pocket objSrcPocket = coreDataWrapper.getPocketById(pendingTransfer.getPocket().getId().longValue(), LockMode.UPGRADE);
		SubscriberMdn objSrcSubMdn = coreDataWrapper.getSubscriberMdn(pendingTransfer.getSourcemdn());
		Subscriber objSourceSubscriber = objSrcSubMdn.getSubscriber();
		
		SubscriberMdn objDestSubMdn = coreDataWrapper.getSubscriberMdn(pendingTransfer.getDestmdn());
		Subscriber objDestSubscriber = objDestSubMdn.getSubscriber();
		Pocket objDestPocket = null;
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubscriber.getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			objDestPocket = coreDataWrapper.getPocketById(
					pendingTransfer.getDestpocketid().longValue(), LockMode.UPGRADE);
		} else {
			objDestPocket = coreDataWrapper.getPocketById(pendingTransfer.getDestpocketid().longValue());
		}

		BigDecimal amount = pendingTransfer.getAmount();
		BigDecimal transferAmountWithCharges = amount.add(pendingTransfer
				.getCharges());
		Long txnID = pendingTransfer.getTransactionLog().getId().longValue();
		Long pendingTransferID = pendingTransfer.getId().longValue();
		Timestamp timeStamp = new Timestamp();

		BackendResponse returnFix = createResponseObject();
		returnFix.setSourceMDN(objSrcSubMdn.getMdn());
		returnFix.setSenderMDN(objSrcSubMdn.getMdn());
		returnFix.setLanguage((int)objSourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(objDestSubMdn.getMdn());
		returnFix.setTransactionID(txnID);
		returnFix.setTransferID(pendingTransferID);
		returnFix.setParentTransactionID(txnID);
		returnFix.setReceiveTime(timeStamp);
		// FIXME:: Check if new message type is needed here
		returnFix.setMessageType((int)pendingTransfer.getMsgtype());

		// Reverting Pocket Limits
		BackendUtil.revertPocketLimits(objSrcPocket, transferAmountWithCharges,
				pendingTransfer);
		
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubMdn.getSubscriber().getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			BackendUtil.revertPocketLimits(objDestPocket, amount, pendingTransfer);
		}		
		if(ledgerService.isImmediateUpdateRequiredForPocket(objSrcPocket)){
		coreDataWrapper.save(objSrcPocket);
		}
		
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubMdn.getSubscriber().getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			if(ledgerService.isImmediateUpdateRequiredForPocket(objDestPocket)){
				coreDataWrapper.save(objDestPocket);
			}
		}
		log.info(":: OnRevertOfTransferInquiryToBank() Reverted Pocket Limits: "
				+ pendingTransferID);

		if (updatePCT) {
			// pendingTransfer.setNotificationCode(NotificationCodes.Resolve_Transaction_To_Fail.getNotificationCode());
			pendingTransfer.setTransferstatus((short)2);
		}

		returnFix.setResult(CmFinoFIX.ResponseCode_Revert_Success);
		returnFix.setInternalErrorCode(NotificationCodes
				.getInternalErrorCodeFromNotificationCode(pendingTransfer
						.getNotificationcode().intValue()));

		ActivitiesLog activitiesLog = new ActivitiesLog();
		activitiesLog
				.setNotificationcode(pendingTransfer.getNotificationcode());
		// write Activity record
		activitiesLog.setMsgtype(Long.valueOf(returnFix.getMessageType()));
		activitiesLog.setParenttransactionid(BigDecimal.valueOf(txnID));

		activitiesLog.setSourcemdn(objSrcSubMdn.getMdn());
		activitiesLog.setSourcemdnid(objSrcSubMdn.getId());
		activitiesLog.setSourcesubscriberid(objSrcSubMdn.getId());
		// activitiesLog.setCompany(objSourceSubscriber.getCompany().getID());
		activitiesLog.setSourcepocketid(objSrcPocket.getId());
		activitiesLog.setSourcepockettype(objSrcPocket.getPocketTemplateByPockettemplateid()
				.getType());
		activitiesLog.setTransferid(pendingTransfer.getId());

		activitiesLog.setCompany(objSourceSubscriber.getCompany());
		if (objSrcPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(objSrcPocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}
		pendingTransfer.setLocalbalancerevertrequired((short)0);
		if (updatePCT) {
			coreDataWrapper.save(pendingTransfer);
			handlePCTonFailure(pendingTransfer);
		}
// [Bala] Commented the Activities log creation		
//		coreDataWrapper.save(activitiesLog);

		log.info(":: OnRevertOfTransferInquiry() completed and moved to ct with status failed for pct: "
				+ pendingTransferID);
		return returnFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onRevertOfTransferConfirmation(
			PendingCommodityTransfer pendingTransfer, boolean updatePCT) {
		log.info(" :: onRevertOfTransferConfirmation() Begin");
		if (pendingTransfer == null) {
			return null;
		}

		Pocket objSrcPocket = coreDataWrapper.getPocketById(pendingTransfer.getPocket().getId().longValue(), LockMode.UPGRADE);
		SubscriberMdn objSrcSubMdn = pendingTransfer
				.getSubscriberMdn();
		Subscriber objSourceSubscriber = pendingTransfer
				.getSubscriber();

		SubscriberMdn objDestSubMdn = coreDataWrapper
				.getSubscriberMdn(pendingTransfer.getDestmdn());
		SystemParameters dummySubMdnParam = coreDataWrapper.getSystemParameterByName(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
		String dummySubMdn = (dummySubMdnParam != null) ? dummySubMdnParam.getParametervalue() : null;		
		
		Pocket objDestPocket = null;
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubMdn.getSubscriber().getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			objDestPocket = coreDataWrapper.getPocketById(
					pendingTransfer.getDestpocketid().longValue(), LockMode.UPGRADE);
		} else {
			objDestPocket = coreDataWrapper.getPocketById(pendingTransfer.getDestpocketid().longValue());
		}		

		BigDecimal amount = pendingTransfer.getAmount();
		BigDecimal transferAmountWithCharges = amount.add(pendingTransfer
				.getCharges());
		Long txnID = pendingTransfer.getTransactionLog()
				.getId().longValue();
		Long pendingTransferID = pendingTransfer.getId().longValue();
		Timestamp timeStamp = new Timestamp();

		BackendResponse returnFix = createResponseObject();
		returnFix.setSourceMDN(objSrcSubMdn.getMdn());
		returnFix.setSenderMDN(objSrcSubMdn.getMdn());
		returnFix.setLanguage((int)objSourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(objDestSubMdn.getMdn());
		returnFix.setTransactionID(txnID);
		returnFix.setTransferID(pendingTransferID);
		returnFix.setParentTransactionID(txnID);
		returnFix.setReceiveTime(timeStamp);
		returnFix.setMessageType((int)pendingTransfer.getMsgtype());
		returnFix.setAmount(transferAmountWithCharges);
		// Reverting Pocket Limits
		BackendUtil.revertPocketLimits(objSrcPocket, transferAmountWithCharges,
				pendingTransfer);
		
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubMdn.getSubscriber().getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			BackendUtil.revertPocketLimits(objDestPocket, amount, pendingTransfer);
		}
		if(ledgerService.isImmediateUpdateRequiredForPocket(objSrcPocket)){
		coreDataWrapper.save(objSrcPocket);
		}
		if ( (CmFinoFIX.SubscriberType_Partner.intValue() != objDestSubMdn.getSubscriber().getType()) &&
				!(objDestSubMdn.getMdn().equals(dummySubMdn)) ) {
			if(ledgerService.isImmediateUpdateRequiredForPocket(objDestPocket)){
				coreDataWrapper.save(objDestPocket);
			}   
		}
		
//		List<Ledger> ledgerEntries = coreDataWrapper
//				.getLedgerEntriesByTransferID(pendingTransfer.getID());
//		for (Ledger ledger : ledgerEntries) {
//			Pocket srcPocket = coreDataWrapper.getPocketById(
//					ledger.getDestPocketID(), LockMode.UPGRADE);
//			Pocket destPocket = coreDataWrapper.getPocketById(
//					ledger.getSourcePocketID(), LockMode.UPGRADE);
//			ledgerService.createLedgerEntry(srcPocket, destPocket, null,
//					pendingTransfer, ledger.getAmount());
//			coreDataWrapper.save(srcPocket);
//			coreDataWrapper.save(destPocket);
//		}
		List<MfsLedger> lstMfsLedgers = coreDataWrapper.getLedgerEntriesByCommodityTransferId(pendingTransferID);
		if (CollectionUtils.isNotEmpty(lstMfsLedgers)) {
			for (MfsLedger mfsLedger: lstMfsLedgers) {
				MfsLedger reverseLedger = ledgerService.generateReverseLedgerEntry(mfsLedger);
				coreDataWrapper.save(reverseLedger);
		}
	}
		returnFix.setResult(CmFinoFIX.ResponseCode_Revert_Success);
		returnFix
				.setInternalErrorCode(NotificationCodes.Resolve_Transaction_To_Fail
						.getInternalErrorCode());

		ActivitiesLog activitiesLog = new ActivitiesLog();
		activitiesLog
				.setNotificationcode(pendingTransfer.getNotificationcode());
		// write Activity record
		activitiesLog.setMsgtype(Long.valueOf(returnFix.getMessageType()));
		activitiesLog.setParenttransactionid(BigDecimal.valueOf(txnID));

		activitiesLog.setSourcemdn(objSrcSubMdn.getMdn());
		activitiesLog.setSourcemdnid(objSrcSubMdn.getId());
		activitiesLog.setSourcesubscriberid(objSrcSubMdn.getId());
		// activitiesLog.setCompany(objSourceSubscriber.getCompany().getID());
		activitiesLog.setSourcepocketid(objSrcPocket.getId());
		activitiesLog.setSourcepockettype(objSrcPocket.getPocketTemplateByPockettemplateid()
				.getType());
		activitiesLog.setTransferid(pendingTransfer.getId());

		activitiesLog.setCompany(objSourceSubscriber.getCompany());
		if (objSrcPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(objSrcPocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}
		pendingTransfer.setLocalbalancerevertrequired((short)0);
		pendingTransfer.setLocalbalancerevertrequired((short)0);
		if (updatePCT) {
			// pendingTransfer.setNotificationCode(NotificationCodes.Resolve_Transaction_To_Fail.getNotificationCode());
			pendingTransfer.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
			coreDataWrapper.save(pendingTransfer);
			handlePCTonFailure(pendingTransfer);
		}
// [Bala] Commented the Activities log creation		
//		coreDataWrapper.save(activitiesLog);

		log.info(":: onRevertOfTransferConfirmation() completed and moved to ct with status failed for pct: "
				+ pendingTransferID);
		return returnFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onResolveCompleteOfTransfer(
			PendingCommodityTransfer pendingTransfer) {
		log.info(" :: onResolveCompleteOfTransfer() Begin");
		if (pendingTransfer == null) {
			return null;
		}

//		Pocket suspensePocket = coreDataWrapper.getSuspensePocketWithLock();
//		Pocket globalSVAPocket = coreDataWrapper.getGlobalSVAPocketWithLock();
//		Pocket chargesPocket = coreDataWrapper.getChargesPocketWithLock();

		Pocket objSrcPocket = pendingTransfer.getPocket();
		SubscriberMdn objSrcSubMdn = pendingTransfer
				.getSubscriberMdn();
		Subscriber objSourceSubscriber = pendingTransfer
				.getSubscriber();

		Pocket objDestPocket = coreDataWrapper.getPocketById(
				pendingTransfer.getDestpocketid().longValue(), LockMode.UPGRADE);
		SubscriberMdn objDestSubMdn = coreDataWrapper
				.getSubscriberMdn(pendingTransfer.getDestmdn());

		BigDecimal amount = pendingTransfer.getAmount();
		BigDecimal charges = pendingTransfer.getCharges();
		Long txnID = pendingTransfer.getTransactionLog()
				.getId().longValue();
		Long pendingTransferID = pendingTransfer.getId().longValue();
		Timestamp timeStamp = new Timestamp();
		Long sctlId = coreDataWrapper.getSCTLIdByCommodityTransferId(pendingTransferID);

		BackendResponse returnFix = createResponseObject();
		returnFix.setSourceMDN(objSrcSubMdn.getMdn());
		returnFix.setSenderMDN(objSrcSubMdn.getMdn());
		returnFix.setLanguage((int)objSourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(objDestSubMdn.getMdn());
		returnFix.setTransactionID(txnID);
		returnFix.setParentTransactionID(txnID);
		returnFix.setTransferID(pendingTransferID);
		returnFix.setReceiveTime(timeStamp);
		returnFix.setAmount(amount);
		// FIXME:: Check if new message type is needed here
		returnFix.setMessageType((int)pendingTransfer.getMsgtype());

		if ((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
				==(CmFinoFIX.PocketType_SVA))
				&& (objDestPocket.getPocketTemplateByPockettemplateid().getType()
						==(CmFinoFIX.PocketType_BankAccount))) {

//			ledgerService.createLedgerEntry(suspensePocket, chargesPocket,
//					null, pendingTransfer, charges);
//			ledgerService.createLedgerEntry(suspensePocket, globalSVAPocket,
//					null, pendingTransfer, amount);
			List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,sctlId, pendingTransferID, coreDataWrapper.getSuspensePocket(), 
					coreDataWrapper.getGlobalSVAPocket(), coreDataWrapper.getChargesPocket(), amount, charges, ConfigurationUtil.getMfinoNettingLedgerEntries());
			coreDataWrapper.save(lstMfsLedgers);
//			coreDataWrapper.save(suspensePocket);
//			if (charges.compareTo(BigDecimal.ZERO) == 1) {
//				coreDataWrapper.save(chargesPocket);
//			}
//			coreDataWrapper.save(globalSVAPocket);
			log.info(":: onResolveCompleteOfTransfer() Completed SVA to Bank transfer: "
					+ pendingTransferID);
		}
		if ((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
				==(CmFinoFIX.PocketType_BankAccount))
				&& (objDestPocket.getPocketTemplateByPockettemplateid().getType()
						==(CmFinoFIX.PocketType_SVA))) {
			pendingTransfer.setDestpocketbalance(objDestPocket
					.getCurrentbalance());
//			ledgerService.createLedgerEntry(suspensePocket, chargesPocket,
//					null, pendingTransfer, charges);
//			ledgerService.createLedgerEntry(suspensePocket, objDestPocket,
//					null, pendingTransfer, amount);
			List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,sctlId, pendingTransferID, coreDataWrapper.getSuspensePocket(), objDestPocket, 
					coreDataWrapper.getChargesPocket(), amount, charges, ConfigurationUtil.getMfinoNettingLedgerEntries());
//			coreDataWrapper.save(suspensePocket);
//			if (charges.compareTo(BigDecimal.ZERO) == 1) {
//				coreDataWrapper.save(chargesPocket);
//			}
			
			if(ledgerService.isImmediateUpdateRequiredForPocket(objDestPocket)){
			coreDataWrapper.save(objDestPocket);
			}
			coreDataWrapper.save(lstMfsLedgers);
			log.info(":: onResolveCompleteOfTransfer() Completed Bank to SVA transfer: "
					+ pendingTransferID);
		}
		if ((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
				==(CmFinoFIX.PocketType_SVA))
				&& (objDestPocket.getPocketTemplateByPockettemplateid().getType()
						==(CmFinoFIX.PocketType_SVA))) {
			log.info(":: onResolveCompleteOfTransfer() SVA to SVA transfer: "
					+ pendingTransferID);
			log.info("Bug in the System Please resolve the above transaction manually... :) :) :)");
			// Should not reach this point.
		}

		// pendingTransfer.setNotificationCode(NotificationCodes.Resolve_Transaction_To_Success.getNotificationCode());
		// pendingTransfer.setTransferStatus(CmFinoFIX.TransferStatus_Completed);

		returnFix.setResult(CmFinoFIX.ResponseCode_Revert_Success);
		returnFix
				.setInternalErrorCode(NotificationCodes.Resolve_Transaction_To_Success
						.getInternalErrorCode());
		returnFix.setResult(CmFinoFIX.ResponseCode_Success);
		ActivitiesLog activitiesLog = new ActivitiesLog();
		activitiesLog
				.setNotificationcode(Long.valueOf(NotificationCodes.Resolve_Transaction_To_Success
						.getNotificationCode()));
		// write Activity record
		activitiesLog.setMsgtype(Long.valueOf(returnFix.getMessageType()));
		activitiesLog.setParenttransactionid(BigDecimal.valueOf(txnID));

		activitiesLog.setSourcemdn(objSrcSubMdn.getMdn());
		activitiesLog.setSourcemdnid(objSrcSubMdn.getId());
		activitiesLog.setSourcesubscriberid(objSrcSubMdn.getId());
		// activitiesLog.setCompany(objSourceSubscriber.getCompany().getID());
		activitiesLog.setSourcepocketid(objSrcPocket.getId());
		activitiesLog.setSourcepockettype(objSrcPocket.getPocketTemplateByPockettemplateid()
				.getType());
		activitiesLog.setTransferid(pendingTransfer.getId());

		activitiesLog.setCompany(objSourceSubscriber.getCompany());
		if (objSrcPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(objSrcPocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}
		coreDataWrapper.save(pendingTransfer);
		handlePCTonSuccess(pendingTransfer);
// [Bala] Commented the Activities log creation		
//		coreDataWrapper.save(activitiesLog);

		log.info(":: onResolveCompleteOfTransfer() completed and moved to ct with status success for pct: "
				+ pendingTransferID);
		return returnFix;
	}

	/**
	 * This method takes care of reversals of any completed transactions,
	 * Currently it supports only E-Money to E-Money transactions.
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onTransactionReversal(
			CMTransactionReversal transactionReversal) {
		log.info("BankServiceDefaultImpl :: onTransactionReversal() BEGIN");

		BackendResponse returnFix = createResponseObject();

		boolean isSystemInitiatedTransaction = true;

		ActivitiesLog activitiesLog = new ActivitiesLog();
		PendingCommodityTransfer pct = coreDataWrapper
				.getPCTById(transactionReversal.getTransferID());

		// locking of subsriber and subscriberMDN records is not needed for
		// system initiated transactions
		Subscriber objSourceSubscriber = coreDataWrapper
				.getSubscriberByMdn(transactionReversal.getSourceMDN());
		Subscriber objDestSubscriber = coreDataWrapper
				.getSubscriberByMdn(transactionReversal.getDestMDN());

		SubscriberMdn objSrcSubMdn = coreDataWrapper
				.getSubscriberMdn(transactionReversal.getSourceMDN());
		SubscriberMdn objDestSubMdn = coreDataWrapper
				.getSubscriberMdn(transactionReversal.getDestMDN());

		Pocket objSrcPocket = coreDataWrapper.getPocketById(
				transactionReversal.getSourcePocketID(), LockMode.UPGRADE);
		Pocket objDestPocket = coreDataWrapper.getPocketById(
				transactionReversal.getDestPocketID(), LockMode.UPGRADE);

//		Pocket suspensePocket = coreDataWrapper.getSuspensePocketWithLock();
//		Pocket chargesPocket = coreDataWrapper.getChargesPocketWithLock();
//		Pocket globalSVAPocket = coreDataWrapper.getGlobalSVAPocketWithLock();

		BigDecimal amount = pct.getAmount();
		BigDecimal charges = pct.getCharges();
//		BigDecimal transferAmountWithCharges = amount.add(charges);

		if (((objSrcPocket.getPocketTemplateByPockettemplateid().getType()
				==(CmFinoFIX.PocketType_SVA)))
				&& ((objDestPocket.getPocketTemplateByPockettemplateid().getType()
						==(CmFinoFIX.PocketType_SVA)))) {
			returnFix = validationService.validateBankAccountSubscriber(
					objSourceSubscriber, objSrcSubMdn, objSrcPocket,
					"mFino260", true, false, false, false,
					isSystemInitiatedTransaction);

			if (isNullorZero(returnFix.getInternalErrorCode())) {
				returnFix = validationService.validateBankAccountSubscriber(
						objDestSubscriber, objDestSubMdn, objDestPocket, "",
						false, false, false, false,
						isSystemInitiatedTransaction);

				log.info("BankServiceDefaultImpl:onTransferConfirmationToBank: Dest subcriber validated ErrorCode="
						+ returnFix.getInternalErrorCode());

				if (isNullorZero(returnFix.getInternalErrorCode())) {
//					ledgerService.createLedgerEntry(objSrcPocket,
//							suspensePocket, null, pct, amount);
//					if (pct.getCharges().compareTo(BigDecimal.valueOf(0)) == 1) {
//						ledgerService.createLedgerEntry(chargesPocket,
//								suspensePocket, null, pct, charges);
//					}
//					ledgerService
//							.createLedgerEntry(suspensePocket, objDestPocket,
//									null, pct, transferAmountWithCharges);
					List<MfsLedger> lstMfsLedgers = ledgerService.createLedgerEntries(false,transactionReversal.getServiceChargeTransactionLogID(), 
							pct.getId().longValue(), objSrcPocket, objDestPocket, coreDataWrapper.getChargesPocket(), amount, charges, ConfigurationUtil.getMfinoNettingLedgerEntries());

					try {
						pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
						coreDataWrapper.save(pct);
//						coreDataWrapper.save(suspensePocket);
						if(ledgerService.isImmediateUpdateRequiredForPocket(objSrcPocket)){
							coreDataWrapper.save(objSrcPocket);
						}
						if(ledgerService.isImmediateUpdateRequiredForPocket(objDestPocket)){
						coreDataWrapper.save(objDestPocket);
						}
						coreDataWrapper.save(lstMfsLedgers);
//						if (pct.getCharges().compareTo(BigDecimal.valueOf(0)) == 1) {
//							coreDataWrapper.save(chargesPocket);
//						}

						returnFix.setSourceMDNBalance(moneyService.round(new BigDecimal(objSrcPocket.getCurrentbalance())));
						returnFix.setDestinationMDNBalance(moneyService.round(new BigDecimal(objDestPocket.getCurrentbalance())));
						returnFix.setResult(CmFinoFIX.ResponseCode_Success);

						handlePCTonFailure(pct);
					} catch (Exception error) {
						returnFix.setResult(CmFinoFIX.ResponseCode_Failure);
						log.error(
								"BankServiceDefaultImpl :: onTransferConfirmationToBank : DB Error "
										+ error.getMessage(), error);
					}

					returnFix
							.setInternalErrorCode(NotificationCodes.ReverseTransactionCompleteToSender
									.getInternalErrorCode());
					activitiesLog.setIssuccessful((short)0);
				}
			}
		} else {
			// FIXME set approriate notification
			throw new RuntimeException(
					"Reversal supported for only E-Money to E-Money");
		}

		returnFix.setSourceMDN(objSrcSubMdn.getMdn());
		returnFix.setSenderMDN(objDestSubMdn.getMdn());
		returnFix.setLanguage((int)objSourceSubscriber.getLanguage());
		returnFix.setReceiverMDN(objDestSubMdn.getMdn());
		returnFix.setReceiveTime(transactionReversal.getReceiveTime());

		if (objSourceSubscriber.getCompany() != null) {
			activitiesLog.setCompany(objSrcPocket.getCompany());
		}

		activitiesLog.setMsgtype(Long.valueOf(transactionReversal.getMessageType()));
		activitiesLog.setParenttransactionid(BigDecimal.valueOf(transactionReversal
				.getParentTransactionID()));
		activitiesLog.setServletpath(transactionReversal.getServletPath());
		activitiesLog.setSourceapplication(Long.valueOf(transactionReversal
				.getSourceApplication()));
		activitiesLog.setSourcemdn(transactionReversal.getSourceMDN());
		activitiesLog.setNotificationcode(Long.valueOf(NotificationCodes
				.getNotificationCodeFromInternalCode(returnFix
						.getInternalErrorCode())));
		activitiesLog.setSourcemdnid(objSrcSubMdn.getId());
		activitiesLog.setSourcesubscriberid(objSourceSubscriber.getId());
		activitiesLog.setSourcepocketid(objSrcPocket.getId());
		activitiesLog.setSourcepockettype(objSrcPocket.getPocketTemplateByPockettemplateid()
				.getType());

		if (objSrcPocket.getPocketTemplateByPockettemplateid().getBankcode() != null) {
			activitiesLog.setIso8583Acquiringinstidcode(objSrcPocket
					.getPocketTemplateByPockettemplateid().getBankcode());
		}

		if (pct != null)
			activitiesLog.setTransferid(pct.getId());

		coreDataWrapper.save(activitiesLog);

		returnFix.setServiceChargeTransactionLogID(transactionReversal
				.getServiceChargeTransactionLogID());

		log.info("BankServiceDefaultImpl :: onTransactionReversal() END");
		return returnFix;
	}
	
	// @Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onTransferReversalToBank(CMMoneyTransferToBank requestFix,
			NoISOResponseMsg responseFix) {
		log.info("sending reversal to bank for request "
				+ requestFix.getTransferID());

		ConversionToReversalRequestProcessor proc = new ConversionToReversalRequestProcessor();
		return proc.processMessage(requestFix, responseFix);

		// ReversalMessage msg = new ReversalMessage();
		// msg.setRequest(responseFix);

		// return requestFix;

	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onTransferReversalToBank(
			CMBankAccountToBankAccountConfirmation requestFix,
			CMMoneyTransferReversalToBank responseFix) {
		log.info("sending reversal to bank for request "
				+ requestFix.getTransferID());
		return responseFix;
	}

	@Override
	public CFIXMsg onAgentToAgentTransferInquiry(
			CMAgentToAgentTransferInquiry agentToAgentTransferInquiry) {
		log.info("BankServiceDefaultImpl :: onAgentToAgentTransferInquiry BEGIN");
		CFIXMsg returnFix = createResponseObject();
		try{
		returnFix  = onTransferInquiryToBank(agentToAgentTransferInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(agentToAgentTransferInquiry);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		log.info("BankServiceDefaultImpl :: onAgentToAgentTransferInquiry END");
		return returnFix;
	}

	@Override
	public CFIXMsg onAgentToAgentTransfer(
			CMAgentToAgentTransfer agentToAgentTransfer) {
		log.info("BankServiceDefaultImpl :: onAgentToAgentTransfer BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(agentToAgentTransfer);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if (returnFix instanceof BackendResponse) {
			if (CmFinoFIX.ResponseCode_Success
					.equals(((BackendResponse) returnFix).getResult())) {
				((BackendResponse) returnFix)
						.setInternalErrorCode(NotificationCodes.AgentToAgentTransferCompletedToSender
								.getInternalErrorCode());
				((BackendResponse) returnFix)
						.setPartnerCode(agentToAgentTransfer.getPartnerCode());
			}
			/*
			 * if required you can write code here to update ct/pct notification
			 * code.
			 */
		}
		log.info("BankServiceDefaultImpl :: onAgentToAgentTransfer END");
		return returnFix;
	}

	/**
	 * Handles any operation to be done on PendingCommodityTransfer record,
	 * during failure.
	 * 
	 * Default implementation moves the record from PendingCommodityTransfer to
	 * CommodityTransfer table
	 * 
	 * @param pct
	 */
	protected void handlePCTonFailure(PendingCommodityTransfer pct) {
		if (pct != null && commodityTransferService != null) {
			pct.setEndtime(new Timestamp());
			commodityTransferService.movePctToCt(pct);
		}
	}

	/**
	 * Handles any operation to be done on PendingCommodityTransfer record,
	 * during failure.
	 * 
	 * Default implementation moves the record from PendingCommodityTransfer to
	 * CommodityTransfer table
	 * 
	 * @param pct
	 */
	protected void handlePCTonSuccess(PendingCommodityTransfer pct) {
		if (pct != null && commodityTransferService != null) {
			pct.setEndtime(new Timestamp());
			pct.setTransferstatus(CmFinoFIX.TransferStatus_Completed);
			pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly));
			commodityTransferService.movePctToCt(pct);
		}
	}

	@Override
	public CFIXMsg onSubscriberRegistrationCashIn(
			CMSubscriberRegistrationCashIn subscriberRegistrationCashIn)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CFIXMsg onPinLessInquiryLessTransfer(
			CMPinLessInquiryLessTransfer transfer) {
		// TODO Auto-generated method stub
		return null;
	}
	private boolean isNFCPocketType(Pocket pocket){
		if (pocket.getPocketTemplateByPockettemplateid().getType() == CmFinoFIX.PocketType_NFC.intValue()) {
			return true;
		}
		return false;
	}
	
	private boolean isLakupandaiPocketType(Pocket pocket){
		if (pocket.getPocketTemplateByPockettemplateid().getType() == CmFinoFIX.PocketType_LakuPandai.intValue()) {
			return true;
		}
		return false;
	}

}

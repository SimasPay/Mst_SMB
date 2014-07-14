package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.BillPayments;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.scheduler.service.SCTLClearService;
import com.mfino.service.AutoReversalsCoreService;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.BulkUploadService;
import com.mfino.service.ChargeTxnCommodityTransferMapService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.EnumTextService;
import com.mfino.service.MfinoService;
import com.mfino.service.PocketService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionTypeService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.transactionapi.handlers.money.AutoReverseHandler;
import com.mfino.transactionapi.service.BulkTransferService;
import com.mfino.transactionapi.vo.TransactionDetails;

@org.springframework.stereotype.Service("SCTLClearServiceImpl")
public class SCTLClearServiceImpl  implements SCTLClearService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private long EXPIRATION_TIME = 600000;// 10mins
	private BigDecimal cashOutExpiryTime = new BigDecimal(-1);
	private long cashOutAtATM_Id = 0l;
	private long unregCashoutTTId = 0l;
	
	private static Service tellerService =null;
	private TransactionType cashOutAtATM = null;
	private TransactionType unregCashout = null;
	private TransactionType airtimePurchase = null;
	private TransactionType billPay = null;
	private TransactionType interEmoneyTransfer = null;

	@Autowired
	@Qualifier("AutoReverseHandlerImpl")
	private AutoReverseHandler autoReverseHandler;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("BulkTransferServiceImpl")
	private BulkTransferService btService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MfinoServiceImpl")
	private MfinoService mfinoService;
	
	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("UnRegisteredTxnInfoServiceImpl")
	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("ChargeTxnCommodityTransferMapServiceImpl")
	private ChargeTxnCommodityTransferMapService chargeTxnCommodityTransferMapService;
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("BillPaymentsServiceImpl")
	private BillPaymentsService billpaymentService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	@Autowired
	@Qualifier("AutoReversalsCoreServiceImpl")
	private AutoReversalsCoreService autoReversalsCoreService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private BulkUploadEntryService bulkUploadEntryService;
		
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	
	public void clearTheSCTL() {
		log.info("SCTLClearServiceImpl :: clearTheSCTL :: BEGIN clear the Service Charge transaction log");
			Integer[] status = new Integer[2];
			status[0] = CmFinoFIX.SCTLStatus_Inquiry;
			status[1] = CmFinoFIX.SCTLStatus_Processing;
		    unregCashout = transactionChargingService.getTransactionType(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
		    unregCashoutTTId = unregCashout != null ? unregCashout.getID() : 0l;
		    Timestamp currentTime = new Timestamp();
			
			if(tellerService==null){
			 tellerService =mfinoService.getServiceByName(ServiceAndTransactionConstants.SERVICE_TELLER);	
			}
			
			Long tellerServiceID = tellerService!=null?tellerService.getID():0L;
			
			cashOutAtATM = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM);
			cashOutAtATM_Id = cashOutAtATM != null ? cashOutAtATM.getID() : 0l;
			List<ServiceChargeTransactionLog> lst = serviceChargeTransactionLogService.getByStatus(status);
			EXPIRATION_TIME = systemParametersService.getLong(SystemParameterKeys.SCTL_TIMEOUT);
			cashOutExpiryTime = systemParametersService.getBigDecimal(SystemParameterKeys.CASHOUT_AT_ATM_EXPIRY_TIME);
			
			airtimePurchase = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
			Long airtimePurchaseTxnId = airtimePurchase != null ? airtimePurchase.getID() : 0l;

			billPay = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
			Long billPayTxnId = billPay != null ? billPay.getID() : 0l;
			
			interEmoneyTransfer = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_INTER_EMONEY_TRANSFER);
			Long ieTxnId = interEmoneyTransfer != null ? interEmoneyTransfer.getID() : 0l;
			
			if (CollectionUtils.isNotEmpty(lst)) {
				for (ServiceChargeTransactionLog sctl : lst) {
					if ((currentTime.getTime() - sctl.getLastUpdateTime().getTime()) > EXPIRATION_TIME) {
					log.info("Checking the status of the Transaction with SCTLID --> " + sctl.getID());
					if (CmFinoFIX.SCTLStatus_Inquiry.intValue() == sctl.getStatus().intValue()) {
						handleTimeout(sctl);	
					}
					else if(sctl.getServiceID().equals(tellerServiceID)){
						handleProcessingStatusTellerTransactions(sctl);
					} 
					else if (sctl.getTransactionTypeID().equals(cashOutAtATM_Id)) {
						handleProcessingStatusCashOutTransactions(sctl);
					}
						else if((sctl.getTransactionTypeID().equals(airtimePurchaseTxnId)) || (sctl.getTransactionTypeID().equals(billPayTxnId))
								 || (sctl.getTransactionTypeID().equals(ieTxnId))){
						handleBillPayProcessingStatusTransactions(sctl);
					}
					else if (CmFinoFIX.SCTLStatus_Processing.intValue() == sctl.getStatus().intValue()) {
						handleProcessingStatusTransactions(sctl);
					}
				}
					else {
						log.info("SCTL with ID: " + sctl.getID() + " will be cleared in next cycle");
					}
				}
			}
		
		log.info("SCTLClearServiceImpl :: clearTheSCTL :: END clear the Service Charge transaction log");
	}
	
	/**
	 * Get Available balance in third party suspense pocket for reversal after subtracting the current day 
	 * Cash-out at atm transactions amount
	 * @return
	 */
	private BigDecimal getAvailableBalanceForCashOutAtATMReversals() {
		String ATMPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		BigDecimal thirdPartySuspensePocketBalnce = BigDecimal.ZERO;
		BigDecimal toDayTxnsAmount = BigDecimal.ZERO;
		BigDecimal result = BigDecimal.ZERO;
		if (StringUtils.isNotBlank(ATMPartnerMDN)) {
			SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(ATMPartnerMDN);
			Partner partner = subscriberMDN.getSubscriber().getPartnerFromSubscriberID().iterator().next();
			Pocket thirdPartySuspensePocket = pocketService.getSuspencePocket(partner);
			if (thirdPartySuspensePocket != null) {
				thirdPartySuspensePocketBalnce = thirdPartySuspensePocket.getCurrentBalance();
			}
		}
		
		Integer[] multiStatus = new Integer[2];
		multiStatus[0] = CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED;
		multiStatus[1] = CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED;
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);
		Date today = cal.getTime();
		
		UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
		urtiQuery.setMultiStatus(multiStatus);
		urtiQuery.setTransactionName(cashOutAtATM.getTransactionName());
		urtiQuery.setCreateTimeGE(today);
		
		List<UnRegisteredTxnInfo> lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
		if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
			for (UnRegisteredTxnInfo urti: lstUnRegisteredTxnInfos) {
				toDayTxnsAmount = toDayTxnsAmount.add(urti.getAmount());
			}
		}
		log.info("Current Balance in ThirdParty : " + thirdPartySuspensePocketBalnce + " And today txn amount: "  + toDayTxnsAmount);
		result = thirdPartySuspensePocketBalnce.subtract(toDayTxnsAmount);
		
		return result;
	}
	
	
	private void handleProcessingStatusCashOutTransactions(ServiceChargeTransactionLog sctl) {
		Timestamp transactionDate = sctl.getCreateTime();
		Timestamp currentDate = new Timestamp();
		
		boolean isTxnExpired = false;
		//Check whether the Cash out transaction is expired or not.
		if (cashOutExpiryTime.doubleValue() == -1) {
			if (! DateUtils.isSameDay(currentDate, transactionDate)) {
				isTxnExpired = true;
			}
		} 
		else {
			long diffTime = currentDate.getTime() - transactionDate.getTime();
			if (diffTime > (cashOutExpiryTime.doubleValue() * 60*60*1000)) {
				isTxnExpired = true;
			}
		}
		
			UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
			urtiQuery.setTransferSctlId(sctl.getID());
			List<UnRegisteredTxnInfo> lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
			UnRegisteredTxnInfo urti = null;
			if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
				urti = lstUnRegisteredTxnInfos.get(0);
			}
		
			
		// as per tkt #2306 Once the with draw from ATM is failed then the transaction need to be expired or reversed.
		if ((urti != null) && (CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(urti.getUnRegisteredTxnStatus())) ) {
			isTxnExpired = true;
		}
		
		// Check the Auto_reversals table for the SCTL and based on reversal status updates the SCTL status. 
		if (urti != null) {
			AutoReversals ar = autoReversalsCoreService.getBySctlId(sctl.getID());
			if (ar != null &&
					(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_COMPLETED.equals(ar.getAutoRevStatus()) || CmFinoFIX.AutoRevStatus_COMPLETED.equals(ar.getAutoRevStatus())) ) {
				
				isTxnExpired = false;
				log.info("Money already reversed to Source pocket and need to change the status of SCTL and URTI entries");
				sctl.setStatus(CmFinoFIX.SCTLStatus_Expired);
				sctl.setFailureReason(MessageText._("Cash out Request Expired and Money reverted to Account"));
				serviceChargeTransactionLogService.save(sctl);
				
				urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_EXPIRED);
				urti.setFailureReason(MessageText._("Cash out Request Expired and Money reverted to Account"));
				unRegisteredTxnInfoService.save(urti);
			}
		}
		else {
			List<CommodityTransfer> lstCT = null;
			CommodityTransferQuery query = new CommodityTransferQuery();
			query.setTransactionID(sctl.getTransactionID());
			
			try {
				lstCT = commodityTransferService.get(query);
				if (CollectionUtils.isNotEmpty(lstCT)) {
					CommodityTransfer ct = lstCT.get(0);
					if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferStatus().intValue()) {
						log.info("Failing the transaction with id : " + sctl.getID() + " as the initial transaction is failed.");
						transactionChargingService.failTheTransaction(sctl, 
								enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferFailureReason()));
						isTxnExpired = false;
					}
				}
				else {
					log.info("Transaction is still in process, try after some time.....................");
				}
			} catch (Exception e) {
				log.error("SCTLClearServiceImpl :: Error while getting the status of the Transaction with SCTLID --> " + sctl.getID(), e);
			}

		}
		
		if (isTxnExpired) {
			BigDecimal availableBalanceForReversal = getAvailableBalanceForCashOutAtATMReversals();
			if (availableBalanceForReversal.compareTo(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge())) < 0 ) {
				isTxnExpired = false;
				log.info("SCTL with id " + sctl.getID() + " can not expired as there is low balance in third party suspense pocket");
			}
		}
		
		if (isTxnExpired) {
			log.info("Reversing the Cash Out at ATM request as it is expired...");

			
			if ((urti != null) && (CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED.equals(urti.getUnRegisteredTxnStatus()) || 
					CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(urti.getUnRegisteredTxnStatus()))) {
				
				log.info("Reversal of Cash Out at ATM request: " + sctl.getID() + " is initialized");
				urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED);
				unRegisteredTxnInfoService.save(urti);
				

				TransactionDetails transactionDetails = new TransactionDetails();
				transactionDetails.setSctlId(sctl.getID());
				transactionDetails.setChargeReverseAlso(true);
				
				Result result = autoReverseHandler.handle(transactionDetails);
				
				if (CmFinoFIX.NotificationCode_AutoReverseSuccess.equals(result.getNotificationCode())) {
					sctl.setStatus(CmFinoFIX.SCTLStatus_Expired);
					sctl.setFailureReason(MessageText._("Cash out Request Expired and Money reverted to Account"));
					serviceChargeTransactionLogService.save(sctl);
					
					urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_EXPIRED);
					urti.setFailureReason(MessageText._("Cash out Request Expired and Money reverted to Account"));
					unRegisteredTxnInfoService.save(urti);
					log.info("Success fully Reversed the Cash Out at ATM request: " + sctl.getID());	
				}
				else if (CmFinoFIX.NotificationCode_AutoReverseFailed.equals(result.getNotificationCode())) {
					log.info("Reversal of Cash Out at ATM request: " + sctl.getID() + " is failed");
					urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
					urti.setFailureReason(MessageText._("Reversal of the Cash out request is failed and will try in next trigger fire event..."));
					unRegisteredTxnInfoService.save(urti);
				}
				else {
					log.info("Reversal of Cash Out at ATM request: " + sctl.getID() + " is Pending");
				}
			}
			
		}
	}
	
	
	private void handleProcessingStatusTellerTransactions(ServiceChargeTransactionLog sctl) {
		UnRegisteredTxnInfo txnInfo =null;
		ChargeTxnCommodityTransferMapQuery query =new ChargeTxnCommodityTransferMapQuery();
		query.setSctlID(sctl.getID());
		
		List<ChargeTxnCommodityTransferMap> results =chargeTxnCommodityTransferMapService.getChargeTxnCommodityTransferMapByQuery(query);
		
		if(sctl.getTransactionTypeID().equals(unregCashoutTTId)){
			UnRegisteredTxnInfoQuery unregquery = new UnRegisteredTxnInfoQuery();
			unregquery.setCashoutSCTLId(sctl.getID());	
			List<UnRegisteredTxnInfo> unRegisteredTxnInfo = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(unregquery);
			if(unRegisteredTxnInfo!=null&&!unRegisteredTxnInfo.isEmpty()){
				txnInfo= unRegisteredTxnInfo.get(0);
			}
		}
		try {
			if (CollectionUtils.isNotEmpty(results)) {
				Long transferID= null;
				boolean confirm= false;
				for(ChargeTxnCommodityTransferMap sctlCtMap:results){		
					CommodityTransfer ct = commodityTransferService.getCommodityTransferById(sctlCtMap.getCommodityTransferID());
					if(ct!=null){
						if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferStatus().intValue()) {
							if(CmFinoFIX.TransactionUICategory_Teller_Cashout.equals(ct.getUICategory())
								&&sctl.getCommodityTransferID()==null){
								transferID = ct.getID();
								break;
							}else if(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank.equals(ct.getUICategory())){
								confirm = true;
								if(sctl.getCommodityTransferID()!=null){
									transferID=sctl.getCommodityTransferID();
								}
								break;
							}else if(CmFinoFIX.TransactionUICategory_Teller_Cashin_Subscriber.equals(ct.getUICategory())){
								confirm = true;
								transferID=ct.getID();
							}else if(CmFinoFIX.TransactionUICategory_Cashout_To_UnRegistered.equals(ct.getUICategory())
									&&txnInfo!=null
									&&CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED.equals(txnInfo.getUnRegisteredTxnStatus())){
								txnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED);
								txnInfo.setCashoutCTId(ct.getID());
								unRegisteredTxnInfoService.save(txnInfo);
							}
						} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferStatus().intValue()) {
								if(CmFinoFIX.TransactionUICategory_Teller_Cashout.equals(ct.getUICategory())
										||CmFinoFIX.TransactionUICategory_Teller_Cashin_SelfTransfer.equals(ct.getUICategory())
										||CmFinoFIX.TransactionUICategory_Teller_Cashin_Subscriber.equals(ct.getUICategory())
										||CmFinoFIX.TransactionUICategory_Cashout_To_UnRegistered.equals(ct.getUICategory())){
									transactionChargingService.failTheTransaction(sctl,enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferFailureReason()));
									if(txnInfo!=null){
										txnInfo.setCashoutCTId(ct.getID());
										txnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
										unRegisteredTxnInfoService.save(txnInfo);
									}
									break;
								}else if(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank.equals(ct.getUICategory())){
									transactionChargingService.changeStatusToPendingResolved(sctl);
									break;
								}
							}
						} else {
							log.info("Transaction is still in process, try after some time.....................");
						}
					}
				if(confirm){
					transactionChargingService.confirmTheTransaction(sctl, transferID);
				}else if(transferID!=null&&sctl.getCommodityTransferID()==null){
					transactionChargingService.addTransferID(sctl, transferID);
				}
				}
			}catch (Exception e) {
				log.error("SCTLClearServiceImpl :: Error while getting the status of the Transaction with SCTLID --> " + sctl.getID(), e);
				}
	}


	
	private void handleTimeout(ServiceChargeTransactionLog sctl){
		Calendar now = Calendar.getInstance();
		
		sctl.getCommodityTransferID();
		if (CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus()) && 
				(now.getTimeInMillis() - sctl.getCreateTime().getTime()) >  EXPIRATION_TIME) {
			transactionChargingService.failTheTransaction(sctl, MessageText._("Time Out"));
			handleUnregisteredTxn(sctl);
		}
	}
	
	
	private void handleUnregisteredTxn(ServiceChargeTransactionLog sctl) {
		if(sctl.getTransactionTypeID().equals(unregCashoutTTId)){
			UnRegisteredTxnInfo txnInfo =null;
			UnRegisteredTxnInfoQuery unregquery = new UnRegisteredTxnInfoQuery();
			unregquery.setCashoutSCTLId(sctl.getID());	
			List<UnRegisteredTxnInfo> unRegisteredTxnInfo = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(unregquery);
			if(unRegisteredTxnInfo!=null&&!unRegisteredTxnInfo.isEmpty()){
				txnInfo= unRegisteredTxnInfo.get(0);
				if(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED.equals(txnInfo.getUnRegisteredTxnStatus())){
					txnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
					unRegisteredTxnInfoService.save(txnInfo);
				}
			}
		}		
	}

	
	private void handleProcessingStatusTransactions(ServiceChargeTransactionLog sctl){
		Timestamp transactionDate = sctl.getLastUpdateTime();
		Timestamp currentDate = new Timestamp();
		//Will the change the status of only those transactions which are in processing status only when the transaction last update time is greater than expiration time otherwise we will simply return
		log.debug("currentTime:"+currentDate.getTime());
		log.debug("transactionTime:"+transactionDate.getTime());
		long diffTime = currentDate.getTime() - transactionDate.getTime();
		log.debug("diffTime:"+diffTime);
		if (diffTime <= EXPIRATION_TIME) {
			log.info("Skipping the transaction with SCTL ID:"+sctl.getID()+" as the configured expiration time has not yet reached to change the status");
			return;
		}
		
		List<CommodityTransfer> lstCT = null;
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setTransactionID(sctl.getTransactionID());
		
		try {
			lstCT = commodityTransferService.get(query);
			if (CollectionUtils.isNotEmpty(lstCT)) {
				CommodityTransfer ct = lstCT.get(0);
				if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferStatus().intValue()) {
					transactionChargingService.confirmTheTransaction(sctl, ct.getID());
				} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferStatus().intValue()) {
					transactionChargingService.failTheTransaction(sctl, 
							enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferFailureReason()));
				}
				// Check if the Transaction is related to Bulk Transfer then do the changes in BulkUpload table also
				updateBulkTransfer(sctl, ct);
			} else {
				log.info("Transaction is still in process, try after some time.....................");
			}
		} catch (Exception e) {
			log.error("SCTLClearServiceImpl :: Error while getting the status of the Transaction with SCTLID --> " + sctl.getID(), e);
		}
	}

	/**
	 * Updates the Bulk Transfer details based on the SCTL
	 * @param sctl
	 * @param ct
	 */
	private void updateBulkTransfer(ServiceChargeTransactionLog sctl, CommodityTransfer ct) {
		
		TransactionType tt = transactionTypeService.getTransactionTypeById(sctl.getTransactionTypeID());
		
		// If the transaction is Bulk Transfer then change the status of bulk upload 
		if (ServiceAndTransactionConstants.TRANSACTION_BULK_TRANSFER.equals(tt.getTransactionName())) {
			BulkUpload bulkUpload = bulkUploadService.getBySCTLId(sctl.getID());
			if (bulkUpload != null) {
				if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferStatus().intValue()) {
					log.info("Changing the Status of the Bulk Transfer from Pending to Approved.");
					bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Approved);
					bulkUpload.setDeliveryDate(new Timestamp());
					bulkUploadService.save(bulkUpload);
				} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferStatus().intValue()) {
					btService.failTheBulkTransfer(bulkUpload, "Transaction is failed");
				}
			}
		}
		// If the transaction is Sub Bulk Transfer then change the status of bulk upload entry
		else if (ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER.equals(tt.getTransactionName())) {
			BulkUploadEntry bue = bulkUploadEntryService.getBulkUploadEntryBySctlID(sctl.getID());
			if (bue != null) {
				log.info("Changing the staus of the Bulk Upload Entry for SCTL id " + sctl.getID() + " to --> " + ct.getTransferStatus());
				bue.setStatus(ct.getTransferStatus());
				bulkUploadEntryService.saveBulkUploadEntry(bue);
			}
		}
		// If the transaction is Reverse Bulk Transfer for non transfered amount then change the status of bulk upload
		else if (ServiceAndTransactionConstants.TRANSACTION_SETTLE_BULK_TRANSFER.equals(tt.getTransactionName())) {
			BulkUpload bulkUpload = bulkUploadService.getByReverseSCTLId(sctl.getID());
			if (bulkUpload != null) {
				if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferStatus().intValue()) {
					bulkUpload.setRevertAmount(BigDecimal.ZERO);
					log.info("Setting the Revert Amount for Bulk Transfer: " + bulkUpload.getID() + " Zero.");
				} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferStatus().intValue()) {
					log.info("As the Transaction is failed for Bulk Transfer: " + bulkUpload.getID() + " the Revert Amount is unchanged.");
				}
				bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Complete);
				bulkUpload.setDeliveryDate(new Timestamp());
				bulkUploadService.save(bulkUpload);
			}
		}
	}
	
	
	private void handleBillPayProcessingStatusTransactions(ServiceChargeTransactionLog sctl){
		log.info("SCTLClearServiceImpl :: handleBillPayProcessingStatusTransactions()");
		List<CommodityTransfer> lstCT = null;
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setTransactionID(sctl.getTransactionID());
		
		BillPaymentsQuery billPayQuery = new BillPaymentsQuery();
		billPayQuery.setSctlID(sctl.getID());
		List<BillPayments> billPaymentsList = billpaymentService.get(billPayQuery);
		
		BillPayments billPayments = null;
		if((null != billPaymentsList) && (billPaymentsList.size() > 0)){
			billPayments = billPaymentsList.get(0);
		}
		
		Collection<Integer> pendingBillPayStatuses = new HashSet<Integer>();
		pendingBillPayStatuses.add(CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_PENDING);
		pendingBillPayStatuses.add(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_PENDING);
		pendingBillPayStatuses.add(CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_PENDING);
		
		try {
			lstCT = commodityTransferService.get(query);
			if(CollectionUtils.isNotEmpty(lstCT)) {
				if((billPayments != null) && (CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_FAILED.equals(billPayments.getBillPayStatus()))) {
					log.info("Failing the transaction as Biller confirmation is failed.");
					transactionChargingService.failTheTransaction(sctl, MessageText._("Fails the transaction as Biller Confirmation is failed"));
				}
				else if((billPayments != null) && (CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_COMPLETED.equals(billPayments.getBillPayStatus()))) {
					log.info("Transaction is still in process, try after some time.....................");
				}
				else if((billPayments != null) && !(pendingBillPayStatuses.contains(billPayments.getBillPayStatus()))){
					CommodityTransfer ct = lstCT.get(0);
					if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferStatus().intValue()) {
						transactionChargingService.confirmTheTransaction(sctl, ct.getID());
					} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferStatus().intValue()) {
						transactionChargingService.failTheTransaction(sctl, 
								enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferFailureReason()));
					}
				}
				else{
					transactionChargingService.changeStatusToPending(sctl);
				}
			} else {
				log.info("handleBillPayProcessingStatusTransactions: Transaction is still in process, try after some time.....................");
			}
		} catch (Exception e) {
			log.error("handleBillPayProcessingStatusTransactions: Error while getting the status of the Transaction with SCTLID --> " + sctl.getID(), e);
		}
	}	
}

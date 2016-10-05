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
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnregisteredTxnInfo;
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
		    unregCashoutTTId = unregCashout != null ? unregCashout.getId().longValue() : 0l;
		    Timestamp currentTime = new Timestamp();
			
			if(tellerService==null){
			 tellerService =mfinoService.getServiceByName(ServiceAndTransactionConstants.SERVICE_TELLER);	
			}
			
			Long tellerServiceID = tellerService!=null?tellerService.getId().longValue():0L;
			
			cashOutAtATM = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM);
			cashOutAtATM_Id = cashOutAtATM != null ? cashOutAtATM.getId().longValue() : 0l;
			List<ServiceChargeTxnLog> lst = serviceChargeTransactionLogService.getByStatus(status);
			EXPIRATION_TIME = systemParametersService.getLong(SystemParameterKeys.SCTL_TIMEOUT);
			cashOutExpiryTime = systemParametersService.getBigDecimal(SystemParameterKeys.CASHOUT_AT_ATM_EXPIRY_TIME);
			
			airtimePurchase = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
			Long airtimePurchaseTxnId = airtimePurchase != null ? airtimePurchase.getId().longValue() : 0l;

			billPay = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
			Long billPayTxnId = billPay != null ? billPay.getId().longValue() : 0l;
			
			interEmoneyTransfer = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_INTER_EMONEY_TRANSFER);
			Long ieTxnId = interEmoneyTransfer != null ? interEmoneyTransfer.getId().longValue() : 0l;
			
			if (CollectionUtils.isNotEmpty(lst)) {
				for (ServiceChargeTxnLog sctl : lst) {
					if ((currentTime.getTime() - sctl.getLastupdatetime().getTime()) > EXPIRATION_TIME) {
					log.info("Checking the status of the Transaction with SCTLID --> " + sctl.getId());
					if (CmFinoFIX.SCTLStatus_Inquiry.intValue() == sctl.getStatus()) {
						handleTimeout(sctl);	
					}
					else if(sctl.getServiceid().equals(tellerServiceID)){
						handleProcessingStatusTellerTransactions(sctl);
					} 
					else if (sctl.getTransactiontypeid().equals(cashOutAtATM_Id)) {
						handleProcessingStatusCashOutTransactions(sctl);
					}
						else if((sctl.getTransactiontypeid().equals(airtimePurchaseTxnId)) || (sctl.getTransactiontypeid().equals(billPayTxnId))
								 || (sctl.getTransactiontypeid().equals(ieTxnId))){
						handleBillPayProcessingStatusTransactions(sctl);
					}
					else if (CmFinoFIX.SCTLStatus_Processing.intValue() == sctl.getStatus()) {
						handleProcessingStatusTransactions(sctl);
					}
				}
					else {
						log.info("SCTL with ID: " + sctl.getId() + " will be cleared in next cycle");
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
			SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(ATMPartnerMDN);
			Partner partner = subscriberMDN.getSubscriber().getPartners().iterator().next();
			Pocket thirdPartySuspensePocket = pocketService.getSuspencePocket(partner);
			if (thirdPartySuspensePocket != null) {
				thirdPartySuspensePocketBalnce = new BigDecimal(thirdPartySuspensePocket.getCurrentbalance());
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
		urtiQuery.setTransactionName(cashOutAtATM.getTransactionname());
		urtiQuery.setCreateTimeGE(today);
		
		List<UnregisteredTxnInfo> lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
		if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
			for (UnregisteredTxnInfo urti: lstUnRegisteredTxnInfos) {
				toDayTxnsAmount = toDayTxnsAmount.add(urti.getAmount());
			}
		}
		log.info("Current Balance in ThirdParty : " + thirdPartySuspensePocketBalnce + " And today txn amount: "  + toDayTxnsAmount);
		result = thirdPartySuspensePocketBalnce.subtract(toDayTxnsAmount);
		
		return result;
	}
	
	
	private void handleProcessingStatusCashOutTransactions(ServiceChargeTxnLog sctl) {
		Timestamp transactionDate = sctl.getCreatetime();
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
			urtiQuery.setTransferSctlId(sctl.getId().longValue());
			List<UnregisteredTxnInfo> lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
			UnregisteredTxnInfo urti = null;
			if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
				urti = lstUnRegisteredTxnInfos.get(0);
			}
		
			
		// as per tkt #2306 Once the with draw from ATM is failed then the transaction need to be expired or reversed.
		if ((urti != null) && (CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(urti.getUnregisteredtxnstatus())) ) {
			isTxnExpired = true;
		}
		
		// Check the Auto_reversals table for the SCTL and based on reversal status updates the SCTL status. 
		if (urti != null) {
			AutoReversals ar = autoReversalsCoreService.getBySctlId(sctl.getId().longValue());
			if (ar != null &&
					(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_COMPLETED.equals(ar.getAutorevstatus()) || CmFinoFIX.AutoRevStatus_COMPLETED.equals(ar.getAutorevstatus())) ) {
				
				isTxnExpired = false;
				log.info("Money already reversed to Source pocket and need to change the status of SCTL and URTI entries");
				sctl.setStatus(CmFinoFIX.SCTLStatus_Expired);
				sctl.setFailurereason(MessageText._("Cash out Request Expired and Money reverted to Account"));
				serviceChargeTransactionLogService.save(sctl);
				
				urti.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_EXPIRED));
				urti.setFailurereason(MessageText._("Cash out Request Expired and Money reverted to Account"));
				unRegisteredTxnInfoService.save(urti);
			}
		}
		else {
			List<CommodityTransfer> lstCT = null;
			CommodityTransferQuery query = new CommodityTransferQuery();
			query.setTransactionID(sctl.getTransactionid().longValue());
			
			try {
				lstCT = commodityTransferService.get(query);
				if (CollectionUtils.isNotEmpty(lstCT)) {
					CommodityTransfer ct = lstCT.get(0);
					if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferstatus()) {
						log.info("Failing the transaction with id : " + sctl.getId() + " as the initial transaction is failed.");
						transactionChargingService.failTheTransaction(sctl, 
								enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferfailurereason()));
						isTxnExpired = false;
					}
				}
				else {
					log.info("Transaction is still in process, try after some time.....................");
				}
			} catch (Exception e) {
				log.error("SCTLClearServiceImpl :: Error while getting the status of the Transaction with SCTLID --> " + sctl.getId(), e);
			}

		}
		
		if (isTxnExpired) {
			BigDecimal availableBalanceForReversal = getAvailableBalanceForCashOutAtATMReversals();
			if (availableBalanceForReversal.compareTo(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge())) < 0 ) {
				isTxnExpired = false;
				log.info("SCTL with id " + sctl.getId() + " can not expired as there is low balance in third party suspense pocket");
			}
		}
		
		if (isTxnExpired) {
			log.info("Reversing the Cash Out at ATM request as it is expired...");

			
			if ((urti != null) && (CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED.equals(urti.getUnregisteredtxnstatus()) || 
					CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(urti.getUnregisteredtxnstatus()))) {
				
				log.info("Reversal of Cash Out at ATM request: " + sctl.getId() + " is initialized");
				urti.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED));
				unRegisteredTxnInfoService.save(urti);
				

				TransactionDetails transactionDetails = new TransactionDetails();
				transactionDetails.setSctlId(sctl.getId().longValue());
				transactionDetails.setChargeReverseAlso(true);
				
				Result result = autoReverseHandler.handle(transactionDetails);
				
				if (CmFinoFIX.NotificationCode_AutoReverseSuccess.equals(result.getNotificationCode())) {
					sctl.setStatus(CmFinoFIX.SCTLStatus_Expired);
					sctl.setFailurereason(MessageText._("Cash out Request Expired and Money reverted to Account"));
					serviceChargeTransactionLogService.save(sctl);
					
					urti.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_EXPIRED));
					urti.setFailurereason(MessageText._("Cash out Request Expired and Money reverted to Account"));
					unRegisteredTxnInfoService.save(urti);
					log.info("Success fully Reversed the Cash Out at ATM request: " + sctl.getId());	
				}
				else if (CmFinoFIX.NotificationCode_AutoReverseFailed.equals(result.getNotificationCode())) {
					log.info("Reversal of Cash Out at ATM request: " + sctl.getId() + " is failed");
					urti.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED));
					urti.setFailurereason(MessageText._("Reversal of the Cash out request is failed and will try in next trigger fire event..."));
					unRegisteredTxnInfoService.save(urti);
				}
				else {
					log.info("Reversal of Cash Out at ATM request: " + sctl.getId() + " is Pending");
				}
			}
			
		}
	}
	
	
	private void handleProcessingStatusTellerTransactions(ServiceChargeTxnLog sctl) {
		UnregisteredTxnInfo txnInfo =null;
		ChargeTxnCommodityTransferMapQuery query =new ChargeTxnCommodityTransferMapQuery();
		query.setSctlID(sctl.getId().longValue());
		
		List<ChargeTxnCommodityTransferMap> results =chargeTxnCommodityTransferMapService.getChargeTxnCommodityTransferMapByQuery(query);
		
		if(sctl.getTransactiontypeid().equals(unregCashoutTTId)){
			UnRegisteredTxnInfoQuery unregquery = new UnRegisteredTxnInfoQuery();
			unregquery.setCashoutSCTLId(sctl.getId().longValue());	
			List<UnregisteredTxnInfo> unRegisteredTxnInfo = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(unregquery);
			if(unRegisteredTxnInfo!=null&&!unRegisteredTxnInfo.isEmpty()){
				txnInfo= unRegisteredTxnInfo.get(0);
			}
		}
		try {
			if (CollectionUtils.isNotEmpty(results)) {
				Long transferID= null;
				boolean confirm= false;
				for(ChargeTxnCommodityTransferMap sctlCtMap:results){		
					CommodityTransfer ct = commodityTransferService.getCommodityTransferById(sctlCtMap.getCommoditytransferid().longValue());
					if(ct!=null){
						if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferstatus()) {
							if(CmFinoFIX.TransactionUICategory_Teller_Cashout.equals(ct.getUicategory())
								&&sctl.getCommoditytransferid()==null){
								transferID = ct.getId().longValue();
								break;
							}else if(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank.equals(ct.getUicategory())){
								confirm = true;
								if(sctl.getCommoditytransferid()!=null){
									transferID=sctl.getCommoditytransferid().longValue();
								}
								break;
							}else if(CmFinoFIX.TransactionUICategory_Teller_Cashin_Subscriber.equals(ct.getUicategory())){
								confirm = true;
								transferID=ct.getId().longValue();
							}else if(CmFinoFIX.TransactionUICategory_Cashout_To_UnRegistered.equals(ct.getUicategory())
									&&txnInfo!=null
									&&CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED.equals(txnInfo.getUnregisteredtxnstatus())){
								txnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED));
								txnInfo.setCashoutctid(ct.getId());
								unRegisteredTxnInfoService.save(txnInfo);
							}
						} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferstatus()) {
								if(CmFinoFIX.TransactionUICategory_Teller_Cashout.equals(ct.getUicategory())
										||CmFinoFIX.TransactionUICategory_Teller_Cashin_SelfTransfer.equals(ct.getUicategory())
										||CmFinoFIX.TransactionUICategory_Teller_Cashin_Subscriber.equals(ct.getUicategory())
										||CmFinoFIX.TransactionUICategory_Cashout_To_UnRegistered.equals(ct.getUicategory())){
									transactionChargingService.failTheTransaction(sctl,enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferfailurereason()));
									if(txnInfo!=null){
										txnInfo.setCashoutctid(ct.getId());
										txnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED));
										unRegisteredTxnInfoService.save(txnInfo);
									}
									break;
								}else if(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank.equals(ct.getUicategory())){
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
				}else if(transferID!=null&&sctl.getCommoditytransferid()==null){
					transactionChargingService.addTransferID(sctl, transferID);
				}
				}
			}catch (Exception e) {
				log.error("SCTLClearServiceImpl :: Error while getting the status of the Transaction with SCTLID --> " + sctl.getId(), e);
				}
	}


	
	private void handleTimeout(ServiceChargeTxnLog sctl){
		Calendar now = Calendar.getInstance();
		
		sctl.getCommoditytransferid();
		if (CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus()) && 
				(now.getTimeInMillis() - sctl.getCreatetime().getTime()) >  EXPIRATION_TIME) {
			transactionChargingService.failTheTransaction(sctl, MessageText._("Time Out"));
			handleUnregisteredTxn(sctl);
		}
	}
	
	
	private void handleUnregisteredTxn(ServiceChargeTxnLog sctl) {
		if(sctl.getTransactiontypeid().equals(unregCashoutTTId)){
			UnregisteredTxnInfo txnInfo =null;
			UnRegisteredTxnInfoQuery unregquery = new UnRegisteredTxnInfoQuery();
			unregquery.setCashoutSCTLId(sctl.getId().longValue());	
			List<UnregisteredTxnInfo> unRegisteredTxnInfo = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(unregquery);
			if(unRegisteredTxnInfo!=null&&!unRegisteredTxnInfo.isEmpty()){
				txnInfo= unRegisteredTxnInfo.get(0);
				if(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED.equals(txnInfo.getUnregisteredtxnstatus())){
					txnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED));
					unRegisteredTxnInfoService.save(txnInfo);
				}
			}
		}		
	}

	
	private void handleProcessingStatusTransactions(ServiceChargeTxnLog sctl){
		Timestamp transactionDate = sctl.getLastupdatetime();
		Timestamp currentDate = new Timestamp();
		//Will the change the status of only those transactions which are in processing status only when the transaction last update time is greater than expiration time otherwise we will simply return
		log.debug("currentTime:"+currentDate.getTime());
		log.debug("transactionTime:"+transactionDate.getTime());
		long diffTime = currentDate.getTime() - transactionDate.getTime();
		log.debug("diffTime:"+diffTime);
		if (diffTime <= EXPIRATION_TIME) {
			log.info("Skipping the transaction with SCTL ID:"+sctl.getId()+" as the configured expiration time has not yet reached to change the status");
			return;
		}
		
		List<CommodityTransfer> lstCT = null;
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setTransactionID(sctl.getTransactionid().longValue());
		
		try {
			lstCT = commodityTransferService.get(query);
			if (CollectionUtils.isNotEmpty(lstCT)) {
				CommodityTransfer ct = lstCT.get(0);
				if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferstatus()) {
					transactionChargingService.confirmTheTransaction(sctl, ct.getId().longValue());
				} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferstatus()) {
					transactionChargingService.failTheTransaction(sctl, 
							enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferfailurereason()));
				}
				// Check if the Transaction is related to Bulk Transfer then do the changes in BulkUpload table also
				updateBulkTransfer(sctl, ct);
			} else {
				log.info("Transaction is still in process, try after some time.....................");
			}
		} catch (Exception e) {
			log.error("SCTLClearServiceImpl :: Error while getting the status of the Transaction with SCTLID --> " + sctl.getId(), e);
		}
	}

	/**
	 * Updates the Bulk Transfer details based on the SCTL
	 * @param sctl
	 * @param ct
	 */
	private void updateBulkTransfer(ServiceChargeTxnLog sctl, CommodityTransfer ct) {
		
		TransactionType tt = transactionTypeService.getTransactionTypeById(sctl.getTransactiontypeid().longValue());
		
		// If the transaction is Bulk Transfer then change the status of bulk upload 
		if (ServiceAndTransactionConstants.TRANSACTION_BULK_TRANSFER.equals(tt.getTransactionname())) {
			BulkUpload bulkUpload = bulkUploadService.getBySCTLId(sctl.getId().longValue());
			if (bulkUpload != null) {
				if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferstatus()) {
					log.info("Changing the Status of the Bulk Transfer from Pending to Approved.");
					bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Approved);
					bulkUpload.setDeliverydate(new Timestamp());
					bulkUploadService.save(bulkUpload);
				} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferstatus()) {
					btService.failTheBulkTransfer(bulkUpload, "Transaction is failed");
				}
			}
		}
		// If the transaction is Sub Bulk Transfer then change the status of bulk upload entry
		else if (ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER.equals(tt.getTransactionname())) {
			BulkUploadEntry bue = bulkUploadEntryService.getBulkUploadEntryBySctlID(sctl.getId().longValue());
			if (bue != null) {
				log.info("Changing the staus of the Bulk Upload Entry for SCTL id " + sctl.getId() + " to --> " + ct.getTransferstatus());
				bue.setStatus(ct.getTransferstatus());
				bulkUploadEntryService.saveBulkUploadEntry(bue);
			}
		}
		// If the transaction is Reverse Bulk Transfer for non transfered amount then change the status of bulk upload
		else if (ServiceAndTransactionConstants.TRANSACTION_SETTLE_BULK_TRANSFER.equals(tt.getTransactionname())) {
			BulkUpload bulkUpload = bulkUploadService.getByReverseSCTLId(sctl.getId().longValue());
			if (bulkUpload != null) {
				if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferstatus()) {
					bulkUpload.setRevertamount(BigDecimal.ZERO);
					log.info("Setting the Revert Amount for Bulk Transfer: " + bulkUpload.getId() + " Zero.");
				} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferstatus()) {
					log.info("As the Transaction is failed for Bulk Transfer: " + bulkUpload.getId() + " the Revert Amount is unchanged.");
				}
				bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Complete);
				bulkUpload.setDeliverydate(new Timestamp());
				bulkUploadService.save(bulkUpload);
			}
		}
	}
	
	
	private void handleBillPayProcessingStatusTransactions(ServiceChargeTxnLog sctl){
		log.info("SCTLClearServiceImpl :: handleBillPayProcessingStatusTransactions()");
		List<CommodityTransfer> lstCT = null;
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setTransactionID(sctl.getTransactionid().longValue());
		
		BillPaymentsQuery billPayQuery = new BillPaymentsQuery();
		billPayQuery.setSctlID(sctl.getId().longValue());
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
				if((billPayments != null) && (CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_FAILED.equals(billPayments.getBillpaystatus()))) {
					log.info("Failing the transaction as Biller confirmation is failed.");
					transactionChargingService.failTheTransaction(sctl, MessageText._("Fails the transaction as Biller Confirmation is failed"));
				}
				else if((billPayments != null) && (CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_COMPLETED.equals(billPayments.getBillpaystatus()))) {
					log.info("Transaction is still in process, try after some time.....................");
				}
				else if((billPayments != null) && !(pendingBillPayStatuses.contains(billPayments.getBillpaystatus()))){
					CommodityTransfer ct = lstCT.get(0);
					if (CmFinoFIX.TransferStatus_Completed.intValue() == ct.getTransferstatus()) {
						transactionChargingService.confirmTheTransaction(sctl, ct.getId().longValue());
					} else if (CmFinoFIX.TransferStatus_Failed.intValue() == ct.getTransferstatus()) {
						transactionChargingService.failTheTransaction(sctl, 
								enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferfailurereason()));
					}
				}
				else{
					transactionChargingService.changeStatusToPending(sctl);
				}
			} else {
				log.info("handleBillPayProcessingStatusTransactions: Transaction is still in process, try after some time.....................");
			}
		} catch (Exception e) {
			log.error("handleBillPayProcessingStatusTransactions: Error while getting the status of the Transaction with SCTLID --> " + sctl.getId(), e);
		}
	}	
}

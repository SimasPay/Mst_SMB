package com.mfino.mce.backend.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SystemParameters;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.backend.CommodityTransferService;
import com.mfino.mce.backend.PendingCommodityTransfer21CtClearance;
import com.mfino.mce.backend.PendingCommodityTransferClearance;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.TransactionChargingService;

/**
 * @author srinivaas
 *
 */
public class PendingCommodityTransfer21CtClearanceImpl extends BaseServiceImpl implements PendingCommodityTransfer21CtClearance{

	private BankService bankService;

	private SessionFactory sessionFactory;

	private CommodityTransferService commodityTransferService;

	//private TransactionChargingServiceImpl tcs =new TransactionChargingServiceImpl();
	private TransactionChargingService transactionChargingService ;

	protected NotificationMessageParserService notificationMessageParserService ;
	protected SMSService smsService;

	public SMSService getSmsService() {
		return smsService;
	}

	public void setSmsService(SMSService smsService) {
		this.smsService = smsService;
	}

	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}

	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}

	@Transactional(readOnly=true, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<PendingCommodityTransfer> getAll21NonPendingTransfers(){
		log.info("Number of Pending transactions that need manual Resolve are : " + coreDataWrapper.getCountOfPendingPCT());
		return coreDataWrapper.getAll21NonPendingTransfers();
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public void calculateFinalState(PendingCommodityTransfer pct) {
		
		log.info("Moving PCT to CT: "+pct.getID());		
		getCommodityTransferService().movePctToCt(pct);				
	}

	private void pendingBulkTransfer(PendingCommodityTransfer pct) {
		log.info("Check whether the Pending transaction is of type Bulk Transfer...");
		ChargeTxnCommodityTransferMapDAO cTxnCommodityTransferMapDAO = DAOFactory.getInstance().getTxnTransferMap();
		ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
		query.setCommodityTransferID(pct.getID());
		List<ChargeTxnCommodityTransferMap> ctTxnCommodityTransferMap = cTxnCommodityTransferMapDAO.get(query);
		Long sctlId = ctTxnCommodityTransferMap!=null&&!ctTxnCommodityTransferMap.isEmpty()?ctTxnCommodityTransferMap.get(0).getSctlId():null;

		// Check if the Transaction is related to Bulk Transfer then do the changes in BulkUpload table also
		if (sctlId != null) {
			if (CmFinoFIX.TransactionUICategory_Bulk_Transfer.equals(pct.getUICategory())) {
				log.info("Changing the Bulk Transfer Status to Pending...");
				BulkUploadDAO buDAO = DAOFactory.getInstance().getBulkUploadDAO();
				BulkUpload bulkUpload = buDAO.getBySCTLId(sctlId);
				if (bulkUpload != null) {
					bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Pending);
					bulkUpload.setDeliveryDate(new Timestamp());
					buDAO.save(bulkUpload);
					// Send Pending Notification to Partner
					NotificationWrapper notification = new NotificationWrapper();
					String mdn = bulkUpload.getMDN();
                    SystemParameters langSystemParam = DAOFactory.getInstance().getSystemParameterDao().getSystemParameterByName(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
                    Integer language = Integer.parseInt(langSystemParam.getParameterValue());
					SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
					if(smdn != null)
					{
						language = smdn.getSubscriber().getLanguage();
						notification.setFirstName(smdn.getSubscriber().getFirstName());
						notification.setLastName(smdn.getSubscriber().getLastName());
					}
					notification.setLanguage(language);
					notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
					notification.setCode(CmFinoFIX.NotificationCode_BulkTransferRequestPendingToPartner);
					notification.setBulkTransferId(bulkUpload.getID());
					notification.setSctlID(bulkUpload.getServiceChargeTransactionLogID());


					String message = notificationMessageParserService.buildMessage(notification,true);
					smsService.setDestinationMDN(mdn);
					smsService.setMessage(message);
					smsService.setNotificationCode(notification.getCode());
					smsService.setSctlId(sctlId);
					smsService.asyncSendSMS();
				}
			}
			else if (CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer.equals(pct.getUICategory())) {
				// Nothing to do
			}
			else if (CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer.equals(pct.getUICategory())) {
				// Nothing to do
			}
		}
	}

	private boolean checkExpiredTransfer(PendingCommodityTransfer pct) {
		Timestamp now = new Timestamp();
		if (!(CmFinoFIX.TransferStatus_Completed.equals(pct.getTransferStatus()))) {
			if ((pct.getStartTime().getTime() + pct.getExpirationTimeout()) < now.getTime()
						&&!(CmFinoFIX.TransferStatus_Pending.equals(pct.getTransferStatus()))) {
				if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_TransferInquirySentToBank)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_BankAccountTransferInquiryToBankExpired);
				} else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_ConfirmationPromptSentToSubscriber)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_ConfirmationPromptToSubscriberExpired);
				} else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_MoneyTransaferSentToBank)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_MoneyTransferToBankExpired);
				}
				/*//smart specific
				else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_TopupSentToBank)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_BankAccountTopupToBankExpired);
				} else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_TopupFromBankAccountSentToOperator)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_BankAccountTopupToOperatorExpired);
				} else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_BankChannelPaymentSentToOperator) ||
						pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_BankChannelTopupSentToOperator) ||
						pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_MobileAgentTopupSentToOperator)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_MobileAgentRechargeToOperatorExpired);
				} else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_ShareLoadSentToOperator)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_ShareLoadToOperatorExpired);
				} else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_BillPaymentSentToBank)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_BillPaymentToBankExpired);
				} else if (pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_BillPaymentTopupSentToBank)) {
					pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_BillPaymentTopupToBankExpired);
				} */
				pct.setTransferStatus(CmFinoFIX.TransferStatus_Pending);
				return true;
			}
		}
		return false;
	}

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param commodityTransferService the commodityTransferService to set
	 */
	public void setCommodityTransferService(CommodityTransferService commodityTransferService) {
		this.commodityTransferService = commodityTransferService;
	}

	/**
	 * @return the commodityTransferService
	 */
	public CommodityTransferService getCommodityTransferService() {
		return commodityTransferService;
	}

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}
}

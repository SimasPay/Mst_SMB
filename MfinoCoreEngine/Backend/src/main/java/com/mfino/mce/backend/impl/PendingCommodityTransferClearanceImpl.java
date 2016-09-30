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
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SystemParameters;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.backend.CommodityTransferService;
import com.mfino.mce.backend.PendingCommodityTransferClearance;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.TransactionChargingService;

public class PendingCommodityTransferClearanceImpl extends BaseServiceImpl implements PendingCommodityTransferClearance{

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

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void movePendingToComplete() {
		log.info("PendingCommodityTransferClearanceImpl:: movePendingToComplete() Begin");
//		log.info("Number of Pending transactions that need manual Resolve are : " + coreDataWrapper.getCountOfPendingPCT());

		List<PendingCommodityTransfer> lst = coreDataWrapper.getAllPendingTransfers();
		for (PendingCommodityTransfer pct: lst) {
			try{
//					PendingCommodityTransfer duplicatePct = new PendingCommodityTransfer();
//					duplicatePct.copy(pct);
				calculateFinalState(pct);
			}
			catch (Exception e) {
				log.error("Exception in movePendingToComplete ",e);
			}
		}
		
		log.info("PendingCommodityTransferClearanceImpl:: movePendingToComplete() End");
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	private boolean calculateFinalState(PendingCommodityTransfer pct) {
		boolean isExpired = checkExpiredTransfer(pct);
		boolean bFinal = false;
		log.info("calculating the final state of pct id: "+ pct.getId());
		log.debug("PendingCommodityTransferClearanceImpl :: CmFinoFIX.TransferStatus_Completed="+CmFinoFIX.TransferStatus_Completed+", pct.getTransferStatus()="+pct.getTransferstatus() + ", CmFinoFIX.TransferStatus_Completed.equals(pct.getTransferStatus())="+CmFinoFIX.TransferStatus_Completed.equals(pct.getTransferstatus()) + ", (CmFinoFIX.TransferStatus_Completed.intValue() == pct.getTransferStatus().intValue())"+(CmFinoFIX.TransferStatus_Completed.intValue() == pct.getTransferstatus().intValue()));

		if (CmFinoFIX.TransferStatus_Completed.equals(pct.getTransferstatus())) {
			// which means the record could not be moved to CT due to some issue with persistence, retry persistence.
			log.info("Moving PCT to CT: "+pct.getId());
			pct.setTransferfailurereason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
			getCommodityTransferService().movePctToCt(pct);
			bFinal = true;
		}else if (CmFinoFIX.TransferStatus_Pending.equals(pct.getTransferstatus())) {
			if(isExpired){
				bFinal = true;
			}
			boolean	bSendReversal	=	false;

			// we check for specific failure reasons to decide if the reversal required
			if (pct.getOperatoractionrequired() != null && pct.getOperatoractionrequired().booleanValue()) {
				bSendReversal = true;
			}
			else if(CmFinoFIX.TransferFailureReason_MoneyTransferToBankFailed.equals(pct.getTransferfailurereason())){
				bSendReversal	=	true;
			}
			else if(CmFinoFIX.TransferFailureReason_MoneyTransferToBankExpired.equals(pct.getTransferfailurereason())){
				bSendReversal	=	true;
			}
			else if(CmFinoFIX.TransferFailureReason_RejectedByIntegration.equals(pct.getTransferfailurereason())){
				bSendReversal	=	true;
			}
			else if(CmFinoFIX.TransferFailureReason_IntegrationServiceUnavailable.equals(pct.getTransferfailurereason())){
				bSendReversal	=	true;
			}

			/*//smart specific
			else if(CmFinoFIX.TransferFailureReason_ShareLoadToOperatorFailed.equals(pct.getTransferFailureReason())){
				bFinal	=	false;
			}else if(CmFinoFIX.TransferFailureReason_BankAccountTopupToOperatorFailed.equals(pct.getTransferFailureReason())){
				bFinal	=	false;
				bSendReversal	=	true;
			}else if(CmFinoFIX.TransferFailureReason_BankAccountTopupToBankFailed.equals(pct.getTransferFailureReason())){
				bSendReversal	=	true;
			}else if(CmFinoFIX.TransferFailureReason_BankAccountTopupRejectedByOperator.equals(pct.getTransferFailureReason())){
				bSendReversal	=	true;
			}else if(CmFinoFIX.TransferFailureReason_MobileAgentRechargeToOperatorFailed.equals(pct.getTransferFailureReason())){
				bFinal	=	false;
			}else if(CmFinoFIX.TransferFailureReason_BankAccountTopupNotConnectedToServiceProvider.equals(pct.getTransferFailureReason())){
				bSendReversal	=	true;
			}else if(CmFinoFIX.TransferFailureReason_BankAccountTopupToBankExpired.equals(pct.getTransferFailureReason())){
				bSendReversal	=	true;
			}else if(CmFinoFIX.TransferFailureReason_BankAccountTopupToOperatorExpired.equals(pct.getTransferFailureReason())){
				bFinal	=	false;
				bSendReversal	=	true;
			}else if(CmFinoFIX.TransferFailureReason_MobileAgentRechargeToOperatorExpired.equals(pct.getTransferFailureReason())){
				bFinal	=	false;
			}else if(CmFinoFIX.TransferFailureReason_ShareLoadToOperatorExpired.equals(pct.getTransferFailureReason())){
				bFinal	=	false;
			}else if(CmFinoFIX.TransferFailureReason_ShareLoadNotConnectedToServiceProvider.equals(pct.getTransferFailureReason())){
				bFinal = true;
			}else if(CmFinoFIX.TransferFailureReason_MobileAgentRechargeNotConnectedToServiceProvider.equals(pct.getTransferFailureReason())){
				bFinal = true;
			}else if(CmFinoFIX.TransferFailureReason_PendingAtOperator.equals(pct.getTransferFailureReason())){
				bFinal = false;
			}else if(CmFinoFIX.TransferFailureReason_BillPaymentToBankExpired.equals(pct.getTransferFailureReason())){
				bSendReversal	=	true;
			}else if(CmFinoFIX.TransferFailureReason_BillPaymentTopupToBankExpired.equals(pct.getTransferFailureReason())){
				bSendReversal	=	true;
			}*/

/*			if(pct.getCSRAction() != null){
				//transaction resolved so reversal not required
				bSendReversal = false;
			}
*/
			if(pct.getBankReversalResponseCode() != null){
				//the bank reversal request has been sent, then we don't have to send it again.
				bSendReversal = false;
			}

			if(bFinal)
			{
				if(!bSendReversal){
						//if no bank reversal is required, we are going to revert right here
						//otherwise we are going to do local reversal when sending bank reversal
					if(pct.getLocalbalancerevertrequired()!=null && pct.getLocalbalancerevertrequired())
					{
						pct.setLocalrevertrequired(CmFinoFIX.Boolean_False);
						pct.setLocalbalancerevertrequired(CmFinoFIX.Boolean_False);
						pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
						pct.setEndtime(new Timestamp());
						coreDataWrapper.save(pct);
						getCommodityTransferService().movePctToCt(pct);
						
//						bankService.onRevertOfTransferConfirmation(pct, true);
					}
					else if(pct.getLocalrevertrequired()!=null	&&	pct.getLocalrevertrequired())
					{
						pct.setTransferstatus(CmFinoFIX.TransferStatus_Failed);
						pct.setLocalrevertrequired(CmFinoFIX.Boolean_False);
						pct.setEndtime(new Timestamp());
						coreDataWrapper.save(pct);
						getCommodityTransferService().movePctToCt(pct);
						
//						bankService.onRevertOfTransferInquiry(pct, true);
					}else {
						log.info("Moving PCT to CT: "+pct.getId());
						getCommodityTransferService().movePctToCt(pct);
					}
				}else{
					//update pct status to failed and failure reason and sctl status to pending

					coreDataWrapper.save(pct);
					transactionChargingService.setPendingStatus(pct.getId().longValue());

					// Check if the Transaction is related to Bulk Transfer then do the changes in BulkUpload table also
					pendingBulkTransfer(pct);
				}
			}else if(!bSendReversal){
				//Due to db issues pct could have been updated but move to ct failed, such cases are handled here
				log.info("Moving PCT to CT: "+pct.getId());
				getCommodityTransferService().movePctToCt(pct);
			}
		}
		return bFinal;
	}

	private void pendingBulkTransfer(PendingCommodityTransfer pct) {
		log.info("Check whether the Pending transaction is of type Bulk Transfer...");
		ChargeTxnCommodityTransferMapDAO cTxnCommodityTransferMapDAO = DAOFactory.getInstance().getTxnTransferMap();
		ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
		query.setCommodityTransferID(pct.getId().longValue());
		List<ChargeTxnCommodityTransferMap> ctTxnCommodityTransferMap = cTxnCommodityTransferMapDAO.get(query);
		Long sctlId = ctTxnCommodityTransferMap!=null&&!ctTxnCommodityTransferMap.isEmpty()?ctTxnCommodityTransferMap.get(0).getSctlid().longValue():null;

		// Check if the Transaction is related to Bulk Transfer then do the changes in BulkUpload table also
		if (sctlId != null) {
			if (CmFinoFIX.TransactionUICategory_Bulk_Transfer.equals(pct.getUicategory())) {
				log.info("Changing the Bulk Transfer Status to Pending...");
				BulkUploadDAO buDAO = DAOFactory.getInstance().getBulkUploadDAO();
				BulkUpload bulkUpload = buDAO.getBySCTLId(sctlId);
				if (bulkUpload != null) {
					bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Pending);
					bulkUpload.setDeliverydate(new Timestamp());
					buDAO.save(bulkUpload);
					// Send Pending Notification to Partner
					NotificationWrapper notification = new NotificationWrapper();
					String mdn = bulkUpload.getMdn();
                    SystemParameters langSystemParam = DAOFactory.getInstance().getSystemParameterDao().getSystemParameterByName(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
                    Integer language = Integer.parseInt(langSystemParam.getParametervalue());
					SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
					if(smdn != null)
					{
						language = (int)smdn.getSubscriber().getLanguage();
						notification.setFirstName(smdn.getSubscriber().getFirstname());
						notification.setLastName(smdn.getSubscriber().getLastname());
					}
					notification.setLanguage(language);
					notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
					notification.setCode(CmFinoFIX.NotificationCode_BulkTransferRequestPendingToPartner);
					notification.setBulkTransferId(bulkUpload.getId().longValue());
					notification.setSctlID(bulkUpload.getServicechargetransactionlogid().longValue());


					String message = notificationMessageParserService.buildMessage(notification,true);
					smsService.setDestinationMDN(mdn);
					smsService.setMessage(message);
					smsService.setNotificationCode(notification.getCode());
					smsService.setSctlId(sctlId);
					smsService.asyncSendSMS();
				}
			}
			else if (CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer.equals(pct.getUicategory())) {
				// Nothing to do
			}
			else if (CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer.equals(pct.getUicategory())) {
				// Nothing to do
			}
		}
	}

	private boolean checkExpiredTransfer(PendingCommodityTransfer pct) {
		Timestamp now = new Timestamp();
		if (!(CmFinoFIX.TransferStatus_Completed.equals(pct.getTransferstatus()))) {
			if ((pct.getStarttime().getTime() + pct.getExpirationtimeout()) < now.getTime()
						&&!(CmFinoFIX.TransferStatus_Pending.equals(pct.getTransferstatus()))) {
				if (pct.getTransferstatus()==(CmFinoFIX.TransferStatus_TransferInquirySentToBank)) {
					pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_BankAccountTransferInquiryToBankExpired));
				} else if (pct.getTransferstatus()==(CmFinoFIX.TransferStatus_ConfirmationPromptSentToSubscriber)) {
					pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_ConfirmationPromptToSubscriberExpired));
				} else if (pct.getTransferstatus()==(CmFinoFIX.TransferStatus_MoneyTransaferSentToBank)) {
					pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_MoneyTransferToBankExpired));
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
				pct.setTransferstatus(CmFinoFIX.TransferStatus_Pending);
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

/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.BulkUploadQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.scheduler.service.BulkTransferSchedulerService;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.BulkUploadService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.PocketService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.task.BulkTransferJob;
import com.mfino.transactionapi.handlers.money.BulkTransferHandler;
import com.mfino.transactionapi.handlers.money.BulkTransferInquiryHandler;
import com.mfino.transactionapi.service.BulkTransferService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.EncryptionUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("BulkTransferSchedulerServiceImpl")
public class BulkTransferSchedulerServiceImpl  implements BulkTransferSchedulerService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("BulkTransferHandlerImpl")
	private BulkTransferHandler bulkTransferHandler;
	
	@Autowired
	@Qualifier("BulkTransferInquiryHandlerImpl")
	private BulkTransferInquiryHandler bulkTransferInquiryHandler;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private  BulkUploadEntryService bulkUploadEntryService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private  ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("BulkTransferServiceImpl")
	private BulkTransferService btService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionsLogService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	public void processBulkTransfer() {
		log.info("Processing of Bulk Transfer Requets :: BEGIN");
		processApprovedBulkTransfer();
		checkCompletedBulkTransferForRevertAmount();
		log.info("Processing of Bulk Transfer Requets :: END");		
	}
	
	/**
	 * Process the Approved Bulk Transfers .
	 */
	private void processApprovedBulkTransfer() {
		log.info("processApprovedBulkTransfer :: BEGIN");
		try {
			BulkUploadQuery query = new BulkUploadQuery();
			query.setFileStatus(CmFinoFIX.BulkUploadDeliveryStatus_Approved);
			List<BulkUpload> lstBulkUpload = bulkUploadService.getByQuery(query);
			if (CollectionUtils.isNotEmpty(lstBulkUpload)) {
				for (BulkUpload bulkUpload: lstBulkUpload) {
					schedulePayment(bulkUpload);
				}
			}
		} catch (Exception e) {
			log.error("Error: While Processing the Approved bulk transfers " + e.getMessage(), e);
		} 
		log.info("processApprovedBulkTransfer :: END");
	}
	
	/**
	 * Check the Completed Bulk Transfers for Failed / Expired Transactions.
	 */
	private void checkCompletedBulkTransferForRevertAmount() {
		log.info("checkCompletedBulkTransferForRevertAmount:: BEGIN");
		try {
			BulkUploadQuery query = new BulkUploadQuery();
			query.setFileStatus(CmFinoFIX.BulkUploadDeliveryStatus_Complete);
			List<BulkUpload> lstBulkUpload = bulkUploadService.getByQuery(query);
			if (CollectionUtils.isNotEmpty(lstBulkUpload)) {
				for (BulkUpload bulkUpload: lstBulkUpload) {
					try {
						calculateRevertAmount(bulkUpload);
					} catch (Exception e) {
						log.error("Error: While calculating the Revert Amount for Bulk Upload : " + bulkUpload.getID(), e);
					}
				}
			}
		} catch (Exception e) {
			log.error("Error: While checking the completed bulk transfers " + e.getMessage(), e);
		} 
		log.info("checkCompletedBulkTransferForRevertAmount:: END");
	}
	
	/**
	 * Calculate the is there any amount to be reverted as part of Failed / Expired Transactions.
	 * @param bulkUpload
	 * @param buDAO
	 */
	private void calculateRevertAmount(BulkUpload bulkUpload){
		int failedCount = 0;
		int pendingCount = 0;
		BigDecimal failedAmount = BigDecimal.ZERO;
		BigDecimal revertAmount = BigDecimal.ZERO;
		log.info("Checking the Bulk transfer " + bulkUpload.getID() + " for expired  / Failed / Pending transfers.");
		
		if (bulkUpload.getRevertAmount() != null){
			revertAmount = bulkUpload.getRevertAmount();
		}
		
		List<BulkUploadEntry> bulkUploadEntries = bulkUploadEntryService.getBulkUploadEntriesForBulkUpload(bulkUpload.getID());
		if (CollectionUtils.isNotEmpty(bulkUploadEntries)) {
			for (BulkUploadEntry bue: bulkUploadEntries) {
				boolean isModified = false;
				if (CmFinoFIX.TransactionsTransferStatus_Pending.equals(bue.getStatus())) {
					pendingCount ++;
				}
				else if (CmFinoFIX.TransactionsTransferStatus_Failed.equals(bue.getStatus())) {
					bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Reversed);
					isModified = true;
					failedAmount = failedAmount.add(bue.getAmount());
					failedCount++;
					revertAmount = revertAmount.add(bue.getAmount());
				}
				if (CmFinoFIX.TransactionsTransferStatus_Expired.equals(bue.getStatus())) {
					bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Reversed);
					isModified = true;
					revertAmount = revertAmount.add(bue.getAmount());
				}
				if (isModified) {
					bulkUploadEntryService.saveBulkUploadEntry(bue);
				}
			}
		}
		
//		bulkUpload.setFailedTransactionsCount(bulkUpload.getFailedTransactionsCount() + failedCount);
//		bulkUpload.setSuccessAmount(bulkUpload.getSuccessAmount().subtract(failedAmount));
		bulkUpload.setRevertAmount(revertAmount);
		
		if ((pendingCount == 0) && (revertAmount.compareTo(BigDecimal.ZERO) > 0) ) {
			log.info("Reverting the Amount " + revertAmount.toPlainString()
					+ " as part of Failed / Expired Transfers for Bulk upload request " + bulkUpload.getID());
			bulkUpload = doReverseBulkTransfer(bulkUpload, revertAmount);
		}
		bulkUploadService.save(bulkUpload);
	}
	
	/**
	 * Revert the Amount from Suspense pocket to Source pocket
	 * @param bulkUpload
	 * @param amount
	 * @return
	 */
	private BulkUpload doReverseBulkTransfer(BulkUpload bulkUpload, BigDecimal amount) {
		
		Pocket destPocket = bulkUpload.getPocketBySourcePocket();
		Pocket sourcePocket = pocketService.getSuspencePocket(bulkUpload.getUser());
		String pin = EncryptionUtil.getDecryptedString(bulkUpload.getPin());
		ChannelCode channelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		
		log.info("Creating the Reverse Bulk Transfer Inquiry object");

		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(bulkUpload.getMDN());
		transactionDetails.setDestMDN(bulkUpload.getMDN());
		transactionDetails.setAmount(amount);
		transactionDetails.setSourcePIN(pin);
		transactionDetails.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_SETTLE_BULK_TRANSFER);
		transactionDetails.setChannelCode(channelCode.getChannelCode());
		transactionDetails.setSrcPocketId(sourcePocket.getID());
		transactionDetails.setDestinationPocketId(destPocket.getID());
		transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		transactionDetails.setCc(channelCode);
		

		XMLResult result = (XMLResult)bulkTransferInquiryHandler.handle(transactionDetails);
		
		if (result != null ) {
			// Setting the Main SCTL of the Bulk transfer as parent SCTL for the Reverse/Settlement Bulk Transfer.
			Long sctlId = result.getSctlID();
			if(sctlId!=null){
			ServiceChargeTransactionLog sctl = serviceChargeTransactionsLogService.getById(sctlId);
			sctl.setParentSCTLID(bulkUpload.getServiceChargeTransactionLogID());
			serviceChargeTransactionsLogService.save(sctl);
			}
			
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(result.getCode())) {
				log.info("Creating the Reverse Bulk Transfer Confirmation object");

				transactionDetails = new TransactionDetails();
				transactionDetails.setSourceMDN(bulkUpload.getMDN());
				transactionDetails.setDestMDN(bulkUpload.getMDN());
				transactionDetails.setSrcPocketId(sourcePocket.getID());
				transactionDetails.setDestinationPocketId(destPocket.getID());
				transactionDetails.setServletPath(CmFinoFIX.ServletPath_Subscribers);
				transactionDetails.setChannelCode(channelCode.getChannelCode());
				transactionDetails.setParentTxnId(result.getParentTransactionID());
				transactionDetails.setTransferId(result.getTransferID());
				transactionDetails.setConfirmString(CmFinoFIX.Boolean_True.toString());
				transactionDetails.setCc(channelCode);

				result = (XMLResult)bulkTransferHandler.handle(transactionDetails);
				
				if (result != null && result.getDetailsOfPresentTransaction()!= null) {
					CommodityTransfer ct = result.getDetailsOfPresentTransaction();
					if (CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) {
						bulkUpload.setRevertAmount(BigDecimal.ZERO);
						log.info("Reverting the Amount " + amount.toPlainString()
								+ " as part of Failed / Expired Transfers for Bulk upload request " + bulkUpload.getID() + " is Success");
					}
					else {
						log.info("Reverting the Amount " + amount.toPlainString()
								+ " as part of Failed / Expired Transfers for Bulk upload request " + bulkUpload.getID() + " is Failed");
					}
				}
				// No Response from Back end after confirming the transaction
				else {
					bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Settlement_Pending);
					bulkUpload.setDeliveryDate(new Timestamp());
					log.info("Reverting the Amount " + amount.toPlainString()
							+ " as part of Failed / Expired Transfers for Bulk upload request " + bulkUpload.getID() + " is Pending");
				}
			}
			else {
				log.info("Reverting the Amount " + amount.toPlainString()
						+ " as part of Failed / Expired Transfers for Bulk upload request " + bulkUpload.getID() + " is failed because of inquiry is failed");
			}
		}
		else {
			log.info("Reverting the Amount " + amount.toPlainString()
					+ " as part of Failed / Expired Transfers for Bulk upload request " + bulkUpload.getID() + " is failed because of inquiry result is null");
		}
		
		return bulkUpload;
	}

	
	private void schedulePayment(BulkUpload bulkUpload){
		
		Timestamp scheduledTime = bulkUpload.getPaymentDate();
		Date todaysDate = new Timestamp();
		if(DateUtils.isSameDay(todaysDate, scheduledTime) || (scheduledTime.before(todaysDate)))
		{
			BulkTransferJob job = new BulkTransferJob();
			job.setBtService(btService);
			job.setBulkuploadService(bulkUploadService);
			job.setPocketService(pocketService);
			job.doBulkTransfer(bulkUpload);
		}
	}
	
}

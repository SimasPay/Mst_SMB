/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.mce.backend.impl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.UserDAO;
import com.mfino.dao.query.PendingTransactionsEntryQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.PendingTxnsEntry;
import com.mfino.domain.PendingTxnsFile;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SmsTransactionLog;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.mce.backend.BulkPendingResolveService;
import com.mfino.mce.backend.PendingClearanceService;
import com.mfino.mce.core.util.BackendResponse;

/**
 * 
 * @author Maruthi
 */
public class BulkPendingResolveServiceImpl extends BaseServiceImpl implements
		BulkPendingResolveService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static Map<Integer, String> errorCodesMap = new HashMap<Integer, String>();
	private SessionFactory sessionFactory;
	private PendingClearanceService pendingClearanceService;
	

	static {
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Success,"Success");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Line,"Invalid Line");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Record_Not_Found,"Invalid Pending Transaction Reference ID");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Can_Only_Be_Marked_As_Failed,"This transfer can only be marked Failed");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Amount,"Entered amount does not match with pending transaction amount");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_SourceMDN,"Entered mdn does not match with pending transaction sourcemdn");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Company,"Invalid company for the transaction");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_Amount,"Invalid amount in the input file");
		errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_TransferID,"Invalid transferID in the input file");
	}


	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void resolve() {
		log.info("BulkPendingResolveServiceImpl:: resolve() Begin");

		try {
			List<PendingTxnsFile> pendingFiles = coreDataWrapper.getPendingUploadedFiles();
			List<PendingTxnsFile> processingFiles = coreDataWrapper.getProcessingFiles();
			log.info("Number Pending Uploaded Files to be processed = "	+ pendingFiles.size());
			log.info("Number Pending Processing Files to be processed = "+ processingFiles.size());

			for (PendingTxnsFile resolvePendingFile : pendingFiles) {
				processFile(resolvePendingFile, false);
			}

			long currentTime = System.currentTimeMillis();

			for (PendingTxnsFile resolvePendingFile : processingFiles) {
				if (resolvePendingFile.getLastupdatetime().getTime() < currentTime - 30 * 60 * 1000) {
					processFile(resolvePendingFile, true);
				}
			}
		} catch (Exception exp) {
			log.error("Failed processing BulkResolveFiles", exp);
		} 
		
		log.info("BulkPendingResolveServiceImpl:: resolve() END");
	}


	private void saveFailedRecord(int linecount, long ptid, int status,
			String resolvefailurereason, BigDecimal amount, Integer noticode,
			PendingCommodityTransfer ct) {
		PendingTxnsEntry fentry = new PendingTxnsEntry();
		fentry.setLinenumber(linecount);
		fentry.setTransactionsfileid(new BigDecimal(ptid));
		fentry.setStatus(status);
		fentry.setResolvefailurereason(resolvefailurereason);
		fentry.setAmount(amount);
		fentry.setNotificationcode(Long.valueOf(noticode));
		if (ct != null) {
			fentry.setTransferid(ct.getId());
			fentry.setSourcemdn(ct.getSourcemdn());
			fentry.setDestmdn(ct.getDestmdn());
		}
		coreDataWrapper.save(fentry);
	}

	private void saveRecord(int linecount, long ptid, int status,
			String resolvefailurereason, Integer noticode,
			ServiceChargeTxnLog sctl) {
		PendingTransactionsEntryQuery query = new PendingTransactionsEntryQuery();
		query.setPendingTransactionsFileID(ptid);
		query.setLineNumber(linecount);
		List<PendingTxnsEntry> fentry = coreDataWrapper.get(query);
		if (fentry.size() > 0) {
			PendingTxnsEntry record = fentry.get(0);
			record.setStatus(status);
			record.setResolvefailurereason(resolvefailurereason);
			record.setNotificationcode(Long.valueOf(noticode));
			log.info("Saving PendingTransactionsEntry with Linecount,status,resolvefailureason"
					+ linecount + "," + status + "," + resolvefailurereason);
			if (sctl != null) {
				log.info("Transfer ID, SourceMDN, DestMDN" + sctl.getId() + ","
						+ sctl.getSourcemdn() + "," + sctl.getDestmdn());
				record.setTransferid(sctl.getId());
				record.setSourcemdn(sctl.getSourcemdn());
				record.setDestmdn(sctl.getDestmdn());
			}
			coreDataWrapper.save(record);
			log.info("Saved the record");
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void processFile(PendingTxnsFile resolvePendingFile,boolean isProcessingFile) {
		String strLine = null;
		int linecount = 0;
		int errorLineCount = 0;
		long transferID;
		long ptID;
		String mdn;
		BigDecimal amount;
		int status = CmFinoFIX.PendingTransationsEntryStatus_pending;
		try {

			if (isProcessingFile) {
				linecount = coreDataWrapper.getProcessedLineCount(resolvePendingFile);
			} else {
				resolvePendingFile.setUploadfilestatus(CmFinoFIX.UploadFileStatus_Processing);
				coreDataWrapper.save(resolvePendingFile);
			}

			Integer resolveAction = resolvePendingFile.getResolveas().intValue();
			BufferedReader bufferedReader = new BufferedReader(	new StringReader(resolvePendingFile.getFiledata().getSubString(0, ((Long)resolvePendingFile.getFiledata().length()).intValue())));
			ptID = resolvePendingFile.getId().longValue();
			// move the file pointer so many lines
			if (linecount > 0) {
				int tempLineCount = 0;
				while (tempLineCount <= linecount
						&& (strLine = bufferedReader.readLine()) != null) {
					// no need to chk for empty lines
					// if (strLine.length() == 0 || tempLineCount == 1) {
					// // skip empty lines
					// continue;
					// } else {
					tempLineCount++;
					// }
				}
			}

			while ((strLine = bufferedReader.readLine()) != null) {
				if (strLine.length() == 0
						|| strLine.toLowerCase().startsWith("resolve")) {
					// skip empty lines
					continue;
				}
				linecount++;
				String[] result = strLine.split(",");
				if (result.length == 3) {
					try {
						transferID = Long.parseLong(result[0].trim());
					} catch (Exception exp) {
						// Invalid transferID
						log.error("Invalid TransferID = " + result[0].trim(),exp);
						errorLineCount++;
						status = CmFinoFIX.PendingTransationsEntryStatus_failed;
						String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_TransferID);
						// since record hasn't saved yet in pending transactions
						// entry we are using saveFailedRecord
						saveFailedRecord(linecount, ptID, status,resolvefailurereason, new BigDecimal(-1), null,null);
						continue;
					}
					mdn = result[1].trim();
					try {
						amount = new BigDecimal(result[2].trim());
					} catch (Exception exp) {
						// Invalid amount
						log.error("Invalid Amount = " + result[2].trim(), exp);
						errorLineCount++;
						status = CmFinoFIX.PendingTransationsEntryStatus_failed;
						String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_Amount);
						// since record hasn't saved yet in pending transactions
						// entry we are using saveFailedRecord
						saveFailedRecord(linecount, ptID, status,resolvefailurereason, new BigDecimal(-1), null,null);
						continue;
					}

					ServiceChargeTxnLog sctl = coreDataWrapper.getSCTLById(transferID, LockMode.UPGRADE);
					// Create a PendingTransactionEntry here and persist since
					// we got a proper line from the file
					PendingTxnsEntry pendingTransactionsEntry = new PendingTxnsEntry();
					log.info("Processing Transactions File ID = " + ptID);
					log.info("Processing Transfer ID = " + transferID);
					pendingTransactionsEntry.setTransactionsfileid(new BigDecimal(ptID));
					pendingTransactionsEntry.setTransferid(new BigDecimal(transferID));
					pendingTransactionsEntry.setSourcemdn(mdn);
					pendingTransactionsEntry.setAmount(amount);
					pendingTransactionsEntry.setLinenumber(linecount);
					pendingTransactionsEntry.setStatus(CmFinoFIX.PendingTransationsEntryStatus_pending);
					coreDataWrapper.save(pendingTransactionsEntry);

					if (sctl == null||(!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus()))) {
						log.info("Invalid Transfer ID = " + transferID);
						errorLineCount++;
						status = CmFinoFIX.PendingTransationsEntryStatus_failed;
						String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Record_Not_Found);
						saveRecord(linecount, ptID, status,resolvefailurereason, null, null);
						continue;
					}

					// if(pendingCommodityTransfer.getCompany() == null) {
					// log.info("Invalid Company for the record = " +
					// transferID);
					// errorLineCount++;
					// status = CmFinoFIX.PendingTransationsEntryStatus_failed;
					// String resolvefailurereason =
					// errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Company);
					// saveRecord(linecount, ptID, status, resolvefailurereason,
					// null, null);
					// continue;
					// }
					//
					// if(!pendingCommodityTransfer.getCompany().getID().equals(resolvePendingFile.getCompany().getID()))
					// {
					// log.info("Invalid Company for the record = " +
					// transferID);
					// errorLineCount++;
					// status = CmFinoFIX.PendingTransationsEntryStatus_failed;
					// String resolvefailurereason =
					// errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Company);
					// saveRecord(linecount, ptID, status, resolvefailurereason,
					// null, null);
					// continue;
					// }
					
					if (sctl.getTransactionamount().compareTo(amount) != 0) {
						log.info("Mismatch of Amount for transferid "+ ptID+ " Entered Value "+ amount
								+ "Transaction Amount "+ sctl.getTransactionamount().longValue());
						status = CmFinoFIX.PendingTransationsEntryStatus_failed;
						String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Amount);
						saveRecord(linecount, ptID, status,resolvefailurereason, null, null);
						continue;
					}
					if (!mdn.equals(sctl.getSourcemdn())) {
						log.info("Mismatch of sourceMDN for transferid" + ptID
								+ "Entered MDN" + mdn + "Transaction SourceMDN"
								+ sctl.getSourcemdn());
						status = CmFinoFIX.PendingTransationsEntryStatus_failed;
						String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_SourceMDN);
						saveRecord(linecount, ptID, status,resolvefailurereason, null, null);
						continue;
					}
					if (sctl != null) {
							BackendResponse response = (BackendResponse) sendPendingCommmodityRequest(sctl,	resolvePendingFile);
							if (CmFinoFIX.ResponseCode_Revert_Success.equals(response.getResult())) {
								status = CmFinoFIX.PendingTransationsEntryStatus_success;
							} else if (CmFinoFIX.ResponseCode_Revert_Failure.equals(response.getResult())) {
								status = CmFinoFIX.PendingTransationsEntryStatus_failed;
							}
							log.info("Processed Transfer ID = " + transferID+ "With Status " + status);
							saveRecord(linecount, ptID, status,	response.getDescription(), null,sctl);
					}
				} else {
					log.info("Invalid Line = " + linecount + " Line = "	+ strLine);
					errorLineCount++;
					amount = new BigDecimal(-1);
					status = CmFinoFIX.PendingTransationsEntryStatus_failed;
					String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Line);
					saveFailedRecord(linecount, ptID, status,resolvefailurereason, amount, null, null);
					continue;
				}

			}
			// done with the processing the file change the status and update
			// the errorlinecount and save the record

			log.info("Done with Processing the File ID= " + ptID);
			resolvePendingFile.setUploadfilestatus(CmFinoFIX.UploadFileStatus_Processed);
			resolvePendingFile.setErrorlinecount(Long.valueOf(errorLineCount));
			coreDataWrapper.save(resolvePendingFile);
		} catch (Exception exp) {
			log.error("Failed" + exp.getMessage(), exp);
		}

	}

	private CFIXMsg sendPendingCommmodityRequest(ServiceChargeTxnLog sctl,PendingTxnsFile resolvePendingFile) {

		CMPendingCommodityTransferRequest newMsg = new CMPendingCommodityTransferRequest();
		String userName = resolvePendingFile.getCreatedby();
		UserDAO userDAO = coreDataWrapper.getUserDAO();
		UserQuery query = new UserQuery();
		query.setUserName(userName);
		List<User> results = (List<User>) userDAO.get(query);
		if (results.size() > 0) {
			User userObj = results.get(0);
			newMsg.setCSRUserID(userObj.getId().longValue());
		}
		newMsg.setLoginName(userName);
		if (sctl.getMfinoServiceProvider() != null) {
			newMsg.setMSPID(sctl.getMfinoServiceProvider().getId().longValue());
		}
		/*if (sctl.getTransactionID() != null) {
			newMsg.setParentTransactionID(sctl.getTransactionID());
		}*/
		if (sctl.getSourcemdn() != null) {
			newMsg.setSourceMDN(sctl.getSourcemdn());
		}
		if (sctl.getTransactionid() != null) {
			newMsg.setTransactionID(sctl.getTransactionid().longValue());
		}
		newMsg.setCSRComment("Bulk Resolve Pending Transactions ");
		newMsg.setTransactionID(sctl.getId().longValue());
		if (CmFinoFIX.ResolveAs_success.equals(resolvePendingFile.getResolveas())) {
			newMsg.setCSRAction(CmFinoFIX.CSRAction_Complete);
		} else {
			newMsg.setCSRAction(CmFinoFIX.CSRAction_Cancel);
		}
		newMsg.setCSRUserName(userName);

		return pendingClearanceService.processMessage(newMsg);

	}

	
	public PendingClearanceService getPendingClearanceService() {
		return pendingClearanceService;
	}

	public void setPendingClearanceService(
			PendingClearanceService pendingClearanceService) {
		this.pendingClearanceService = pendingClearanceService;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}

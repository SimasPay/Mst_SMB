/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.application;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadEntryDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PendingTransactionsEntryDAO;
import com.mfino.dao.PendingTransactionsFileDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.BulkUploadEntryQuery;
import com.mfino.dao.query.PendingTransactionsEntryQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.PendingTransactionsEntry;
import com.mfino.domain.PendingTransactionsFile;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.mailer.SendNotificationToSubscriber;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author Raju
 */
public class BulkPendingResolveTool {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static Map<Integer, String> errorCodesMap = new HashMap<Integer, String>();

    static {
        // this is required before start decoding fix messages
        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
    }

    static {
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Success, "Success");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Line, "Invalid Line");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Record_Not_Found, "Invalid Pending Transaction Reference ID");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Can_Only_Be_Marked_As_Failed, "This transfer can only be marked Failed");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Amount, "Entered amount does not match with pending transaction amount");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_SourceMDN, "Entered mdn does not match with pending transaction sourcemdn");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Company, "Invalid company for the transaction");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_Amount, "Invalid amount in the input file");
        errorCodesMap.put(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_TransferID, "Invalid transferID in the input file");
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    private void saveFailedRecord(int linecount, long ptid, int status, String resolvefailurereason, BigDecimal amount, Integer noticode, PendingCommodityTransfer ct) {
        PendingTransactionsEntryDAO dao = DAOFactory.getInstance().getPendingTransactionsEntryDAO();
        PendingTransactionsEntry fentry = new PendingTransactionsEntry();
        fentry.setLineNumber(linecount);
        fentry.setTransactionsFileID(ptid);
        fentry.setStatus(status);
        fentry.setResolveFailureReason(resolvefailurereason);
        fentry.setAmount(amount);
        fentry.setNotificationCode(noticode);
        if (ct != null) {
            fentry.setTransferID(ct.getID());
            fentry.setSourceMDN(ct.getSourceMDN());
            fentry.setDestMDN(ct.getDestMDN());
        }
        dao.save(fentry);
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    private void saveRecord(int linecount, long ptid, int status, String resolvefailurereason, Integer noticode, PendingCommodityTransfer ct) {
        PendingTransactionsEntryDAO dao = DAOFactory.getInstance().getPendingTransactionsEntryDAO();
        PendingTransactionsEntryQuery query = new PendingTransactionsEntryQuery();
        query.setPendingTransactionsFileID(ptid);
        query.setLineNumber(linecount);
        List<PendingTransactionsEntry> fentry = dao.get(query);
        if (fentry.size() > 0) {
            PendingTransactionsEntry record = fentry.get(0);
            record.setStatus(status);
            record.setResolveFailureReason(resolvefailurereason);
            record.setNotificationCode(noticode);
            log.info("Saving PendingTransactionsEntry with Linecount,status,resolvefailureason" + linecount +","+status + ","+resolvefailurereason);
            if (ct != null) {
            	log.info("Transfer ID, SourceMDN, DestMDN"+ ct.getID()+","+ct.getSourceMDN()+","+ct.getDestMDN());
                record.setTransferID(ct.getID());
                record.setSourceMDN(ct.getSourceMDN());
                record.setDestMDN(ct.getDestMDN());
            }
            dao.save(record);
            log.info("Saved the record"); 
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void uploadData() throws Throwable {
        try {
            PendingTransactionsFileDAO bulkResolveFileDAO = DAOFactory.getInstance().getPendingTransactionsFileDAO();
            List<PendingTransactionsFile> pendingFiles = bulkResolveFileDAO.getPendingUploadedFiles();
            List<PendingTransactionsFile> processingFiles = bulkResolveFileDAO.getProcessingFiles();
            log.info("Number Pending Uploaded Files to be processed = " + pendingFiles.size());
            log.info("Number Pending Processing Files to be processed = " + processingFiles.size());
            for (PendingTransactionsFile resolvePendingFile : pendingFiles) {
            	processFile(resolvePendingFile, false);
            }
            long currentTime = System.currentTimeMillis();
            for (PendingTransactionsFile resolvePendingFile : processingFiles) {
            	if(resolvePendingFile.getLastUpdateTime().getTime() <  currentTime - 30 * 60 * 1000) {
            		processFile(resolvePendingFile,true);
            	}
            }
        } catch (Exception exp) {
            log.error("Failed", exp);
        }
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    private void processFile(PendingTransactionsFile resolvePendingFile ,boolean isProcessingFile) {
    	PendingCommodityTransferDAO pendingCommodityTransferDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
        PendingTransactionsEntryDAO pendingTransactionsEntryDAO = DAOFactory.getInstance().getPendingTransactionsEntryDAO();
        PendingTransactionsFileDAO bulkResolveFileDAO = DAOFactory.getInstance().getPendingTransactionsFileDAO();
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
            	linecount = pendingTransactionsEntryDAO.getProcessedLineCount(resolvePendingFile);
            }
        	Integer resolveAction = resolvePendingFile.getResolveAs();
            resolvePendingFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Processing);
            bulkResolveFileDAO.save(resolvePendingFile);
            BufferedReader bufferedReader = new BufferedReader(new StringReader(resolvePendingFile.getFileData()));
            ptID = resolvePendingFile.getID();
            // move the file pointer so many lines
            if(linecount > 0) {
            	int tempLineCount = 0; 
            	while (tempLineCount <= linecount && (strLine = bufferedReader.readLine()) != null) {
            		//no need to chk for empty lines
//            		if (strLine.length() == 0 || tempLineCount == 1) {
//                        // skip empty lines
//                        continue;  
//                    } else {  
                    	tempLineCount++;
                    //}
            	}
            }
             while ((strLine = bufferedReader.readLine()) != null) {
                 if (strLine.length() == 0 || strLine.toLowerCase().startsWith("resolve")) {
                    // skip empty lines
                    continue;
                }
                 linecount++;
                String[] result = strLine.split(",");
                if (result.length == 3) {
                	try {
                	transferID = Long.parseLong(result[0].trim());
                	}catch (Exception exp) {
						// Invalid transferID 
                    	log.error("Invalid TransferID = " + result[0].trim(), exp);
                        errorLineCount++;
                        status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                        String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_TransferID);
                        //since record hasn't saved yet in pending transactions entry we are using saveFailedRecord
                        saveFailedRecord(linecount, ptID, status, resolvefailurereason, new BigDecimal(-1), null, null);
                        continue;
					}
                    mdn = result[1].trim();
                    try {
//                    	amount = Long.parseLong(result[2].trim());
                    	amount = new BigDecimal(result[2].trim());
                    } catch (Exception exp) {
						// Invalid amount
                    	log.error("Invalid Amount = " + result[2].trim(),exp);
                        errorLineCount++;
                        status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                        String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Input_Parameter_Amount);
                        //since record hasn't saved yet in pending transactions entry we are using saveFailedRecord
                        saveFailedRecord(linecount, ptID, status, resolvefailurereason, new BigDecimal(-1), null, null);
                        continue;
					}
                    PendingCommodityTransfer pendingCommodityTransfer = pendingCommodityTransferDAO.getById(transferID, LockMode.UPGRADE);
                    // Create a PendingTransactionEntry here and persist since we got a proper line from the file
                    PendingTransactionsEntry pendingTransactionsEntry = new PendingTransactionsEntry();
                    log.info("Processing Transactions File ID = " + ptID);
                    log.info("Processing Transfer ID = " + transferID);
                    pendingTransactionsEntry.setTransactionsFileID(ptID);
                    pendingTransactionsEntry.setTransferID(transferID);
                    pendingTransactionsEntry.setSourceMDN(mdn);
                    pendingTransactionsEntry.setAmount(amount);
                    pendingTransactionsEntry.setLineNumber(linecount);
                    pendingTransactionsEntry.setStatus(CmFinoFIX.PendingTransationsEntryStatus_pending);
                    pendingTransactionsEntryDAO.save(pendingTransactionsEntry);
                    if (pendingCommodityTransfer == null) {
                        log.info("Invalid Transfer ID = " + transferID);
                        errorLineCount++;
                        status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                        String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Record_Not_Found);
                        saveRecord(linecount, ptID, status, resolvefailurereason, null, null);
                        continue;
                    }
                    if(pendingCommodityTransfer.getCompany() == null) {
                    	log.info("Invalid Company for the record = " + transferID);
                        errorLineCount++;
                        status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                        String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Company);
                        saveRecord(linecount, ptID, status, resolvefailurereason, null, null);
                        continue;
                    }
                    if(!pendingCommodityTransfer.getCompany().getID().equals(resolvePendingFile.getCompany().getID())) {
                    	log.info("Invalid Company for the record = " + transferID);
                        errorLineCount++;
                        status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                        String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Company);
                        saveRecord(linecount, ptID, status, resolvefailurereason, null, null);
                        continue;
                    }
//                    if (pendingCommodityTransfer.getAmount().longValue() != amount) {
                      if (pendingCommodityTransfer.getAmount().compareTo(amount)!= 0) {
                        log.info("Mismatch of Amount for transferid" + ptID +"Entered Value" + amount + "Transaction Amount"+pendingCommodityTransfer.getAmount().longValue());
                        status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                        String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Amount);
                        saveRecord(linecount, ptID, status, resolvefailurereason, null, null);
                        continue;
                    }
                    if (!mdn.equals(pendingCommodityTransfer.getSourceMDN())) {
                        log.info("Mismatch of sourceMDN for transferid" + ptID +"Entered MDN" + mdn + "Transaction SourceMDN"+pendingCommodityTransfer.getSourceMDN());
                        status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                        String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_SourceMDN);
                        saveRecord(linecount, ptID, status, resolvefailurereason, null, null);
                        continue;
                    }
                    if (pendingCommodityTransfer != null) {
                        Integer uiCategory = pendingCommodityTransfer.getUICategory();
                        if (!pendingCommodityTransfer.getTransferStatus().equals(CmFinoFIX.TransferStatus_Failed) || !pendingCommodityTransfer.getOperatorActionRequired()) {
                            log.info("Cannot Mark as Complete for this Transfer ID = " + transferID);
                            errorLineCount++;
                            status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                            String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Can_Only_Be_Marked_As_Failed);
                            saveRecord(linecount, ptID, status, resolvefailurereason, null, pendingCommodityTransfer);
                        } else if ((CmFinoFIX.TransactionUICategory_EMoney_CashIn.equals(uiCategory) || CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf.equals(uiCategory))&&CmFinoFIX.ResolveAs_success.equals(resolveAction)) {
                           
                                log.info("Cannot Mark as Complete for this Transfer ID = " + transferID);
                                errorLineCount++;
                                status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                                String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Can_Only_Be_Marked_As_Failed);
                                saveRecord(linecount, ptID, status, resolvefailurereason, null, pendingCommodityTransfer);
                            }
                         else if (CmFinoFIX.TransactionUICategory_MA_Topup.equals(uiCategory) || CmFinoFIX.TransactionUICategory_BulkTopup.equals(uiCategory)) {
                            // Handle the resolve action here
                            pendingCommodityTransfer = pendingCommodityTransferDAO.getById(transferID, LockMode.UPGRADE);
                            status = resolvePendingTransaction(pendingCommodityTransfer, resolvePendingFile);
                            log.info("Processed Transfer ID = " + transferID + "With Status " + status);
                            saveRecord(linecount, ptID, status, null, null, pendingCommodityTransfer);
                        } else {
                            CMJSError errorMsg = sendPendingCommmodityRequest(pendingCommodityTransfer, resolvePendingFile);
                            if (CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())) {
                                status = CmFinoFIX.PendingTransationsEntryStatus_success;
                            } else {
                                status = CmFinoFIX.PendingTransationsEntryStatus_pending;
                            }
                            log.info("Processed Transfer ID = " + transferID + "With Status " + status);
                            saveRecord(linecount, ptID, status, errorMsg.getErrorDescription(), null, pendingCommodityTransfer);
                        }
                    }
                } else {
                	log.info("Invalid Line = " + linecount + " Line = " + strLine);
                    errorLineCount++;
                    amount = new BigDecimal(-1);
                    status = CmFinoFIX.PendingTransationsEntryStatus_failed;
                    String resolvefailurereason = errorCodesMap.get(CmFinoFIX.PendingTransactionsErrors_Invalid_Line);
                    saveFailedRecord(linecount, ptID, status, resolvefailurereason, amount, null, null);
                    continue;
                }
            }
            //done with the processing the file change the status and update the errorlinecount and save the record
            log.info("Done with Processing the File ID= " + ptID);
            resolvePendingFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Processed);
            resolvePendingFile.setErrorLineCount(errorLineCount);
            bulkResolveFileDAO.save(resolvePendingFile);
        } catch (Exception exp) {
        	log.error("Failed" + exp.getMessage(), exp);
        }
    
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    private CMJSError sendPendingCommmodityRequest(
            PendingCommodityTransfer pendingCommodityTransfer, PendingTransactionsFile resolvePendingFile) {

        CMPendingCommodityTransferRequest newMsg = new CMPendingCommodityTransferRequest();
        String userName = resolvePendingFile.getCreatedBy();
        UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
        UserQuery query = new UserQuery();
        query.setUserName(userName);
        List<User> results = (List<User>) userDAO.get(query);
        if (results.size() > 0) {
            User userObj = results.get(0);
            newMsg.setCSRUserID(userObj.getID());
        }
        newMsg.setLoginName(userName);
        if (pendingCommodityTransfer.getmFinoServiceProviderByMSPID() != null) {
            newMsg.setMSPID(pendingCommodityTransfer.getmFinoServiceProviderByMSPID().getID());
        }
        if (pendingCommodityTransfer.getTransactionsLogByTransactionID().getParentTransactionID() != null) {
            newMsg.setParentTransactionID(pendingCommodityTransfer.getTransactionsLogByTransactionID().getParentTransactionID());
        }
        if (pendingCommodityTransfer.getSourceMDN() != null) {
            newMsg.setSourceMDN(pendingCommodityTransfer.getSourceMDN());
        }
        if (pendingCommodityTransfer.getTransactionsLogByTransactionID() != null) {
            newMsg.setTransactionID(pendingCommodityTransfer.getTransactionsLogByTransactionID().getID());
        }
        newMsg.setCSRComment("Bulk Resolve Pending Transactions ");
        newMsg.setTransferID(pendingCommodityTransfer.getID());
        if (CmFinoFIX.ResolveAs_success.equals(resolvePendingFile.getResolveAs())) {
            newMsg.setCSRAction(CmFinoFIX.CSRAction_Complete);
        } else {
            newMsg.setCSRAction(CmFinoFIX.CSRAction_Cancel);
        }
        newMsg.setCSRUserName(userName);
        CommHandler commHandler = new CommHandler();
        CFIXMsg response = commHandler.process(newMsg);
        log.info("Response from multix " + response.DumpFields());
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        if (response != null) {
            if (response instanceof CmFinoFIX.CMJSError) {
                CMJSError errMsg = (CMJSError) response;
                if (errMsg.getErrorCode() == CmFinoFIX.ErrorCode_NoError) {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
                } else {
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    errorMsg.setErrorDescription(errMsg.getErrorDescription());
                }
            }
        }
        return errorMsg;
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    private int resolvePendingTransaction(PendingCommodityTransfer pendingCommodityTransfer, PendingTransactionsFile resolvePendingFile) {
        int returnValue = CmFinoFIX.PendingTransationsEntryStatus_failed;
        String userName = resolvePendingFile.getCreatedBy();
        UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
        UserQuery query = new UserQuery();
        query.setUserName(userName);
        List<User> results = (List<User>) userDAO.get(query);
        User userObj = null;
        if (results.size() > 0) {
            userObj = results.get(0);
        }
        /*CommodityTransferDAO commodityTransferDAO = new CommodityTransferDAO();
        CommodityTransfer commodityTransfer = new CommodityTransfer();*/
        if (CmFinoFIX.ResolveAs_success.equals(resolvePendingFile.getResolveAs())) {
            pendingCommodityTransfer.setCSRAction(CmFinoFIX.CSRAction_Complete);
        } else {
            pendingCommodityTransfer.setCSRAction(CmFinoFIX.CSRAction_Cancel);
        }
        pendingCommodityTransfer.setCSRActionTime(new Timestamp());
        pendingCommodityTransfer.setCSRComment("Bulk Resolve Pending Transactions ");
        if(userObj!=null){
        pendingCommodityTransfer.setCSRUserID(userObj.getID());
        pendingCommodityTransfer.setCSRUserName(userObj.getUsername());
        }
        SubscriberMDN subscriberMDN = pendingCommodityTransfer.getSubscriberMDNBySourceMDNID();
        if (subscriberMDN == null) {
            return CmFinoFIX.PendingTransactionsErrors_SourceMDN_NotFound_For_CSRAction;
        }
        Integer notificationCode = null;
        Integer receiverNotificationCode = null;
        if (CmFinoFIX.CSRAction_Cancel.equals(pendingCommodityTransfer.getCSRAction())) {
            pendingCommodityTransfer.setOperatorActionRequired(false);
        } else {
            if (CmFinoFIX.TransferFailureReason_MobileAgentRechargeToOperatorExpired.equals(pendingCommodityTransfer.getTransferFailureReason()) || CmFinoFIX.TransferFailureReason_MobileAgentRechargeToOperatorFailed.equals(pendingCommodityTransfer.getTransferFailureReason())) {
                notificationCode = CmFinoFIX.NotificationCode_MobileAgentRechargeCompleted;
                receiverNotificationCode = CmFinoFIX.NotificationCode_MobileAgentRechargeCompletedToReceiverMDN;
            }
            pendingCommodityTransfer.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
        }
        returnValue = updatePendingCommodityTransfer(pendingCommodityTransfer);
        NotificationWrapper notificationMsg = new NotificationWrapper();
        notificationMsg.setCompany(pendingCommodityTransfer.getCompany());
        notificationMsg.setLanguage(subscriberMDN.getSubscriber().getLanguage());
        notificationMsg.setFirstName(subscriberMDN.getSubscriber().getFirstName());
        notificationMsg.setLastName(subscriberMDN.getSubscriber().getLastName());
        notificationMsg.setNotificationMethod(subscriberMDN.getSubscriber().getNotificationMethod());
        notificationMsg.setEmailId(subscriberMDN.getSubscriber().getEmail());
        notificationMsg.setDestMDN(subscriberMDN.getMDN());
        notificationMsg.setDestPocket(pendingCommodityTransfer.getPocketBySourcePocketID());
        CommodityTransfer cmdTrans = new CommodityTransfer();
        cmdTrans.copy(pendingCommodityTransfer);
        notificationMsg.setCommodityTransfer(cmdTrans);
        if (pendingCommodityTransfer.getCSRAction().equals(CmFinoFIX.CSRAction_Complete)) {
            if (pendingCommodityTransfer.getSourceApplication().equals(CmFinoFIX.SourceApplication_Phone) || pendingCommodityTransfer.getSourceApplication().equals(CmFinoFIX.SourceApplication_Web) || pendingCommodityTransfer.getSourceApplication().equals(CmFinoFIX.SourceApplication_WebService)) {
                notificationMsg.setCode(notificationCode);
                SendNotificationToSubscriber sendToSub1 = new SendNotificationToSubscriber(notificationMsg);
                sendToSub1.handle();

                notificationMsg.setCode(CmFinoFIX.NotificationCode_Resovle_Transaction_To_Success);
                SendNotificationToSubscriber sendToSub2 = new SendNotificationToSubscriber(notificationMsg);
                sendToSub2.handle();
            }

            if (receiverNotificationCode != 0) {
                notificationMsg.setDestMDN(pendingCommodityTransfer.getDestMDN());
                String destMDN = pendingCommodityTransfer.getDestMDN();
                SubscriberMDN destSubscriberMDN = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(destMDN);
                if(destSubscriberMDN != null)
                {
                	Integer language = destSubscriberMDN.getSubscriber().getLanguage();
                	notificationMsg.setLanguage(language);
                }
                else
                {
                	notificationMsg.setLanguage(ConfigurationUtil.getDefaultLanguage());
                }
                notificationMsg.setEmailId("");
                notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
                notificationMsg.setCode(receiverNotificationCode);
                SendNotificationToSubscriber sendToSub = new SendNotificationToSubscriber(notificationMsg);
                sendToSub.handle();
            }
        } else {
            if (pendingCommodityTransfer.getSourceApplication().equals(CmFinoFIX.SourceApplication_Phone) || pendingCommodityTransfer.getSourceApplication().equals(CmFinoFIX.SourceApplication_Web) || pendingCommodityTransfer.getSourceApplication().equals(CmFinoFIX.SourceApplication_WebService)) {
                notificationMsg.setCode(CmFinoFIX.NotificationCode_Resovle_Transaction_To_Fail);
                SendNotificationToSubscriber sendToSub = new SendNotificationToSubscriber(notificationMsg);
                sendToSub.handle();
            }
        }
        return returnValue;
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    private int updatePendingCommodityTransfer(
            PendingCommodityTransfer pendingCommodityTransfer) {
        // CheckExpiredTransfer
        if (!CmFinoFIX.TransferStatus_Completed.equals(pendingCommodityTransfer.getTransferStatus())) {
            // Rec.GetStartTimeByRef().TimeT()	+	Rec.GetExpirationTimeoutValue()/1000	<	Now.TimeT()
            long time = pendingCommodityTransfer.getStartTime().getTime() + pendingCommodityTransfer.getExpirationTimeout();
            if (time < System.currentTimeMillis()) {
                if (pendingCommodityTransfer.getTransferStatus().equals(CmFinoFIX.TransferStatus_MobileAgentTopupSentToOperator)) {
                    pendingCommodityTransfer.setTransferFailureReason(CmFinoFIX.TransferFailureReason_MobileAgentRechargeToOperatorExpired);
                }
                pendingCommodityTransfer.setTransferStatus(CmFinoFIX.TransferStatus_Failed);
            }
        }
        // Calculate final state
        if ( CmFinoFIX.CSRAction_Cancel.equals(pendingCommodityTransfer.getCSRAction()) ) {
            // since this top up else part is not required
            if (pendingCommodityTransfer.getLocalRevertRequired() && !revertPocketsUsage(pendingCommodityTransfer)) {
                return CmFinoFIX.PendingTransationsEntryStatus_failed;
            }
            log.info("Local balance revert required = " + pendingCommodityTransfer.getLocalBalanceRevertRequired());
            //if no bank reversual is required, we are going to revert right here
            //otherwise we are going to do local reversual when sending bank reversual
            // also need not check for if(!bSendReversal)
            if (pendingCommodityTransfer.getLocalBalanceRevertRequired() && !revertPocketsBalance(pendingCommodityTransfer)) {
                return CmFinoFIX.PendingTransationsEntryStatus_failed;
            }
        }
        
        CommodityTransfer commodityTransfer = new CommodityTransfer();
        commodityTransfer.copy(pendingCommodityTransfer);
      //Since we are inserting for the first time to CT we need to update the creatime too.
        commodityTransfer.setCreateTime(new Timestamp()); 
        if (commodityTransfer.getEndTime() == null) {
            commodityTransfer.setEndTime(new Timestamp());
        }
        // Update Bulk Upload Entry with the new transfer id in the commodity transfer.
        CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
        commodityTransferDAO.save(commodityTransfer);
        if (commodityTransfer.getBulkUploadID() != null && commodityTransfer.getBulkUploadLineNumber() != null) {
            BulkUploadEntryDAO bulkUploadEntryDAO = DAOFactory.getInstance().getBulkUploadEntryDAO();
            BulkUploadEntryQuery bulkUploadEntryQuery = new BulkUploadEntryQuery();
            bulkUploadEntryQuery.setBulkid(commodityTransfer.getBulkUploadID());
            bulkUploadEntryQuery.setBulkUploadLineNumber(commodityTransfer.getBulkUploadLineNumber());
            try {
                List<BulkUploadEntry> bulkUploadEntryList = bulkUploadEntryDAO.get(bulkUploadEntryQuery);
                if (bulkUploadEntryList.size() > 0) {
                    BulkUploadEntry bulkUploadEntry = bulkUploadEntryList.get(0);
                    bulkUploadEntry.setTransferID(commodityTransfer.getID());
                    bulkUploadEntry.setStatus(commodityTransfer.getTransferStatus());
                    bulkUploadEntry.setTransferFailureReason(commodityTransfer.getTransferFailureReason());
                    bulkUploadEntry.setNotificationCode(commodityTransfer.getNotificationCode());
                }
            } catch (Exception exp) {
				log.error(exp.getMessage(), exp);
            }
        }
        PendingCommodityTransferDAO pendingCommodityTransferDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
        pendingCommodityTransferDAO.delete(pendingCommodityTransfer);
        return CmFinoFIX.PendingTransationsEntryStatus_success;
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public boolean revertPocketsUsage(PendingCommodityTransfer pendingCommodityTransfer) {
    	
    	PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
        if (!pendingCommodityTransfer.getLocalRevertRequired()) {
            return true;
        }
        BigDecimal Amount = pendingCommodityTransfer.getAmount();
        Pocket SourcePocket = pendingCommodityTransfer.getPocketBySourcePocketID();
        SourcePocket = pocketDao.getById(SourcePocket.getID() , LockMode.UPGRADE);
        if (SourcePocket == null) {
            return false;
        }
        SourcePocket.setCurrentDailyExpenditure(SourcePocket.getCurrentDailyExpenditure().subtract(Amount));
        SourcePocket.setCurrentDailyTxnsCount(SourcePocket.getCurrentDailyTxnsCount() - 1);
        SourcePocket.setCurrentMonthlyExpenditure(SourcePocket.getCurrentMonthlyExpenditure().subtract(Amount));
        SourcePocket.setCurrentMonthlyTxnsCount(SourcePocket.getCurrentMonthlyTxnsCount() - 1);
        SourcePocket.setCurrentWeeklyExpenditure(SourcePocket.getCurrentWeeklyExpenditure().subtract(Amount));
        SourcePocket.setCurrentWeeklyTxnsCount(SourcePocket.getCurrentWeeklyTxnsCount() - 1);

       
        pocketDao.save(SourcePocket);

        pendingCommodityTransfer.setLocalRevertRequired(CmFinoFIX.Boolean_False);
        return true;
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public boolean revertPocketsBalance(PendingCommodityTransfer pendingCommodityTransfer) {
    	PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
        if (!pendingCommodityTransfer.getLocalBalanceRevertRequired()) {
            return true;
        }
        
        Pocket SourcePocket = pendingCommodityTransfer.getPocketBySourcePocketID();
        SourcePocket = pocketDao.getById(SourcePocket.getID() , LockMode.UPGRADE);
        if (SourcePocket == null) {
            return false;
        }
        BigDecimal Amount = pendingCommodityTransfer.getAmount();
        BigDecimal newBalance = Amount.add(SourcePocket.getCurrentBalance());        
        if (CmFinoFIX.PocketType_SVA.equals(pendingCommodityTransfer.getSourcePocketType())) {
        	log.info( " Updating pocket " + SourcePocket.getID() + " balance from " + SourcePocket.getCurrentBalance() + " to " + newBalance);
            SourcePocket.setCurrentBalance(newBalance);
        }
        
        pocketDao.save(SourcePocket);

        if (pendingCommodityTransfer.getDestPocketID() != null && pendingCommodityTransfer.getDestPocketType() == CmFinoFIX.PocketType_SVA) {
            Pocket DestPocket;
            DestPocket = pocketDao.getById(pendingCommodityTransfer.getDestPocketID());
            DestPocket.setCurrentBalance(DestPocket.getCurrentBalance().subtract(Amount));
            pocketDao.save(DestPocket);
        }

        pendingCommodityTransfer.setLocalBalanceRevertRequired(CmFinoFIX.Boolean_False);
        return true;
    }

    static class CommHandler extends MultixCommunicationHandler {

        @Override
        public CFIXMsg process(CFIXMsg msg) {
            return handleRequestResponse((CMBase) msg);
        }
       
    }
    public static void main(String[] args) throws Throwable{
		BulkPendingResolveTool tool = new BulkPendingResolveTool();
		tool.uploadData();
	}
}

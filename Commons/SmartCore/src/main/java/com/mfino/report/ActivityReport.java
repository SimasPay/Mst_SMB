/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.LOPDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.dao.query.ActivitiesLogQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Company;
import com.mfino.domain.LOP;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class ActivityReport extends OfflineReportBase {

    private static final int NUM_COLUMNS = 64;
    private static final String HEADER_ROW = "#, Reference No, Trans ID, LOP ID, Bulk Upload ID, Status, Return Code, Start Date & Time, " +
            "Completion Date & Time, Transaction Type, Bucket Type, Merchant ID, Source MDN, Source Pocket, Source Pocket Template," +
            "Destination MDN, Destination Pocket, Destination Pocket Template, Amount, Commission, Paid Amount, Currency, Channel Name, " +
            "Source Distributor Name, Destination Distributor Name, Merchant Reference ID, Bank, " +
            "Bank Channel, Bank Response Code, Bank Payment Gateway Transaction Ref No, Bank Channel RRN, CBoss Ref No, Bank RRN, Bank STAN, " +
            "Operator RRN, Source Region Code, Destination Region Code, Source Company Code, Destination Company Code, " +
            "Source Pocket ID, Destination Pocket ID, Source Pocket Status, Destination Pocket Status, Merchant Transaction ID, Error Code, " +
            "Error Description, User Code, Trasaction Status, Amount, EUI, Trasaction ID, " +
            "IS BlackListed, Fraud Risk Level, Fraud Risk Score, Exceed High Risk, Card Type, Card No Partial, Card Name, Bank Res Code, " +
            "Bank Res Message, Bank Reference,  Auth ID, Acquirer Bank, ISO8583_Variant";
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private ActivitiesLogDAO activitiesDAO = DAOFactory.getInstance().getActivitiesLogDAO();
    private TransactionsLogDAO tLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
    private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
    private CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
    private PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
    private LOPDAO lopDAO = DAOFactory.getInstance().getLopDAO();
    private SubscriberDAO subDAO = DAOFactory.getInstance().getSubscriberDAO();
    private SubscriberMDNDAO subMdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
  //  private Set<Long> dompetTransferIdSet = new HashSet<Long>();
    // Contains all the transfer ids that have been added to report
    private Map<Long, Long> dompetTransferMap = new HashMap<Long, Long>();
    private Map<Long, Long> billPayments = new HashMap<Long, Long>();
    // Contains all the pending transfer ids that
    private Map<Long, Long> pendingDompetTransferMap = new HashMap<Long, Long>();
    private HashMap<Integer, String> channelcodes ;

    @Override
    public String getReportName() {
        return "ActivityReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start, end, null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;

        try {
            HibernateUtil.getCurrentSession().beginTransaction();
           
            ActivitiesLogQuery query = new ActivitiesLogQuery();
            Company company = null;
            if (companyID != null) {
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                company = companyDao.getById(companyID);
                if (company != null) {
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            } else {
                reportFile = getReportFilePath();
            }
            if (null != start) {
                query.setLastUpdateTimeGE(start);
            }
            if (null != end) {
                query.setLastUpdateTimeLT(end);
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            int seq = 1;

            writer.println(HEADER_ROW);
            int firstResult = 0;
            int batchSize = ConfigurationUtil.getActivityReportBatchSize();
            //smart#726 
            channelcodes = ChannelCodeService.getChannelCodeMap();

            int count = activitiesDAO.getActivityCountBetween(start, end, company, null);
            while (firstResult < count) {
                query.setStart(firstResult);
                query.setLimit(batchSize);
                query.setIDOrdered(true);
                List<ActivitiesLog> results = activitiesDAO.get(query);
                seq = reportForBatch(results, writer, seq);
                firstResult += results.size();
                HibernateUtil.getCurrentSession().clear();
                results.clear();                
            }
            // Appending only pending Dompet transfer to the report
            Collection<Long> acivitiesIds = pendingDompetTransferMap.values();
            List<ActivitiesLog> activitiesLogList = new ArrayList<ActivitiesLog>();
            for (Long id : acivitiesIds) {
                activitiesLogList.add(activitiesDAO.getById(id));
            }
            reportForBatch(activitiesLogList, writer, seq);
            writer.close();
            HibernateUtil.getCurrentTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentTransaction().rollback();
            log.error("Error in ActivityReport", t);
        }
        return reportFile;
    }

    private int reportForBatch(List<ActivitiesLog> results, PrintWriter writer, int seq) {
        DateFormat df = getDateFormat();
        String formatStr = getFormatString(NUM_COLUMNS);
        for (ActivitiesLog activity : results) {
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal amountPaid = BigDecimal.ZERO;
            BigDecimal commission = BigDecimal.ZERO;

            Long merchantID = null;
            Long lopID = null;
            Long bulkID = null;
            Long transferID = activity.getTransferID();
            Long transID = activity.getParentTransactionID();
            String status = StringUtils.EMPTY;
            String sourceReferenceID = StringUtils.EMPTY;
            String sourceDistributorName = StringUtils.EMPTY;
            String destDistributorName = StringUtils.EMPTY;
            String uiCategory = StringUtils.EMPTY;
            String destPocketType = StringUtils.EMPTY;
            String sourcePocketTemplate = StringUtils.EMPTY;
            String destinationPocketTemplate = StringUtils.EMPTY;
            String sourceRegionCode = GeneralConstants.EMPTY_STRING;
            String destinationRegionCode = GeneralConstants.EMPTY_STRING;
            Integer sourceCompanyCode = null;
            Integer destinationCompanyCode = null;
            Long sourcePocketId = null;
            Long destPocketID = null;
            Integer sourcePocketStatus = null;
            Integer destinationPocketStatus = null;
            Subscriber sourceSubscriber = null;
            Long merchantTransactionID = null;
       String errorCode = StringUtils.EMPTY;
       String desc = StringUtils.EMPTY;
       String userCode = StringUtils.EMPTY;
       String transStatus = StringUtils.EMPTY;
       BigDecimal CCamount = null;
       String Eui = StringUtils.EMPTY;
       Long transId = null;
       String isBlackListed = StringUtils.EMPTY;
       Integer fraudRiskLevel = null;
       BigDecimal fraudRiskScore = null;
       String exceedHighRisk = StringUtils.EMPTY;
       String cardType = StringUtils.EMPTY;
       String cardNoPartial = StringUtils.EMPTY;
       String cardName = StringUtils.EMPTY;
       String bankResCode = StringUtils.EMPTY;
       String bankResMsg = StringUtils.EMPTY;
       String bankRef = StringUtils.EMPTY;
       String authId = StringUtils.EMPTY;
       String acquirerBank = StringUtils.EMPTY;
       String channel = null;
       String bucketType = null;
            //Base class for both CommodityTransfer and PendingCommodityTransfer
            CRCommodityTransfer ct = null;
            boolean isPendingRecord = false;

            if (transferID != null) {
                ct = ctDAO.getById(transferID);
                if (ct == null) {
                    isPendingRecord = true;
                    ct = pctDAO.getById(transferID);
                }
            }
            if(ct!=null){
            	channel = channelcodes.get(ct.getSourceApplication());
            	bucketType = ct.getBucketType();
            }else{
            	channel = channelcodes.get(activity.getSourceApplication());
            }

            if (ct != null && !CmFinoFIX.MsgType_H2HTransferInquiry.equals(activity.getMsgType())) {
                //Check if we should show the source distributor name
                if (OfflineReportUtil.isMerchantTxn(ct.getUICategory())) {
                    sourceSubscriber = ct.getSubscriberBySourceSubscriberID();
                    if (sourceSubscriber != null) {
                        merchantID = sourceSubscriber.getID();
                        if (sourceSubscriber.getUser() != null) {
                            sourceDistributorName = sourceSubscriber.getUser().getUsername();
                        }
                    }
                }
                Subscriber destSubscriber = null;
                Long destSubID = ct.getDestSubscriberID();
                if(destSubID != null)
                    destSubscriber = subDAO.getById(destSubID);
                //Get the destination subscriber name
                if (CmFinoFIX.TransactionUICategory_MA_Transfer.equals(ct.getUICategory()) || CmFinoFIX.TransactionUICategory_Distribute_LOP.equals(ct.getUICategory()) || CmFinoFIX.TransactionUICategory_BulkTransfer.equals(ct.getUICategory())) {
//                    Long destSubID = ct.getDestSubscriberID();
//                    destSubscriber = subDAO.getById(destSubID);
                    if (destSubscriber != null && destSubscriber.getUser() != null) {
                        destDistributorName = destSubscriber.getUser().getUsername();
                    }
                    if (destSubscriber != null && destSubscriber.getMerchant() != null && destSubscriber.getMerchant().getRegion() != null) {
                        destinationRegionCode = destSubscriber.getMerchant().getRegion().getRegionCode();
                    }
//                    if (destSubscriber != null && destSubscriber.getCompany() != null) {
//                        destinationCompanyCode = destSubscriber.getCompany().getCompanyCode();
//                    }
//                    destPocketID = ct.getDestPocketID();
//                    if (destPocketID != null) {
//                        Pocket destPocket = pocketDAO.getById(destPocketID);
//                        destinationPocketStatus = destPocket.getStatus();
//                    }
                }
                if (destSubscriber != null && destSubscriber.getCompany() != null) {
                    destinationCompanyCode = destSubscriber.getCompany().getCompanyCode();
                }
                amount = ct.getAmount();
                amountPaid = ct.getAmount();
                commission = BigDecimal.ZERO;

                if (isPendingRecord) {
                    status = "Pending";
                } else {
                    status = (CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) ? "Successful" : "Failed";
                }
                sourceReferenceID = (ct.getSourceReferenceID() != null) ? ct.getSourceReferenceID() : StringUtils.EMPTY;
                uiCategory = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory());
                destPocketType = (ct.getDestPocketType() != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, ct.getDestPocketType()) : "";
                if (StringUtils.isEmpty(destPocketType)) {
                    destPocketType = OfflineReportUtil.getDestinationPocketType(ct.getUICategory());
                    if (OfflineReportUtil.BOB_ACCOUNT.equals(destPocketType)) {
                        destinationPocketTemplate = OfflineReportUtil.CBOSS_PREPAID;
                    }
                }

                if (ct.getTransactionsLogByTransactionID() != null) {
                    transID = ct.getTransactionsLogByTransactionID().getID();
                }

                bulkID = ct.getBulkUploadID();
                if (ct.getLOP() != null) {
                    lopID = ct.getLOP().getID();
                } else {
                    lopID = activity.getLOPID();
                }

                Pocket sourcePocket = ct.getPocketBySourcePocketID();
                if (sourcePocket != null) {
                    sourcePocketTemplate = sourcePocket.getPocketTemplate().getDescription();
                }

                destPocketID = ct.getDestPocketID();
                if (destPocketID != null) {
                    Pocket destPocket = pocketDAO.getById(destPocketID);
                    destinationPocketTemplate = destPocket.getPocketTemplate().getDescription();
                    destinationPocketStatus = destPocket.getStatus();
                }

            } else {

                Boolean isSuccessful = activity.getIsSuccessful();
                if (isSuccessful == null) {
                    status = "Unknown";
                } else {
                    status = (isSuccessful) ? "Successful" : "Failed";
                }

                Integer pocketType = activity.getSourcePocketType();
                Long pocketID = activity.getSourcePocketID();
                if (pocketID != null) {
                    Pocket sourcePocket = pocketDAO.getById(pocketID);
                    sourcePocketTemplate = sourcePocket.getPocketTemplate().getDescription();
                }
                uiCategory = OfflineReportUtil.getUICategory(activity.getMsgType(), activity.getSourceMDN(), "", activity.getServletPath(), activity.getCommodity(), pocketType);

                //FIXME:
                //For failed bulk transfers, I have no way of getting the bulk upload ID!
                //The failed bulk upload is not captured anywhere!!!
                //It is present only in the msg data!
                if (OfflineReportUtil.BULK_TRANSFER.equals(uiCategory) || OfflineReportUtil.BULK_TOPUP.equals(uiCategory)) {
                    TransactionsLog tlog = tLogDAO.getById(transID);
                    String msg = tlog.getMessageData();
                    bulkID = OfflineReportUtil.getUploadIDFromMsg(msg);
                }

                Long sourceID = activity.getSourceSubscriberID();
                if (sourceID != null && OfflineReportUtil.isMerchantActivity(uiCategory)) {
                    Subscriber sourceSub = subDAO.getById(sourceID);
                    merchantID = sourceID;
                    if (sourceSub != null && sourceSub.getUser() != null) {
                        sourceDistributorName = sourceSub.getUser().getUsername();
                    }
                }
                lopID = activity.getLOPID();
            }

            LOP lop = null;
            if (lopID != null) {
                lop = lopDAO.getById(lopID);

                if (lop != null) {
                    amount = lop.getAmountDistributed();
                    amountPaid = lop.getActualAmountPaid();
//                    commission = lop.getAmountDistributed() - amountPaid;
                    commission = lop.getAmountDistributed().subtract(amountPaid);
                }
            }
            Integer pocketType = activity.getSourcePocketType();
            if (pocketType == null) {
                pocketType = getSourcePocketType(uiCategory);
            }

            if (ct != null) {
                sourceSubscriber = ct.getSubscriberBySourceSubscriberID();
                if (sourceSubscriber.getCompany() != null) {
                    sourceCompanyCode = sourceSubscriber.getCompany().getCompanyCode();
                }
                if (sourceSubscriber.getMerchant() != null && sourceSubscriber.getMerchant().getRegion() != null) {
                    sourceRegionCode = sourceSubscriber.getMerchant().getRegion().getRegionCode();
                }
                if (ct.getPocketBySourcePocketID() != null) {
                    sourcePocketId = ct.getPocketBySourcePocketID().getID();
                    sourcePocketStatus = ct.getPocketBySourcePocketID().getStatus();
                }
            } else if (activity.getSourceMDNID() != null) {
                SubscriberMDN subMdn = subMdnDAO.getById(activity.getSourceMDNID());
                if (subMdn != null) {
                    sourceCompanyCode = (subMdn.getSubscriber().getCompany() != null) ? subMdn.getSubscriber().getCompany().getCompanyCode() : null;
                    if (subMdn.getSubscriber().getMerchant() != null) {
                        sourceRegionCode = (subMdn.getSubscriber().getMerchant().getRegion() != null) ? subMdn.getSubscriber().getMerchant().getRegion().getRegionCode() : StringUtils.EMPTY;
//                        Pocket p = SubscriberService.getDefaultPocket(subMdn.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Airtime);
//                        sourcePocketId = (p != null) ? p.getID() : null;
//                        sourcePocketStatus = (p != null) ? p.getStatus() : null;
//                        System.out.println("Merchant MDN =" + activity.getSourceMDN() + " MDN ID :" + activity.getSourceMDNID() + " ID :" + activity.getID());
//                    } else if (subMdn.getSubscriber() != null) {
//                        Pocket p = SubscriberService.getDefaultPocket(subMdn.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
//                        sourcePocketId = (p != null) ? p.getID() : null;
//                        sourcePocketStatus = (p != null) ? p.getStatus() : null;
//                        System.out.println("Subscriber MDN =" + activity.getSourceMDN() + " MDN ID :" + activity.getSourceMDNID() + " ID :" + activity.getID());
                    }
                    if (activity.getSourcePocketID() != null) {
                        sourcePocketId = activity.getSourcePocketID();
                        sourcePocketStatus = pocketDAO.getById(sourcePocketId).getStatus();
                    }
                }
            }

            String destMDN = (ct != null) ? ct.getDestMDN() : activity.getDestMDN();

            // smart #314,
            //Use the notification code from activity record first.
            //Sometimes there is a pending state stored to activity_log which is also reported to the user.
            Integer notificationCode = activity.getNotificationCode();

            if (notificationCode == null) {
                notificationCode = (ct != null && ct.getNotificationCode() != null) ? ct.getNotificationCode() : null;
            }

            //smart #314
            //Ignore the activity log with pending notification (61), (72), (24)
            if (CmFinoFIX.NotificationCode_TransferRecordNotFound.equals(notificationCode)
                    || CmFinoFIX.NotificationCode_DBInsertPendingTransferRecordFailed.equals(notificationCode)) {
                continue;
            }
            if (CmFinoFIX.NotificationCode_BankAccountToBankAccountPending.equals(notificationCode) || CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.equals(notificationCode)) {
                if (ct != null && CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer.equals(ct.getUICategory()) && !dompetTransferMap.containsKey(transferID)) {
                    if (!pendingDompetTransferMap.containsKey(transferID)) {
                        pendingDompetTransferMap.put(transferID, activity.getID());
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (ct != null && CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer.equals(ct.getUICategory())) {
            	if(dompetTransferMap.containsKey(transferID)){
            		//smart798
            		continue;
            	}
                dompetTransferMap.put(transferID, activity.getID());
            }
            if (ct != null && (CmFinoFIX.TransactionUICategory_Bill_Payment.equals(ct.getUICategory())||CmFinoFIX.TransactionUICategory_Bill_Payment_Topup.equals(ct.getUICategory()))) {
            	if(billPayments.containsKey(transferID)){
            		//smart798 skipping inquiry record for bill payments
            		continue;
            	}
                billPayments.put(transferID, activity.getID());
            }
            if(ct!=null && ct.getNotificationCode()!=null){
            	//overwrite notificationcode with ct notificationcode
            	notificationCode = ct.getNotificationCode();
            }

            if (StringUtils.isEmpty(uiCategory)) {
                uiCategory = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_MsgType, null, activity.getMsgType());
            }

            String RRN = null;
            if (ct != null && ct.getBankRetrievalReferenceNumber() != null) {
                RRN = OfflineReportUtil.formatRRN(ct.getBankRetrievalReferenceNumber());
            }

            String bankChannelRRN = (ct != null && ct.getISO8583_RetrievalReferenceNum() != null)
                    ? ct.getISO8583_RetrievalReferenceNum()
                    : activity.getISO8583_RetrievalReferenceNum();

            if (StringUtils.isNotEmpty(bankChannelRRN)) {
                bankChannelRRN = OfflineReportUtil.formatRRN(bankChannelRRN);
            }

        if(ct != null && ct.getCreditCardTransaction() != null)
        {
           merchantTransactionID = ct.getCreditCardTransaction().getID();
           errorCode = ct.getCreditCardTransaction().getErrCode();
           desc = ct.getCreditCardTransaction().getDescription();
           userCode = ct.getCreditCardTransaction().getUserCode();
           transStatus = ct.getCreditCardTransaction().getTransStatus();
           CCamount = ct.getCreditCardTransaction().getAmount();
           Eui = ct.getCreditCardTransaction().getEUI();
           transId = ct.getCreditCardTransaction().getTransactionID();
           isBlackListed = ct.getCreditCardTransaction().getIsBlackListed();
           fraudRiskLevel = ct.getCreditCardTransaction().getFraudRiskLevel();
           fraudRiskScore = ct.getCreditCardTransaction().getFraudRiskScore();
           exceedHighRisk = ct.getCreditCardTransaction().getExceedHighRisk();
           cardType = ct.getCreditCardTransaction().getCardType();
           cardNoPartial = ct.getCreditCardTransaction().getCardNoPartial();
           cardName = ct.getCreditCardTransaction().getCardName();
           bankResCode = ct.getCreditCardTransaction().getBankResCode();
           bankResMsg = ct.getCreditCardTransaction().getBankResMsg();
           bankRef = ct.getCreditCardTransaction().getBankReference();
           authId = ct.getCreditCardTransaction().getAuthID();
           acquirerBank = ct.getCreditCardTransaction().getAcquirerBank();
        }
        
        if(ct!=null && CmFinoFIX.SourceApplication_BankChannel.equals(ct.getSourceApplication()) && CmFinoFIX.ResponseCode_Success.equals(ct.getOperatorReversalResponseCode())){
            status ="Reversed";     	
       }
            try {
                writer.println(String.format(formatStr,
                        seq,
                        (transferID != null) ? transferID : StringUtils.EMPTY,
                        activity.getParentTransactionID(),
                        (lopID != null) ? lopID : StringUtils.EMPTY,
                        (bulkID != null) ? bulkID : StringUtils.EMPTY,
                        status,
                        notificationCode,
                        (ct != null && !CmFinoFIX.MsgType_H2HTransferInquiry.equals(activity.getMsgType())) ? df.format(ct.getStartTime()) : df.format(activity.getCreateTime()),
                        (ct != null && !CmFinoFIX.MsgType_H2HTransferInquiry.equals(activity.getMsgType()) && ct.getEndTime() != null) ? df.format(ct.getEndTime()) : df.format(activity.getLastUpdateTime()),
                        uiCategory,
                        bucketType!=null ?EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType,null,bucketType) : StringUtils.EMPTY,
                        merchantID != null ? merchantID : StringUtils.EMPTY,
                        OfflineReportUtil.stripRx(activity.getSourceMDN()),
                        (pocketType != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, pocketType) : StringUtils.EMPTY,
                        sourcePocketTemplate,
                        (destMDN != null) ? OfflineReportUtil.stripRx(destMDN) : StringUtils.EMPTY,
                        destPocketType,
                        destinationPocketTemplate,
                        amount,
                        commission,
                        amountPaid,
                        (ct != null && !CmFinoFIX.MsgType_H2HTransferInquiry.equals(activity.getMsgType())) ? ct.getCurrency() : StringUtils.EMPTY,
                        channel!=null? channel : activity.getSourceApplication(),
                        sourceDistributorName,
                        (destDistributorName != null) ? destDistributorName : StringUtils.EMPTY,
                        sourceReferenceID,
                        (ct != null) ? ct.getISO8583_AcquiringInstIdCode() : activity.getISO8583_AcquiringInstIdCode(),
                        (ct != null) ? ct.getISO8583_MerchantType() : activity.getISO8583_MerchantType(),
                        (ct != null) ? ct.getISO8583_ResponseCode() : activity.getISO8583_ResponseCode(),
                        (ct != null) ? ct.getISO8583_SystemTraceAuditNumber() : activity.getISO8583_SystemTraceAuditNumber(),
                        bankChannelRRN,
                        (ct != null && ct.getOperatorAuthorizationCode() != null && !CmFinoFIX.MsgType_H2HTransferInquiry.equals(activity.getMsgType())) ? ct.getOperatorAuthorizationCode() : null,
                        RRN,
                        (ct != null) ? ct.getBankSystemTraceAuditNumber() : null,
                		(ct!=null && StringUtils.isNotBlank(ct.getOperatorRRN()))? ct.getOperatorRRN(): StringUtils.EMPTY,
                        sourceRegionCode,
                        destinationRegionCode,
                        (sourceCompanyCode != null) ? sourceCompanyCode : StringUtils.EMPTY,
                        (destinationCompanyCode != null) ? destinationCompanyCode : StringUtils.EMPTY,
                        (sourcePocketId != null) ? sourcePocketId : StringUtils.EMPTY,
                        (destPocketID != null) ? destPocketID : StringUtils.EMPTY,
                        (sourcePocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, sourcePocketStatus) : StringUtils.EMPTY,
                        (destinationPocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, destinationPocketStatus) : StringUtils.EMPTY,
                (merchantTransactionID != null) ? merchantTransactionID : StringUtils.EMPTY,
                errorCode,
                desc,
                userCode,
                transStatus,
                (CCamount != null) ? CCamount : StringUtils.EMPTY,
                Eui,
                (transId != null) ? transId: StringUtils.EMPTY,
                isBlackListed,
                fraudRiskLevel != null ? fraudRiskLevel : StringUtils.EMPTY,
                fraudRiskScore != null ? fraudRiskScore : StringUtils.EMPTY,
                exceedHighRisk,
                cardType,
                cardNoPartial,
                cardName,
                bankResCode,
                bankResMsg,
                bankRef,
                authId,
                acquirerBank,
                (ct != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_Variant, null, ct.getISO8583_Variant()) : StringUtils.EMPTY
                ));
            } catch (Exception ex) {
                //Most likely because of invalid data. We need to recover from this and continue with other records.
                log.error("Error in Activity Report", ex);
            }
            seq++;
        }
        return seq;
    }

    private Integer getSourcePocketType(String uiCategoryFromCT) {

        if (StringUtils.isEmpty(uiCategoryFromCT)) {
            return null;
        }

        if (OfflineReportUtil.CHANGE_MA_PIN.equals(uiCategoryFromCT) || OfflineReportUtil.RESET_MA_PIN.equals(uiCategoryFromCT) || OfflineReportUtil.MA_LAST_3.equals(uiCategoryFromCT)) {
            return CmFinoFIX.PocketType_SVA;
        }

        if (OfflineReportUtil.RESET_MPIN.equals(uiCategoryFromCT)) {
            return CmFinoFIX.PocketType_BOBAccount;
        }

        return null;
    }

    /*
    public static void main(String args[]){
    ActivityReport sReport = new ActivityReport();
    sReport.run(null, null);
    }
     */
}

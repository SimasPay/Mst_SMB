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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author sandeepjs
 */
public class PendingTransactionsReport extends OfflineReportBase {

    private PendingCommodityTransferDAO pendingCommodityTransferDAO = null;
    private HashMap<Integer, String> channelcodes;
    private static final int NUM_COLUMNS = 33;
    private static final String HEADER_ROW = "#, Reference Number, Status, CSRAction, Return Code, Start Date & Time, Completion Date & Time, " +
            "Transaction Type, Bucket Type, Distributor/Agent ID, Source MDN, Source Pocket, " +
            "Destination MDN, Destination Pocket, Amount, Commission, Paid Amount, Currency, Channel Name, " +
            "Source Distributor Name, Destination Distributor Name, Merchant Reference ID, Bank, " +
            "Bank Channel, Bank Response Code, Bank Payment Gateway Transaction Ref No, Company Code, " +
            " Source Pocket ID, Destination Pocket ID, Source Pocket Status, Destination Pocket Status, Bank Code, ISO8583_Variant";

    @Inject
    public PendingTransactionsReport(PendingCommodityTransferDAO ptDAO) {
        pendingCommodityTransferDAO = ptDAO;
    }
    @Override
    public File run(Date start, Date end) {
        return run(start, end, null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        String formatStr = getFormatString(NUM_COLUMNS);

        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            channelcodes = ChannelCodeService.getChannelCodeMap();
            CommodityTransferQuery query = new CommodityTransferQuery();
            query.setCreateTimeGE(start);
            query.setCreateTimeLT(end);
            if(companyID != null){
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                Company company =companyDao.getById(companyID);
                if(company != null){
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            }
            else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            // Get all pending transactions.
            List<PendingCommodityTransfer> results = pendingCommodityTransferDAO.get(query);

            int seq = 1;
            DateFormat df = getDateFormat();
            writer.println(HEADER_ROW);
            seq = reportForThisList(results, writer, formatStr, seq, df);

            // Here get all transactions that were manually resolved from
            // Commodity Transfer table.
            CommodityTransferQuery querySecond = new CommodityTransferQuery();
            querySecond.setCreateTimeGE(start);
            querySecond.setCreateTimeLT(end);
            querySecond.setStartTimeGE(start);
            querySecond.setStartTimeLT(end);
            querySecond.setHasCSRAction(true);

            CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();

            if(companyID != null){
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                Company company =companyDao.getById(companyID);
                if(company != null){
                    querySecond.setCompany(company);
                }
            }
            List<CommodityTransfer> ctResults = commodityTransferDAO.get(querySecond);
            reportForThisList(ctResults, writer, formatStr, seq, df);

            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentTransaction().rollback();
            log.error("Error in PendingTransactionsReport", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "PendingTransactionsReport";
    }

    private int reportForThisList(List results, PrintWriter writer, String formatStr, int seq, DateFormat df) {
        SubscriberDAO subDAO = DAOFactory.getInstance().getSubscriberDAO();
        PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
        for (int i = 0; i < results.size(); i++) {
            CmFinoFIX.CRCommodityTransfer eachRecord = (CmFinoFIX.CRCommodityTransfer) results.get(i);
            String sourceDistributorName = StringUtils.EMPTY;
            String destDistributorName = StringUtils.EMPTY;
            String destPocketType = StringUtils.EMPTY;
            String transactionTypeText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, eachRecord.getUICategory());
            String transferState = StringUtils.EMPTY;

            Long sourcePocketId = null;
            Long destinationPocketId = null;
            Integer sourcePocketStatus = null;
            Integer destinationPocketStatus = null;
            Integer companyCode = null;
            Long agentID = null;
           /* Merchant merchant = MerchantService.getMerchantFromMDN(eachRecord.getSourceMDN());
            // Getting the applicable DCT for the merchant.
            if (merchant != null) {
                agentID = merchant.getID(); //MerchantService.getUserNameForMerchant(merchant);
            }*/
            Subscriber sourceSubscriber = eachRecord.getSubscriberBySourceSubscriberID();
            if (sourceSubscriber != null && sourceSubscriber.getUser() != null) {
                Pocket sourcePocket = eachRecord.getPocketBySourcePocketID();
                if (sourcePocket != null && CmFinoFIX.PocketType_SVA.equals(sourcePocket.getPocketTemplate().getType()) && CmFinoFIX.Commodity_Airtime.equals(sourcePocket.getPocketTemplate().getCommodity())) {
                    sourceDistributorName = sourceSubscriber.getUser().getUsername();
                    sourcePocketId = sourcePocket.getID();
                    sourcePocketStatus = sourcePocket.getStatus();
                }
            }
            if (sourceSubscriber != null && sourceSubscriber.getCompany() != null) {
                companyCode = sourceSubscriber.getCompany().getCompanyCode();
            }
            if (CmFinoFIX.TransactionUICategory_MA_Transfer.equals(eachRecord.getUICategory()) || CmFinoFIX.TransactionUICategory_BulkTransfer.equals(eachRecord.getUICategory())) {
                Long destSubID = eachRecord.getDestSubscriberID();
                Subscriber destSubscriber = subDAO.getById(destSubID);
                if (destSubscriber != null && destSubscriber.getUser() != null) {
                    destDistributorName = destSubscriber.getUser().getUsername();
                }
            }
            if(eachRecord.getDestPocketID()!= null) {
                Pocket destPocket = pocketDAO.getById(eachRecord.getDestPocketID());
                if(destPocket != null) {
                    destinationPocketId = destPocket.getID();
                    destinationPocketStatus = destPocket.getStatus();
                }
            }
            destPocketType = (eachRecord.getDestPocketType() != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, eachRecord.getDestPocketType()) : "";
            if (StringUtils.isEmpty(destPocketType)) {
                destPocketType = OfflineReportUtil.getDestinationPocketType(eachRecord.getUICategory());
            }
            
//            Long commission = (eachRecord.getLOP() != null) ? eachRecord.getLOP().getAmountDistributed() - eachRecord.getLOP().getActualAmountPaid() : 0L;
            BigDecimal commission = (eachRecord.getLOP() != null) ? eachRecord.getLOP().getAmountDistributed().subtract(eachRecord.getLOP().getActualAmountPaid()) : BigDecimal.ZERO;
            if (eachRecord instanceof PendingCommodityTransfer) {
              transferState = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Pending);
            } else {
              transferState = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Complete);
            }
            
            String csrAction = StringUtils.EMPTY;
            if(null != eachRecord.getCSRAction()) {
              csrAction = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CSRAction, null, eachRecord.getCSRAction());
            }
            
            writer.println(String.format(formatStr,
                    seq,
                    eachRecord.getID(),
                    transferState,
                    csrAction,
                    eachRecord.getNotificationCode(),
                    df.format(eachRecord.getStartTime()),
                    eachRecord.getEndTime() != null ? df.format(eachRecord.getEndTime()) : StringUtils.EMPTY,
                    transactionTypeText,
                    eachRecord.getBucketType() != null? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType,null,eachRecord.getBucketType()) : StringUtils.EMPTY,
                    agentID,
                    OfflineReportUtil.stripRx(eachRecord.getSourceMDN()),
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, eachRecord.getSourcePocketType()),
                    OfflineReportUtil.stripRx(eachRecord.getDestMDN()),
                    destPocketType, 
                    eachRecord.getAmount(),
                    commission,
                    (eachRecord.getLOP() != null) ? eachRecord.getLOP().getActualAmountPaid() : eachRecord.getAmount(),
                    eachRecord.getCurrency(),
                    channelcodes.get(eachRecord.getSourceApplication()),
                    sourceDistributorName,
                    destDistributorName,
                    eachRecord.getSourceReferenceID(),
                    eachRecord.getISO8583_AcquiringInstIdCode(),
                    eachRecord.getISO8583_MerchantType(),
                    eachRecord.getISO8583_ResponseCode(),
                    eachRecord.getISO8583_SystemTraceAuditNumber(),
                    (companyCode != null) ? companyCode : StringUtils.EMPTY,
                    (sourcePocketId != null) ? sourcePocketId : StringUtils.EMPTY,
                    (destinationPocketId != null)? destinationPocketId : StringUtils.EMPTY,
                    (sourcePocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,sourcePocketStatus) : StringUtils.EMPTY,
                    (destinationPocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,destinationPocketStatus) : StringUtils.EMPTY,
                    (eachRecord.getBankCode() != null) ? eachRecord.getBankCode() : StringUtils.EMPTY,
                     EnumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_Variant, null, eachRecord.getISO8583_Variant())		
                    ));
            seq++;
        }

        return seq;
    }
}

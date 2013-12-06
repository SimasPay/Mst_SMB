/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author sunil
 */
public class ResolvedPendingTransactionsReport extends OfflineReportBase {

    private static final int NUM_COLUMNS = 26;
    private static final String HEADER_ROW = "#,Date of Transaction,Transaction type, Bucket Type, Distributor Name,Source MDN,Destination MDN,Reference No," +
                    "Merchant Ref No,Amount,Bank,Bank Response code,Bank Payment Gateway Transaction Ref No,Channel,Old status,New status,Resolved Date," +
                    "User Id,Operator RRN, Company Code, Source Pocket ID, Destination Pocket ID, Source Pocket Status, Destination Pocket Status, Bank Code," +
                    "ISO8583_Variant";
    private CommodityTransferDAO commodityTransferDAO = null;
    private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
    @Inject
    public ResolvedPendingTransactionsReport(CommodityTransferDAO ctDAO) {
        commodityTransferDAO = ctDAO;
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
            CommodityTransferQuery query = new CommodityTransferQuery();
            // We should check if there is any CSR action or not.
            // That is an indication that the transaction was 'resolved' by somebody.

            query.setHasCSRAction(true);
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
            List<CommodityTransfer> results = commodityTransferDAO.get(query);
            int seq = 1;
            
            String transferStatusText = StringUtils.EMPTY;
            String transactionTypeText = StringUtils.EMPTY;
            String oldTransactionStatusText = StringUtils.EMPTY;
            String distributorName = StringUtils.EMPTY;
            Long sourcePocketId = null;
            Long destinationPocketId = null;
            Integer sourcePocketStatus = null;
            Integer destinationPocketStatus = null;
            Integer companyCode = null;
            writer.println(HEADER_ROW);

            DateFormat df = getDateFormat();

            for (CommodityTransfer ct : results) {
                transactionTypeText = StringUtils.EMPTY;
                transferStatusText = StringUtils.EMPTY;
                oldTransactionStatusText = StringUtils.EMPTY;
                distributorName = StringUtils.EMPTY;
                companyCode = null;
                    
                transferStatusText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, ct.getTransferStatus());

                transactionTypeText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory());
                oldTransactionStatusText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Pending);

                if (ct.getSubscriberBySourceSubscriberID() != null) {
                    if (ct.getSubscriberBySourceSubscriberID().getUser() != null) {
                        distributorName = ct.getSubscriberBySourceSubscriberID().getUser().getUsername();
                        }
                    if(ct.getSubscriberBySourceSubscriberID().getCompany() != null){
                        companyCode = ct.getSubscriberBySourceSubscriberID().getCompany().getCompanyCode();
                    }
//                    if (ct.getSubscriberBySourceSubscriberID().getMerchant() != null) {
//                        Long dctID = ct.getSubscriberBySourceSubscriberID().getMerchant().getDistributionChainTemplateID();
//                        if (dctID != null) {
//                            dctName = DistributionChainTemplateService.getName(dctID);
//                        }
//                    }
//                    if (ct.getSubscriberBySourceSubscriberID().getUser() != null) {
//                        userId = ct.getSubscriberBySourceSubscriberID().getUser().getID();
//                    }
                }
                if(ct.getPocketBySourcePocketID() != null) {
                    Pocket sourcePocket = ct.getPocketBySourcePocketID();
                    if(sourcePocket != null){
                        sourcePocketId = sourcePocket.getID();
                        sourcePocketStatus = sourcePocket.getStatus();
                    }
                }
                if(ct.getDestPocketID()!= null) {
                    Pocket destPocket = pocketDAO.getById(ct.getDestPocketID());
                    if(destPocket != null) {
                        destinationPocketId = destPocket.getID();
                        destinationPocketStatus = destPocket.getStatus();
                    }
                }
                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seq,
                        df.format(ct.getStartTime()),
                        transactionTypeText,
                        ct.getBucketType() != null ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType,null,ct.getBucketType()) : StringUtils.EMPTY,
                        distributorName,
                        OfflineReportUtil.stripRx(ct.getSourceMDN()),
                        OfflineReportUtil.stripRx(ct.getDestMDN()),
                        ct.getID(),
                        ct.getSourceReferenceID(),
                        ct.getAmount(),
                        ct.getISO8583_AcquiringInstIdCode(),
                        ct.getISO8583_ResponseCode(),
                        ct.getISO8583_SystemTraceAuditNumber(),
                        ct.getISO8583_MerchantType(),
                        oldTransactionStatusText,
                        transferStatusText,
                        df.format(ct.getLastUpdateTime()),
                        ct.getCSRUserName(), //Resolved By Username,
                        StringUtils.isNotBlank(ct.getOperatorRRN())? ct.getOperatorRRN() : StringUtils.EMPTY,
                        (companyCode != null) ? companyCode : StringUtils.EMPTY,
                        (sourcePocketId != null) ? sourcePocketId : StringUtils.EMPTY,
                        (destinationPocketId != null)? destinationPocketId : StringUtils.EMPTY,
                        (sourcePocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,sourcePocketStatus) : StringUtils.EMPTY,
                        (destinationPocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,destinationPocketStatus) : StringUtils.EMPTY,
                        (ct.getBankCode() != null) ? ct.getBankCode() : StringUtils.EMPTY,
                         EnumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_Variant, null, ct.getISO8583_Variant())
                        ));
                seq++;
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        }catch(Throwable t){
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in ResolvedPendingTransactionsReport", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "ResolvedPendingTransactionsReport";

    }
}

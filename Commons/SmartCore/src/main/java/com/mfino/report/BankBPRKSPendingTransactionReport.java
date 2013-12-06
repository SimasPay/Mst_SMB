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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author Maruthi
 */
public class BankBPRKSPendingTransactionReport extends OfflineReportBase{

    private static final int NUM_COLUMNS = 24;
    private static final String HEADER_ROW = "#, Date & Time, Reference No, Status, Transaction Type, Bucket Type, Channel, From MDN, " +
            "To MDN, Card Number, Transaction Amount, Response Code, Bank Reference Number, Operator RRN, Source Company Code, Destination Company Code," +
            "Source Pocket ID, Destination Pocket ID, Source PocketStatus, Destination Pocket Status, Error Code, Error Description, CSRAction, Bank Code";
    private HashMap<Integer, String> channelcodes ;

    
    @Override
    public String getReportName() {
        return "BankBPRKSPendingTransactionReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start,end,null);
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
            query.setBankRoutingCode(ConfigurationUtil.getBPRKSRoutingCode());
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
            PendingCommodityTransferDAO pendingCommodityTransferDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
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
            querySecond.setBankRoutingCode(ConfigurationUtil.getBPRKSRoutingCode());

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
            log.error("Error in BankBPRKSPendingTransactionsReport", t);
        }
        return reportFile;
    }
    
    @SuppressWarnings("unchecked")
	private int reportForThisList(List results, PrintWriter writer, String formatStr, int seq, DateFormat df) {
        SubscriberDAO subDAO = DAOFactory.getInstance().getSubscriberDAO();
        PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
        for (int i = 0; i < results.size(); i++) {
            CmFinoFIX.CRCommodityTransfer ct = (CmFinoFIX.CRCommodityTransfer) results.get(i);
            Integer destinationPocketStatus = null;
            Integer sourceCompanyCode = null;
            Integer destCompanyCode = null;
            Integer sourcePocketStatus = null;
            String errorDescription = null ;
            String transferState = StringUtils.EMPTY;
                        
            if(ct.getNotificationCode()!=null){
            	errorDescription = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_NotificationCode,CmFinoFIX.Language_English , ct.getNotificationCode());
            }
            if(ct.getPocketBySourcePocketID()!= null) {
            	sourcePocketStatus = ct.getPocketBySourcePocketID().getStatus();
            }
            if(ct.getDestPocketID() != null) {
                Pocket pocket = pocketDAO.getById(ct.getDestPocketID());
                if(pocket != null)
                    destinationPocketStatus = pocket.getStatus();
            }
            if(ct.getSubscriberBySourceSubscriberID() != null && ct.getSubscriberBySourceSubscriberID().getCompany() != null){
                sourceCompanyCode = ct.getSubscriberBySourceSubscriberID().getCompany().getCompanyCode();
            }
            Long destSubscriberId = ct.getDestSubscriberID();

            if (destSubscriberId != null) {
                Subscriber destSubscriber = subDAO.getById(destSubscriberId);
                if (destSubscriber.getCompany() != null) {
                    destCompanyCode = destSubscriber.getCompany().getCompanyCode();
                }
            }
            String csrAction = StringUtils.EMPTY;
            if(null != ct.getCSRAction()) {
              csrAction = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CSRAction, null, ct.getCSRAction());
            }
            if (ct instanceof PendingCommodityTransfer) {
                transferState = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Pending);
              } else {
                transferState = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferState, null, CmFinoFIX.TransferState_Complete);
              }
            
            writer.println(String.format(formatStr,
                    seq,
                    df.format(ct.getStartTime()),
                    ct.getID(),
                    transferState,
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()),
                    ct.getBucketType() != null ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType,null,ct.getBucketType()) : StringUtils.EMPTY,
                    channelcodes.get(ct.getSourceApplication()),
                    OfflineReportUtil.stripRx(ct.getSourceMDN()),
                    OfflineReportUtil.stripRx(ct.getDestMDN()),
                    ct.getSourceCardPAN()!=null ? ct.getSourceCardPAN():StringUtils.EMPTY ,
                    ct.getAmount(),
                    ct.getBankResponseCode() != null ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BankResponseCode, null, ct.getBankResponseCode()) : StringUtils.EMPTY,
                    // ct.getISO8583_SystemTraceAuditNumber() //Bank Reference Number??
                    ct.getBankAuthorizationCode() != null ? ct.getBankAuthorizationCode() : StringUtils.EMPTY,
                    StringUtils.isNotBlank(ct.getOperatorRRN())? ct.getOperatorRRN() : StringUtils.EMPTY,
                    (sourceCompanyCode != null) ? sourceCompanyCode : StringUtils.EMPTY,
                    (destCompanyCode != null) ? destCompanyCode : StringUtils.EMPTY,
                    (ct.getPocketBySourcePocketID() != null) ? ct.getPocketBySourcePocketID().getID() : StringUtils.EMPTY,
                    (ct.getDestPocketID() != null) ? ct.getDestPocketID() : StringUtils.EMPTY,
                    (sourcePocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,sourcePocketStatus) : StringUtils.EMPTY,
                    (destinationPocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,destinationPocketStatus) : StringUtils.EMPTY,
                    ct.getNotificationCode() != null ? ct.getNotificationCode() : StringUtils.EMPTY,
                    errorDescription != null ? errorDescription : StringUtils.EMPTY,
                    csrAction,
                    (ct.getBankCode() != null) ? ct.getBankCode() : StringUtils.EMPTY
                    ));
            seq++;
        }
        return seq;
    }
    
    /*public static void main(String args[])
	 { 
	 BankBPRKSPendingTransactionReport sReport = new BankBPRKSPendingTransactionReport();
	 Date s = new Date();
	 Date e = DateUtil.addDays(s, -400);
	 sReport.run(e, s);
	 }*/

}

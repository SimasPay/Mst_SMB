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

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author Maruthi
 */
public class BankBPRKSFailedReport extends OfflineReportBase {

    private static final int NUM_COULMNS = 18;
    private static final String HEADER_ROW = "#,Parent Ref No,Date & Time,Sequence,Transaction Code,From MDN, " +
            "To MDN,Card Number,Transaction Amount,Response Code,Operator RRN, Source Company Code, Destination Company Code," +
            "Source Pocket ID, Destination Pocket ID, Source Pocket Status, Destination Pocket Status, Bank Code";

    @Override
    public String getReportName() {
        return "BankBPRKSFailedReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start, end, null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        DateFormat df = getDateFormat();
        String formatStr = getFormatString(NUM_COULMNS);
//        ActivitiesLogDAO activitiesDAO = new ActivitiesLogDAO();
        CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
        SubscriberDAO subDAO = DAOFactory.getInstance().getSubscriberDAO();
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            int seq = 1;

            CommodityTransferQuery query = new CommodityTransferQuery();
            query.setTransferStatus(CmFinoFIX.TransferStatus_Failed);
            query.setHasCSRAction(false);
            query.setIsDompetTxn(true);
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
            } else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
            List<CommodityTransfer> results = ctDAO.get(query);

            writer.println(HEADER_ROW);
            for (CommodityTransfer ct : results) {
                Integer destinationPocketStatus = null;
                Integer sourceCompanyCode = null;
                Integer destCompanyCode = null;
                Integer sourcePocketStarus = null;
                if(ct.getPocketBySourcePocketID() != null){
                    sourcePocketStarus = ct.getPocketBySourcePocketID().getStatus();
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
                writer.println(String.format(formatStr,
                        seq,
                        ct.getTransactionsLogByTransactionID().getID(),
                        df.format(ct.getStartTime()),
                        ct.getID(),
                        (ct.getUICategory() != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()) : StringUtils.EMPTY,
                        OfflineReportUtil.stripRx(ct.getSourceMDN()),
                        OfflineReportUtil.stripRx(ct.getDestMDN()),
                        ct.getSourceCardPAN(),
                        ct.getAmount(),
                        (ct.getNotificationCode() != null) ? ct.getNotificationCode() : StringUtils.EMPTY,// + " " + EnumTextService.getEnumTextValue(CmFinoFIX.TagID_NotificationCode, null, ct.getNotificationCode())
                        StringUtils.isNotBlank(ct.getOperatorRRN())? ct.getOperatorRRN() : StringUtils.EMPTY,
                        (sourceCompanyCode != null) ? sourceCompanyCode : StringUtils.EMPTY,
                        (destCompanyCode != null) ? destCompanyCode : StringUtils.EMPTY,
                        (ct.getPocketBySourcePocketID() != null) ? ct.getPocketBySourcePocketID().getID() : StringUtils.EMPTY,
                        (ct.getDestPocketID() != null) ? ct.getDestPocketID() : StringUtils.EMPTY,
                        (sourcePocketStarus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,sourcePocketStarus) : "",
                        (destinationPocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,destinationPocketStatus) : "",
                        (ct.getBankCode() != null) ? ct.getBankCode() : StringUtils.EMPTY
                        ));
                seq++;
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in BankBPRKSFailedReport", t);
        }
        return reportFile;
    }
}

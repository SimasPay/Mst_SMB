/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 * 
 * @author Venkata Krishna Teja D
 */
public class EmptySVAMoneyReport extends OfflineReportBase {

    private static final String HEADER_ROW =
            "#, Subscriber ID, First Name, Last Name,Subscriber MDN, Subscriber Status, SVA Money Pocket Status, Pocket Template Description,  " + "Date of Emptied SVA, Emptied By, Emptied Amount, Last Balance, Company Code, Pocket ID";
    private static final int NUM_COLUMNS = 14;
    public EmptySVAMoneyReport() {
    }

    @Override
    public String getReportName() {
        return "EmptySVAMoneyReport";
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

            CommodityTransferQuery commodityTransferQuery = new CommodityTransferQuery();
            //commodityTransferQuery.setMsgType(CmFinoFIX.MsgType_EmptySVAPocket);
            //commodityTransferQuery.setCommodity(CmFinoFIX.Commodity_Money);
            commodityTransferQuery.setUiCategory(CmFinoFIX.TransactionUICategory_EMoney_Empty_SVA);
            commodityTransferQuery.setCreateTimeGE(start);
            commodityTransferQuery.setCreateTimeLT(end);
            if (companyID != null) {
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                Company company = companyDao.getById(companyID);
                if (company != null) {
                    commodityTransferQuery.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            } else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
            List<CommodityTransfer> results = commodityTransferDAO.get(commodityTransferQuery);
            int seq = 1;
            writer.println(HEADER_ROW);
            DateFormat df = getDateFormat();

            for (CommodityTransfer ct : results) {
                SubscriberMDN mdn = ct.getSubscriberMDNBySourceMDNID();
                Subscriber sourceSubscriber = ct.getSubscriberBySourceSubscriberID();

                Long subscriberId = sourceSubscriber.getID();
                String firstName = sourceSubscriber.getFirstName();
                String lastName = sourceSubscriber.getLastName();
                Integer subscriberStatus = mdn.getStatus();
                String ptDesc = StringUtils.EMPTY;

                // Get the pocket Status.
                Pocket pocket = ct.getPocketBySourcePocketID();// /SourcePocketID();

                Integer pocketStatus = null;
                BigDecimal pocketBalance = null;
                if (pocket != null) {
                    pocketStatus = pocket.getStatus();
                    pocketBalance = pocket.getCurrentBalance();
                    PocketTemplate pt = pocket.getPocketTemplate();
                    ptDesc = pt.getDescription();
                }

//                String regionCode = StringUtils.EMPTY;
//                if(sourceSubscriber.getMerchant() != null && sourceSubscriber.getMerchant().getRegion() != null){
//                    regionCode = sourceSubscriber.getMerchant().getRegion().getRegionCode();
//                }
                Integer companyCode = null;
                if (sourceSubscriber.getCompany() != null) {
                    companyCode = sourceSubscriber.getCompany().getCompanyCode();
                }
                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seq,
                        subscriberId,
                        firstName,
                        lastName,
                        OfflineReportUtil.stripRx(ct.getSourceMDN()), 
                        (subscriberStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, subscriberStatus) : StringUtils.EMPTY,
                        (pocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, pocketStatus) : StringUtils.EMPTY,
                        ptDesc,
                        df.format(ct.getStartTime()),
                        ct.getCreatedBy(),
                        ct.getAmount(),
                        // TODO :: We need Confirm the LAST Balance.
                        // Currently putting Pocket Balance.
                        (pocketBalance != null) ? pocketBalance : StringUtils.EMPTY,
                        //                        regionCode,
                        (companyCode != null) ? companyCode : StringUtils.EMPTY,
                        pocket.getID()));
                seq++;
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in EmptySVAReport", t);
        }

        return reportFile;
    }

    public static void main(String args[]) {
        TimeZone tz = ConfigurationUtil.getLocalTimeZone();
        Calendar cal = Calendar.getInstance(tz);
        Date startTime = null;
        Date endTime = null;
        cal.set(2010, 3, 05, 0, 0, 0);
        startTime = cal.getTime();
        cal.set(2010, 3, 06, 0, 0, 0);
        endTime = cal.getTime();

        EmptySVAMoneyReport aReport = new EmptySVAMoneyReport();
        aReport.run(startTime, endTime);

    }
}

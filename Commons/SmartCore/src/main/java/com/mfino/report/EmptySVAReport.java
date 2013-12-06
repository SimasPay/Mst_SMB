/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
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
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 * 
 * @author xchen
 */
public class EmptySVAReport extends OfflineReportBase {

    private static final String HEADER_ROW = "#, Distributor Name, Distributor MDN, Level, " +
            "Date of Emptied SVA, Emptied By, Emptied Amount, Region Code, Company Code, Pocket ID, Pocket Status, Reference ID";
    private static final int NUM_COLUMNS = 12;
    public EmptySVAReport() {
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
//      commodityTransferQuery.setMsgType(CmFinoFIX.MsgType_EmptySVAPocket);
//      commodityTransferQuery.setCommodity(CmFinoFIX.Commodity_Airtime);
            commodityTransferQuery.setUiCategory(CmFinoFIX.TransactionUICategory_Empty_SVA);
            commodityTransferQuery.setStartTimeGE(start);
            commodityTransferQuery.setStartTimeLT(end);

            CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();

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
            List<CommodityTransfer> results = commodityTransferDAO.get(commodityTransferQuery);
            int seq = 1;
            writer.println(HEADER_ROW);
            DateFormat df = getDateFormat();

            for (CommodityTransfer ct : results) {
                Subscriber subscriber = ct.getSubscriberBySourceSubscriberID();
                long subscriberId = subscriber.getID();
                String sourceDistributorName = subscriber.getUser().getUsername();
                long dctLevel = 0;
                //long dctLevel = MerchantService.getDCTIDAndLevel(subscriberId)[1];

                Pocket pocket = ct.getPocketBySourcePocketID();
                String regionCode = StringUtils.EMPTY;
                if (subscriber.getMerchant() != null && subscriber.getMerchant().getRegion() != null) {
                    regionCode = subscriber.getMerchant().getRegion().getRegionCode();
                }
                Integer companyCode = null;
                if (subscriber.getCompany() != null) {
                    companyCode = subscriber.getCompany().getCompanyCode();
                }
                String pocketStatus = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, pocket.getStatus());
                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seq,
                        sourceDistributorName,
                        OfflineReportUtil.stripRx(ct.getSourceMDN()),
                        dctLevel,
                        df.format(ct.getStartTime()),
                        ct.getCreatedBy(),
                        ct.getAmount(),
                        regionCode,
                        (companyCode != null) ? companyCode : StringUtils.EMPTY,
                        pocket.getID(),
                        pocketStatus,
                        ct.getID()));
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

    @Override
    public String getReportName() {
        return "EmptySVAReport";
    }
}

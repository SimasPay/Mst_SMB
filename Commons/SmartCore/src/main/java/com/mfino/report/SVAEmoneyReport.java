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
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Raju
 */
public class SVAEmoneyReport extends OfflineReportBase {

    private static final int NUM_COLUMNS = 23;
    private static final String HEADER_ROW = "#,MDN,First Name, Last Name,Language,Time Zone, Subscriber Status,Restriction," +
            "Pocket Template Description, PocketID, Commodity, Pocket Status, ATM No, Address, KTP/KIMS, Dompet Merchant,Cash In,Cash Out," +
            "Smart Cash Balance, Created Date/Time, Last modified Date/Time, Company Code, Pocket Status";

    @Override
    public String getReportName() {
        return "SVAEmoneyReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start, end , null);
    }
    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            int seq = 1;
            PocketDAO dao = DAOFactory.getInstance().getPocketDAO();
            PocketQuery query = new PocketQuery();
            query.setCommodity(CmFinoFIX.Commodity_Money);
            query.setPocketType(CmFinoFIX.PocketType_SVA);
            
            query.setLastUpdateTimeLT(end);
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
            writer.println(HEADER_ROW);
            
            List<Pocket> results = dao.get(query);
            reportForSVAEmoneyList(results, writer, end, seq);
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in SVAEmoneyReport", t);
        }

        return reportFile;
    }

    public int reportForSVAEmoneyList(List<Pocket> results, PrintWriter writer, Date end, int seq) throws Exception {
        String formatStr = getFormatString(NUM_COLUMNS);
        DateFormat df = getDateFormat();
        String pocketTemplateDesc = StringUtils.EMPTY;
        String pocketCommodity = StringUtils.EMPTY;
        boolean isDompetMerchant = false;
        boolean isCashOut = false;
        boolean isCashIn = false;

        for (Pocket pocket : results) {
          try {
            SubscriberMDN subscriberMDN = pocket.getSubscriberMDNByMDNID();
            if(null == subscriberMDN) {
              log.error("Ghost pocket found!. PocketID = " + pocket.getID());
              continue;
            }
            
            Subscriber sub = subscriberMDN.getSubscriber();
            //Long smartCashBalance = pocket.getCurrentBalance();
            BigDecimal smartCashBalance = SubscriberService.getMoneySVABalanceAsOf(pocket, end);
            if (smartCashBalance==null || smartCashBalance.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            if(CmFinoFIX.PocketStatus_Retired.equals(pocket.getStatus())
                || CmFinoFIX.SubscriberStatus_Retired.equals(subscriberMDN.getStatus())){
              continue;
            }
                
            
            if (pocket.getPocketTemplate() != null) {
                PocketTemplate pTemplate = pocket.getPocketTemplate();
                pocketTemplateDesc = pTemplate.getDescription();
                int allowance = pTemplate.getAllowance();
                if ((allowance & CmFinoFIX.PocketAllowance_MerchantDompet) > 0) {
                    isDompetMerchant = true;
                }
                if ((allowance & CmFinoFIX.PocketAllowance_CashInDompet) > 0) {
                    isCashIn = true;
                }
                if ((allowance & CmFinoFIX.PocketAllowance_CashOutDompet) > 0) {
                    isCashOut = true;
                }

                pocketCommodity = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, pTemplate.getCommodity());
            }
            writer.println(String.format(formatStr,
                    seq,
                    subscriberMDN.getMDN(),
                    sub.getFirstName(),
                    sub.getLastName(),
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, null, sub.getLanguage()),
                    sub.getTimezone(),
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, subscriberMDN.getStatus()),
                    SubscriberService.getRestrictionsAsString(sub).trim(),
                    pocketTemplateDesc,
                    pocket.getID(),
                    pocketCommodity,
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, pocket.getStatus()),
                    pocket.getCardPAN(),
                    "null", //Address
                    "null", //KTP/KIMS
                    (isDompetMerchant) ? "Y" : "N",
                    (isCashIn) ? "Y" : "N",
                    (isCashOut) ? "Y" : "N",
                    smartCashBalance != null ? smartCashBalance : 0,
                    df.format(subscriberMDN.getCreateTime()),
                    df.format(subscriberMDN.getLastUpdateTime()),
                    (sub.getCompany() != null) ? sub.getCompany().getCompanyCode() : "",
                    (pocket.getStatus() != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,pocket.getStatus()): ""
                    ));
            seq++;
          } catch (Exception ex) {
            log.error("Exception in SVAEmoney Report", ex);
          }

        }
        return seq;
    }
}

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

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author Raju
 */
public class SubscriberRetirementReport extends OfflineReportBase {

    private static final int NUM_COLUMNS = 21;
    private static final String HEADER_ROW = "#,MDN, First Name, Last Name, Status, Pocket Template Description, " +
    		"Merchant Dompet, Cash In, Cash out," +
            " Last transaction Date, Last Recharge Date, Empty SVA Amount," +
            " Remaining Balance, Empty SVA Date, Retire Status Date," +
            "Created By, Updated By, Company Code, Pocket ID, Pocket Status, Pocket Type";

    @Override
    public String getReportName() {
        return "SubscriberRetirementReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start, end , null);
    }
    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            int seq = 1;
            SubscriberMdnQuery query = new SubscriberMdnQuery();
            query.setStatusIn(new Integer[]{CmFinoFIX.SubscriberStatus_PendingRetirement, CmFinoFIX.SubscriberStatus_Retired});
            query.setStatusTimeGE(start);
            query.setStatusTimeLT(end);
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
            List<SubscriberMDN> results = mdnDAO.get(query);
            reportForSubRetList(results, writer, end, seq);
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in SubscriberRetirementReport", t);
        }


        return reportFile;
    }

    public int reportForSubRetList(List<SubscriberMDN> results, PrintWriter writer, Date end, int seq) throws Exception {
        String formatStr = getFormatString(NUM_COLUMNS);
        DateFormat df = getDateFormat();
        String mdn = StringUtils.EMPTY;
        BigDecimal svaAmount = null;
        String svaStartTime = GeneralConstants.EMPTY_STRING;
        Long pocketId = null;
        Integer pocketStatus = null;
        Integer pocketType = null;
        Integer companyCode = null;

        for (SubscriberMDN subscriberMDN : results) {
            svaAmount = null;
            Subscriber sub = subscriberMDN.getSubscriber();
            mdn = subscriberMDN.getMDN();
            svaStartTime = GeneralConstants.EMPTY_STRING;

            CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
            CommodityTransfer emptySVATxn = commodityTransferDAO.getEMoneyEmptySVATxn(subscriberMDN.getID());
            if (null != emptySVATxn) {
                svaAmount = emptySVATxn.getAmount();
                svaStartTime = GeneralConstants.EMPTY_STRING + df.format(emptySVATxn.getStartTime());
            }
            //get the balance
            Pocket moneyPocket = SubscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
            
            String ptDesc = StringUtils.EMPTY;
            boolean isDompetMerchant = false;
            boolean isCashOut = false;
            boolean isCashIn = false;
            if(null != moneyPocket) {
              PocketTemplate pt = moneyPocket.getPocketTemplate(); 
              ptDesc = pt.getDescription();     
              int allowance = pt.getAllowance();

              pocketType = pt.getType();
              pocketId = moneyPocket.getID();
              pocketStatus = moneyPocket.getStatus();

              if((allowance & CmFinoFIX.PocketAllowance_MerchantDompet) > 0){
                isDompetMerchant = true;
              }
              if((allowance & CmFinoFIX.PocketAllowance_CashInDompet) > 0){
                isCashIn = true;
              }
              if((allowance & CmFinoFIX.PocketAllowance_CashOutDompet) > 0){
                isCashOut = true;
              }
            }
            
            BigDecimal balance = BigDecimal.ZERO;
            if(CmFinoFIX.MDNStatus_PendingRetirement.equals(subscriberMDN.getStatus())) {
            	balance = SubscriberService.getMoneySVABalanceAsOf(moneyPocket, end);
            }

            //getting the last recharge time
            String topUpTime = GeneralConstants.EMPTY_STRING;
            Date topUpDate = commodityTransferDAO.getTopUpTime(subscriberMDN.getID());
            if (topUpDate != null){
              topUpTime = GeneralConstants.EMPTY_STRING + df.format(topUpDate);
            }
            if(sub != null && sub.getCompany() != null){
                companyCode = sub.getCompany().getCompanyCode();
            }
            writer.println(String.format(formatStr,
                    seq,
                    OfflineReportUtil.stripRx(mdn),
                    sub.getFirstName(),
                    sub.getLastName(),
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, subscriberMDN.getStatus()),
                    ptDesc,
                    (isDompetMerchant)? "Y":"N",
                    (isCashIn)?"Y":"N",
                    (isCashOut)?"Y":"N",                    
                    subscriberMDN.getLastTransactionTime() != null ? df.format(subscriberMDN.getLastTransactionTime()) : StringUtils.EMPTY,
                    topUpTime,
                    svaAmount != null ? svaAmount : 0,
                    balance != null ? balance : 0,
                    svaStartTime, // Empty SVA DateTime
                    df.format(subscriberMDN.getStatusTime()),
                    subscriberMDN.getCreatedBy(),
                    subscriberMDN.getUpdatedBy(),
                    (companyCode != null) ? companyCode : StringUtils.EMPTY,
                    (pocketId != null) ? pocketId : StringUtils.EMPTY,
                    (pocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, pocketStatus) : StringUtils.EMPTY,
                    (pocketType != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, pocketType) : StringUtils.EMPTY
                    ));
            seq++;
        }
        return seq;
    }
}

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
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class SubscribersReport extends OfflineReportBase{
    private static final int NUM_COLUMNS = 33;
    private static final String HEADER_ROW = "#,MDN, First Name, Last Name, Language,Time Zone, Subscriber Status,Restriction, " +
            "Pocket Template Description, PocketID, Commodity, Pocket Status, ATM No, Address, KTP/KIMS, Dompet Merchant, Cash In, Cash Out, Smart Cash Balance, " +
            "Created Date/Time, Last modified Date/Time, Number of recharge per day, Allowed recharge per day, " +
            "Number of recharge per week, Allowed recharge per week, Number of recharge per month, " +
            "Allowed recharge per month, Accumulated Transaction Daily, Expenditure limit per day, " +
            "Accumulated Transaction Weekly, Expenditure limit per week, Accumulated transaction value monthly, " +
            "Expenditure limit per month";
    
     
    @Override
    public String getReportName() {
        return "SubscribersReport";
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
            //Get all active, pending retired subscribers
            int batchSize = ConfigurationUtil.getSubscriberReportBatchSize();
            int firstResult = 0;
            
            SubscriberMdnQuery query = new SubscriberMdnQuery();
            Company company = null;
            //query.setStatusIn(new Integer[]{CmFinoFIX.SubscriberStatus_Initialized, CmFinoFIX.SubscriberStatus_Active, CmFinoFIX.SubscriberStatus_PendingRetirement});
            query.setStatusNE(CmFinoFIX.SubscriberStatus_Retired);
            query.setLastUpdateTimeLT(end);
            query.setIDOrdered(true);
            
            if(companyID != null){
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                company = companyDao.getById(companyID);
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
            
            int total = mdnDAO.getNonRetiredCount(end, companyID);
            while(firstResult < total) {
              query.setStart(firstResult);
              query.setLimit(batchSize);
              List<SubscriberMDN> tempResults = mdnDAO.get(query);
              seq = reportForSubList(tempResults, writer, end, seq);
              firstResult += batchSize;
              
              //clear all the cached results so that we don't use up all the memory
              HibernateUtil.getCurrentSession().clear();
            }
            
          //Get all subscribers retired today.
            List<SubscriberMDN> results = null;
            SubscriberMdnQuery query2 = new SubscriberMdnQuery();            
            //query2.setStatusIn(new Integer[]{CmFinoFIX.SubscriberStatus_Retired});
            query2.setStatusEQ(CmFinoFIX.SubscriberStatus_Retired);
            query2.setStatusTimeGE(start);
            query2.setStatusTimeLT(end); 
            query2.setCompany(company);
            results = mdnDAO.get(query2);

            reportForSubList(results, writer, end, seq);

            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        }catch(Throwable t){
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in SubscribersReport", t);
        }
        return reportFile;
    }

    public int reportForSubList(List<SubscriberMDN> results, PrintWriter writer, Date end, int seq){
        String formatStr = getFormatString(NUM_COLUMNS);
        DateFormat df = getDateFormat();
        BigDecimal dailyTxnValue = BigDecimal.ZERO;
        BigDecimal dailyTxnLimit = BigDecimal.ZERO;
            BigDecimal weeklyTxnValue = BigDecimal.ZERO;
            BigDecimal weeklyTxnLimit = BigDecimal.ZERO;
            BigDecimal monthlyTxnValue = BigDecimal.ZERO;
            BigDecimal monthlyTxnLimit = BigDecimal.ZERO;
            Integer dailyTxnCount = 0;
            Integer dailyTxnCountLimit = 0;
            Integer weeklyTxnCount = 0;
            Integer weeklyTxnCountLimit = 0;
            Integer monthlyTxnCount = 0;
            Integer monthlyTxnCountLimit = 0;
            String pocketTemplateDesc = StringUtils.EMPTY;
            String pocketCommodity = StringUtils.EMPTY;
            String mdn = StringUtils.EMPTY;
            boolean isDompetMerchant = false;
            boolean isCashOut = false;
            boolean isCashIn = false;
            BigDecimal smartCashBalance = null; 

            for (SubscriberMDN subscriberMDN : results) {
              try {
                Subscriber sub = subscriberMDN.getSubscriber();                
                Set<Pocket> pocketSet= subscriberMDN.getPocketFromMDNID();
                for (Pocket pocket : pocketSet){
                    isDompetMerchant = false;
                    isCashOut = false;
                    isCashIn = false;
                    pocketTemplateDesc = null;
                    pocketCommodity = null;
                    dailyTxnValue = pocket.getCurrentDailyExpenditure();
                    weeklyTxnValue = pocket.getCurrentWeeklyExpenditure();
                    monthlyTxnValue = pocket.getCurrentMonthlyExpenditure();
                    dailyTxnCount = pocket.getCurrentDailyTxnsCount();
                    weeklyTxnCount = pocket.getCurrentWeeklyTxnsCount();
                    monthlyTxnCount = pocket.getCurrentMonthlyTxnsCount();
                    smartCashBalance = null;

                    if(pocket.getPocketTemplate() != null){
                        PocketTemplate pTemplate = pocket.getPocketTemplate();
                        pocketTemplateDesc = pTemplate.getDescription();
                        int allowance = pTemplate.getAllowance();
                        if((allowance & CmFinoFIX.PocketAllowance_MerchantDompet) > 0){
                          isDompetMerchant = true;
                        }
                        if((allowance & CmFinoFIX.PocketAllowance_CashInDompet) > 0){
                          isCashIn = true;
                        }
                        if((allowance & CmFinoFIX.PocketAllowance_CashOutDompet) > 0){
                          isCashOut = true;
                        }

                        pocketCommodity = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, pTemplate.getCommodity());
                        dailyTxnLimit = pTemplate.getMaxAmountPerDay();
                        weeklyTxnLimit = pTemplate.getMaxAmountPerWeek();
                        monthlyTxnLimit  = pTemplate.getMaxAmountPerMonth();
                        dailyTxnCountLimit = pTemplate.getMaxTransactionsPerDay();
                        weeklyTxnCountLimit = pTemplate.getMaxTransactionsPerWeek();
                        monthlyTxnCountLimit = pTemplate.getMaxTransactionsPerMonth();
                        if(CmFinoFIX.Commodity_Money.equals(pTemplate.getCommodity()) && CmFinoFIX.PocketType_SVA.equals(pTemplate.getType()))
                          smartCashBalance = SubscriberService.getMoneySVABalanceAsOf(pocket, end);                        
                    }

                    mdn = subscriberMDN.getMDN();

//                    if(CmFinoFIX.SubscriberStatus_Retired.equals(subscriberMDN.getStatus())) {
//                        mdn = OfflineReportUtil.stripRx(mdn);
//                    }
                    mdn = OfflineReportUtil.stripRx(mdn);
                    
                    //Pocket moneyPocket = SubscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
                                        
                    writer.println(String.format(formatStr,
                            seq,
                            mdn,                            
                            StringUtils.isNotBlank(sub.getFirstName()) ? sub.getFirstName().replace("\n", "").replace(",", ""):"",
                            StringUtils.isNotBlank(sub.getLastName()) ? sub.getLastName().replace("\n", "").replace(",", ""):"",                           
                            EnumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, null,  sub.getLanguage()),
                            sub.getTimezone(),
                            EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null,  subscriberMDN.getStatus()),
                            SubscriberService.getRestrictionsAsString(sub).trim(),
                            pocketTemplateDesc,
                            pocket.getID(),
                            pocketCommodity,
                            EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, pocket.getStatus()),
                            pocket.getCardPAN(),
                            "null", //Address
                            "null", //KTP/KIMS
                            (isDompetMerchant)? "Y":"N",
                            (isCashIn)?"Y":"N",
                            (isCashOut)?"Y":"N",
                            smartCashBalance,
                            df.format(subscriberMDN.getCreateTime()),
                            df.format(subscriberMDN.getLastUpdateTime()),
                            (dailyTxnValue != null) ? dailyTxnValue : 0,
                            (dailyTxnLimit != null) ? dailyTxnLimit : 0,
                            (weeklyTxnValue != null) ? weeklyTxnValue : 0,
                            (weeklyTxnLimit != null) ? weeklyTxnLimit : 0,
                            (monthlyTxnValue != null) ? monthlyTxnValue : 0,
                            (monthlyTxnLimit != null) ? monthlyTxnLimit : 0,
                            (dailyTxnCount != null) ? dailyTxnCount : 0,
                            (dailyTxnCountLimit != null) ? dailyTxnCountLimit : 0,
                            (weeklyTxnCount != null) ? weeklyTxnCount : 0,
                            (weeklyTxnCountLimit != null) ? weeklyTxnCountLimit : 0,
                            (monthlyTxnCount != null) ? monthlyTxnCount : 0,
                            (monthlyTxnCountLimit != null) ? monthlyTxnCountLimit : 0
                            ));
                    
                    seq++;
                }
              } catch (Exception ex) {
                log.error("Error at seq = "+seq, ex);
                continue;
              }
            }
            return seq;
    }

}

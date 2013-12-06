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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainLevelDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.query.DistributionChainLevelQuery;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.DistributionChainLevel;
import com.mfino.domain.Merchant;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.service.DistributionChainTemplateService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author sunil
 */
public class MerchantReport extends OfflineReportBase {

    private Logger log = LoggerFactory.getLogger(this.getClass()); 
    private CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
    //private PendingCommodityTransferDAO pctDAO = new PendingCommodityTransferDAO();
    private MerchantDAO merchantDao = DAOFactory.getInstance().getMerchantDAO();
    private DistributionChainLevelDAO levelDAO = DAOFactory.getInstance().getDistributionChainLevelDAO();
    private static final int NUM_COLUMNS = 52;
    private static final String HEADER_ROW = "#,Distributor Name,Language,Time Zone,Channel Name,Level Number,Group ID," +
            "Contact Detail First Name,Contact Detail Last Name,MDN,Parent Distributor Name,Parent MDN No.,Address,City,Email Address," +
            "Postal Code,Phone no,Fax No,Outlet Address,Notification Method,Geographic Restriction Code,Status,Restriction," +
            "Pocket Type,PocketCommodity,PocketID," +
            "Allowed for Distribution,Allowed for Subscriber Recharge,Allowed for Transfer,Allowed for indirect distribution," +
            "Allowed for indirect Transfer,Airtime stock balance,Commission,Accumulated Transaction Value Daily," +
            "Maximum spending limit per day,Accumulated Transaction value weekly,Maximum spending limit per week," +
            "Accumulated transaction value monthly,Maximum spending limit per month,Number Transactions per day," +
            "Allowed number transaction per day,Number Transactions per week,Allowed number transactions per week," +
            "Number transactions per month,Allowed number transactions per month,Created Date/Time,Last modified Date/Time," +
            "Created By,Updated By, Region code, Company Code, Pocket Status" ;

    private Map<Long, List<CommodityTransfer>> sourcePocketIDVsRAFTfrs = new HashMap<Long, List<CommodityTransfer>>();
  //  private Map<Long, PendingCommodityTransfer> pocketIDVsLastPendingTfr = new HashMap<Long, PendingCommodityTransfer>();
   // private Map<Long, CommodityTransfer> pocketIDVsLastTfr = new HashMap<Long, CommodityTransfer>();
    
    //Sid: Can we Inject multiple DAOs?
    @Inject
    public MerchantReport(MerchantDAO merDAO) {
        merchantDao = merDAO;
    } 

    private void processRAFTxns(Date start, Date end, Long companyId) {
      
      HibernateUtil.getCurrentSession().beginTransaction();
     
      long stTime = System.currentTimeMillis();
      Integer count = ctDAO.getCountOfAllRAFTxnAfter(end, companyId);
      int firstResult = 0;
      //secret params
     int maxResults = ConfigurationUtil.getMerchantReportTxnBatchSize();
     
      while(firstResult < count) {
        log.info("Count = " + firstResult);
        List<CommodityTransfer> allRAFTxns = ctDAO.getAllRAFTxnsAfter(firstResult, maxResults, end, companyId);
        firstResult += allRAFTxns.size();

        for(CommodityTransfer ct : allRAFTxns) {
          Long sourcePocketID = ct.getPocketBySourcePocketID().getID();
          if(sourcePocketID != null) {
            List<CommodityTransfer> ctList = sourcePocketIDVsRAFTfrs.get(sourcePocketID);
            if(ctList == null)
              ctList = new LinkedList<CommodityTransfer>(); 
            ctList.add(ct);
            sourcePocketIDVsRAFTfrs.put(sourcePocketID, ctList);
          }
        }
      }
      HibernateUtil.getCurrentSession().getTransaction().rollback();
      
      long endTime = System.currentTimeMillis();
      log.info("Pre-processing Time for RAF Txns = " + (endTime - stTime));
    }
    
    @Override
    public File run(Date start, Date end) {
        return run(start, end , null);
    }
    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        
        try {
            //FIXME: Having this in the same transaction is resulting in "communications link failure" msg
            processRAFTxns(start, end, companyID);
            HibernateUtil.getCurrentSession().beginTransaction();
          //  HibernateUtil.getCurrentSession().connection().setReadOnly(true);
            
            int seq = 1;
     
          //Get all non-retired merchants.
            
            int firstResult = 0;
            int batchSize = ConfigurationUtil.getMerchantReportBatchSize();
            int total = merchantDao.getActiveMerchantCount(end, companyID);
            
            MerchantQuery query = new MerchantQuery();
            query.setMerchantStatusIn(new Integer[]{CmFinoFIX.SubscriberStatus_Initialized, CmFinoFIX.SubscriberStatus_Active, CmFinoFIX.SubscriberStatus_PendingRetirement});
            query.setEndRegistrationDate(end);
            query.setIDOrdered(true);

            Company company = null;
            if(companyID != null){
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                company =companyDao.getById(companyID);
                if(company != null){
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            }
            else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            List<Merchant> results = null;
            writer.println(HEADER_ROW);
            
            while(firstResult < total) {
              
              query.setStart(firstResult);
              query.setLimit(batchSize);              
              results = merchantDao.get(query);
              
              seq = reportForMerchantList(results, writer, seq, start, end);
              
              firstResult += batchSize;
              results.clear();
              HibernateUtil.getCurrentSession().clear();
              System.gc();
            }
            
            //Get all merchants retired today
            MerchantQuery query2 = new MerchantQuery();
            query2.setMerchantStatus(CmFinoFIX.SubscriberStatus_Retired);
            query2.setStatusTimeLT(end);
            query2.setStatusTimeGE(start);
            query2.setCompany(company);
            
            results = merchantDao.get(query2);
            reportForMerchantList(results, writer, seq, start, end);
            
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        }catch(Throwable t){
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in Merchant Report", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "MerchantReport";

    }

    private int reportForMerchantList(List<Merchant> results, PrintWriter writer, int seq, Date start, Date end){
        DateFormat df = getDateFormat();
        String formatStr = getFormatString(NUM_COLUMNS);
        String mdn = "";
        String parenDistributorName = "";
        String parentMdn ="";
        String distributorName = "";
        String dctName = "";
        String authenticationPhoneNumber="";
        int levelNumber = 0;
        boolean allowedForDistribution;
        boolean allowedForSubscriberRecharge;
        boolean allowedForTransfer;
        boolean allowedForIndirectDistribution;
        boolean allowedForIndirectTransfer;
        BigDecimal airtimeStockBalance = null;
        BigDecimal commission=BigDecimal.ZERO;
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
        String pocketType = StringUtils.EMPTY;
        String pocketCommodity = StringUtils.EMPTY;

        String addressLine1="";
        String addressCity="";
        String addressZipCode="";
        String addressFranchisetLine1="";
        String notificationMethodText="";
        String restrictionsText="";
        Integer companyCode = null;

        for (Merchant merchant : results) {
          try {
          mdn = "";
          parenDistributorName = "";
          distributorName = "";
          dctName = "";
          levelNumber = 0;
          parentMdn ="";
          authenticationPhoneNumber="";
          allowedForDistribution = false;
          allowedForSubscriberRecharge = false;
          allowedForTransfer = false;
          allowedForIndirectDistribution = false;
          allowedForIndirectTransfer = false;                
          commission=BigDecimal.ZERO;
          addressLine1="";
          addressCity="";
          addressZipCode="";
          addressFranchisetLine1="";
          notificationMethodText="";
          restrictionsText="";
          long[] dctIDAndLevel = new long[] {-1,-1};

          /*try {
            dctIDAndLevel = MerchantService.getDCTIDAndLevel(merchant.getID());
          } catch (Throwable ex) {
            log.error("Merchant ID = " + merchant.getID(), ex);
            continue;
          }*/

          long templateID = dctIDAndLevel[0];
          levelNumber = (int) dctIDAndLevel[1];

          dctName = DistributionChainTemplateService.getName(templateID);                

          Set<SubscriberMDN> mdnSet=merchant.getSubscriber().getSubscriberMDNFromSubscriberID();
          SubscriberMDN subcr = (SubscriberMDN) mdnSet.toArray()[0];
          mdn = subcr.getMDN();

          if(OfflineReportUtil.isSystemMDN(mdn))
            continue;

          authenticationPhoneNumber = subcr.getAuthenticationPhoneNumber();

          DistributionChainLevelQuery dctQuery = new DistributionChainLevelQuery();
          dctQuery.setDistributionChainTemplateID(templateID);
          dctQuery.setLevel( new Integer(levelNumber));
          List<DistributionChainLevel> dctLevels = levelDAO.get(dctQuery);
          DistributionChainLevel dctLevel = null;
          if (dctLevels != null && dctLevels.size() > 0) {
            dctLevel = dctLevels.get(0);
          }

          if (dctLevel != null) {
            if (dctLevel.getPermissions() != null) {
              int perm = dctLevel.getPermissions();
              allowedForDistribution = (perm & CmFinoFIX.DistributionPermissions_DirectDistribute) > 0;
              allowedForIndirectDistribution = (perm & CmFinoFIX.DistributionPermissions_IndirectDistribute) > 0;
              allowedForTransfer = (perm & CmFinoFIX.DistributionPermissions_DirectTransfer) > 0;
              allowedForIndirectTransfer = (perm & CmFinoFIX.DistributionPermissions_IndirectTransfer) > 0;
              allowedForSubscriberRecharge = (perm & CmFinoFIX.DistributionPermissions_Recharge) > 0;
              //allowedForLop = getBooleanString((perm & CmFinoFIX.DistributionPermissions_LOP) > 0);
              //all=getBooleanString((perm & CmFinoFIX.DistributionPermissions_LOPDistribute) > 0);
            }
            if (dctLevel.getCommission() != null) {
              commission = dctLevel.getCommission();
            }
          }

          notificationMethodText = SubscriberService.getNotificationMethodAsString(merchant.getSubscriber());
          restrictionsText = SubscriberService.getRestrictionsAsString(merchant.getSubscriber());

          if (merchant.getAddressByMerchantAddressID() != null) {
            if (merchant.getAddressByMerchantAddressID().getLine1() != null) {
              addressLine1 = merchant.getAddressByMerchantAddressID().getLine1();
            }
            if (merchant.getAddressByMerchantAddressID().getLine2() != null) {
              addressLine1 = addressLine1 + merchant.getAddressByMerchantAddressID().getLine2();
            }
            if (merchant.getAddressByMerchantAddressID().getCity() != null) {
              addressCity = merchant.getAddressByMerchantAddressID().getCity();
            }
            if (merchant.getAddressByMerchantAddressID().getZipCode() != null) {
              addressZipCode = merchant.getAddressByMerchantAddressID().getZipCode();
            }
          }

          if(merchant.getAddressByFranchiseOutletAddressID() !=null){
            addressFranchisetLine1=merchant.getAddressByFranchiseOutletAddressID().getLine1();
          }

          if (merchant.getMerchantByParentID() != null) {
            Subscriber sub = merchant.getMerchantByParentID().getSubscriber();
            User user = sub.getUser();

            if (user != null) {
              parenDistributorName = user.getUsername();
            }

            subcr = (SubscriberMDN) sub.getSubscriberMDNFromSubscriberID().toArray()[0];
            if (subcr != null) {
              parentMdn = subcr.getMDN();
            }
          }

          User u = merchant.getSubscriber().getUser();
          if (u != null) {
            distributorName = u.getUsername();
          }

          Set<Pocket> pocketSet = null;
          Set<SubscriberMDN> mdns = merchant.getSubscriber().getSubscriberMDNFromSubscriberID();
          if(mdns.isEmpty() == false){
            pocketSet = mdns.iterator().next().getPocketFromMDNID();
          }
          if(merchant.getSubscriber() != null && merchant.getSubscriber().getCompany() != null)
            companyCode = merchant.getSubscriber().getCompany().getCompanyCode();
          for (Pocket pocket : pocketSet){
            pocketType = null;
            pocketCommodity = null;
            airtimeStockBalance = null;
            dailyTxnValue = pocket.getCurrentDailyExpenditure();
            weeklyTxnValue = pocket.getCurrentWeeklyExpenditure();
            monthlyTxnValue = pocket.getCurrentMonthlyExpenditure();
            dailyTxnCount = pocket.getCurrentDailyTxnsCount();
            weeklyTxnCount = pocket.getCurrentWeeklyTxnsCount();
            monthlyTxnCount = pocket.getCurrentMonthlyTxnsCount();

            if(pocket.getPocketTemplate() != null){
              PocketTemplate pTemplate = pocket.getPocketTemplate();
              //Code to get the Current Balance
              if(!(pTemplate.getType().equals(CmFinoFIX.PocketType_SVA)
                  && pTemplate.getCommodity().equals(CmFinoFIX.Commodity_Airtime)
              )) {
                continue;
              }

              // airtimeStockBalance = pocket.getCurrentBalance();
              List<CommodityTransfer> rafTxns = sourcePocketIDVsRAFTfrs.get(pocket.getID());
               // airtimeStockBalance = MerchantService.getAirtimeSVABalanceAsOf(merchant, pocket, end, rafTxns);
              //airtimeStockBalance = getMerchantAirtimeBalance(merchant, pocket, end);
              
              pocketType = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, pTemplate.getType());
              pocketCommodity = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, pTemplate.getCommodity());
              dailyTxnLimit = pTemplate.getMaxAmountPerDay();
              weeklyTxnLimit = pTemplate.getMaxAmountPerWeek();
              monthlyTxnLimit  = pTemplate.getMaxAmountPerMonth();
              dailyTxnCountLimit = pTemplate.getMaxTransactionsPerDay();
              weeklyTxnCountLimit = pTemplate.getMaxTransactionsPerWeek();
              monthlyTxnCountLimit = pTemplate.getMaxTransactionsPerMonth();
            }

            writer.println(String.format(formatStr,
                seq,
                distributorName,
                EnumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, null, merchant.getSubscriber().getLanguage()),
                merchant.getSubscriber().getTimezone(),
                dctName,
                (levelNumber > -1) ? levelNumber :"", //Is there any use showing invalid level info?
                    merchant.getGroupID(),
                    StringUtils.isNotBlank(merchant.getSubscriber().getFirstName()) ? merchant.getSubscriber().getFirstName().replace(",", "") : "",
                    StringUtils.isNotBlank(merchant.getSubscriber().getLastName()) ? merchant.getSubscriber().getLastName().replace(",", "") : "",                    
                    OfflineReportUtil.stripRx(mdn),
                    parenDistributorName,
                    parentMdn,
                    (StringUtils.isNotBlank(addressLine1)) ? addressLine1.replace(",", "") : "",
                    (StringUtils.isNotBlank(addressCity)) ? addressCity.replace(",", ""): "",
                    merchant.getSubscriber().getEmail(),
                    addressZipCode,
                    authenticationPhoneNumber,
                    merchant.getAuthorizedFaxNumber(),
                    StringUtils.isNotBlank(addressFranchisetLine1) ? addressFranchisetLine1.replace(",", ""):"",
                    notificationMethodText.trim(),
                    "N/A",//Geographic Restricions Code
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, merchant.getStatus()),
                    restrictionsText.trim(),
                    pocketType,
                    pocketCommodity,
                    pocket.getID(),
                    allowedForDistribution ? "Y" : "N",
                    allowedForSubscriberRecharge ? "Y" : "N",
                    allowedForTransfer ? "Y" : "N",
                    allowedForIndirectDistribution ? "Y" : "N",
                    allowedForIndirectTransfer ? "Y" : "N",
                    airtimeStockBalance != null ? airtimeStockBalance : 0,
                    commission,
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
                            (monthlyTxnCountLimit != null) ? monthlyTxnCountLimit : 0,
                            df.format(merchant.getCreateTime()),
                            df.format(merchant.getLastUpdateTime()),
                            merchant.getCreatedBy(),
                            merchant.getUpdatedBy(),
                            (merchant.getRegion() != null) ? merchant.getRegion().getRegionCode() : "",
                            (companyCode != null) ? companyCode : "",
                            (pocket.getStatus() != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, pocket.getStatus()): ""
                            ));
                    seq++;
                    if(seq % 1000 == 0) {
                      log.info("Completed = " + seq);
                    }
          }
        
        }catch (ObjectNotFoundException ex) {
          log.error("Error in Merchant Report", ex);
          continue;
        }
        }
        return seq;
    }
      }

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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.mfino.constants.GeneralConstants;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author sandeepjs
 */
public class RetiredEloadMerchantsReport extends OfflineReportBase {

    private Logger log = LoggerFactory.getLogger(this.getClass()); 
    private CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
    private MerchantDAO merchantDAO = null;
    private static final String HEADER_ROW = "#, Distributor Name, Level number, Group ID, MDN, Status, " +
            "Last transaction Date & Time, Last Recharge Date & Time, Empty SVA Amount, " +
            "Remaining Balance, Empty SVA Date & Time, Retire Status Date & Time, Created By, Updated By, " +
            "Region Code, Company Code, Pocket ID, Pocket Status";
    private static final int NUM_COLUMNS = 18;
    private Map<Long, List<CommodityTransfer>> sourcePocketIDVsRAFTfrs = new HashMap<Long, List<CommodityTransfer>>();
    @Inject
    public RetiredEloadMerchantsReport(MerchantDAO merchantDAO) {
        this.merchantDAO = merchantDAO;
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
        return run(start, end, null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;

        try {
            processRAFTxns(start, end, companyID);
            HibernateUtil.getCurrentSession().beginTransaction();
            MerchantQuery merchantQuery = new MerchantQuery();
            merchantQuery.setMerchantStatusIn(new Integer[]{CmFinoFIX.SubscriberStatus_PendingRetirement, CmFinoFIX.SubscriberStatus_Retired});
            merchantQuery.setStatusTimeLT(end);
            merchantQuery.setStatusTimeGE(start);

            if(companyID != null){
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                Company company =companyDao.getById(companyID);
                if(company != null){
                    merchantQuery.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            }
            else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            // Get all merchants whose status is "Retired or Pending Retired".
            List<Merchant> results = merchantDAO.getByHQL(merchantQuery);

            int seqno = 1;
            DateFormat df = getDateFormat();
            writer.println(HEADER_ROW);
            for (Merchant merchantObj : results) {
                SubscriberMDN subscriberMDN  = (SubscriberMDN) merchantObj.getSubscriber().getSubscriberMDNFromSubscriberID().toArray()[0];

                //long level = MerchantService.getDCTIDAndLevel(merchantObj.getID())[1];
                long level = 1;
                // Get the corresponding transaction empty sva
                /*CommodityTransferQuery query = new CommodityTransferQuery();
                query.setUiCategory(CmFinoFIX.TransactionUICategory_Empty_SVA);
                query.setCommodity(CmFinoFIX.Commodity_Airtime);
                query.setExactSourceMDN(subscriberMDN.getMDN());
                CommodityTransferDAO commodityTransferDAO = new CommodityTransferDAO();
                List<CommodityTransfer> emptySVATxns = commodityTransferDAO.get(query);*/
                
                CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
                String svaAmount = GeneralConstants.EMPTY_STRING;
                String svaStartTime = GeneralConstants.EMPTY_STRING;
                CommodityTransfer emptySVATxn = commodityTransferDAO.getEmptySVATxn(subscriberMDN.getID());

                if (emptySVATxn != null){ 
                    
                    svaAmount = GeneralConstants.EMPTY_STRING + emptySVATxn.getAmount();
                    svaStartTime = GeneralConstants.EMPTY_STRING + df.format(emptySVATxn.getStartTime());
                }


                // Get the EMTPTY SVA POCKET FOR REMAINING BALANCE.
//                PocketDAO pdao = new PocketDAO();
//                PocketQuery pquery = new PocketQuery();
//                pquery.setMdnIDSearch(merchantObj.getID());
//                pquery.setPocketType(CmFinoFIX.PocketType_SVA);
//                pquery.setCommodity(CmFinoFIX.Commodity_Airtime);
//                pquery.setIsDefault(true);
//                List<Pocket> pockets = pdao.get(pquery);
//
//                Pocket p = null;
//                if(pockets != null && pockets.size() > 0)
//                    p = pockets.get(0);

//                for (int i = 0; i < pockets.size(); i++) {
//                    p = (Pocket) pockets.get(i);
//                    if (p.getIsDefault()) {
//                        break;
//                    }
//                }
//                
//                Pocket p = SubscriberService.getDefaultPocket(merchantObj.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Airtime);
//
//                String balance = GeneralConstants.EMPTY_STRING + 0;
//
//                if (p != null && p.getCurrentBalance() != null) {
//                    balance = GeneralConstants.EMPTY_STRING + p.getCurrentBalance();
//                }
//
                Pocket pocket = SubscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Airtime);
                List<CommodityTransfer> rafTxns = sourcePocketIDVsRAFTfrs.get(pocket.getID());
                BigDecimal balance = BigDecimal.ZERO;
                //if(CmFinoFIX.MDNStatus_PendingRetirement.equals(merchantObj.getStatus()))
                	//balance = MerchantService.getAirtimeSVABalanceAsOf(merchantObj, pocket, end, rafTxns);
                	
               /* CommodityTransferQuery ctquery = new CommodityTransferQuery();
                ctquery.setUiCategory(CmFinoFIX.TransactionUICategory_MA_Topup);
                ctquery.setExactSourceMDN(subscriberMDN.getMDN());
                CommodityTransferDAO commodityTransferDAO1 = new CommodityTransferDAO();
                List<CommodityTransfer> topUpCTS = commodityTransferDAO1.get(ctquery);*/

                String topUpTime = GeneralConstants.EMPTY_STRING;
                Date topUpDate = commodityTransferDAO.getTopUpTime(subscriberMDN.getID());
                if (topUpDate != null){
                  topUpTime = GeneralConstants.EMPTY_STRING + df.format(topUpDate);
                }

                String mdnString = subscriberMDN.getMDN();
                if(StringUtils.isNotEmpty(mdnString)) {
                    mdnString = OfflineReportUtil.stripRx(mdnString);
                }
                String regionCode = GeneralConstants.EMPTY_STRING;
                if(merchantObj.getRegion() != null)
                    regionCode = merchantObj.getRegion().getRegionCode();
                Integer companyCode = null;
                if(subscriberMDN != null && subscriberMDN.getSubscriber() != null && subscriberMDN.getSubscriber().getCompany() != null){
                    companyCode = subscriberMDN.getSubscriber().getCompany().getCompanyCode();
                }
                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seqno++,
                        merchantObj.getSubscriber().getUser().getUsername(),
                        (level > -1) ? level : StringUtils.EMPTY,
                        merchantObj.getGroupID(),
                        mdnString,
                        EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, merchantObj.getStatus()),
                        subscriberMDN.getLastTransactionTime() != null ? df.format(subscriberMDN.getLastTransactionTime()) : StringUtils.EMPTY,
                        topUpTime, //Recharge DateTime
                        svaAmount, // Empty SVA Amount
                        (balance != null) ? balance : 0, // Remaining Balance
                        svaStartTime, // Empty SVA DateTime
                        merchantObj.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired) && merchantObj.getLastUpdateTime() != null ? df.format(merchantObj.getLastUpdateTime()) : "",
                        merchantObj.getCreatedBy(),
                        merchantObj.getUpdatedBy(),
                        regionCode,
                        (companyCode != null) ? companyCode : StringUtils.EMPTY,
                        pocket.getID(),
                        EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,pocket.getStatus())
                        ));
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in RetiredEloadMerchantsReport", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "RetiredEloadMerchantsReport";
    }
}

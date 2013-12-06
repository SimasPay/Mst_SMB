/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.report;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Merchant;
import com.mfino.domain.Pocket;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author sunil
 */
public class MerchantReportTest  {
	
    @Test
    public void testSearch() throws Exception {
        MerchantDAO merDao=new MerchantDAO();
        DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        Date stDt=df.parse("20081117160000Z");
        Date endDt=df.parse("20101117160000Z");

        MerchantReport rpt=new MerchantReport(merDao);

        File report = rpt.run(stDt, endDt);

        report.delete();
    }

    @Test
    public void testMerchantBalance(){
    	TimeZone tz = TimeZone.getTimeZone("Asia/Jakarta");
        Calendar cal = Calendar.getInstance(tz);
        Date end = null;
        cal.set(2010, 5, 01, 0, 0, 0);
        end = cal.getTime();
        
    	long merchantID = 3364758;
    	long pocketID = 3449465;
    	HibernateUtil.getCurrentSession().beginTransaction();
    	Map<Long, List<CommodityTransfer>> sourcePocketIDVsRAFTfrs = new HashMap<Long, List<CommodityTransfer>>();
    	processRAFTxns(end, sourcePocketIDVsRAFTfrs, pocketID);
    	Merchant merchant = new MerchantDAO().getById(merchantID);
    	Pocket pocket = new PocketDAO().getById(pocketID);
    	List<CommodityTransfer> raf = sourcePocketIDVsRAFTfrs.get(pocketID);
    	//BigDecimal airtimeStockBalance = MerchantService.getAirtimeSVABalanceAsOf(merchant, pocket, end, raf);
    	BigDecimal airtimeStockBalance = new BigDecimal(0);
    	HibernateUtil.getCurrentTransaction().rollback();
    	System.out.println("Balance = " + airtimeStockBalance);
}
    private void processRAFTxns(Date end, Map<Long, List<CommodityTransfer>> sourcePocketIDVsRAFTfrs, Long pocketid) {

     //   HibernateUtil.getCurrentReportSession().beginTransaction();
        CommodityTransferDAO ctDAO = new CommodityTransferDAO();
        long stTime = System.currentTimeMillis();
        //Integer count = ctDAO.getCountOfAllRAFTxnAfter(end);
        int firstResult = 0;
        //secret params
       int maxResults = ConfigurationUtil.getMerchantReportTxnBatchSize();

       
          System.out.println("Count = " + firstResult);
          List<CommodityTransfer> allRAFTxns = ctDAO.getAllRAFTxnsAfter(firstResult, maxResults, end, pocketid, null);
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
        
       // HibernateUtil.getCurrentReportSession().getTransaction().rollback();
        
        long endTime = System.currentTimeMillis();
        //log.info("Pre-processing Time for RAF Txns = " + (endTime - stTime));
      }

}



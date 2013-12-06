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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.LOPDAO;
import com.mfino.dao.query.LOPQuery;
import com.mfino.domain.Company;
import com.mfino.domain.LOP;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class LOPDiscountChangeReport extends OfflineReportBase {

    private LOPDAO lopDAO = null;
    
    private static final int NUM_COLUMNS = 11;
    private static final String HEADER_ROW = "#,LOPID,Merchant Name,LOP Amount,Initial Discount,New Discount,Paid Amount,Created Date,Updated Date,Updated By,Comment";
    private Logger log = LoggerFactory.getLogger(this.getClass()); 
    
    @Inject
    public LOPDiscountChangeReport(LOPDAO lpDAO) {
        lopDAO = lpDAO;
    }
    @Override
    public File run(Date start, Date end) {
        return run(start, end , null);
    }
    @Override
    public File run(Date start, Date end, Long companyID) {
        HibernateUtil.getCurrentSession().beginTransaction();
        File reportFile = null;
        try {
            LOPQuery query = new LOPQuery();
            query.setCommissionChanged(true);
            query.setLastUpdateTimeGE(start);
            query.setLastUpdateTimeLT(end);
            if(companyID != null){                
                Company company = DAOFactory.getInstance().getCompanyDAO().getById(companyID);
                if(company != null){
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            }
            else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            List<LOP> results = lopDAO.get(query);
            
            int seq = 1;
            
            writer.println(HEADER_ROW);

            for (LOP lop : results) {            	
                String distributorUsername = lop.getMerchantBySubscriberID().getSubscriber().getUser().getUsername();                
                BigDecimal initialDiscount = lop.getDistributionChainLevelByDCTLevelID().getCommission();
                
                DateFormat df = getDateFormat();

                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seq,
                        lop.getID(),
                        distributorUsername,
                        lop.getAmountDistributed(),
                        initialDiscount,
                        lop.getCommission(),
                        lop.getActualAmountPaid(),
                        df.format(lop.getCreateTime()),
                        df.format(lop.getLastUpdateTime()),
                        lop.getUpdatedBy(),""
                        //lop.getComment()
                        ));
                seq++;
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        }catch(Throwable t){
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in LOPDiscountChangeReport", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "LOPDiscountChangeReport";

    }
    /*
    public static void main(String...agrs){
        LOPDiscountChangeReport lop = new LOPDiscountChangeReport(new LOPDAO());
        lop.run(null, null);
    }*/
}
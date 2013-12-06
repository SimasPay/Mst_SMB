/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import com.mfino.dao.BulkBankAccountDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkBankAccountQuery;
import com.mfino.domain.BulkBankAccount;
import com.mfino.domain.Company;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class BulkRemittanceReport extends OfflineReportBase {

    private static final int NUM_COULMNS = 5;
    private static final String HEADER_ROW = "#, Date And Time, File Name, No. Of Records In File, Company Name";

    @Override
    public String getReportName() {
        return "BulkRemittanceReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start,end,null);
    }
    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        String formatStr = getFormatString(NUM_COULMNS);

        BulkBankAccountDAO bulkBankAccountDAO = DAOFactory.getInstance().getBulkBankAccountDAO();
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            int seq = 1;

            BulkBankAccountQuery query = new BulkBankAccountQuery();
            if (null != start) {
                query.setStartDate(start);
            }

            if (null != end) {
                query.setEndDate(end);
            }

            query.setIsStatusUploadToBankOrCompleted(Boolean.TRUE);

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
            List<BulkBankAccount> results = bulkBankAccountDAO.get(query);

            DateFormat df = getDateFormat();
            
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            writer.println(HEADER_ROW);
            for (BulkBankAccount ct : results) {
                writer.println(String.format(formatStr,
                        seq,
                        df.format(ct.getCreateTime()),
                        ct.getFileName(),
                        ct.getTotalLineCount(),
                        ct.getCreatedBy()));
                seq++;
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in BulkRemitanceReport", t);
        }
        return reportFile;
    }
}

package com.mfino.report;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class MerchantDompetReport extends OfflineReportBase {

    private static final int NUM_COLUMNS = 24;
    private static final String HEADER_ROW = "#, MDN, Merchant Code, User Name, Trade Name, " +
            "Line of Business, Address, City, Zipcode, Contact Person 1, Contact Person 2, " +
            "Title of Contact Person 1, Title of Contact Person 2, Phone No, Fax No, Email, Account No, " +
            "ATM No, Merchant Class, Date Created, Date Modified, Length of Business, Pocket ID, Company Code";

    @Override
    public String getReportName() {
        return "MerchantDompetReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start,end,null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        Company company = null;
        DateFormat df = getDateFormat();
        String formatStr = getFormatString(NUM_COLUMNS);
        PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();

        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            //PocketQuery query = new PocketQuery();
            //query.setDompetMerchant(Boolean.TRUE);
            List<Pocket> results = pocketDAO.getDompetMerchantByHQL(companyID);
            if(companyID != null){
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                company = companyDao.getById(companyID);
                reportFile = getReportFilePath(company);
            } else {
                reportFile = getReportFilePath();
            }
            //List<Subscriber> results = subscriberDAO.get(query);
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            int seq = 1;
            writer.println(HEADER_ROW);
            String cardPan = StringUtils.EMPTY;
            String accountNumber = StringUtils.EMPTY;
            Integer companyCode = null;
            for (Pocket poc : results) {
                cardPan = StringUtils.EMPTY;
                accountNumber = StringUtils.EMPTY;
                SubscriberMDN subMDN = poc.getSubscriberMDNByMDNID();
                if (poc.getPocketTemplate().getBankAccountCardType() == CmFinoFIX.BankAccountCardType_DirectBankAccount) {
                    accountNumber = poc.getCardPAN();
                } else {
                    cardPan = poc.getCardPAN();
                }
                if(poc.getSubscriberMDNByMDNID().getSubscriber() != null && poc.getSubscriberMDNByMDNID().getSubscriber().getCompany() != null){
                    companyCode = poc.getSubscriberMDNByMDNID().getSubscriber().getCompany().getCompanyCode();
                }
                writer.println(String.format(formatStr,
                        seq,
                        subMDN.getMDN(),
                        StringUtils.EMPTY, // Merchant Code??
                        poc.getSubscriberMDNByMDNID().getSubscriber().getFirstName() + " " + poc.getSubscriberMDNByMDNID().getSubscriber().getLastName(),
                        StringUtils.EMPTY, //Trade Name
                        StringUtils.EMPTY, //Line of Buisness
                        StringUtils.EMPTY, //Address
                        StringUtils.EMPTY, //City
                        StringUtils.EMPTY, //ZipCode
                        StringUtils.EMPTY, //Contact Person1
                        StringUtils.EMPTY, //Contact Person2
                        StringUtils.EMPTY,//title of contact person
                        StringUtils.EMPTY,// title of second contact
                        StringUtils.EMPTY,// Phone No
                        StringUtils.EMPTY,// Fax No
                        poc.getSubscriberMDNByMDNID().getSubscriber().getEmail(),
                        accountNumber,
                        cardPan,  // ATM No
                        StringUtils.EMPTY, // Merchant Class
                        df.format(poc.getSubscriberMDNByMDNID().getSubscriber().getCreateTime()),
                        df.format(poc.getSubscriberMDNByMDNID().getSubscriber().getLastUpdateTime()),
                        StringUtils.EMPTY, //Length of Buisness
                        poc.getID(),    // pocket id
                        (companyCode != null) ? companyCode : StringUtils.EMPTY  // company code
                        )); 
                 seq++;
            }

            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in MerchantDompetReport", t);
        }
        return reportFile;
    }

/*
    public static void main(String args[]) {
    MerchantDompetReport sReport = new MerchantDompetReport();
    sReport.run(null, null);
    }
*/
     
}

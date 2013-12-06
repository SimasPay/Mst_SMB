/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author sandeepjs
 */
public class BankReport extends OfflineReportBase {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private CommodityTransferDAO commodityTransferDAO = null;
    private SubscriberDAO subDAO = DAOFactory.getInstance().getSubscriberDAO();
    private static final int NUM_COLUMNS = 20;
    private static final String HEADER_ROW = "#,MSISDN,Subscriber Name,Bank Card,Bank Name,Terminal ID,Delivery Channel," +
            "Banking and Payment Gateway Transaction Trace Number, RRN, Reference Number,Operator Ref No,Transaction Date,Transaction Time," +
            "Transaction Type,Transaction Amount,Transaction Status,Response Code, Operator RRN, Destination Company Code, ISO8583_Variant";
    private String formatStr = getFormatString(NUM_COLUMNS);
    private DateFormat df = getDateFormat();
    private PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();

    @Inject
    public BankReport(CommodityTransferDAO ctDAO) {
        commodityTransferDAO = ctDAO;
    }

    @Override
    public List<File> runAndGetMutlipleReports(Date start, Date end) {
        return runAndGetMutlipleReports(start, end, null);
    }
    
    @Override
    public List<File> runAndGetMutlipleReports(Date start, Date end, Long companyId) {
        List<File> list = new ArrayList<File>();
        try {
            HibernateUtil.getCurrentSession().beginTransaction();

            Iterator<Integer> iterator = getUniqueBankCodes(start, end).iterator();
            int count =0;
            while (iterator.hasNext()) {
                Integer uniqueBankCode = iterator.next();
                if (null == uniqueBankCode) {
                    // Log here.
                    //DefaultLogger.error("Bad data got in Bank Report. The bank code obtained is null.");
                    continue;
                }
                count++;
                log.info("Generating Bank Report for Bank Code =" + uniqueBankCode);
                File reportFileForThisBank = runForThisBankCode(start, end, uniqueBankCode, companyId);
                list.add(reportFileForThisBank);
            }
            log.info("Generated Bank Reports for " + count + " Banks");
            HibernateUtil.getCurrentTransaction().commit();

        } catch (Throwable t) {
            HibernateUtil.getCurrentTransaction().rollback();
            log.error("Error in BankReport", t);
        }

        return list;
    }

    private File runForThisBankCode(Date start, Date end, Integer bankCode, Long companyId) throws Exception {
        CommodityTransferQuery query = new CommodityTransferQuery();
        query.setIsBankChannel(Boolean.TRUE);
        query.setExactBankCode(bankCode);
        query.setStartTimeGE(start);
        query.setStartTimeLT(end);
        query.setHasCSRAction(false);
        Company company =null;
        if (companyId != null) {
            CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
            company = companyDao.getById(companyId);
            if (company != null) {
                query.setCompany(company);
            }
        }

        List<CommodityTransfer> results = commodityTransferDAO.get(query);
        List<PendingCommodityTransfer> pendingResults = pctDAO.get(query);

        File reportFile = null;
        if(company == null) {
            reportFile = getReportFilePath("Bank_" + bankCode.intValue() + "_");
        }else {
            reportFile = getReportFilePath(company.getCompanyName()+ "_Bank_" + bankCode.intValue() + "_");
        }
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
        int seq = 1;
        writer.println(HEADER_ROW);
        for (CommodityTransfer ct : results) {

            writer.println(constructString(ct, seq, false));
            seq++;
        }

        for (PendingCommodityTransfer ct : pendingResults) {
            writer.println(constructString(ct, seq, true));
            seq++;
        }
        writer.close();

        return reportFile;
    }

    private Set<Integer> getUniqueBankCodes(Date startGE, Date startLT) {
        List<Integer> uniqueBankCodes = commodityTransferDAO.getAllDistinctBankCodes(startGE, startLT);
        List<Integer> uniqueBankCodesFromPendingCommodity = pctDAO.getAllDistinctBankCodes(startGE, startLT);
        uniqueBankCodes.addAll(uniqueBankCodesFromPendingCommodity);
        Set<Integer> set = new HashSet<Integer>();
        set.addAll(uniqueBankCodes);

        return set;
    }
    @Override
    public File run(Date start, Date end) {
        return run(start,end,null);
    }
    @Override
    public File run(Date start, Date end, Long companyID){

        File reportFile = null;

        try {
            HibernateUtil.getCurrentSession().beginTransaction();

            CommodityTransferQuery query = new CommodityTransferQuery();
            query.setIsBankChannel(Boolean.TRUE);
            query.setHasCSRAction(false);

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
            List<CommodityTransfer> results = commodityTransferDAO.get(query);
            List<PendingCommodityTransfer> pendingResults = pctDAO.get(query);

            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            int seq = 1;
            writer.println(HEADER_ROW);
            for (CommodityTransfer ct : results) {
                writer.println(constructString(ct, seq, false));
                seq++;
            }

            for (PendingCommodityTransfer ct : pendingResults) {
                writer.println(constructString(ct, seq, true));
                seq++;
            }
            writer.close();

            HibernateUtil.getCurrentTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentTransaction().rollback();
            log.error("Error in BankReport", t);
        }

        return reportFile;
    }

    @Override
    public String getReportName() {
        return "BankReport";
    }

    private String constructString(CmFinoFIX.CRCommodityTransfer ct, int seq, boolean isPending) {
        String destMDN = ct.getDestMDN();
        Integer destCompanyCode = null;
        Long destSubscriberId = ct.getDestSubscriberID();
        String status=(CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) ? "Successful" : "Failed";
        String destSubName = SubscriberService.getSubscriberName(destMDN);
        if(CmFinoFIX.SourceApplication_BankChannel.equals(ct.getSourceApplication()) && CmFinoFIX.ResponseCode_Success.equals(ct.getOperatorReversalResponseCode())){
            status ="Reversed";     	
       }
        if (destSubscriberId != null) {
            Subscriber destSubscriber = subDAO.getById(destSubscriberId);
            if (destSubscriber.getCompany() != null) {
                destCompanyCode = destSubscriber.getCompany().getCompanyCode();
            }
        }
        return String.format(formatStr,
                seq,
		OfflineReportUtil.stripRx(destMDN),
                (destSubName != null) ? destSubName : ct.getDestSubscriberName(),
                ct.getDestCardPAN(),
                ct.getISO8583_AcquiringInstIdCode(),
                ct.getISO8583_CardAcceptorIdCode(),
                ct.getISO8583_MerchantType(),
                ct.getISO8583_SystemTraceAuditNumber(),
                ct.getISO8583_RetrievalReferenceNum(),                
                ct.getID(),
                ct.getOperatorAuthorizationCode(), //Operator Transaction ID?
                df.format(ct.getStartTime()),
                ct.getISO8583_LocalTxnTimeHhmmss(),
                EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()),
                ct.getAmount(),
                (isPending)
                ? "Pending"
                : status,
                ct.getISO8583_ResponseCode(),
                StringUtils.isNotBlank(ct.getOperatorRRN())? ct.getOperatorRRN() : StringUtils.EMPTY,
                (destCompanyCode != null) ? destCompanyCode : StringUtils.EMPTY,
                 EnumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_Variant, null, ct.getISO8583_Variant())
                );
    }

    public boolean hasMultipleReports() {
        return true;
    }
}

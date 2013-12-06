package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.query.CreditCardTransactionQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.service.EnumTextService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoUtil;

public class CreditCardTransactionReport extends OfflineReportBase {
    private static final int NUM_COLUMNS = 29;
    private static final String HEADER_ROW = "#,CCTxnID," +
    "SubscriberID,PocketID,Amount," +
    "CurrName,TransactionDate,NSIATransactionCompletionTime," +
    "CardNoPartial," +
    "Bank,BankResCode,BankResMsg,ApprovalCode," +
    "BillReferenceNumber,LastUpdateTime,CompanyCode,"+
    "OrderNo,TransactionStatus,TransferFailureReason,TransactionType,SourceUsername,SourceMDN,DestMDN,OperatorRRN,BucketType,PaymentGatewayEDU,IsVoid,VoidBy,SourceIP";
    
    
    @Override
    public String getReportName() {
        return "CreditCardTransactionReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start, end, null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        DateFormat df = getDateFormat();
        String formatStr = getFormatString(NUM_COLUMNS);
        try {
            CreditCardTransactionQuery query = new CreditCardTransactionQuery();
            HibernateUtil.getCurrentSession().beginTransaction();
            int seq = 1;
            Company company = null;
            if (companyID != null) {
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                company = companyDao.getById(companyID);
                if (company != null) {
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            } else {
                reportFile = getReportFilePath();
            }
            if (null != start) {
                query.setStartDate(start);
            }
            if (null != end) {
                query.setEndDate(end);
            }
            CreditCardTransactionDAO ccDAO = DAOFactory.getInstance().getCreditCardTransactionDAO();
            List<CreditCardTransaction> ccResults = ccDAO.get(query);
            // trying to get all the transaction from commodity transfer
            // and pending commodity transfer with transaction type , start and
            // end date
            CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
            PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
            List<CommodityTransfer> ctResults = ctDAO.getAllCompleteCCTransactions(start, end, company);
            List<PendingCommodityTransfer> pctResults = pctDAO.getAllPendingCCTransactions(start, end, company);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            Map<Long,CRCommodityTransfer> ccMap = new HashMap<Long,CRCommodityTransfer>();
			for(CommodityTransfer ctRecord:ctResults){
				ccMap.put(ctRecord.getCreditCardTransaction().getID(),ctRecord);
			}
			for(PendingCommodityTransfer pctRecord:pctResults){
				ccMap.put(pctRecord.getCreditCardTransaction().getID(),pctRecord);
			}
            writer.println(HEADER_ROW);
            for (CreditCardTransaction ccRecord : ccResults) {
                 CRCommodityTransfer ctRecord = ccMap.get(ccRecord.getID());
                 String transferStatusText = null;
                 String transferFailureReasonText = null;
                 String transType = null;
                 String ccfailureReason = null;
                 
                 if(ctRecord != null){
                	 if(ctRecord.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)){
                		 transferStatusText =CmFinoFIX.TransStatus_Successful;
                	 }else if(ctRecord.getTransferStatus().equals(CmFinoFIX.TransferStatus_Failed)){
                		 transferStatusText =CmFinoFIX.TransStatus_Failed;
                	 }                	 
                	 transferFailureReasonText =EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ctRecord.getTransferFailureReason());                	 
                 }else{
                	 transferFailureReasonText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CCFailureReason,null,ccRecord.getCCFailureReason());
                 }
				if ("1".equals(ccRecord.getOperation())) {
					transType = "CC_Payment";
				} else if ("2".equals(ccRecord.getOperation())) {
					transType = "CC_TopUp";
				}
				
				
				
            	 writer.println(String.format(formatStr,
                        seq,
                        ccRecord.getID(),
                        ccRecord.getSubscriber() != null ? ccRecord.getSubscriber().getID() : StringUtils.EMPTY,
                        ccRecord.getPocket() != null ? ccRecord.getPocket().getID() : StringUtils.EMPTY,
//                        ccRecord.getDescription() != null ? ccRecord.getDescription() : StringUtils.EMPTY,
                        ccRecord.getAmount(),
                       // ccRecord.getTransStatus() != null ? ccRecord.getTransStatus() : StringUtils.EMPTY,
                        ccRecord.getCurrCode() != null ? MfinoUtil.getCurrencyName(ccRecord.getCurrCode()) : StringUtils.EMPTY,
//                        ccRecord.getTransactionDate() != null ? ccRecord.getTransactionDate().substring(0,ccRecord.getTransactionDate().length()-4 ) : StringUtils.EMPTY,
                        df.format(ccRecord.getCreateTime()),		
                        ccRecord.getNSIATransCompletionTime() != null ? df.format(ccRecord.getNSIATransCompletionTime()) : StringUtils.EMPTY,
                        ccRecord.getCardNoPartial() != null ? ccRecord.getCardNoPartial() : StringUtils.EMPTY,
                        ccRecord.getAcquirerBank() != null ? ccRecord.getAcquirerBank() : StringUtils.EMPTY,
                        ccRecord.getBankResCode() != null ? ccRecord.getBankResCode() : StringUtils.EMPTY,
                        ccRecord.getBankResMsg() != null ? ccRecord.getBankResMsg() : StringUtils.EMPTY,
                        ccRecord.getAuthID() != null ? ccRecord.getAuthID() : StringUtils.EMPTY,                        
                        ccRecord.getBillReferenceNumber() != null ? ccRecord.getBankReference() : StringUtils.EMPTY,                        
//                        df.format(ccRecord.getCreateTime()),
                        df.format(ccRecord.getLastUpdateTime()),
                        ccRecord.getCompany() != null ? ccRecord.getCompany().getCompanyCode() : StringUtils.EMPTY,
                        ctRecord != null ?ctRecord.getID():StringUtils.EMPTY,
                        (transferStatusText != null) ? transferStatusText : ccRecord.getTransStatus(),
                        (transferFailureReasonText != null) ? transferFailureReasonText: StringUtils.EMPTY,
                        (transType != null) ? transType: StringUtils.EMPTY,
                        ccRecord.getSubscriber().getUserBySubscriberUserID().getUsername(),            
                        ccRecord.getSubscriber().getUserBySubscriberUserID().getUsername(),
                        ccRecord.getMDN()!=null?ccRecord.getMDN():StringUtils.EMPTY,
                        (ctRecord!=null && ctRecord.getOperatorRRN()!=null )?ctRecord.getOperatorRRN():StringUtils.EMPTY,
                        ccRecord.getCCBucketType()!=null?EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CCBucketType,null,ccRecord.getCCBucketType()):StringUtils.EMPTY,
                        ccRecord.getPaymentGatewayEDU()!=null && ccRecord.getPaymentGatewayEDU().equals(Boolean.TRUE)?"Y":"N",
                        ccRecord.getIsVoid()!=null&&ccRecord.getIsVoid().equals(Boolean.TRUE)?"Y":"N",
                        ccRecord.getVoidBy()!=null?ccRecord.getVoidBy():StringUtils.EMPTY,
                        ccRecord.getSourceIP()!=null?ccRecord.getSourceIP():StringUtils.EMPTY
                       ));
                seq++;
            }
           
            writer.close();
        } catch (Throwable t) {
            log.error("Error in CreditCard Transaction Report", t);
        } finally {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
        }
        return reportFile;
    }

//    public static void main(String args[]) {
//        CreditCardTransactionReport report = new CreditCardTransactionReport();
//        report.run(null, null, 1l);
//    }
}

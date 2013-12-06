package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.BillPaymentTransactionDAO;
import com.mfino.dao.BillerDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.query.BillPaymentTransactionQuery;
import com.mfino.domain.BillPaymentTransaction;
import com.mfino.domain.Biller;
import com.mfino.domain.Company;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.service.EnumTextService;
import com.mfino.util.HibernateUtil;

public class BillPaymentTransactionReport extends OfflineReportBase {
    private static final int NUM_COLUMNS = 15;
    private static final String HEADER_ROW = "#, ID, Reference No, TransactionID, " +
    "SubscriberMDN," +
    " TransactionDate, LastUpdateTime, BillerCode, BillerName," +    
    " BillReferenceNumber, Amount,"+
    " Status, TransactionType, CustomerID, TransationFee,";
    private CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
    private PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
    private BillerDAO billerDAO = DAOFactory.getInstance().getBillerDAO();
    private HashMap<Long, Biller> billerMap = new HashMap<Long, Biller>();
    private Biller biller;
    private List<Long> billPayments = new ArrayList<Long>();
    
    
    @Override
    public String getReportName() {
        return "BillsPaymentTransactionReport";
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
            BillPaymentTransactionQuery query = new BillPaymentTransactionQuery();
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
                query.setCreateTimeGE(start);
            }
            if (null != end) {
                query.setCreateTimeLT(end);
            }
           BillPaymentTransactionDAO bptDAO = DAOFactory.getInstance().getBillPaymentTransactionDAO();
            List<BillPaymentTransaction> bptResults = bptDAO.get(query);
           
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
           
            writer.println(HEADER_ROW);
            
            for (BillPaymentTransaction bptRecord : bptResults) {

                Long transferID;
                CRCommodityTransfer ctRecord = null;
            	String status;
            	String type = StringUtils.EMPTY;
            	if(bptRecord.getBillPaymentTransactionType()!=null)
               	 type=EnumTextService.getEnumTextValue(CmFinoFIX.TagID_MsgType, CmFinoFIX.Language_English, bptRecord.getBillPaymentTransactionType()); 
                       	
            	transferID=bptRecord.getTransactionID();
            	if (transferID != null) {
                    ctRecord = ctDao.getById(transferID);
                    if (ctRecord == null) {
                        ctRecord = pctDao.getById(transferID);
                    }
                }
            	
            	status = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, bptRecord.getStatus());	
                 if(ctRecord != null){
                	 if(billPayments.contains(transferID)){
                		 continue;
                	 }
                	 billPayments.add(transferID);
                	 
                	 status = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, ctRecord.getTransferStatus());
//                	 transferFailureReasonText =EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ctRecord.getTransferFailureReason());                	 
                	 
                 }
                 if(bptRecord.getBillerID()!=null){
                	if(billerMap.containsKey(bptRecord.getBillerID())){
                		biller = billerMap.get(bptRecord.getBillerID());
                	}else{
                		biller=billerDAO.getById(bptRecord.getBillerID());
                		billerMap.put(bptRecord.getBillerID(), biller);
                	}
                 }

                 String subscriberMDN = null;
                 if(bptRecord.getSubscriber()!=null){
                	Set<SubscriberMDN> mdnSet = bptRecord.getSubscriber().getSubscriberMDNFromSubscriberID();
                	for(SubscriberMDN mdn: mdnSet){
                		subscriberMDN = mdn.getMDN(); 
                		//FIXME: currently only one mdn is allowed for subscriber;
                		break;
                	}
                 }

                 type=EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BillPaymentTransactionType, CmFinoFIX.Language_English, bptRecord.getBillPaymentTransactionType());

                 
            	 writer.println(String.format(formatStr,
                        seq,
                        bptRecord.getID(),
                        transferID!=null? transferID:StringUtils.EMPTY,
                        bptRecord.getParentTransactionID(),
                        subscriberMDN!= null ? subscriberMDN : StringUtils.EMPTY,
                        df.format(bptRecord.getCreateTime()),
                        df.format(bptRecord.getLastUpdateTime()),
                        biller!=null?biller.getBillerCode():StringUtils.EMPTY,
                        biller!=null?biller.getBillerName():StringUtils.EMPTY,
                        bptRecord.getBillPaymentReferenceID()!=null ? bptRecord.getBillPaymentReferenceID() : StringUtils.EMPTY,
                        bptRecord.getAmount()!=null ? bptRecord.getAmount() : StringUtils.EMPTY,
                        status,
                        type,
                        bptRecord.getCustomerID()!=null ? bptRecord.getCustomerID() : StringUtils.EMPTY,
                        bptRecord.getTransactionFee()!=null ? bptRecord.getTransactionFee() : StringUtils.EMPTY                   
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

    public static void main(String args[]) {
        BillPaymentTransactionReport report = new BillPaymentTransactionReport();
        report.run(null, null, 1l);
    }
}

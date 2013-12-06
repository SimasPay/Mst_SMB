package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.util.DateUtil;
import com.mfino.util.ReportUtil;

public class PartnerTransactionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 11;
	private String HEADER_ROW = "# ," +
			"Partner Code , " +
			"Partner Name, " +
			"Total transactions , " +
			"Total Amount , " +
			"No.of Successful Transactions, " +
			"Successful Transaction Amount, " +
			"No.of Failed Transactions, " +
			"Failed Transaction Amount, " +
			"Credit Amount, " +
			"Debit Amount";
	
	private File reportFile;
	private PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	private HashMap<Long,Integer> totalTransaction;
	private HashMap<Long,Integer> successfulTransaction;
	private HashMap<Long,Integer> failedTransaction;
	private HashMap<Long,BigDecimal> successAmount;
	private HashMap<Long,BigDecimal> failAmount;
	private HashMap<Long,BigDecimal> totalAmount;
	private HashMap<Long,BigDecimal> creditAmount;
	private HashMap<Long,BigDecimal> debitAmount;
	private HashMap<Long,Long> partnerIdvsSubId;
	private HashMap<Long,Long> subIdvsPartnerId;
	private List<Long> partnersubIds;
	private List<String> partnermdns;
	private HashMap<String,Long> mdnvsIds;
	private HashMap<Long,List<CommodityTransfer>> partnerIdvsTransactions;
		
	private TransactionsLogDAO tlDao = DAOFactory.getInstance().getTransactionsLogDAO();
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end, ReportBaseData basedata) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		String startTime = getDateFormat().format(new Date());
		String formatStr = getFormatString(NUM_COLUMNS);
		List<File> reports = new ArrayList<File>();
		log.info("Processing "+reportName+" StartTime:"+startTime);
		
		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			//writer.println(HEADER_ROW);
			XLSReport xlsReport = new XLSReport(reportName, end);
			
			//adding logo
			try{
				xlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			
			xlsReport.addMergedRegion();
			
			xlsReport.addReportTitle(reportName);
			xlsReport.addHeaderRow(HEADER_ROW);
		
			
			ReportBaseData data = basedata;
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
			} 
			if(data.getCtList()==null){
				data.getCommodityTransactions(start, end);
			}
			PartnerQuery query = new PartnerQuery();
			query.setPartnerType(CmFinoFIX.TagID_BusinessPartnerTypePartner);
//			query.setUpgradeStateSearch(CmFinoFIX.UpgradeState_Approved);
			List<Partner> partners = partnerDao.get(query);
			getPartnerSubscriberMap(partners);
			getCounts(data);
			
			int seq = 1;
			for(Partner partner:partners){
				String rowContent = String.format(formatStr, 
							seq,
							partner.getPartnerCode(),
							partner.getTradeName(),
							totalTransaction.get(partnerIdvsSubId.get(partner.getID())),
							totalAmount.get(partnerIdvsSubId.get(partner.getID())),
							successfulTransaction.get(partnerIdvsSubId.get(partner.getID())),
							successAmount.get(partnerIdvsSubId.get(partner.getID())),
							failedTransaction.get(partnerIdvsSubId.get(partner.getID())),
							failAmount.get(partnerIdvsSubId.get(partner.getID())),
							creditAmount.get(partnerIdvsSubId.get(partner.getID())),
							debitAmount.get(partnerIdvsSubId.get(partner.getID()))
							);
				xlsReport.addRowContent(rowContent); 
				seq++;
				}
							
			xlsReport.writeToFileStream(reportFile,HEADER_ROW, reportName);
		reports.add(reportFile);
		List<File> partnerreports =generateTransactionDetailReports(partners,partnerIdvsTransactions,end);
		if(partnerreports!=null&&!partnerreports.isEmpty()){
		reports.addAll(partnerreports);
		}
		}catch (Exception e) {
			log.error("Error in"+ reportName+":",e);
		}
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime"+getDateFormat().format(new Date()));
		return reports;
	}

	private void getPartnerSubscriberMap(List<Partner> partners) {
		partnerIdvsSubId = new HashMap<Long, Long>();
		subIdvsPartnerId = new HashMap<Long, Long>();
		partnersubIds = new ArrayList<Long>();
		partnermdns = new ArrayList<String>();
		mdnvsIds = new HashMap<String, Long>();
			for(Partner partner:partners){
			Partner partner2 = partnerDao.getById(partner.getID());	
			SubscriberMDN subMdn =  partner2.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
			partnerIdvsSubId.put(partner2.getID(),  partner2.getSubscriber().getID());
			subIdvsPartnerId.put( partner2.getSubscriber().getID(),partner2.getID());
			partnersubIds.add( partner2.getSubscriber().getID());
			partnermdns.add(subMdn.getMDN());
			mdnvsIds.put(subMdn.getMDN(),  partner2.getSubscriber().getID());
		}
	}
	
	private void getCounts(ReportBaseData data) {
		 totalTransaction = new HashMap<Long, Integer>();
		 successfulTransaction = new HashMap<Long, Integer>();
		 failedTransaction = new HashMap<Long, Integer>();
		 successAmount = new HashMap<Long, BigDecimal>();
		 failAmount = new HashMap<Long, BigDecimal>();
		 totalAmount = new HashMap<Long, BigDecimal>();
		 creditAmount = new HashMap<Long, BigDecimal>();
		 debitAmount = new HashMap<Long, BigDecimal>();
		 partnerIdvsTransactions = new HashMap<Long, List<CommodityTransfer>>();
		 List<CommodityTransfer> ctlist=data.getCtList();
		if(ctlist!=null){
		 for(CommodityTransfer ct:ctlist){
			 if(partnersubIds.contains(ct.getSubscriberBySourceSubscriberID().getID())){
				 putCount(ct.getSubscriberBySourceSubscriberID().getID(),ct,true);
			 }else if(partnersubIds.contains(ct.getDestSubscriberID())){
				 putCount(ct.getDestSubscriberID(),ct,false);
			 }else if(partnermdns.contains(ct.getSourceMDN())){
				 putCount(mdnvsIds.get(ct.getSourceMDN()),ct,true); 
			 }else if(partnermdns.contains(ct.getDestMDN())){
				 putCount(mdnvsIds.get(ct.getDestMDN()),ct,false); 
			 }			
		 }
		}
		 
		 for(Long subid:partnersubIds){
			 if(!totalTransaction.containsKey(subid)){
				 totalTransaction.put(subid, 0);
				 totalAmount.put(subid, BigDecimal.ZERO);
				 successfulTransaction.put(subid, 0);
				 successAmount.put(subid, BigDecimal.ZERO);
				 failedTransaction.put(subid, 0);
				 failAmount.put(subid, BigDecimal.ZERO);
				 creditAmount.put(subid, BigDecimal.ZERO);
				 debitAmount.put(subid, BigDecimal.ZERO);
			 }else{
				 if(!successfulTransaction.containsKey(subid)){
					 successfulTransaction.put(subid, 0);
					 successAmount.put(subid, BigDecimal.ZERO);
				 }
				 if(!failedTransaction.containsKey(subid)){
					 failedTransaction.put(subid, 0);
					 failAmount.put(subid, BigDecimal.ZERO);
				 }
				 if(!creditAmount.containsKey(subid)){
					 creditAmount.put(subid, BigDecimal.ZERO);
				 }
				 if(!debitAmount.containsKey(subid)){
					 debitAmount.put(subid, BigDecimal.ZERO);
				 }				 
			 }
		 }
	}	
	
	private void putCount(Long subid, CommodityTransfer ct, boolean isSource) {
		List<CommodityTransfer> ctList;
		Long partnerID = subIdvsPartnerId.get(subid);
		if(partnerIdvsTransactions.containsKey(partnerID)){
			ctList = partnerIdvsTransactions.get(partnerID);
		}else{
			ctList = new ArrayList<CommodityTransfer>(); 
		}
		ctList.add(ct);
		partnerIdvsTransactions.put(partnerID, ctList);
		
		BigDecimal transAmount = BigDecimal.ZERO;
		if(isSource){
			transAmount = ct.getAmount().add(ct.getCharges());
		}else{
			transAmount = ct.getAmount();
		}
		
		//set total transaction count
		if(totalTransaction.containsKey(subid)){
			totalTransaction.put(subid,totalTransaction.get(subid)+1);
		}else{
			totalTransaction.put(subid, 1);
		}
		
		//set total amount
		if(totalAmount.containsKey(subid)){
			totalAmount.put(subid,totalAmount.get(subid).add(transAmount));
		}else{
			totalAmount.put(subid, transAmount);
		}
		
		//set successful transaction count and amount
		if(CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())){
			if(successfulTransaction.containsKey(subid)){
				successfulTransaction.put(subid, successfulTransaction.get(subid)+1);
			}else{
				successfulTransaction.put(subid, 1);
			}
			if(successAmount.containsKey(subid)){
				successAmount.put(subid, successAmount.get(subid).add(transAmount));
			}else{
				successAmount.put(subid, transAmount);
			}
			//set credit or debit amount
			if(isSource){
				if(creditAmount.containsKey(subid)){
					creditAmount.put(subid, creditAmount.get(subid).add(transAmount));
				}else{
					creditAmount.put(subid, transAmount);
				}
			}else{
				if(debitAmount.containsKey(subid)){
					debitAmount.put(subid, debitAmount.get(subid).add(transAmount));
				}else{
					debitAmount.put(subid, transAmount);
				}
			}
		}else{ //set failed transaction count and amount
			if(failedTransaction.containsKey(subid)){
				failedTransaction.put(subid, failedTransaction.get(subid)+1);
			}else{
				failedTransaction.put(subid, 1);
			}
			if(failAmount.containsKey(subid)){
				failAmount.put(subid, failAmount.get(subid).add(transAmount));
			}else{
				failAmount.put(subid, transAmount);
			}
		}		
	}
	
	private List<File> generateTransactionDetailReports(List<Partner> partners,
			HashMap<Long, List<CommodityTransfer>> partnerIdTransactions,Date end) {
		 int NUM_COLUMNS = 11;
		 String HEADER_ROW = "# ," +
		 		"Date , " +
		 		"Source MDN, " +
		 		"NameOfSubscriber , " +
		 		"Amount , " +
		 		"Transaction type, " +
		 		"Status, " +
		 		"Reference ID, " +
		 		"Bill details, " +
		 		"charges," +
		 		"Bank Stan"; 
		 String formatStr = getFormatString(NUM_COLUMNS);
		 DateFormat df = getDateFormat();
		 ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		 CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
		 ServiceChargeTransactionLog sctl;
		 CommodityTransfer ct2;
		 List<File> partnerreports = new ArrayList<File>();
		for(Partner partner:partners){
			try{
			List<CommodityTransfer> ctList = partnerIdTransactions.get(partner.getID());
			File report = ReportUtil.getReportFilePath(partner.getTradeName()+"_PartnerTransactionsReport",end,ReportUtil.EXCEL_EXTENTION);
			String partnerReportName = partner.getTradeName()+"_TransactionsReport";
			XLSReport xlsReport = new XLSReport(partnerReportName, end);
			
			//adding logo
			try{
				xlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			
			xlsReport.addMergedRegion();
			
			xlsReport.addReportTitle(partner.getTradeName()+"_TransactionsReport");
			xlsReport.addHeaderRow(HEADER_ROW);
		
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(report)));
			//writer.println(HEADER_ROW);
			int seq=1;
			if(ctList!=null){
			for(CommodityTransfer ct:ctList){
				sctl=null;
				ct2=null;
				TransactionsLog tl = tlDao.getById(ct.getTransactionsLogByTransactionID().getID());
				long transactionid = tl.getParentTransactionID()!=null? tl.getParentTransactionID():tl.getID();
				sctl = sctlDao.getByTransactionLogId(transactionid);
				if(sctl!=null
						&&sctl.getCommodityTransferID()!=null
						&&(!sctl.getCommodityTransferID().equals(ct.getID()))){
					ct2 = ctDao.getById(sctl.getCommodityTransferID());
				}
				String bankStan = ct.getBankSystemTraceAuditNumber();
				if(bankStan == null){
					bankStan = "";
				}
				String transactionType = ct.getSourceMessage();
				if(transactionType==null || transactionType.isEmpty())
				{
					transactionType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, CmFinoFIX.Language_English, ct.getUICategory());
				}
				String rowContent = String.format(formatStr, 
						seq,
						df.format(ct.getCreateTime()),
						ct2!=null?ct2.getSourceMDN():ct.getSourceMDN(),
					    ct2!=null?ct2.getSourceSubscriberName():ct.getSourceSubscriberName(),
						ct.getAmount(),
						transactionType,
						enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, CmFinoFIX.Language_English, ct.getTransferStatus()),
						ct.getID(),
						ct.getSourceReferenceID()!=null?ct.getSourceReferenceID():"",
						ct.getCharges(),
						bankStan
						);
				xlsReport.addRowContent(rowContent);
				seq++;
			}
			}
			xlsReport.writeToFileStream(report,HEADER_ROW, reportName);
			if(report!=null){
			partnerreports.add(report);
			}
		}catch (Exception e) {
			log.error("Exception occured for partner"+partner.getPartnerCode(),e);
		}
		}
		return partnerreports;
		
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end,ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getFileName() {
		return reportFile.getName();
	}
	
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

	
	@Override
	public boolean hasMultipleReports(){
		return true;
	}

	public static void main(String a[]){
		Date d= new Date();
		PartnerTransactionReport report = new PartnerTransactionReport();
		report.runAndGetMutlipleReports(DateUtil.addDays(d, -60), d, null);
	}

}

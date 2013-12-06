package com.mfino.report.generalreports;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.OfflineReport;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.ReportMailUtil;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.EnumTextService;
import com.mfino.service.HibernateService;
import com.mfino.util.DateUtil;
import com.mfino.util.ReportUtil;

public class EODSummaryReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private File reportFile;
	private String reportName;
	private static final int NUM_COLUMNS = 12;
	private String HEADER_ROW = "# ," +
			"StartTime," +
			"EndTime ," +
			"Source MDN," +
			"Destination MDN," +
			"Amount," +
			"Total charge," +
			"Transaction type," +
			"Status," +
			"Reference ID,"+
			"Process Time(sec.),"+
			"Bank Stan";
	private int seq;
	private BigDecimal totalTransferedAmount = BigDecimal.ZERO;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	
	public void run() {
		Date end = new Date();
		Date start = DateUtil.addHours(end, -24);
		log.info("Starting offline report processing for "
				+ SimpleDateFormat.getInstance().format(start) + " to "
				+ SimpleDateFormat.getInstance().format(end));
		HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
		Session session = hibernateService.getSessionFactory().openSession();
		HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
		sessionHolder.setSession(session);
		DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
		try {
			OfflineReportDAO dao = DAOFactory.getInstance().getOfflineReportDAO();
			OfflineReport eodReport = dao.getByReportClass("com.mfino.report.generalreports.EODSummaryReport") ;
			reportName = eodReport.getName();
			reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
			
			DateFormat df = getDateFormat();
			String startTime = df.format(new Date());
			log.info("processing "+reportName+" StartTime:"+startTime);
			
			
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
			seq=1;
			
			addTransactionsBySourceMessage(xlsReport,start,end,ServiceAndTransactionConstants.MESSAGE_TELLER_CLEARANCE);
			addTransactionsBySourceMessage(xlsReport,start,end,ServiceAndTransactionConstants.MESSAGE_CHARGE_SETTLEMENT);
			addTransactionsBySourceMessage(xlsReport,start,end,ServiceAndTransactionConstants.MESSAGE_SUB_BULK_TRANSFER);
			addTransactionsBySourceMessage(xlsReport,start,end,ServiceAndTransactionConstants.MESSAGE_AUTO_REVERSE);
			
			addSummary(xlsReport);
			
			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
			ReportMailUtil reportMailUtil = new ReportMailUtil();
			reportMailUtil.sendReport(eodReport, reportFile);
		} catch (Throwable t) {
			try {
				session.getTransaction().rollback();
			} catch (Throwable innerT) {
				log.error("Failed to rollback transaction", innerT);
			}
			log.error("Error in OfflineReport", t);
		}finally{
			if(session!=null){
				session.close();
			}
		}
	}
	
	private void addSummary(XLSReport xlsReport) {
		xlsReport.addContent("");
		xlsReport.addContent("Summary");
		xlsReport.addContent("Total Settlements Done,"+(seq-1));
		xlsReport.addContent("Total Transfered Amount,"+totalTransferedAmount.toString());
		
	}

	private void addTransactionsByUICategory(XLSReport xlsReport, Date start, Date end,Integer uiCat) {
		CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setCreateTimeGE(start);
		query.setCreateTimeLT(end);
		query.setUiCategory(uiCat);
		List<CommodityTransfer> ctList;
		try {
			ctList = ctDao.get(query);
			writeToFile(ctList, null, xlsReport);
		} catch (Exception e) {
			log.error("Exception:",e);
		}		
	}
	
	private void addTransactionsBySourceMessage(XLSReport xlsReport, Date start, Date end,String sourceMessage) {
		CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setCreateTimeGE(start);
		query.setCreateTimeLT(end);
		query.setSourceMessage(sourceMessage);
		List<CommodityTransfer> ctList;
		try {
			ctList = ctDao.get(query);
			writeToFile(ctList, null, xlsReport);
		} catch (Exception e) {
			log.error("Exception:",e);
		}
		
		
	}


	@SuppressWarnings("unchecked")
	private void writeToFile(List<CommodityTransfer> ctList, List<PendingCommodityTransfer> pctList, XLSReport xlsReport) throws IOException {		

		List<CRCommodityTransfer> crctList = (List<CRCommodityTransfer>) (ctList!=null?ctList:pctList);
		for(CRCommodityTransfer ct:crctList){
			DateFormat df = getDateFormat();
			String date=df.format(ct.getCreateTime());
			String formatStr = getFormatString(NUM_COLUMNS); 
			String destmdn = ct.getDestMDN();
			BigDecimal amount =ct.getAmount();
			BigDecimal charge =ct.getCharges();
		/*	BigDecimal sourceOpeningBalance =ct.getSourcePocketBalance()!=null?ct.getSourcePocketBalance():BigDecimal.ZERO;
			BigDecimal sourceClosingBalance = CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())?sourceOpeningBalance.subtract(amount.add(charge)):sourceOpeningBalance;
			BigDecimal destOpeningBalance =ct.getDestPocketBalance()!=null?ct.getDestPocketBalance():BigDecimal.ZERO;
			BigDecimal destClosingBalance = CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())?destOpeningBalance.add(amount):destOpeningBalance;
			if(!CmFinoFIX.PocketType_SVA.equals(ct.getSourcePocketType())){
				sourceOpeningBalance = BigDecimal.ZERO;
				sourceClosingBalance = BigDecimal.ZERO;
			}
			if(!CmFinoFIX.PocketType_SVA.equals(ct.getDestPocketType())){
				destOpeningBalance = BigDecimal.ZERO;
				destClosingBalance = BigDecimal.ZERO;
			}*/
			String transactionType = ct.getSourceMessage();
			if(transactionType==null || transactionType.isEmpty())
			{
				transactionType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, CmFinoFIX.Language_English, ct.getUICategory());
			}
			String status = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionsTransferStatus, CmFinoFIX.Language_English, ct.getTransferStatus());
			if(ct instanceof PendingCommodityTransfer){
				status = CmFinoFIX.TransferStateValue_Pending;
			}
			if(ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)){
				totalTransferedAmount = totalTransferedAmount.add(ct.getAmount());
			}
			String bankStan = ct.getBankSystemTraceAuditNumber();
			if(bankStan == null){
				bankStan = "";
			}
			String rowContent = String.format(formatStr, 
					seq,
					ct.getStartTime(),
					ct.getEndTime(),
					ct.getSourceMDN(),
					destmdn!=null?destmdn:"",
							amount,
							charge,
							transactionType!=null?transactionType:"",
									status!=null?status:"",
											ct.getID(),
					(ct.getEndTime().getTime()-ct.getStartTime().getTime())/1000,
					bankStan);
			xlsReport.addRowContent(rowContent);
			seq++;
		}

	}


	@Override
	public boolean hasMultipleReports(){
		return false;
	}

	@Override
	public String getFileName() {
		return reportFile.getName();
	}

	@Override
	public void setReportName(String reportName) {
		this.reportName ="EODSummaryReport";
	}

	
	
	public void getTellerEmoneyClearanceTransactions(){
		
	}


	@Override
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		run();
		return null;
	}

	@Override
	protected File run(Date start, Date end, ReportBaseData data) {
		// TODO Auto-generated method stub
		run();
		return null;
	}

}

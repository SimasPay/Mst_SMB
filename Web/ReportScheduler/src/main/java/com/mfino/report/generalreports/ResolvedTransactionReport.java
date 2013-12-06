package com.mfino.report.generalreports;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.util.ReportUtil;

public class ResolvedTransactionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 11;
	private String HEADER_ROW = "# ,UserName , SubscriberMdn , RefID, TransactionType, TransactionAmount, Charge, TransactionTime , ResolvedTime, ResolvedAs, BankStan";
	private CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
	private File report;
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;	
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		report =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		String formatStr = getFormatString(NUM_COLUMNS);
		DateFormat df = getDateFormat();
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" startTime"+startTime);
		try{
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(report)));
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
		
		 CommodityTransferQuery query = new CommodityTransferQuery();
         query.setHasCSRAction(true);
         query.setCreateTimeGE(start);
         query.setCreateTimeLT(end);
         List<CommodityTransfer> results = commodityTransferDAO.get(query);
         
		int seq = 1;
		if(results!=null){
		for(CommodityTransfer ct:results){
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
					ct.getCSRUserName(),					
					ct.getSourceMDN(),
					ct.getID(),
					transactionType,
					ct.getAmount(),
					ct.getCharges(),
					df.format(ct.getStartTime()),
					df.format(ct.getCSRActionTime()),
					enumTextService.getEnumTextValue(CmFinoFIX.TagID_CSRAction, CmFinoFIX.Language_English,ct.getCSRAction()),
					bankStan);
			xlsReport.addRowContent(rowContent);  
			seq++;
			}
	
		}
		xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+":",e);
		} 
		log.info("Processing "+reportName+" startTime"+startTime + " EndTime"+df.format(new Date()));
		return report;		
	}
	
		
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return report.getName();
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

	
}

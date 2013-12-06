package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.util.ReportUtil;

public class PendingTransactionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 11;
	private static final String HEADER_ROW = "# ,TransactionID , MDN ,WalletType, Amount inClearing"
		+ ", Balance Before, Balance After, Transaction Date, Status, TransactionType, BankStan";
	private File reportFile;
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;	
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String startTime =df.format(new Date());
		log.info("processing "+reportName+" StartTime:"+startTime);
		String formatStr = getFormatString(NUM_COLUMNS);
		
		try {
			if(data==null){
				data = new ReportBaseData();
				data.intializeStaticData();
	        	data.getPendingCommodityTransactions(start, end);
			}
		
			
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
			
			int seq = 1;
			List<PendingCommodityTransfer> pendingTransactions = data.getPctList();
			if(pendingTransactions!=null){
			for(PendingCommodityTransfer pct:pendingTransactions){
				BigDecimal amounInClearing = pct.getAmount().add(pct.getCharges());
				BigDecimal sourceOpeningBalance =pct.getSourcePocketBalance()!=null?pct.getSourcePocketBalance():BigDecimal.ZERO;
				BigDecimal sourceClosingBalance =sourceOpeningBalance.subtract(amounInClearing);
				if(!CmFinoFIX.PocketType_SVA.equals(pct.getSourcePocketType())){
					sourceOpeningBalance = BigDecimal.ZERO;
					sourceClosingBalance = BigDecimal.ZERO;
				}
				String bankStan = pct.getBankSystemTraceAuditNumber();
				if(bankStan == null){
					bankStan = "";
				}
				String transactionType = pct.getSourceMessage();
				if(transactionType==null || transactionType.isEmpty())
				{
					transactionType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, CmFinoFIX.Language_English, pct.getUICategory());
				}
				String rowContent = String.format(formatStr, 
						seq,
						pct.getID(),
						pct.getSourceMDN(),
						enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, CmFinoFIX.Language_English, pct.getSourcePocketType()),
						amounInClearing,
						sourceOpeningBalance,
						sourceClosingBalance, 
						df.format(pct.getCreateTime()),
						enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferState, CmFinoFIX.Language_English, CmFinoFIX.TransferState_Pending),
						transactionType,
						bankStan);
				xlsReport.addRowContent(rowContent);  			
				seq++;
			}
			}
			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
		} catch (Exception e) {
			log.error("Error in "+reportName+":", e);
		}
		log.info("processing "+reportName+" StartTime:"+startTime+" EndTime:"+ df.format(new Date()));
		return reportFile;
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}




	@Override
	public String getFileName() {
		return reportFile.getName();
	}
	
}

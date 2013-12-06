package com.mfino.report.generalreports;

import java.io.File;
import java.io.IOException;
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

import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.util.ReportUtil;

public class FundMovementReport extends OfflineReportBase {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 14;
	private String HEADER_ROW = "# ," +
			"Date," +
			/*"Access medium ," +*/
			"Source MDN," +
			"Destination MDN," +
			"Amount," +
			"Total Service charge," +
			"Source Balance Before," +
			"Source Balance After," +
			"Destination Balance Before," +
			"Destination Balance After," +
			"Transaction type," +
			"Status," +
			"Reference ID," +
			"Bank STAN";
	private File reportFile;
	DateFormat df;
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;


	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData basedata) {

		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		df = getDateFormat();
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+ startTime);
		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			ReportBaseData data = basedata;
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
				data.getPendingCommodityTransactions(start, end);
			} 
			//writer.println(HEADER_ROW);
			List<CommodityTransfer> ctList = data.getCtList();
			List<PendingCommodityTransfer> pctList = data.getPctList();
			int seq=1;
			
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
			
			if(ctList!=null){	
				seq = writeToFile(ctList,null,seq,xlsReport);
			}
			if(pctList!=null){					
				seq = writeToFile(null,pctList,seq,xlsReport);
			}
			//writer.close();
			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
			
		} catch (Exception e) {
			log.error("Error in "+reportName+":", e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+"  EndTime"+df.format(new Date()));
		return reportFile;
	}

	@SuppressWarnings("unchecked")
	private int writeToFile(List<CommodityTransfer> ctList, List<PendingCommodityTransfer> pctList,int seq, XLSReport xlsReport) throws IOException {		

		List<CRCommodityTransfer> crctList = (List<CRCommodityTransfer>) (ctList!=null?ctList:pctList);
		for(CRCommodityTransfer ct:crctList){
			String date=df.format(ct.getCreateTime());
			String formatStr = getFormatString(NUM_COLUMNS); 
			String destmdn = ct.getDestMDN();
			BigDecimal amount =ct.getAmount();
			BigDecimal charge =ct.getCharges();
			BigDecimal sourceOpeningBalance =ct.getSourcePocketBalance()!=null?ct.getSourcePocketBalance():BigDecimal.ZERO;
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
			}
			String transactionType = ct.getSourceMessage();
			if(transactionType==null || transactionType.isEmpty())
			{
				transactionType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, CmFinoFIX.Language_English, ct.getUICategory());
			}
			
			String status = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionsTransferStatus, CmFinoFIX.Language_English, ct.getTransferStatus());
			if(ct instanceof PendingCommodityTransfer){
				status = CmFinoFIX.TransferStateValue_Pending;
			}
			String bankStan = ct.getBankSystemTraceAuditNumber();
			if(bankStan == null){
				bankStan = "";
			}
			String rowContent = String.format(formatStr, 
					seq,
					date,
					/*channel,*/
					ct.getSourceMDN(),
					destmdn!=null?destmdn:"",
							amount,
							charge,
							sourceOpeningBalance, 
							sourceClosingBalance,
							destOpeningBalance,
							destClosingBalance,
							transactionType!=null?transactionType:"",
									status!=null?status:"",
					ct.getID(),
					bankStan);
			xlsReport.addRowContent(rowContent);
			seq++;
		}

			
		return seq;
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
		return "FundMovementReport";
	}

	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

}

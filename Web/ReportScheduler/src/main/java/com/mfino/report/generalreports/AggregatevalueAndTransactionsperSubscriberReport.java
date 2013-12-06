package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.domain.CommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.ReportParametersService;
import com.mfino.util.ReportUtil;

public class AggregatevalueAndTransactionsperSubscriberReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 4;
	private File report;
	private String HEADER_ROW = "# ,SubscriberMdn , TransactionCount, TransactionAmount";
	private String reportName;


	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		report =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		String formatStr = getFormatString(NUM_COLUMNS);

		String startTime = getDateFormat().format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);

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


			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
			}
			if(data.getCtList()!=null){
				Map<String, BigDecimal[]> subTransactions = getTransactionsBySubscriber(data);
				Set<String> mdns = subTransactions.keySet();
				BigDecimal limit =ReportParametersService.getBigDecimal(ReportParameterKeys.SUCCESS_AMOUNT_LIMIT);

				int seq = 1;
				for(String mdn:mdns){
					BigDecimal[] counts = subTransactions.get(mdn);
					if(counts[1].compareTo(limit)>0)
					{
						String rowContent = String.format(formatStr, 
								seq,
								mdn,
								counts[0],
								counts[1]
								);
						xlsReport.addRowContent(rowContent); 
						seq++;


					}
				}
			}
			xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		}catch (Exception e) {
			 log.error("Error in "+reportName+" :",e);
		}
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+getDateFormat().format(new Date()));
		return report;		
	}


	
	private Map<String, BigDecimal[]> getTransactionsBySubscriber(ReportBaseData data) {
		List<CommodityTransfer> ctList = data.getCtList();


		Map<String, BigDecimal[]> subtransactions = new HashMap<String, BigDecimal[]>();
		for(CommodityTransfer ct:ctList){
			if(CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())){
				BigDecimal[] counts = {BigDecimal.ZERO,BigDecimal.ZERO};
				if(subtransactions.containsKey(ct.getSourceMDN())){
					counts = subtransactions.get(ct.getSourceMDN());
					counts[0] = counts[0].add(new BigDecimal(1));
					counts[1] =counts[1].add(ct.getAmount().add(ct.getCharges()));
				}else{
					counts[0] = new BigDecimal(1);
					counts[1] =ct.getAmount().add(ct.getCharges());
				}
				subtransactions.put(ct.getSourceMDN(), counts);
			}
		}
		return subtransactions;
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
		this.reportName=reportName;		
	}

}

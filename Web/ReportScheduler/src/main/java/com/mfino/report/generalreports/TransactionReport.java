package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.util.ReportUtil;

public class TransactionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 11;
	private static final String HEADER_ROW = "# ,Date ,Service Name , Total Transaction, Transaction Amount"
			+ ", No of Failed transaction, Failed Amount, No of Successful transaction, Success Amount, "+"No of pending transaction, Pending Amount";
	private File reportFile;
	private String reportName;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" Starttime: "+ startTime);
		try {
			if(data==null){
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
				data.getPendingCommodityTransactions(start, end);
				data.getServiceTransactionLogs(start, end);
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
			String st=df.format(start)+"to"+df.format(end);
			List<ServiceChargeTransactionLog> sctlogs =data.getServiceChargeTransactionLogs();
			if(!sctlogs.isEmpty()){			
				Map<Long, List<ServiceChargeTransactionLog>> serviceSCTLMap=getServiceSCTLMap(sctlogs);
				for(Service service:data.services){
					String serviceName=service.getDisplayName();
					List<ServiceChargeTransactionLog> servicesctl = serviceSCTLMap.get(service.getID());
					BigDecimal[] counts = getTransactionCountsAndAmounts(servicesctl);						
					String rowContent = String.format(formatStr, 
							seq,
							st,
							serviceName,
							servicesctl!=null?servicesctl.size():0,
									counts[0],
									counts[1].intValue(),
									counts[2],
									counts[3].intValue(),
									counts[4],
									counts[5].intValue(),
									counts[6]
							);
					xlsReport.addRowContent(rowContent); 
					seq++;
				}
			}
			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);			
		} catch (Exception e) {
			log.error("Error in "+reportName+" ::", e);
		} 
		log.info("Processing "+reportName+" Starttime: "+ startTime);
		return reportFile;
	}

	private BigDecimal[] getTransactionCountsAndAmounts(List<ServiceChargeTransactionLog> servicesctl) {

		int successCount = 0;
		int failCount = 0;
		int pendingCount = 0;
		BigDecimal pendingAmount = BigDecimal.ZERO;
		BigDecimal totalAmount=BigDecimal.ZERO;
		BigDecimal successAmount=BigDecimal.ZERO;		
		BigDecimal failedAmount=BigDecimal.ZERO;

		if(servicesctl!=null){
			for(ServiceChargeTransactionLog st: servicesctl){
				totalAmount = totalAmount.add(st.getTransactionAmount()).add(st.getCalculatedCharge());
				if(st.getStatus().equals(CmFinoFIX.SCTLStatus_Processing)){				
					pendingCount++;
					pendingAmount= pendingAmount.add(st.getTransactionAmount().add(st.getCalculatedCharge()));
					continue;
				}			
				if(st.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)
						||st.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started)
						||st.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)){
					successCount++;
					successAmount = successAmount.add(st.getTransactionAmount()).add(st.getCalculatedCharge());				
				}else if(st.getStatus().equals(CmFinoFIX.SCTLStatus_Failed)){
					failCount++;
					failedAmount = failedAmount.add(st.getTransactionAmount()).add(st.getCalculatedCharge());
				}
			}
		}

		BigDecimal[] counts ={totalAmount,new BigDecimal(failCount),failedAmount,new BigDecimal(successCount),successAmount,new BigDecimal(pendingCount),pendingAmount};
		return counts;
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
		return reportFile.getName();
	}

	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}


}

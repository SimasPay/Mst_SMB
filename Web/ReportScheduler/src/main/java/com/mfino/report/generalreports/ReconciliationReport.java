package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.util.ReportUtil;

public class ReconciliationReport extends OfflineReportBase {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 9;
	private String HEADER_ROW = "# ,Date ,Service name ,Transaction Type ,Access medium, TotalTransaction"
			+ ", Distinct Channel MDN count, Total funds collected ";
	private List<ServiceChargeTransactionLog> sctlogs = null;
	private Map<Long, Map<Long, Map<Long, List<ServiceChargeTransactionLog>>>> sTASCTLMap = null;
	private ChannelCodeDAO channelDao = DAOFactory.getInstance().getChannelCodeDao();
	private File reportFile;
	private String reportName;
	
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData basedata) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);
		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			XLSReport xlsReport = new XLSReport(reportName, end);
			
			//adding logo
			try{
				xlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			
			xlsReport.addMergedRegion();
			
			xlsReport.addReportTitle(reportName);
		
		    
		    
			ReportBaseData data = basedata;
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
				data.getPendingCommodityTransactions(start, end);
				data.getServiceTransactionLogs(start, end);
			} else if (data.getServiceChargeTransactionLogs() == null) {
				data.getServiceTransactionLogs(start, end);
			}
			sctlogs = data.getServiceChargeTransactionLogs();
			HEADER_ROW=HEADER_ROW+ data.chargeTypesHeader;
			
			xlsReport.addHeaderRow(HEADER_ROW);
			
			if (!sctlogs.isEmpty()) {
				sTASCTLMap = getSTCSCTLMap(sctlogs);
				int seq = 1;
				
				Set<Long> serviceids = sTASCTLMap.keySet();
				for (Long serviceid : serviceids) {
					Map<Long, Map<Long, List<ServiceChargeTransactionLog>>> tSCTLMap = sTASCTLMap.get(serviceid);
					String serviceName = ReportBaseData.serviceMap.get(serviceid).getDisplayName();
					Set<Long> transactiontypeids = tSCTLMap.keySet();
					for (Long tansactionTypeid : transactiontypeids) {
						Map<Long, List<ServiceChargeTransactionLog>> acSCTLMap = tSCTLMap.get(tansactionTypeid);
						Set<Long> channelIDs = acSCTLMap.keySet();
						String transactionTypeName = ReportBaseData.transactiontypeMap.get(tansactionTypeid).getDisplayName();
						for (Long channelid : channelIDs) {
							List<ServiceChargeTransactionLog> sctl = acSCTLMap.get(channelid);
							String channelName = channelDao.getById(channelid).getChannelName();
							int mdncount = getDistinctMDNCount(sctl);
							HashMap<Long, BigDecimal> chageTypeFunds = getChargeTypeFunds(data,sctl);
							String chargeFunds = getChargeTypeFundsString(data,chageTypeFunds);
							BigDecimal totalFunds = getTotalCollectedFunds(sctl);
							String st=df.format(start)+"to"+df.format(end);
							String rowContent = String.format(formatStr, 
									seq,
									st,
									serviceName,
									transactionTypeName,
									channelName,
									sctl.size(), 
									mdncount,
									totalFunds,
									chargeFunds);
							xlsReport.addRowContent(rowContent);  
							seq++;
						}
					}
				}
			}
			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);

		} catch (Exception e) {
			log.error("Error in "+reportName+":", e);
		} 
		log.info("Processing ReconciliationReport StartTime:"+startTime+" EndTime:"+ df.format(new Date()));
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
	public String getFileName() {
		return reportFile.getName();
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}
	
}

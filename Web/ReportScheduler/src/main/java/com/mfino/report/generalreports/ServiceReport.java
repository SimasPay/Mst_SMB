package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
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
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.util.ReportUtil;

public class ServiceReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 8;
	private String HEADER_ROW = "# ,Date ,Transaction Type ,Access medium, TotalTransaction"
		+ ", Distinct Channel MDN count, Total funds collected ";
	private List<ServiceChargeTransactionLog> sctlogs = null;
	private Map<Long, Map<Long, Map<Long, List<ServiceChargeTransactionLog>>>> sTASCTLMap = null;
	private ChannelCodeDAO channelDao = DAOFactory.getInstance().getChannelCodeDao();
	private String reportName;
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end, ReportBaseData basedata) {
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		List<File> serviceReports = new ArrayList<File>();
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);
		try {
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
			
			if (!sctlogs.isEmpty()) {
				sTASCTLMap = getSTCSCTLMap(sctlogs);
				int seq = 1;
				
				for (Service service : data.services) {
					Map<Long, Map<Long, List<ServiceChargeTransactionLog>>> tSCTLMap = sTASCTLMap.get(service.getID());
					String serviceName = service.getDisplayName();
					File reportFile = ReportUtil.getReportFilePath(serviceName+"_ServiceReport",end,ReportUtil.EXCEL_EXTENTION);
					//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
					//writer.println(HEADER_ROW);
					XLSReport xlsReport = new XLSReport(serviceName+"_ServiceReport", end);
					
					//adding logo
					try{
						xlsReport.addLogo();
					}catch (Exception e) {
						log.error("Failed to load logo",e);
					}
					
					xlsReport.addMergedRegion();
					
					xlsReport.addReportTitle(serviceName+"_ServiceReport");
					xlsReport.addHeaderRow(HEADER_ROW);
					
					if(tSCTLMap!=null){
						
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
//									serviceName,
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
					xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
					serviceReports.add(reportFile);
				}
			}
		} catch (Exception e) {
			log.error("Error in "+reportName+"", e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime"+df.format(new Date()));
		return serviceReports;
	}
	
	@Override
	  public boolean hasMultipleReports() {
	        return true;
	    }

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

	
	
}

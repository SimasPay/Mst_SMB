package com.mfino.report.generalreports;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.util.ReportUtil;

public class PartnerClassificationReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int PARTNER_NUM_COLUMNS = 5;
	private String PARTNER_HEADER_ROW = "# , City, Total Number, Tellers, Merchants";
	private File reportFile;
	private String reportName;
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, Map<String,Integer[]> countMap) {
		DateFormat df = getDateFormat();
		String agentstr = getFormatString(PARTNER_NUM_COLUMNS);
		String startTime = df.format(new Date());
		
		log.info("Processing PartnerClassification report StartTime:"+startTime);
		try {
			if(StringUtils.isBlank(reportName)){
				OfflineReportDAO offlineReportDAO =DAOFactory.getInstance().getOfflineReportDAO();
				reportName =  offlineReportDAO.getByReportClass("com.mfino.report.generalreports.PartnerClassificationReport").getName();
			}
			reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
			XLSReport agentXlsReport = new XLSReport(reportName, end);
			//adding logo
			try{
				agentXlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			
			agentXlsReport.addMergedRegion();
			
			agentXlsReport.addReportTitle(reportName);
			agentXlsReport.addHeaderRow(PARTNER_HEADER_ROW);
			if(countMap==null||countMap.isEmpty())	{	
				SubscriberClassificationReport subscriberClassificationReport = new SubscriberClassificationReport();
				countMap=subscriberClassificationReport.getCounts();
			}
			int seq = 1;
			for(String city:countMap.keySet()){
				Integer[] counts = countMap.get(city);
					String agentRowContent = String.format(agentstr, 
						seq,
						city.replace(",", " "),
						counts[8],
						counts[9],
						counts[10]);
				agentXlsReport.addRowContent(agentRowContent);
					seq++;
				}		
			agentXlsReport.writeToFileStream(reportFile,PARTNER_HEADER_ROW , reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+":",e);
		}
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
		
		return reportFile;
	}

	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end,
			ReportBaseData data) {
		Map<String,Integer[]> countMap = null;
		return run(start, end, countMap);
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
		return false;
	}


	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}

}

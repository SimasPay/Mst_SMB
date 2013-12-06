package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ReportUtil;

public class AirtimeSummaryReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private String AIRTIMESUMMARY_HEADER_ROW = "# , MDN, TransactionType, count, TotalAmount";
	private int AIRTIMESUMMARY_NUM_COLUMNS = AIRTIMESUMMARY_HEADER_ROW.split(",").length;
	private File reportFile;
	private String reportName;

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, Map<String,Integer[]> countMap) {
		DateFormat df = getDateFormat();
		String agentstr = getFormatString(AIRTIMESUMMARY_NUM_COLUMNS);
		if(end == null){
			Calendar cal = new GregorianCalendar(ConfigurationUtil.getLocalTimeZone());
			cal.setTime(new Date());
			end = DateUtils.truncate(cal, Calendar.DATE).getTime();
			start = DateUtils.addDays(end, -1);
		}else if(start == null){
			start = DateUtils.addDays(end, -1);
		}
		String startTime = df.format(start);
		String endTime = df.format(end);

		log.info("Processing AirtimeSummary report StartTime:"+startTime+"  and EndTime:"+endTime);
		try {
			if(StringUtils.isBlank(reportName)){
				OfflineReportDAO offlineReportDAO =DAOFactory.getInstance().getOfflineReportDAO();
				reportName =  offlineReportDAO.getByReportClass("com.mfino.report.generalreports.AirtimeSummaryReport").getName();
			}
			reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
			XLSReport airtimeXlsReport = new XLSReport(reportName, end);
			//adding logo
			try{
				airtimeXlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			airtimeXlsReport.addMergedRegion();
			airtimeXlsReport.addReportTitle(reportName);
			airtimeXlsReport.addHeaderRow(AIRTIMESUMMARY_HEADER_ROW);
			
			String hqlQuery = "select sctl.SourceMDN, "
			+ "tt.TransactionName, "
			+ "count(sctl.ID), "
			+ "sum(sctl.TransactionAmount) "
			+ "from ServiceChargeTransactionLog as sctl,TransactionType tt,EnumText et "
			+ "where "
			+ "tt.TransactionName = 'AirtimePurchase' "
			+ "and sctl.TransactionTypeID = tt.ID "
			+ "and (sctl.Status = 2 or sctl.Status = 3 or sctl.Status = 4) "
			+ "and et.TagID = 6089 and sctl.Status = et.EnumCode "		
			+ "and (sctl.CreateTime >= :startTime  and sctl.CreateTime < :endTime ) "
			+ "group by sctl.SourceMDN,tt.TransactionName ";
			
			HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
			Session session = hibernateService.getSessionFactory().openSession();
			Query query = session.createQuery(hqlQuery);
			query.setDate("startTime", start);
			query.setDate("endTime", end);
			List subList = query.list();
			session.close();
			log.info("Total records"+subList.size());
			int seq = 1;
			for(int i = 0; i < subList.size(); i++) {		
				int j = -1;
				Object[] obj = (Object[]) subList.get(i);
				String agentRowContent = String.format(agentstr, 
						seq,
						obj[++j] != null ? obj[j].toString() : "",
						obj[++j] != null ? obj[j].toString() : "",
						obj[++j] != null ? obj[j].toString() : "",
						obj[++j] != null ? ((BigDecimal)obj[j]).setScale(2, RoundingMode.HALF_DOWN) : "");
				airtimeXlsReport.addRowContent(agentRowContent);
				seq++;
			}
			airtimeXlsReport.writeToFileStream(reportFile, AIRTIMESUMMARY_HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+":",e);
		}
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));

		return reportFile;
	}


	@Override
	public File run(Date start, Date end,
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

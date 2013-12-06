package com.mfino.billpay.startimes.reconciliation;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.util.ConfigurationUtil;

public class ReconciliationReportService {
	
	private Log log = LogFactory.getLog(this.getClass());
	private ReconciliationReport reconciliationReport;
	private StarFTPService starFTPService;
	
	public ReconciliationReport getReconciliationReport() {
		return reconciliationReport;
	}
	public void setReconciliationReport(ReconciliationReport reconciliationReport) {
		this.reconciliationReport = reconciliationReport;
	}
	public StarFTPService getStarFTPService() {
		return starFTPService;
	}
	public void setStarFTPService(StarFTPService starFTPService) {
		this.starFTPService = starFTPService;
	}
	
	public void generateAndSend(){
		log.info("ReconciliationReportService::generateAndSend():BEGIN");
		Calendar cal = new GregorianCalendar(ConfigurationUtil.getLocalTimeZone());
		cal.setTime(new Date());
		Date end = DateUtils.truncate(cal, Calendar.DATE).getTime();
		Date start = DateUtils.addDays(end, -1);
		File report = reconciliationReport.generate(start,end);
		if(report == null)
			log.info("ReconciliationReport generation failed");
		else{
			if(!starFTPService.send(report))
				log.error("Sending File failed");
		}
		log.info("ReconciliationReportService::generateAndSend():END");
	}
}

package com.dimo.fuse.reports.scheduler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 *
 */
public class MonthlyReportScheduler extends ReportScheduler{

	private static Logger log = LoggerFactory.getLogger("MonthlyReportScheduler");
	
	protected void setZipDirs() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
		String dateString = sdf.format(endTime);
		zipFile = outputDirectory + File.separator + dateString ;
	}
			
	protected void initaliseTimes() throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
		String currTimeMillis = fmt.format(System.currentTimeMillis());
		Date currDateTime = fmt.parse(currTimeMillis);
		Calendar cal = Calendar.getInstance(); 
		
		cal.setTime(currDateTime);
		cal.add(Calendar.MONTH, -1);
		startTime = cal.getTime();
		
		cal.setTime(currDateTime);
		cal.add(Calendar.MILLISECOND, -1);
		endTime = cal.getTime();
		
		log.info("StartTime : "+startTime.toString());
		log.info("endTime : "+endTime.toString());		
	}
	
	protected String getOutputDirectory()
	{
		SimpleDateFormat fmt = new SimpleDateFormat("MMM-yyyy");
		return ReportSchedulerProperties.getScheduledReportsOutputDir() + File.separator + fmt.format(endTime);
	}
	
	protected String getQuery()
	{
		return "SELECT ReportClass, IsMonthly from offline_report";
	}
	
}

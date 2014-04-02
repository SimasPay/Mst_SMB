package com.dimo.fuse.reports.scheduler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
		String monthEnd = fmt.format(System.currentTimeMillis());
		endTime = fmt.parse(monthEnd);
		Calendar cal = Calendar.getInstance();
		cal.setTime(endTime);
		cal.add(Calendar.MONTH, -1);
		startTime = cal.getTime();
		log.info("StartTime : "+startTime.toString());
		log.info("endTime : "+endTime.toString());		
		
	}
	
	protected String getOutputDirectory()
	{
		SimpleDateFormat fmt = new SimpleDateFormat("MMM-yyyy");
		return ReportSchedulerProperties.getOtherReportsOutputDir() + File.separator + fmt.format(endTime);
	}
	
	protected String getQuery()
	{
		return "SELECT ReportClass, IsMonthly from offline_report";
	}
	
}

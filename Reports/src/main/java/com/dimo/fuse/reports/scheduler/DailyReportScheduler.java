package com.dimo.fuse.reports.scheduler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 *
 */
public class DailyReportScheduler extends ReportScheduler{

	private static Logger log = LoggerFactory.getLogger("DailyReportScheduler");
	private int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
	
	protected void setZipDirs() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		zipFile = outputDirectory + File.separator + date ;
	}
	
	protected void initaliseTimes() throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		String yesterdayEnd = fmt.format(System.currentTimeMillis());
		endTime = fmt.parse(yesterdayEnd);
		String yesterdayStart = fmt.format(endTime.getTime() - MILLIS_IN_DAY);
		startTime = fmt.parse(yesterdayStart);
		log.info("StartTime : "+startTime.toString());
		log.info("endTime : "+endTime.toString());		
	}
	
	protected String getOutputDirectory()
	{
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		return ReportSchedulerProperties.getScheduledReportsOutputDir() + File.separator + fmt.format(endTime);
	}
	
	protected String getQuery()
	{
		return "SELECT ReportClass, IsDaily from offline_report";
	}
	
}

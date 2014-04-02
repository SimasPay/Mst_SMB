package com.dimo.fuse.reports.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 *
 */
public class ReportSchedulerProperties {

	private static Logger log = LoggerFactory.getLogger("DailyReportGenerator");
	
	private static String dailyReportsInputDir = null;
	private static String dailyReportsOutputDir = null;
	private static String otherReportsOutputDir = null;
	private static Properties schedulerProperties = new Properties();
	private static String emailRecipients = "";
	
	
	static{
		loadProperties();
	}
	
	public static void loadProperties() /*throws IOException*/ {
		log.info("Loading Report Scheduler properties");
		InputStream ins = ReportSchedulerProperties.class.getClassLoader().getResourceAsStream("/reportScheduler.properties");
		try {
			schedulerProperties.load(ins);
			ins.close();
			dailyReportsInputDir = schedulerProperties.getProperty("dailyReportsInputDir");
			dailyReportsOutputDir = schedulerProperties.getProperty("dailyReportsOutputDir");
			otherReportsOutputDir = schedulerProperties.getProperty("otherReportsOutputDir");
			emailRecipients = schedulerProperties.getProperty("email.recipient.list");
			log.info("dailyReportsInputDir :" + dailyReportsInputDir);
			log.info("dailyReportsOutputDir :" + dailyReportsOutputDir);
			log.info("emailRecipients :" + emailRecipients);
		} catch (IOException e) {
			log.error("Error loading report scheduler properties", e);
		}		
	}

	public static String getDailyReportsInputDir() {
		return dailyReportsInputDir;
	}

	public static String getDailyReportsOutputDir() {
		return dailyReportsOutputDir;
	}

	public static String getEmailRecipients() {
		return emailRecipients;
	}

	public static String getOtherReportsOutputDir() {
		return otherReportsOutputDir;
	}
	
}

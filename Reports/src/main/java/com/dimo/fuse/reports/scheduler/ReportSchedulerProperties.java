package com.dimo.fuse.reports.scheduler;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.util.PropertiesFileReaderTool;

/**
 * 
 * @author Amar
 *
 */
public class ReportSchedulerProperties {

	private static Logger log = LoggerFactory.getLogger("ReportSchedulerProperties");
	
	private static String reportsInputDir = null;
	private static String scheduledReportsOutputDir = null;
	private static String otherReportsOutputDir = null;
	private static Properties schedulerProperties = new Properties();
	private static String emailRecipients = "";
	private static String reportFooterText = "";
	private static String _propertiesFileName = "mfino.properties";
	
	
	static{
		loadProperties();
	}	
	
	public static void loadProperties() {
		log.info("Loading Report Scheduler properties");
		schedulerProperties = PropertiesFileReaderTool.readProperties(_propertiesFileName);
		reportsInputDir = schedulerProperties.getProperty("reportsInputDir");
		scheduledReportsOutputDir = schedulerProperties.getProperty("scheduledReportsOutputDir");
		otherReportsOutputDir = schedulerProperties.getProperty("mfino.report.directory");
		emailRecipients = schedulerProperties.getProperty("email.recipient.list");
		reportFooterText = schedulerProperties.getProperty("mfino.report.footer");
		log.info("reportsInputDir :" + reportsInputDir);
		log.info("scheduledReportsOutputDir :" + scheduledReportsOutputDir);
		log.info("otherReportsOutputDir :" + otherReportsOutputDir);
		log.info("emailRecipients :" + emailRecipients);
	}

	public static String getReportsInputDir() {
		return reportsInputDir;
	}

	public static String getScheduledReportsOutputDir() {
		return scheduledReportsOutputDir;
	}

	public static String getEmailRecipients() {
		return emailRecipients;
	}

	public static String getOtherReportsOutputDir() {
		return otherReportsOutputDir;
	}
	
	public static String getProperty(String propertyName) {
		return schedulerProperties.getProperty(propertyName);
	}

	public static String getReportFooterText() {
		return reportFooterText;
	}
}

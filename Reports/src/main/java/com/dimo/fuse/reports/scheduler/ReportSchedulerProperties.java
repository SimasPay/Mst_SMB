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
	private static String glOmnibusAccount = "";
	private static String glKycAccount = "";
	private static String glNonKycAccount = "";
	private static String glDebitCurrency = "IDR";
	private static String glCreditCurrency = "IDR";
	private static String glBranchCode = "ID0010121";
	
	private static String ftpHost = "";
	private static String ftpPort = "";
	private static String ftpUsername = "";
	private static String ftpPassword = "";
	private static String glReportRemoteDir = "";
	
	
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
		glOmnibusAccount = schedulerProperties.getProperty("gl.report.omnibus.account");
		glNonKycAccount = schedulerProperties.getProperty("gl.report.nonkyc.account");
		glKycAccount = schedulerProperties.getProperty("gl.report.kyc.account");
		glDebitCurrency = schedulerProperties.getProperty("gl.report.debit.currency");
		glCreditCurrency = schedulerProperties.getProperty("gl.report.credit.currency");
		glBranchCode = schedulerProperties.getProperty("gl.report.branch.code");

		ftpHost = schedulerProperties.getProperty("ftp.report.host");
		ftpPort = schedulerProperties.getProperty("ftp.report.port");
		ftpUsername = schedulerProperties.getProperty("ftp.report.username");
		ftpPassword = schedulerProperties.getProperty("ftp.report.password");
		glReportRemoteDir = schedulerProperties.getProperty("ftp.report.remote.dir.path");
		
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

	public static String getGlOmnibusAccount() {
		return glOmnibusAccount;
	}

	public static String getGlKycAccount() {
		return glKycAccount;
	}

	public static String getGlNonKycAccount() {
		return glNonKycAccount;
	}

	public static String getGlDebitCurrency() {
		return glDebitCurrency;
	}

	public static String getGlCreditCurrency() {
		return glCreditCurrency;
	}

	public static String getGlBranchCode() {
		return glBranchCode;
	}

	public static String getFtpHost() {
		return ftpHost;
	}

	public static String getFtpUsername() {
		return ftpUsername;
	}

	public static String getFtpPassword() {
		return ftpPassword;
	}

	public static String getGlReportRemoteDir() {
		return glReportRemoteDir;
	}

	public static String getFtpPort() {
		return ftpPort;
	}
}

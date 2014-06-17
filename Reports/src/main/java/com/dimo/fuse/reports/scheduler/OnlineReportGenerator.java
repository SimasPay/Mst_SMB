package com.dimo.fuse.reports.scheduler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.ReportParameters;
import com.dimo.fuse.reports.ReportTool;

/**
 * 
 * @author Amar
 *
 */
public class OnlineReportGenerator {

	private static Logger log = LoggerFactory.getLogger("OnlineReportGenerator");	
	private String outputDirectory;	

	private boolean createDir(String dirPath) {
		return new File(dirPath).mkdirs();
	}
	
	private void sendMail(String emailRecipients,String subject, String message, String attachmentFileName){
		log.info("sending mail to " + emailRecipients);
		try{
			String[] emailRecipientsList = emailRecipients.split(",");
			File attachmentFile = new File(attachmentFileName);
			MailUtil mailUtil = new MailUtil();
			for(int i=0; i< emailRecipientsList.length; i++){
				if(mailUtil.isValidEmailAddress(emailRecipientsList[i])){
					mailUtil.sendMail(emailRecipientsList[i], "", subject, message, attachmentFile);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}	
	
	
	public void generateReportsOnDemand(String reportName, ReportParameters reportParams) throws IOException, ParseException{
		log.info("Report generation on demand has stared");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String startDate = fmt.format(reportParams.getStartTime());
		String endDate = fmt.format(reportParams.getEndTime());
		outputDirectory = ReportSchedulerProperties.getOtherReportsOutputDir() + File.separator + startDate + "-"  + endDate;
		if (!new File(outputDirectory).exists()) {
			createDir(outputDirectory);
		}
		reportParams.setUserName("System");
		reportParams.setDestinationFolder(outputDirectory);
		ReportTool.generateReports(ReportSchedulerProperties.getReportsInputDir() + File.separator + reportName + ".json", reportParams);
		log.info("generateReport function finished");
		
		if(ReportSchedulerProperties.getEmailRecipients()!=null)
			sendMail(ReportSchedulerProperties.getEmailRecipients(), reportName, "", outputDirectory + File.separator + reportName + "_"+ startDate +"-" + endDate + ".pdf");
		
	}	

}

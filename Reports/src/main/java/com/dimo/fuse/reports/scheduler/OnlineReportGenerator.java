package com.dimo.fuse.reports.scheduler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
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
//	static String[] REPORT_FILE_EXTENSIONS = { "pdf", "xls", "csv" };

	private boolean createDir(String dirPath) {
		return new File(dirPath).mkdirs();
	}
	
//	private void sendMail(String emailRecipients,String subject, String message, List<File> attachments){
//		log.info("sending mail to " + emailRecipients);
//		try{
//			String[] emailRecipientsList = emailRecipients.split(",");			
//			MailUtil mailUtil = new MailUtil();
//			for(int i=0; i< emailRecipientsList.length; i++){
//				if(mailUtil.isValidEmailAddress(emailRecipientsList[i])){
//					mailUtil.sendMail(emailRecipientsList[i], "", subject, message, attachments);
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}		
//	}	
	
	
	public void generateReportsOnDemand(String reportName, ReportParameters reportParams) throws IOException, ParseException{
		log.info("Report generation on demand has stared");
		SimpleDateFormat fmt = new SimpleDateFormat(ReportSchedulerProperties.getProperty("dateFormatInReportFileNames"));
		String startDate = fmt.format(reportParams.getStartTime());
		String endDate = fmt.format(reportParams.getEndTime());
		outputDirectory = ReportSchedulerProperties.getOtherReportsOutputDir() + File.separator + startDate + "-"  + endDate;
		if (!new File(outputDirectory).exists()) {
			createDir(outputDirectory);
		}
		reportParams.setUserName(StringUtils.isNotBlank(reportParams.getUserName()) ? reportParams.getUserName() : "System");
		reportParams.setDestinationFolder(outputDirectory);
		ReportTool.generateReports(ReportSchedulerProperties.getReportsInputDir() + File.separator + reportName + ".json", reportParams);
		log.info("generateReport function finished");
//		String fileNameWithoutExtension = outputDirectory + File.separator + reportName + "_"+ startDate +"-" + endDate;
//		if(StringUtils.isNotBlank(reportParams.getTransactionTypeId())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getTransactionTypeId();
//		}
//		if(StringUtils.isNotBlank(reportParams.getTransactionStatusId())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getTransactionStatusId();
//		}
//		if(StringUtils.isNotBlank(reportParams.getSourceMdn())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getSourceMdn();
//		}
//		if(StringUtils.isNotBlank(reportParams.getDestMdn())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getDestMdn();
//		}
//		if(StringUtils.isNotBlank(reportParams.getBillerCode())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getBillerCode();
//		}
//		if(StringUtils.isNotBlank(reportParams.getBankRRN())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getBankRRN();
//		}
//		if(StringUtils.isNotBlank(reportParams.getSourcePartnerCode())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getSourcePartnerCode();
//		}
//		if(StringUtils.isNotBlank(reportParams.getDestPartnerCode())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getDestPartnerCode();
//		}
//		if(StringUtils.isNotBlank(reportParams.getChannelName())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getChannelName();
//		}
//		if(StringUtils.isNotBlank(reportParams.getReferenceNo())){
//			fileNameWithoutExtension = fileNameWithoutExtension+"-"+reportParams.getReferenceNo();
//		}
//		
//		if(ReportSchedulerProperties.getEmailRecipients()!=null){
//			List<File> attachments = new ArrayList<File>();
//			for(String extension : REPORT_FILE_EXTENSIONS){
//				attachments.add(new File(fileNameWithoutExtension + "." + extension));
//			}
//			String emailRecepients = ReportSchedulerProperties.getEmailRecipients()+","+reportParams.getEmail();
//			sendMail(emailRecepients, reportName, "", attachments);
//		}		
	}	

}

package com.dimo.fuse.reports.scheduler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.ReportParameters;
import com.dimo.fuse.reports.ReportTool;
import com.dimo.fuse.reports.db.QueryExecutor;

/**
 * 
 * @author Amar
 *
 */
public abstract class ReportScheduler {

	private static Logger log = LoggerFactory.getLogger("ReportScheduler");
	
	protected String[] inputFileNames;
	protected Date startTime;
	protected Date endTime;
	protected String zipFile = "";
	protected String outputDirectory;
	private Map<String, Boolean> ReportSchedules = new HashMap<String, Boolean>();
	
	private boolean isValidPath(File dir) {
		if (!dir.exists()) {
			log.info("Daily Reports Path Doesn't Exist and the path is : " + dir.getPath());
			return false;
		}
		if (!dir.isDirectory()) {
			log.info("Daily Reports Path is not a Directory and the path is : " + dir.getPath());
			return false;
		}
		return true;
	}

	protected boolean createDir(String dirPath) {
		return new File(dirPath).mkdirs();
	}
	
	protected abstract void setZipDirs();
	
	protected String[] getFilesListFromDirectory(String directory,final String extension){
		File dir = new File(directory);
		if (!isValidPath(dir)) {
			return null;
		}
		String[] fileList = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(extension)) {
					return true;
				}
				return false;
			}
		});
		return fileList;
	}
	
	protected abstract String getQuery();
	
	
	
	protected String zipFiles(String zipInputDir,String zipOutputDir,String extension) {
		log.debug("zipFiles function Started");
		log.info("zipFiles Input Directory Path: " + zipInputDir);
		log.info("zipFiles Output Directory Path: " + zipOutputDir);
		try {
			File inFolder = new File(zipInputDir);
			File zipFile = new File(zipOutputDir + ".zip");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
			String files[] = getFilesListFromDirectory(zipInputDir, extension);
			BufferedInputStream in = null;
			byte[] data = new byte[1000];
			
			for (int i = 0; i < files.length; i++) {
				log.info("Ziping file : "+ inFolder.getPath() + File.separator + files[i]);
				in = new BufferedInputStream(new FileInputStream(inFolder.getPath() + File.separator + files[i]), 1000);
				out.putNextEntry(new ZipEntry(files[i]));
				int count;
				while ((count = in.read(data, 0, 1000)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry();
				in.close();
				log.info("Successfully Zipped file : "+ inFolder.getPath() + File.separator + files[i]);
			}
			out.flush();
			out.close();
			log.debug("zipFiles function Finished");
			return zipFile.toString();
		} catch (Exception e) {
			log.error("Error occured while zipping files and the zip Input Directory is  "+zipInputDir+ " and the zip Output Directory is "+zipOutputDir, e);
		}
		return null;
	}
	
	public String[] FilterInputFilesBasedOnDBSchedulerTimings(String[] inputFileNames){
		readFromDB();
		List<String> inputFileList = new LinkedList<String>(Arrays.asList(inputFileNames));
		for(int i=inputFileList.size()-1; i>=0; i--){
			String inputFileName = inputFileList.get(i);
			String inputFileNameWithoutExtension = inputFileName.substring(0, inputFileName.indexOf(".json"));
			if(ReportSchedules.containsKey(inputFileNameWithoutExtension) && !ReportSchedules.get(inputFileNameWithoutExtension)){
				inputFileList.remove(inputFileName);
			}
		}
		return inputFileList.toArray(new String[0]);
	}
	
	public void generate() {
		log.debug("Scheduler Trigered, for the generation of Daily Reports has started");
		try{			
			inputFileNames = getFilesListFromDirectory(ReportSchedulerProperties.getDailyReportsInputDir(),".json");
			if(inputFileNames != null && inputFileNames.length > 0){
				inputFileNames = FilterInputFilesBasedOnDBSchedulerTimings(inputFileNames);
				initaliseTimes();
				generateReports();
				setZipDirs();

				if(ReportSchedulerProperties.getEmailRecipients()!=null){
					String pdfZipFile = zipFiles(outputDirectory,zipFile+"_pdf",".pdf");
					if(pdfZipFile != null){
						sendMail(ReportSchedulerProperties.getEmailRecipients(), "Scheduled PDF Reports", "Attached Scheduled Reports Zip File", pdfZipFile);
					}else{
						log.info("Unable to zip the pdf files from input directory "+outputDirectory+" and hence not sending mail");
					}
					
					String xlsZipFile = zipFiles(outputDirectory,zipFile+"_xls",".xls");
					if(xlsZipFile != null){
						sendMail(ReportSchedulerProperties.getEmailRecipients(), "Scheduled XLS Reports", "Attached Scheduled Reports Zip File", xlsZipFile);
					}else{
						log.info("Unable to zip the xls files from input directory "+outputDirectory+" and hence not sending mail");
					}
					
					String csvZipFile = zipFiles(outputDirectory,zipFile+"_csv",".csv");
					if(csvZipFile != null){
						sendMail(ReportSchedulerProperties.getEmailRecipients(), "Scheduled PDF Reports", "Attached Scheduled Reports Zip File", csvZipFile);
					}else{
						log.info("Unable to zip the cls files from input directory "+outputDirectory+" and hence not sending mail");
					}					
				}			
				
			}else{
				log.info("No Input *.json files exist to generate reports");
			}
		}catch(Exception e){
			log.error("Error ocurred while generating Daily reports, to know the exact error please check the stack trace",e);
		}
		log.debug("Scheduler Triggered for the generation of Daily Reports has completed");
	}
	
	public static void sendMail(String emailRecipients,String subject, String message, String attachmentFileName){
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
	
	protected abstract void initaliseTimes() throws ParseException;
		
	protected void generateReports() throws FileNotFoundException, UnsupportedEncodingException {
		log.debug("generateReports function started");
		
		outputDirectory = getOutputDirectory();
		if (!new File(outputDirectory).exists()) {
			createDir(outputDirectory);
		}
		for (int i = 0; i < inputFileNames.length; i++) {
			log.info("Report  started"+inputFileNames[i]);
			ReportParameters reportparams = new ReportParameters();
			reportparams.setStartTime(startTime);
			reportparams.setEndTime(endTime);
			reportparams.setUserName("System");
			reportparams.setScheduledReport(true);
			reportparams.setDestinationFolder(outputDirectory);
			ReportTool.generateReports(ReportSchedulerProperties.getDailyReportsInputDir() + File.separator + inputFileNames[i], reportparams);
		}
		log.debug("generateReports function finished");
	}
	
	private void readFromDB()
	{
		QueryExecutor qe = null;
		try {
			qe = new QueryExecutor();
			ResultSet rs = qe.getResultSet(getQuery());
			while (rs.next()) {
				String[] rowContent = qe.fetchNextRowData(rs);
				ReportSchedules.put(rowContent[0], new Boolean((rowContent[1].equals("1")) ? "true" : "false"));
				
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}finally{
			if(qe!= null){
				qe.closeConnection();
			}
		}	
	}
	
	protected abstract String getOutputDirectory();
	
	
}

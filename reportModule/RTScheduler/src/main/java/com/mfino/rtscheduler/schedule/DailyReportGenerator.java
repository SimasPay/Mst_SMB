package com.mfino.rtscheduler.schedule;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.apache.commons.io.FileUtils;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mfino.domain.User;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ReportUtil;

public class DailyReportGenerator {

	private static Logger log = LoggerFactory.getLogger("DailyReportGenerator");
	
	private String dailyReportsInputDir = null;
	private String[] inputFileNames;
	private String dailyReportsOutputDir = null;
	private String zipInputDir = "";
	private String zipOutputDir = "";
	//private String emailRecipients = "mfinoemailtest@gmail.com";
	private String emailRecipients = "";
	private String yesterdayEnd;
	private Date startTime;
	private String yesterdayStart;
	private Date endTime;
	private String footerMessage;
	private SimpleDateFormat dateFormatForReports = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   
	protected static HibernateSessionHolder hibernateSessionHolder = null;
	private int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
	private Properties prop = new Properties();
	private String oneDayFileNameContent = "Daily";
	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..//spring-datasource-beans.xml");
		//ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("com\\mfino\\rtscheduler\\schedule\\spring.xml");
		
	}
	public void initaliseTimes() throws ParseException {
		log.info("initaliseTimes started");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		yesterdayEnd = fmt.format(System.currentTimeMillis());
		endTime = fmt.parse(yesterdayEnd);
		yesterdayStart = fmt.format(endTime.getTime() - MILLIS_IN_DAY);
		startTime = fmt.parse(yesterdayStart);
		log.info("StartTime : "+startTime.toString());
		log.info("endTime : "+endTime.toString());
		
		log.info("initaliseTimes Finished");
		
	}

	public void loadProperties() throws IOException {
		log.info("Loading Properties Started");
		InputStream ins = this.getClass().getResourceAsStream("/rtscheduler.properties");
		prop.load(ins);
		ins.close();
		dailyReportsInputDir = prop.getProperty("dailyReportsInputDir");
		dailyReportsOutputDir = prop.getProperty("dailyReportsOutputDir");
		emailRecipients = prop.getProperty("email.recipient.list");
		footerMessage = ConfigurationUtil.getReportFooter();
		log.info("dailyReportsInputDir :" + dailyReportsInputDir);
		log.info("dailyReportsOutputDir :" + dailyReportsOutputDir);
		//log.info("emailRecipients :" + emailRecipients);
		log.info("Loading Properties Finished");
	}
	

	public boolean hasMultipleReports() {
        return false;
    }
	
   private void getAllInputFileNames(final String extension) throws Exception {
		File dir = new File(dailyReportsInputDir);
		if (!isValidPath(dir)) {
			return;
		}
		inputFileNames = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(extension)) {
					return true;
				}
				return false;
			}
		});
	}

	private void printAllInputFileNames() {
		for (int i = 0; i < inputFileNames.length; i++) {
			log.info("Generating Reports for the following *.prpt files");
			log.info(inputFileNames[i]);
		}
	}

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

	public void generate() {
		log.info("Scheduler Trigered, for the generation of Daily Reports has started");
		try{
			loadProperties();
			getAllInputFileNames(".prpt");
			if(inputFileNames != null && inputFileNames.length > 0){
				printAllInputFileNames();
				initaliseTimes();
				generateReports();
				setZipDirs();
				zipOutputDir = zipFiles(zipInputDir,zipOutputDir+"_pdf",".pdf");
				if(zipOutputDir != null){
				    if(emailRecipients!=null)
					sendMail(emailRecipients,"DailyReports_Pdf_"+yesterdayEnd.replace("-", ""), "Attached Daily Reports Zip File", zipOutputDir);
				}else{
					log.info("Not able to zip the input directory "+zipInputDir+" and hence not sending mail");
				}
				setZipDirs();
				zipOutputDir = zipFiles(zipInputDir,zipOutputDir+"_xls",".xls");
				if(zipOutputDir != null){
				    if(emailRecipients!=null)
					sendMail(emailRecipients,"DailyReports_xls_"+yesterdayEnd.replace("-", ""), "Attached Daily Reports Zip File", zipOutputDir);
				}else{
					log.info("Not able to zip the input directory "+zipInputDir+" and hence not sending mail");
				}
				setZipDirs();
				zipOutputDir = zipFiles(zipInputDir,zipOutputDir+"_csv",".csv");
				if(zipOutputDir != null){
				    if(emailRecipients!=null)
					sendMail(emailRecipients,"DailyReports_csv_"+yesterdayEnd.replace("-", ""), "Attached Daily Reports Zip File", zipOutputDir);
				}else{
					log.info("Not able to zip the input directory "+zipInputDir+" and hence not sending mail");
				}
			}else{
				log.info("No Input *.prpt files exist to generate reports");
			}
		}catch(Exception e){
			log.error("Some Error while generating Daily reports, to know the exact error please check the stack trace",e);
		}
		log.info("Scheduler Trigered, for the generation of Daily Reports has completed");
	}
	public void generateReportsOnDemand(Date StartTime,Date EndTime,String ReportName, String generatedReportFileName, String userName)
			throws IOException, ParseException{
		log.info("Begin: generateReportsOnDemand");
		loadProperties();
		startTime=StartTime;
		endTime=EndTime;
		
	    genOnDemandReport("file:" + dailyReportsInputDir + File.separator +ReportName+".prpt", generatedReportFileName, userName);
	    generateCSVFromXLS(generatedReportFileName+ReportUtil.EXCEL_EXTENTION);

	    String zipFile = generatedReportFileName + ReportUtil.ZIP_EXTENTION;
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(zipFile)));
		
		if (new File(generatedReportFileName + ReportUtil.PDF_EXTENTION).exists()) 
			addFileToZipFile(zos, generatedReportFileName + ReportUtil.PDF_EXTENTION);
		if (new File(generatedReportFileName + ReportUtil.EXCEL_EXTENTION).exists())
			addFileToZipFile(zos, generatedReportFileName + ReportUtil.EXCEL_EXTENTION);
		if (new File(generatedReportFileName + ReportUtil.CSV_EXTENTION).exists())
			addFileToZipFile(zos, generatedReportFileName + ReportUtil.CSV_EXTENTION);
		
		zos.flush();
		zos.close();   
		
		if(emailRecipients!=null) {
			String formatStartDate = dateFormatForReports.format(StartTime);
			String formatEndDate = dateFormatForReports.format(EndTime);
			String subject = ReportName + " for period: " + formatStartDate + " to " + formatEndDate;
			String message = "Attached " + ReportName + " for period: " + formatStartDate + " to " + formatEndDate; 
			sendMail(emailRecipients, subject, message, zipFile);
		}
		log.info("End: generateReportsOnDemand");
	}
	
	private void addFileToZipFile(ZipOutputStream zos, String fileName) {
		log.info("Zipping the file : "+ fileName );
		byte[] buffer = new byte[1024];
		
		try {
			zos.putNextEntry(new ZipEntry(fileName.substring(fileName.lastIndexOf(File.separator)+1)));
			FileInputStream in = new FileInputStream(fileName);
			int read;
			while ((read=in.read(buffer)) > 0) {
				zos.write(buffer, 0, read);
			}
			zos.closeEntry();
			in.close();
			log.info("Successfully zipped the file : "+ fileName);
		} catch (FileNotFoundException e) {
			log.error("Error: FileNotFoundException While zipping the report file:" + fileName);
		} catch (IOException e) {
			log.error("Error: IOException While zipping the report file:" + fileName);
		}
	}
	
	
	private void generateCSVFromXLS(String xlsFilePath) {
		log.info("Begin: generateCSVFromXLS for file: " + xlsFilePath);
		try {
			String encoding = "UTF8";
			String csvFileName = xlsFilePath.substring(0, xlsFilePath.lastIndexOf(".")) + ReportUtil.CSV_EXTENTION;
			OutputStream os = (OutputStream) new FileOutputStream(new File(csvFileName));
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			BufferedWriter bw = new BufferedWriter(osw);

			WorkbookSettings ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			Workbook w = Workbook.getWorkbook(new File(xlsFilePath), ws);

			log.info("Getting sheets from the worksheet");
			for (int s = 0; s < w.getNumberOfSheets(); s++) {
				Sheet sheet = w.getSheet(s);
				Cell[] row = null;

				log.info("Getting cells from the Sheet");
				for (int r = 0; r < sheet.getRows(); r++) {
					row = sheet.getRow(r);

					if (row.length > 0) {
						bw.write(row[0].getContents());
						for (int c = 1; c < row.length; c++) {
							bw.write(',');
							bw.write(row[c].getContents());
						}
					}
					bw.newLine();
				}
			}
			bw.flush();
			bw.close();
			os.close();
		} catch (FileNotFoundException e) {
			log.error("Error: FileNotFoundException While generationg the CSV from XLS file:"+ xlsFilePath, e);
		} catch (UnsupportedEncodingException e) {
			log.error("Error: UnsupportedEncodingException While generationg the CSV from XLS file:"+ xlsFilePath, e);
		} catch (BiffException e) {
			log.error("Error: BiffException While generationg the CSV from XLS file:"+ xlsFilePath, e);
		} catch (IndexOutOfBoundsException e) {
			log.error("Error: IndexOutOfBoundsException While generationg the CSV from XLS file:"+ xlsFilePath, e);
		} catch (IOException e) {
			log.error("Error: IOException While generationg the CSV from XLS file:"+ xlsFilePath, e);
		}
		log.info("End: generateCSVFromXLS for file: " + xlsFilePath);
	}
	
	private void sendMail(String emailRecipients,String subject, String message, String attachmentFileName){
		log.info("sending mail with reports as an attachment : " + attachmentFileName);
		try{
			String[] emailRecipientsList = emailRecipients.split(",");
			File attachmentFile = new File(attachmentFileName);
			MailUtil mailUtil = new MailUtil();
			for(int i=0; i< emailRecipientsList.length; i++){
				if(mailUtil.isValidEmailAddress(emailRecipientsList[i])){
					mailUtil.sendMail(emailRecipientsList[i], "", subject, message, attachmentFile);
				}
			}
			log.info("sendMail function finished");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private boolean createDir(String dirPath) {
		return new File(dirPath).mkdirs();
	}

	private String genTillNowOutputFileName(String inputFileName) {
		StringBuilder sb = new StringBuilder();
		String date = yesterdayEnd.replace("-", "");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		String start =  fmt.format(startTime.getTime()).replace("-", "");
        String end  = fmt.format(endTime.getTime()).replace("-", "");
		String outputDir = dailyReportsOutputDir + File.separator + date;
		if (!new File(outputDir).exists()) {
			createDir(outputDir);
		}
		sb.append(outputDir);
		sb.append(File.separator);
		sb.append(inputFileName.replace(".prpt", ""));
		sb.append("_");
		//sb.append(start);
		//sb.append("-");
		sb.append(end);
		//System.out.println(sb);
		return sb.toString();
	}

	private String genOneDayOutputFileName(String inputFileName) {
		StringBuilder sb = new StringBuilder();
		String start = yesterdayStart.replace("-", "");
		String end = yesterdayEnd.replace("-", "");
		String outputDir = dailyReportsOutputDir + File.separator + end;
		if (!new File(outputDir).exists()) {
			createDir(outputDir);
		}
		sb.append(outputDir);
		sb.append(File.separator);
		sb.append(inputFileName.replace(".prpt", ""));
		sb.append("_");
		sb.append(start);
		sb.append("-");
		sb.append(end);
		//System.out.println(sb);
		return sb.toString();
	}

	private void generateReports() throws FileNotFoundException, UnsupportedEncodingException {
		log.info("generateReports function started");
		for (int i = 0; i < inputFileNames.length; i++) {
			log.info("Report  started"+inputFileNames[i]);
			if (isOneDayReport(inputFileNames[i])) {
				String oneDayOutputFileName = genOneDayOutputFileName(inputFileNames[i]);
				/*if(inputFileNames[i].equals("NDICReport1.prpt")){
					log.info("NDIC function started");
					NDICReportGenerator form = new NDICReportGenerator();
					form.NDICReportGeneration();
				}*/
				genOneDayReport("file:" + dailyReportsInputDir + File.separator
						+ inputFileNames[i], oneDayOutputFileName);
				
			} else {
				String tillNowOutputFileName = genTillNowOutputFileName(inputFileNames[i]);
				/*if(inputFileNames[i].equals("NDICReport1.prpt")){
					log.info("NDIC function started");
					NDICReportGenerator form = new NDICReportGenerator();
					form.NDICReportGeneration();
				}*/
				genOverallReport("file:" + dailyReportsInputDir + File.separator
						+ inputFileNames[i], tillNowOutputFileName);
			}
			
		}
		csvConversion();
		log.info("generateReports function finished");
	}
	
	private boolean isOneDayReport(String fileName) {
		if (fileName.toLowerCase()
				.contains(oneDayFileNameContent.toLowerCase())) {
			return true;
		}
		return false;
	}

	private void genOneDayReport(String inputfileName, String outputFileName) {
		ClassicEngineBoot.getInstance().start();
		ResourceManager manager = new ResourceManager();
		manager.registerDefaults();
		log.info("Generating report for " + inputfileName);
		try {
			Resource res = manager.createDirectly(new URL(inputfileName),
					MasterReport.class);
			MasterReport report = (MasterReport) res.getResource();
			report.getParameterValues().put(ReportConstants.PARAMETER_QUERY_START_TIME, startTime);
			report.getParameterValues().put(ReportConstants.PARAMETER_QUERY_END_TIME, endTime);
			report.getParameterValues().put(ReportConstants.PARAMETER_FOOTER_MESSAGE, footerMessage);
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_TYPE, "Daily");
			report.getParameterValues().put(ReportConstants.PARAMETER_DISPLAY_START_TIME, dateFormatForReports.format(startTime));
			report.getParameterValues().put(ReportConstants.PARAMETER_DISPLAY_END_TIME, dateFormatForReports.format(endTime));
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_GENERATED_BY, "Scheduled Job");
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_GENERATED_TIME, dateFormatForReports.format(new Date()));			
			PdfReportUtil.createPDF(report, outputFileName + ".pdf");
			log.info("Generated PDF report : " + outputFileName + ".pdf");
			ExcelReportUtil.createXLS(report, outputFileName + ".xls");
			log.info("Generated XLS report : " + outputFileName + ".xls");
			
		} catch (Exception e) {
			log.error("Error occured while generating report and the input file name  is "+inputfileName, e);
		}
	}

	private void genOverallReport(String inputfileName, String outputFileName) {
		ClassicEngineBoot.getInstance().start();
		ResourceManager manager = new ResourceManager();
		manager.registerDefaults();
		log.info("Generating report for " + inputfileName);
		try {
			Resource res = manager.createDirectly(new URL(inputfileName),
					MasterReport.class);
			MasterReport report = (MasterReport) res.getResource();
			report.getParameterValues().put(ReportConstants.PARAMETER_QUERY_START_TIME, startTime);
			report.getParameterValues().put(ReportConstants.PARAMETER_QUERY_END_TIME, endTime);
			report.getParameterValues().put(ReportConstants.PARAMETER_FOOTER_MESSAGE, footerMessage);
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_TYPE, "All");
			report.getParameterValues().put(ReportConstants.PARAMETER_DISPLAY_START_TIME, dateFormatForReports.format(startTime));
			report.getParameterValues().put(ReportConstants.PARAMETER_DISPLAY_END_TIME, dateFormatForReports.format(endTime));
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_GENERATED_BY, "Scheduled Job");
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_GENERATED_TIME, dateFormatForReports.format(new Date()));				
			PdfReportUtil.createPDF(report, outputFileName + ".pdf");
			log.info("Generated PDF report : " + outputFileName + ".pdf");
			ExcelReportUtil.createXLS(report, outputFileName + ".xls");
			log.info("Generated XLS report : " + outputFileName + ".xls");
			
		} catch (Exception e) {
			log.error("Error occured while generating report and the input file name  is "+inputfileName, e);
		}
	}
	private void genOnDemandReport(String ReportName, String outputFileName, String userName) {
		ClassicEngineBoot.getInstance().start();
		ResourceManager manager = new ResourceManager();
		manager.registerDefaults();
		log.info("Generating report" + ReportName);
		try {
			Resource res = manager.createDirectly(new URL(ReportName),
					MasterReport.class);
			MasterReport report = (MasterReport) res.getResource();
			report.getParameterValues().put(ReportConstants.PARAMETER_QUERY_START_TIME, startTime);
			report.getParameterValues().put(ReportConstants.PARAMETER_QUERY_END_TIME, endTime);
			report.getParameterValues().put(ReportConstants.PARAMETER_FOOTER_MESSAGE, footerMessage);
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_TYPE, "On Demand");
			report.getParameterValues().put(ReportConstants.PARAMETER_DISPLAY_START_TIME, dateFormatForReports.format(startTime));
			report.getParameterValues().put(ReportConstants.PARAMETER_DISPLAY_END_TIME, dateFormatForReports.format(endTime));
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_GENERATED_BY, userName);
			report.getParameterValues().put(ReportConstants.PARAMETER_REPORT_GENERATED_TIME, dateFormatForReports.format(new Date()));
			
			PdfReportUtil.createPDF(report, outputFileName + ".pdf");
			log.info("Generated PDF report : " + outputFileName + ".pdf");
			ExcelReportUtil.createXLS(report, outputFileName + ".xls");
			log.info("Generated XLS report : " + outputFileName + ".xls");
			
		} catch (Exception e) {
			log.error("Error occured while generating report and the input file name  is "+ReportName, e);
		}
	}

	private void setZipDirs() {
		String date = yesterdayEnd.replace("-", "");
		zipInputDir = dailyReportsOutputDir + File.separator + date;
		zipOutputDir = zipInputDir + File.separator + date ;
	}

	private String zipFiles(String zipInputDir,String zipOutputDir,String extension) {
		log.info("zipFiles function Started");
		log.info("zipFiles Input Directory Path: " + zipInputDir);
		log.info("zipFiles Output Directory Path: " + zipOutputDir);
		try {
			File inFolder = new File(zipInputDir);
			File outFolder = new File(zipOutputDir + ".zip");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));
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
			log.info("zipFiles function Finished");
			return outFolder.toString();
		} catch (Exception e) {
			log.error("Error occured while zipping files and the zip Input Directory is  "+zipInputDir+ " and the zip Output Directory is "+zipOutputDir, e);
		}
		return null;
	}
	
	private String[] getFilesListFromDirectory(String directory,final String extension){
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

	public static void subReport() {
		ClassicEngineBoot.getInstance().start();
		ResourceManager manager = new ResourceManager();
		manager.registerDefaults();
		try {
			Resource res = manager.createDirectly(new URL(
					"file:C:\\Users\\Pradeep\\Desktop\\sub.prpt"),
					MasterReport.class);
			MasterReport report = (MasterReport) res.getResource();
			PdfReportUtil.createPDF(report, "subscriber.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void csvConversion() throws FileNotFoundException,
			UnsupportedEncodingException {
		try {
			log.info("Entered csvConversion function");
			String date = yesterdayEnd.replace("-", "");
			String searchInFolder = dailyReportsOutputDir + File.separator
					+ date;
			File folderName = new File(searchInFolder);

			log.info("Started searching for xls extension files");
			String[] extensions = { "xls" };
			// boolean recursive = true;
			@SuppressWarnings("rawtypes")
			Collection xslFiles = FileUtils.listFiles(folderName, extensions,
					true);
			for (@SuppressWarnings("rawtypes")
			Iterator iterator = xslFiles.iterator(); iterator.hasNext();) {
				File filename1 = (File) iterator.next();
				String xslFileName = filename1.getAbsolutePath();

				int filename_splitindex = xslFileName
						.lastIndexOf(File.separator);
				int filename_splitindex1 = xslFileName.lastIndexOf(".");
				String name = xslFileName.substring(filename_splitindex + 1,
						filename_splitindex1);

				// String tempFolder = dailyReportsOutputDir + File.separator +
				// date;

				log.info("Conversion to csv function started");
				File csvFileName = new File(searchInFolder + File.separator
						+ name + ".csv");
				OutputStream os = (OutputStream) new FileOutputStream(
						csvFileName);
				String encoding = "UTF8";
				OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
				BufferedWriter bw = new BufferedWriter(osw);

				WorkbookSettings ws = new WorkbookSettings();
				ws.setLocale(new Locale("en", "EN"));
				Workbook w = Workbook.getWorkbook(new File(xslFileName), ws);

				log.info("Getting sheets from the worksheet");
				for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
					Sheet s = w.getSheet(sheet);

					// bw.write(s.getName());
					// bw.newLine();

					Cell[] row = null;

					log.info("Getting cells from the Sheet");
					for (int i = 0; i < s.getRows(); i++) {
						row = s.getRow(i);

						if (row.length > 0) {
							bw.write(row[0].getContents());
							for (int j = 1; j < row.length; j++) {
								bw.write(',');
								bw.write(row[j].getContents());
							}
						}
						bw.newLine();
					}
				}
				bw.flush();
				bw.close();
			}

			log.info("Conversion to CSVs completed");
		}

		catch (UnsupportedEncodingException e)

		{
			log.error("Error in CSV Conversion Unsupported String Encoding ", e);
		} catch (IOException e) {
			log.error("Error in CSV Conversion IOException", e);

		} catch (Exception e) {
			log.error("Error in CSV Conversion", e);

		}
	}
	

	public static void main(String args[]) {
		System.out.println("Started");
		DailyReportGenerator drg = new DailyReportGenerator();
		drg.generate();
		// subReport();
		System.out.println("Completed");
	}
}

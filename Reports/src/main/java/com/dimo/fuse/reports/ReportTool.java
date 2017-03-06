package com.dimo.fuse.reports;

import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.scheduler.ReportSchedulerProperties;
import com.dimo.fuse.reports.util.FileReaderUtil;
import com.dimo.fuse.reports.Impl.CSVReportGenerator;
import com.dimo.fuse.reports.Impl.ExcelReportGenerator;
import com.dimo.fuse.reports.Impl.PDFReportGenerator;
import com.dimo.fuse.reports.Impl.TextReportGenerator;
import com.dimo.fuse.reports.db.DBProperties;
import com.dimo.fuse.reports.db.QueryExecutor;

/**
 * 
 * @author Amar
 * 
 */
public class ReportTool {

	private static Logger log = LoggerFactory.getLogger(ReportTool.class);
	static final String DB_DRIVERS = DBProperties.getJDBCDriver();
	
	public static void generateReports(String reportPropertiesFilePath,
			ReportParameters reportParameters) {
		QueryExecutor qe = null;
		try {
			ReportProperties reportProperties = new ReportProperties(
					reportPropertiesFilePath);
			JSONArray listOfReportTypes = reportProperties
					.getJSONArray(ReportPropertyConstants.REPORT_TYPE);

			log.info("Generating reports for report types:"
					+ listOfReportTypes.toString());
			String query = reportProperties
					.getProperty(ReportPropertyConstants.SQL_QUERY);
			query = replaceQueryVariablesWithActuals(query, reportParameters,
					reportProperties);
			query = query.replace(";", "");
			JSONArray encryptedFields = reportProperties
					.getJSONArray(ReportPropertyConstants.ENCRYPTED_COLUMN_INDICES);

			qe = new QueryExecutor();
			qe.setEncryptedFields(encryptedFields);
			qe.setNullValueReplacementText(reportProperties
					.getProperty(ReportPropertyConstants.NULL_VALUE_REPLACEMENT_TEXT));
			ResultSet rs = qe.getResultSet(query);
			List<ReportGenerator> reportsList = new ArrayList<ReportGenerator>();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String reportName = reportProperties
			.getProperty(ReportPropertyConstants.NAME_OF_THE_REPORT);

			String reportFilePathWithOutExtension = 
					reportParameters.getDestinationFolder()
					+ File.separator
					+ reportProperties
							.getProperty(ReportPropertyConstants.NAME_OF_THE_REPORT)
					+ "_"
					+ dateFormat.format(reportParameters
							.getStartTime())
					+ "-"
					+ dateFormat.format(reportParameters
							.getEndTime());
			if(StringUtils.isNotBlank(reportParameters.getTransactionTypeId())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getTransactionTypeId();
			}
			if(StringUtils.isNotBlank(reportParameters.getTransactionStatusId())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getTransactionStatusId();
			}
			if(StringUtils.isNotBlank(reportParameters.getSourceMdn())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getSourceMdn();
			}
			if(StringUtils.isNotBlank(reportParameters.getDestMdn())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getDestMdn();
			}
			if(StringUtils.isNotBlank(reportParameters.getBillerCode())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getBillerCode();
			}
			if(StringUtils.isNotBlank(reportParameters.getBankRRN())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getBankRRN();
			}
			if(StringUtils.isNotBlank(reportParameters.getSourcePartnerCode())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getSourcePartnerCode();
			}
			if(StringUtils.isNotBlank(reportParameters.getDestPartnerCode())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getDestPartnerCode();
			}
			if(StringUtils.isNotBlank(reportParameters.getChannelName())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getChannelName();
			}
			if(StringUtils.isNotBlank(reportParameters.getReferenceNo())){
				reportFilePathWithOutExtension = reportFilePathWithOutExtension+"-"+reportParameters.getReferenceNo();
			}
			
			
			for (int index = 0; index < listOfReportTypes.length(); index++) {
				String reportType = listOfReportTypes.getString(index);
				ReportGenerator reportGenerator = getReportGenerator(reportType);
				reportsList.add(reportGenerator);
				reportGenerator.setReportProperties(reportProperties);
				reportGenerator.setReportParameters(reportParameters);
				reportGenerator
						.setReportFilePath(new File(reportFilePathWithOutExtension + "."
										+ reportType.toLowerCase()));
				reportGenerator.openDocAndCreateHeaders();
			}
			log.info("Fecthing data fron db and writing to transaction tables");
			while (rs!=null && rs.next()) {
				String[] rowContent = qe.fetchNextRowData(rs);
				for (ReportGenerator report : reportsList) {
					if(reportName.equalsIgnoreCase("BIReport") || reportName.equalsIgnoreCase("BIMonthlyOnlineReport")){ // if report BIReport OR BIMonthlyReport; Needs to write in cleaner way
						for(int i=0; i< rowContent.length ; i+=2){
							String[] newRowContent = new String[]{rowContent[i],rowContent[i+1]};							
							report.addRowContent(newRowContent);
						}
					}else
						report.addRowContent(rowContent);
				}
			}
			for (ReportGenerator report : reportsList) {
				report.createFootersAndCloseDoc();
			}

		} catch (JSONException e) {
			log.error("An error occured while reading a json file."
					+ e.getMessage());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if(qe != null){
				qe.closeConnection();
			}
		}

	}

	private static ReportGenerator getReportGenerator(String reportType)
			throws Exception {
		ReportGenerator reportGenerator = null;

		if (reportType.equalsIgnoreCase("pdf")) {
			reportGenerator = new PDFReportGenerator();
		} else if (reportType.equalsIgnoreCase("xls")
				|| reportType.equalsIgnoreCase("xlsx")) {
			reportGenerator = new ExcelReportGenerator();
		} else if (reportType.equalsIgnoreCase("csv")) {
			reportGenerator = new CSVReportGenerator();
		} else if (reportType.equalsIgnoreCase("txt")) {
			reportGenerator = new TextReportGenerator();
		} else {
			log.error("Invalid Report Type:" + reportType);
			throw new Exception("Invalid Report Type:" + reportType);
		}
		return reportGenerator;
	}

	public static String replaceQueryVariablesWithActuals(String query,
			ReportParameters reportParameters, ReportProperties reportProperties) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(ReportSchedulerProperties.getProperty("dateFormatInReportQuery"));
		if (reportParameters.getEndTime() != null) {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				Date endGMTDate = new Date(reportParameters.getEndTime().getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${EndTime}",	"'" + dateFormat.format(endGMTDate)	+ "'");
			} else {
				query = query.replace("${EndTime}", "'" + dateFormat.format(reportParameters.getEndTime()) + "'");
			}
		} else {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				Date endGMTDate = new Date(new Date().getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${EndTime}", "'" + dateFormat.format(endGMTDate) + "'");
			} else {
				query = query.replace("${EndTime}", "'" + dateFormat.format(new Date()) + "'");
			}			
		}
		
		if (reportParameters.getStartTime() != null) {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				Date startGMTDate = new Date(reportParameters.getStartTime().getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${StartTime}", "'" + dateFormat.format(startGMTDate) + "'");
			} else {
				query = query.replace("${StartTime}", "'" + dateFormat.format(reportParameters.getStartTime()) + "'");
			}
		} else {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				Date startGMTDate = new Date(new Date(0).getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${StartTime}", "'" + dateFormat.format(startGMTDate) + "'");
			} else {
				query = query.replace("${StartTime}", "'" + dateFormat.format(new Date(0)) + "'");
			}			
		}
		
		if (reportParameters.getFromUpdatedDate() != null) {
			if (DB_DRIVERS.equals("oracle.jdbc.OracleDriver")) {
				Date updatedGMTDate = new Date(reportParameters.getFromUpdatedDate().getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${FromLastUpdateTime}", "'" + dateFormat.format(updatedGMTDate) + "'");
			} else {
				query = query.replace("${FromLastUpdateTime}", "'" + dateFormat.format(reportParameters.getFromUpdatedDate()) + "'");
			}
		} else {
			if (DB_DRIVERS.equals("oracle.jdbc.OracleDriver")) {
				Date updatedGMTDate = new Date(new Date(0).getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${FromLastUpdateTime}", "'" + dateFormat.format(updatedGMTDate) + "'");
			} else {
				query = query.replace("${FromLastUpdateTime}", "'" + dateFormat.format(new Date(0)) + "'");
			}
		}
		
		if (reportParameters.getToUpdatedDate() != null) {
			if (DB_DRIVERS.equals("oracle.jdbc.OracleDriver")) {
				Date toUpdatedGMTDate = new Date(reportParameters.getToUpdatedDate().getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${ToLastUpdateTime}", "'" + dateFormat.format(toUpdatedGMTDate) + "'");
			} else {
				query = query.replace("${ToLastUpdateTime}", "'" + dateFormat.format(reportParameters.getToUpdatedDate()) + "'");
			}
		} else {
			if (DB_DRIVERS.equals("oracle.jdbc.OracleDriver")) {
				Date toUpdatedGMTDate = new Date(new Date().getTime() - TimeZone.getDefault().getRawOffset());
				query = query.replace("${ToLastUpdateTime}", "'" + dateFormat.format(toUpdatedGMTDate) + "'");
			} else {
				query = query.replace("${ToLastUpdateTime}", "'" + dateFormat.format(new Date()) + "'");				
			}
		}

		if (reportProperties
				.getProperty(ReportPropertyConstants.DATE_TIME_FORMAT_OF_DATA_FROM_DB) != null) {
			query = query
					.replace(
							"${DateTimeFormat}",
							"'"
									+ reportProperties
											.getProperty(ReportPropertyConstants.DATE_TIME_FORMAT_OF_DATA_FROM_DB)
									+ "'");
		}
		
		if (reportParameters.getSubscriberStatusId() != null) {
			query = query.replace("${SubscriberStatus}", "'" + reportParameters.getSubscriberStatusId() + "'");
		} else {
			query = query.replace("${SubscriberStatus}", "'%%'");
		}
		
		
		if (reportParameters.getPocketTemplateId() != null) {
			query = query.replace("${PocketTemplateID}", "'" + reportParameters.getPocketTemplateId() + "'");
		} else {
			query = query.replace("${PocketTemplateID}", "'%%'");
		}

		
		if (reportParameters.getSubscriberRestrictions() != null) {
			query = query.replace("${SubscriberRestrictions}", "'" + reportParameters.getSubscriberRestrictions() + "'");
		} else {
			query = query.replace("${SubscriberRestrictions}", "'%%'");
		}
		
		if (reportParameters.getTransactionStatusId() != null) {
			query = query.replace("${TransactionStatus}", "'" + reportParameters.getTransactionStatusId() + "'");
		} else {
			query = query.replace("${TransactionStatus}", "'%%'");
		}
		
		if (reportParameters.getSourceMdn() != null) {
			query = query.replace("${SourceMDN}", "'" + reportParameters.getSourceMdn() + "'");
		} else {
			query = query.replace("${SourceMDN}", "'%%'");
		}
		
		if (reportParameters.getDestMdn() != null) {
			query = query.replace("${DestinationMDN}", "'" + reportParameters.getDestMdn() + "'");
		} else {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				query = query.replace("${DestinationMDN}", "'%%' OR 1=1");
			}else{
				query = query.replace("${DestinationMDN}", "'%%'");
			}			
		}
		
		if (reportParameters.getSourcePocketType() != null) {
			query = query.replace("${SourcePocketType}", "'" + reportParameters.getSourcePocketType() + "'");
		} else {
			query = query.replace("${SourcePocketType}", "'%%'");
		}
		if (reportParameters.getMdn() != null) {
			query = query.replace("${MDN}", "'" + reportParameters.getMdn() + "'");
		} else {
			query = query.replace("${MDN}", "'%%'");
		}
		
		if (reportParameters.getTransactionTypeId() != null) {
			query = query.replace("${TransactionType}", "'" + reportParameters.getTransactionTypeId() + "'");
		} else {
			query = query.replace("${TransactionType}", "'%%'");
		}
		
		if (reportParameters.getDestinationPocketStatusId() != null) {
			query = query.replace("${DestinationPocketStatus}", "'" + reportParameters.getDestinationPocketStatusId() + "'");
		} else {
			query = query.replace("${DestinationPocketStatus}", "'%%'");
		}
		
		if (reportParameters.getSettlementStatusId() != null) {
			query = query.replace("${SettlementStatus}", "'" + reportParameters.getSettlementStatusId() + "'");
		} else {
			query = query.replace("${SettlementStatus}", "'%%'");
		}
		
		if (reportParameters.getPartnerCode() != null) {
			query = query.replace("${PartnerCode}", "'" + reportParameters.getPartnerCode() + "'");
		} else {
			query = query.replace("${PartnerCode}", "'%%'");
		}
		
		if (reportParameters.getBillerCode() != null) {
			query = query.replace("${BillerCode}", "'" + reportParameters.getBillerCode() + "'");
		} else {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				query = query.replace("${BillerCode}", "'%%' OR 1=1");
			}else{
				query = query.replace("${BillerCode}", "'%%'");
			}
		}
		
		if (reportParameters.getPartnerTypeId() != null) {
			query = query.replace("${PartnerType}", "'" + reportParameters.getPartnerTypeId() + "'");
		} else {
			query = query.replace("${PartnerType}", "'%%'");
		}
		
		if (reportParameters.getCsrUserName() != null) {
			query = query.replace("${CSRUserName}", "'" + reportParameters.getCsrUserName() + "'");
		} else {
			query = query.replace("${CSRUserName}", "'%%'");
		}
		
		if (reportParameters.getMerchantId() != null) {
			query = query.replace("${MerchantID}", "'" + reportParameters.getMerchantId() + "'");
		} else {
			query = query.replace("${MerchantID}", "'%%'");
		}
		
		if (reportParameters.getMerchantAccount() != null) {
			query = query.replace("${MerchantAccount}", "'" + reportParameters.getMerchantAccount() + "'");
		} else {
			query = query.replace("${MerchantAccount}", "'%%'");
		}
		
		if (reportParameters.getReferenceNo() != null) {
			query = query.replace("${ReferenceNo}", "'" + reportParameters.getReferenceNo() + "'");
		} else {
			query = query.replace("${ReferenceNo}", "'%%'");
		}
		
		if (reportParameters.getIdNumber() != null) {
			query = query.replace("${IDCardNo}", "'" + reportParameters.getIdNumber() + "'");
		} else {
			query = query.replace("${IDCardNo}", "'%%'");
		}
		
		if (query.contains("$(TimeZone)")) {
			query = query.replace("$(TimeZone)", TimeZone.getDefault().getID());
		}	
		
		
		if (reportParameters.getSourcePartnerCode() != null) {
			query = query.replace("${SourcePartnerCode}", "'" + reportParameters.getSourcePartnerCode() + "'");
		} else {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				query = query.replace("${SourcePartnerCode}", "'%%' OR 1=1");
			}else{
				query = query.replace("${SourcePartnerCode}", "'%%'");
			}
		}
		
		if (reportParameters.getDestPartnerCode() != null) {
			query = query.replace("${DestPartnerCode}", "'" + reportParameters.getDestPartnerCode() + "'");
		} else {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				query = query.replace("${DestPartnerCode}", "'%%' OR 1=1");
			}else{
				query = query.replace("${DestPartnerCode}", "'%%'");
			}			
		}
		
		if (reportParameters.getChannelName() != null) {
			query = query.replace("${ChannelName}", "'" + reportParameters.getChannelName() + "'");
		} else {
			query = query.replace("${ChannelName}", "'%%'");
		}
		
		if (reportParameters.getBankRRN() != null) {
			query = query.replace("${BankRRN}", "'" + reportParameters.getBankRRN() + "'");
		} else {
			if(DB_DRIVERS.equals("oracle.jdbc.OracleDriver")){
				query = query.replace("${BankRRN}", "'%%' OR 1=1");
			}else{
				query = query.replace("${BankRRN}", "'%%'");
			}			
		}
		
		log.info("The Query being executed as part of the report is : " + query);
		return query;
	}

	public static JSONObject readReportProperties(String filePath) {
		return FileReaderUtil.readFileContAsJsonObj(filePath);
	}

	public static int[] convertToIntArray(JSONArray jsonArray) {
		try {
			int[] intArray = new int[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				intArray[i] = jsonArray.getInt(i);
			}
			return intArray;
		} catch (JSONException e) {
			log.error("Invalid Data Type in Json Array. It should be an Integer.");
			e.printStackTrace();
		}
		return null;
	}
}

package com.dimo.fuse.reports;

import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 * 
 */
public abstract class ReportGenerator {
	private ResultSet resultSet;
	private File reportFilePath;
	protected ReportProperties reportProperties;
	private ReportParameters reportParameters;

	public abstract void createDocument();

	public abstract void openDocument();

	public abstract void closeDocument();

	public abstract void addLogo();

	public abstract void addPageHeader();

	public abstract void addPageFooter();

	public abstract void addColumnHeaders();

	public abstract void addReportHeader();

	public abstract void addReportFooter();

	public abstract void addRowContent(String[] rowContent);

	private static Logger log = LoggerFactory.getLogger(ReportGenerator.class);

	public void openDocAndCreateHeaders() {
		try {
			createDocument();
//			openDocument();
			addPageHeader();
			addPageFooter();
			openDocument();
			addLogo();
			addReportHeader();
			addColumnHeaders();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void createFootersAndCloseDoc() {
		try {
			addReportFooter();
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			closeDocument();
		}
	}

	public File getReportFilePath() {
		return reportFilePath;
	}

	public void setReportFilePath(File reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public ReportProperties getReportProperties() {
		return reportProperties;
	}

	public void setReportProperties(ReportProperties reportProperties) {
		this.reportProperties = reportProperties;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public Integer getMaxNoColumnsInReportHeaderFooter(String property) {
		int maxNoColumnsInReportHeader = 0;
		try {
			JSONArray headerRows = reportProperties
					.getJSONArray(ReportPropertyConstants.REPORT_HEADER);
			for (int i = 0; i < headerRows.length(); i++) {
				JSONArray headerRow = headerRows.getJSONArray(i);
				if (maxNoColumnsInReportHeader < headerRow.length())
					maxNoColumnsInReportHeader = headerRow.length();
			}
		} catch (JSONException e) {
			log.error("Error occured while reading a Json array. "
					+ e.getMessage());
		}
		return maxNoColumnsInReportHeader;
	}

	public ReportParameters getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(ReportParameters reportParameters) {
		this.reportParameters = reportParameters;
	}

	protected String replaceWithActualValue(String cellContent) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				reportProperties
						.getProperty(ReportPropertyConstants.DATE_FORMAT));
		cellContent = cellContent.replace("${EndTime}",
				dateFormat.format(getReportParameters().getEndTime()));
		cellContent = cellContent.replace("${StartTime}",
				dateFormat.format(getReportParameters().getStartTime()));
		cellContent = cellContent.replace("${ReportGenerationDate}",
				dateFormat.format(new Date()));
		cellContent = cellContent.replace("${UserName}", getReportParameters()
				.getUserName());
		
		cellContent = cellContent.replace("${SubscriberStatus}", StringUtils.isNotBlank(getReportParameters().getSubscriberStatusText()) ? getReportParameters().getSubscriberStatusText() : "(ALL)");
		
		cellContent = cellContent.replace("${PocketTemplateDescription}", StringUtils.isNotBlank(getReportParameters().getPocketTemplateDescription()) ? getReportParameters().getPocketTemplateDescription() : "(ALL)");
		
		cellContent = cellContent.replace("${SubscriberRestrictions}", StringUtils.isNotBlank(getReportParameters().getSubscriberRestrictionsText()) ? getReportParameters().getSubscriberRestrictionsText() : "(NONE)");
		
		cellContent = cellContent.replace("${SourceMDN}", StringUtils.isNotBlank(getReportParameters().getSourceMdn()) ? getReportParameters().getSourceMdn() : "");
		
		cellContent = cellContent.replace("${TransactionType}", StringUtils.isNotBlank(getReportParameters().getTransactionTypeText()) ? getReportParameters().getTransactionTypeText() : "(ALL)");
		
		cellContent = cellContent.replace("${TransactionStatus}", StringUtils.isNotBlank(getReportParameters().getTransactionStatusText()) ? getReportParameters().getTransactionStatusText() : "(ALL)");
		
		cellContent = cellContent.replace("${DestinationPocketStatus}", StringUtils.isNotBlank(getReportParameters().getDestinationPocketStatusText()) ? getReportParameters().getDestinationPocketStatusText() : "(ALL)");
		
		cellContent = cellContent.replace("${DestinationMDN}", StringUtils.isNotBlank(getReportParameters().getDestMdn()) ? getReportParameters().getDestMdn() : "");
		
		cellContent = cellContent.replace("${PartnerCode}", StringUtils.isNotBlank(getReportParameters().getPartnerCode()) ? getReportParameters().getPartnerCode() : "");
		
		cellContent = cellContent.replace("${BillerCode}", StringUtils.isNotBlank(getReportParameters().getBillerCode()) ? getReportParameters().getBillerCode() : "");
		
		cellContent = cellContent.replace("${PartnerType}", StringUtils.isNotBlank(getReportParameters().getPartnerTypeText()) ? getReportParameters().getPartnerTypeText() : "(ALL)");
		
		cellContent = cellContent.replace("${SettlementStatus}", StringUtils.isNotBlank(getReportParameters().getSettlementStatusText()) ? getReportParameters().getSettlementStatusText() : "(ALL)");
		
		cellContent = cellContent.replace("${CSRUserName}", StringUtils.isNotBlank(getReportParameters().getCsrUserName()) ? getReportParameters().getCsrUserName() : "");
		
		cellContent = cellContent.replace("${IDCardNo}", StringUtils.isNotBlank(getReportParameters().getIdNumber()) ? getReportParameters().getIdNumber() : "");
		
		cellContent = cellContent.replace("${MDN}", StringUtils.isNotBlank(getReportParameters().getMdn()) ? getReportParameters().getMdn() : "");
		
		cellContent = cellContent.replace("${MerchantID}", StringUtils.isNotBlank(getReportParameters().getMerchantId()) ? getReportParameters().getMerchantId() : "");
		
		cellContent = cellContent.replace("${MerchantAccount}", StringUtils.isNotBlank(getReportParameters().getMerchantAccount()) ? getReportParameters().getMerchantAccount() : "");
		
		cellContent = cellContent.replace("${ReferenceNo}", StringUtils.isNotBlank(getReportParameters().getReferenceNo()) ? getReportParameters().getReferenceNo() : "");
		
		cellContent = cellContent.replace("${FromLastUpdateTime}", (getReportParameters().getFromUpdatedDate() != null) ? dateFormat.format(getReportParameters().getFromUpdatedDate()) : "");
		
		cellContent = cellContent.replace("${ToLastUpdateTime}", (getReportParameters().getToUpdatedDate() != null) ? dateFormat.format(getReportParameters().getToUpdatedDate()) : "");
		
		
		return cellContent;
	}

}

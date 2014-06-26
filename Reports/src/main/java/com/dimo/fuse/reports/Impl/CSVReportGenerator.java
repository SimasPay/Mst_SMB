package com.dimo.fuse.reports.Impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.ReportGenerator;
import com.dimo.fuse.reports.ReportPropertyConstants;

/**
 * 
 * @author Amar
 * 
 */
public class CSVReportGenerator extends ReportGenerator {

	FileWriter writer;
	private final String DEFAULT_DELIMITER = ",";

	private static Logger log = LoggerFactory
			.getLogger(CSVReportGenerator.class);

	@Override
	public void createDocument() {
		File file = getReportFilePath();
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			log.error("Failed to create a document", e);
		}

	}

	@Override
	public void openDocument() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeDocument() {
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			log.error("Error in closing the document", e);
		}

	}

	@Override
	public void addLogo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPageHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPageFooter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addColumnHeaders() {
		try {
			log.info("Creating column headers...");
			JSONArray headerRowArray = reportProperties
					.getJSONArray(ReportPropertyConstants.HEADER_COLUMNS);
			if (headerRowArray != null) {
				for (int i = 0; i < headerRowArray.length(); i++) {
					if (i != 0) {
						writer.append(getCsvDelimiter());
					}
					writer.append(headerRowArray.getString(i));
				}
				writer.append('\n');
			}
		} catch (JSONException e) {
			log.error("Failed to read JSON object", e);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	@Override
	public void addReportHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addReportFooter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRowContent(String[] rowContent) {
		try {
			for (int i = 0; i < rowContent.length; i++) {
				if (i != 0) {
					writer.append(getCsvDelimiter());
				}
				writer.append(rowContent[i]);
			}
			writer.append('\n');
		} catch (IOException e) {
			log.error("IOEXception occurred. " + e.getMessage());
		}

	}

	protected String getCsvDelimiter() {
		String delimiter = reportProperties
				.getProperty(ReportPropertyConstants.CSV_DELIMITER);
		if (StringUtils.isNotBlank(delimiter)) {
			return delimiter;
		}
		return DEFAULT_DELIMITER;
	}

}

package com.dimo.fuse.reports.Impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.ReportGenerator;
import com.dimo.fuse.reports.ReportPropertyConstants;
import com.dimo.fuse.reports.scheduler.ReportSchedulerProperties;

/**
 * 
 * @author Amar
 * 
 */
public class ExcelReportGenerator extends ReportGenerator {

	Workbook workbook;
	Sheet sheet;
	int nofColumns;
	int rowCount = 0;
	int dataRowCount = 0;
	private static final int DEFAULT_COLUMN_WIDTH = 20;

	private static Logger log = LoggerFactory
			.getLogger(ExcelReportGenerator.class);

	@Override
	public void createDocument() {

		String extension = FilenameUtils.getExtension(getReportFilePath()
				.getName());
		log.info("Creating an excel report " + getReportFilePath());
		if (extension.contains("xlsx")) {
			workbook = new SXSSFWorkbook();
		} else {
			workbook = new HSSFWorkbook();
		}
		if (reportProperties
				.getProperty(ReportPropertyConstants.NAME_OF_THE_REPORT) != null) {
			sheet = workbook.createSheet(reportProperties
					.getProperty(ReportPropertyConstants.NAME_OF_THE_REPORT));
		} else {
			sheet = workbook.createSheet();
		}
	}

	@Override
	public void openDocument() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeDocument() {

		FileOutputStream out;
		try {
			out = new FileOutputStream(getReportFilePath());
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void addLogo() {
		try {
			CellRangeAddress region = new CellRangeAddress(rowCount, rowCount+2, 0,
					getNumberOfHeaderColumns() - 1);
			sheet.addMergedRegion(region);
			
			InputStream is = this.getClass().getResourceAsStream(
					reportProperties.getProperty(ReportPropertyConstants.HEADER_LOGO));
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			is.close();
			CreationHelper helper = workbook.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(getHeaderLogoAlignment());
			anchor.setRow1(0);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize();
			rowCount = 3;
		} catch (Exception e) {
			log.warn("Failed to load Logo. " + e.getMessage(), e);
		}
	}
	
	private int getNumberOfHeaderColumns () {
		int result = 0;
		try {
			JSONArray headerRowArray = reportProperties
						.getJSONArray(ReportPropertyConstants.HEADER_COLUMNS);
			if (headerRowArray != null) {
				result = headerRowArray.length();
			}
		} catch (JSONException e) {
			log.warn("Failed to load Json array for Header columns." + e.getMessage(), e);
		}
		return result;
	}
	
	private int getHeaderLogoAlignment() {
		int result = getNumberOfHeaderColumns();
		String alignment = reportProperties.getProperty(ReportPropertyConstants.HEADER_LOGO_ALIGNMENT);
		if(alignment.equalsIgnoreCase("center") || alignment.equalsIgnoreCase("centre")){
			result = result /2;
		} else if(alignment.equalsIgnoreCase("left")){
			result = 0;
		} else if(alignment.equalsIgnoreCase("right")){
			result = result - 1;
		} else{
			result = 0;
		}
		return result;
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
		sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);
		Row rowhead = createRow();
		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		// font.setFontHeight((short) 250);
		font.setFontName("Arial");
		cellStyle.setFont(font);
		rowhead.setRowStyle(cellStyle);
		try {
			JSONArray headerRowArray = reportProperties
					.getJSONArray(ReportPropertyConstants.HEADER_COLUMNS);
			if (headerRowArray != null) {
				log.info("Creating Column Headers. No of header columns:"
						+ headerRowArray.length());
				this.nofColumns = headerRowArray.length();
				if (this.nofColumns != 0) {

					for (int i = 0; i < headerRowArray.length(); i++) {
						Cell headerCell = rowhead.createCell(i);
						headerCell.setCellValue(headerRowArray.getString(i));
						headerCell.setCellStyle(cellStyle);
					}
				}
			}
		} catch (JSONException e) {
			log.error("Error reading a JSON Object", e);
		}

		rowhead.setRowStyle(cellStyle);
	}

	@Override
	public void addReportHeader() {
		try {

			CellRangeAddress region = new CellRangeAddress(
					rowCount,
					rowCount,
					0,
					reportProperties.getJSONArray(
							ReportPropertyConstants.HEADER_COLUMNS).length() - 1);
			sheet.addMergedRegion(region);

			Row rowhead = createRow();
			rowhead.setHeight((short) 500);
			CellStyle cellStyle = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			font.setFontHeight((short) 350);
			font.setFontName("Arial");
			font.setColor(IndexedColors.WHITE.getIndex());
			cellStyle.setFont(font);
			cellStyle.setFillBackgroundColor(IndexedColors.DARK_RED.getIndex());
			cellStyle.setFillForegroundColor(Font.COLOR_RED);
			cellStyle.setFillPattern(CellStyle.FINE_DOTS);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			Cell headerCell = rowhead.createCell(0);
			String headerText = reportProperties
					.getProperty(ReportPropertyConstants.REPORT_HEADER_TEXT) != null ? reportProperties
					.getProperty(ReportPropertyConstants.REPORT_HEADER_TEXT)
					: "";
			headerCell.setCellValue(headerText);
			headerCell.setCellStyle(cellStyle);
			log.info("Adding a report header with text " + headerText);
			//if(!getReportParameters().isScheduledReport())
			//{
				addMetaData(ReportPropertyConstants.REPORT_HEADER);
			//}
			
		} catch (JSONException e) {
			log.error("Error reading a json array", e);
		}

	}

	private void addMetaData(String blockType) {
		try {
			int nofcolumnsInDataTable = reportProperties.getJSONArray(
					ReportPropertyConstants.HEADER_COLUMNS).length();
			int nofColumns = getMaxNoColumnsInReportHeaderFooter(blockType);
			JSONArray headerFooterRows = reportProperties
					.getJSONArray(blockType);
			int columnWidth = nofcolumnsInDataTable / nofColumns != 0 ? nofcolumnsInDataTable
					/ nofColumns
					: 1;
			for (int i = 0; i < headerFooterRows.length(); i++) {
				JSONArray headerRow = headerFooterRows.getJSONArray(i);
				Row row = sheet.createRow(rowCount);
				;

				for (int j = 0; j < headerRow.length(); j = j + 2) {
					String name = headerRow.getString(j);
					createMergedRegionAndInsertData(row, j, columnWidth, name
							+ " :");

					String value = headerRow.getString(j + 1);
					value = replaceWithActualValue(value);
					createMergedRegionAndInsertData(row, j + 1, columnWidth,
							value);
				}
				if (nofColumns > headerRow.length()) {
					for (int j = headerRow.length(); j < nofColumns; j++) {
						CellRangeAddress region = new CellRangeAddress(
								rowCount, rowCount + 1, j * columnWidth, j
										* columnWidth + columnWidth - 1);
						sheet.addMergedRegion(region);
					}
				}
				rowCount += 2;
			}
			addEmptyRow();

		} catch (JSONException e) {
			log.error("Error in reading a json object. " + e.getMessage());
		}

	}

	private void createMergedRegionAndInsertData(Row row, int columnIndex,
			int columnWidth, String data) {

		CellRangeAddress region = new CellRangeAddress(rowCount, rowCount + 1,
				columnIndex * columnWidth, columnIndex * columnWidth
						+ columnWidth - 1);
		sheet.addMergedRegion(region);

		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeight((short) 230);
		font.setFontName("Arial");
		cellStyle.setFont(font);

		Cell cell = row.createCell(columnIndex * columnWidth);
		cell.setCellValue(data);
		cell.setCellStyle(cellStyle);

	}

	private void addEmptyRow() throws JSONException {
		CellRangeAddress region = new CellRangeAddress(rowCount, rowCount, 0,
				reportProperties.getJSONArray(
						ReportPropertyConstants.HEADER_COLUMNS).length() - 1);
		sheet.addMergedRegion(region);
		createRow();
	}

	@Override
	public void addReportFooter() {
		try {
			log.info("Adding Report Footer...");
			if (dataRowCount == 0) {
				addNoRecordsFoundMsg();
			}
			addEmptyRow();
			addMetaData(ReportPropertyConstants.REPORT_FOOTER);
			pageFooter();
		} catch (JSONException e) {
			log.error("Error while creating report footer. " + e.getMessage());
		}

	}

	private void addNoRecordsFoundMsg() {
		CellRangeAddress region = new CellRangeAddress(rowCount, rowCount, 0,
				getNumberOfHeaderColumns() - 1);
		sheet.addMergedRegion(region);

		Row rowhead = createRow();
		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontName("Arial");
		cellStyle.setFont(font);
		Cell headerCell = rowhead.createCell(0);
		String footerText  = "No Records Found";
		headerCell.setCellValue(footerText);
		headerCell.setCellStyle(cellStyle);
	}
	
	private void pageFooter() {
		CellRangeAddress region = new CellRangeAddress(rowCount, rowCount, 0,
				getNumberOfHeaderColumns() - 1);
		sheet.addMergedRegion(region);

		Row rowhead = createRow();
		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontName("Arial");
		cellStyle.setFont(font);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		Cell headerCell = rowhead.createCell(0);
		String footerText  = ReportSchedulerProperties.getReportFooterText();
		if (StringUtils.isBlank(footerText)) {
			footerText = reportProperties.getProperty(ReportPropertyConstants.PAGE_FOOTER_TEXT);
		}
		headerCell.setCellValue(footerText);
		headerCell.setCellStyle(cellStyle);
	}

	@Override
	public void addRowContent(String[] rowContent) {

		Row row = createRow();
		for (int i = 0; i < rowContent.length; i++) {
			row.createCell(i).setCellValue(rowContent[i]);
		}
		dataRowCount++;
	}

	private Row createRow() {
		Row row = sheet.createRow(rowCount);
		rowCount++;
		return row;
	}

}

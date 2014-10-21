package com.dimo.fuse.reports.Impl;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.ReportGenerator;
import com.dimo.fuse.reports.ReportPropertyConstants;
import com.dimo.fuse.reports.ReportTool;
import com.dimo.fuse.reports.scheduler.ReportSchedulerProperties;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * @author Amar
 * 
 */
public class PDFReportGenerator extends ReportGenerator {
	Document document;
	PdfWriter pdfwriter;
	PdfPTable headerTable;
	PdfPTable footerTable;
	PdfPTable transactionTable;
	Image headerLogoImage;
	PdfPCell cell;

	int numColumns;
	int rowCount = 0;
	private static Logger log = LoggerFactory
			.getLogger(PDFReportGenerator.class);

	private final Rectangle DEFAULT_PAGE_SIZE = PageSize.A2;
	
	private final int DEFAULT_HEADER_LOGO_ALIGNMENT = Element.ALIGN_CENTER;

	@Override
	public void createDocument() {
		try {
			log.info("creating a pdf document:" + getReportFilePath());
			this.document = new Document(getPageSize());
			this.pdfwriter = PdfWriter.getInstance(this.document,
					new FileOutputStream(getReportFilePath()));
			this.document.setMargins(20, 20, 40, 40);
		} catch (Exception e) {
			log.error("Failed to create pdf report", e);
		}

	}

	@Override
	public void closeDocument() {

		try {
			this.document.add(headerTable);
			if (this.rowCount == 0) {
				this.cell = new PdfPCell(new Phrase("No Records Found"));
				this.cell.setColspan(this.numColumns);
				this.transactionTable.addCell(this.cell);
			}
			this.document.add(transactionTable);
			this.document.add(footerTable);
			this.document.close();
		} catch (Exception e) {
			log.error("Failed to close pdf report", e);
		}
	}

	@Override
	public void addLogo() {
		try {
			InputStream is = this.getClass().getResourceAsStream(
					reportProperties
							.getProperty(ReportPropertyConstants.HEADER_LOGO));
			byte[] bytes = IOUtils.toByteArray(is);
			Image image = Image.getInstance(bytes);
			image.scaleAbsolute(100, 0);
			image.scalePercent(60);
			int headerLogoAlignment = getHeaderLogoAlignment();
			image.setAlignment(headerLogoAlignment);			
			this.document.add(image);
		} catch (Exception e) {
			log.warn("Failed to load Logo. " + e.getMessage());
		}

	}

	private int getHeaderLogoAlignment() {
		String alignment = reportProperties.getProperty(ReportPropertyConstants.HEADER_LOGO_ALIGNMENT);
		if(alignment.equalsIgnoreCase("center") || alignment.equalsIgnoreCase("centre")){
			return Element.ALIGN_CENTER;
		} else if(alignment.equalsIgnoreCase("left")){
			return Element.ALIGN_LEFT;
		} else if(alignment.equalsIgnoreCase("right")){
			return Element.ALIGN_RIGHT;
		} else{
			return DEFAULT_HEADER_LOGO_ALIGNMENT;
		}
	}

	@Override
	public void addPageHeader() {
		String headerText = reportProperties
				.getProperty(ReportPropertyConstants.PAGE_HEADER_TEXT);
		log.info("Adding Page Header with text " + headerText);
		if (StringUtils.isNotBlank(headerText)) {
			HeaderFooter headerFooter = new HeaderFooter(
					new Phrase(headerText), false);
			headerFooter.setAlignment(Element.ALIGN_CENTER);
			headerFooter.setBorder(0);
			this.document.setHeader(headerFooter);
		}
	}

	@Override
	public void addPageFooter() {
		String footerText  = ReportSchedulerProperties.getReportFooterText();
		if (StringUtils.isBlank(footerText)) {
			footerText = reportProperties
				.getProperty(ReportPropertyConstants.PAGE_FOOTER_TEXT);
		}
		log.info("Adding Page Footer with text " + footerText);
		if (StringUtils.isNotBlank(footerText)) {
			HeaderFooter headerFooter = new HeaderFooter(
					new Phrase(footerText), false);
			headerFooter.setAlignment(Element.ALIGN_CENTER);
			headerFooter.setBorder(0);
			this.document.setFooter(headerFooter);
		}
	}

	@Override
	public void addColumnHeaders() {
		try {
			JSONArray headerRowArray = reportProperties
					.getJSONArray(ReportPropertyConstants.HEADER_COLUMNS);
			log.info("Creating Column Headers. No of header columns:"
					+ headerRowArray.length());
			if (headerRowArray != null) {
				this.numColumns = headerRowArray.length();
				if (this.numColumns != 0) {
					this.transactionTable = new PdfPTable(this.numColumns);
					this.transactionTable.setLockedWidth(true);
					this.transactionTable.setTotalWidth(this.document
							.getPageSize().getWidth());
					this.transactionTable.setHeaderRows(1);
					JSONArray widths = reportProperties
							.getJSONArray(ReportPropertyConstants.HEADER_COLUMNS_WIDTH);
					int[] headerWidths = ReportTool.convertToIntArray(widths);
					if (headerWidths != null && headerWidths.length != 0) {
						this.transactionTable.setWidths(headerWidths);
					}
				}
				for (int i = 0; i < headerRowArray.length(); i++) {
					addHeaderCellToTransactionTable(headerRowArray.getString(i));
				}
			}
		} catch (JSONException e) {
			log.error("Failed to read JSON Array.", e);
		} catch (DocumentException e) {
			log.error("Failed to set column widths to transaction table.", e);
		}

	}

	private void addHeaderCellToTransactionTable(String content) {
		this.cell = new PdfPCell(new Phrase(new Chunk(content,
				FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD,
						new Color(0, 0, 0)))));
		this.cell.setBackgroundColor(Color.LIGHT_GRAY);
		this.cell.setBorderWidth(0.1f);
		this.cell.setPaddingLeft(5.0f);
		this.cell.setPaddingTop(10.0f);
		this.cell.setPaddingBottom(10.0f);
		this.cell.setPaddingRight(5.0f);
		this.cell.setVerticalAlignment(Element.ALIGN_CENTER);
		this.cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		this.cell.setBorderColor(Color.GRAY);
		this.transactionTable.addCell(this.cell);

	}

	@Override
	public void addRowContent(String[] rowContent) {
		for (int i = 0; i < rowContent.length; i++) {
			this.cell = new PdfPCell(new Phrase(new Chunk(rowContent[i],
					FontFactory.getFont(FontFactory.HELVETICA, 6, new Color(0,
							0, 0)))));
			this.cell.setVerticalAlignment(Element.ALIGN_CENTER);
			this.cell.setFixedHeight(25.0f);
			this.cell.setBorderWidth(0.1f);
			this.cell.setPaddingTop(10.0f);
			this.cell.setPaddingBottom(10.0f);
			this.cell.setPaddingLeft(5.0f);
			this.cell.setPaddingRight(5.0f);
			this.cell.setBorderColor(Color.LIGHT_GRAY);
			this.transactionTable.addCell(this.cell);
			rowCount++;
		}
		if (this.numColumns > rowContent.length) {
			for (int i = rowContent.length; i < this.numColumns; i++) {
				this.cell = new PdfPCell(new Phrase(" "));
				this.transactionTable.addCell(this.cell);
			}
		}
	}

	@Override
	public void addReportHeader() {
		log.info("Creating Report Header...");
		this.headerTable = new PdfPTable(1);
		this.headerTable.setLockedWidth(true);
		this.headerTable.setTotalWidth(this.document.getPageSize().getWidth());

		String headerText = reportProperties
				.getProperty(ReportPropertyConstants.REPORT_HEADER_TEXT) != null ? reportProperties
				.getProperty(ReportPropertyConstants.REPORT_HEADER_TEXT) : "";
		Phrase phrase = new Phrase(18, new Chunk(headerText,
				FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD,
						new Color(255, 255, 255))));
		PdfPCell headerCell = new PdfPCell(phrase);
		headerCell.setBackgroundColor(new Color(153, 0, 0));
		headerCell.setBorder(0);
		headerCell.setPaddingTop(10.0f);
		headerCell.setPaddingBottom(10.0f);
		headerCell.setPaddingLeft(20.0f);
		headerCell.setColspan(4);
		headerCell.setVerticalAlignment(Element.ALIGN_CENTER);
		headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		this.headerTable.addCell(headerCell);
		try {
			this.document.add(this.headerTable);
			//if(!getReportParameters().isScheduledReport())
			//{
				addMetaData(ReportPropertyConstants.REPORT_HEADER,
						ReportPropertyConstants.REPORT_HEADER_DATA_COLUMNS_WIDTH);
			//}
		} catch (DocumentException e) {
			log.error("Error while creating report header. " + e.getMessage());
		}

	}

	@Override
	public void addReportFooter() {
		try {
			addMetaData(ReportPropertyConstants.REPORT_FOOTER,
					ReportPropertyConstants.REPORT_FOOTER_DATA_COLUMNS_WIDTH);
		} catch (DocumentException e) {
			log.error("Error while creating report footer. " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param headerFooterTable
	 * @param blockType
	 *            - It can be either of "ReportHeader" or "ReportFooter".
	 * @param columnsWidthProperty
	 *            - It can be either of "ReportHeaderColumnsWidth" or
	 *            "ReportFooterColumnsWidth"
	 * @throws DocumentException
	 */
	public void addMetaData(String blockType, String columnsWidthProperty)
			throws DocumentException {
		int nofColumns = getMaxNoColumnsInReportHeaderFooter(blockType);
		PdfPTable headerFooterTable = new PdfPTable(nofColumns);
		headerFooterTable.setLockedWidth(true);
		headerFooterTable.setTotalWidth(this.document.getPageSize().getWidth());

		try {

			JSONArray widths = reportProperties
					.getJSONArray(columnsWidthProperty);
			int[] columnWidths = ReportTool.convertToIntArray(widths);
			if (columnWidths != null && columnWidths.length != 0) {
				headerFooterTable.setWidths(columnWidths);
			}
			addEmptyCellToTable(headerFooterTable, nofColumns, 3.0f);

			JSONArray headerRows = reportProperties.getJSONArray(blockType);
			for (int i = 0; i < headerRows.length(); i++) {
				JSONArray headerRow = headerRows.getJSONArray(i);
				for (int j = 0; j < headerRow.length(); j = j + 2) {
					String name = headerRow.getString(j);
					String value = headerRow.getString(j + 1);
					value = replaceWithActualValue(value);
					addCellToReportHeaderFooterTable(headerFooterTable, name,
							value);
				}
				if (nofColumns > headerRow.length()) {
					for (int j = headerRow.length(); j < nofColumns; j++) {
						this.cell = new PdfPCell(new Phrase(" "));
						this.cell.setBorder(0);
						headerFooterTable.addCell(this.cell);
					}
				}
			}
			addEmptyCellToTable(headerFooterTable, nofColumns, 3.0f);
			if (ReportPropertyConstants.REPORT_HEADER.equals(blockType)) {
				this.headerTable = headerFooterTable;
				log.info("created a header table with " + columnWidths.length
						+ "columns");
			} else {
				this.footerTable = headerFooterTable;
				log.info("created a footer table with" + columnWidths.length
						+ "columns");
			}
		} catch (JSONException e) {
			log.error("Error occured while reading a JSon object", e);
		}

	}

	private void addEmptyCellToTable(PdfPTable table, int colSpan,
			float fixedHeight) {
		PdfPCell emptyCell = new PdfPCell(new Phrase(""));
		emptyCell.setColspan(colSpan);
		emptyCell.setBorder(0);
		emptyCell.setFixedHeight(fixedHeight);
		table.addCell(emptyCell);
	}

	private void addCellToReportHeaderFooterTable(PdfPTable table,
			String colName, String colValue) {
		Phrase namePhrase = new Phrase(18, new Chunk(colName,
				FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD)));
		PdfPCell nameCell = new PdfPCell(new Phrase(namePhrase));
		nameCell.setPaddingTop(5.0f);
		nameCell.setPaddingBottom(5.0f);
		nameCell.setBorder(0);
		nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nameCell.setPaddingLeft(10.0f);
		table.addCell(nameCell);

		Phrase valuePhrase = new Phrase(18, new Chunk(": " + colValue,
				FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD)));
		PdfPCell valueCell = new PdfPCell(new Phrase(valuePhrase));
		valueCell.setPaddingTop(5.0f);
		valueCell.setPaddingBottom(5.0f);
		valueCell.setBorder(0);
		valueCell.setPaddingLeft(10.0f);
		valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(valueCell);
	}

	@Override
	public void openDocument() {
		this.document.open();
	}

	public Rectangle getPageSize() {

		Rectangle rectangle;
		String pageSize = reportProperties
				.getProperty(ReportPropertyConstants.PAGE_SIZE);

		try {
			rectangle = PageSize.getRectangle(pageSize);
		} catch (Exception e) {
			rectangle = DEFAULT_PAGE_SIZE;
		}

		String orientation = reportProperties
				.getProperty(ReportPropertyConstants.PAGE_ORIENTATION);
		if (StringUtils.isNotBlank(orientation)) {
			if (orientation.equalsIgnoreCase("landscape")
					&& rectangle.getWidth() < rectangle.getHeight()) {
				return rectangle.rotate();
			}

			if (orientation.equalsIgnoreCase("portrait")
					&& rectangle.getWidth() > rectangle.getHeight()) {
				return rectangle.rotate();
			}
		}
		log.info("Page Size of the PDF:" + rectangle.toString());
		return rectangle;
	}

}

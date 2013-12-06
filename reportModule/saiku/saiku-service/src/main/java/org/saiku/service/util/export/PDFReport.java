package org.saiku.service.util.export;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PDFReport {

	Document document;
	PdfWriter pdfwriter;
	PdfPTable table;
	PdfPCell cell;
	PDFReportEvents events;
	String filePath;
	String reportTitle;
	Image chartImage;
	int numColumns;
	int numRows;
	int colIndex;
	int rowIndex = 7;
	int headerRows = 0;

	public PDFReport(ByteArrayOutputStream baos, String reportTitle)
			throws DocumentException {
		this.filePath = "";
		this.reportTitle = reportTitle;
		this.document = new Document(PageSize.A3);
		this.pdfwriter = PdfWriter.getInstance(this.document, baos);
		this.events = new PDFReportEvents();
		this.pdfwriter.setBoxSize("art", PageSize.A3);
		this.pdfwriter.setPageEvent(this.events);
		this.document.open();

	}

	public void createTable(int headerRows, int numColumns) {
		if (numColumns != 0) {
			this.numColumns = numColumns;
			this.headerRows = headerRows;
			this.table = new PdfPTable(numColumns);
			this.table.setHeaderRows(headerRows + 2);
		}
	}

	public void addLogoAndTitle() throws BadElementException, IOException {
		addLogo();
		addReportTitle();
	}

	public void addRow(String[] row) {
		if (this.headerRows != 0) {
			for (int j = 0; j < row.length; j++) {
				addHeaderCell(row[j]);
			}
			this.headerRows--;
		} else {
			for (int j = 0; j < row.length; j++) {
				addCell(row[j]);
			}
		}

	}

	public void addHeaderCell(String cellContent) {
		this.cell = new PdfPCell(new Phrase(cellContent));
		this.cell.setBackgroundColor(Color.LIGHT_GRAY);
		this.table.addCell(this.cell);
	}

	public void addCell(String cellContent) {
		this.cell = new PdfPCell(new Phrase(cellContent));
		this.table.addCell(this.cell);
	}

	public void closePdfReport() throws DocumentException {
		this.document.add(this.table);
		this.document.close();

	}

	private void addReportTitle() {
		Font font = new Font(Font.TIMES_ROMAN, 16);
		// if (this.reportTitle == null || this.reportTitle == "") {
		// this.reportTitle = "No Title Set for this report";
		// }
		Phrase phrase = new Phrase(this.reportTitle, font);
		this.cell = new PdfPCell(phrase);
		this.cell.setColspan(this.numColumns);
		this.cell.setBorder(0);
		this.cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		this.cell.setPadding(10);
		this.table.addCell(this.cell);

	}

	public void addLogo() throws IOException, BadElementException {
		Logger log = LoggerFactory.getLogger(PDFReport.class);
		try {
			String imagePath = ".." + File.separator + "webapps"
					+ File.separator + "saiku" + File.separator + "images"
					+ File.separator + "logo.png";
			// Image image = Image
			// .getInstance("..\\webapps\\saiku\\images\\logo.png");
			log.info("ImagePath:" + imagePath);
			Image image = Image.getInstance(imagePath);
			image.scaleAbsolute(100, 30);
			// image.scalePercent(50);
			PdfPCell cell = new PdfPCell(image);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(this.numColumns);
			cell.setBorder(0);
			this.table.addCell(cell);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

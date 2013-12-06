package com.mfino.report.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


public class SpecialPDFReport {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	Document document;
	PdfWriter pdfwriter;
	PdfPTable table;
	PdfPCell cell;
	PDFReportEvents events;
	File filePath;
	String reportTitle;
	int numColumns;
	int numRows;
	int colIndex;
	int rowIndex = 7;

	public SpecialPDFReport(File filePath, String reportTitle)
	{
		try{
			this.filePath = filePath;
			this.reportTitle = reportTitle;
			this.document = new Document(PageSize.A2);
			this.pdfwriter = PdfWriter.getInstance(this.document,
					new FileOutputStream(filePath));

			this.events = new PDFReportEvents();
			this.pdfwriter.setBoxSize("art", PageSize.A2);
			this.pdfwriter.setPageEvent(this.events);
			this.document.open();

		}catch(Exception e){
			log.error("PDFReport: Failed to create pdf report",e);
		}

	}

	private void addLogo() {
		try{
			InputStream is = this.getClass().getResourceAsStream("/logo.png");
			byte[] bytes = IOUtils.toByteArray(is);
			Image image = Image.getInstance(bytes);
			image.scaleAbsolute(100, 30);
			PdfPCell cell = new PdfPCell(image);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(this.numColumns);
			cell.setBorder(0);
			this.table.addCell(cell);
		}catch(Exception e){
			log.error("PDFReport: Failed to load Logo",e);
		}

	}

	public void addHeaderRow(String headerRow) {
		String[] headerRowArray = headerRow.split(",");
		this.numColumns = headerRowArray.length;
		if (this.numColumns != 0) {
			this.table = new PdfPTable(this.numColumns);
			this.table.setHeaderRows(3);
		}
		addLogo();
		addReportTitle();
		for (String element : headerRowArray) {
			this.cell = new PdfPCell(new Phrase(element));
			this.cell.setBackgroundColor(Color.LIGHT_GRAY);
			this.table.addCell(this.cell);
		}

	}

	public void addRowContent(String rowContent) {
		String[] rowcontentArray = rowContent.split(",");
		for (String element : rowcontentArray) {

			if(element.equalsIgnoreCase("empty")){
				this.cell = new PdfPCell(new Phrase("       "));
				this.cell.setBorder(0);
			}else{
				this.cell = new PdfPCell(new Phrase(element));
			}
			this.table.addCell(this.cell);
		}

	}

	public void closePdfReport() {
		this.document.close();
	}

	private void addReportTitle() {
		Font font = new Font(Font.TIMES_ROMAN, 16);
		Phrase phrase = new Phrase(this.reportTitle, font);
		this.cell = new PdfPCell(phrase);
		this.cell.setColspan(this.numColumns);
		this.cell.setBorder(0);
		this.cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		this.cell.setPadding(10);
		this.table.addCell(this.cell);

	}

	public void createTable(int numColumns) {
		this.numColumns = numColumns;
		if (this.numColumns != 0) {
			this.table = new PdfPTable(this.numColumns);
			this.table.setHeaderRows(2);
		}
		addLogo();
		addReportTitle();

	}

	public void createAnotherTable(String tableTitle,int numColumns){
		this.numColumns = numColumns;
		if (this.numColumns != 0) {
			this.table = new PdfPTable(this.numColumns);
			//	this.table.setHeaderRows(3);
		}
		Font font = new Font(Font.TIMES_ROMAN, 12);
		Phrase phrase = new Phrase(tableTitle, font);
		this.cell = new PdfPCell(phrase);
		this.cell.setColspan(this.numColumns);
		this.cell.setBorder(0);
		this.cell.setPadding(2);
		this.table.addCell(this.cell);

		//		this.cell = new PdfPCell(new Phrase(tableTitle));
		//		this.cell.setBorder(0);
		//		this.cell.setColspan(numColumns);
		//		this.table.addCell(this.cell);
	}

	public void addHeaderTable(){
		this.numColumns = 1;
		this.table = new PdfPTable(1);
		//	this.table.setHeaderRows(2);
		addLogo();
		addReportTitle();
	}

	public void addTableToPdf(){
		try{
			if(this.table!=null){
				this.document.add(this.table);
			}
		}catch(Exception e){
			log.error("SpecialPdfReport: Failed to add pdf Table",e);
		}
	}

	public void addSpecialRow(String rowContent,int rowSpan){
		String[] rowcontentArray = rowContent.split(",");
		for (String element : rowcontentArray) {
			this.cell = new PdfPCell(new Phrase(element));
			this.cell.setRowspan(rowSpan);
			this.table.addCell(this.cell);
			rowSpan = 1;
		}
	}

	public void addEmptyRow(int numColumns){
		for(int i = 0; i < numColumns; i++){
			this.cell = new PdfPCell(new Phrase(""));
			this.cell.setBorder(0);
			this.table.addCell(this.cell);
		}
	}

	public static void main(String[] args) {
		formatingTest();
		// secondFormatingTest();
	}

	public static void formatingTest()  {
		String tit = "Kin Information Missing Account Report";
		String hr = "#,SubscriberMdn,AccountID,AccountType,Status";
		String r1 = "1,2343242,32,UnBanked,Retired";
		String r2 = "1,2343242,32,UnBanked,Retired";

		String filePath = "src/main/resources/table1.pdf";
		File fl = new File(filePath);
		System.out.println(fl.getAbsolutePath());

		SpecialPDFReport pdf = new SpecialPDFReport(fl, tit);
		pdf.addHeaderTable();
		pdf.addTableToPdf();
		pdf.createAnotherTable("Table1", 5);
		pdf.addRowContent(r1);
		pdf.addRowContent(r2);
		pdf.addTableToPdf();

		// for (int i = 0; i < 50; i++) {
		// pdfReport.addRowContent(r1);
		// pdfReport.addRowContent(r2);
		// }

		pdf.closePdfReport();
		System.out.println("Finished");
	}

	//
	// public static void secondFormatingTest() throws IOException,
	// DocumentException {
	// String tit = "Kin Information Missing Account Report";
	// String hr =
	// "#,Agent Code,MDN,Agent Name,Total transactions,Cash-in Transactions,Cash-in Amount,Cash-Out Transactions,Cash-Out Amount,Bill Payments,Bill Payment amount,Registrations Done,Total  Service charge earned,Total WHT Paid ";
	// String r1 =
	// "14,102,234100102,100102,32,0,0,32,300.0000,0,0,2,22.5800,1.6700";
	//
	// String filePath = "res/table2.pdf";
	//
	// PDFReport pdfReport = new PDFReport(filePath, tit);
	//
	// pdfReport.addHeaderRow(hr);
	//
	// for (int i = 0; i < 50; i++) {
	// pdfReport.addRowContent(r1);
	// pdfReport.addRowContent(r1);
	// }
	//
	// pdfReport.closePdfReport();
	//
	// }
}

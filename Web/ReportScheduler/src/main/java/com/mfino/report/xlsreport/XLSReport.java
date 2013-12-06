package com.mfino.report.xlsreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.lowagie.text.BadElementException;
import com.mfino.constants.ReportParameterKeys;
import com.mfino.report.pdf.PDFReport;
import com.mfino.service.ReportParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ExcelUtil;
import com.mfino.util.ReportUtil;

public class XLSReport {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	SXSSFWorkbook hwb;
	Sheet sheet;
	int cols;
	int rowIndex = 7;
	Row row;
	Font boldfont;
	Font font;
	CellStyle dataCellstyle;
	CellStyle headerCellstyle;
	PDFReport pdfReport;
	int rowCount = 0;

	public XLSReport(String reportName, Date end){
		hwb=new SXSSFWorkbook();
		sheet =  hwb.createSheet(reportName);
		sheet.setDisplayGridlines(false);
		sheet.setPrintGridlines(false);
		boldfont =ExcelUtil.getFont(hwb, true, reportName); 
		font = ExcelUtil.getFont(hwb, false, reportName);
		dataCellstyle = createDataCellStyle();
		headerCellstyle = createHeaderCellStyle();
		PrintSetup printerSetup = sheet.getPrintSetup();
		printerSetup.setHeaderMargin(ReportParametersService.getInteger(ReportParameterKeys.REPORT_HEADER_MARGIN));
		printerSetup.setFooterMargin(ReportParametersService.getInteger(ReportParameterKeys.REPORT_FOOTER_MARGIN));
		printerSetup.setLandscape(ReportParametersService.isLandscape(reportName));
		File pdfReportFilePath =ReportUtil.getReportFilePath(reportName,end,ReportUtil.PDF_EXTENTION);
		try{
			pdfReport = new PDFReport(pdfReportFilePath, reportName);
		}catch(Exception e){
			log.error("Failed to Create Pdf Report",e);
		}
	}

	private CellStyle createHeaderCellStyle() {
		CellStyle style = hwb.createCellStyle();
		style.setFont(boldfont);
		style.setBorderLeft(CellStyle.BORDER_DOTTED);
		style.setBorderTop(CellStyle.BORDER_DOTTED);
		style.setBorderRight(CellStyle.BORDER_DOTTED);
		style.setBorderBottom(CellStyle.BORDER_DOTTED);
		style.setWrapText(true);
		return style;
	}

	private CellStyle createDataCellStyle() {
		CellStyle style = hwb.createCellStyle();
		style.setFont(font);
		style.setBorderLeft(CellStyle.BORDER_DOTTED);
		style.setBorderTop(CellStyle.BORDER_DOTTED);
		style.setBorderRight(CellStyle.BORDER_DOTTED);
		style.setBorderBottom(CellStyle.BORDER_DOTTED);
		style.setWrapText(true);
		return style;
	}

	//adding logo
	public void addLogo() throws IOException, BadElementException{
		InputStream is = this.getClass().getResourceAsStream("/logo.png");
		byte[] bytes = IOUtils.toByteArray(is);
		int pictureIdx = hwb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
		is.close();
		CreationHelper helper = hwb.getCreationHelper();
		Drawing drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(0);
		anchor.setRow1(0);
		Picture pict = drawing.createPicture(anchor, pictureIdx);
		pict.resize();
	}

	public void addMergedRegion(){
		sheet.addMergedRegion(new CellRangeAddress(0,3,0,2));
	}

	public void addReportTitle(String reportTitle){	
		row=   sheet.createRow(1);	
		ExcelUtil.createHeaderCell(hwb, row, 3).setCellValue(reportTitle);	
	}

	public void addHeaderRow(String headerRow){
		String[] headerRowArray = headerRow.split(",");
		//ExcelUtil.autoSizeColumn(hwb, headerRowArray.length);
		cols = headerRowArray.length;
		row =   sheet.createRow(rowIndex++);
		for(int i=0; i<headerRowArray.length; i++)
		{
			Cell cell = row.createCell( i);
			cell.setCellStyle(headerCellstyle);
			cell.setCellValue(headerRowArray[i]);
		}
		try{
			pdfReport.addHeaderRow(headerRow);
		}catch(Exception e){
			log.error("Failed to Create Pdf Report",e);
		}
	}


	public void addRowContent(String rowContent){	
		String[] rowcontentArray = rowContent.split(",");
		row =  sheet.createRow(rowIndex++);
		for(int i=0; i<rowcontentArray.length; i++)
		{
			Cell cell = row.createCell( i);
			cell.setCellStyle(dataCellstyle);
			cell.setCellValue(rowcontentArray[i]);
		}
		rowCount++;
		pdfReport.addRowContent(rowContent);

	}
	
	public void addContent(String rowContent){	
		String[] rowcontentArray = rowContent.split(",");
		row =  sheet.createRow(rowIndex++);
		for(int i=0; i<rowcontentArray.length; i++)
		{
			Cell cell = row.createCell( i);
			cell.setCellStyle(dataCellstyle);
			cell.setCellValue(rowcontentArray[i]);
		}
		pdfReport.addContent(rowContent);

	}

	public static void convertXlsTopdf(File inputFile, File outputFile) throws ConnectException {		
		// connect to an OpenOffice.org instance running on port 8100
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(ConfigurationUtil.getOpenOfficePort());
		connection.connect();

		// convert
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
		converter.convert(inputFile, outputFile);

		// close the connection
		connection.disconnect();
	}
	
	public void addNoRecordsFoundRow(){	
		row =  sheet.createRow(rowIndex++);
		Cell cell = row.createCell(0);
		cell.setCellStyle(dataCellstyle);
		cell.setCellValue("No Records Found");
		rowCount++;	
	}
	
	public void writeToFileStream(File report, String headerRow, String reportName){
		if(rowCount == 0){
			addNoRecordsFoundRow();
		}
		try{
			log.info("writing workbook to file"+report);
			//uncomment if condition to check total no.of cells
			//		long totalcells =  (rowIndex-1)*cols;
			//		if(totalcells<Short.MAX_VALUE){
			String[] headerRowArray = headerRow.split(",");
			for(int i=0; i<cols; i++)
			{
				//sheet.autoSizeColumn(i);
				int columnWidth = ReportParametersService.getColumnWidth(reportName, headerRowArray[i]);
				sheet.setColumnWidth(i, columnWidth);
			}
			//		}
			FileOutputStream fs = new FileOutputStream(report);
			hwb.write(fs);
			fs.close();

			//convert to Pdf		
			String xlsFileName = report.getAbsolutePath();
			int extIndex = xlsFileName.indexOf(".xls");
			String fileNameNoExt = xlsFileName.substring(0,extIndex);
			String pdfFileName = fileNameNoExt+".pdf";
			//*FindbugsChange*
	    	// Previous -- Removed the line "File outputFile = new File(pdfFileName);"
			pdfReport.closePdfReport();
			//	convertXlsTopdf(report,outputFile);
		}catch (Exception e) {
			log.error("Exception"+e);
		}
	}

	public static void main(String args[]) throws IOException{
		// *FindbugsChange*
    	// Previous -- Commented out the line "File rep = new File("D:\\Report\\20120110\\klm.xls");"
		//File rep = new File("D:\\Report\\20120110\\klm.xls");
		//		XLSReport xls = new XLSReport("klm_xls");
		//xls.writeToFileStream(rep,"klm_xls");

	}


}

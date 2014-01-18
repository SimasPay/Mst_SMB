package com.mfino.transactionapi.util;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
public class PDFDocument {

	private static Logger log = LoggerFactory.getLogger(PDFDocument.class);
	Document document;
	PdfWriter pdfwriter;
	PdfPTable table;
	PdfPTable transactionTable;
	PdfPCell cell;
	//PDFReportEvents events;
	File filePath;
	int numColumns;
	int numRows;
	int colIndex;
	int rowIndex = 7;
	int rowCount = 0;
	Integer language = new Integer(0); 
	
	public PDFDocument(File filePath, String encryptionKey)
	{
		try{
			this.filePath = filePath;
			this.document = new Document(PageSize.A4);
			this.pdfwriter = PdfWriter.getInstance(this.document,
					new FileOutputStream(filePath));

			pdfwriter.setEncryption(encryptionKey.getBytes(), null, PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
			//this.events = new PDFReportEvents();
			//this.pdfwriter.setBoxSize("art", PageSize.A4);
			//this.pdfwriter.setPageEvent(this.events);
			
			this.document.setMargins(0, 0, 0, 36);
			
			HeaderFooter headerFooter = new HeaderFooter(new Phrase(ConfigurationUtil.getPdfHistoryFooter()), false);
			headerFooter.setAlignment(Element.ALIGN_CENTER);
			headerFooter.setBorder(0);
			this.document.setFooter(headerFooter);
			
			this.document.open();

		}catch(Exception e){
			log.error("PDFReport: Failed to create pdf report",e);
		}

	}

	public void addLogo() {
		try{
			
			//PdfPTable logoTable = new PdfPTable(1);			
			
			//InputStream is = this.getClass().getResourceAsStream("/logo.png");
			InputStream is = this.getClass().getResourceAsStream("/../../images/pdf_header_logo.png");
			byte[] bytes = IOUtils.toByteArray(is);
			Image image = Image.getInstance(bytes);
			image.scaleAbsolute(100, 0);
			image.scalePercent(60);
//			PdfPCell cell = new PdfPCell(image);
//			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			cell.setBorder(PdfPCell.LEFT);
//			logoTable.addCell(image);
			this.document.add(image);
		}catch(Exception e){
			log.error("PDFReport: Failed to load Logo",e);
		}

	}
	
	public void addSubscriberDetails(TransactionDetails txnDetails, SubscriberMDN subscriberMDN, Pocket pocket)throws IOException, DocumentException 
	{
		this.table = new PdfPTable(2);
		
		int[] widths = {3,2};
		this.table.setWidths(widths);
		this.table.setHorizontalAlignment(Element.ALIGN_LEFT);
		Integer Language = subscriberMDN.getSubscriber().getLanguage();
		
		String title;
		String name;
		String cardName;
		String currentBalance;
		String mdn;
		String date;
		String period;
		String transactionDetails;
		String cardNo;
		if(Language.equals(CmFinoFIX.Language_Bahasa))
		{
			title = "Transaksi Uangku";
			name = "Nama          : ";
			cardName = "Nama Kartu  : ";
			currentBalance = "Saldo Saat ini  : ";
			mdn = "Nomor Ponsel  : ";
			date = "Tangal Cetak    : ";
			period = "Periode       : ";
			transactionDetails = "Rincian Transaksi" ;	
			cardNo = "Nomor Kartu : ";
		}
		else
		{
			title = "Uanku E-Statement";
			name = "Name           : ";
			cardName = "Card Name  : ";
			currentBalance = "Current Balance : ";
			mdn = "Mobile No     : ";
			date = "Print Date      : ";
			period = "Period          : ";
			transactionDetails = "Transaction Details" ;
			cardNo = "Card Number : ";
		}
		
		
		Phrase phrase = new Phrase(18, new Chunk(title, FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new Color(0, 0, 0))));
		PdfPCell headerCell = new PdfPCell(phrase);
		headerCell.setBackgroundColor(Color.LIGHT_GRAY);
		headerCell.setBorder(0);
		headerCell.setPaddingTop(10.0f);
		headerCell.setPaddingBottom(10.0f);
		headerCell.setColspan(2);
		this.table.addCell(headerCell);
		
		String emptyString = "";
		PdfPCell emptyCell = new PdfPCell(new Phrase(emptyString));
//		emptyCell.setBackgroundColor(Color.LIGHT_GRAY);
//		emptyCell.setBorder(0);
//		this.table.addCell(emptyCell);
		
		
		name = name + (subscriberMDN.getSubscriber().getNickname() != null ? subscriberMDN.getSubscriber().getNickname() : "");
		PdfPCell nameCell = new PdfPCell(new Phrase(name));
		nameCell.setBorder(0);
		this.table.addCell(nameCell);
		
		currentBalance = currentBalance + pocket.getCurrentBalance();
		PdfPCell currentBalanceCell = new PdfPCell(new Phrase(currentBalance));
		currentBalanceCell.setBorder(0);
		this.table.addCell(currentBalanceCell);
		
		mdn = mdn + txnDetails.getSourceMDN();
		PdfPCell mdnCell = new PdfPCell(new Phrase(mdn));
		mdnCell.setBorder(0);
		this.table.addCell(mdnCell);
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		date = date + dateFormat.format(new Date());
		PdfPCell dateCell = new PdfPCell(new Phrase(date));
		dateCell.setBorder(0);
		this.table.addCell(dateCell);
		
		period = period + dateFormat.format(txnDetails.getFromDate()) + "-" + dateFormat.format(txnDetails.getToDate());
		PdfPCell periodCell = new PdfPCell(new Phrase(period));
		periodCell.setBorder(0);
		this.table.addCell(periodCell);
		
		emptyCell.setBackgroundColor(Color.WHITE);
		this.table.addCell(emptyCell);
		
		this.table.addCell(emptyCell);
		this.table.addCell(emptyCell);
		this.table.addCell(emptyCell);
		this.table.addCell(emptyCell);
		
		PdfPCell transactionDetailsCell = new PdfPCell(new Phrase(transactionDetails));
		transactionDetailsCell.setBorder(0);
		transactionDetailsCell.setPaddingBottom(10.0f);
		this.table.addCell(transactionDetailsCell);		
		this.table.addCell(emptyCell);		
		this.document.add(this.table);
	}

	
	private void addSubscriberDetailsHeader(TransactionDetails txnDetails, SubscriberMDN subscriberMDN, Pocket pocket)throws IOException, DocumentException
	{
		this.table = new PdfPTable(4); 
		this.table.setLockedWidth(true);
		this.table.setTotalWidth(this.document.getPageSize().getWidth());
		int[] widths = {1,2,1,1};
     	this.table.setWidths(widths);
		language = subscriberMDN.getSubscriber().getLanguage();
				
		addEmptyCellToSubscriberDetailsTable(this.table, 4, 0.1f);
		
		Phrase phrase = new Phrase(18, new Chunk(getTitle(txnDetails), FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new Color(0, 0, 0))));
		PdfPCell headerCell = new PdfPCell(phrase);
		headerCell.setBackgroundColor(Color.LIGHT_GRAY);
		headerCell.setBorder(0);
		headerCell.setPaddingTop(10.0f);
		headerCell.setPaddingBottom(10.0f);
		headerCell.setPaddingLeft(20.0f);
		headerCell.setColspan(4);
		this.table.addCell(headerCell);
		
	}
	
	private void addSubscriberDetailsSpecificToNFCTxnHistory(TransactionDetails txnDetails, Pocket pocket)
	{
		String cardName = "Card Name";
		String currentBalance = "Current Balance";
		String cardNo = "Card Number";
		
		String cardNameVal = pocket.getCardAlias() != null ? pocket.getCardAlias() : "";
		String cardPANVal = pocket.getCardPAN();
		
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, cardName), cardNameVal);
		String currentBalanceVal;
		if(txnDetails.getAmount() != null)
		{
			currentBalanceVal = "Rp. " + MfinoUtil.getNumberFormat().format(txnDetails.getAmount());
		}
		else
		{
			currentBalanceVal =  LanguageTranslator.translate(language, "Not Available");
		}
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, currentBalance), currentBalanceVal);
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, cardNo), cardPANVal);
	}
	
	private void addSubscriberDetailsSpecificToEmoneyTxnHistory(TransactionDetails txnDetails, SubscriberMDN subscriberMDN, Pocket pocket)
	{
		String currentBalance = "Current Balance";
		String 	name = "Name";
		String mdn = "Mobile No";
		
		String firstName = subscriberMDN.getSubscriber().getFirstName();
		String lastName = subscriberMDN.getSubscriber().getLastName();
		String nameVal = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
//		if(StringUtils.isBlank(nameVal))
//		{
//			nameVal = subscriberMDN.getSubscriber().getNickname() != null ? subscriberMDN.getSubscriber().getNickname() : "";
//		}
		String currentBalanceVal = "Rp. ";
		currentBalanceVal += MfinoUtil.getNumberFormat().format(pocket.getCurrentBalance());
					
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, name), nameVal);
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, currentBalance), currentBalanceVal);
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, mdn), txnDetails.getSourceMDN());
		
	}
	private void addOtherSubscriberDetails(TransactionDetails txnDetails, SubscriberMDN subscriberMDN, Pocket pocket)throws IOException, DocumentException
	{

		String date,period,transactionDetails;

		date = "Print Date";
		period = "Period";
		transactionDetails = "Transaction Details" ;

		SimpleDateFormat dateFormat = new SimpleDateFormat(ConfigurationUtil.getPdfHistoryDateFormat());
		String dateVal = dateFormat.format(new Date());
		String periodVal = dateFormat.format(txnDetails.getFromDate()) + " to " + dateFormat.format(txnDetails.getToDate());
		
		if(txnDetails.getServiceName().equals(ServiceAndTransactionConstants.SERVICE_NFC))
		{
			addSubscriberDetailsSpecificToNFCTxnHistory(txnDetails, pocket);	
		}
		else
		{
			addSubscriberDetailsSpecificToEmoneyTxnHistory(txnDetails, subscriberMDN, pocket);	
		}
		
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, date), dateVal);
		addCellToSubscriberDetailsTable(this.table, LanguageTranslator.translate(language, period), periodVal);
		addEmptyCellToSubscriberDetailsTable(this.table, 2, 10.0f);
		addEmptyCellToSubscriberDetailsTable(this.table, 4, 10.0f);
		
		PdfPCell transactionDetailsCell = new PdfPCell(new Phrase(LanguageTranslator.translate(language, transactionDetails)));
		transactionDetailsCell.setBorder(0);
		transactionDetailsCell.setPaddingTop(10.0f);
		transactionDetailsCell.setPaddingBottom(5.0f);
		transactionDetailsCell.setColspan(4);
		transactionDetailsCell.setPaddingLeft(20.0f);
		this.table.addCell(transactionDetailsCell);
		this.document.add(this.table);
	}
	
	private String getTitle(TransactionDetails txnDetails)
	{
		String title;
		if(txnDetails.getServiceName().equals(ServiceAndTransactionConstants.SERVICE_NFC))
		{
			title = "NFC Card Transactions";
		}
		else
		{
			title = "Uangku E-Statement";
		}
		return LanguageTranslator.translate(language, title);
	}
	
	public void addSubscriberDetailsTable(TransactionDetails txnDetails, SubscriberMDN subscriberMDN, Pocket pocket)throws IOException, DocumentException 
	{
		
		addSubscriberDetailsHeader(txnDetails, subscriberMDN, pocket);
		addOtherSubscriberDetails(txnDetails, subscriberMDN, pocket);

	}
	
	public void addCellToSubscriberDetailsTable(PdfPTable subTable, String colName, String colValue){
		PdfPCell nameCell = new PdfPCell(new Phrase(colName));
		nameCell.setPaddingTop(5.0f);
		nameCell.setPaddingBottom(5.0f);
		nameCell.setBorder(0);
		nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nameCell.setPaddingLeft(20.0f);
		subTable.addCell(nameCell);
		
		PdfPCell valueCell = new PdfPCell(new Phrase(": "+colValue));
		valueCell.setPaddingTop(5.0f);
		valueCell.setPaddingBottom(5.0f);
		valueCell.setBorder(0);
		valueCell.setPaddingLeft(20.0f);
		valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		subTable.addCell(valueCell);
	}
	
	public void addEmptyCellToSubscriberDetailsTable(PdfPTable subTable, int colSpan, float fixedHeight){
		PdfPCell emptyCell = new PdfPCell(new Phrase(""));
		emptyCell.setColspan(colSpan);
		emptyCell.setBorder(0);
		emptyCell.setFixedHeight(fixedHeight);
		subTable.addCell(emptyCell);
	}
	
	public void addHeaderRow(String headerRow) throws IOException, DocumentException {
		String[] headerRowArray = headerRow.split("\\|");
		this.numColumns = headerRowArray.length;
		if (this.numColumns != 0) {
			this.transactionTable = new PdfPTable(this.numColumns);
			this.transactionTable.setLockedWidth(true);
			this.transactionTable.setTotalWidth(this.document.getPageSize().getWidth());
			this.transactionTable.setHeaderRows(1);
			int[] widths = {1,2,1};
			this.transactionTable.setWidths(widths);
		}
		for(int i=0; i<headerRowArray.length-1; i++){
			this.cell = new PdfPCell( new Phrase(18, new Chunk(headerRowArray[i], FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new Color(0, 0, 0)))));
			this.cell.setBackgroundColor(Color.LIGHT_GRAY);
			//this.cell.setFixedHeight(25.0f);
			this.cell.setBorderWidth(0.1f);
			this.cell.setPaddingLeft(20.0f);
			this.cell.setPaddingTop(10.0f);
			this.cell.setPaddingBottom(10.0f);
			this.cell.setVerticalAlignment(Element.ALIGN_CENTER);
			this.cell.setBorderColor(Color.GRAY);
			this.transactionTable.addCell(this.cell);
		}
		this.cell = new PdfPCell( new Phrase(18, new Chunk(headerRowArray[headerRowArray.length-1], FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new Color(0, 0, 0)))));
		this.cell.setBackgroundColor(Color.LIGHT_GRAY);
		//this.cell.setFixedHeight(25.0f);
		this.cell.setVerticalAlignment(Element.ALIGN_CENTER);
		this.cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		this.cell.setBorderWidth(0.1f);
		this.cell.setPaddingTop(10.0f);
		this.cell.setPaddingBottom(10.0f);
		this.cell.setPaddingRight(20.0f);
		this.cell.setBorderColor(Color.GRAY);
		this.transactionTable.addCell(this.cell);
//		for (String element : headerRowArray) {
//			this.cell = new PdfPCell( new Phrase(18, new Chunk(element, FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new Color(0, 0, 0)))));
//			
//			this.cell.setBackgroundColor(Color.LIGHT_GRAY);
//			this.transactionTable.addCell(this.cell);
//		}

	}

	public void addRowContent(String rowContent) {
		String[] rowcontentArray = rowContent.split("\\|");
		for(int i=0; i<rowcontentArray.length; i++){
//			if(localTxnNames != null && language.equals(CmFinoFIX.Language_Bahasa) && i == 1){
//				this.cell = new PdfPCell(new Phrase(getLocalTxnName(rowcontentArray[i])));
//			}else{
//				this.cell = new PdfPCell(new Phrase(rowcontentArray[i]));
//			}
			this.cell = new PdfPCell(new Phrase(rowcontentArray[i]));
			if(i == (rowcontentArray.length-1)){
				this.cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				this.cell.setPaddingRight(20.0f);
			}else{
				this.cell.setPaddingLeft(20.0f);
			}
			this.cell.setVerticalAlignment(Element.ALIGN_CENTER);
			this.cell.setFixedHeight(25.0f);
			this.cell.setBorderWidth(0.1f);
			this.cell.setPaddingTop(10.0f);
			this.cell.setPaddingBottom(10.0f);
			this.cell.setBorderColor(Color.LIGHT_GRAY);
			this.transactionTable.addCell(this.cell);
			rowCount++;
		}
		if(this.numColumns>rowcontentArray.length){
			for (int i=rowcontentArray.length;i<this.numColumns;i++) {
				this.cell = new PdfPCell(new Phrase(" "));
				this.transactionTable.addCell(this.cell);
			}
		}
	}

	public void closePdfReport() {
		if(this.rowCount == 0){
			this.cell = new PdfPCell(new Phrase("No Records Found"));
			this.cell.setColspan(this.numColumns);
			this.transactionTable.addCell(this.cell);
		}
		try{
			//this.document.add(this.table);
			this.document.add(this.transactionTable);
			this.document.close();
		}catch(Exception e){
			log.error("PDFReport: Failed to close pdf report",e);
		}

	}
	
	
	public static Date getDate(String dateStr) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		dateFormat.setLenient(false);
		Date dateOfBirth;
		dateOfBirth = dateFormat.parse(dateStr);

		return dateOfBirth;
	}
	
	
}

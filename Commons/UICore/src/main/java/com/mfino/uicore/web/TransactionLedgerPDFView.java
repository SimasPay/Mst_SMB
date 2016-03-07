package com.mfino.uicore.web;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.CommodityTransferProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

@Component("TransactionLedgerPDFView")
public class TransactionLedgerPDFView extends AbstractPdfView{

	private Logger log = LoggerFactory.getLogger(this.getClass());
    private DateFormat df = DateUtil.getExcelDateFormat();
    
    @Autowired
    @Qualifier("CommodityTransferProcessorImpl")
    private CommodityTransferProcessor commodityTransferProcessor;
	
    void initializeTableHeader(PdfPTable table, int currentRow) {
    	try{

        	
    		Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 11);
    		PdfPCell cell;
    		  
    		cell=new PdfPCell(new Phrase("Reference ID", titleFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		table.addCell(cell);
            
    		cell=new PdfPCell(new Phrase("Date", titleFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell=new PdfPCell(new Phrase("Transaction Type", titleFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell=new PdfPCell(new Phrase("Internal Txn Type", titleFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell=new PdfPCell(new Phrase("Credit Amount", titleFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell( cell);

            cell=new PdfPCell(new Phrase("Debit Amount", titleFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell=new PdfPCell(new Phrase("Status", titleFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell( cell);
            
            
            /*
            table.addCell( new Phrase("Commodity", titleFont));

            table.addCell( new Phrase("Channel Name", titleFont));

            table.addCell(new Phrase("Transfer ID", titleFont));
            table.addCell( new Phrase("Bank RRN", titleFont));

            table.addCell(new Phrase("To", titleFont));
            
            table.addCell(new Phrase("From MDN", titleFont));

            table.addCell(new Phrase("To/From PocketID", titleFont));
            
            table.addCell( new Phrase("Opening Balance", titleFont));

            table.addCell( new Phrase("Closing Balance", titleFont));
            */
            
                        
           
        
    	}catch(Exception e){
    		e.printStackTrace();
    		log.error("Error in creating header row of table :",e);
    	}
    }
    
    private void fillTableContent(CMJSCommodityTransfer.CGEntries jsCommodity, PdfPTable table) {
    	try{
    		Locale locale = new Locale("in");
            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
    		//MfinoUtil.getNumberFormat();
    		 

    		Font contentFont = FontFactory.getFont(FontFactory.TIMES, 10);
    		PdfPCell cell;
    		
        	String refId = jsCommodity.getServiceChargeTransactionLogID()!=null ? jsCommodity.getServiceChargeTransactionLogID().toString() : ""; 

        	cell=new PdfPCell(new Phrase(refId, contentFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(cell);
        	
        	table.addCell(new Phrase(df.format(jsCommodity.getStartTime()), contentFont));    	
        	table.addCell(new Phrase(jsCommodity.getTransactionTypeText(), contentFont));
        	table.addCell(new Phrase(StringUtils.isNotEmpty(jsCommodity.getInternalTxnType()) ? jsCommodity.getInternalTxnType() : StringUtils.EMPTY, contentFont));
        	
        	String creditAmount="";
        	BigDecimal bd_creditAmount=jsCommodity.getCreditAmount()!=null?jsCommodity.getCreditAmount():null;
//        	System.out.println("cr :"+bd_creditAmount);
        	if(bd_creditAmount!=null ){
        		creditAmount=numberFormat.format(bd_creditAmount);
        		if(creditAmount!=null && !creditAmount.equals("") && !creditAmount.contains(",")){
        			creditAmount=creditAmount+",00";
        		}
        	}
        	
        	cell=new PdfPCell(new Phrase(creditAmount, contentFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(cell);
        	
        	String debitAmount="";
        	BigDecimal bd_debitAmount=jsCommodity.getDebitAmount()!=null?jsCommodity.getDebitAmount():null;
//        	System.out.println("dr:"+bd_debitAmount);
        	if(bd_debitAmount!=null){
        		debitAmount=numberFormat.format(bd_debitAmount);
        		if(debitAmount!=null && !debitAmount.equals("") && !debitAmount.contains(",")){
        			debitAmount=debitAmount+",00";
        		}
        	}
        	cell=new PdfPCell(new Phrase(debitAmount, contentFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(cell);
        	
        	cell=new PdfPCell(new Phrase(jsCommodity.getTransferStatusText(), contentFont));
    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(cell);
        	
        	
        	/*
        	 * 
        	table.addCell(jsCommodity.getCommodityText());
        	table.addCell(jsCommodity.getAccessMethodText());
        	
        	table.addCell(String.valueOf(jsCommodity.getTransactionID()));//transactionid
        	table.addCell(jsCommodity.getBankRetrievalReferenceNumber()!=null?jsCommodity.getBankRetrievalReferenceNumber():"");/brrn
        	
        	table.addCell(jsCommodity.getDestMDN());//to
        	table.addCell(jsCommodity.getSourceMDN());//from
        	table.addCell(jsCommodity.getCreditAmount()!=null?jsCommodity.getDestPocketID()!=null?jsCommodity.getDestPocketID().toString():"":jsCommodity.getSourcePocketID()!=null?jsCommodity.getSourcePocketID().toString():"");//pocketid
        	
        	table.addCell(jsCommodity.getCreditAmount()!=null?jsCommodity.getDestPocketBalance()!=null?jsCommodity.getDestPocketBalance().toString():"":jsCommodity.getSourcePocketBalance()!=null?jsCommodity.getSourcePocketBalance().toString():"");//openingbalance
        	table.addCell(jsCommodity.getCreditAmount()!=null?jsCommodity.getDestPocketClosingBalance()!=null?jsCommodity.getDestPocketClosingBalance().toString():"":jsCommodity.getSourcePocketClosingBalance()!=null?jsCommodity.getSourcePocketClosingBalance().toString():"");//closing balance
        	*/
        
    	}catch(Exception e){
    		e.printStackTrace();
    		log.error("Error in Creating rows of table : ",e);
    	}
    }


	@Override
	@SuppressWarnings("rawtypes")
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// TODO Auto-generated method stub
		
		try{


	    	response.setHeader("Content-Disposition", "attachment;filename=Transactions.pdf");
	    	CMJSCommodityTransfer jscommodity = new CMJSCommodityTransfer();
	        
	        String sourceDestnPocketID = request.getParameter(CmFinoFIX.CMJSCommodityTransfer.FieldName_SourceDestnPocketID);
	        if(sourceDestnPocketID!=null&&sourceDestnPocketID!=""){
	        	jscommodity.setSourceDestnPocketID(Long.valueOf(sourceDestnPocketID));
	        }
	        
	        String transferState = request.getParameter(CmFinoFIX.CMJSCommodityTransfer.FieldName_TransferState);
	        if(transferState!=null&&transferState!=""){
	        	jscommodity.setTransferState(Integer.valueOf(transferState));
	        }
	        
	        String transferStatus = request.getParameter(CmFinoFIX.CMJSCommodityTransfer.FieldName_TransactionsTransferStatus);
	        if(transferStatus!=null&&transferStatus!=""){
	        	jscommodity.setTransactionsTransferStatus(Integer.valueOf(transferStatus));
	        }
	        
	        String isMini = request.getParameter(CmFinoFIX.CMJSCommodityTransfer.FieldName_IsMiniStatementRequest);
	        if(isMini!=null&&isMini!=""){
	        	jscommodity.setIsMiniStatementRequest(Boolean.valueOf(isMini));
	        }
	        String startDate = request.getParameter(CMJSCommodityTransfer.FieldName_StartTime);
	        DateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
	        df.setTimeZone(TimeZone.getTimeZone("UTC"));
	        if (startDate != null && startDate.trim().length() > 0) {
	            Date stDate = df.parse(startDate);
	            Timestamp tstDate = new Timestamp(stDate);
	            jscommodity.setStartTime(tstDate);
	        }
	        String endDate = request.getParameter(CMJSCommodityTransfer.FieldName_EndTime);
	        if (endDate != null && endDate.trim().length() > 0) {
	            Date enDate = df.parse(endDate);
	            Timestamp tenDate = new Timestamp(enDate);
	            jscommodity.setEndTime(tenDate);
	        }
			jscommodity.setaction(CmFinoFIX.JSaction_Select);
//			jscommodity.setlimit(ConfigurationUtil.getExcelRowLimit());
			        
	        try {
	            Rectangle rect = new Rectangle(30, 30, 550, 800);
	            writer.setBoxSize("art", rect);
//	            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
//	            writer.setPageEvent(event);
	        	document.addTitle("Transactions");
	        	document.setMargins(10, 10, 20, 20);
	        	System.out.println("Writer : "+writer.getPageNumber());
	        	document.open();
	            int currentRow = 0;
	            int columnsLength=7;
	            PdfPTable table = new PdfPTable(columnsLength);
	            initializeTableHeader(table, currentRow);
	            System.out.println("Writer : "+writer.getPageNumber());
	            CMJSCommodityTransfer ctList = (CMJSCommodityTransfer)commodityTransferProcessor.process(jscommodity);
	            
	            if (ctList.getEntries() != null) {
		            for (CMJSCommodityTransfer.CGEntries ipEntry : ctList.getEntries()) {
		                currentRow++;
		                	fillTableContent(ipEntry, table);	
		                
		            }
	           }
	            
	            document.add(table);
	        } catch (Exception error) {
	        	error.printStackTrace();
	            log.error("Error in Integration Partner Excel Dowload ::"+error.getMessage(),error);
	        }
	    
			
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error in Creating Pdf :",e);
		}
		
	
		
	}
    public static void main(String[] args) throws DocumentException, MalformedURLException, IOException {
	Document document = new Document();
	FileOutputStream fos=new FileOutputStream("C://Users//Gopal//Desktop//HeaderFooter.pdf");
	PdfWriter writer = PdfWriter.getInstance(document, fos);
        Rectangle rect = new Rectangle(30, 30, 550, 800);
        writer.setBoxSize("art", rect);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);
        document.open();
        document.add(new Paragraph("This is Page One"));
        document.newPage();
        document.add(new Paragraph("This is Page two"));
        document.close();
	System.out.println("Done");
    }
	

}
 class HeaderFooterPageEvent extends PdfPageEventHelper {
    public void onStartPage(PdfWriter writer,Document document) {
    	Rectangle rect = writer.getBoxSize("art");
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Left"), rect.getLeft(), rect.getTop(), 0);
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Right:"+writer.getPageNumber()), rect.getRight(), rect.getTop(), 0);
    }
    public void onEndPage(PdfWriter writer,Document document) {
    	Rectangle rect = writer.getBoxSize("art");
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Bottom Left"), rect.getLeft(), rect.getBottom(), 0);
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Bottom Right"), rect.getRight(), rect.getBottom(), 0);
    }
}

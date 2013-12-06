package com.mfino.uicore.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.CommodityTransferProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 * @author sasidhar
 * Excel Report for Integration Partner list.
 */
@Component("TransactionLedgerExcelView")
public class TransactionLedgerExcelView extends AbstractExcelView {
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private DateFormat df = DateUtil.getExcelDateFormat();
    
    @Autowired
    @Qualifier("CommodityTransferProcessorImpl")
    private CommodityTransferProcessor commodityTransferProcessor;
    
    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "Reference ID");
        
        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "Transfer ID");
        
        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Bank RRN");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Date");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Transaction Type");
        
        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Internal Txn Type");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "To");
        
        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "From MDN");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "To/From PocketID");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Credit Amount");

        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "Debit Amount");

        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "Opening Balance");

        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "Closing Balance");
        
        HSSFCell header14 = getCell(sheet, currentRow, 13);
        setText(header14, "Status");
        
        HSSFCell header15 = getCell(sheet, currentRow, 14);
        setText(header15, "Commodity");

        HSSFCell header16 = getCell(sheet, currentRow, 15);
        setText(header16, "Channel Name");
        
       
    }

    private void fillPartnerCells(CMJSCommodityTransfer.CGEntries jsCommodity, HSSFRow row) {
    	String refId = jsCommodity.getServiceChargeTransactionLogID()!=null ? jsCommodity.getServiceChargeTransactionLogID().toString() : ""; 
    	row.createCell(0).setCellValue(refId);
    	row.createCell(1).setCellValue(jsCommodity.getTransactionID());
    	row.createCell(2).setCellValue(jsCommodity.getBankRetrievalReferenceNumber()!=null?jsCommodity.getBankRetrievalReferenceNumber():"");
    	row.createCell(3).setCellValue(df.format(jsCommodity.getStartTime()));    	
    	row.createCell(4).setCellValue(jsCommodity.getTransactionTypeText());
    	row.createCell(5).setCellValue(StringUtils.isNotEmpty(jsCommodity.getInternalTxnType()) ? jsCommodity.getInternalTxnType() : StringUtils.EMPTY);
    	row.createCell(6).setCellValue(jsCommodity.getDestMDN());
    	row.createCell(7).setCellValue(jsCommodity.getSourceMDN());
    	row.createCell(8).setCellValue(jsCommodity.getCreditAmount()!=null?jsCommodity.getDestPocketID()!=null?jsCommodity.getDestPocketID().toString():"":jsCommodity.getSourcePocketID()!=null?jsCommodity.getSourcePocketID().toString():"");
    	row.createCell(9).setCellValue(jsCommodity.getCreditAmount()!=null?jsCommodity.getCreditAmount().toString():"");
    	row.createCell(10).setCellValue(jsCommodity.getDebitAmount()!=null?jsCommodity.getDebitAmount().toString():"");
    	row.createCell(11).setCellValue(jsCommodity.getCreditAmount()!=null?jsCommodity.getDestPocketBalance()!=null?jsCommodity.getDestPocketBalance().toString():"":jsCommodity.getSourcePocketBalance()!=null?jsCommodity.getSourcePocketBalance().toString():"");
    	row.createCell(12).setCellValue(jsCommodity.getCreditAmount()!=null?jsCommodity.getDestPocketClosingBalance()!=null?jsCommodity.getDestPocketClosingBalance().toString():"":jsCommodity.getSourcePocketClosingBalance()!=null?jsCommodity.getSourcePocketClosingBalance().toString():"");
    	row.createCell(13).setCellValue(jsCommodity.getTransferStatusText());
    	row.createCell(14).setCellValue(jsCommodity.getCommodityText());
    	row.createCell(15).setCellValue(jsCommodity.getAccessMethodText());
    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=Transactions.xls");
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
		jscommodity.setlimit(ConfigurationUtil.getExcelRowLimit());
		        
        try {
            HSSFSheet sheet = workbook.createSheet("Transactions");
            sheet.setDefaultColumnWidth(18);
            int currentRow = 0;
            initializeWorkBook(sheet, currentRow);
            
            CMJSCommodityTransfer ctList = (CMJSCommodityTransfer)commodityTransferProcessor.process(jscommodity);

            if (ctList.getEntries() != null) {
	            for (CMJSCommodityTransfer.CGEntries ipEntry : ctList.getEntries()) {
	                currentRow++;
	                HSSFRow row = sheet.createRow(currentRow);
	                fillPartnerCells(ipEntry, row);
	            }
           }
        } catch (Exception error) {
            log.error("Error in Integration Partner Excel Dowload ::"+error.getMessage(),error);
        }
    }
}

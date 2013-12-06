package com.mfino.uicore.web;

import java.util.Map;

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
import com.mfino.fix.CmFinoFIX.CMJSBulkUploadEntry;
import com.mfino.uicore.fix.processor.BulkUploadEntryProcessor;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Bala Sunku
 */
@Component("BulkTransferExcelView")
public class BulkTransferExcelView extends AbstractExcelView {
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    @Qualifier("BulkUploadEntryProcessorImpl")
    private BulkUploadEntryProcessor bulkUploadEntryProcessor;
    
    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "Reference ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "First Name");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Last Name");
        
        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "MDN");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Amount");
        
        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Status");
        
        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Failure Reason");
    }

    private void fillPartnerCells(CMJSBulkUploadEntry.CGEntries entry, HSSFRow row) {
    	String RefId = entry.getServiceChargeTransactionLogID() != null ? entry.getServiceChargeTransactionLogID().toString() : "";
    	row.createCell(0).setCellValue(RefId);
    	row.createCell(1).setCellValue(entry.getFirstName());    	
    	row.createCell(2).setCellValue(entry.getLastName());
    	row.createCell(3).setCellValue(entry.getDestMDN());
    	row.createCell(4).setCellValue(entry.getAmount().toPlainString());
    	row.createCell(5).setCellValue(entry.getTransferStatusText());
    	row.createCell(6).setCellValue(entry.getFailureReason());
    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String filename = "BulkTransfer_" + request.getParameter(CmFinoFIX.CMJSBulkUploadEntry.FieldName_IDSearch) + ".xls";
        response.setHeader("Content-Disposition", "attachment;filename=" + filename );
    	CMJSBulkUploadEntry jsBulkUploadEntry = new CMJSBulkUploadEntry();
        
        String bulkUploadId = request.getParameter(CmFinoFIX.CMJSBulkUploadEntry.FieldName_IDSearch);
        if(StringUtils.isNotBlank(bulkUploadId)){
        	jsBulkUploadEntry.setIDSearch(Long.valueOf(bulkUploadId));
        }
        
		jsBulkUploadEntry.setaction(CmFinoFIX.JSaction_Select);
		jsBulkUploadEntry.setlimit(ConfigurationUtil.getExcelRowLimit());
        
        try {
            HSSFSheet sheet = workbook.createSheet("Transactions");
            sheet.setDefaultColumnWidth(6);
            int currentRow = 0;
            initializeWorkBook(sheet, currentRow);
            
            CMJSBulkUploadEntry ctList = (CMJSBulkUploadEntry)bulkUploadEntryProcessor.process(jsBulkUploadEntry);

            if (ctList.getEntries() != null) {
	            for (CMJSBulkUploadEntry.CGEntries ipEntry : ctList.getEntries()) {
	                currentRow++;
	                HSSFRow row = sheet.createRow(currentRow);
	                fillPartnerCells(ipEntry, row);
	            }
           }
        } catch (Exception error) {
            log.error("Error in Bulk Transfer Excel Downalod ::",error);
        }
    }
}

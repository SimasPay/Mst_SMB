/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import java.text.DateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkUploadEntry;
import com.mfino.uicore.fix.processor.BulkUploadEntryProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 *
 * @author sunil
 */
@Component("BulkUploadExcelView")
public class BulkUploadExcelView extends AbstractExcelView {

    private DateFormat df = DateUtil.getExcelDateFormat();
    
    @Autowired
    @Qualifier("BulkUploadEntryProcessorImpl")
    private BulkUploadEntryProcessor bulkUploadEntryProcessor;

    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "No");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "MDN");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Transaction Reference ID");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Amount");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Status");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Failure Reason");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Status Time");
    }

    private void fillBulkUploadCells(CMJSBulkUploadEntry.CGEntries bulkentry, HSSFRow row) {
        row.createCell(0).setCellValue(bulkentry.getLineNumber());
        row.createCell(1).setCellValue(bulkentry.getDestMDN());
        if (bulkentry.getTransferID() != null) {
            row.createCell(2).setCellValue(bulkentry.getTransferID());
        } else {
            row.createCell(2).setCellValue("--");
        }
        row.createCell(3).setCellValue(bulkentry.getAmount().doubleValue());
        String status = bulkentry.getTransferStatusText();
        Integer intStatus = bulkentry.getStatus();
        if (CmFinoFIX.TransferStatus_Completed.equals(intStatus) || CmFinoFIX.TransferStatus_Failed.equals(intStatus)) {
            row.createCell(4).setCellValue(status);
        } else {
            row.createCell(4).setCellValue("Pending");
        }
        if (bulkentry.getTransferFailureReason() != null) {
            row.createCell(5).setCellValue(bulkentry.getTransferFailureReasonText());
        } else {
            row.createCell(5).setCellValue("--");
        }
        if (bulkentry.getLastUpdateTime() != null) {
            row.createCell(6).setCellValue(df.format(bulkentry.getLastUpdateTime()));
        } else {
            row.createCell(6).setCellValue("--");
        }

    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=BulkRecords.xls");
    	CMJSBulkUploadEntry jsBUE = new CMJSBulkUploadEntry();
        String id = request.getParameter(CMJSBulkUploadEntry.FieldName_IDSearch);
        if (id != null && id.trim().length() > 0) {
            jsBUE.setIDSearch(Long.parseLong(id));
        }
        jsBUE.setlimit(ConfigurationUtil.getExcelRowLimit());
        jsBUE.setaction(CmFinoFIX.JSaction_Select);
        CMJSBulkUploadEntry processedList = (CMJSBulkUploadEntry) bulkUploadEntryProcessor.process(jsBUE);
        HSSFSheet sheet = workbook.createSheet("BulkUpload Excel Document");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;
        initializeWorkBook(sheet, currentRow);
        if (processedList.getEntries() != null) {
        for (CMJSBulkUploadEntry.CGEntries bulkentry : processedList.getEntries()) {
            currentRow++;
            HSSFRow row = sheet.createRow(currentRow);
            fillBulkUploadCells(bulkentry, row);
        }
    }
    }
}

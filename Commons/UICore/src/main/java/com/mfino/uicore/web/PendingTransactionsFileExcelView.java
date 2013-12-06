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
import com.mfino.fix.CmFinoFIX.CMJSPendingTransactionsFile;
import com.mfino.uicore.fix.processor.PendingTransactionsFileProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 *
 * @author Raju
 */
@Component("PendingTransactionsFileExcelView")
public class PendingTransactionsFileExcelView extends AbstractExcelView {

    private DateFormat df = DateUtil.getExcelDateFormat();    
    
    @Autowired
    @Qualifier("PendingTransactionsFileProcessorImpl")
    private PendingTransactionsFileProcessor pendingTransactionsFileProcessor;

    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "Line Count");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Resolve As");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Uploaded By");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Last Updated By");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Create Time");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Last Update Time");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "File Status");

    }

    private void fillPendingtTansactionsFileCells(CMJSPendingTransactionsFile.CGEntries ptfentry, HSSFRow row) {
        row.createCell(0).setCellValue(ptfentry.getID());
        row.createCell(1).setCellValue(ptfentry.getRecordCount());
        if (ptfentry.getResolveAs() != null) {
            row.createCell(2).setCellValue(ptfentry.getResolveAsText());
        } else {
            row.createCell(2).setCellValue("--");
        }
        if (ptfentry.getCreatedBy() != null) {
            row.createCell(3).setCellValue(ptfentry.getCreatedBy());
        }
        if (ptfentry.getUpdatedBy() != null) {
            row.createCell(4).setCellValue(ptfentry.getUpdatedBy());
        } else {
            row.createCell(4).setCellValue("Pending");
        }
        if (ptfentry.getCreateTime() != null) {
            row.createCell(5).setCellValue(df.format(ptfentry.getCreateTime()));
        } else {
            row.createCell(5).setCellValue("--");
        }
        if (ptfentry.getLastUpdateTime() != null) {
            row.createCell(6).setCellValue(df.format(ptfentry.getLastUpdateTime()));
        } else {
            row.createCell(6).setCellValue("--");
        }
        if (ptfentry.getUploadStatusText() != null) {
            row.createCell(7).setCellValue(ptfentry.getUploadStatusText());
        } else {
            row.createCell(7).setCellValue("--");
        }

    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=PendingTransactionsFile.xls");
    	CMJSPendingTransactionsFile jsptf = new CMJSPendingTransactionsFile();
        jsptf.setlimit(ConfigurationUtil.getExcelRowLimit());
        jsptf.setaction(CmFinoFIX.JSaction_Select);
        CMJSPendingTransactionsFile processedList = (CMJSPendingTransactionsFile) pendingTransactionsFileProcessor.process(jsptf);
        HSSFSheet sheet = workbook.createSheet("BulkUpload Excel Document");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;
        initializeWorkBook(sheet, currentRow);
        if (processedList.getEntries() != null) {
            for (CMJSPendingTransactionsFile.CGEntries ptfentry : processedList.getEntries()) {
                currentRow++;
                HSSFRow row = sheet.createRow(currentRow);
                fillPendingtTansactionsFileCells(ptfentry, row);
            }
        }
    }
}

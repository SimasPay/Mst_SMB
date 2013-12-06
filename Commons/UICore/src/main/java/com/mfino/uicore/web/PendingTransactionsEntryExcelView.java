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
import com.mfino.fix.CmFinoFIX.CMJSPendingTransactionsEntry;
import com.mfino.uicore.fix.processor.PendingTransactionsEntryProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 * 
 * @author Raju
 */
@Component("PendingTransactionsEntryExcelView")
public class PendingTransactionsEntryExcelView extends AbstractExcelView {

	private DateFormat df = DateUtil.getExcelDateFormat();
	
	@Autowired
	@Qualifier("PendingTransactionsEntryProcessorImpl")
	private PendingTransactionsEntryProcessor pendingTransactionsEntryProcessor;

	void initializeWorkBook(HSSFSheet sheet, int currentRow) {
		HSSFCell header1 = getCell(sheet, currentRow, 0);
		setText(header1, "Transfer ID");

		HSSFCell header2 = getCell(sheet, currentRow, 1);
		setText(header2, "Line Number");

		HSSFCell header3 = getCell(sheet, currentRow, 2);
		setText(header3, "Source MDN");

		HSSFCell header4 = getCell(sheet, currentRow, 3);
		setText(header4, "Dest MDN");

		HSSFCell header5 = getCell(sheet, currentRow, 4);
		setText(header5, "Amount");

		HSSFCell header6 = getCell(sheet, currentRow, 5);
		setText(header6, "ID");

		HSSFCell header7 = getCell(sheet, currentRow, 6);
		setText(header7, "Status");
          
		HSSFCell header8 = getCell(sheet, currentRow, 7);
		setText(header8, "Failure Reason");

		HSSFCell header9 = getCell(sheet, currentRow, 8);
		setText(header9, "Process Time");

	}

	private void fillBulkUploadCells(
			CMJSPendingTransactionsEntry.CGEntries pte, HSSFRow row) {
		if (pte.getTransferID() != null) {
			row.createCell(0).setCellValue(pte.getTransferID());
		}
		row.createCell(1).setCellValue(pte.getLineNumber());
		if (pte.getSourceMDN() != null) {
			row.createCell(2).setCellValue(pte.getSourceMDN());
		} else {
			row.createCell(2).setCellValue("--");
		}
		if (pte.getDestMDN() != null) {
			row.createCell(3).setCellValue(pte.getDestMDN());
		} else {
			row.createCell(3).setCellValue("--");
		}
		row.createCell(4).setCellValue(pte.getAmount().doubleValue());
		row.createCell(5).setCellValue(pte.getID());
		if (pte.getStatus() != null) {
			row.createCell(6).setCellValue(pte.getResolveStatusText());
		} else {
			row.createCell(6).setCellValue("--");
		}
		if(pte.getResolveFailureReason()!=null){
			row.createCell(7).setCellValue(pte.getResolveFailureReason());
		} else {
			row.createCell(7).setCellValue("--");
		}
		if (pte.getLastUpdateTime() != null) {
			row.createCell(8).setCellValue(df.format(pte.getLastUpdateTime()));
		} else {
			row.createCell(8).setCellValue("--");
		}
	}

	@SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment;filename=PendingTransactionsEntry.xls");
    	CMJSPendingTransactionsEntry jsPTE = new CMJSPendingTransactionsEntry();
		String pendingTransactionsFileID = request
				.getParameter(CMJSPendingTransactionsEntry.FieldName_PendingTransactionsFileID);
		if (pendingTransactionsFileID != null
				&& pendingTransactionsFileID.trim().length() > 0) {
			jsPTE.setPendingTransactionsFileID(Long
					.parseLong(pendingTransactionsFileID));
		}
		jsPTE.setlimit(ConfigurationUtil.getExcelRowLimit());
		jsPTE.setaction(CmFinoFIX.JSaction_Select);
		CMJSPendingTransactionsEntry processedList = (CMJSPendingTransactionsEntry) pendingTransactionsEntryProcessor
				.process(jsPTE);
		HSSFSheet sheet = workbook.createSheet("BulkUpload Excel Document");
		sheet.setDefaultColumnWidth(16);
		int currentRow = 0;
		initializeWorkBook(sheet, currentRow);
		if (processedList.getEntries() != null) {
			for (CMJSPendingTransactionsEntry.CGEntries bulkentry : processedList
					.getEntries()) {
				currentRow++;
				HSSFRow row = sheet.createRow(currentRow);
				fillBulkUploadCells(bulkentry, row);
			}
		}
	}
}

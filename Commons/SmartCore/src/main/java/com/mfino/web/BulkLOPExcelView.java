/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.web;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkLOP;
import com.mfino.fix.processor.BulkLOPProcessor;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.web.ExcelView;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author admin
 */
public class BulkLOPExcelView  extends ExcelView{
	private static BigDecimal ZERO = new BigDecimal(0);
	private static BigDecimal HUNDRED = new BigDecimal(100);
	private static final Integer start = 0;
    private DateFormat df = getDateFormat();

    private void fillLOPCells(CMJSBulkLOP.CGEntries lop, HSSFRow row) {
        row.createCell(0).setCellValue(lop.getID());
        row.createCell(1).setCellValue(lop.getGiroRefID());
        if (lop.getUsername() != null) {
            row.createCell(2).setCellValue(lop.getUsername());

        } else {
            row.createCell(2).setCellValue("");
        }
        row.createCell(3).setCellValue(lop.getTransferDate());
        row.createCell(4).setCellValue(lop.getActualAmountPaid().doubleValue());
        row.createCell(5).setCellValue(lop.getAmountDistributed().doubleValue());
        row.createCell(6).setCellValue(lop.getStatus());
        row.createCell(7).setCellValue(lop.getApprovedBy());
        if (lop.getCreateTime() != null) {
            row.createCell(8).setCellValue(df.format(lop.getCreateTime()));
        } else {
            row.createCell(8).setCellValue("");
        }
        if (lop.getApprovalTime() != null) {
            row.createCell(9).setCellValue(df.format(lop.getApprovalTime()));
        } else {
            row.createCell(9).setCellValue("");
        }

        if (lop.getDistributeTime() != null) {
            row.createCell(10).setCellValue(df.format(lop.getDistributeTime()));
        } else {
            row.createCell(10).setCellValue("");
        }
        row.createCell(11).setCellValue(lop.getDistributedBy());

        if (lop.getMerchantID() != null) {
            row.createCell(12).setCellValue(lop.getMerchantID());
        } else {
            row.createCell(12).setCellValue("");
        }
        if (lop.getMDN() != null) {
            row.createCell(14).setCellValue(lop.getMDN());
        } else {
            row.createCell(14).setCellValue("");
        }
        if (lop.getActualAmountPaid() != null && lop.getAmountDistributed() != null) {
            BigDecimal amountPaid = lop.getActualAmountPaid();
            BigDecimal amountDistributed = lop.getAmountDistributed();
            BigDecimal commission = ZERO;
//            commission = ((amountDistributed - amountPaid) * 100) / amountDistributed;
            commission = amountDistributed.subtract(amountPaid).multiply(HUNDRED).divide(amountDistributed);            
            row.createCell(15).setCellValue(commission.doubleValue());
        } else {
            row.createCell(15).setCellValue("");
        }
        if (lop.getLastUpdateTime() != null) {
            row.createCell(16).setCellValue(df.format(lop.getLastUpdateTime()));
        } else {
            row.createCell(16).setCellValue("");
        }
        if (lop.getDistributorName() != null) {
            row.createCell(17).setCellValue(lop.getDistributorName());
        } else {
            row.createCell(17).setCellValue("");
        }
    }

    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "LOP ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "GiroRefID");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "User Name");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Transfer Date");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Paid Amount");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Value Amount");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Status");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "Approved/Rejected By");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "Create Date");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Approval Date");

        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "Distribution Time");

        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "Distributed By");

        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "Merchant ID");

        HSSFCell header14 = getCell(sheet, currentRow, 13);
        setText(header14, "Transaction Ref");

        HSSFCell header15 = getCell(sheet, currentRow, 14);
        setText(header15, "MDN");

        HSSFCell header16 = getCell(sheet, currentRow, 15);
        setText(header16, "Commission");

        HSSFCell header17 = getCell(sheet, currentRow, 16);
        setText(header17, "Last Modified");

        HSSFCell header18 = getCell(sheet, currentRow, 17);
        setText(header18, "DCT Name");
    }

    public void buildExcelViewDocument(HttpServletRequest request, HSSFWorkbook workbook) throws ParseException, Exception {
        CMJSBulkLOP jsLOP = new CMJSBulkLOP();
        String id = request.getParameter(CMJSBulkLOP.FieldName_IDSearch);
        if (StringUtils.isNotEmpty(id)) {
            jsLOP.setIDSearch(Long.parseLong(id));
        }
        String statusSearch = request.getParameter(CMJSBulkLOP.FieldName_LOPStatusSearch);
        if (StringUtils.isNotEmpty(statusSearch)) {
            jsLOP.setLOPStatusSearch(statusSearch);
        }
        String dctNameSearch = request.getParameter(CMJSBulkLOP.FieldName_DCTNameSearch);
        if (StringUtils.isNotEmpty(dctNameSearch)) {
            jsLOP.setDCTNameSearch(dctNameSearch);
        }
        String userNameSearch = request.getParameter(CMJSBulkLOP.FieldName_UsernameSearch);
        if (StringUtils.isNotEmpty(userNameSearch)) {
            jsLOP.setUsernameSearch(userNameSearch);
        }
        String startDateSearch = request.getParameter(CMJSBulkLOP.FieldName_StartDateSearch);
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:S");

        if (startDateSearch != null && startDateSearch.trim().length() > 0) {
            Date startTime = df.parse(startDateSearch);
            Timestamp tstartTime = new Timestamp(startTime);
            jsLOP.setStartDateSearch(tstartTime);
        }
        String endDateSearch = request.getParameter(CMJSBulkLOP.FieldName_EndDateSearch);

        if (endDateSearch != null && endDateSearch.trim().length() > 0) {
            Date endTime = df.parse(endDateSearch);
            Timestamp tendTime = new Timestamp(endTime);
            jsLOP.setEndDateSearch(tendTime);
        }
        Long merid = 0l;
        if (request.getParameter(CMJSBulkLOP.FieldName_MerchantIDSearch) != null) {
            merid = Long.parseLong(request.getParameter(CMJSBulkLOP.FieldName_MerchantIDSearch));
        }
        if (merid > 0) {
            jsLOP.setMerchantIDSearch(merid);
        }
        jsLOP.setstart(start);
        jsLOP.setlimit(ConfigurationUtil.getExcelRowLimit());
        jsLOP.setaction(CmFinoFIX.JSaction_Select);
        BulkLOPProcessor temp = new BulkLOPProcessor();
        CMJSBulkLOP processedList = (CMJSBulkLOP) temp.process(jsLOP);


        HSSFSheet sheet = workbook.createSheet("LOP");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;
        initializeWorkBook(sheet, currentRow);
        if (processedList.getEntries() != null) {
        for (CMJSBulkLOP.CGEntries lop : processedList.getEntries()) {
            currentRow++;
            HSSFRow row = sheet.createRow(currentRow);
            fillLOPCells(lop, row);
        }
    }
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.web;

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
import com.mfino.fix.CmFinoFIX.CMJSCreditCardTransaction;
import com.mfino.fix.processor.CreditCardTransactionProcessor;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.web.ExcelView;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author ADMIN
 */
public class CreditCardTransactionsExcelView extends ExcelView {

    private static final Integer start = 0;
    private DateFormat df = getDateFormat();

    private void fillCreditCardCells(CMJSCreditCardTransaction.CGEntries cc, HSSFRow row) {
        row.createCell(0).setCellValue(cc.getID());
        if(cc.getCCBucketType() != null) {
            row.createCell(1).setCellValue(cc.getCCBucketType());
        } else {
            row.createCell(1).setCellValue("");
        }
        if(cc.getOperation() != null){
            row.createCell(2).setCellValue(cc.getOperation());
        }else {
            row.createCell(2).setCellValue("");
        }
        
        if (cc.getMDN() != null) {
            row.createCell(3).setCellValue(cc.getMDN());
        } else {
            row.createCell(3).setCellValue("");
        }
        if(cc.getCurrCode() != null){
            row.createCell(4).setCellValue(cc.getCurrencyName());
        }else {
            row.createCell(4).setCellValue("");
        }
        
//        if(cc.getTransactionDate() != null){
//            row.createCell(5).setCellValue(cc.getTransactionDate().substring(0,cc.getTransactionDate().length()-4 ));
//        }else {
//            row.createCell(5).setCellValue("");
//        }
        if(cc.getTransStatus() != null){
            row.createCell(5).setCellValue(cc.getTransStatus());
        }else {
            row.createCell(5).setCellValue("");
        }
        
        if(cc.getNSIATransCompletionTime() != null){
            row.createCell(6).setCellValue(df.format(cc.getNSIATransCompletionTime()));
        }else {
            row.createCell(6).setCellValue("");
        }
        
        if (cc.getAmount() != null) {
            row.createCell(7).setCellValue(cc.getAmount().doubleValue());
        } else {
            row.createCell(7).setCellValue("");
        }
        
        if (cc.getCardNoPartial() != null) {
            row.createCell(8).setCellValue(cc.getCardNoPartial());
        } else {
            row.createCell(8).setCellValue("");
        }
        if (cc.getAcquirerBank() != null) {
            row.createCell(9).setCellValue(cc.getAcquirerBank());
        } else {
            row.createCell(9).setCellValue("");
        }
        if (cc.getBankResCode() != null) {
            row.createCell(10).setCellValue(cc.getBankResCode());
        } else {
            row.createCell(10).setCellValue("");
        }
        if (cc.getBankResMsg() != null) {
            row.createCell(11).setCellValue(cc.getBankResMsg());
        } else {
            row.createCell(11).setCellValue("");
        }
        if (cc.getAuthID() != null) {
            row.createCell(12).setCellValue(cc.getAuthID());
        } else {
            row.createCell(12).setCellValue("");
        }        
        if (cc.getBillReferenceNumber() != null) {
            row.createCell(13).setCellValue(cc.getBillReferenceNumber());
        } else {
            row.createCell(13).setCellValue("");
        }
        
        if (cc.getCreateTime() != null) {
            row.createCell(14).setCellValue(df.format(cc.getCreateTime()));
        } else {
            row.createCell(14).setCellValue("");
        }
        if (cc.getCreatedBy() != null) {
            row.createCell(15).setCellValue(cc.getCreatedBy());
        } else {
            row.createCell(15).setCellValue("");
        }
        if (cc.getLastUpdateTime() != null) {
            row.createCell(16).setCellValue(df.format(cc.getLastUpdateTime()));
        } else {
            row.createCell(16).setCellValue("");
        }
        if (cc.getUpdatedBy() != null) {
            row.createCell(17).setCellValue(cc.getUpdatedBy());
        } else {
            row.createCell(17).setCellValue("");
        }      
        
       
        if(cc.getCCFailureReasonText() != null){
            row.createCell(18).setCellValue(cc.getCCFailureReasonText());
        }else {
            row.createCell(18).setCellValue("");
        }     
        
        if(cc.getTransactionID() != null){
            row.createCell(19).setCellValue(cc.getTransactionID());
        }else {
            row.createCell(19).setCellValue("");
        }     
    }

    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "CC Txn ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "Bucket Type");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Operation");
        
        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Destination MDN");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Currency Name");
        
//        HSSFCell header6 = getCell(sheet, currentRow, 5);
//        setText(header6, "Transaction Date");
        
        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Trans Status");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "NSIATransCompletionTime");

        
        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "Amount");
        
        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "Card No Partial");
        
        
        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Acquirer Bank");

        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "Bank Res Code");

        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "Bank Res Msg");
        
        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "Auth ID");

        HSSFCell header14 = getCell(sheet, currentRow, 13);
        setText(header14, "Bill Ref Number");
        
        HSSFCell header15 = getCell(sheet, currentRow, 14);
        setText(header15, "Create Time");

        HSSFCell header16 = getCell(sheet, currentRow, 15);
        setText(header16, "Created By");

        HSSFCell header17 = getCell(sheet, currentRow, 16);
        setText(header17, "Last Update Time");

        HSSFCell header18 = getCell(sheet, currentRow, 17);
        setText(header18, "Updated By");
        
        
        HSSFCell header19 = getCell(sheet, currentRow, 18);
        setText(header19, "Failure Reason"); 
        
        HSSFCell header20 = getCell(sheet, currentRow, 19);
        setText(header20, "ReferenceID");
        
    }

    public void buildExcelViewDocument(HttpServletRequest request, HSSFWorkbook workbook) throws ParseException, Exception {
        CMJSCreditCardTransaction jsCC = new CMJSCreditCardTransaction();
        String id = request.getParameter(CMJSCreditCardTransaction.FieldName_IDSearch);
        if (StringUtils.isNotBlank(id)) {
            jsCC.setIDSearch(Long.parseLong(id));
        }
        String authIdSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_AuthIdSearch);
        if (StringUtils.isNotBlank(authIdSearch)) {
            jsCC.setAuthIdSearch(authIdSearch);
        }
        String transIdSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_TransactionIdSearch);
        if (StringUtils.isNotBlank(transIdSearch)) {
            jsCC.setTransactionIdSearch(Long.parseLong(transIdSearch));
        }
        String bankRefSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_BankReferenceNumberSearch);
        if (StringUtils.isNotBlank(bankRefSearch)) {
            jsCC.setBankReferenceNumberSearch(bankRefSearch);
        }
        String destMdnSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_DestMDNSearch);
        if (StringUtils.isNotBlank(destMdnSearch)) {
            jsCC.setDestMDNSearch(destMdnSearch);
        }
        String operationSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_OperationSearch);
        if (StringUtils.isNotBlank(operationSearch)) {
            jsCC.setOperationSearch(operationSearch);
        }
        String transStatusSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_TransStatusSearch);
        if (StringUtils.isNotBlank(transStatusSearch)) {
            jsCC.setTransStatusSearch(transStatusSearch);
        }

        String startDateSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_StartDateSearch);
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:S");

        if (startDateSearch != null && startDateSearch.trim().length() > 0) {
            Date startTime = df.parse(startDateSearch);
            Timestamp tstartTime = new Timestamp(startTime);
            jsCC.setStartDateSearch(tstartTime);
        }
        String endDateSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_EndDateSearch);

        if (endDateSearch != null && endDateSearch.trim().length() > 0) {
            Date endTime = df.parse(endDateSearch);
            Timestamp tendTime = new Timestamp(endTime);
            jsCC.setEndDateSearch(tendTime);
        }
        String lastStartDateSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_LastUpdateStartTime);
        

        if (lastStartDateSearch != null && lastStartDateSearch.trim().length() > 0) {
            Date startTime = df.parse(lastStartDateSearch);
            Timestamp tstartTime = new Timestamp(startTime);
            jsCC.setLastUpdateStartTime(tstartTime);
        }
        String lastEndDateSearch = request.getParameter(CMJSCreditCardTransaction.FieldName_LastUpdateEndTime);

        if (lastEndDateSearch != null && lastEndDateSearch.trim().length() > 0) {
            Date endTime = df.parse(lastEndDateSearch);
            Timestamp tendTime = new Timestamp(endTime);
            jsCC.setLastUpdateEndTime(tendTime);
        }
        
        jsCC.setstart(start);
        jsCC.setlimit(ConfigurationUtil.getExcelRowLimit());
        jsCC.setaction(CmFinoFIX.JSaction_Select);
        CreditCardTransactionProcessor temp = new CreditCardTransactionProcessor();
        CMJSCreditCardTransaction processedList = (CMJSCreditCardTransaction) temp.process(jsCC);


        HSSFSheet sheet = workbook.createSheet("CreditCardTransactions");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;
        initializeWorkBook(sheet, currentRow);
        if (processedList.getEntries() != null) {
        for (CMJSCreditCardTransaction.CGEntries cc : processedList.getEntries()) {
            currentRow++;
            HSSFRow row = sheet.createRow(currentRow);
            fillCreditCardCells(cc, row);
        }
    }
    }
}

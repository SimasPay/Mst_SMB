/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.mfino.fix.CmFinoFIX.CMJSCashFlow;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.CashFlowProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 *
 * @author sunil
 */
@Component("CashFlowExcelView")
public class CashFlowExcelView extends AbstractExcelView {

    private Logger log = LoggerFactory.getLogger(this.getClass());    
    private DateFormat df = DateUtil.getExcelDateFormat();
    
    @Autowired
    @Qualifier("CashFlowProcessorImpl")
    private CashFlowProcessor cashFlowProcessor;

    public int compareTo(Object object1, Object object2) throws ClassCastException {
        if (!(object1 instanceof CMJSCashFlow.CGEntries) || !(object2 instanceof CMJSCashFlow.CGEntries)) {
            throw new ClassCastException("CMJSCashFlow.CGEntries object Expected");
        }
        long diffValue = ((CMJSCashFlow.CGEntries) object1).getID() - ((CMJSCashFlow.CGEntries) object2).getID();
        if (diffValue > 0) {
            return 1;
        } else if (diffValue < 0) {
            return -1;
        }
        return 0;
    }

    public CMJSCashFlow mergeResults(CMJSCashFlow.CGEntries pendingEntries[], CMJSCashFlow.CGEntries completedEntries[]) {

        CMJSCashFlow mergedSet = new CMJSCashFlow();
        List<CMJSCashFlow.CGEntries> results = new ArrayList<CMJSCashFlow.CGEntries>();
        int pendingIterator = 0;
        int completedIterator = 0;
        int maxPendingSize = 0;
        int maxCompletedSize = 0;
        if (pendingEntries != null) {
            maxPendingSize = pendingEntries.length;
        }
        if (completedEntries != null) {
            maxCompletedSize = completedEntries.length;
        }
        while (true) {
            if (maxCompletedSize <= completedIterator || maxPendingSize <= pendingIterator) {
                break;
            }
            try {
                if (compareTo(pendingEntries[pendingIterator], completedEntries[completedIterator]) <= 0) {
                    results.add(completedEntries[completedIterator]);
                    completedIterator++;
                } else {
                    results.add(pendingEntries[pendingIterator]);
                    pendingIterator++;
                }
            } catch (ClassCastException e) {
                log.error("Invalid Object sent " + e);
            }
        }
        if (completedIterator >= maxCompletedSize) {
            while (pendingIterator < maxPendingSize) {
                results.add(pendingEntries[pendingIterator]);
                pendingIterator++;
            }
        } else {
            while (completedIterator < maxCompletedSize) {
                results.add(completedEntries[completedIterator]);
                completedIterator++;
            }
        }
        if (results != null) {
            mergedSet.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                mergedSet.getEntries()[i] = results.get(i);
            }
        }

        return mergedSet;

    }    

    private void fillCashFlowCells(CMJSCashFlow.CGEntries cashflow, HSSFRow row) {
        if (cashflow.getID() < 0) {
            row.createCell(0).setCellValue("--");
        } else {
            row.createCell(0).setCellValue(cashflow.getID());
        }
        if (cashflow.getStartTime() != null) {
            row.createCell(1).setCellValue(df.format(cashflow.getStartTime()));
        } else {
            row.createCell(1).setCellValue("");
        }
        if (cashflow.getSourceDestnPocketID().equals(cashflow.getDestPocketID())) {
            row.createCell(11).setCellValue(CmFinoFIX.TransactionType_Buy);
        } else if (cashflow.getSourceDestnPocketID().equals(cashflow.getSourcePocketID())) {
            row.createCell(11).setCellValue(CmFinoFIX.TransactionType_Sell);
        } else {
            row.createCell(11).setCellValue("--");
        }
        if (cashflow.getTransactionUICategory() != null) {
            row.createCell(2).setCellValue(cashflow.getTransactionUICategoryText());
        } else {
            row.createCell(2).setCellValue("--");
        }
        if (cashflow.getCommodityText() != null) {
            row.createCell(3).setCellValue(cashflow.getCommodityText());
        } else {
            row.createCell(3).setCellValue("--");
        }

        if (cashflow.getAmount() != null) {
            row.createCell(4).setCellValue(cashflow.getAmount().doubleValue());
        } else {
            row.createCell(4).setCellValue("--");
        }

        if (cashflow.getPaidAmount() != null) {
            row.createCell(5).setCellValue(cashflow.getPaidAmount().doubleValue());
//            row.createCell(6).setCellValue(cashflow.getPaidAmount() - cashflow.getAmount());
            row.createCell(6).setCellValue(cashflow.getPaidAmount().subtract(cashflow.getAmount()).doubleValue());            
        } else {
            row.createCell(5).setCellValue("--");
            row.createCell(6).setCellValue("--");
        }

        if (cashflow.getTransactionUICategory() == null) {
            row.createCell(7).setCellValue("--");
        } else if (cashflow.getTransactionUICategory().equals(CmFinoFIX.TransactionUICategory_MA_Topup) ||
                cashflow.getTransactionUICategory().equals(CmFinoFIX.TransactionUICategory_BulkTopup) ||
                cashflow.getTransactionUICategory().equals(CmFinoFIX.TransactionUICategory_Dompet_Self_Topup) ||
                cashflow.getTransactionUICategory().equals(CmFinoFIX.TransactionUICategory_Dompet_Topup_Another)) {
            if (cashflow.getSourceDestnPocketID().equals(cashflow.getSourcePocketID())) {
                if (cashflow.getDestMDN() != null) {
                    row.createCell(7).setCellValue(cashflow.getDestMDN());
                } else {
                    row.createCell(7).setCellValue("--");
                }
            } else {
                if (cashflow.getSourceMDN() != null) {
                    row.createCell(7).setCellValue(cashflow.getSourceMDN());
                } else {
                    row.createCell(7).setCellValue("--");
                }
            }
        } else if (cashflow.getTransactionUICategory().equals(CmFinoFIX.TransactionUICategory_MA_Transfer) || cashflow.getTransactionUICategory().equals(CmFinoFIX.TransactionUICategory_BulkTransfer)) {
            if (cashflow.getSourceDestnPocketID().equals(cashflow.getSourcePocketID())) {
                if (cashflow.getDestMDN() != null) {
                    if (cashflow.getDestSubscriberID() != null) {
//                        Long destnSubscriberId = cashflow.getDestSubscriberID();
//                        SubscriberDAO destSubDao = new SubscriberDAO();
//                        Subscriber sub = destSubDao.getById(destnSubscriberId);
//                        if (sub.getUser() != null) {
//                            row.createCell(7).setCellValue(sub.getUser().getUsername());
//                        }
                    //no need of call to the DB for getting Destination Username
                    row.createCell(7).setCellValue(cashflow.getDestnUserName());
                    }

                } else {
                    row.createCell(7).setCellValue("--");
                }
            } else {
                if (cashflow.getSourceMDN() != null) {
                    row.createCell(7).setCellValue(cashflow.getSourceUserName());
                } else {
                    row.createCell(7).setCellValue("--");
                }
            }
        } else if (StringUtils.isNotEmpty(StringUtils.trim(cashflow.getDestSubscriberName())) || StringUtils.isNotEmpty(StringUtils.trim(cashflow.getSourceSubscriberName()))) {
            if (cashflow.getSourceDestnPocketID().equals(cashflow.getSourcePocketID())) {
                if (StringUtils.isNotEmpty(StringUtils.trim(cashflow.getDestSubscriberName()))) {
                    row.createCell(7).setCellValue(cashflow.getDestSubscriberName());
                } else {
                    row.createCell(7).setCellValue("--");
                }
            } else {
                if (StringUtils.isNotEmpty(StringUtils.trim(cashflow.getSourceSubscriberName()))) {
                    row.createCell(7).setCellValue(cashflow.getSourceSubscriberName());
                } else {
                    row.createCell(7).setCellValue("--");
                }
            }
        } else {
            row.createCell(7).setCellValue("--");
        }
        if (cashflow.getTransferStatus() != null) {
            row.createCell(8).setCellValue(cashflow.getTransferStatusText());
        } else {
            row.createCell(8).setCellValue("--");
        }

        if (cashflow.getDestPocketBalance() != null || cashflow.getSourcePocketBalance() != null) {
            if (cashflow.getSourceDestnPocketID().equals(cashflow.getDestPocketID())) {
                if (cashflow.getTransferStatus().equals(CmFinoFIX.TransferStatus_Failed) && cashflow.getTransferStateText().equals(CmFinoFIX.TransferStateValue_Complete)) {
                    row.createCell(9).setCellValue(cashflow.getDestPocketBalance().doubleValue());
                } else {
//                    row.createCell(9).setCellValue(cashflow.getDestPocketBalance() + cashflow.getAmount());
                	row.createCell(9).setCellValue(cashflow.getDestPocketBalance().add(cashflow.getAmount()).doubleValue());                	
                }
            } else {
                if (cashflow.getTransferStatus().equals(CmFinoFIX.TransferStatus_Failed) && cashflow.getTransferStateText().equals(CmFinoFIX.TransferStateValue_Complete)
                        && cashflow.getCSRAction() == null) {
                    row.createCell(9).setCellValue(cashflow.getSourcePocketBalance().doubleValue());
                } else {
//                    row.createCell(9).setCellValue(cashflow.getSourcePocketBalance() - cashflow.getAmount());
                	row.createCell(9).setCellValue(cashflow.getSourcePocketBalance().subtract(cashflow.getAmount()).doubleValue());                	
                }
            }
        } else {
            row.createCell(9).setCellValue("--");
        }

        if (cashflow.getSourceApplication() != null) {
            row.createCell(10).setCellValue(cashflow.getAccessMethodText());
        } else {
            row.createCell(10).setCellValue("--");
        }
        if (cashflow.getSourceTerminalID() != null) {
            row.createCell(12).setCellValue(cashflow.getSourceTerminalID());
        } else {
            row.createCell(12).setCellValue("--");
        }
        if (cashflow.getSourceReferenceID() != null) {
            row.createCell(13).setCellValue(cashflow.getSourceReferenceID());
        } else {
            row.createCell(13).setCellValue("--");
        }
        row.createCell(14).setCellValue(cashflow.getTransferStateText());
    }

    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "Reference ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "Date & Time");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Transaction Type");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Commodity Type");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Value");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Paid");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Margin");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "To/From");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "Status");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Ending Balance");

        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "Channel Name");

        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "Buy / Sell");

        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "Source Terminal Id");

        HSSFCell header14 = getCell(sheet, currentRow, 13);
        setText(header14, "Merchant Ref No");

        HSSFCell header15 = getCell(sheet, currentRow, 14);
        setText(header15, "State");

    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=CashFlow.xls");
        CMJSCashFlow jsCF = new CMJSCashFlow();
        String transactionTypeSearch = request.getParameter(CMJSCashFlow.FieldName_TransactionUICategory);
        if (StringUtils.isNotEmpty(transactionTypeSearch)) {
            jsCF.setTransactionUICategory(Integer.parseInt(transactionTypeSearch));
        }
        String transactionType = request.getParameter(CMJSCashFlow.FieldName_TransactionType);
        if (StringUtils.isNotEmpty(transactionType)) {
            jsCF.setTransactionType(transactionType);
        }
        String startTimeTxt = request.getParameter(CMJSCashFlow.FieldName_StartTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (startTimeTxt != null && startTimeTxt.trim().length() > 0) {
            Date parsedDate = dateFormat.parse(startTimeTxt);
            Timestamp ts = new Timestamp(parsedDate);
            jsCF.setStartTime(ts);
        }

        String endTimeTxt = request.getParameter(CMJSCashFlow.FieldName_EndTime);
        if (endTimeTxt != null) {
            Date parsedDate = dateFormat.parse(endTimeTxt);
            Timestamp ts = new Timestamp(parsedDate);
            jsCF.setEndTime(ts);
        }

        String transferStatus = request.getParameter(CMJSCashFlow.FieldName_TransferStatus);
        if (StringUtils.isNotEmpty(transferStatus)) {
            jsCF.setTransferStatus(Integer.parseInt(transferStatus));
        }
        String transactionTransferStatus = request.getParameter(CMJSCashFlow.FieldName_TransactionsTransferStatus);
        if (StringUtils.isNotEmpty(transactionTransferStatus)) {
            jsCF.setTransactionsTransferStatus(Integer.parseInt(transactionTransferStatus));
        }

        String subtotalByTxt = request.getParameter(CMJSCashFlow.FieldName_SubtotalBy);
        if (StringUtils.isNotEmpty(subtotalByTxt)) {
            jsCF.setSubtotalBy(Integer.parseInt(subtotalByTxt));
        }

        String sourceApplicationSearchTxt = request.getParameter(CMJSCashFlow.FieldName_SourceApplicationSearch);
        if (StringUtils.isNotEmpty(sourceApplicationSearchTxt)) {
            jsCF.setSourceApplicationSearch(Integer.parseInt(sourceApplicationSearchTxt));
        }

        jsCF.setSourceDestnMDN(request.getParameter(CMJSCashFlow.FieldName_SourceDestnMDN));
        String refId = request.getParameter(CMJSCashFlow.FieldName_IDSearch);
        if (refId != null) {
            jsCF.setIDSearch(Long.parseLong(refId));
        }

        String transferStateString = request.getParameter(CMJSCashFlow.FieldName_TransferState);
        jsCF.setTransferState(CmFinoFIX.TransferState_Complete);
        if (StringUtils.isNotEmpty(transferStateString)) {
            jsCF.setTransferState(Integer.parseInt(transferStateString));
        }
        String isBothString = request.getParameter("isBoth");
        Boolean isBoth = Boolean.parseBoolean(isBothString);

        jsCF.setlimit(ConfigurationUtil.getExcelRowLimit());

        HSSFSheet sheet = workbook.createSheet("Transactions");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;
        initializeWorkBook(sheet, currentRow);

        if (isBoth) {
            jsCF.setTransferState(CmFinoFIX.TransferState_Complete);
            CMJSCashFlow completeProcessedList = (CMJSCashFlow) cashFlowProcessor.process(jsCF);
            CMJSCashFlow.CGEntries completeResults[]=null;
            if (completeProcessedList != null) {
                completeResults = completeProcessedList.getEntries();
            }

            jsCF.setTransferState(CmFinoFIX.TransferState_Pending);
            jsCF.setEntries(null);
            CMJSCashFlow pendinProcessedList = null;
            if (StringUtils.isEmpty(transactionTransferStatus)) {
                pendinProcessedList = (CMJSCashFlow) cashFlowProcessor.process(jsCF);
            }
            
            CMJSCashFlow mergedResults = mergeResults(((pendinProcessedList==null)? null:pendinProcessedList.getEntries()),completeResults);
            if (mergedResults != null) {
                if (mergedResults.getEntries() != null) {
                    for (CMJSCashFlow.CGEntries cashflow : mergedResults.getEntries()) {
                        currentRow++;
                        HSSFRow row = sheet.createRow(currentRow);
                        fillCashFlowCells(cashflow, row);
                    }
                }
            }
        } else {
            CMJSCashFlow processedList = (CMJSCashFlow) cashFlowProcessor.process(jsCF);
            if (processedList.getEntries() != null) {
                for (CMJSCashFlow.CGEntries cashflow : processedList.getEntries()) {
                    currentRow++;
                    HSSFRow row = sheet.createRow(currentRow);
                    fillCashFlowCells(cashflow, row);
                }
            }
        }

    }
}

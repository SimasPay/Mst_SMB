package com.mfino.uicore.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.mfino.fix.CmFinoFIX.CMJSTransactionAmountDistributionLog;
import com.mfino.uicore.fix.processor.TransactionAmountDistributorProcess;
import com.mfino.util.ConfigurationUtil;

/**
 * @author sasidhar
 * Excel Report for Integration Partner list.
 */
@Component("ChargeDistributionExcelView")
public class ChargeDistributionExcelView extends AbstractExcelView {
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    @Qualifier("TransactionAmountDistributorProcessImpl")
    private  TransactionAmountDistributorProcess transactionAmountDistributorProcess;
    
    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "Reference ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "PartnerID");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "PartnerTradeName");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "PocketID");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "ShareAmount");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "ChargeType");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Status");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "Source MDN");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "Failure Reason");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Processed Time");
        
       
    }

    private void fillPartnerCells(CMJSTransactionAmountDistributionLog.CGEntries tadl, HSSFRow row) {
    	row.createCell(0).setCellValue(tadl.getServiceChargeTransactionLogID());
    	row.createCell(1).setCellValue(tadl.getPartnerID());
    	row.createCell(2).setCellValue(tadl.getDestPartnerTradeName());
    	row.createCell(3).setCellValue(tadl.getPocketID());
    	row.createCell(4).setCellValue(tadl.getShareAmount().doubleValue());
    	row.createCell(5).setCellValue(tadl.getChargeTypeName());
    	row.createCell(6).setCellValue(tadl.getTransferStatusText());
    	row.createCell(7).setCellValue(tadl.getSourceMDN());
    	row.createCell(8).setCellValue(tadl.getTransferFailureReasonText());
    	row.createCell(9).setCellValue(tadl.getLastUpdateTime());
    }

    @SuppressWarnings("rawtypes")
   	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=ChargeDistribution.xls");
       	CMJSTransactionAmountDistributionLog jsTADL = new CMJSTransactionAmountDistributionLog();
        
        String sctlID = request.getParameter(CmFinoFIX.CMJSTransactionAmountDistributionLog.FieldName_ServiceChargeTransactionLogID);
        if(sctlID!=null&&sctlID!=""){
        	jsTADL.setServiceChargeTransactionLogID(Long.valueOf(sctlID));
        }
        
		jsTADL.setaction(CmFinoFIX.JSaction_Select);
		jsTADL.setlimit(ConfigurationUtil.getExcelRowLimit());
		        
        try {
            HSSFSheet sheet = workbook.createSheet("Charge Distribution");
            sheet.setDefaultColumnWidth(16);
            int currentRow = 0;
            initializeWorkBook(sheet, currentRow);
            
            CMJSTransactionAmountDistributionLog tadlList = (CMJSTransactionAmountDistributionLog)transactionAmountDistributorProcess.process(jsTADL);

            if (tadlList.getEntries() != null) {
	            for (CMJSTransactionAmountDistributionLog.CGEntries ipEntry : tadlList.getEntries()) {
	                currentRow++;
	                HSSFRow row = sheet.createRow(currentRow);
	                fillPartnerCells(ipEntry, row);
	            }
           }
        } catch (Exception error) {
            log.error("Error in Integration Partner Excel Dowload ::",error);
        }
    }
}

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
import com.mfino.fix.CmFinoFIX.CMJSServiceChargeTransactions;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.ServiceChargeTransactionLogProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 * @author sasidhar
 * Excel Report for Integration Partner list.
 */
@Component("ServiceChargeTransactionExcelView")
public class ServiceChargeTransactionExcelView extends AbstractExcelView {
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private DateFormat df = DateUtil.getExcelDateFormat();
    
    @Autowired
    @Qualifier("ServiceChargeTransactionLogProcessorImpl")
    private ServiceChargeTransactionLogProcessor serviceChargeTransactionLogProcessor;
    
    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "Reference ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "Transaction Type");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Transaction Amount");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "CalculatedCharge");
        
        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Charge Mode");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Status");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Status Reason");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "SorceMDN");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "DestinationMDN");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Sorce PartnerCode");

        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "Destination PartnerCode");
        
        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "Biller Code");
        
        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "IntegrationRRN");
        
        HSSFCell header14 = getCell(sheet, currentRow, 13);
        setText(header14, "Service Name");
        
        HSSFCell header15 = getCell(sheet, currentRow, 14);
        setText(header15, "Transaction Time");
        
        HSSFCell header16 = getCell(sheet, currentRow, 15);
        setText(header16, "Channel Name");
        
        HSSFCell header17 = getCell(sheet, currentRow, 16);
        setText(header17, "AddtionalInfo");
        
        HSSFCell header18 = getCell(sheet, currentRow, 17);
        setText(header18, "Info1");
        
        HSSFCell header19 = getCell(sheet, currentRow, 18);
        setText(header19, "IntegrationType");
        
        HSSFCell header20 = getCell(sheet, currentRow, 19);
        setText(header20, "ReconcilationID1");
        
        HSSFCell header21 = getCell(sheet, currentRow, 20);
        setText(header21, "ReconcilationID2");
        
        HSSFCell header22 = getCell(sheet, currentRow, 21);
        setText(header22, "ReconcilationID3");
        
        HSSFCell header23 = getCell(sheet, currentRow, 22);
        setText(header23, "InvoiceNo");
        
        HSSFCell header24 = getCell(sheet, currentRow, 23);
        setText(header24, "Description");
        
        HSSFCell header25 = getCell(sheet, currentRow, 24);
        setText(header25, "Operator Response Code");
        
    }

    private void fillPartnerCells(CMJSServiceChargeTransactions.CGEntries jsSCTL, HSSFRow row) {
    	row.createCell(0).setCellValue(jsSCTL.getID());
    	row.createCell(1).setCellValue(jsSCTL.getTransactionName());
    	row.createCell(2).setCellValue(jsSCTL.getTransactionAmount().doubleValue());
    	row.createCell(3).setCellValue(jsSCTL.getCalculatedCharge().doubleValue());
    	row.createCell(4).setCellValue(jsSCTL.getChargeModeText());
    	row.createCell(5).setCellValue(jsSCTL.getTransferStatusText());
    	row.createCell(6).setCellValue(jsSCTL.getFailureReason());
    	row.createCell(7).setCellValue(jsSCTL.getSourceMDN());
    	row.createCell(8).setCellValue(jsSCTL.getDestMDN());
    	row.createCell(9).setCellValue(jsSCTL.getSourcePartnerCode());
    	row.createCell(10).setCellValue(jsSCTL.getDestPartnerCode());
    	row.createCell(11).setCellValue(jsSCTL.getMFSBillerCode());
    	row.createCell(12).setCellValue(jsSCTL.getBankRetrievalReferenceNumber());
    	row.createCell(13).setCellValue(jsSCTL.getServiceName());
    	row.createCell(14).setCellValue(df.format(jsSCTL.getTransactionTime()));
    	row.createCell(15).setCellValue(jsSCTL.getAccessMethodText());    	
    	row.createCell(16).setCellValue(jsSCTL.getAdditionalInfo());
    	row.createCell(17).setCellValue(jsSCTL.getInfo1());
    	row.createCell(18).setCellValue(jsSCTL.getIntegrationType());
    	row.createCell(19).setCellValue(jsSCTL.getReconcilationID1());
    	row.createCell(20).setCellValue(jsSCTL.getReconcilationID2());
    	row.createCell(21).setCellValue(jsSCTL.getReconcilationID3());
    	row.createCell(22).setCellValue(jsSCTL.getInvoiceNo());
    	row.createCell(23).setCellValue(jsSCTL.getDescription());
    	row.createCell(24).setCellValue(jsSCTL.getOperatorResponseCode());
    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=Transactions.xls");
    	CMJSServiceChargeTransactions jsServicechargeTransactions = new CMJSServiceChargeTransactions();
        
        String iDSearch = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_IDSearch);
        
		if((null != iDSearch) && !("".equals(iDSearch))){
			jsServicechargeTransactions.setIDSearch(Long.valueOf(iDSearch));
		}

        String transactionID = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_TransactionIdSearch);
        
		if((null != transactionID) && !("".equals(transactionID))){
			jsServicechargeTransactions.setTransactionIdSearch(Long.valueOf(transactionID));
		}
		
		String accessMethod = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_SourceApplicationSearch);
		if((null != accessMethod) && !("".equals(accessMethod))){
			jsServicechargeTransactions.setSourceApplicationSearch(Integer.valueOf(accessMethod));
		}
		
		String refID = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_TransferID);
		if((null != refID) && !("".equals(refID))){
			jsServicechargeTransactions.setTransferID(Long.valueOf(refID));
		}
		
		String sourcePartnerCode = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_SourcePartnerCode);
		if(StringUtils.isNotBlank(sourcePartnerCode)){
			jsServicechargeTransactions.setSourcePartnerCode(sourcePartnerCode);
		}
		
		String destPartnerCode = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_DestPartnerCode);
		if(StringUtils.isNotBlank(destPartnerCode)){
			jsServicechargeTransactions.setDestPartnerCode(destPartnerCode);
		}
		String sourceMdn = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_SourceMDN);
		if((null != sourceMdn) && !("".equals(sourceMdn))){
			jsServicechargeTransactions.setSourceMDN(sourceMdn);
		}
		String destMdn = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_DestMDN);
		if((null != destMdn) && !("".equals(destMdn))){
			jsServicechargeTransactions.setDestMDN(destMdn);
		}
		String status = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_TransferStatus);
		if((null != status) && !("".equals(status))){
			jsServicechargeTransactions.setStatus(Integer.valueOf(status));
		}
       
        String startDateSearch = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_StartDateSearch);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		if((null != startDateSearch) && !("".equals(startDateSearch))){
          Date parsedDate = dateFormat.parse(startDateSearch);
          Timestamp ts = new Timestamp(parsedDate);
          jsServicechargeTransactions.setStartDateSearch(ts);
		}
        
        String endDateSearch = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_EndDateSearch);

		if((null != endDateSearch) && !("".equals(endDateSearch))){
          Date parsedDate = dateFormat.parse(endDateSearch);
          Timestamp ts = new Timestamp(parsedDate);
          jsServicechargeTransactions.setEndDateSearch(ts);
		}
		
		String rrn = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_BankRetrievalReferenceNumber);
        
		if(StringUtils.isNotBlank(rrn)){
			jsServicechargeTransactions.setBankRetrievalReferenceNumber(rrn);
		}
		
		String transactiontypeid = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_TransactionTypeID);
		
		if(StringUtils.isNotBlank(transactiontypeid)){
			jsServicechargeTransactions.setTransactionTypeID(Long.valueOf(transactiontypeid));
		}
		
		String billerCode = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_MFSBillerCode);
        
		if(StringUtils.isNotBlank(billerCode)){
			jsServicechargeTransactions.setMFSBillerCode(billerCode);
		}
        String Info1 = request.getParameter(CmFinoFIX.CMJSServiceChargeTransactions.FieldName_Info1);
        
		if(StringUtils.isNotBlank(Info1)){
			jsServicechargeTransactions.setInfo1(Info1);
		}
		
		jsServicechargeTransactions.setaction(CmFinoFIX.JSaction_Select);
		jsServicechargeTransactions.setlimit(ConfigurationUtil.getExcelRowLimit());
        
        try {
            HSSFSheet sheet = workbook.createSheet("Transactions");
            sheet.setDefaultColumnWidth(16);
            int currentRow = 0;
            initializeWorkBook(sheet, currentRow);
            
            CMJSServiceChargeTransactions sctlList = (CMJSServiceChargeTransactions)serviceChargeTransactionLogProcessor.process(jsServicechargeTransactions);

            if (sctlList.getEntries() != null) {
	            for (CMJSServiceChargeTransactions.CGEntries ipEntry : sctlList.getEntries()) {
	                currentRow++;
	                HSSFRow row = sheet.createRow(currentRow);
	                fillPartnerCells(ipEntry, row);
	            }
           }
        } catch (Exception error) {
            log.error("Error in Transactions Excel Dowload ::",error);
        }
    }
}

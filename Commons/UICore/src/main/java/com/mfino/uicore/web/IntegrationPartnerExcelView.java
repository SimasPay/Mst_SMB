package com.mfino.uicore.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * @author sasidhar
 * Excel Report for Integration Partner list.
 */
@Component("IntegrationPartnerExcelView")
public class IntegrationPartnerExcelView extends AbstractExcelView {
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
//    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
//        HSSFCell header1 = getCell(sheet, currentRow, 0);
//        setText(header1, "ID");
//
//        HSSFCell header2 = getCell(sheet, currentRow, 1);
//        setText(header2, "PartnerID");
//
//        HSSFCell header3 = getCell(sheet, currentRow, 2);
//        setText(header3, "SubscriberID");
//
//        HSSFCell header4 = getCell(sheet, currentRow, 3);
//        setText(header4, "PartnerCode");
//
//        HSSFCell header5 = getCell(sheet, currentRow, 4);
//        setText(header5, "PartnerStatus");
//
//        HSSFCell header6 = getCell(sheet, currentRow, 5);
//        setText(header6, "TradeName");
//
//        HSSFCell header7 = getCell(sheet, currentRow, 6);
//        setText(header7, "TypeOfOrganization");
//
//        HSSFCell header8 = getCell(sheet, currentRow, 7);
//        setText(header8, "FaxNumber");
//
//        HSSFCell header9 = getCell(sheet, currentRow, 8);
//        setText(header9, "WebSite");
//
//        HSSFCell header10 = getCell(sheet, currentRow, 9);
//        setText(header10, "AuthorizedRepresentative");
//        
//        HSSFCell header11 = getCell(sheet, currentRow, 10);
//        setText(header11, "RepresentativeName");
//        
//        HSSFCell header12 = getCell(sheet, currentRow, 11);
//        setText(header12, "Designation");
//        
//        HSSFCell header13 = getCell(sheet, currentRow, 12);
//        setText(header13, "FranchisePhoneNumber");
//        
//        HSSFCell header14 = getCell(sheet, currentRow, 13);
//        setText(header14, "Classification");
//        
//        HSSFCell header15 = getCell(sheet, currentRow, 14);
//        setText(header15, "NumberOfOutlets");
//        
//        HSSFCell header16 = getCell(sheet, currentRow, 15);
//        setText(header16, "IndustryClassification");
//        
//        HSSFCell header17 = getCell(sheet, currentRow, 16);
//        setText(header17, "YearEstablished");
//        
//        HSSFCell header18 = getCell(sheet, currentRow, 17);
//        setText(header18, "AuthorizedFaxNumber");
//        
//        HSSFCell header19 = getCell(sheet, currentRow, 18);
//        setText(header19, "AuthorizedEmail");
//    }
//
//    private void fillPartnerCells(CMJSIntegrationPartner.CGEntries jsIntegrationPartner, HSSFRow row) {
//    	row.createCell(0).setCellValue(jsIntegrationPartner.getID());
//    	row.createCell(1).setCellValue(jsIntegrationPartner.getPartnerID());
//    	row.createCell(2).setCellValue(jsIntegrationPartner.getSubscriberID());
//    	row.createCell(3).setCellValue(jsIntegrationPartner.getPartnerCode());
//    	row.createCell(4).setCellValue(jsIntegrationPartner.getPartnerStatusText());
//    	row.createCell(5).setCellValue(jsIntegrationPartner.getTradeName());
//    	row.createCell(6).setCellValue(jsIntegrationPartner.getTypeOfOrganization());
//    	row.createCell(7).setCellValue(jsIntegrationPartner.getFaxNumber());
//    	row.createCell(8).setCellValue(jsIntegrationPartner.getWebSite());
//    	row.createCell(9).setCellValue(jsIntegrationPartner.getAuthorizedRepresentative());
//    	row.createCell(10).setCellValue(jsIntegrationPartner.getRepresentativeName());
//    	row.createCell(11).setCellValue(jsIntegrationPartner.getDesignation());
//    	row.createCell(12).setCellValue(jsIntegrationPartner.getFranchisePhoneNumber());
//    	row.createCell(13).setCellValue(jsIntegrationPartner.getClassification());
//    	row.createCell(14).setCellValue(jsIntegrationPartner.getNumberOfOutlets() == null ? 0 : jsIntegrationPartner.getNumberOfOutlets());
//    	row.createCell(15).setCellValue(jsIntegrationPartner.getIndustryClassification());
//    	row.createCell(16).setCellValue(jsIntegrationPartner.getYearEstablished());
//    	row.createCell(17).setCellValue(jsIntegrationPartner.getAuthorizedFaxNumber());
//    	row.createCell(18).setCellValue(jsIntegrationPartner.getAuthorizedEmail());
//    }
//
    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        CMJSIntegrationPartner jsIntegrationPartner = new CMJSIntegrationPartner();
//        
//        String partnerIDSearch = request.getParameter(CmFinoFIX.CMJSServicePartner.FieldName_PartnerIDSearch);
//        
//		if((null != partnerIDSearch) && !("".equals(partnerIDSearch))){
//			jsIntegrationPartner.setPartnerIDSearch(partnerIDSearch);
//		}
//
//        String partnerCodeSearch = request.getParameter(CmFinoFIX.CMJSServicePartner.FieldName_PartnerCodeSearch);
//        
//		if((null != partnerCodeSearch) && !("".equals(partnerCodeSearch))){
//			jsIntegrationPartner.setPartnerCodeSearch(partnerCodeSearch);
//		}
//        
//        String tradeNameSearch = request.getParameter(CmFinoFIX.CMJSServicePartner.FieldName_TradeNameSearch);
//        
//		if((null != tradeNameSearch) && !("".equals(tradeNameSearch))){
//			jsIntegrationPartner.setTradeNameSearch(tradeNameSearch);
//		}
//		
//        String authorizedEmailSearch = request.getParameter(CmFinoFIX.CMJSServicePartner.FieldName_AuthorizedEmailSearch);
//        
//		if((null != authorizedEmailSearch) && !("".equals(authorizedEmailSearch))){
//			jsIntegrationPartner.setAuthorizedEmailSearch(authorizedEmailSearch);
//		}
//        
//        String startDateSearch = request.getParameter(CmFinoFIX.CMJSServicePartner.FieldName_StartDateSearch);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
//        
//		if((null != startDateSearch) && !("".equals(startDateSearch))){
//          Date parsedDate = dateFormat.parse(startDateSearch);
//          Timestamp ts = new Timestamp(parsedDate);
//          jsIntegrationPartner.setStartDateSearch(ts);
//		}
//        
//        String endDateSearch = request.getParameter(CmFinoFIX.CMJSServicePartner.FieldName_EndDateSearch);
//
//		if((null != endDateSearch) && !("".equals(endDateSearch))){
//          Date parsedDate = dateFormat.parse(endDateSearch);
//          Timestamp ts = new Timestamp(parsedDate);
//          jsIntegrationPartner.setStartDateSearch(ts);
//		}
//        
//		jsIntegrationPartner.setaction(CmFinoFIX.JSaction_Select);
//		jsIntegrationPartner.setlimit(ConfigurationUtil.getExcelRowLimit());
//		
//        IntegrationPartnerProcessor integrationPartnerProcessor = new IntegrationPartnerProcessor();
//        
//        try {
//            HSSFSheet sheet = workbook.createSheet("Integration Partner");
//            sheet.setDefaultColumnWidth(16);
//            int currentRow = 0;
//            initializeWorkBook(sheet, currentRow);
//            
//            CMJSIntegrationPartner integrationPartnerList = (CMJSIntegrationPartner)integrationPartnerProcessor.process(jsIntegrationPartner);
//
//            if (integrationPartnerList.getEntries() != null) {
//	            for (CMJSIntegrationPartner.CGEntries ipEntry : integrationPartnerList.getEntries()) {
//	                currentRow++;
//	                HSSFRow row = sheet.createRow(currentRow);
//	                fillPartnerCells(ipEntry, row);
//	            }
//           }
//        } catch (Exception error) {
//            log.error("Error in Integration Partner Excel Dowload ::",error);
//        }
    }
}

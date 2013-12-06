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
 * Excel Report for Business Partner list.
 */
@Component("BusinessPartnerExcelView")
public class BusinessPartnerExcelView extends AbstractExcelView {
	
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
//    private void fillPartnerCells(CMJSBusinessPartner.CGEntries jsBusinessPartner, HSSFRow row) {
//    	row.createCell(0).setCellValue(jsBusinessPartner.getID());
//    	row.createCell(1).setCellValue(jsBusinessPartner.getPartnerID());
//    	row.createCell(2).setCellValue(jsBusinessPartner.getSubscriberID());
//    	row.createCell(3).setCellValue(jsBusinessPartner.getPartnerCode());
//    	row.createCell(4).setCellValue(jsBusinessPartner.getPartnerStatusText());
//    	row.createCell(5).setCellValue(jsBusinessPartner.getTradeName());
//    	row.createCell(6).setCellValue(jsBusinessPartner.getTypeOfOrganization());
//    	row.createCell(7).setCellValue(jsBusinessPartner.getFaxNumber());
//    	row.createCell(8).setCellValue(jsBusinessPartner.getWebSite());
//    	row.createCell(9).setCellValue(jsBusinessPartner.getAuthorizedRepresentative());
//    	row.createCell(10).setCellValue(jsBusinessPartner.getRepresentativeName());
//    	row.createCell(11).setCellValue(jsBusinessPartner.getDesignation());
//    	row.createCell(12).setCellValue(jsBusinessPartner.getFranchisePhoneNumber());
//    	row.createCell(13).setCellValue(jsBusinessPartner.getClassification());
//    	row.createCell(14).setCellValue(jsBusinessPartner.getNumberOfOutlets() == null ? 0 : jsBusinessPartner.getNumberOfOutlets());
//    	row.createCell(15).setCellValue(jsBusinessPartner.getIndustryClassification());
//    	row.createCell(16).setCellValue(jsBusinessPartner.getYearEstablished());
//    	row.createCell(17).setCellValue(jsBusinessPartner.getAuthorizedFaxNumber());
//    	row.createCell(18).setCellValue(jsBusinessPartner.getAuthorizedEmail());
//    }
//
    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        CMJSBusinessPartner jsBusinessPartner = new CMJSBusinessPartner();
//        
//        String partnerIDSearch = request.getParameter(CmFinoFIX.CMJSBusinessPartner.FieldName_PartnerIDSearch);
//        
//		if((null != partnerIDSearch) && !("".equals(partnerIDSearch))){
//			jsBusinessPartner.setPartnerIDSearch(partnerIDSearch);
//		}
//
//        String partnerCodeSearch = request.getParameter(CmFinoFIX.CMJSBusinessPartner.FieldName_PartnerCodeSearch);
//        
//		if((null != partnerCodeSearch) && !("".equals(partnerCodeSearch))){
//			jsBusinessPartner.setPartnerCodeSearch(partnerCodeSearch);
//		}
//        
//        String tradeNameSearch = request.getParameter(CmFinoFIX.CMJSBusinessPartner.FieldName_TradeNameSearch);
//        
//		if((null != tradeNameSearch) && !("".equals(tradeNameSearch))){
//			jsBusinessPartner.setTradeNameSearch(tradeNameSearch);
//		}
//		
//        String authorizedEmailSearch = request.getParameter(CmFinoFIX.CMJSBusinessPartner.FieldName_AuthorizedEmailSearch);
//        
//		if((null != authorizedEmailSearch) && !("".equals(authorizedEmailSearch))){
//			jsBusinessPartner.setAuthorizedEmailSearch(authorizedEmailSearch);
//		}
//        
//        String startDateSearch = request.getParameter(CmFinoFIX.CMJSBusinessPartner.FieldName_StartDateSearch);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
//        
//		if((null != startDateSearch) && !("".equals(startDateSearch))){
//          Date parsedDate = dateFormat.parse(startDateSearch);
//          Timestamp ts = new Timestamp(parsedDate);
//          jsBusinessPartner.setStartDateSearch(ts);
//		}
//        
//        String endDateSearch = request.getParameter(CmFinoFIX.CMJSBusinessPartner.FieldName_EndDateSearch);
//
//		if((null != endDateSearch) && !("".equals(endDateSearch))){
//          Date parsedDate = dateFormat.parse(endDateSearch);
//          Timestamp ts = new Timestamp(parsedDate);
//          jsBusinessPartner.setStartDateSearch(ts);
//		}
//        
//		jsBusinessPartner.setaction(CmFinoFIX.JSaction_Select);
//		jsBusinessPartner.setlimit(ConfigurationUtil.getExcelRowLimit());
//		
//        BusinessPartnerProcessor businessPartnerProcessor = new BusinessPartnerProcessor();
//        
//        try {
//            HSSFSheet sheet = workbook.createSheet("Business Partner");
//            sheet.setDefaultColumnWidth(16);
//            int currentRow = 0;
//            initializeWorkBook(sheet, currentRow);
//            CMJSBusinessPartner businessPartnerList = (CMJSBusinessPartner)businessPartnerProcessor.process(jsBusinessPartner);
//
//            if (businessPartnerList.getEntries() != null) {
//	            for (CMJSBusinessPartner.CGEntries ipEntry : businessPartnerList.getEntries()) {
//	                currentRow++;
//	                HSSFRow row = sheet.createRow(currentRow);
//	                fillPartnerCells(ipEntry, row);
//	            }
//           }
//        } catch (Exception error) {
//            log.error("Error in Business Partner Excel Dowload ::" , error);
//        }
    }
}

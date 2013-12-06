package com.mfino.uicore.web;

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
import com.mfino.fix.CmFinoFIX.CMJSPartner;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.ServicePartnerProcessor;
import com.mfino.util.ConfigurationUtil;

/**
 * @author sasidhar
 * Excel Report for Service Partner list.
 */
@Component("ServicePartnerExcelView")
public class ServicePartnerExcelView extends AbstractExcelView {
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    @Qualifier("ServicePartnerProcessorImpl")
    private ServicePartnerProcessor servicePartnerProcessor;
    
    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "UserName");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "SubscriberID");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "PartnerCode");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "PartnerStatus");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "TradeName");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "TypeOfOrganization");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "FaxNumber");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "WebSite");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "AuthorizedRepresentative");
        
        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "RepresentativeName");
        
        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "Designation");
        
        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "FranchisePhoneNumber");
        
        HSSFCell header14 = getCell(sheet, currentRow, 13);
        setText(header14, "Classification");
        
        HSSFCell header15 = getCell(sheet, currentRow, 14);
        setText(header15, "NumberOfOutlets");
        
        HSSFCell header16 = getCell(sheet, currentRow, 15);
        setText(header16, "IndustryClassification");
        
        HSSFCell header17 = getCell(sheet, currentRow, 16);
        setText(header17, "YearEstablished");
        
        HSSFCell header18 = getCell(sheet, currentRow, 17);
        setText(header18, "AuthorizedFaxNumber");
        
        HSSFCell header19 = getCell(sheet, currentRow, 18);
        setText(header19, "AuthorizedEmail");
    }

    private void fillPartnerCells(CMJSPartner.CGEntries jsServicePartner, HSSFRow row) {
    	row.createCell(0).setCellValue(jsServicePartner.getID());
    	row.createCell(1).setCellValue(jsServicePartner.getUsername());
    	row.createCell(2).setCellValue(jsServicePartner.getSubscriberID());
    	row.createCell(3).setCellValue(jsServicePartner.getPartnerCode());
    	row.createCell(4).setCellValue(jsServicePartner.getPartnerStatusText());
    	row.createCell(5).setCellValue(jsServicePartner.getTradeName());
    	row.createCell(6).setCellValue(jsServicePartner.getTypeOfOrganization());
    	row.createCell(7).setCellValue(jsServicePartner.getFaxNumber());
    	row.createCell(8).setCellValue(jsServicePartner.getWebSite());
    	row.createCell(9).setCellValue(jsServicePartner.getAuthorizedRepresentative());
    	row.createCell(10).setCellValue(jsServicePartner.getRepresentativeName());
    	row.createCell(11).setCellValue(jsServicePartner.getDesignation());
    	row.createCell(12).setCellValue(jsServicePartner.getFranchisePhoneNumber());
    	row.createCell(13).setCellValue(jsServicePartner.getClassification());
    	row.createCell(14).setCellValue(jsServicePartner.getNumberOfOutlets() == null ? 0 : jsServicePartner.getNumberOfOutlets());
    	row.createCell(15).setCellValue(jsServicePartner.getIndustryClassification());
    	row.createCell(16).setCellValue(jsServicePartner.getYearEstablished());
    	row.createCell(17).setCellValue(jsServicePartner.getAuthorizedFaxNumber());
    	row.createCell(18).setCellValue(jsServicePartner.getAuthorizedEmail());
    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String partnerType = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_PartnerTypeSearch);
    	if(StringUtils.isNotBlank(partnerType)&&(CmFinoFIX.TagID_BusinessPartnerTypeAgent==Integer.parseInt(partnerType))){
    		response.setHeader("Content-Disposition", "attachment;filename=Agent.xls");
    	}else{
    		response.setHeader("Content-Disposition", "attachment;filename=Partner.xls");
    	}
    	CMJSPartner jsServicePartner = new CMJSPartner();
        
        String partnerIDSearch = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_PartnerIDSearch);
        
		if((null != partnerIDSearch) && !("".equals(partnerIDSearch))){
			jsServicePartner.setPartnerIDSearch(partnerIDSearch);
		}

        String partnerCodeSearch = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_PartnerCodeSearch);
        
		if((null != partnerCodeSearch) && !("".equals(partnerCodeSearch))){
			jsServicePartner.setPartnerCodeSearch(partnerCodeSearch);
		}
        
        String tradeNameSearch = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_TradeNameSearch);
        
		if((null != tradeNameSearch) && !("".equals(tradeNameSearch))){
			jsServicePartner.setTradeNameSearch(tradeNameSearch);
		}
		
        String authorizedEmailSearch = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_AuthorizedEmailSearch);
        
		if((null != authorizedEmailSearch) && !("".equals(authorizedEmailSearch))){
			jsServicePartner.setAuthorizedEmailSearch(authorizedEmailSearch);
		}
        
        String startDateSearch = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_StartDateSearch);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		if((null != startDateSearch) && !("".equals(startDateSearch))){
          Date parsedDate = dateFormat.parse(startDateSearch);
          Timestamp ts = new Timestamp(parsedDate);
          jsServicePartner.setStartDateSearch(ts);
		}
        
        String endDateSearch = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_EndDateSearch);

		if((null != endDateSearch) && !("".equals(endDateSearch))){
          Date parsedDate = dateFormat.parse(endDateSearch);
          Timestamp ts = new Timestamp(parsedDate);
          jsServicePartner.setStartDateSearch(ts);
		}
		String serviceId = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_ServiceIDSearch);
		if(StringUtils.isNotBlank(serviceId)){
	          jsServicePartner.setServiceIDSearch(Long.parseLong(serviceId));
			}
		String status = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_UpgradeStateSearch);
		if(StringUtils.isNotBlank(status)){
	          jsServicePartner.setUpgradeStateSearch(Integer.parseInt(status));
			}
		int partnerTypesearch = 0;
		if(null!=partnerType&&!"".equals(partnerType)){
			partnerTypesearch=Integer.valueOf(partnerType);
			jsServicePartner.setPartnerTypeSearch(Integer.valueOf(partnerType));
		}
		
        
		jsServicePartner.setaction(CmFinoFIX.JSaction_Select);
		jsServicePartner.setlimit(ConfigurationUtil.getExcelRowLimit());
		     
        try {
        	HSSFSheet sheet;
        	if(CmFinoFIX.TagID_BusinessPartnerTypeAgent==partnerTypesearch){
            sheet = workbook.createSheet("Agents");
        	}else{
        		sheet = workbook.createSheet("Partners");
        	}
            sheet.setDefaultColumnWidth(16);
            int currentRow = 0;
            initializeWorkBook(sheet, currentRow);
            CMJSPartner servicePartnerList = (CMJSPartner)servicePartnerProcessor.process(jsServicePartner);

            if (servicePartnerList.getEntries() != null) {
	            for (CMJSPartner.CGEntries spEntry : servicePartnerList.getEntries()) {
	                currentRow++;
	                HSSFRow row = sheet.createRow(currentRow);
	                fillPartnerCells(spEntry, row);
	            }
           }
        } catch (Exception error) {
            log.error("Error in Service Partner Excel Dowload ::" , error);
        }
    }
}

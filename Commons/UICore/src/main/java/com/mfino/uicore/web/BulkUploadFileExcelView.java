/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import java.util.Iterator;
import java.util.List;
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

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.domain.BulkUploadFileEntry;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkUploadFileEntry;
import com.mfino.service.BulkUploadFileEntryService;
import com.mfino.service.EnumTextService;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author sunil
 */
@Component("BulkUploadFileExcelView")
public class BulkUploadFileExcelView extends AbstractExcelView {

    private static final Integer start = 0;   

    @Autowired
    @Qualifier("BulkUploadFileEntryServiceImpl")
    private BulkUploadFileEntryService bulkUploadFileEntryService;
    
    @Autowired
    @Qualifier("EnumTextServiceImpl")
    private EnumTextService enumTextService;     
    
    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=BulkFileRecords.xls");
    	List<BulkUploadFileEntry> results = getBulkUploadEntryDetails(request);
        HSSFSheet sheet = workbook.createSheet("BulkUploadFile Excel Document");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;        
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "Line Num");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "MDN");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Record Status");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Failure Reason");
        if(results != null) {
        	Iterator<BulkUploadFileEntry> iterator = results.iterator();
        	while(iterator.hasNext()) {
        		currentRow++;
        		BulkUploadFileEntry bulkUploadFileEntry = iterator.next();
            	HSSFRow row = sheet.createRow(currentRow);
                row.createCell(0).setCellValue(bulkUploadFileEntry.getLineNumber());
                String strLine = bulkUploadFileEntry.getLineData();
            	if(strLine != null) {
            		String input[] = strLine.split("\\|"); // Pipe is a special character.
                    if(input.length==1)
                    {
                    	input = strLine.split(GeneralConstants.COMMA_STRING);
                    }
                    if (input.length > 2) {
                        if (CmFinoFIX.RecordType_Agent.equals(bulkUploadFileEntry.getBulkUploadFile().getRecordType())) {
                        	row.createCell(1).setCellValue(input[0]);
                        } else {
                        	row.createCell(1).setCellValue(input[2]);
                        }                       
                    }
            	}            
                row.createCell(2).setCellValue(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadFileEntryStatus, null, bulkUploadFileEntry.getBulkUploadFileEntryStatus()));
                row.createCell(3).setCellValue(bulkUploadFileEntry.getFailureReason());                
        	}
        }
    }

    private List<BulkUploadFileEntry> getBulkUploadEntryDetails(HttpServletRequest request) throws Exception {
        BulkUploadFileEntryQuery query = new BulkUploadFileEntryQuery();
        String id = request.getParameter(CMJSBulkUploadFileEntry.FieldName_IDSearch);	
        if (id != null && id.trim().length() > 0) {
        	query.setUploadFileID(Long.parseLong(id));
            query.setStart(start);
            query.setLimit(ConfigurationUtil.getExcelRowLimit());
            List<BulkUploadFileEntry> results = bulkUploadFileEntryService.get(query);
            return results;
        }
        return null;
    }
}

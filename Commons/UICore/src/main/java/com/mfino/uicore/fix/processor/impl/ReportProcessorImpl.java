/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMJSReport;
import com.mfino.service.ReportService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ReportProcessor;
import com.mfino.util.DateUtil;
import com.mfino.util.ReportUtil;

/**
 *
 * @author Maruthi
 */
@Service("ReportProcessorImpl")
public class ReportProcessorImpl extends BaseFixProcessor implements ReportProcessor{
	FormulaEvaluator evaluator;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
    	CMJSReport realMsg = (CMJSReport) msg;
    	 InputStream file = null;
         File report = null;
         int k=0;
         int split=0;
         boolean xlsreport=false;
         SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
         Date end = dateFormat.parse(realMsg.getReportEndDate());
         Date start = dateFormat.parse(realMsg.getReportStartDate());
    	if(ReportParameterKeys.REPORT_BALANCESHEET.equals(realMsg.getReportName())){   		
    		realMsg.setIsActive(false);
    		report = ReportUtil.getReportFilePath(realMsg.getReportName(),start,end,ReportUtil.EXCEL_EXTENTION);
    		xlsreport= true;
    	}else if(ReportParameterKeys.REPORT_CASHFLOW.equals(realMsg.getReportName())){
    		k=1;
    		split = 25;
    		realMsg.setIsActive(true);
    		xlsreport= true;
    		report = ReportUtil.getReportFilePath(realMsg.getReportName(),start,end,ReportUtil.EXCEL_EXTENTION);
    	}else if(ReportParameterKeys.REPORT_INCOME.equals(realMsg.getReportName())){
    		split = 19;
    		realMsg.setIsActive(true);
    		start = dateFormat.parse(realMsg.getReportStartDate());
    		report = ReportUtil.getReportFilePath(realMsg.getReportName(),start,end);
    		xlsreport= true;
    	}else if(ReportParameterKeys.REPORT_CBNREPORT.equals(realMsg.getReportName())){
    		report = ReportUtil.getReportFilePath(realMsg.getReportName()+"Pdf",start,end,ReportUtil.ZIP_EXTENTION);
    	}else{
    		report = ReportUtil.getReportFilePath(realMsg.getReportName(),start,end,ReportUtil.EXCEL_EXTENTION);
    	}
    	try {
    		  try{
    		  file = new FileInputStream(report.getPath());
    		  }catch (FileNotFoundException ex) {
    		            log.error("File not Found "+ex.getMessage());
    		            sendRequest(realMsg);
    		            file = new FileInputStream(report.getPath());
    		   }
          if(xlsreport) 	{
    	  HSSFWorkbook wb = new HSSFWorkbook(file);
    	  HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
    	  evaluator = wb.getCreationHelper().createFormulaEvaluator();
    	  int i=0;
    	   CMJSReport.CGEntries[] entries = new	CMJSReport.CGEntries[100];
    	  for (Row row : sheet){
    		  if(row.getRowNum()<12){
    			  continue;
    		  }
    		   for(int j=k;j<5;j++){
    		  Cell cell = row.getCell(j);
    		  if(cell!=null&&StringUtils.isNotBlank(getValue(cell))){
    			  entries[i] = new CMJSReport.CGEntries();
    			  entries[i].setIndex(split>0&&row.getRowNum()>=split?1:cell.getColumnIndex()<=1?0:1);
    			 entries[i].setLable(getValue(cell));
    			 cell = row.getCell(++j);
    			 if(cell!=null){
    			 entries[i].setValue(getValue(cell));
    			 }
    			 i++;
    		  }
    		  }
    	  }
    	  realMsg.allocateEntries(i);
    	  for(int j=0;j<i;j++){
    		  realMsg.getEntries()[j]=entries[j];
    	  }
    	  realMsg.settotal(i);
    	  if(start==null){
        	  realMsg.setReportStartDate(dateFormat.format(ReportUtil.getFinacialYearDate(new Date())));
        	  }
          }else{
        	  realMsg.allocateEntries(0);
        	  realMsg.settotal(0);
          }
          realMsg.setReportName(report.getPath());
    	  if(StringUtils.isBlank(realMsg.getReportStartDate())){
    		  if(StringUtils.isBlank(realMsg.getReportEndDate())){
    			  realMsg.setReportStartDate(dateFormat.format(DateUtil.addDays(new Date(),-1)));
    		  }else{
    			  realMsg.setReportStartDate(dateFormat.format(DateUtil.addDays(end,-1)));
    		  }
    	  }
    	  if(StringUtils.isBlank(realMsg.getReportEndDate())){
    	  realMsg.setReportEndDate(dateFormat.format(new Date()));
    	  }
    	  realMsg.setsuccess(true);
    	}catch (Exception e) {
			log.error("Exception processing report request:",e);
		}
		return realMsg;
      
    }

	private void sendRequest(CMJSReport realMsg) {
		ReportService reportService = new ReportService();
        reportService.setReportName(realMsg.getReportName());
        reportService.setStartDate(realMsg.getReportStartDate());
        reportService.setEndDate(realMsg.getReportEndDate());
        HttpResponse response =  reportService.send();
	}

	private String getValue(Cell cell) {
		CellValue cellValue = evaluator.evaluate(cell);
		if(cellValue==null){
			 return StringUtils.EMPTY;
		}
		switch (cellValue.getCellType()) {
	    case Cell.CELL_TYPE_BOOLEAN:
	       return String.valueOf(cellValue.getBooleanValue());
	    case Cell.CELL_TYPE_NUMERIC:
	    	return String.valueOf(cellValue.getNumberValue());
	    case Cell.CELL_TYPE_STRING:
	       return cellValue.getStringValue();
	     default: 
	        return StringUtils.EMPTY;
	}				
	}
}

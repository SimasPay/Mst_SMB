/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMJSReport;
import com.mfino.service.ReportService;
import com.mfino.service.UserService;
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
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	FormulaEvaluator evaluator;
	String generatedReportName;
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
    	CMJSReport realMsg = (CMJSReport) msg;
    	File report = null;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    	Date end = dateFormat.parse(realMsg.getReportEndDate());
    	Date start = dateFormat.parse(realMsg.getReportStartDate());
    	generatedReportName = ReportUtil.generateReportFilePath(realMsg.getReportName(),start,end);
    	report = new File(generatedReportName+ReportUtil.EXCEL_EXTENTION);
    	try {
    		if (!(report.exists())) {
    			sendRequest(realMsg);
    		}
    		realMsg.allocateEntries(0);
    		realMsg.settotal(0);
    		realMsg.setReportName(report.getPath());
    		if(StringUtils.isBlank(realMsg.getReportStartDate())){
    			if(StringUtils.isBlank(realMsg.getReportEndDate())){
    				realMsg.setReportStartDate(dateFormat.format(DateUtil.addDays(new Date(),-1)));
    			}
    			else {
    				realMsg.setReportStartDate(dateFormat.format(DateUtil.addDays(end,-1)));
    			}
    		}
    		if(StringUtils.isBlank(realMsg.getReportEndDate())){
    			realMsg.setReportEndDate(dateFormat.format(new Date()));
    		}
    		realMsg.setsuccess(true);
    	} catch (Exception e) {
    		log.error("Exception processing report request:",e);
    	}
    	return realMsg;
    }

	private void sendRequest(CMJSReport realMsg) {
		ReportService reportService = new ReportService();
        reportService.setReportName(realMsg.getReportName());
        reportService.setStartDate(realMsg.getReportStartDate());
        reportService.setEndDate(realMsg.getReportEndDate());
        reportService.setGeneratedReportName(generatedReportName);
        String logged_in_username = (userService.getCurrentUser()!=null) ? userService.getCurrentUser().getUsername() : "";
        reportService.setUserName(logged_in_username);
        HttpResponse response =  reportService.send();
	}
}

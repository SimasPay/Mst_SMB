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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.mfino.service.MailService;
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
	FormulaEvaluator evaluator;
	
	String generatedReportName;
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;	
	

	@Transactional(readOnly=true, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSReport realMsg = (CMJSReport) msg;
		InputStream file = null;
		File report = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date end = dateFormat.parse(realMsg.getReportEndDate());
		Date start = dateFormat.parse(realMsg.getReportStartDate());
		String reportName = realMsg.getReportName();
		
		generatedReportName = ReportUtil.generateReportFilePath(realMsg.getReportName(),start,end);
		if(realMsg.getTransactionTypeID()!=null){
			generatedReportName = generatedReportName+"-"+realMsg.getTransactionTypeID();
		}
		if(StringUtils.isNotBlank(realMsg.getTransactionStatus())){
			generatedReportName = generatedReportName+"-"+realMsg.getTransactionStatus();
		}
		if(StringUtils.isNotBlank(realMsg.getSourceMDN())){
			generatedReportName = generatedReportName+"-"+realMsg.getSourceMDN();
		}
		if(StringUtils.isNotBlank(realMsg.getDestMDN())){
			generatedReportName = generatedReportName+"-"+realMsg.getDestMDN();
		}
		if(StringUtils.isNotBlank(realMsg.getBillerCode())){
			generatedReportName = generatedReportName+"-"+realMsg.getBillerCode();
		}
		if(StringUtils.isNotBlank(realMsg.getBankRetrievalReferenceNumber())){
			generatedReportName = generatedReportName+"-"+realMsg.getBankRetrievalReferenceNumber();
		}
		if(StringUtils.isNotBlank(realMsg.getSourcePartnerCode())){
			generatedReportName = generatedReportName+"-"+realMsg.getSourcePartnerCode();
		}
		if(StringUtils.isNotBlank(realMsg.getDestPartnerCode())){
			generatedReportName = generatedReportName+"-"+realMsg.getDestPartnerCode();
		}
		if(StringUtils.isNotBlank(realMsg.getChannelName())){
			generatedReportName = generatedReportName+"-"+realMsg.getChannelName();
		}
		if(realMsg.getReferenceNumber()!=null){
			generatedReportName = generatedReportName+"-"+realMsg.getReferenceNumber();
		}
    	report = new File(generatedReportName+ReportUtil.PDF_EXTENTION);

		try {
			try{
				file = new FileInputStream(report.getPath());
			}catch (FileNotFoundException ex) {
				log.info("File not Found "+ex.getMessage());
				sendRequest(realMsg);
			}
			finally{
				if(file != null){
					file.close();
				}
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
			
			// Sending the Report as attchment to the given Email Id.
			List<File> attachments = new ArrayList<File>();
			attachments.add(new File(generatedReportName+ReportUtil.PDF_EXTENTION));
			attachments.add(new File(generatedReportName+ReportUtil.EXCEL_EXTENTION));
			attachments.add(new File(generatedReportName+ReportUtil.TXT_EXTENTION));
			attachments.add(new File(generatedReportName+ReportUtil.CSV_EXTENTION));
			attachments.add(new File(generatedReportName+ReportUtil.XLSX_EXTENTION));
			
			mailService.asyncSendMail(realMsg.getEmail(), "", reportName, "Attached "+reportName, attachments);
			
		}catch (Exception e) {
			log.error("Exception processing report request:",e);
		}
		return realMsg;

	}

	private HttpResponse sendRequest(CMJSReport realMsg) {
		ReportService reportService = new ReportService();
		reportService.setReportParameters(realMsg);
		String loggedInUsername = (userService.getCurrentUser()!=null) ? userService.getCurrentUser().getUsername() : "";
        reportService.setUserName(loggedInUsername);
        reportService.setReportName(realMsg.getReportName());
        HttpResponse response =  reportService.send();
		return response;
	}
}
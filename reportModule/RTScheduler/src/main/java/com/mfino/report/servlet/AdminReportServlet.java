/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.rtscheduler.schedule.DailyReportGenerator;
import com.mfino.util.DateUtil;

public class AdminReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("Post: Got Request:"+request);
		
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("Get: Got Request:"+request);
		processRequest(request, response);
	}

	
	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String reportName=request.getParameter(ReportParameterKeys.REPORT_PARAMETER_NAME);
		String generatedFileName = request.getParameter(ReportParameterKeys.REPORT_PARAMETER_GENERATEDFILENAME);
		String userName = request.getParameter(ReportParameterKeys.REPORT_PARAMETER_USERNAME);
		Date enddate = new Date();
		Date startdate = null;
		try {
			if (StringUtils.isNotBlank(request.getParameter(ReportParameterKeys.REPORT_PARAMETER_STARTDATE))) {
				startdate = dateFormat.parse(request.getParameter(ReportParameterKeys.REPORT_PARAMETER_STARTDATE));
			}
			if (StringUtils.isNotBlank(request.getParameter(ReportParameterKeys.REPORT_PARAMETER_ENDDATE))) {
				enddate = dateFormat.parse(request.getParameter(ReportParameterKeys.REPORT_PARAMETER_ENDDATE));
				if(startdate == null){
					startdate = DateUtil.addDays(enddate, -1);
				}
			}
		DailyReportGenerator report = new DailyReportGenerator();
		
		report.generateReportsOnDemand(startdate,enddate,reportName,generatedFileName, userName);
		
			
	    }catch (Exception e) {
			log.error("Error processing request:",e);
		}
	}


}

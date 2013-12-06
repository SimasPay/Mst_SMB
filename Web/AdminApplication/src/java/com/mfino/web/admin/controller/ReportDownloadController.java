package com.mfino.web.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mfino.util.ReportUtil;

/**
 * 
 * @author Maruthi
 */
@Controller
public class ReportDownloadController {

	@RequestMapping("/reportdownload.htm")
	public ModelAndView download(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String reportName = request.getParameter("reportName");
		String format = request.getParameter("format");
		File report = null;
		if (StringUtils.isNotBlank(reportName)
				&& StringUtils.isNotBlank(format)) {
			if (format.equals(ReportUtil.EXCEL_EXTENTION)) {
				response.setContentType("application/vnd.ms-excel");
			} else {
				response.setContentType("application/pdf");
			}
			if (reportName.substring(reportName.lastIndexOf(".")).equals(ReportUtil.CSV_EXTENTION)
					&& format.equals(ReportUtil.EXCEL_EXTENTION)) {
				report = ReportUtil.getReportFilePath(reportName);
				response.setContentType("application/octet-stream");
			}else if (reportName.indexOf("CBNReportsPdf") != -1) {
				if(format.equals(ReportUtil.PDF_EXTENTION)){
					report = ReportUtil.getReportFilePath(reportName);
					response.setContentType("application/zip");
				}else if(format.equals(ReportUtil.EXCEL_EXTENTION)){
					reportName = reportName.replace("Pdf", "XL");
					report = ReportUtil.getReportFilePath(reportName);
					response.setContentType("application/zip");
				}
				
			}else {
				report = ReportUtil.getReportFilePath(reportName.substring(0,reportName.lastIndexOf(".")) + format);
			}
			
			try {
				InputStream in = new FileInputStream(report);
				response.setHeader("Content-Disposition","attachment;filename=" + report.getName());
				FileCopyUtils.copy(in, response.getOutputStream());
			} catch (Exception e) {
				System.out.println(e);
				response.setContentType("text/plain");
				PrintWriter out = response.getWriter();
				out.write("Unable to download file try again");
			}
		} else {
			response.sendError(response.SC_BAD_REQUEST, "param needed");
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.write("Unable to download file try again");
		}

		return null;

	}
}
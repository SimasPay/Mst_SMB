/**
 * 
 */
package com.mfino.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.service.ReportParametersService;

/**
 * @author Maruthi
 * 
 */
public class ReportUtil {
	public static final String EXCEL_EXTENTION = ".xls";
	public static final String PDF_EXTENTION = ".pdf";
	public static final String CSV_EXTENTION = ".csv";
	public static final String ZIP_EXTENTION = ".zip";

	private static DateFormat df = new SimpleDateFormat("yyyyMMdd");

	public static String generateReportFilePath(String reportName,Date start,Date end) {
		File reportDir = new File(ConfigurationUtil.getReportDir(), df.format(start)+"-"+df.format(end));
		if (!reportDir.exists() || !reportDir.isDirectory()) {
			reportDir.mkdir();
		}
		String reportFile = reportDir.getPath() + File.separator + reportName +"_"+ df.format(start) +"-"+ df.format(end);
		return reportFile;
	}

	public static File getReportFilePath(String reportName) {
		File reportFile = new File(reportName);
		return reportFile;
	}

	public static Date getFinacialYearDate(Date end) {
		Integer fy_day = ReportParametersService
				.getInteger(ReportParameterKeys.FINANCIALYEAR_DAY);
		Integer fy_month = ReportParametersService
				.getInteger(ReportParameterKeys.FINANCIALYEAR_MONTH);

		Calendar cal = new GregorianCalendar(
				ConfigurationUtil.getLocalTimeZone());
		cal.setTime(end);
		Integer fy_year = cal.get(Calendar.YEAR);
		if (fy_month > cal.get(Calendar.MONTH)) {
			fy_year = fy_year - 1;
		} else if (fy_month == cal.get(Calendar.MONTH)
				&& fy_day >= cal.get(Calendar.DATE)) {
			fy_year = fy_year - 1;
		}
		cal.set(fy_year, fy_month, fy_day);
		return cal.getTime();
	}

//	public static File getReportFilePath(String reportName, Date start, Date end) {
//		File reportDir = new File(ConfigurationUtil.getReportDir(),
//				df.format(end));
//		if (!reportDir.exists() || !reportDir.isDirectory()) {
//			reportDir.mkdir();
//		}
//		File reportFile = new File(reportDir.getPath(), reportName
//				+ df.format(start) + "_" + df.format(end)
//				+ ReportUtil.EXCEL_EXTENTION);
//		return reportFile;
//	}

	public static File getPdfReportFilePath(String reportName, Date start,
			Date end) {
		File reportDir = new File(ConfigurationUtil.getReportDir(),
				df.format(end));
		if (!reportDir.exists() || !reportDir.isDirectory()) {
			reportDir.mkdir();
		}
		File reportFile = new File(reportDir.getPath(), reportName
				+ df.format(start) + "_" + df.format(end)
				+ ReportUtil.PDF_EXTENTION);
		return reportFile;
	}
}

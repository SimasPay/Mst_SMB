/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.dao.query.OfflineReportQuery;
import com.mfino.domain.Company;
import com.mfino.domain.OfflineReport;
import com.mfino.domain.OfflineReportReceiver;
import com.mfino.i18n.MessageText;
import com.mfino.report.SubscribersReport;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MailUtil;

/**
 * 
 * @author Maruthi
 */
public class SubscriberOfflineReport {
	private final static String reportClass = "com.mfino.report.SubscribersReport";
	
	private static Logger log = LoggerFactory.getLogger(SubscriberOfflineReport.class);

	private static void sendToReciever(File reportFile, String email,OfflineReport report, Company comp) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				reportFile));

		// GZip the file
		String gZipFileName = reportFile.getAbsolutePath() + ".gz";
		File gzippedFile = new File(gZipFileName);
		FileOutputStream fos = new FileOutputStream(gzippedFile);
		GZIPOutputStream gzos = new GZIPOutputStream(new BufferedOutputStream(fos));
		int count = 0;
		byte[] buffer = new byte[8192];
		while ((count = bis.read(buffer, 0, buffer.length)) > 0) {
			gzos.write(buffer, 0, count);
		}
		gzos.close();
		bis.close();
		log.info("File: " + gzippedFile.getName());
		log.info("File size: " + gzippedFile.length());

		if (gzippedFile.length() <= ConfigurationUtil
				.getEMailAttachmentSizeLimit()) {
			try {
				log.info("sending report to" + email);
				MailUtil.sendMail(email, StringUtils.EMPTY, report.getName(),
						comp == null ? String.format(MessageText._("Here is the offline report")
								+ (" %s."), report.getName()) : String.format(MessageText._("Here is the offline report")
										+ (" %s.")
										+ MessageText._(" for company")
										+ (" %s."), report.getName(), comp.getCompanyName()), gzippedFile);
			} catch (Exception ex) {
				log.error("Error sending mail", ex);
			}
		}
	}

	public static void main(String args[]) {
		Calendar cal = new GregorianCalendar(ConfigurationUtil.getLocalTimeZone());
		cal.setTime(new Date());
		Date end = DateUtils.truncate(cal, Calendar.DATE).getTime();
		Date start = DateUtils.addDays(end, -1);
		HibernateUtil.getCurrentSession().beginTransaction();
		try {
			Long companyid = Long.parseLong(args[0]);
			OfflineReportDAO ofrd = DAOFactory.getInstance().getOfflineReportDAO();
			OfflineReportQuery query = new OfflineReportQuery();
			CompanyDAO cdao = DAOFactory.getInstance().getCompanyDAO();
			Company company = cdao.getById(companyid);
			if (company != null) {
				query.setReprtClass(reportClass);
				List<OfflineReport> offlinereport = ofrd.getOfflineReport(query);
				if (offlinereport.size() > 0) {
					OfflineReport ofr = offlinereport.get(0);
					System.out.println("processing Subscriber Report for "+ company.getCompanyName());
					SubscribersReport sr = new SubscribersReport();
					File reportFile = sr.run(start, end, companyid);
					System.out.println("Subscriber Report for "+ company.getCompanyName() + " processed");
					for (OfflineReportReceiver receiver : ofr.getOfflineReportReceiverFromReportID()) {
						String email = receiver.getEmail();
						sendToReciever(reportFile, email, ofr, company);
					}
				} else {
					System.out.println("No entry for Subscriber report found in offline_report");
					}
			} else {
				System.out.println("No company found with id:" + companyid);
				}
		} catch (Exception exp) {
			log.error("Error in Subscriber report", exp);
			HibernateUtil.getCurrentTransaction().rollback();
		}
		finally{
			HibernateUtil.getCurrentTransaction().rollback();
		}
	}
}

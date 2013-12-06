package com.mfino.report.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.OfflineReport;
import com.mfino.domain.OfflineReportReceiver;
import com.mfino.i18n.MessageText;
import com.mfino.service.impl.MailServiceImpl;
import com.mfino.util.ConfigurationUtil;

public class ReportMailUtil {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void sendReports(OfflineReport report,List<File> files){
		try {
			for (File eachReportfile : files) {
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(eachReportfile));

				// GZip the file
				String gZipFileName = eachReportfile.getAbsolutePath() + ".gz";
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

				// deliver all the reports
				log.info("Sending...");

				for (OfflineReportReceiver receiver : report.getOfflineReportReceiverFromReportID()) {
					log.info("To: " + receiver.getEmail());

					log.info("File: " + gzippedFile.getName());
					log.info("File size: " + gzippedFile.length());

					if (gzippedFile.length() <= ConfigurationUtil.getEMailAttachmentSizeLimit()) {
						try {
							MailServiceImpl mailServiceImpl = new MailServiceImpl();
							mailServiceImpl.sendMail(receiver.getEmail(),
									StringUtils.EMPTY,
									report.getName(),
									String.format(MessageText._("Here is the offline report")+ (" %s."),report.getName()),
									gzippedFile);
						} catch (Exception ex) {
							log.error("Error sending mail", ex);
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error in mailing report", e);
		}
	}

	public void sendReport(OfflineReport report, File reportFile) {
		List<File> files = new ArrayList<File>();
		files.add(reportFile);
		sendReports(report, files);
		
	}

}

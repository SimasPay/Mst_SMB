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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.domain.Company;
import com.mfino.domain.OfflineReport;
import com.mfino.domain.OfflineReportForCompany;
import com.mfino.domain.OfflineReportReceiver;
import com.mfino.i18n.MessageText;
import com.mfino.module.DefaultModule;
import com.mfino.report.OfflineReportBase;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MailUtil;

/**
 *
 * @author xchen
 */
public class SmartOfflineReport {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
    Injector injector = null;

    public SmartOfflineReport(){
    	this.injector = Guice.createInjector(new DefaultModule());
    }
    public SmartOfflineReport(Injector injector) {
        this.injector = injector;
    }

    public static void main(String[] args) {
        System.out.println("Generating offline reports...");
        SmartOfflineReport program = new SmartOfflineReport();
        program.run();
    }

    private void run() {
        //get the timezone and current day, start time and end time
        Calendar cal = new GregorianCalendar(ConfigurationUtil.getLocalTimeZone());
        cal.setTime(new Date());
        Date end = DateUtils.truncate(cal, Calendar.DATE).getTime();
        Date start = DateUtils.addDays(end, -1);
        
        log.info("Starting offline report processing for " + SimpleDateFormat.getInstance().format(start) + " to " + SimpleDateFormat.getInstance().format(end));
        try {
            Session session = HibernateUtil.getCurrentSession();
            session.beginTransaction();
            //load all report records
            OfflineReportDAO dao = DAOFactory.getInstance().getOfflineReportDAO();
            List<OfflineReport> reports = dao.getAll();
            //initialize the lazy loading property. maybe there is a cleaner way. 
            for (OfflineReport report : reports) {
                Hibernate.initialize(report.getOfflineReportReceiverFromReportID());
                Hibernate.initialize(report.getOfflineReportForCompanyFromReportID());
                for(OfflineReportForCompany compRpt : report.getOfflineReportForCompanyFromReportID())
                {
                    Hibernate.initialize(compRpt.getCompany());
                }
            }
            //now commits the transaction. we still need to figure out a way to better identify when and where 
            //transaction start and ends. 
            HibernateUtil.getCurrentTransaction().commit();

            //execute all the reports
            for (OfflineReport report : reports) {
                log.info("Processing offline report: " + report.getName());
                
                File reportFile = null;
                if (StringUtils.isNotEmpty(report.getReportSql())) {
                    String sql = report.getReportSql();
                    sql = replaceParams(sql, start, end);
                    reportFile = executeSqlReport(report.getName(), sql);

                    //deliver all the reports
                    //TODO: need to figure out how to email an attachment
                    // this will also involve changing the mailer service
                    log.info("Sending...");
                    for (OfflineReportReceiver receiver : report.getOfflineReportReceiverFromReportID()) {
                        log.info("To: " + receiver.getEmail());
                        
                        MailUtil.sendMailMultiX(receiver.getEmail(),
                                StringUtils.EMPTY, report.getName(),
                                FileUtils.readFileToString(reportFile));
                    }
                } else {
                    for(OfflineReportForCompany compReport : report.getOfflineReportForCompanyFromReportID()){
                        Company comp = compReport.getCompany();
                        List<File> files = executeClassReport(report.getReportClass(), start, end, comp == null ? null : comp.getID());
                        if(files == null){
                            log.error("Class " + report.getReportClass() + " did not return any file.");
                            continue;
                        }

                        for (File eachReportfile : files) {
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(eachReportfile));

                          //GZip the file
                            String gZipFileName = eachReportfile.getAbsolutePath() + ".gz";
                            File gzippedFile = new File(gZipFileName);
                            FileOutputStream fos = new FileOutputStream(gzippedFile);
                            GZIPOutputStream gzos = new GZIPOutputStream(new BufferedOutputStream(fos));

                            int count = 0;
                            byte[] buffer = new byte[8192];
                            while( (count = bis.read(buffer, 0, buffer.length)) > 0) {
                              gzos.write(buffer, 0, count);
                            }

                            gzos.close();
                            bis.close();

                            //deliver all the reports
                            log.info("Sending...");

                            for (OfflineReportReceiver receiver : report.getOfflineReportReceiverFromReportID()) {
                                log.info("To: " + receiver.getEmail());

                                log.info("File: " + gzippedFile.getName());
                                log.info("File size: " + gzippedFile.length());

                                if(gzippedFile.length() <= ConfigurationUtil.getEMailAttachmentSizeLimit()) {
                                  try {
                                  MailUtil.sendMail(receiver.getEmail(),
                                        StringUtils.EMPTY, report.getName(),
                                        comp == null ?
                                        String.format(MessageText._("Here is the offline report") +(" %s."), report.getName())
                                        : String.format(MessageText._("Here is the offline report") +(" %s.") + MessageText._(" for company") +(" %s.")
                                        , report.getName(), comp.getCompanyName()),
                                        gzippedFile);
                                  } catch(Exception ex) {
                                    log.error("Error sending mail", ex);
                                    continue;
                                  }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            try {
                HibernateUtil.getCurrentTransaction().rollback();
            } catch (Throwable innerT) {
                log.error("Failed to rollback transaction", innerT);
            }
            log.error("Error in SmartOfflineReport", t);
        }
    }

    private File executeSqlReport(String reportName, String sql) throws IOException {
        SQLQuery query = HibernateUtil.getCurrentSession().createSQLQuery(sql);
        List result = query.list();
        return saveToCSV(reportName, result);
    }

    private List<File> executeClassReport(String className, Date start, Date end, Long companyID) {
        List<File> result = null;
        try {
        	@SuppressWarnings("unchecked")
			Class<OfflineReportBase> c = (Class<OfflineReportBase>)Class.forName(className);
        	if(c == null){
        		log.error("Bad report class name:" + className);
        		return new ArrayList<File>();
        	}
            OfflineReportBase obj = injector.getInstance(c);

            if (obj.hasMultipleReports()) {
                result = obj.runAndGetMutlipleReports(start, end, companyID);
            } else {
                File resultantFile = obj.run(start, end, companyID);
                result = new ArrayList<File>();
                result.add(resultantFile);
            }
        } catch (IllegalArgumentException ex) {
            log.error("Error in SmartOfflineReport" + className, ex);
        } catch (ClassNotFoundException ex) {
            log.error("Error in SmartOfflineReport: Cannot load class " + className, ex);
        }
        return result;
    }

    private File saveToCSV(String reportName, List result) throws IOException {
        File reportFile = com.mfino.report.OfflineReportBase.getReportFilePath(reportName);
        PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
        for (Object obj : result) {
            if (obj instanceof Object[]) {
                Object[] resultArr = (Object[]) obj;
                writer.print(resultArr[0]);
                for (int i = 1; i < resultArr.length; i++) {
                    writer.print("," + resultArr[i]);
                }
            } else {
                writer.print(obj);
            }
            writer.println();
        }
        writer.close();
        return reportFile;
    }

    private String replaceParams(String sql, Date start, Date end) {
        return sql.replaceAll("{StartTime}", DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).format(start)).replaceAll("{EndTime}", DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).format(end));
    }
}

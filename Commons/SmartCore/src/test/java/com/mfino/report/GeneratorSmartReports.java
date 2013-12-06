/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.domain.OfflineReport;
import com.mfino.module.DefaultModule;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Raju
 */
class StreamGobbler extends Thread {

    InputStream is;
    String type;

    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(type + ">" + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

class OnlyExt implements FilenameFilter {

    String ext;

    public OnlyExt(String extension) {
        ext = "." + extension;
    }

    public boolean accept(File dir, String name) {
        return name.endsWith(ext);
    }
}

public class GeneratorSmartReports {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    Injector injector = null;

    public GeneratorSmartReports() {
        this.injector = Guice.createInjector(new DefaultModule());
    }

    public GeneratorSmartReports(Injector injector) {
        this.injector = injector;
    }

    public static void main(String[] args) throws ParseException {
        // Keep all the backup files in a folder named EC2_Backups in C directory : "C:/EC2_Backups";
        //Make Sure that Temp Folder doesnot contain any .CSV files before running. Remove all the unnecessary folders.
        //On completion we can get the reports moved into the respective folders named date of the report.
        //Make Sure that there is no backup files with the same file name with time changed.
        String dirname = "C:/EC2_Backups";
        File f1 = new File(dirname);
        FilenameFilter only = new OnlyExt("sql");
        FilenameFilter onlycsv = new OnlyExt("csv");
        String s[] = f1.list(only);
        int i;
        for (i = 0; i < s.length; i++) {
            int indexof = s[i].indexOf("y");
            String substring = s[i].substring(indexof + 2, indexof + 10);
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            Date stdate = df.parse(substring);
            Date enddate = DateUtils.addDays(stdate, +1);
            System.out.println(substring);
            String mysql = "mysql --user=mfino --password=mFino260 mfino <";
            String toBeExecuted = mysql + s[0];
            System.out.println("To Be Executed" + toBeExecuted);
            try {
                String osName = System.getProperty("os.name");
                String[] cmd = new String[3];
                if (osName.equals("Windows XP")) {
                    cmd[0] = "cmd.exe";
                    cmd[1] = "/C";
                    cmd[2] = toBeExecuted;
                } else {
                    System.out.println("It workds only for Windows XP");
                    return;
                }
                Runtime rt = Runtime.getRuntime();
                System.out.println("Executing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
                Process proc = rt.exec(cmd);
                // any error message?
                StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
                // any error???
                int exitVal = proc.waitFor();
                System.out.println("ExitValue: " + exitVal);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            System.out.println("Generating offline reports...");
            GeneratorSmartReports program = new GeneratorSmartReports();
            program.runMerchantReport(stdate, enddate);
            //code for moving the files to a folder
            {
                String move2Folder = System.getProperty("java.io.tmpdir") + substring;
                File movefiledest = new File(move2Folder);
                File movefilesource = new File(System.getProperty("java.io.tmpdir"));
                if (movefiledest.mkdir()) {
                    System.out.println("Created" + substring);
                } else {
                    System.exit(1);
                }
                String csvfiles[] = movefilesource.list(onlycsv);
                for (int k = 0; k < csvfiles.length; k++) {
                    File temp = new File(System.getProperty("java.io.tmpdir") + csvfiles[k]);
                    File tempfile = new File(move2Folder);
                    String t = tempfile + "\\" + csvfiles[k];
                    if (temp.renameTo(new File(t))) {
                        System.out.println("File Name" + csvfiles[k]);
                    }
                }
            }
        }
    }

    private void runMerchantReport(Date startDate, Date endDate) {
        MerchantReport aReport = new MerchantReport(new MerchantDAO());
        aReport.run(startDate, endDate);
    }

    private void run(Date startDate, Date endDate) {
        //get the timezone and current day, start time and end time
        Calendar cal = new GregorianCalendar(ConfigurationUtil.getLocalTimeZone());
        cal.setTime(new Date());
        Date end = endDate;
        Date start = startDate;

        log.info("Starting offline report processing for " + SimpleDateFormat.getInstance().format(start) + " to " + SimpleDateFormat.getInstance().format(end));
        try {
            org.hibernate.Session session = HibernateUtil.getCurrentSession();
            session.beginTransaction();
            //load all report records
            OfflineReportDAO dao = new OfflineReportDAO();
            List<OfflineReport> reports = dao.getAll();
            //initialize the lazy loading property. maybe there is a cleaner way.
            for (OfflineReport report : reports) {
                Hibernate.initialize(report.getOfflineReportReceiverFromReportID());
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
                    sql =
                            replaceParams(sql, start, end);
                    reportFile =
                            executeSqlReport(report.getName(), sql);


                    //deliver all the reports
                    //TODO: need to figure out how to email an attachment
                    // this will also involve changing the mailer service
//                    DefaultLogger.info("Sending...");
//                    for (OfflineReportReceiver receiver : report.getOfflineReportReceiverFromReportID()) {
//                        DefaultLogger.info("To: " + receiver.getEmail());
//
//                        MailUtil.sendMailMultiX(receiver.getEmail(),
//                                StringUtils.EMPTY, report.getName(),
//                                FileUtils.readFileToString(reportFile));
//                    }
                } else {
                    List<File> files = executeClassReport(report.getReportClass(), start, end);
                    if (files == null) {
                        log.error("Class " + report.getReportClass() + " did not return any file.");
                        continue;

                    }

//                    for (File eachReportfile : files) {
//                        //deliver all the reports
//                        DefaultLogger.info("Sending...");
//                        for (OfflineReportReceiver receiver : report.getOfflineReportReceiverFromReportID()) {
//                            DefaultLogger.info("To: " + receiver.getEmail());
//
//                            MailUtil.sendMail(receiver.getEmail(),
//                                    StringUtils.EMPTY, report.getName(),
//                                    String.format(MessageText._("Here is the offline report ") +("%s."), report.getName()),
//                                    eachReportfile);
//                        }
//                    }

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

    private List<File> executeClassReport(String className, Date start, Date end) {
        List<File> result = null;
        try {
            @SuppressWarnings("unchecked")
            Class<OfflineReportBase> c = (Class<OfflineReportBase>) Class.forName(className);
            if (c == null) {
                log.error("Bad report class name:" + className);
                return new ArrayList<File>();
            }

            OfflineReportBase obj = injector.getInstance(c);

            if (true == obj.hasMultipleReports()) {
                result = obj.runAndGetMutlipleReports(start, end);
            } else {
                File resultantFile = obj.run(start, end);
                result =
                        new ArrayList<File>();
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
                for (int i = 1; i <
                        resultArr.length; i++) {
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


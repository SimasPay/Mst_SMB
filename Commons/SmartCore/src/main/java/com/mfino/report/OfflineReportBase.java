/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.Company;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.SystemParametersUtil;

/**
 *
 * @author xchen
 */
public abstract class OfflineReportBase {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
    public boolean hasMultipleReports() {
        return false;
    }

    public abstract String getReportName();

    public abstract File run(Date start, Date end);

    public abstract File run(Date start, Date end, Long companyID);
    /*
     * Should be overridden by Reports which has 
     */
    public List<File> runAndGetMutlipleReports(Date start, Date end) {
        throw new UnsupportedOperationException();
    }
     /*
     * Should be overridden by Reports which has
     */
    public List<File> runAndGetMutlipleReports(Date start, Date end, Long CompanyId) {
        throw new UnsupportedOperationException();
    }
    
    protected File getReportFilePath() {
        return getReportFilePath(getReportName(), null);
    }

    protected File getReportFilePath(Company company) {
        return getReportFilePath(getReportName(), company);
    }

    public static File getReportFilePath(String reportName) {
        return getReportFilePath(reportName, null);
    }

    public static File getReportFilePath(String reportName, Company company) {
        File reportFile;
        if(company == null)
            reportFile = new File(ConfigurationUtil.getTempDir(), reportName + getTimeStamp() + ".csv");
        else
            reportFile = new File(ConfigurationUtil.getTempDir(), company.getCompanyName() + "_" + reportName + getTimeStamp() + ".csv");
        return reportFile;
    }

    public static String getTimeStamp() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        return sf.format(d);
    }

    protected static String getFormatString(int columns){
        String formatStr = "%s";
        for(int i = 0; i < columns - 1; i++){
            formatStr += ",%s";
        }
        return formatStr;
    }
    
    protected DateFormat getDateFormat(){
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	// Get the Time zone from System Parameters
        TimeZone zone = TimeZone.getTimeZone(SystemParametersUtil.getString(SystemParameterKeys.TIME_ZONE));
        df.setTimeZone(zone);
        return df;
    }
    
    protected DateFormat getDateFormat(String format){
    	DateFormat df = new SimpleDateFormat(format);
    	// Get the Time zone from System Parameters
    	TimeZone zone = TimeZone.getTimeZone(SystemParametersUtil.getString(SystemParameterKeys.TIME_ZONE));
        df.setTimeZone(zone);
        return df;
    }
    
}

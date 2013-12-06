/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author xchen
 */
public class DateUtil {
	public static Log log = LogFactory.getLog(DateUtil.class);
	public static final String defaultDateFormat = "dd/MM/yyyy";

    public static Date addDays(Date d, int num){
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        cl.add(Calendar.DATE, num);

        return cl.getTime();
    }
    
    public static Date addMinutes(Date d, int num){
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        cl.add(Calendar.MINUTE, num);

        return cl.getTime();
    }
    
    public static Date addHours(Date d, int num){
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        cl.add(Calendar.HOUR, num);

        return cl.getTime();
    }
    public static Date addYears(Date d, int num){
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        cl.add(Calendar.YEAR, num);

        return cl.getTime();
    }
    
    public static String getFormattedDate() {
    	SimpleDateFormat sdf = new SimpleDateFormat();
    	return sdf.format(new Date());
    }
    public static String getFormattedDate(Date date, String dateFormat) {
    	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    	String formattedDate = sdf.format(date);
    	return formattedDate;
    }
    public static String getFormattedDate(Date date) {
    	return getFormattedDate(date, defaultDateFormat);
    }
    public static Date getDate(String dateString, String dateFormat){
    	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    	Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			log.error("DateUtil :: getDate(String dateString, String dateFormat) dateFormat="+dateFormat+", dateString="+dateString, e);
			throw new RuntimeException(e);
		}
    	return date;
    }
    
    public static Date getDate(String dateString){
    	try{
    		return getDate(dateString, defaultDateFormat);
    	}
    	catch(Exception e){
    		log.error("DateUTil : getDate() ", e);
    		throw new RuntimeException(e);
    	}
    }
    
    /*
     * This method is used by ExcelView classes(UICore)
     */
    public static DateFormat getExcelDateFormat() {
    	DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
        // Making timezone as a configurable property
        TimeZone zone = ConfigurationUtil.getLocalTimeZone();
        df.setTimeZone(zone);
        return df;
    }
}

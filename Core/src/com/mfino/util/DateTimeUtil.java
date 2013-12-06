/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.mfino.hibernate.Timestamp;

/**
 *
 * @author xchen
 */
public class DateTimeUtil {
    public static Date addDays(Date d, int num){
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        cl.add(Calendar.DATE, num);

        return cl.getTime();
    }
    
    public static String getFormattedDate() {
    	SimpleDateFormat sdf = new SimpleDateFormat();
    	return sdf.format(new Date());
    }
    
    public static Timestamp getLocalTime() {
		Calendar cal = null;
		cal = Calendar.getInstance(TimeZone.getDefault());
		return new Timestamp(cal.getTime());
	}
	public static Timestamp getGMTTime() {
		Timestamp ts = null;
		Calendar cal = null;
		cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		return new Timestamp(cal.getTime());
	}
	
	public static Date addMinutes(Date d, int num){
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        cl.add(Calendar.MINUTE, num);
        return cl.getTime();
    }
	
	public static Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	public static Date addHours(Date d, int num){
        Calendar cl = Calendar.getInstance();
        cl.setTime(d);
        cl.add(Calendar.HOUR, num);
        return cl.getTime();
    }
}

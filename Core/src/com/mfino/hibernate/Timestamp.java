/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.hibernate;

import com.mfino.fix.CUTCTimeStamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author xchen
 */
public class Timestamp extends java.sql.Timestamp {

    public Timestamp() {
        super(new Date().getTime());
    }

    public Timestamp(long ticks) {
        super(ticks);
    }

    public static Timestamp fromString(String s){
        CUTCTimeStamp ts = CUTCTimeStamp.fromString(s);
        return ts.getDate();
    }

    //yyyyMMdd-HH:mm:ss.SSS
    public static Timestamp fromString(String s,String format){
    	if(StringUtils.isBlank(s))
    		return null;
        CUTCTimeStamp ts = CUTCTimeStamp.fromString(s,format);
        return ts.getDate();
    }
    
    public Timestamp(Date d) {
        super(d.getTime());
        if (d instanceof java.sql.Timestamp) {
            this.setNanos(((java.sql.Timestamp) d).getNanos());
        }
    }
}

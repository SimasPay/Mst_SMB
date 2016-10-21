package com.mfino.fix;

import com.mfino.hibernate.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.TimeZone;

/**
 * Summary description for CUTCTimeStamp.
 */
public class CUTCTimeStamp {

    private static String m_DateFormatString = "yyyyMMdd-HH:mm:ss.SSS";
    private Timestamp m_Date;

    public Timestamp getDate() {
        return m_Date;
    }

    private void setDate(Date d) {
    	if (d != null) {
    		m_Date = new Timestamp(d.getTime());
		}
    }

    public String getDateFormatString() {
        return m_DateFormatString;
    }

    public CUTCTimeStamp() {
        setTimeNow();
    }

    public CUTCTimeStamp(Date d) {
        setDate(d);
    }

    public CUTCTimeStamp(CUTCTimeStamp NewValue) {
        m_Date = NewValue.m_Date;
    }

    public void clear() {
        m_Date = null;
    }

    public void setTimeNow() {
        setDate(new Timestamp());
    }

    public static CUTCTimeStamp now() {
        return new CUTCTimeStamp();
    }

    public static CUTCTimeStamp fromString(String S) {
        CUTCTimeStamp returnValue = new CUTCTimeStamp();
        SimpleDateFormat Format = new SimpleDateFormat(m_DateFormatString);
        Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        returnValue.setDate(Format.parse(S, new ParsePosition(0)));
        return returnValue;
    }

    public static CUTCTimeStamp fromString(String S,String format) {
        CUTCTimeStamp returnValue = new CUTCTimeStamp();
        SimpleDateFormat Format = new SimpleDateFormat(format);
        Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        returnValue.setDate(Format.parse(S, new ParsePosition(0)));
        return returnValue;
    }

    
    @Override
    public String toString() {
        if (m_Date == null) {
            return "";
        }
        SimpleDateFormat Format = new SimpleDateFormat(m_DateFormatString);
        Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Format.format(m_Date);
    }

    public boolean EQ(CUTCTimeStamp Other) {
        return m_Date.getTime() == Other.m_Date.getTime();
    }

    public boolean NE(CUTCTimeStamp Other) {
        return m_Date.getTime() != Other.m_Date.getTime();
    }

    public boolean LT(CUTCTimeStamp Other) {
        return m_Date.getTime() < Other.m_Date.getTime();
    }

    public boolean LE(CUTCTimeStamp Other) {
        return m_Date.getTime() <= Other.m_Date.getTime();
    }

    public long Sub(CUTCTimeStamp Other) {
        return m_Date.getTime() - Other.m_Date.getTime();
    }
}

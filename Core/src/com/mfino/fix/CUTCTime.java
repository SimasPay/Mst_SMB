package com.mfino.fix;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.TimeZone;

/**
 * Summary description for CUTCTime.
 */
public class CUTCTime {

    public Date m_Date;

    public CUTCTime() {
        m_Date = null;
    }

    public static CUTCTime FromString(String S) {
        CUTCTime RetVal = new CUTCTime();
        SimpleDateFormat Format = new SimpleDateFormat("HH:mm:ss.SSS");
        Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            RetVal.m_Date = Format.parse(S, new ParsePosition(0));
        return RetVal;
    }

    public String ToString() {
        SimpleDateFormat Format = new SimpleDateFormat("HH:mm:ss.SSS");
        Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Format.format(m_Date);
    }
}

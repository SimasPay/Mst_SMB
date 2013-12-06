package com.mfino.fix;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.TimeZone;

/**
 * Summary description for CUTCDate.
 */
public class CUTCDate {

    public Date m_Date;

    public CUTCDate() {
        m_Date = null;
    }

    public CUTCDate(CUTCDate NewVal) {
        m_Date = NewVal.m_Date;
    }

    public boolean EQ(CUTCDate Other) {
        return m_Date.compareTo(Other.m_Date) == 0;
    }

    public boolean NE(CUTCDate Other) {
        return m_Date.compareTo(Other.m_Date) != 0;
    }

    public static CUTCDate FromString(String S) {
        CUTCDate RetVal = new CUTCDate();
        SimpleDateFormat Format = new SimpleDateFormat("yyyyMMdd");
        Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            RetVal.m_Date = Format.parse(S, new ParsePosition(0));
        return RetVal;
    }

    public String ToString() {
        SimpleDateFormat Format = new SimpleDateFormat("yyyyMMdd");
        Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Format.format(m_Date);
    }
}

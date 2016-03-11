package com.mfino.bsim.iso8583.utils;

import java.util.TimeZone;

import com.mfino.hibernate.Timestamp;

public class DateTimeFormatter {
	
	public static String getMMDDHHMMSS(Timestamp timeStamp) {
		
		TimeZone tz = TimeZone.getDefault();
		Timestamp ts = new Timestamp(timeStamp.getTime() - tz.getRawOffset());
		
		String str="%Tm%<Td%<TH%<TM%<TS";
		return formatDateTime(str, ts);
	}
	public static String getMMDD(Timestamp timeStamp) {
		String str="%Tm%<Td";
		return formatDateTime(str, timeStamp);
	}
	public static String getYYMM(Timestamp timeStamp) {
		String str="%Ty%<Tm";
		return formatDateTime(str, timeStamp);
	}
	public static String getHHMMSS(Timestamp timeStamp) {
		String str="%TH%<TM%<TS";
		return formatDateTime(str, timeStamp);
	}
	public static String getCCYYMMDD(Timestamp timeStamp) {
		String str="%TC%<Ty%<Tm%<Td";
		return formatDateTime(str, timeStamp);
	}
	private static String formatDateTime(String format,Timestamp ts) {
		return String.format(format, ts);
	}

}

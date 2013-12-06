package com.mfino.iframework;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mfino.hibernate.Timestamp;

public class DateTimeFormatter {
	
	public static String getMMDDHHMMSS(Timestamp timeStamp) {
		String str="%Tm%<Td%<TH%<TM%<TS";
		return formatDateTime(str, timeStamp);
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
	public static String getYYYYMMDDHHMMSS(Timestamp timestamp){
		String str = "yyyyMMddHHmmss";
		SimpleDateFormat format = new SimpleDateFormat(str);
		return format.format(timestamp);
	}
	
	public static void main(String[] args){
		
		System.out.println(getYYYYMMDDHHMMSS(new Timestamp((new Date()).getTime())));
		
	}
}

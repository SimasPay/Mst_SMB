package com.mfino.fidelity.iso8583.utils;

import com.mfino.hibernate.Timestamp;

public class DateTimeFormatter {
	
	public static String getYYYYMMDDhhmmss(Timestamp timeStamp) {
		String str="%TY%<Tm%<Td%<TH%<TM%<TS";
		return formatDateTime(str, timeStamp);
	}
	
	public static String getYYYYMMDD(Timestamp timeStamp) {
		String str="%TY%<Tm%<Td";
		return formatDateTime(str, timeStamp);
	}
	
	public static String getMMDD(Timestamp timeStamp) {
		String str="%Tm%<Td";
		return formatDateTime(str, timeStamp);
	}
	
	private static String formatDateTime(String format,Timestamp ts) {
		return String.format(format, ts);
	}
	
	
	public static void main(String a[]){
		System.out.println(getYYYYMMDDhhmmss(new Timestamp()));
	}

	
	
}

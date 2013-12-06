package com.mfino.bsm.iso8583.nm;

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
	public static String getYYMMDD(Timestamp timeStamp) {
		String str="20%Ty-%<Tm-%<Td";
		return formatDateTime(str, timeStamp);
	}
	
	public static void main(String... args){
		Timestamp ts = new Timestamp();
		System.out.println(getYYMMDD(ts));
	}

}
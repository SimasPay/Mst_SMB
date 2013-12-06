package com.mfino.zte.iso8583;

import com.mfino.hibernate.Timestamp;
import com.mfino.zte.iso8583.utils.DateTimeFormatter;

public class GetConstantCodes {
	
	public static String SUCCESS = "00";
	

	
	/**
	 * Returns the next day time stamp
	 * @return
	 */
	public static String getDE14(Timestamp currentTime)
	{
		//get the next month date
		return DateTimeFormatter.getYYMM(new Timestamp(currentTime.getTime()+30*24*60*60*1000));
	}
	

}

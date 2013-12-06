package com.mfino.mce.iso.jpos.test.server;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * 
 * @author Sreenath
 *
 */

public class TransactionDate {
public static String getTransactionDate(){
	 Calendar currentDate = Calendar.getInstance();
	  SimpleDateFormat formatter= 
	  new SimpleDateFormat("yyyyMMddHHmmss");
	  String dateNow = formatter.format(currentDate.getTime());
	  //System.out.println("Now the date is :=>  " + dateNow);
	  return dateNow;
}
}

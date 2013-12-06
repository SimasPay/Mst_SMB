package com.mfino.scheduler.settlement;

/**
 * 
 * @author sasidhar
 * Scheduler related constants.
 */
public class SchedulerConstants {
	
/*	//0 0/2 8-17 * * ?
	//0 0/5 * * * ? trigger that runs every 5 minutes 
	public static final String CRON_DAILY = "0 0/5 * * * ?";  //0 0 2 * * ? runs every night at 2 am
	public static final String CRON_WEEKLY = "0 0 15 ? * WED";  //"0 0 15 ? * WED"))  fire every wednesday at 15:00
	public static final String CRON_MONTHLY = "0 0 15 5 * ?"; //"0 0 15 5 * ?" fire on the 5th day of every month at 15:00
*/	
	public static final String CRON_DAILY = "CRON_DAILY";  
	public static final String CRON_WEEKLY = "CRON_WEEKLY";  
	public static final String CRON_MONTHLY = "CRON_MONTHLY";
	public static final String CRON_EVERY_HOUR = "CRON_EVERY_HOUR";
}

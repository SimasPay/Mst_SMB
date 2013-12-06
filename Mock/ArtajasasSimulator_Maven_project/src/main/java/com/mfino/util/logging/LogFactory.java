package com.mfino.util.logging;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

public class LogFactory {
	private static boolean isInit = false;
	
	public static ILogger getLogger() {
		
		if(!isInit){
			isInit = true;
                        String path =
                            LogFactory.class.getProtectionDomain().getCodeSource()
                                .getLocation().toString();
                        path = path.substring(6,path.length()-15).concat("log4j.xml");
			if(path != null){
				File configFile = new File(path);
				System.out.println("Trying to load log4j configuration file " + configFile.getAbsolutePath());
				DOMConfigurator.configureAndWatch(configFile.getAbsolutePath());
			}
			else
			{
				System.out.println("No Log4j configuration specified, use basic profile");
				BasicConfigurator.configure();
			}
		}
		
		StackTraceElement[] stackTrac = Thread.currentThread().getStackTrace();
		String callingClass = LogFactory.class.getName();

		for (int i = 1; i < stackTrac.length; i++) {
			if (stackTrac[i].getClassName().equals(callingClass)) {
				callingClass = stackTrac[i + 1].getClassName();
				break;
			}
		}

		return new Log4JLogger(callingClass);
	}
}

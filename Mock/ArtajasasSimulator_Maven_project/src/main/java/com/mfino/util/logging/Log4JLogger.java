package com.mfino.util.logging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Log4JLogger implements ILogger {

	private Logger log = null;
	
	public Log4JLogger(String name){
		log = LogManager.getLogger(name);
	}
	
	public void debug(String msg) {
		log.debug(msg);
	}

	public void debug(String msg, Throwable t) {
		log.debug(msg, t);
	}

	public void error(String msg) {
	    // NOTE :: I am not sure why the hell we are sending a new Exception
	    // when we want to just log the error message.
	    // Changing this for now. BUT NEED TO INVESTIGATE ON WHY WE HAD THIS
	    // in the first place.
		//log.error(msg, new Exception());
	    log.error(msg);
	}

	public void error(String msg, Throwable t) {
		log.error(msg, t);
	}

	public void info(String msg) {
		log.info(msg);
	}

	public void info(String msg, Throwable t) {
		log.info(msg, t);
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return true;
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	public String getName(){
		return log.getName();
	}
}

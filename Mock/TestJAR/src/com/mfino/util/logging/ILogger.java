package com.mfino.util.logging;

public interface ILogger {

	public String getName();
	
	public boolean isDebugEnabled();

	public void debug(String msg);

	public void debug(String msg, Throwable t);

	public boolean isInfoEnabled();

	public void info(String msg);

	public void info(String msg, Throwable t);

	public boolean isErrorEnabled();

	public void error(String msg);

	public void error(String msg, Throwable t);
}

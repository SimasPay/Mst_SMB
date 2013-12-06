package com.mfino.mce.notification;

import org.apache.camel.Processor;

public interface EmailNotificationService extends Processor
{
		
	public String getHostName();
	
	public void setHostName(String hostName);
	
	public String getSmtpPort();
	
	public void setSmtpPort(String smtpPort);
	
	public String getUserName();
	
	public void setUserName(String userName);
	
	public String getPassword();
	
	public void setPassword(String password);
	
	public boolean getMailServerRequireAuthentication();
	
	public void setMailServerRequireAuthentication(boolean mailServerRequireAuthentication);
	
	public boolean getIsSSL();
	
	public void setIsSSL(boolean isSSL);
	
	public String getFromName();
	
	public void setFromName(String fromName);
	
	

}

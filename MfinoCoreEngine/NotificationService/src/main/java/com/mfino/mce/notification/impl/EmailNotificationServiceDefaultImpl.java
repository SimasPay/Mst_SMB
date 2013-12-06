package com.mfino.mce.notification.impl;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NotificationLogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.notification.EmailNotification;
import com.mfino.mce.notification.EmailNotificationService;

public class EmailNotificationServiceDefaultImpl implements
		EmailNotificationService 
{
	private static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
	
	private String hostName;
	private String smtpPort;
	private String userName;
	private String password;
	private String fromName;
	private boolean mailServerRequireAuthentication;
	private boolean isSSL;
	
	private Log log = LogFactory.getLog(EmailNotificationServiceDefaultImpl.class);
	
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,isolation=Isolation.SERIALIZABLE) 
	public void process(Exchange exchange) throws Exception {
		
		EmailNotification emailNotification = exchange.getIn().getBody(EmailNotification.class);
		Long notificationDetailsLogID = emailNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NotificationLogDetails notificationLogDetails = notificationLogDetailsDao.getById(notificationDetailsLogID);
		try{

			SimpleEmail email = new SimpleEmail();
			email.setHostName(hostName);
			email.setSmtpPort(Integer.parseInt(smtpPort));
			if (mailServerRequireAuthentication) {
				email.setAuthenticator(new DefaultAuthenticator(userName, password));
			}
			email.setFrom(userName,fromName);
			if (isSSL) {
				email.setTLS(true);
				email.getMailSession().getProperties().put("mail.smtp.socketFactory.class", MAIL_SMTP_SOCKET_FACTORY_CLASS);
			}

			email.addTo(emailNotification.getToRecipents()[0]);
			email.setSubject(emailNotification.getSubject());
			email.setMsg(emailNotification.getContent());
			email.send();
		}
		catch(Exception e)
		{
			notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
			notificationLogDetailsDao.save(notificationLogDetails);
			log.error(e.getMessage());
			log.info("Failed to send email message with notificationLogDetailsID " + notificationDetailsLogID);
			throw new Exception(e);
		}
		notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Success);
		notificationLogDetailsDao.save(notificationLogDetails);
		log.info("Email with NotificationLogDetailsID " + notificationDetailsLogID + " was successfully sent");
	
	}

	@Override
	public String getHostName() {	
		return hostName;
	}

	@Override
	public void setHostName(String hostName) {
		this.hostName = hostName;		
	}

	@Override
	public String getSmtpPort() {
		return smtpPort;
	}

	@Override
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;		
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean getMailServerRequireAuthentication() {
		return mailServerRequireAuthentication;
	}

	@Override
	public void setMailServerRequireAuthentication(boolean mailServerRequireAuthentication) {
		this.mailServerRequireAuthentication = mailServerRequireAuthentication;
	}

	@Override
	public boolean getIsSSL() {
		return isSSL;
	}

	@Override
	public void setIsSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}

	@Override
	public String getFromName() {
		return fromName;
	}

	@Override
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

}

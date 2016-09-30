/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.service.impl;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.mfino.domain.NotificationLogDetails;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;
import com.mfino.service.MailService;
import com.mfino.service.NotificationLogDetailsService;
import com.mfino.util.ConfigurationUtil;

/**
 * 
 * @author Siddhartha Chinthapally
 */
@Service("MailServiceImpl")
public class MailServiceImpl implements MailService {

	@Autowired
	@Qualifier("NotificationLogDetailsServiceImpl")
	private NotificationLogDetailsService notificationLogDetailsService;

	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	private static Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
	private static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
	
	/**
	 * It is possible to set the sender but not provided now.
	 * 
	 */
	// FIXME: Catch this exception and throws our own exception. Need to keep
	// the
	// client code free of the implementation.
	public void sendMail(String toAddress, String toName, String subject,
			String message) throws EmailException {
		
		if (!ConfigurationUtil.isUseSmtp()) {
			sendMailWithSES(toAddress, toName, subject, message);
		} else {
			SimpleEmail email = new SimpleEmail();
			email.setHostName(ConfigurationUtil.getMailServer());
			email.setSmtpPort(ConfigurationUtil.getMailServerPort());
			if (ConfigurationUtil.getMailServerRequireAuth()) {
				email.setAuthenticator(new DefaultAuthenticator(ConfigurationUtil
						.getMailServerAuthName(), ConfigurationUtil
						.getMailServerAuthPassword()));
			}

			email.setFrom(ConfigurationUtil.getMailServerAuthName(), ConfigurationUtil
					.getMailServerFromName());
			if (ConfigurationUtil.getMailServerRequireSSL()) {
				email.setTLS(true);
				email.getMailSession().getProperties().put(
						"mail.smtp.socketFactory.class", MAIL_SMTP_SOCKET_FACTORY_CLASS);
			}

			email.addTo(toAddress, toName);
			email.setSubject(subject);
			email.setMsg(message);
			email.send();
		}
		
	}

	public void sendMailWithSES(String toAddress, String toName, String subject,
			String message) throws EmailException {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", ConfigurationUtil.getMailSesTransport());
		props.setProperty("mail.aws.user", ConfigurationUtil.getMailAwsUser());
		props.setProperty("mail.aws.password", ConfigurationUtil.getMailAwsPassword());
		Session session = Session.getInstance(props);
		
		Transport t = new AWSJavaMailTransport(session, null);
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(ConfigurationUtil.getMailAwsFrom()));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			msg.setSubject(subject);
			msg.setText(message);
			msg.saveChanges();
			
			t.connect();
			t.sendMessage(msg, null);
		} catch (Exception e) {
			throw new EmailException(e.getMessage(), e.getCause());
		} finally{
			try {
				t.close();
			} catch (MessagingException e) {
				throw new EmailException(e.getMessage(), e.getCause());
			}
		}
	}
	
	public EmailAttachment createEmailAttachment(String filePath,
			String description, String name) {
		EmailAttachment attachment = new EmailAttachment();
		attachment.setPath(filePath);
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		attachment.setDescription(description);
		attachment.setName(name);
		return attachment;
	}

	public void sendMailwithAttachment(String toAddress, String toName,
			String subject, String message, String filePath)
			throws EmailException {

		// Create the attachment
		EmailAttachment attachment = new EmailAttachment();
		attachment.setPath(filePath);
		attachment.setDisposition(EmailAttachment.ATTACHMENT);

		MultiPartEmail email = new MultiPartEmail();
		email.setHostName(ConfigurationUtil.getMailServer());
		email.setSmtpPort(ConfigurationUtil.getMailServerPort());
		if (ConfigurationUtil.getMailServerRequireAuth()) {
			email.setAuthenticator(new DefaultAuthenticator(ConfigurationUtil
					.getMailServerAuthName(), ConfigurationUtil
					.getMailServerAuthPassword()));
		}

		email.setFrom(ConfigurationUtil.getMailServerAuthName(),
				ConfigurationUtil.getMailServerFromName());
		if (ConfigurationUtil.getMailServerRequireSSL()) {
			email.setTLS(true);
			email.getMailSession()
					.getProperties()
					.put("mail.smtp.socketFactory.class",
							MAIL_SMTP_SOCKET_FACTORY_CLASS);
		}

		email.attach(attachment);
		email.addTo(toAddress, toName);
		email.setSubject(subject);
		if (StringUtils.isNotBlank(message)) {
			email.setMsg(message);
		}
		email.send();
	}

	private void sendMail(String toAddress, String toName, String subject,
			String message, List<File> attachments) {
		log.info("Sending Reports Mail to " + toAddress);
		try {
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(ConfigurationUtil.getMailServer());
			email.setSmtpPort(ConfigurationUtil.getMailServerPort());
			if (ConfigurationUtil.getMailServerRequireAuth()) {
				email.setAuthenticator(new DefaultAuthenticator(
						ConfigurationUtil.getMailServerAuthName(),
						ConfigurationUtil.getMailServerAuthPassword()));
			}
			email.setFrom(ConfigurationUtil.getMailServerAuthName(),
					ConfigurationUtil.getMailServerFromName());
			if (ConfigurationUtil.getMailServerRequireSSL()) {
				email.setTLS(true);
				email.getMailSession()
						.getProperties()
						.put("mail.smtp.socketFactory.class",
								MAIL_SMTP_SOCKET_FACTORY_CLASS);
			}
			email.addTo(toAddress, toName);
			if (StringUtils.isNotBlank(subject)) {
				email.setSubject(subject);
			}
			if (StringUtils.isNotBlank(message)) {
				email.setMsg(message);
			}
			Iterator<File> it = attachments.iterator();
			while (it.hasNext()) {
				File attachmentFile = it.next();
				if (attachmentFile.exists()) {
					log.info("Attaching file " + attachmentFile.getName());
					EmailAttachment attachment = new EmailAttachment();
					attachment.setPath(attachmentFile.getAbsolutePath());
					attachment.setDisposition(EmailAttachment.ATTACHMENT);
					attachment.setDescription(attachmentFile.getName());
					attachment.setName(attachmentFile.getName());
					email.attach(attachment);
				}
			}
			email.send();
			log.info("Successfully Sent Mail to " + toAddress);
		} catch (Exception e) {
			log.error("Error while sending mail to " + toAddress, e);
		}
	}

	public void asyncSendMail(final String toAddress, final String toName,
			final String subject, final String message,
			final List<File> attachments) {
		threadPool.execute(new Runnable() {

			@Override
			public void run() {
				sendMail(toAddress, toName, subject, message, attachments);
			}
		});
	}

	public void sendMail(String toAddress, String toName, String subject,
			String message, File attachmentFile) throws EmailException {
		MultiPartEmail email = new MultiPartEmail();
		email.setHostName(ConfigurationUtil.getMailServer());
		email.setSmtpPort(ConfigurationUtil.getMailServerPort());
		if (ConfigurationUtil.getMailServerRequireAuth()) {
			email.setAuthenticator(new DefaultAuthenticator(ConfigurationUtil
					.getMailServerAuthName(), ConfigurationUtil
					.getMailServerAuthPassword()));
		}

		email.setFrom(ConfigurationUtil.getMailServerAuthName(),
				ConfigurationUtil.getMailServerFromName());
		if (ConfigurationUtil.getMailServerRequireSSL()) {
			email.setTLS(true);
			email.getMailSession()
					.getProperties()
					.put("mail.smtp.socketFactory.class",
							MAIL_SMTP_SOCKET_FACTORY_CLASS);
		}

		email.addTo(toAddress, toName);
		email.setSubject(subject);
		email.setMsg(message);

		if (attachmentFile.exists()) {
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(attachmentFile.getAbsolutePath());
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription(attachmentFile.getName());
			attachment.setName(attachmentFile.getName());

			email.attach(attachment);
		}

		email.send();
	}

	public CFIXMsg sendMailMultiX(String toAddress, String toName,
			String subject, String message) {

		// FixMessageSerializer fms = new
		// FixMessageSerializer(ConfigurationUtil.getBackendURL());
		//
		// CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
		// CmFinoFIX.CMSubscriberNotification subNotification = new
		// CmFinoFIX.CMSubscriberNotification();
		//
		// subNotification.setSenderEmail(ConfigurationUtil.getMailServerAuthName());
		// subNotification.setReceiverEmail(toAddress);
		// subNotification.setText(message);
		// subNotification.setEmailSubject(subject);
		// subNotification.setSourceApplication(CmFinoFIX.SourceApplication_Web);
		// subNotification.setMethod(CmFinoFIX.NotificationMethod_Email);
		// subNotification.setCode(0);
		// subNotification.setServletPath(CmFinoFIX.ServletPath_WebAppFEForSubscribers);
		//
		// return fms.send(subNotification);

		CMErrorNotification error = new CMErrorNotification();
		error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		try {
			sendMail(toAddress, toName, subject, message);
		} catch (Exception err) {
			error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			String msg = "Failed to send mail ";
			error.setErrorDescription(msg);
			log.error(msg, err);
		}
		return error;
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public void asyncSendEmail(final String toAddress, final String toName,
			final String subject, final String message) {
		asyncSendEmail(toAddress, toName, subject, message, null);

	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public void asyncSendEmailWithAttachment(final String toAddress,
			final String toName, final String subject, final String message,
			final String filePath) {
		asyncSendEmailwithAttachment(toAddress, toName, subject, message,
				filePath, null);

	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public void asyncSendEmailwithAttachment(final String toAddress,
			final String toName, final String subject, final String message,
			final String filePath, final Long notificationLogDetailsID) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				log.info("sending email to " + toAddress);
				NotificationLogDetails notificationLogDetails = null;
				if (notificationLogDetailsID != null) {
					notificationLogDetails = notificationLogDetailsService
							.getNotificationLogDetailsById(notificationLogDetailsID);
				}
				try {
					sendMailwithAttachment(toAddress, toName, subject, message,
							filePath);
					if (notificationLogDetails != null) {
						notificationLogDetails
								.setStatus(CmFinoFIX.SendNotificationStatus_Success);
						notificationLogDetailsService
								.saveNotificationLogDetails(notificationLogDetails);
					}
				} catch (EmailException err) {
					log.error("failed to send email to " + toAddress, err);
					if (notificationLogDetails != null) {
						notificationLogDetails
								.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
						notificationLogDetailsService
								.saveNotificationLogDetails(notificationLogDetails);
					}
				}
			}
		});

	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public void asyncSendEmail(final String toAddress, final String toName,
			final String subject, final String message,
			final Long notificationLogDetailsID) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				log.info("sending email to " + toAddress);
				NotificationLogDetails notificationLogDetails = null;
				if (notificationLogDetailsID != null) {
					notificationLogDetails = notificationLogDetailsService
							.getNotificationLogDetailsById(notificationLogDetailsID);
				}
				try {
					sendMail(toAddress, toName, subject, message);
					if (notificationLogDetails != null) {
						notificationLogDetails
								.setStatus(CmFinoFIX.SendNotificationStatus_Success);
						notificationLogDetailsService
								.saveNotificationLogDetails(notificationLogDetails);
					}
				} catch (EmailException err) {
					log.error("failed to send email to " + toAddress, err);
					if (notificationLogDetails != null) {
						notificationLogDetails
								.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
						notificationLogDetailsService
								.saveNotificationLogDetails(notificationLogDetails);
					}
				}
			}
		});

	}

	public boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (Exception ex) {
			result = false;
			log.error("Invalid email address: ", ex);
		}
		return result;
	}

	public void generateEmailVerificationMail(Subscriber subscriber,
			String email) {
		String mailBody = ConfigurationUtil.getEmailVerificationMessage();
		mailBody = mailBody.replace("$(AppURL)", ConfigurationUtil.getAppURL());
		mailBody = mailBody.replace("$(subscriberID)", subscriber.getId()
				.toString());
		mailBody = mailBody.replace("$(email)", email);
		asyncSendEmail(email,
				subscriber.getFirstname() + " " + subscriber.getLastname(),
				ConfigurationUtil.getEmailVerificationSubject(), mailBody);
	}
}

package com.dimo.fuse.reports.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simpleemail.AWSJavaMailTransport;

/**
 * 
 * @author Amar
 *
 */
public class MailUtil {

  private static Logger log = LoggerFactory.getLogger("MailUtil");	
  private static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
  private String mailServer = "smtp.gmail.com";
  private String mailServerPort = "465";
  private String mailServerAuthName = "mfinoemailtest@gmail.com";
  private String mailServerAuthPassword = "User1234";
  private String mailServerFromName = "dev.dimo.com";
  private String mailServerRequireAuth = "true";
  private String mailServerRequireSSL = "true";
  private String useSmtp = "true";
  private String sesTransportProtocol = "aws";
  private String sesUser = "AKIAIZW252ECC7BIJ5QQ";
  private String sesPassword = "p9L+u1zfKmTaYvR7JgOJLHCdfH/IbBhVgPmR0zAC";
  private String sesFromMail = "noreply@dimo.co.id";
  
  public MailUtil(){
	  loadProperties();
  }
   
  private void loadProperties() {
	  log.info("loading mail properties started");
	  try {
		  mailServer = ReportSchedulerProperties.getProperty("mfino.mail.server");
		  mailServerPort = ReportSchedulerProperties.getProperty("mfino.mail.server.port");
		  mailServerAuthName = ReportSchedulerProperties.getProperty("mfino.mail.server.auth_name");
		  mailServerAuthPassword = ReportSchedulerProperties.getProperty("mfino.mail.server.auth_password");
		  mailServerFromName = ReportSchedulerProperties.getProperty("mfino.mail.server.from_name");
		  mailServerRequireAuth = ReportSchedulerProperties.getProperty("mfino.mail.server.require_auth");
		  mailServerRequireSSL = ReportSchedulerProperties.getProperty("mfino.mail.server.require_ssl");
		  
		  useSmtp = ReportSchedulerProperties.getProperty("mfino.mail.use.smtp");
		  sesTransportProtocol = ReportSchedulerProperties.getProperty("mfino.mail.transport.protocol");
		  sesUser = ReportSchedulerProperties.getProperty("mfino.mail.aws.user");
		  sesPassword = ReportSchedulerProperties.getProperty("mfino.mail.aws.password");
		  sesFromMail = ReportSchedulerProperties.getProperty("mfino.mail.aws.from");
	  } catch (Exception e) {
		  log.error("Error while loading Email Proeprties",e);
	  }
	  log.info("loading mail properties finished");
  }

 
  public  void sendMail(String toAddress, String toName, String subject, String message){
	  	log.info("sendMail function started");
	  	try{
	  		if(!StringUtils.equals("true", useSmtp)){
	  			sendMailWithSES(toAddress, toName, subject, message, null);
	  		}
			SimpleEmail email = new SimpleEmail();
			email.setHostName(mailServer);
			email.setSmtpPort(Integer.parseInt(mailServerPort));
		    if (mailServerRequireAuth.equalsIgnoreCase("true")) {
		      email.setAuthenticator(new DefaultAuthenticator(mailServerAuthName, mailServerAuthPassword));
		    }
		    email.setFrom(mailServerAuthName, mailServerFromName);
		    if (mailServerRequireSSL.equalsIgnoreCase("true")) {
		      email.setTLS(true);
		      email.getMailSession().getProperties().put("mail.smtp.socketFactory.class", MAIL_SMTP_SOCKET_FACTORY_CLASS);
		    }
		    email.addTo(toAddress, toName);
		    email.setSubject(subject);
		    email.setMsg(message);
		    log.info("Sending Reports Mail to "+toAddress);
		    email.send();
		    log.info("Successfully Sent Reports Mail to "+toAddress);
	  	}catch(Exception e){
	  		log.error("Error while sending mail to "+toAddress ,e);
	  	}
	    log.info("sendMail function finished");
  }

  public  void sendMail(String toAddress, String toName, String subject, String message, File attachmentFile) {
	  log.info("sendMail function started"); 
	  try{
		  	if(!StringUtils.equals("true", useSmtp)){
		  		List<File> attchments = new ArrayList<File>();
		  		attchments.add(attachmentFile);
	  			sendMailWithSES(toAddress, toName, subject, message, attchments);
	  		}
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(mailServer);
			email.setSmtpPort(Integer.parseInt(mailServerPort));
		    if (mailServerRequireAuth.equalsIgnoreCase("true")) {
		      email.setAuthenticator(new DefaultAuthenticator(mailServerAuthName, mailServerAuthPassword));
		    }
		    email.setFrom(mailServerAuthName, mailServerFromName);
		    if (mailServerRequireSSL.equalsIgnoreCase("true")) {
		      email.setTLS(true);
		      email.getMailSession().getProperties().put("mail.smtp.socketFactory.class", MAIL_SMTP_SOCKET_FACTORY_CLASS);
		    }
		    email.addTo(toAddress, toName);
		    if(StringUtils.isNotBlank(subject)){
		    	email.setSubject(subject);
		    }
		    if(StringUtils.isNotBlank(message)){
		    	email.setMsg(message);
		    }
		    if (attachmentFile.exists()) {
		      EmailAttachment attachment = new EmailAttachment();
		      attachment.setPath(attachmentFile.getAbsolutePath());
		      attachment.setDisposition(EmailAttachment.ATTACHMENT);
		      attachment.setDescription(attachmentFile.getName());
		      attachment.setName(attachmentFile.getName());
		      email.attach(attachment);
		    }
		    log.info("Sending Reports Mail to "+toAddress+" and the attachment file name is "+attachmentFile.getName());
		    email.send();
		    log.info("Successfully Sent Reports Mail to "+toAddress+" and the attachment file name is "+attachmentFile.getName());
	  }catch(Exception e){
		  log.error("Error while sending mail to "+toAddress+"and the attachment file name is "+attachmentFile.getName() ,e);
	  }
	  log.info("sendMail function finished");
  }
  
  public  void sendMail(String toAddress, String toName, String subject, String message, List<File> attachments) {
	  log.info("Sending Reports Mail to " + toAddress);
	  try{
		  	if(!StringUtils.equals("true", useSmtp)){
	  			sendMailWithSES(toAddress, toName, subject, message, attachments);
	  		}
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(mailServer);
			email.setSmtpPort(Integer.parseInt(mailServerPort));
		    if (mailServerRequireAuth.equalsIgnoreCase("true")) {
		      email.setAuthenticator(new DefaultAuthenticator(mailServerAuthName, mailServerAuthPassword));
		    }
		    email.setFrom(mailServerAuthName, mailServerFromName);
		    if (mailServerRequireSSL.equalsIgnoreCase("true")) {
		      email.setTLS(true);
		      email.getMailSession().getProperties().put("mail.smtp.socketFactory.class", MAIL_SMTP_SOCKET_FACTORY_CLASS);
		    }
		    email.addTo(toAddress, toName);
		    if(StringUtils.isNotBlank(subject)){
		    	email.setSubject(subject);
		    }
		    if(StringUtils.isNotBlank(message)){
		    	email.setMsg(message);
		    }
		    Iterator<File> it = attachments.iterator();
		    while(it.hasNext()){
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
		    log.info("Successfully Sent Reports Mail to " + toAddress );
	  }catch(Exception e){
		  log.error("Error while sending mail to " + toAddress ,e);
	  }
	  log.info("sendMail function finished");
  }
  
  public void sendMailWithSES(String toAddress, String toName, String subject,
			String message, List<File> attachments) throws EmailException {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", sesTransportProtocol);
		props.setProperty("mail.aws.user", sesUser);
		props.setProperty("mail.aws.password", sesPassword);
		Session session = Session.getInstance(props);
		
		Transport t = new AWSJavaMailTransport(session, null);
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(sesFromMail));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			msg.setSubject(subject);
			
		    if (attachments != null && attachments.size() > 0){
				BodyPart part = new MimeBodyPart();
			    part.setContent(message, "text/plain");
			    
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(part);
				
				for (File file : attachments) {
					if(file.exists()){
						BodyPart messageBodyPart = new MimeBodyPart();
						DataSource source = new FileDataSource(file.getAbsolutePath());
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(source.getName());
					    multipart.addBodyPart(messageBodyPart);
					}
				}
				msg.setContent(multipart);
				
		    } else{
		    	msg.setText(message);
		    }
		    
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
  
  public  boolean isValidEmailAddress(String email) {
	   boolean result = true;
	   try {
	      InternetAddress emailAddr = new InternetAddress(email);
	      emailAddr.validate();
	   } catch (Exception ex) {
	      result = false;
	   }
	   return result;
	}

}

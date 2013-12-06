/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.rtscheduler.schedule;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailUtil {

  private static Logger log = LoggerFactory.getLogger("MailUtil");	
  private static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
  private Properties prop = new Properties();
  private String mailServer = "smtp.gmail.com";
  private String mailServerPort = "465";
  private String mailServerAuthName = "mfinoemailtest@gmail.com";
  private String mailServerAuthPassword = "User1234";
  private String mailServerFromName = "dev.mfino.com";
  private String mailServerRequireAuth = "true";
  private String mailServerRequireSSL = "true";
  
  public MailUtil(){
	  loadProperties();
  }
  

  
  private void loadProperties() {
	  log.info("loading mail properties started");
		try {
			  InputStream ins = this.getClass().getResourceAsStream("/rtscheduler.properties");
			  prop.load(ins);
			  ins.close();
			  mailServer = prop.getProperty("mfino.mail.server");
			  mailServerPort = prop.getProperty("mfino.mail.server.port");
			  mailServerAuthName = prop.getProperty("mfino.mail.server.auth_name");
			  mailServerAuthPassword = prop.getProperty("mfino.mail.server.auth_password");
			  mailServerFromName = prop.getProperty("mfino.mail.server.from_name");
			  mailServerRequireAuth = prop.getProperty("mfino.mail.server.require_auth");
			  mailServerRequireSSL = prop.getProperty("mfino.mail.server.require_ssl");
		} catch (Exception e) {
			log.error("Error while loading Email Proeprties",e);
		}
		log.info("loading mail properties finished");
	}

 
  public  void sendMail(String toAddress, String toName, String subject, String message){
	  	log.info("sendMail function started");
	  	try{
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
		    log.info("Sending Reports Mail to "+toAddress+" and the attachment file name is "+attachmentFile.getName());
		    email.send();
		    log.info("Successfully Sent Reports Mail to "+toAddress+" and the attachment file name is "+attachmentFile.getName());
	  }catch(Exception e){
		  log.error("Error while sending mail to "+toAddress+"and the attachment file name is "+attachmentFile.getName() ,e);
	  }
	  log.info("sendMail function finished");
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

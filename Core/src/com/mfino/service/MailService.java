/**
 * 
 */
package com.mfino.service;

import java.io.File;
import java.util.List;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;

import com.mfino.domain.Subscriber;
import com.mfino.fix.CFIXMsg;

/**
 * @author Sreenath
 *
 */
public interface MailService {
	/**
	 * 	
	 * @param toAddress
	 * @param toName
	 * @param subject
	 * @param message
	 * @throws EmailException
	 */
	public void sendMail(String toAddress, String toName, String subject,
		      String message) throws EmailException;
	/**
	 * 	  
	 * @param toAddress
	 * @param toName
	 * @param subject
	 * @param message
	 * @param attachmentFile
	 * @throws EmailException
	 */
	public void sendMail(String toAddress, String toName, String subject,
	      String message, File attachmentFile) throws EmailException;
	
	/**
	 * 
	 * @param toAddress
	 * @param toName
	 * @param subject
	 * @param message
	 * @return
	 */
	public CFIXMsg sendMailMultiX(String toAddress, String toName,
	      String subject, String message);
	/**
	 *   
	 * @param toAddress
	 * @param toName
	 * @param subject
	 * @param message
	 */
	public void asyncSendEmail(final String toAddress, final String toName, final String subject,
		      final String message );
	/**
	 *   
	 * @param toAddress
	 * @param toName
	 * @param subject
	 * @param message
	 * @param notificationLogDetailsID
	 */
	public void asyncSendEmail(final String toAddress, final String toName, final String subject,
		      final String message, final Long notificationLogDetailsID);
	
	
	/**
	 * 
	 * @param filePath
	 * @param description
	 * @param name
	 * @return
	 */
	 public EmailAttachment createEmailAttachment(String filePath, String description, String name);
	/**
	 * 
	 * @param toAddress
	 * @param toName
	 * @param subject
	 * @param message
	 * @param filePath
	 */
	public void asyncSendEmailWithAttachment(final String toAddress, final String toName, final String subject,
		      final String message, final String filePath );
	
	/**
	 * 
	 * @param toAddress
	 * @param toName
	 * @param subject
	 * @param message
	 * @param filePath
	 * @param notificationLogDetailsID
	 */
	public void asyncSendEmailwithAttachment(final String toAddress, final String toName, final String subject,
		      final String message, final String filePath, final Long notificationLogDetailsID);
		
	/**
	 * 
	 * @param email
	 * @return
	 */
	public boolean isValidEmailAddress(String email);

	public void generateEmailVerificationMail(Subscriber subscriber,
			String email);
	/**
	 * Send mail with multiple attachments
	 */
	public void asyncSendMail(final String toAddress, final String toName, final String subject, final String message, final List<File> attachments);
}

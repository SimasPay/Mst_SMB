package com.mfino.mce.notification;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author sasidhar
 * Bean holds necessary information for sending Email Notification.
 */
public class EmailNotification extends Notification {
	
	private String fromId;
	private String fromName;
	private String replyId;
	private String replyName;
	
	private String[] toRecipents = {};
	private String[] ccRecipents = {};
	private String[] bccRecipents = {};
	
	private String subject;
	private String content;
	private String type;
	
	private Map<Integer,String> parameters = new TreeMap<Integer, String>();
	
	
	public String getFromId() {
		return fromId;
	}
	
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	
	public String getFromName() {
		return fromName;
	}
	
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	
	public String getReplyId() {
		return replyId;
	}
	
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}
	
	public String getReplyName() {
		return replyName;
	}
	
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}
	
	public String[] getToRecipents() {
		return toRecipents;
	}
	
	public void setToRecipents(String[] toRecipents) {
		this.toRecipents = toRecipents;
	}
	
	public String[] getCcRecipents() {
		return ccRecipents;
	}
	
	public void setCcRecipents(String[] ccRecipents) {
		this.ccRecipents = ccRecipents;
	}
	
	public String[] getBccRecipents() {
		return bccRecipents;
	}
	
	public void setBccRecipents(String[] bccRecipents) {
		this.bccRecipents = bccRecipents;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void addParameter(Integer index, String value){
		parameters.put(index, value);
	}

	public Object[] getParameters() {
		return parameters.values().toArray();
	}
}

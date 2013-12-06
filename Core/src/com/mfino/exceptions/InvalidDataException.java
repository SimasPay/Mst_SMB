package com.mfino.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bala Sunku
 */
public class InvalidDataException extends Exception {
	
	private Integer notificationCode;
	private String parameterName;
	private Map<Integer, Object> keyValueMap = new HashMap<Integer, Object>();

	public Integer getNotificationCode() {
		return notificationCode;
	}

	public void setNotificationCode(Integer notificationCode) {
		this.notificationCode = notificationCode;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public InvalidDataException(String message){
		super(message);
	}
	
	public InvalidDataException(Throwable throwable){
		super(throwable);
	}
	
	public InvalidDataException(String message, Integer notificationCode, String parameterName) {
		super(message);
		setNotificationCode(notificationCode);
		setParameterName(parameterName);
	}

	public InvalidDataException(String message, Integer notificationCode, Map<Integer, Object> keyParameterMap) {
		super(message);
		setNotificationCode(notificationCode);
		setKeyValueMap(keyParameterMap);
	}

	
	public Map<Integer, Object> getKeyValueMap() {
		return keyValueMap;
	}

	public void setKeyValueMap(Map<Integer, Object> keyValueMap) {
		this.keyValueMap = keyValueMap;
	}

	public String getLogMessage(){
		StringBuilder builder = new StringBuilder();
		builder.append(getMessage());
		builder.append(" ");
		builder.append(getParameterName());
		builder.append(" Notification Code:");
		builder.append(getNotificationCode());
		return builder.toString();
	}
}

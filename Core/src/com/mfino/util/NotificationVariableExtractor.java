package com.mfino.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.service.NotificationService;
import com.mfino.util.StringWordDiffUtil.DiffPair;

public class NotificationVariableExtractor{
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private static NotificationService notificationService;
	
	private static StringWordDiffUtil diffUtil = new StringWordDiffUtil();
	static {		
		// configure all the variables which can have multiple words as values
		diffUtil.setVariableLength("$(TransactionDateTime)", 2);
	}
		
	public static List<DiffPair> getVariableValues(String notificationMessage, int language) {		
		String[] tokens = notificationMessage.split(" ");
		String notifToken = tokens[0].substring(2, tokens[0].length() - 1);
		int notificationCode = Integer.parseInt(notifToken);
		return getVariableValues(notificationCode, notificationMessage.substring(tokens[0].length()), language);		
	}

	public static List<DiffPair> getVariableValues(int notificationCode,
			String notificationMessage, int language) {		
		String rawNotifText = notificationService.getNotificationText(notificationCode, language);
		return diffUtil.diff(rawNotifText, notificationMessage);		
	}


}

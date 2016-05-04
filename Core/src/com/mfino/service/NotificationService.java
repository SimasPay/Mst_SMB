package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Notification;

public interface NotificationService {
	
    public String getNotificationText(Integer notificationCode, Integer lang);
    
    public String getNotificationText(Integer notificationCode, Integer lang, Integer notificationMethod);
    
    public String getNotificationText(Integer notificationCode, Integer lang, Integer notificationMethod, Company company);
    
    public String checkForReplacementValues(String str, String cusomerServiceShortCode);
    
    public  Notification getByNoticationCode(Integer code);
    
    public  List<Notification> getByQuery(NotificationQuery notificationQuery);
    
	public List<Notification> getLanguageBasedNotificationsByQuery(NotificationQuery notificationQuery);
	
	public Notification getByNotificationCodeAndLang(Integer code, Integer lang);

}



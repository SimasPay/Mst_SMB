package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationDAO;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Notification;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.NotificationService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;

@Service("NotificationServiceImpl")
public class NotificationServiceImpl implements NotificationService{
	private static Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String getNotificationText(Integer notificationCode, Integer lang) 
    {
        return getNotificationText(notificationCode, lang, CmFinoFIX.NotificationMethod_Web);
    }
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String getNotificationText(Integer notificationCode, Integer lang, Integer notificationMethod){
        return  getNotificationText(notificationCode, lang, notificationMethod, null);
    }
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String getNotificationText(Integer notificationCode, Integer lang, Integer notificationMethod, Company company) {
        String notificationTxt="";
        String cusomerServiceShortCode= ConfigurationUtil.getCustomerServiceShortCode();
        NotificationDAO dao = DAOFactory.getInstance().getNotificationDAO();
        NotificationQuery query = new NotificationQuery();
        query.setNotificationCode(notificationCode);
        if(notificationMethod== null){
            query.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
        }else{
            query.setNotificationMethod(notificationMethod);
        }
        if(lang == null){
			lang = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
            query.setLanguage(lang);
        }else{
            query.setLanguage(lang);
        }
        if(null != company){
            query.setCompany(company);
            cusomerServiceShortCode = company.getCustomerServiceNumber();
        }
        List<Notification> results = dao.getLanguageBasedNotifications(query);

        if (results.size() <= 0) {
            log.error(String.format("No text for tag %1$s", query.getNotificationCode()));
        }
        if(results.size() > 0)
        {
            notificationTxt=checkForReplacementValues(results.get(0).getText(), cusomerServiceShortCode);
        }

        return notificationTxt;
    }
    
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String checkForReplacementValues(String str, String cusomerServiceShortCode){
        CharSequence searchString="$(CustomerServiceShortCode)";
        String modifiedStr=str;
        if (str.contains(searchString)) {
            modifiedStr = str.replace(searchString, cusomerServiceShortCode);
        }
        
        return modifiedStr;
    }
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public  Notification getByNoticationCode(Integer code){
		Notification notification = DAOFactory.getInstance().getNotificationDAO().getByNotificationCode(code);

		return notification;
    	
    }
    
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public  List<Notification> getByQuery(NotificationQuery notificationQuery){
		List<Notification> notification = DAOFactory.getInstance().getNotificationDAO().get(notificationQuery);
		return notification;
    }
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Notification> getLanguageBasedNotificationsByQuery(NotificationQuery notificationQuery){
		Integer language = notificationQuery.getLanguage();
		if(language == null) {
			language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
			notificationQuery.setLanguage(language);
		}
		List<Notification> notification = DAOFactory.getInstance().getNotificationDAO().getLanguageBasedNotifications(notificationQuery);
		return notification;
	}
	
	public Notification getByNotificationCodeAndLang(Integer code, Integer lang) {
		return DAOFactory.getInstance().getNotificationDAO().getByNotificationCodeAndLang(code, lang);
	}	
}



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Notification;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author sunil
 */
public class NotificationDAO extends BaseDAO<Notification> {

	/*
	 * If language is not mentioned, notifications for default language are fetched.
	 * Else get the notifications for the given language. 
	 * If the notification for given language doesn't exist, then fetches the notifications for default language.
	 * Note: This function can't be used for queries from UI as the result should match the exact criteria 
	 */
    @SuppressWarnings("unchecked")
	public List<Notification> getLanguageBasedNotifications(NotificationQuery query) {

        Criteria criteria = createCriteria();
        Disjunction dis = Restrictions.disjunction();
        
        if (query.getLanguage() != null) {
            dis.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_Language, query.getLanguage()));
        }
        if (query.getNotificationCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationCode, query.getNotificationCode()));
        }
        if (query.getNotificationMethod() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationMethod, query.getNotificationMethod()));
        }
        if (query.getNotificationText() != null && !(query.getNotificationText().equals(""))) {
            addLikeAnywhereRestriction(criteria, CmFinoFIX.CRNotification.FieldName_NotificationText, query.getNotificationText());
        }

        if (query.getNotificationID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_RecordID, query.getNotificationID()));
        }
        if (query.getNotificationCodeName() != null && !(query.getNotificationCodeName().equals(""))) {
            addLikeStartRestriction(criteria, CmFinoFIX.CRNotification.FieldName_NotificationCodeName, query.getNotificationCodeName());
        }
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_Company, query.getCompany()));
        }
        criteria.add(dis);
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);

        //applying Order
        applyOrder(query, criteria);
        List<Notification> results = criteria.list();
                
        if(results == null || results.size() == 0)
        {
        	dis.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_Language, CmFinoFIX.Language_English));
        	processBaseQuery(query, criteria);
            // Paging
            processPaging(query, criteria);

            //applying Order
            applyOrder(query, criteria);
            results = criteria.list();           
        }

        return results;
    }
    
    public List<Notification> get(NotificationQuery query) {

        Criteria criteria = createCriteria();

        if (query.getLanguage() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_Language, query.getLanguage()));
        }
        if (query.getNotificationCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationCode, query.getNotificationCode()));
        }
        if (query.getNotificationMethod() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationMethod, query.getNotificationMethod()));
        }
        if (query.getNotificationText() != null && !(query.getNotificationText().equals(""))) {
            addLikeAnywhereRestriction(criteria, CmFinoFIX.CRNotification.FieldName_NotificationText, query.getNotificationText());
        }

        if (query.getNotificationID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_RecordID, query.getNotificationID()));
        }
        if (query.getNotificationCodeName() != null && !(query.getNotificationCodeName().equals(""))) {
            addLikeStartRestriction(criteria, CmFinoFIX.CRNotification.FieldName_NotificationCodeName, query.getNotificationCodeName());
        }
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_Company, query.getCompany()));
        }
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);

        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<Notification> results = criteria.list();

        return results;
    }
    
   /**
    * 
    * @param code
    * @return Notification based on notification code for English language and web notification method.
    */
    public Notification getByNotificationCode(Integer code) 
    {
    	if(code == null)
    	{
    		return null;
    	}
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationCode, code));
        criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationMethod, CmFinoFIX.NotificationMethod_Web));
        criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_Language, CmFinoFIX.Language_English));
        return (Notification) criteria.uniqueResult();
    }
    
    /**
     * 
     * @param code
     * @return Notification based on notification code and language for web notification method.
     */
    public Notification getByNotificationCodeAndLang(Integer code, Integer lang) 
    {
    	if(code == null)
    	{
    		return null;
    	}
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationCode, code));
        criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_NotificationMethod, CmFinoFIX.NotificationMethod_Web));
        criteria.add(Restrictions.eq(CmFinoFIX.CRNotification.FieldName_Language, lang));
        return (Notification) criteria.uniqueResult();
    }


    @Override
    public void save(Notification s) {
        if (s.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1L);
            s.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(s);
    }
}


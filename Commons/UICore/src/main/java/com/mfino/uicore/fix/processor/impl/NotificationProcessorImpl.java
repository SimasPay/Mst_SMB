/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.NotificationDAO;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Notification;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSNotification;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.NotificationProcessor;

/**
 *
 * @author sunil
 */
@Service("NotificationProcessorImpl")
public class NotificationProcessorImpl extends BaseFixProcessor implements NotificationProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    private void updateEntity(Notification s, CMJSNotification.CGEntries e) {

        if (e.getID() != null) {
            s.setID(e.getID());
        }
        if (e.getLanguage() != null) {
            s.setLanguage(e.getLanguage());
        }
        if (e.getMSPID() != null) {
            MfinoServiceProviderDAO mspdao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            s.setmFinoServiceProviderByMSPID(mspdao.getById(e.getMSPID()));
        }
        if (e.getNotificationCode() != null) {
            s.setCode(e.getNotificationCode());
        }
        if (e.getNotificationCodeName() != null) {
            s.setCodeName(e.getNotificationCodeName());
        }
        if (e.getNotificationMethod() != null) {
            s.setNotificationMethod(e.getNotificationMethod());
        }
        if (e.getNotificationStatus() != null) {
            s.setStatus(e.getNotificationStatus());
        }
        if (e.getNotificationText() != null) {
            s.setText(e.getNotificationText());
        }
        if (e.getSTKMLText() != null) {
            s.setSTKML(e.getSTKMLText());
        }
        if (e.getStatusTime()!= null) {
            s.setStatusTime(e.getStatusTime());
        }
        if (e.getUpdatedBy()!= null) {
            s.setUpdatedBy(e.getUpdatedBy());
        }
         if (e.getLastUpdateTime()!= null) {
            s.setLastUpdateTime(e.getLastUpdateTime());
        }

        if (e.getCreateTime() != null) {
            s.setCreateTime(e.getCreateTime());
        }
        if (e.getCreatedBy() != null) {
            s.setCreatedBy(e.getCreatedBy());
        }
        if (e.getAccessCode() != null) {
            s.setAccessCode(e.getAccessCode());
        }
        if (e.getSMSNotificationCode() != null) {
            s.setSMSNotificationCode(e.getSMSNotificationCode());
        }
        if (e.getCompanyID() != null) {
            CompanyDAO dao = DAOFactory.getInstance().getCompanyDAO();
            Company company= dao.getById(e.getCompanyID());
            s.setCompany(company);
        }
        if (e.getIsActive() != null) {
            s.setIsActive(e.getIsActive());
        }
    }

    private void updateMessage(Notification e, CMJSNotification.CGEntries s) {
        if (e.getID() != null) {
            s.setID(e.getID());
        }
        if (e.getLanguage() != null) {
            s.setLanguage(e.getLanguage());
        }
        s.setMSPID(e.getmFinoServiceProviderByMSPID().getID());
        if (e.getCode() != null) {
            s.setNotificationCode(e.getCode());
        }
        if (e.getCodeName() != null) {
            s.setNotificationCodeName(e.getCodeName());
        }
        if (e.getNotificationMethod() != null) {
            s.setNotificationMethod(e.getNotificationMethod());
        }
        if (e.getStatus() != null) {
            s.setNotificationStatus(e.getStatus());
        }
        if (e.getText() != null) {
            s.setNotificationText(e.getText());
        }
        if (e.getSTKML() != null) {
            s.setSTKMLText(e.getSTKML());
        }
        if (e.getStatusTime()!= null) {
            s.setStatusTime(e.getStatusTime());
        }
        if (e.getUpdatedBy()!= null) {
            s.setUpdatedBy(e.getUpdatedBy());
        }
         if (e.getLastUpdateTime()!= null) {
            s.setLastUpdateTime(e.getLastUpdateTime());
        }
        if (e.getCreateTime() != null) {
            s.setCreateTime(e.getCreateTime());
        }
        if (e.getCreatedBy() != null) {
            s.setCreatedBy(e.getCreatedBy());
        }
        if (e.getAccessCode() != null) {
            s.setAccessCode(e.getAccessCode());
        }
        if (e.getSMSNotificationCode() != null) {
            s.setSMSNotificationCode(e.getSMSNotificationCode());
        }
        if(e.getCompany() != null) {
            Company company = e.getCompany();
            s.setCompanyID(company.getID());
        }
        if (e.getIsActive() != null) {
            s.setIsActive(e.getIsActive());
        }
        s.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, e.getLanguage(), e.getLanguage()));
        s.setNotificationMethodText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_NotificationMethod, e.getNotificationMethod(), e.getNotificationMethod()));
    }
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSNotification realMsg = (CMJSNotification) msg;

        NotificationDAO dao = DAOFactory.getInstance().getNotificationDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSNotification.CGEntries[] entries = realMsg.getEntries();

            for (CMJSNotification.CGEntries e : entries) {
                Notification s = dao.getById(e.getID());

                updateEntity(s, e);
                dao.save(s);
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            NotificationQuery query = new NotificationQuery();
            query.setNotificationCode(realMsg.getNotificationCode());
            query.setNotificationCodeName(realMsg.getNotificationCodeName());
            query.setNotificationID(realMsg.getNotificationID());
            query.setNotificationMethod(realMsg.getNotificationMethod());
            query.setNotificationText(realMsg.getNotificationText());
            query.setLanguage(realMsg.getLanguage());
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            
            if (userService.getUserCompany() != null) {
                query.setCompany(userService.getUserCompany());
            }
            @SuppressWarnings("unchecked")
            List<Notification> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Notification s = results.get(i);
                CMJSNotification.CGEntries entry =
                        new CMJSNotification.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSNotification.CGEntries[] entries = realMsg.getEntries();

            for (CMJSNotification.CGEntries e : entries) {
                Notification s = new Notification();
                updateEntity(s, e);
                try {
                    dao.save(s);
                } catch(ConstraintViolationException error){
                    return handleUniqueConstraintViolation(error);
                }
                
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }

        return realMsg;
    }
    private CMJSError handleUniqueConstraintViolation(ConstraintViolationException cvError) {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        String message = MessageText._("Notification with same Mode and Language Already Exists");
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        error.setErrorDescription(message);
        log.warn(message, cvError);
        return error;
    }
}


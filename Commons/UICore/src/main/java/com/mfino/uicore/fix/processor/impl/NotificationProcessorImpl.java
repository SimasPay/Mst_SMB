/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
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
            s.setId(new BigDecimal(e.getID()));
        }
        if (e.getLanguage() != null) {
            s.setLanguage(e.getLanguage());
        }
        if (e.getMSPID() != null) {
            MfinoServiceProviderDAO mspdao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            s.setMfinoServiceProvider(mspdao.getById(e.getMSPID()));
        }
        if (e.getNotificationCode() != null) {
            s.setCode(e.getNotificationCode());
        }
        if (e.getNotificationCodeName() != null) {
            s.setCodename(e.getNotificationCodeName());
        }
        if (e.getNotificationMethod() != null) {
            s.setNotificationmethod(e.getNotificationMethod());
        }
        if (e.getNotificationStatus() != null) {
            s.setStatus(e.getNotificationStatus());
        }
        if (e.getNotificationText() != null) {
            s.setText(e.getNotificationText());
        }
        if (e.getSTKMLText() != null) {
            s.setStkml(e.getSTKMLText());
        }
        if (e.getStatusTime()!= null) {
            s.setStatustime(e.getStatusTime());
        }
        if (e.getUpdatedBy()!= null) {
            s.setUpdatedby(e.getUpdatedBy());
        }
         if (e.getLastUpdateTime()!= null) {
            s.setLastupdatetime(e.getLastUpdateTime());
        }

        if (e.getCreateTime() != null) {
            s.setCreatetime(e.getCreateTime());
        }
        if (e.getCreatedBy() != null) {
            s.setCreatedby(e.getCreatedBy());
        }
        if (e.getAccessCode() != null) {
            s.setAccesscode(e.getAccessCode());
        }
        if (e.getSMSNotificationCode() != null) {
            s.setSmsnotificationcode(e.getSMSNotificationCode());
        }
        if (e.getCompanyID() != null) {
            CompanyDAO dao = DAOFactory.getInstance().getCompanyDAO();
            Company company= dao.getById(e.getCompanyID());
            s.setCompany(company);
        }
        if (e.getIsActive() != null) {
            s.setIsactive((short) (e.getIsActive() ? 1:0));
        }
    }

    private void updateMessage(Notification e, CMJSNotification.CGEntries s) {
        if (e.getId() != null) {
            s.setID(e.getId().longValue());
        }
        if ((Long)e.getLanguage() != null) {
            s.setLanguage(((Long)e.getLanguage()).intValue());
        }
        s.setMSPID(e.getMfinoServiceProvider().getId().longValue());
        if ((Long)e.getCode() != null) {
            s.setNotificationCode(((Long)e.getCode()).intValue());
        }
        if (e.getCodename() != null) {
            s.setNotificationCodeName(e.getCodename());
        }
        if ((Long)e.getNotificationmethod() != null) {
            s.setNotificationMethod(((Long)e.getNotificationmethod()).intValue());
        }
        if ((Long)e.getStatus() != null) {
            s.setNotificationStatus(((Long)e.getStatus()).intValue());
        }
        if (e.getText() != null) {
            try {
				s.setNotificationText(e.getText().getSubString(0, ((Long)e.getText().length()).intValue()));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        }
        if (e.getStkml() != null) {
            try {
				s.setSTKMLText(e.getStkml().getSubString(0, ((Long)e.getStkml().length()).intValue()));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        }
        if (e.getStatustime()!= null) {
            s.setStatusTime(e.getStatustime());
        }
        if (e.getUpdatedby()!= null) {
            s.setUpdatedBy(e.getUpdatedby());
        }
         if (e.getLastupdatetime()!= null) {
            s.setLastUpdateTime(e.getLastupdatetime());
        }
        if (e.getCreatetime() != null) {
            s.setCreateTime(e.getCreatetime());
        }
        if (e.getCreatedby() != null) {
            s.setCreatedBy(e.getCreatedby());
        }
        if (e.getAccesscode() != null) {
            s.setAccessCode(e.getAccesscode());
        }
        if (e.getSmsnotificationcode() != null) {
            s.setSMSNotificationCode(e.getSmsnotificationcode());
        }
        if(e.getCompany() != null) {
            Company company = e.getCompany();
            s.setCompanyID(company.getId().longValue());
        }
        if (e.getIsactive() != null) {
            s.setIsActive(e.getIsactive() != 0);
        }
        s.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, ((Long)e.getLanguage()).intValue(), e.getLanguage()));
        s.setNotificationMethodText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_NotificationMethod, ((Long)e.getNotificationmethod()).intValue(), e.getNotificationmethod()));
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


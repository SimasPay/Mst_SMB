package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSubscribers;
import com.mfino.service.MailService;
import com.mfino.service.SystemParametersService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SubscriberAdditonalFieldsProcessor;

@Service("SubscriberAdditonalFieldsProcessorImpl")
public class SubscriberAdditonalFieldsProcessorImpl extends BaseFixProcessor implements SubscriberAdditonalFieldsProcessor{
    private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
    @Autowired
    @Qualifier("MailServiceImpl")
    private MailService mailService;
    
    @Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
    
    private void updateEntity(Subscriber s, CMJSSubscribers.CGEntries e) {
        if (e.getFirstName() != null) {
            s.setFirstname(e.getFirstName());
        }
        if (e.getLastName() != null) {
            s.setLastname(e.getLastName());
        }
        if (e.getEmail() != null) {
            s.setEmail(e.getEmail());
            s.setIsemailverified((short) Boolean.compare(false, true));
        }
        if (e.getLanguage() != null) {
            s.setLanguage(e.getLanguage());
        }
        else
        {
        	s.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
        }
        if (e.getMSPID() != null) {
            s.setMfinoServiceProvider(mspDAO.getById(e.getMSPID()));
        }
        if (e.getNotificationMethod() != null) {
            s.setNotificationmethod(e.getNotificationMethod().longValue());
        }
//        if (e.getParentID() != null) {
//            s.setParentID(e.getParentID());
//        }
        if (e.getSubscriberRestrictions() != null) {
            s.setRestrictions(e.getSubscriberRestrictions());
        }
        if (e.getSubscriberStatus() != null) {
            s.setStatus(e.getSubscriberStatus());
        }
        if (e.getSubscriberType() != null) {
            s.setType(e.getSubscriberType());
        }
        if (e.getTimezone() != null) {
            s.setTimezone(e.getTimezone());
        }
        if (e.getStatusTime() != null) {
            s.setStatustime(e.getStatusTime());
        }
//       if(e.getDompetMerchant()!=null){
//           s.setDompetMerchant(e.getDompetMerchant());
//       }
    }

    private void updateMessage(Subscriber s, CMJSSubscribers.CGEntries entry) {
        entry.setID(s.getId().longValue());

        if (s.getFirstname() != null) {
            entry.setFirstName(s.getFirstname());
        }
        if (s.getLastname() != null) {
            entry.setLastName(s.getLastname());
        }

        if (s.getEmail() != null) {
            entry.setEmail(s.getEmail());
        }

        entry.setLanguage(Integer.valueOf(Long.valueOf(s.getLanguage()).intValue()));

        if (s.getActivationtime() != null) {
            entry.setActivationTime(s.getActivationtime());
        }

        if (s.getLastupdatetime() != null) {
            entry.setLastUpdateTime(s.getLastupdatetime());
        }

        entry.setMSPID(s.getMfinoServiceProvider().getId().longValue());

        if (s.getNotificationmethod() != null) {
            entry.setNotificationMethod(s.getNotificationmethod().intValue());
        }

        if (s.getCreatetime() != null) {
            entry.setCreateTime(s.getCreatetime());
        }

//        if (s.getParentID() != null) {
//            entry.setParentID(s.getParentID());
//        }
//        if(s.getDompetMerchant()!=null)
//        {
//            entry.setDompetMerchant(s.getDompetMerchant());
//        }
        if (s.getEmail() != null) {
            entry.setEmail(s.getEmail());
        }

        entry.setSubscriberRestrictions(Integer.valueOf(Long.valueOf(s.getRestrictions()).intValue()));

        entry.setSubscriberStatus(Integer.valueOf(Long.valueOf(s.getStatus()).intValue()));

        if (s.getUpdatedby() != null) {
            entry.setUpdatedBy(s.getUpdatedby());
        }

        if (s.getCreatedby() != null) {
            entry.setCreatedBy(s.getCreatedby());
        }

        entry.setSubscriberType(Integer.valueOf(Long.valueOf(s.getType()).intValue()));

        if (s.getTimezone() != null) {
            entry.setTimezone(s.getTimezone());
        }
        if (s.getStatustime() != null) {
            entry.setStatusTime(s.getStatustime());
        }

        if (s.getVersion() != 0) {
            entry.setRecordVersion(Integer.valueOf(Long.valueOf(s.getVersion()).intValue()));
        }
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSSubscribers realMsg = (CMJSSubscribers) msg;
        @SuppressWarnings("unused")
		
        SubscribersAdditionalFieldsDAO addFieldsDAO = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
        SubscriberDAO dao = DAOFactory.getInstance().getSubscriberDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSubscribers.CGEntries[] entries = realMsg.getEntries();

            for (CMJSSubscribers.CGEntries entry : entries) {
                Subscriber subscriberObj = dao.getById(entry.getID());

                // Check for Stale Data
                if (!entry.getRecordVersion().equals(subscriberObj.getVersion())) {
                    handleStaleDataException();
                }

                updateEntity(subscriberObj, entry);
                dao.save(subscriberObj);
                if(entry.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
					mailService.generateEmailVerificationMail(subscriberObj, entry.getEmail());					
				}
                updateMessage(subscriberObj, entry);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            SubscriberQuery query = new SubscriberQuery();
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            List<Subscriber> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Subscriber s = results.get(i);
                CMJSSubscribers.CGEntries entry =
                        new CMJSSubscribers.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSubscribers.CGEntries[] entries = realMsg.getEntries();

            for (CMJSSubscribers.CGEntries e : entries) {
                Subscriber s = new Subscriber();
                updateEntity(s, e);
                dao.save(s);
                if(e.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
					mailService.generateEmailVerificationMail(s, e.getEmail());
				}
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }

        return realMsg;
    }
}

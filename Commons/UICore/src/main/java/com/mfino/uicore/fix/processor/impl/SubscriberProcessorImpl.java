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
import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSubscribers;
import com.mfino.service.MailService;
import com.mfino.service.SystemParametersService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SubscriberProcessor;

@Service("SubscriberProcessorImpl")
public class SubscriberProcessorImpl extends BaseFixProcessor implements SubscriberProcessor{

	@Autowired
	 @Qualifier("MailServiceImpl")
	 private MailService mailService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
    private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();

    private void updateEntity(Subscriber s, CMJSSubscribers.CGEntries e) {
    	String ID = String.valueOf(s.getId());
    	if(ID==null){
    		ID = e.getFirstName();
    	}
        if (e.getFirstName() != null) {
        	if(!e.getFirstName().equals(s.getFirstname())){
        		log.info("Subscriber:"+ID+" First name updated to "+e.getFirstName()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setFirstname(e.getFirstName());
        }
        if (e.getLastName() != null) {
        	if(!e.getLastName().equals(s.getLastname())){
        		log.info("Subscriber:"+ID+" Last name updated to "+e.getLastName()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setLastname(e.getLastName());
        }
        if (e.getNickname() != null) {
        	if(!e.getNickname().equals(s.getNickname())){
        		log.info("Subscriber:"+ID+" Last name updated to "+e.getNickname()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setNickname(e.getNickname());
        }
        if (e.getEmail() != null) {
        	if(!e.getEmail().equals(s.getEmail())){
        		log.info("Subscriber:"+ID+" Email updated to "+e.getEmail()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setEmail(e.getEmail());
            s.setIsemailverified((short) Boolean.compare(false, true));
        }
        if (e.getLanguage() != null) {
        	if(!e.getLanguage().equals(s.getLanguage())){
        		log.info("Subscriber:"+ID+" Language updated to "+e.getLanguage()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setLanguage(e.getLanguage());
        }
        else
        {
        	s.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
        }
        if (e.getMSPID() != null) {
        	MfinoServiceProvider msp = mspDAO.getById(e.getMSPID());
        	if(s.getMfinoServiceProvider()!=msp){
        		log.info("Subscriber:"+ID+" mFinoServiceProvider updated to "+msp.getId()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setMfinoServiceProvider(msp);
        }
        if (e.getNotificationMethod() != null) {
        	if(!e.getNotificationMethod().equals(s.getNotificationmethod())){
        		log.info("Subscriber:"+ID+" Notification method updated to "+e.getNotificationMethod()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setNotificationmethod(e.getNotificationMethod());
        }
//        if (e.getParentID() != null) {
//            s.setParentID(e.getParentID());
//        }
        if (e.getSubscriberRestrictions() != null) {
        	if(!e.getSubscriberRestrictions().equals(s.getRestrictions())){
        		log.info("Subscriber:"+ID+" Restrictions updated to "+e.getSubscriberRestrictions()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setRestrictions(e.getSubscriberRestrictions());
        }
        if (e.getSubscriberStatus() != null) {
        	if(!e.getSubscriberStatus().equals(s.getStatus())){
        		log.info("Subscriber:"+ID+" Status updated to "+e.getSubscriberStatus()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setStatus(e.getSubscriberStatus());
        }
        if (e.getSubscriberType() != null) {
        	if(!e.getSubscriberType().equals(s.getType())){
        		log.info("Subscriber:"+ID+" Type updated to "+e.getSubscriberType()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setType(e.getSubscriberType());
        }
        if (e.getTimezone() != null) {
        	if(!e.getTimezone().equals(s.getTimezone())){
        		log.info("Subscriber:"+ID+" Timezone updated to "+e.getTimezone()+" by user:"+getLoggedUserNameWithIP());
        	}
            s.setTimezone(e.getTimezone());
        }
        if (e.getStatusTime() != null) {
        	// *FindbugsChange*
        	// Previous -- if(!e.getStatusTime().equals(s.getTimezone())){
        	if(!e.getStatusTime().equals(s.getStatustime())){
        		log.info("Subscriber:"+ID+" StatusTime updated to "+e.getStatusTime()+" by user:"+getLoggedUserNameWithIP());
        	}
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
        if (s.getNickname() != null) {
            entry.setNickname(s.getNickname());
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

        SubscriberDAO dao = DAOFactory.getInstance().getSubscriberDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSubscribers.CGEntries[] entries = realMsg.getEntries();

            for (CMJSSubscribers.CGEntries entry : entries) {
                Subscriber subscriberObj = dao.getById(entry.getID());
                log.info("Subscriber:"+subscriberObj.getId()+" details edit requested by user:"+getLoggedUserNameWithIP());

                // Check for Stale Data
                if (!entry.getRecordVersion().equals(subscriberObj.getVersion())) {
                    handleStaleDataException();
                }

                updateEntity(subscriberObj, entry);
                dao.save(subscriberObj);
                if(entry.getEmail()!= null && systemParametersService.getIsEmailVerificationNeeded()) {
                	mailService.generateEmailVerificationMail(subscriberObj, entry.getEmail());
                }
                updateMessage(subscriberObj, entry);
                log.info("Subscriber:"+subscriberObj.getId()+" details edit completed by user:"+getLoggedUserNameWithIP());
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
                log.info("Subscriber:"+s.getId()+" details viewed completed by user:"+getLoggedUserNameWithIP());
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSubscribers.CGEntries[] entries = realMsg.getEntries();

            for (CMJSSubscribers.CGEntries e : entries) {
                Subscriber s = new Subscriber();
                updateEntity(s, e);
                s.setRegistrationmedium(CmFinoFIX.RegistrationMedium_AdminApp);
                dao.save(s);
                log.info("Subscriber:"+s.getId()+" created by user:"+getLoggedUserNameWithIP());
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.Person2PersonDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.Person2PersonQuery;
import com.mfino.domain.Person2Person;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSPerson2Person;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.Person2PersonProcessor;

/**
 *
 * @author sunil
 */
@Service("Person2PersonProcessorImpl")
public class Person2PersonProcessorImpl extends BaseFixProcessor implements Person2PersonProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSPerson2Person realMsg = (CMJSPerson2Person) msg;

        Person2PersonDAO Person2PersonDAO = DAOFactory.getInstance().getPerson2PersonDAO();
        

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CmFinoFIX.CMJSPerson2Person.CGEntries[] entries = realMsg.getEntries();
            Person2PersonQuery query = new Person2PersonQuery();
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            for (CMJSPerson2Person.CGEntries e : entries) {
                Person2Person s = Person2PersonDAO.getById(e.getID());

                // Check for Stale Data
               if(!e.getRecordVersion().equals(s.getVersion()))
               {
                        handleStaleDataException();
               }

                updateEntity(s, e);
                try {
                    Person2PersonDAO.save(s);
                } catch(ConstraintViolationException error){
                    return handleUniqueConstraintViolation(error);
                }
                updateMessage(s, e);
            }
            realMsg.settotal(query.getTotal());
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            Person2PersonQuery query = new Person2PersonQuery();
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
           
            query.setSubscriberId(realMsg.getSubscriberIDSearch());
            List<Person2Person> results;
            results = Person2PersonDAO.get(query);

            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                Person2Person s = results.get(i);
                CMJSPerson2Person.CGEntries entry =
                        new CMJSPerson2Person.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.settotal(query.getTotal());
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSPerson2Person.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPerson2Person.CGEntries e : entries) {
                Person2Person s = new Person2Person();
                updateEntity(s, e);
                try {
                    Person2PersonDAO.save(s);
                } catch(ConstraintViolationException error){
                    return handleUniqueConstraintViolation(error);
                }
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSPerson2Person.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPerson2Person.CGEntries e : entries) {
                Person2PersonDAO.deleteById(e.getID());
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        
        return realMsg;
    }
    public CMJSError handleUniqueConstraintViolation(ConstraintViolationException error) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._("MDN already exists.Please enter another MDN.");
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        log.warn(message, error);
        return errorMsg;
    }

    public void updateEntity(Person2Person s, CMJSPerson2Person.CGEntries e) {

        if (e.getID() != null) {
            s.setID(e.getID());
        }
        if (e.getActivationTime() != null) {
            s.setActivationTime(e.getActivationTime());
        }
        if (e.getCreatedBy() != null) {
            s.setCreatedBy(e.getCreatedBy());
        }

        if (e.getCreateTime() != null) {
            s.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            s.setLastUpdateTime(e.getLastUpdateTime());
        }
        if (e.getMDN() != null) {
            s.setMDN((e.getMDN()));
        }
        if (e.getMSPID() != null) {
            MfinoServiceProviderDAO mspdao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            s.setmFinoServiceProviderByMSPID(mspdao.getById(e.getMSPID()));
        }
        if (e.getPeerName() != null) {
            s.setPeerName((e.getPeerName()));
        }
        if (e.getSubscriberID() != null) {
            SubscriberDAO sdao = DAOFactory.getInstance().getSubscriberDAO();
            s.setSubscriber(sdao.getById(e.getSubscriberID()));
        }
        if (e.getUpdatedBy() != null) {
            s.setUpdatedBy((e.getUpdatedBy()));
        }
    }

    public void updateMessage(Person2Person s, CMJSPerson2Person.CGEntries e) {

        if (s.getID() != null) {
            e.setID(s.getID());
        }
        if (s.getActivationTime() != null) {
            e.setActivationTime(s.getActivationTime());
        }
        if (s.getCreatedBy() != null) {
            e.setCreatedBy(s.getCreatedBy());
        }

        if (s.getCreateTime() != null) {
            e.setCreateTime(s.getCreateTime());
        }
        if (s.getLastUpdateTime() != null) {
            e.setLastUpdateTime(s.getLastUpdateTime());
        }

        if (s.getMDN() != null) {
            e.setMDN((s.getMDN()));
        }
        if (s.getmFinoServiceProviderByMSPID() != null) {
            e.setMSPID(s.getmFinoServiceProviderByMSPID().getID());
        }
        if (s.getPeerName() != null) {
            e.setPeerName((s.getPeerName()));
        }
        if (s.getSubscriber() != null) {
            e.setSubscriberID(s.getSubscriber().getID());
        }
        if (s.getUpdatedBy() != null) {
            e.setUpdatedBy((s.getUpdatedBy()));
        }

        if(s.getVersion() != null)
        {
            e.setRecordVersion(s.getVersion());
        }

    }
}

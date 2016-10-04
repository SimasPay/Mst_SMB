/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMfinoServiceProvider;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.MfinoServiceproviderProcessor;

/**
 *
 * @author Srinivas
 */

@Service("MfinoServiceproviderProcessorImpl")
public class MfinoServiceproviderProcessorImpl extends BaseFixProcessor implements MfinoServiceproviderProcessor {

	MfinoServiceProviderDAO dao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    private void updateEntity(MfinoServiceProvider m, CMJSMfinoServiceProvider.CGEntries e) {
        if (e.getName() != null) {
            m.setName(e.getName());
        }
        if (e.getDescription() != null) {
            m.setDescription(e.getDescription());
        }
        if (e.getStatus() != null) {
            m.setStatus(e.getStatus().longValue());
        }
        if (e.getStatusTime() != null) {
            m.setStatustime(e.getStatusTime());
        }
        if (e.getCreateTime() != null) {
            m.setCreatetime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            m.setLastupdatetime(e.getLastUpdateTime());
        }
        if (e.getUpdatedBy() != null) {
            m.setUpdatedby(e.getUpdatedBy());
        }
    }

    private void updateMessage(MfinoServiceProvider m, CMJSMfinoServiceProvider.CGEntries entry) {
        entry.setID(m.getId().longValue());

        if (m.getName() != null) {
            entry.setName(m.getName());
        }
        if (m.getDescription() != null) {
            entry.setDescription(m.getDescription());
        }
        if (m.getStatus() != null) {
            entry.setStatus(m.getStatus().intValue());
        }
        if (m.getStatustime() != null) {
            entry.setStatusTime(m.getStatustime());
        }
        if (m.getCreatetime() != null) {
            entry.setCreateTime(m.getCreatetime());
        }
        if (m.getLastupdatetime() != null) {
            entry.setLastUpdateTime(m.getLastupdatetime());
        }
        if (m.getUpdatedby() != null) {
            entry.setUpdatedBy(m.getUpdatedby());
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSMfinoServiceProvider realMsg = (CMJSMfinoServiceProvider) msg;

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMfinoServiceProvider.CGEntries[] entries = realMsg.getEntries();

            for (CMJSMfinoServiceProvider.CGEntries e : entries) {
                MfinoServiceProvider m = dao.getById(e.getID());
                updateEntity(m, e);
                dao.save(m);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            List<MfinoServiceProvider> results = dao.getAll();
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                MfinoServiceProvider s = results.get(i);
                CMJSMfinoServiceProvider.CGEntries entry =
                        new CMJSMfinoServiceProvider.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMfinoServiceProvider.CGEntries[] entries = realMsg.getEntries();

            for (CMJSMfinoServiceProvider.CGEntries e : entries) {
                MfinoServiceProvider m = new MfinoServiceProvider();
                updateEntity(m, e);
                dao.save(m);
                updateMessage(m, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }

        return realMsg;
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public MfinoServiceProvider getById(long id) {
    	return dao.getById(id);
    }
}

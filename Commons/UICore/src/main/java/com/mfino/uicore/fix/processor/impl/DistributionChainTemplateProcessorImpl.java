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
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDistributionChainTemplate;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.DistributionChainTemplateProcessor;

/**
 *
 * @author xchen
 */
@Service("DistributionChainTemplateProcessorImpl")
public class DistributionChainTemplateProcessorImpl extends BaseFixProcessor implements DistributionChainTemplateProcessor{
    private DistributionChainTemplateDAO templateDAO = DAOFactory.getInstance().getDistributionChainTemplateDAO();

    private void updateMessage(DistributionChainTemplate e,
            CMJSDistributionChainTemplate.CGEntries m) {
        m.setID(e.getID());
        m.setDescription(e.getDescription());
        m.setDistributionChainName(e.getName());
        m.setLevelNumber(e.getDistributionChainLevelFromTemplateID().size());
        m.setCreatedBy(e.getCreatedBy());
        m.setCreateTime(e.getCreateTime());
        m.setUpdatedBy(e.getUpdatedBy());
        m.setLastUpdateTime(e.getLastUpdateTime());
        if (null != e.getVersion()) {
            m.setRecordVersion(e.getVersion());
        }
        if(e.getService() != null){
        	m.setServiceName(e.getService().getServiceName());
        	m.setServiceID(e.getService().getID());
        }
    }

    private void updateEntity(DistributionChainTemplate e,
            CMJSDistributionChainTemplate.CGEntries m) {
    	ServiceDAO serviceDao = DAOFactory.getInstance().getServiceDAO();
        if (m.getDescription() != null) {
            e.setDescription(m.getDescription());
        }
        if (m.getDistributionChainName() != null) {
            e.setName(m.getDistributionChainName());
        }
        if(null != m.getServiceID()){
        	e.setService(serviceDao.getById(m.getServiceID()));
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSDistributionChainTemplate realMsg = (CMJSDistributionChainTemplate) msg;

        if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
            CMJSDistributionChainTemplate.CGEntries[] entries = realMsg.getEntries();

            for (CMJSDistributionChainTemplate.CGEntries e : entries) {
                DistributionChainTemplate s = templateDAO.getById(e.getID());

                // Check for Stale Data
                if (!e.getRecordVersion().equals(s.getVersion())) {
                    handleStaleDataException();
                }

                updateEntity(s, e);
                templateDAO.save(s);
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            DistributionChainTemplateQuery query = new DistributionChainTemplateQuery();
            query.setId(realMsg.getIDSearch());
            query.setDistributionChainTemplateName(realMsg.getNameSearch());
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            query.setCreatedBy(realMsg.getCreatedBySearch());
            query.setServiceIdSearch(realMsg.getServiceIDSearch());
            
            List<DistributionChainTemplate> results = templateDAO.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                DistributionChainTemplate s = results.get(i);
                CMJSDistributionChainTemplate.CGEntries entry =
                        new CMJSDistributionChainTemplate.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSDistributionChainTemplate.CGEntries[] entries = realMsg.getEntries();
            
            for (CMJSDistributionChainTemplate.CGEntries e : entries) {
                DistributionChainTemplate s = new DistributionChainTemplate();
                updateEntity(s, e);
                templateDAO.save(s);
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }
        return realMsg;
    }
}

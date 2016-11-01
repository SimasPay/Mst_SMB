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
import com.mfino.domain.DistributionChainTemp;
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

    private void updateMessage(DistributionChainTemp e,
            CMJSDistributionChainTemplate.CGEntries m) {
        m.setID(e.getId().longValue());
        m.setDescription(e.getDescription());
        m.setDistributionChainName(e.getName());
        m.setLevelNumber(e.getDistributionChainLvls().size());
        m.setCreatedBy(e.getCreatedby());
        m.setCreateTime(e.getCreatetime());
        m.setUpdatedBy(e.getUpdatedby());
        m.setLastUpdateTime(e.getLastupdatetime());
        if (null != e.getVersion()) {
            m.setRecordVersion(e.getVersion());
        }
        if(((Long)e.getServiceid()) != null){
        	ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
        	com.mfino.domain.Service service = serviceDAO.getById(e.getServiceid());
        	m.setServiceName(service.getServicename());
        	m.setServiceID(e.getServiceid());
        }
    }

    private void updateEntity(DistributionChainTemp e,
            CMJSDistributionChainTemplate.CGEntries m) {
        if (m.getDescription() != null) {
            e.setDescription(m.getDescription());
        }
        if (m.getDistributionChainName() != null) {
            e.setName(m.getDistributionChainName());
        }
        if(null != m.getServiceID()){
        	e.setServiceid(m.getServiceID());
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSDistributionChainTemplate realMsg = (CMJSDistributionChainTemplate) msg;

        if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
            CMJSDistributionChainTemplate.CGEntries[] entries = realMsg.getEntries();

            for (CMJSDistributionChainTemplate.CGEntries e : entries) {
                DistributionChainTemp s = templateDAO.getById(e.getID());

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
            
            List<DistributionChainTemp> results = templateDAO.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                DistributionChainTemp s = results.get(i);
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
                DistributionChainTemp s = new DistributionChainTemp();
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

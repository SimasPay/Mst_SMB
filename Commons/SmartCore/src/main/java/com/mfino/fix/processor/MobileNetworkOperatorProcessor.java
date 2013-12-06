/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.fix.processor;

import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MobileNetworkOperatorDAO;
import com.mfino.domain.MobileNetworkOperator;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMobileNetworkOperator;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 *
 * @author Raju
 */
public class MobileNetworkOperatorProcessor extends BaseFixProcessor {
private void updateEntity(MobileNetworkOperator m, CmFinoFIX.CMJSMobileNetworkOperator.CGEntries e) {
        if (e.getName() != null) {
            m.setName(e.getName());
        }
        if (e.getStatus() != null) {
            m.setStatus(e.getStatus());
        }
        if (e.getDescription() != null) {
            m.setDescription(e.getDescription());
        }
        if (e.getStatusTime() != null) {
            m.setStatusTime(e.getStatusTime());
        }
        if (e.getCreateTime() != null) {
            m.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            m.setLastUpdateTime(e.getLastUpdateTime());
        }

       if (e.getUpdatedBy() != null) {
            m.setUpdatedBy(e.getUpdatedBy());
        }
        if(e.getCreatedBy()!=null){
            m.setCreatedBy(e.getCreatedBy());
        }

    }

    private void updateMessage(MobileNetworkOperator m, CMJSMobileNetworkOperator.CGEntries entry) {
        entry.setID(m.getID());

         if (m.getName() != null) {
            entry.setName(m.getName());
        }
        if (m.getStatus() != null) {
            entry.setStatus(m.getStatus());
        }
        if (m.getDescription() != null) {
            entry.setDescription(m.getDescription());
        }
        if (m.getStatusTime() != null) {
            entry.setStatusTime(m.getStatusTime());
        }
        if (m.getCreateTime() != null) {
            entry.setCreateTime(m.getCreateTime());
        }
        if (m.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(m.getLastUpdateTime());
        }
         if (m.getUpdatedBy() != null) {
            entry.setUpdatedBy(m.getUpdatedBy());
        }
        if(m.getCreatedBy()!= null)
        {
            entry.setCreatedBy(m.getCreatedBy());
        }
        if (m.getVersion() != null) {
            entry.setRecordVersion(m.getVersion());
        }

    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSMobileNetworkOperator realMsg = (CMJSMobileNetworkOperator) msg;

        MobileNetworkOperatorDAO dao = DAOFactory.getInstance().getMobileNetworkOperatorDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMobileNetworkOperator.CGEntries[] entries = realMsg.getEntries();

            for (CMJSMobileNetworkOperator.CGEntries e : entries) {
                MobileNetworkOperator m = dao.getById(e.getID());
                updateEntity(m, e);
                dao.save(m);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            List<MobileNetworkOperator> results = dao.getAll();
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
               MobileNetworkOperator s = results.get(i);
                CMJSMobileNetworkOperator.CGEntries entry =
                        new CMJSMobileNetworkOperator.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMobileNetworkOperator.CGEntries[] entries = realMsg.getEntries();

            for (CMJSMobileNetworkOperator.CGEntries e : entries) {
                MobileNetworkOperator m = new MobileNetworkOperator();
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
}



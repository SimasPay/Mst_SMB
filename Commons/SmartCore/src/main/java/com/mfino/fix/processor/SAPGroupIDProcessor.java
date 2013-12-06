/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SAPGroupIDDAO;
import com.mfino.dao.query.SAPGroupIDQuery;
import com.mfino.domain.SAPGroupID;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 *
 * @author xchen
 */
public class SAPGroupIDProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CmFinoFIX.CMJSSAPGroupID realMsg = (CmFinoFIX.CMJSSAPGroupID) msg;

        SAPGroupIDDAO dao = DAOFactory.getInstance().getSAPGroupIDDAO();

        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            SAPGroupIDQuery query = new SAPGroupIDQuery();
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            String jsquery = realMsg.getquery();
            if (jsquery != null) {
                if (StringUtils.isNumeric(jsquery)) {
                    query.setGroupID(jsquery);
                } else {
                    query.setGroupIDName(jsquery);
                }
            }

            query.setSortString(CmFinoFIX.CMJSSAPGroupID.CGEntries.FieldName_GroupIDName + ":asc");
            List<SAPGroupID> results = dao.get(query);
            realMsg.settotal(query.getTotal());
            realMsg.allocateEntries(results.size());

            int i = 0;
            for (SAPGroupID r : results) {
                CmFinoFIX.CMJSSAPGroupID.CGEntries entry = new CmFinoFIX.CMJSSAPGroupID.CGEntries();
                entry.setGroupID(r.getGroupID());
                entry.setGroupIDName(r.getGroupIDName());
                entry.setDisplayText(String.format("%s (%s)", r.getGroupID(), r.getGroupIDName()));
                realMsg.getEntries()[i] = entry;
                i++;
            }
        }
        return realMsg;
    }
}

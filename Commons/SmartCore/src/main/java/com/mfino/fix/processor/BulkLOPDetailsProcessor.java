/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.fix.processor;

import com.mfino.dao.BulkLOPDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.BulkLOP;
import com.mfino.domain.Pocket;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkLOPDetails;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 *
 * @author admin
 */
public class BulkLOPDetailsProcessor extends BaseFixProcessor
{
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSBulkLOPDetails realMsg = (CMJSBulkLOPDetails) msg;
        BulkLOPDAO bDao = DAOFactory.getInstance().getBulkLOPDAO();
        BulkLOP bLop = bDao.getById(realMsg.getIDSearch());
        String str = bLop.getFileData();
        String[] records = str.split("[|]");
        PocketDAO pDao = DAOFactory.getInstance().getPocketDAO();
        realMsg.allocateEntries(records.length);
        for(int i=0;i<records.length; i++)
        {
            CMJSBulkLOPDetails.CGEntries entry =
                        new CMJSBulkLOPDetails.CGEntries();
            String[] fields = str.split(",");
            Pocket pocket = pDao.getById(Long.parseLong(fields[0]));
            entry.setPocketTemplDescription(pocket.getPocketTemplate().getDescription());
            if(fields[1]!=null && !fields[1].equals("null"))
            {
                entry.setDenomination(Long.parseLong(fields[1]));
            }
            else
            {
                entry.setDenomination(1L);
            }
            entry.setUnits(Long.parseLong(fields[2]));
            realMsg.getEntries()[i] = entry;
        }
        realMsg.setsuccess(CmFinoFIX.Boolean_True);
        realMsg.settotal(records.length);
        return realMsg;
    }
}

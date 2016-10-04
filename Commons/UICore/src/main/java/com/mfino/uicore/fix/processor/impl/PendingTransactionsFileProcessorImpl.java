/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingTransactionsFileDAO;
import com.mfino.dao.query.PendingTransactionsFileQuery;
import com.mfino.domain.PendingTxnsFile;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPendingTransactionsFile;
import com.mfino.service.EnumTextService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PendingTransactionsFileProcessor;

/**
 *
 * @author Raju
 */
@Service("PendingTransactionsFileProcessorImpl")
public class PendingTransactionsFileProcessorImpl extends BaseFixProcessor implements PendingTransactionsFileProcessor{

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    public void updateMessage(PendingTxnsFile ptf, CMJSPendingTransactionsFile.CGEntries entry) {
        entry.setID(ptf.getId().longValue());

        if (ptf.getRecordcount() != null) {
            entry.setRecordCount(ptf.getRecordcount().intValue());
        }
        if (ptf.getLastupdatetime() != null) {
            entry.setLastUpdateTime(ptf.getLastupdatetime());
        }
        if (ptf.getFilename() != null) {
            entry.setFileName(ptf.getFilename());
        }
        if (ptf.getCreatedby() != null) {
            entry.setCreatedBy(ptf.getCreatedby());
        }
        if (ptf.getUpdatedby() != null) {
            entry.setUpdatedBy(ptf.getUpdatedby());
        }
        if (ptf.getCreatetime() != null) {
            entry.setCreateTime(ptf.getCreatetime());
        }
        if (ptf.getLastupdatetime() != null) {
            entry.setLastUpdateTime(ptf.getLastupdatetime());
        }
        if (ptf.getResolveas() != null) {
            entry.setResolveAs(ptf.getResolveas().intValue());
        }
        if ((Long)ptf.getUploadfilestatus() != null) {
            entry.setUploadFileStatus(((Long)ptf.getUploadfilestatus()).intValue());
            entry.setUploadStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UploadFileStatus, null, ptf.getUploadfilestatus()));
        }

        entry.setResolveAsText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ResolveAs, null, ptf.getResolveas()));

    }
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
        CMJSPendingTransactionsFile realMsg = (CMJSPendingTransactionsFile) msg;
        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            PendingTransactionsFileQuery query = new PendingTransactionsFileQuery();
            PendingTransactionsFileDAO dao = DAOFactory.getInstance().getPendingTransactionsFileDAO();
            query.setCompany(userService.getUserCompany());
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            List<PendingTxnsFile> results = null;
            try {
                results = dao.get(query);
                realMsg.allocateEntries(results.size());
                for (int i = 0; i < results.size(); i++) {
                	PendingTxnsFile s = results.get(i);
                    CMJSPendingTransactionsFile.CGEntries entry =
                            new CMJSPendingTransactionsFile.CGEntries();
                    updateMessage(s, entry);
                    realMsg.getEntries()[i] = entry;
                }
            } catch (Exception error) {
            	log.error("Error while process PendingTransactions file: ", error);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        }
        return realMsg;
    }
}

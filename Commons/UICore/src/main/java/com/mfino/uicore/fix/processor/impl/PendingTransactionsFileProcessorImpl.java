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
import com.mfino.domain.PendingTransactionsFile;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPendingTransactionsFile;
import com.mfino.service.EnumTextService;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
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

    public void updateMessage(PendingTransactionsFile ptf, CMJSPendingTransactionsFile.CGEntries entry) {
        entry.setID(ptf.getID());

        if (ptf.getRecordCount() != null) {
            entry.setRecordCount(ptf.getRecordCount());
        }
        if (ptf.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(ptf.getLastUpdateTime());
        }
        if (ptf.getFileName() != null) {
            entry.setFileName(ptf.getFileName());
        }
        if (ptf.getCreatedBy() != null) {
            entry.setCreatedBy(ptf.getCreatedBy());
        }
        if (ptf.getUpdatedBy() != null) {
            entry.setUpdatedBy(ptf.getUpdatedBy());
        }
        if (ptf.getCreateTime() != null) {
            entry.setCreateTime(ptf.getCreateTime());
        }
        if (ptf.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(ptf.getLastUpdateTime());
        }
        if (ptf.getResolveAs() != null) {
            entry.setResolveAs(ptf.getResolveAs());
        }
        if (ptf.getUploadFileStatus() != null) {
            entry.setUploadFileStatus(ptf.getUploadFileStatus());
            entry.setUploadStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UploadFileStatus, null, ptf.getUploadFileStatus()));
        }

        entry.setResolveAsText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ResolveAs, null, ptf.getResolveAs()));

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
            List<PendingTransactionsFile> results = null;
            try {
                results = dao.get(query);
                realMsg.allocateEntries(results.size());
                for (int i = 0; i < results.size(); i++) {
                    PendingTransactionsFile s = results.get(i);
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

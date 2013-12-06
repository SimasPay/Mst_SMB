/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadEntryDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkUploadEntryQuery;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkUploadEntry;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.BulkUploadEntryProcessor;

/**
 *
 * @author Raju
 */
@Service("BulkUploadEntryProcessorImpl")
public class BulkUploadEntryProcessorImpl extends BaseFixProcessor implements BulkUploadEntryProcessor{  
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

    public void updateMessage(BulkUploadEntry bue, CMJSBulkUploadEntry.CGEntries entry) {
        if (bue.getAmount() != null) {
            entry.setAmount(bue.getAmount());
        }
        if (bue.getID() != null) {
            entry.setID(bue.getID());
        }
        if (bue.getUploadID() != null) {
        	entry.setUploadID(bue.getUploadID());
        }
        if (bue.getDestMDN() != null) {
            entry.setDestMDN(bue.getDestMDN());
        }
        if (bue.getFailureReason() != null) {
            entry.setFailureReason(bue.getFailureReason());
        }
        if (bue.getLineNumber() != null) {
            entry.setLineNumber(bue.getLineNumber());
        }
        if (bue.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(bue.getLastUpdateTime());
        }
        if (bue.getStatus() != null) {
            entry.setStatus(bue.getStatus());
        }
        if (bue.getNotificationCode() != null) {
            entry.setNotificationCode(bue.getNotificationCode());
        }
        if (bue.getTransferFailureReason() != null) {
            entry.setTransferFailureReason(bue.getTransferFailureReason());
        }
        if (bue.getTransferID() != null) {
            entry.setTransferID(bue.getTransferID());
        }
        if (bue.getServiceChargeTransactionLogID() != null) {
        	entry.setServiceChargeTransactionLogID(bue.getServiceChargeTransactionLogID());
        }
        if (StringUtils.isNotBlank(bue.getFirstName())) {
        	entry.setFirstName(bue.getFirstName());
        }
        if (StringUtils.isNotBlank(bue.getLastName())) {
        	entry.setLastName(bue.getLastName());
        }
        if (bue.getIsUnRegistered() != null) {
        	entry.setIsUnRegistered(bue.getIsUnRegistered());
        }

        entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionsTransferStatus, null, entry.getStatus()));
        entry.setTransferFailureReasonText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, entry.getTransferFailureReason()));
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
        CMJSBulkUploadEntry realMsg = (CMJSBulkUploadEntry) msg;
        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            BulkUploadEntryQuery query = new BulkUploadEntryQuery();
            BulkUploadEntryDAO dao = DAOFactory.getInstance().getBulkUploadEntryDAO();
            if (realMsg.getIDSearch() != null) {
                query.setId(realMsg.getIDSearch());
            }
            if (realMsg.getTransactionsTransferStatus() != null) {
            	query.setStatus(realMsg.getTransactionsTransferStatus());
            }
            if (realMsg.getIsUnRegistered() != null) {
            	query.setIsUnRegistered(realMsg.getIsUnRegistered());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            List<BulkUploadEntry> results = null;
            try {
                results = dao.get(query);
                realMsg.allocateEntries(results.size());
                for (int i = 0; i < results.size(); i++) {
                    BulkUploadEntry s = results.get(i);
                    CMJSBulkUploadEntry.CGEntries entry =
                            new CMJSBulkUploadEntry.CGEntries();
                    updateMessage(s, entry);
                    realMsg.getEntries()[i] = entry;
                }
            } catch (Exception excp) {
                log.error("BulkUploadEntryProcessor process method ", excp);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        }
        return realMsg;
    }
//}
}

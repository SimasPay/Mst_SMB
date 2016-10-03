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
        if (bue.getId() != null) {
            entry.setID(bue.getId().longValue());
        }
        if (bue.getUploadid() != null) {
        	entry.setUploadID(bue.getUploadid().longValue());
        }
        if (bue.getDestmdn() != null) {
            entry.setDestMDN(bue.getDestmdn());
        }
        if (bue.getFailurereason() != null) {
            entry.setFailureReason(bue.getFailurereason());
        }
        if ((Long)bue.getLinenumber() != null) {
            entry.setLineNumber( ((Long)bue.getLinenumber()).intValue() );
        }
        if (bue.getLastupdatetime() != null) {
            entry.setLastUpdateTime(bue.getLastupdatetime());
        }
        if ((Long)bue.getStatus() != null) {
            entry.setStatus( ((Long)bue.getStatus()).intValue() );
        }
        if (bue.getNotificationcode() != null) {
            entry.setNotificationCode(bue.getNotificationcode().intValue());
        }
        if (bue.getTransferfailurereason() != null) {
            entry.setTransferFailureReason(bue.getTransferfailurereason().intValue());
        }
        if (bue.getTransferid() != null) {
            entry.setTransferID(bue.getTransferid().longValue());
        }
        if (bue.getServicechargetransactionlogid() != null) {
        	entry.setServiceChargeTransactionLogID(bue.getServicechargetransactionlogid().longValue());
        }
        if (StringUtils.isNotBlank(bue.getFirstname())) {
        	entry.setFirstName(bue.getFirstname());
        }
        if (StringUtils.isNotBlank(bue.getLastname())) {
        	entry.setLastName(bue.getLastname());
        }
        if (bue.getIsunregistered() != null) {
        	entry.setIsUnRegistered(bue.getIsunregistered() != 0);
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

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
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PendingTransactionsEntryDAO;
import com.mfino.dao.PendingTransactionsFileDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.PendingTransactionsEntryQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.PendingTransactionsEntry;
import com.mfino.domain.PendingTransactionsFile;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPendingTransactionsEntry;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PendingTransactionsEntryProcessor;

/**
 * 
 * @author Raju
 */
@Service("PendingTransactionsEntryProcessorImpl")
public class PendingTransactionsEntryProcessorImpl extends BaseFixProcessor implements PendingTransactionsEntryProcessor{

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	private void updateMessage(PendingTransactionsEntry pte,CMJSPendingTransactionsEntry.CGEntries entry) {
		
		entry.setID(pte.getID());
		if (pte.getAmount() != null) {
			entry.setAmount(pte.getAmount());
		}
		if (pte.getSourceMDN() != null) {
			entry.setSourceMDN(pte.getSourceMDN());
		}
		if (pte.getDestMDN() != null) {
			entry.setDestMDN(pte.getDestMDN());
		}
		if (pte.getCreateTime() != null) {
			entry.setCreateTime(pte.getCreateTime());
		}
		if (pte.getCreatedBy() != null) {
			entry.setCreatedBy(pte.getCreatedBy());
		}
		if (pte.getLastUpdateTime() != null) {
			entry.setLastUpdateTime(pte.getLastUpdateTime());
		}
		if (pte.getUpdatedBy() != null) {
			entry.setUpdatedBy(pte.getUpdatedBy());
		}
		if (pte.getTransactionsFileID() != null) {
			entry.setTransactionsFileID(pte.getTransactionsFileID());
		}
		if (pte.getLineNumber() != null) {
			entry.setLineNumber(pte.getLineNumber());
		}
		if (pte.getTransferID() != null) {
			entry.setTransferID(pte.getTransferID());
		}
		if (pte.getResolveFailureReason() != null) {
			entry.setResolveFailureReason(pte.getResolveFailureReason());
		}
		if (pte.getNotificationCode() != null) {
			entry.setNotificationCode(pte.getNotificationCode());
		}
		if (pte.getStatus() != null) {
			entry.setStatus(pte.getStatus());
			entry.setResolveStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PendingTransationsEntryStatus, null, pte.getStatus()));
		}
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
		CMJSPendingTransactionsEntry realMsg = (CMJSPendingTransactionsEntry) msg;
		if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			PendingTransactionsEntryDAO dao = DAOFactory.getInstance().getPendingTransactionsEntryDAO();
			PendingTransactionsEntryQuery query = new PendingTransactionsEntryQuery();
			query.setPendingTransactionsFileID(realMsg.getPendingTransactionsFileID());
			query.setStart(realMsg.getstart());
			query.setLimit(realMsg.getlimit());
			
			List<PendingTransactionsEntry> results = dao.get(query);			
            int flag =0;
			//chk each transactions
			for (int i = 0; i < results.size(); i++) {
				PendingTransactionsEntry pendingTransactionsEntry = results.get(i);
				if(pendingTransactionsEntry.getStatus().equals(CmFinoFIX.PendingTransationsEntryStatus_pending))
				{
					flag=1;
					PendingCommodityTransferDAO pendingCommodityTransferDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					PendingTransactionsFileDAO pendingTransactionsFileDAO = DAOFactory.getInstance().getPendingTransactionsFileDAO();
					PendingTransactionsFile file = pendingTransactionsFileDAO.getById(pendingTransactionsEntry.getTransactionsFileID());
					CommodityTransferQuery ctquery = new CommodityTransferQuery();
					ctquery.setId(pendingTransactionsEntry.getTransferID());
					ctquery.setCompany(file.getCompany());
					try {
					List<PendingCommodityTransfer> record = pendingCommodityTransferDAO.get(ctquery);
					if (record.size() == 0) {
						pendingTransactionsEntry.setStatus(CmFinoFIX.PendingTransationsEntryStatus_success);
						pendingTransactionsEntry.setResolveFailureReason(null);
						dao.save(pendingTransactionsEntry);
							}
					} catch (Exception error) {
						log.error("Error while process PendingTransactions entry: ", error);
					}
			  }
		}
			if(flag==1){
			 results = dao.get(query);		
			}
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				PendingTransactionsEntry s = results.get(i);
				CMJSPendingTransactionsEntry.CGEntries entry = new CMJSPendingTransactionsEntry.CGEntries();
				updateMessage(s, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
		}
		return realMsg;
	}
}

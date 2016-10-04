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
import com.mfino.domain.PendingTxnsEntry;
import com.mfino.domain.PendingTxnsFile;
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
	
	private void updateMessage(PendingTxnsEntry pte,CMJSPendingTransactionsEntry.CGEntries entry) {
		
		entry.setID(pte.getId().longValue());
		if (pte.getAmount() != null) {
			entry.setAmount(pte.getAmount());
		}
		if (pte.getSourcemdn() != null) {
			entry.setSourceMDN(pte.getSourcemdn());
		}
		if (pte.getDestmdn() != null) {
			entry.setDestMDN(pte.getDestmdn());
		}
		if (pte.getCreatetime() != null) {
			entry.setCreateTime(pte.getCreatetime());
		}
		if (pte.getCreatedby() != null) {
			entry.setCreatedBy(pte.getCreatedby());
		}
		if (pte.getLastupdatetime() != null) {
			entry.setLastUpdateTime(pte.getLastupdatetime());
		}
		if (pte.getUpdatedby() != null) {
			entry.setUpdatedBy(pte.getUpdatedby());
		}
		if (pte.getTransactionsfileid() != null) {
			entry.setTransactionsFileID(pte.getTransactionsfileid().longValue());
		}
		if ((Long)pte.getLinenumber() != null) {
			entry.setLineNumber(((Long)pte.getLinenumber()).intValue());
		}
		if (pte.getTransferid() != null) {
			entry.setTransferID(pte.getTransferid().longValue());
		}
		if (pte.getResolvefailurereason() != null) {
			entry.setResolveFailureReason(pte.getResolvefailurereason());
		}
		if (pte.getNotificationcode() != null) {
			entry.setNotificationCode(pte.getNotificationcode().intValue());
		}
		if ((Long)pte.getStatus() != null) {
			entry.setStatus(((Long)pte.getStatus()).intValue());
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
			
			List<PendingTxnsEntry> results = dao.get(query);			
            int flag =0;
			//chk each transactions
			for (int i = 0; i < results.size(); i++) {
				PendingTxnsEntry pendingTransactionsEntry = results.get(i);
				if(((Long)pendingTransactionsEntry.getStatus()).equals(CmFinoFIX.PendingTransationsEntryStatus_pending))
				{
					flag=1;
					PendingCommodityTransferDAO pendingCommodityTransferDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					PendingTransactionsFileDAO pendingTransactionsFileDAO = DAOFactory.getInstance().getPendingTransactionsFileDAO();
					PendingTxnsFile file = pendingTransactionsFileDAO.getById(pendingTransactionsEntry.getTransactionsfileid().longValue());
					CommodityTransferQuery ctquery = new CommodityTransferQuery();
					ctquery.setId(pendingTransactionsEntry.getTransferid().longValue());
					ctquery.setCompany(file.getCompany());
					try {
					List<PendingCommodityTransfer> record = pendingCommodityTransferDAO.get(ctquery);
					if (record.size() == 0) {
						pendingTransactionsEntry.setStatus(CmFinoFIX.PendingTransationsEntryStatus_success);
						pendingTransactionsEntry.setResolvefailurereason(null);
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
				PendingTxnsEntry s = results.get(i);
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

package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSLedgerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.Pocket;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSLedger;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.LedgerProcessor;

/**
 * @author Srikanth
 *
 */
@Service("LedgerProcessorImpl")
public class LedgerProcessorImpl extends BaseFixProcessor implements LedgerProcessor{
	
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	
	private void updateMessage(MFSLedger ledger, CMJSLedger.CGEntries e) {
		e.setID(ledger.getId().longValue());
		if(ledger.getCommoditytransferid() !=  null) {
			e.setCommodityTransferID(ledger.getCommoditytransferid().longValue());
		}
//		if(ledger.getSourcePocketID() != null) {
//			Pocket pocket = pocketDao.getById(ledger.getSourcePocketID());
//			e.setSourcePocketID(pocket.getID());
//			e.setSourcePocketTemplateDescription(pocket.getPocketTemplate().getDescription() + "(ID:" + pocket.getID() + ")");
//		}
//		if(ledger.getDestPocketID() != null) {
//			Pocket pocket = pocketDao.getById(ledger.getDestPocketID());
//			e.setDestPocketID(pocket.getID());
//			e.setDestPocketTemplateDescription(pocket.getPocketTemplate().getDescription() + "(ID:" + pocket.getID() + ")");
//		}		
//		if(ledger.getAmount() != null) {
//			e.setAmount(ledger.getAmount());
//		}
//		if(ledger.getSourceMDN() != null) {
//			e.setSourceMDN(ledger.getSourceMDN());
//		}
//		if(ledger.getDestMDN() != null) {
//			e.setDestMDN(ledger.getDestMDN());
//		}
		
		e.setAmount(ledger.getAmount());
		Pocket pocket = pocketDao.getById(ledger.getPocketid().longValue());
		if (pocket != null) {
			if (DAOConstants.DEBIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {
				e.setSourcePocketID(pocket.getId().longValue());
				e.setSourceMDN(pocket.getSubscriberMdn().getMdn());
			}
			else if (DAOConstants.CREDIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {
				e.setDestPocketID(pocket.getId().longValue());
				e.setDestMDN(pocket.getSubscriberMdn().getMdn());
			}
		}		
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSLedger realMsg = (CMJSLedger) msg;
		MFSLedgerDAO dao = DAOFactory.getInstance().getMFSLedgerDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			if(realMsg.getSctlId() != null){
				List<MFSLedger> lst = dao.getLedgerEntriesBySctlId(realMsg.getSctlId());
				if (CollectionUtils.isNotEmpty(lst)) {
					realMsg.allocateEntries(lst.size());
					int i=0;
					for (MFSLedger ledger: lst){
						CMJSLedger.CGEntries e = new CMJSLedger.CGEntries();
						updateMessage(ledger, e);
						realMsg.getEntries()[i] = e;
	        			i++;
	        		}
	        	}        	
	        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
	        	realMsg.settotal(lst.size());
			}			
		} 
		return realMsg;
	}
}

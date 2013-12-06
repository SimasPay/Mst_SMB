package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.MFSDenominationsDAO;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MFSDenominations;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMFSDenominations;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.MFSDenominationsProcessor;

/**
 * @author Satya
 *
 */

@Service("MFSDenominationsProcessorImpl")
public class MFSDenominationsProcessorImpl extends BaseFixProcessor implements MFSDenominationsProcessor{
	
	private void updateEntity(MFSDenominations md, CMJSMFSDenominations.CGEntries e) {
		MFSBillerPartnerDAO mbpDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
				
		if(e.getMFSID() != null){
			md.setMFSBillerPartnerByMFSID(mbpDAO.getById(e.getMFSID()));
		}
		if(e.getDenominationAmount() != null){
			md.setDenominationAmount(e.getDenominationAmount());
		}
		if(e.getDescription() != null){
			md.setDescription(e.getDescription());
		}
		if(e.getProductCode() != null){
			md.setProductCode(e.getProductCode());
		}		
	}
	
	private void updateMessage(MFSDenominations md, CMJSMFSDenominations.CGEntries e) {
		e.setID(md.getID());
		e.setMFSID(md.getMFSBillerPartnerByMFSID().getID());
		e.setDenominationAmount(md.getDenominationAmount());
		e.setDescription(md.getDescription());
		e.setProductCode(md.getProductCode());	
		e.setRecordVersion(md.getVersion());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSMFSDenominations realMsg = (CMJSMFSDenominations) msg;
		MFSDenominationsDAO dao = DAOFactory.getInstance().getMfsDenominationsDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			MFSDenominationsQuery query = new MFSDenominationsQuery();
			int i=0;
			
			if (realMsg.getMFSID() != null) {
				query.setMfsID(realMsg.getMFSID());
			}
			
			List<MFSDenominations> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (MFSDenominations md: lst){
					CMJSMFSDenominations.CGEntries e = new CMJSMFSDenominations.CGEntries();
					updateMessage(md, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSMFSDenominations.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSDenominations.CGEntries e: entries) {
				MFSDenominations md = new MFSDenominations();
				updateEntity(md, e);
				dao.save(md);
				updateMessage(md, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSMFSDenominations.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSDenominations.CGEntries e: entries) {
				MFSDenominations md = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(md.getVersion()))) {
        			handleStaleDataException();
        		}
        		updateEntity(md, e);
				dao.save(md);
        		updateMessage(md, e);
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSMFSDenominations.CGEntries[] entries = realMsg.getEntries();
			for (CMJSMFSDenominations.CGEntries e: entries) {
				dao.deleteById(e.getID());
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);			
		}
		return realMsg;
	}
}

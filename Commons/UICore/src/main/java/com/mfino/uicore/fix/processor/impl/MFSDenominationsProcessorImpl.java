package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSDenominationsDAO;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MfsDenominations;
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
	
	private void updateEntity(MfsDenominations md, CMJSMFSDenominations.CGEntries e) {		
		if(e.getMFSID() != null){
			md.setMfsid(e.getMFSID());
		}
		if(e.getDenominationAmount() != null){
			md.setDenominationamount(e.getDenominationAmount());
		}
		if(e.getDescription() != null){
			md.setDescription(e.getDescription());
		}
		if(e.getProductCode() != null){
			md.setProductcode(e.getProductCode());
		}		
	}
	
	private void updateMessage(MfsDenominations md, CMJSMFSDenominations.CGEntries e) {
		e.setID(md.getId().longValue());
		e.setMFSID(md.getMfsid().longValue());
		e.setDenominationAmount(md.getDenominationamount());
		e.setDescription(md.getDescription());
		e.setProductCode(md.getProductcode());	
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
			
			List<MfsDenominations> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (MfsDenominations md: lst){
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
				MfsDenominations md = new MfsDenominations();
				updateEntity(md, e);
				dao.save(md);
				updateMessage(md, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSMFSDenominations.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSDenominations.CGEntries e: entries) {
				MfsDenominations md = dao.getById(e.getID());
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

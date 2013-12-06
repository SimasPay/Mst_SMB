package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerDAO;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.dao.query.MFSBillerQuery;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMFSBiller;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.MFSBillerProcessor;

@Service("MFSBillerProcessorImpl")
public class MFSBillerProcessorImpl extends BaseFixProcessor implements MFSBillerProcessor{
	
	public void updateEntity(MFSBiller mb, CMJSMFSBiller.CGEntries e) {
		
		if (e.getMFSBillerName() != null) {
			mb.setMFSBillerName(e.getMFSBillerName());
		}
		
		if (e.getMFSBillerCode() != null) {
			mb.setMFSBillerCode(e.getMFSBillerCode());
		}
		
		if (e.getMFSBillerType() != null) {
			mb.setMFSBillerType(e.getMFSBillerType());
		}
	}
	
	public void updateMessage(MFSBiller mb, CMJSMFSBiller.CGEntries e) {
		e.setID(mb.getID());
		e.setMSPID(mb.getmFinoServiceProviderByMSPID().getID());
		e.setRecordVersion(mb.getVersion());
		e.setMFSBillerName(mb.getMFSBillerName());
		e.setMFSBillerCode(mb.getMFSBillerCode());
		e.setMFSBillerType(mb.getMFSBillerType());
		e.setCreatedBy(mb.getCreatedBy());
		e.setCreateTime(mb.getCreateTime());
		e.setUpdatedBy(mb.getUpdatedBy());
		e.setLastUpdateTime(mb.getLastUpdateTime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSMFSBiller realMsg = (CMJSMFSBiller)msg;
		MFSBillerDAO dao = DAOFactory.getInstance().getMFSBillerDAO();
		MFSBillerPartnerDAO mfsbpDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		
		if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSMFSBiller.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSBiller.CGEntries e: entries) {
				MFSBiller sc = dao.getById(e.getID());
				
				if (!(sc.getVersion().equals(e.getRecordVersion()))) {
					handleStaleDataException();
				}
				
				updateEntity(sc, e);
				dao.save(sc);
				updateMessage(sc, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			MFSBillerQuery query = new MFSBillerQuery();
			int i = 0;

			query.setBillerName(realMsg.getBillerNameSearch());
			query.setBillerCode(realMsg.getBillerCodeSearch());
			query.setBillerType(realMsg.getBillerTypeSearch());
			
        	List<MFSBiller> results = dao.get(query);
        	if (CollectionUtils.isNotEmpty(results)) {
        		realMsg.allocateEntries(results.size());
        		
        		for (MFSBiller sc: results) {
        			CMJSMFSBiller.CGEntries e = new CMJSMFSBiller.CGEntries();
        			updateMessage(sc, e);
        			realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
        	
		} if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSMFSBiller.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSBiller.CGEntries e: entries) {
				MFSBiller sc = new MFSBiller();
				updateEntity(sc, e);
				dao.save(sc);
				updateMessage(sc, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSMFSBiller.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSBiller.CGEntries e: entries) {
				
				MFSBillerPartnerQuery q = new MFSBillerPartnerQuery();
				q.setMfsBillerId(e.getID());
				List<MFSBillerPartner> lst = mfsbpDAO.get(q);
				for (MFSBillerPartner mfsbp: lst) {
					mfsbpDAO.deleteById(mfsbp.getID());
				}
				
				dao.deleteById(e.getID());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} 
		return realMsg;
	}

}

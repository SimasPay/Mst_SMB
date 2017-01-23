package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.SharePartnerDAO;
import com.mfino.dao.TransactionChargeDAO;
import com.mfino.dao.query.SharePartnerQuery;
import com.mfino.domain.SharePartner;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSharePartner;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SharePartnerProcessor;

@Service("SharePartnerProcessorImpl")
public class SharePartnerProcessorImpl extends BaseFixProcessor implements SharePartnerProcessor{
	private void updateEntity(SharePartner sp, CMJSSharePartner.CGEntries e) {
		TransactionChargeDAO tcDAO = DAOFactory.getInstance().getTransactionChargeDAO();
		PartnerDAO pDAO = DAOFactory.getInstance().getPartnerDAO();
		if (e.getTransactionChargeID() != null) {
			sp.setTransactionCharge(tcDAO.getById(e.getTransactionChargeID()));
		}
		if (e.getPartnerID() != null) {
			sp.setPartner(pDAO.getById(e.getPartnerID()));
		}
		if (e.getActualSharePercentage() != null) {
			sp.setActualsharepercentage(e.getActualSharePercentage());
			sp.setMinsharepercentage(e.getActualSharePercentage());
			sp.setMaxsharepercentage(e.getActualSharePercentage());
		}
		if (e.getShareHolderType() != null) {
			sp.setShareholdertype(e.getShareHolderType());
		}
		if (e.getShareType() != null) {
			sp.setSharetype(e.getShareType());
		}
	}
	
	private void updateMessage(SharePartner sp, CMJSSharePartner.CGEntries e) {
		e.setID(sp.getId().longValue());
		e.setMSPID(sp.getMfinoServiceProvider().getId().longValue());
		if (sp.getTransactionCharge() != null) {
			e.setTransactionChargeID(sp.getTransactionCharge().getId().longValue());
		}
		if (sp.getPartner() != null){
			e.setPartnerID(sp.getPartner().getId().longValue());
		}
		if (sp.getShareholdertype() != null){
			e.setShareHolderType(sp.getShareholdertype());
		}
		if (sp.getSharetype() != null){
			e.setShareType(sp.getSharetype());
		}
		e.setActualSharePercentage(sp.getActualsharepercentage());
		e.setMinSharePercentage(sp.getMinsharepercentage());
		e.setMaxSharePercentage(sp.getMaxsharepercentage());
		e.setRecordVersion(Integer.valueOf(Long.valueOf(sp.getVersion()).intValue()));
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSSharePartner realMsg = (CMJSSharePartner) msg;
		SharePartnerDAO dao = DAOFactory.getInstance().getSharePartnerDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			SharePartnerQuery query = new SharePartnerQuery();
			int i=0;
			
			if (realMsg.getTransactionChargeID() != null) {
				query.setTransactionChargeId(realMsg.getTransactionChargeID());
			}
			
			List<SharePartner> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (SharePartner sp: lst){
					CMJSSharePartner.CGEntries e = new CMJSSharePartner.CGEntries();
					updateMessage(sp, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSSharePartner.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSSharePartner.CGEntries e: entries) {
				SharePartner sp = new SharePartner();
				updateEntity(sp, e);
				dao.save(sp);
				updateMessage(sp, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSSharePartner.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSSharePartner.CGEntries e: entries) {
				SharePartner cp = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(cp.getVersion()))) {
        			handleStaleDataException();
        		}
        		
        		updateEntity(cp, e);
				dao.save(cp);
        		updateMessage(cp, e);
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSSharePartner.CGEntries[] entries = realMsg.getEntries();
			for (CMJSSharePartner.CGEntries e: entries) {
				dao.deleteById(e.getID());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);			
		}
		return realMsg;
	}
}

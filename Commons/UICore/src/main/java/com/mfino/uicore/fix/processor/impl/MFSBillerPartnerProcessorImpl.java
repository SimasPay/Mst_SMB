package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerDAO;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.MFSDenominationsDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.MFSDenominations;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMFSBillerPartner;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.MFSBillerPartnerProcessor;

@Service("MFSBillerPartnerProcessorImpl")
public class MFSBillerPartnerProcessorImpl extends BaseFixProcessor implements MFSBillerPartnerProcessor{
	
	private void updateMessage(MFSBillerPartner s, CMJSMFSBillerPartner.CGEntries e) {
		e.setID(s.getId().longValue());
		e.setMSPID(s.getMfinoServiceProvider().getId().longValue());
		e.setRecordVersion(((Long)s.getVersion()).intValue());
		if (s.getMfsBiller() != null) {
			e.setMFSBillerId(s.getMfsBiller().getId().longValue());
		}
		if (s.getPartner() != null) {
			e.setPartnerID(s.getPartner().getId().longValue());
			e.setPartnerName(s.getPartner().getTradename());
		}
		if(s.getBillerpartnertype() != null){
			e.setBillerPartnerType(s.getBillerpartnertype().intValue());
		}
		if(s.getIntegrationcode() != null){
			e.setIntegrationCode(s.getIntegrationcode());
		}
		if(s.getChargesincluded() != null){
			e.setChargesIncluded(s.getChargesincluded() != 0);
		}
		
		e.setPartnerBillerCode(s.getPartnerbillercode());
		e.setCreatedBy(s.getCreatedby());
		e.setCreateTime(s.getCreatetime());
		e.setUpdatedBy(s.getUpdatedby());
		e.setLastUpdateTime(s.getLastupdatetime());
	}
	
	private void updateEntity(MFSBillerPartner s, CMJSMFSBillerPartner.CGEntries e) {
		MFSBillerDAO mbDAO = DAOFactory.getInstance().getMFSBillerDAO();
		PartnerDAO pDAO = DAOFactory.getInstance().getPartnerDAO();
		if (e.getMFSBillerId() != null) {
			s.setMfsBiller(mbDAO.getById(e.getMFSBillerId()));
		}
		if (e.getPartnerID() != null) {
			s.setPartner(pDAO.getById(e.getPartnerID()));
		}
		if(e.getBillerPartnerType() != null){
			s.setBillerpartnertype(e.getBillerPartnerType().longValue());
		}
		if(e.getIntegrationCode() != null){
			s.setIntegrationcode(e.getIntegrationCode());
		}
		if (StringUtils.isNotBlank(e.getPartnerBillerCode())) {
			s.setPartnerbillercode(e.getPartnerBillerCode());
		}
		if(e.getChargesIncluded()!=null){
			s.setChargesincluded((short) (e.getChargesIncluded() ? 1:0));
		}

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSMFSBillerPartner realMsg = (CMJSMFSBillerPartner) msg;
		MFSBillerPartnerDAO dao = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		
		if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSMFSBillerPartner.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSBillerPartner.CGEntries e: entries) {
				MFSBillerPartner mbp = dao.getById(e.getID());
				
				if (!(e.getRecordVersion().equals(mbp.getVersion()))) {
					handleStaleDataException();
				}
				
				updateEntity(mbp, e);
				dao.save(mbp);
				updateMessage(mbp, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			MFSBillerPartnerQuery query = new MFSBillerPartnerQuery();
			int i = 0;

			query.setMfsBillerId(realMsg.getMFSBillerIdSearch());
        	query.setStart(realMsg.getstart());
        	query.setLimit(realMsg.getlimit());
        	
        	List<MFSBillerPartner> results = dao.get(query);
        	if (CollectionUtils.isNotEmpty(results)) {
        		realMsg.allocateEntries(results.size());
        		
        		for (MFSBillerPartner mbp: results) {
        			CMJSMFSBillerPartner.CGEntries e = new CMJSMFSBillerPartner.CGEntries();
        			updateMessage(mbp, e);
        			realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
        	
		} if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSMFSBillerPartner.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSBillerPartner.CGEntries e: entries) {
				MFSBillerPartner s = new MFSBillerPartner();
				updateEntity(s, e);
				dao.save(s);
				updateMessage(s, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSMFSBillerPartner.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSMFSBillerPartner.CGEntries e: entries) {
				deleteDenominations(e.getID());
				dao.deleteById(e.getID());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} 
		return realMsg;
	}

	private void deleteDenominations(Long id) {
		MFSDenominationsDAO dao = DAOFactory.getInstance().getMfsDenominationsDAO();
	    MFSDenominationsQuery query = new MFSDenominationsQuery();
	    query.setMfsID(id);
	    List<MFSDenominations> results = dao.get(query);
	    if (results.size() > 0) {
	    	dao.delete(results);
	    }
	}
}

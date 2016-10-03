package com.mfino.uicore.fix.processor.impl;


import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ClosedAccountSettlementMDNDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MoneyClearanceGravedDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.ClosedAccountSettlementMDNQuery;
import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.domain.ClosedAccountSettlementMDN;
import com.mfino.domain.MoneyClearanceGraved;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSClosedAccountSettlementMdn;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ClosedAccountSettlementMdnProcessor;
/**
 * @author Satya
 *
 */
@Service("ClosedAccountSettlementMdnProcessorImpl")
public class ClosedAccountSettlementMdnProcessorImpl extends BaseFixProcessor implements ClosedAccountSettlementMdnProcessor{
	private void updateEntity(ClosedAccountSettlementMDN casmdn, CMJSClosedAccountSettlementMdn.CGEntries e) {
		SubscriberMDNDAO smdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
				
		if(e.getMDNID() != null){
			casmdn.setSubscriberMdn(smdnDAO.getById(e.getMDNID()));
		}
		
		if(e.getToBankAccount() != null){
			casmdn.setTobankaccount((short) (e.getToBankAccount() ? 1 : 0));
		}
		
		if(e.getSettlementMDN() != null){
			casmdn.setSettlementmdn(e.getSettlementMDN());
		}
		
		if(e.getSettlementAccountNumber() != null){
			casmdn.setSettlementaccountnumber(e.getSettlementAccountNumber());
		}
		
		if(e.getApprovalState() != null){
			casmdn.setApprovalstate(e.getApprovalState().longValue());			
		}
		
		if(e.getApprovedOrRejectedBy() != null){
			casmdn.setApprovedorrejectedby(e.getApprovedOrRejectedBy());
		}
		
		if(e.getApproveOrRejectComment() != null){
			casmdn.setApproveorrejectcomment(e.getApproveOrRejectComment());
		}
		
		if(e.getApproveOrRejectTime() != null){
			casmdn.setApproveorrejecttime(e.getApproveOrRejectTime());
		}
	}
	
	private void updateMessage(ClosedAccountSettlementMDN casmdn, CMJSClosedAccountSettlementMdn.CGEntries e) {
		e.setID(casmdn.getId().longValue());
		e.setMDNID(casmdn.getSubscriberMdn().getId().longValue());
		e.setToBankAccount(casmdn.getTobankaccount() != 0);
		e.setSettlementMDN(casmdn.getSettlementmdn());
		e.setSettlementAccountNumber(casmdn.getSettlementaccountnumber());
		e.setApprovalState(casmdn.getApprovalstate().intValue());
		e.setApprovedOrRejectedBy(casmdn.getApprovedorrejectedby());
		e.setApproveOrRejectComment(casmdn.getApproveorrejectcomment());
		e.setApproveOrRejectTime(casmdn.getApproveorrejecttime());
		}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSClosedAccountSettlementMdn realMsg = (CMJSClosedAccountSettlementMdn) msg;
		ClosedAccountSettlementMDNDAO dao = DAOFactory.getInstance().getClosedAccountSettlementMdnDao();
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		MoneyClearanceGravedDAO mcgDAO = DAOFactory.getInstance().getMoneyClearanceGravedDao();
		MoneyClearanceGravedQuery mcgQuery = new MoneyClearanceGravedQuery();
		MoneyClearanceGraved mcg;
		if(realMsg.getMDNID() != null){
			mcgQuery.setMdnId(realMsg.getMDNID());
		}
		if(realMsg.getEntries() != null  && realMsg.getEntries()[0].getMDNID() != null){
			mcgQuery.setMdnId(realMsg.getEntries()[0].getMDNID());
		}
		
		List <MoneyClearanceGraved> mcgLst = mcgDAO.get(mcgQuery);
		if(mcgLst.size() > 0){
			mcg = mcgLst.get(0);
			if(((Long)mcg.getMcstatus()).intValue() == CmFinoFIX.MCStatus_MOVED_TO_NATIONAL_TREASURY){
				errorMsg.setErrorDescription(MessageText._("Money moved to NationalTreasury"));
				log.warn("Money Moved To National Treasury");
				return errorMsg;
			}
			if(((Long)mcg.getMcstatus()).intValue() == CmFinoFIX.MCStatus_REFUNDED){
				errorMsg.setErrorDescription(MessageText._("Money is already refunded"));
				log.warn("Money is already refunded");
				return errorMsg;
			}
		}else{
			errorMsg.setErrorDescription(MessageText._("No Balance in the Graved MDN to settle"));
			log.warn("No Balance in the Graved MDN to settle");
			return errorMsg;
		}
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())){
			ClosedAccountSettlementMDNQuery query = new ClosedAccountSettlementMDNQuery();
			int i=0;
			
			if(realMsg.getMDNID() != null){
				query.setMdnId(realMsg.getMDNID());
			}
			if(realMsg.getEntries() != null  && realMsg.getEntries()[0].getMDNID() != null){
				query.setMdnId(realMsg.getEntries()[0].getMDNID());
			}
			
			List<ClosedAccountSettlementMDN> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (ClosedAccountSettlementMDN casmdn: lst){
					CMJSClosedAccountSettlementMdn.CGEntries e = new CMJSClosedAccountSettlementMdn.CGEntries();
					updateMessage(casmdn, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
			
		}else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())){
			CMJSClosedAccountSettlementMdn.CGEntries[] entries = realMsg.getEntries();
			ClosedAccountSettlementMDNQuery query = new ClosedAccountSettlementMDNQuery();
			if(realMsg.getMDNID() != null){
				query.setMdnId(realMsg.getMDNID());
			}
			if(realMsg.getEntries() != null  && realMsg.getEntries()[0].getMDNID() != null){
				query.setMdnId(realMsg.getEntries()[0].getMDNID());
			}
			
			List<ClosedAccountSettlementMDN> lst = dao.get(query);
			for(CMJSClosedAccountSettlementMdn.CGEntries e : entries){
				ClosedAccountSettlementMDN cas;
				if(lst.size() > 0)
					cas = lst.get(0);
				else
				    cas = new ClosedAccountSettlementMDN();
        		updateEntity(cas, e);
				dao.save(cas);
        		updateMessage(cas, e);
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())){
			CMJSClosedAccountSettlementMdn.CGEntries[] entries = realMsg.getEntries();
			for(CMJSClosedAccountSettlementMdn.CGEntries e : entries){
				ClosedAccountSettlementMDN cas = dao.getById(e.getID());
        		updateEntity(cas, e);
				dao.save(cas);
        		updateMessage(cas, e);
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		}
		
		return realMsg;
	}
}
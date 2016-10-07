package com.mfino.uicore.fix.processor.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ScheduleTemplateDAO;
import com.mfino.dao.ServiceSettlementConfigDAO;
import com.mfino.dao.SettlementTemplateDAO;
import com.mfino.dao.query.SettlementTemplateQuery;
import com.mfino.domain.ServiceSettlementCfg;
import com.mfino.domain.SettlementTemplate;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSettlementTemplate;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SettlementTemplateProcessor;

@Service("SettlementTemplateProcessorImpl")
public class SettlementTemplateProcessorImpl extends BaseFixProcessor implements SettlementTemplateProcessor{
	private void updateEntity(SettlementTemplate st, CMJSSettlementTemplate.CGEntries e) {
		PartnerDAO pDAO = DAOFactory.getInstance().getPartnerDAO();
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		ScheduleTemplateDAO scheduleTemplateDAO = DAOFactory.getInstance().getScheduleTemplateDao();
		
		
		if (e.getSettlementName() != null) {
			if(!e.getSettlementName().equals(st.getSettlementname())){
        		log.info("Settlement ID:"+ st.getId()+" Settlement Name updated to "+e.getSettlementName()+" by user:"+getLoggedUserNameWithIP());
        	}
			st.setSettlementname(e.getSettlementName());
			
		}
		
		if(e.getScheduleTemplateID() != null){
			st.setScheduleTemplateByCutofftime(scheduleTemplateDAO.getById(e.getScheduleTemplateID()));
		}
		
		if (e.getSettlementPocket() != null) {
			if(st.getPocket()==null || !e.getSettlementPocket().equals(st.getPocket().getId())){
        		log.info("Settlement ID:"+ st.getId()+" Settlement Pocket updated to "+e.getSettlementPocket()+" by user:"+getLoggedUserNameWithIP());
        	}
			st.setPocket(pocketDAO.getById(e.getSettlementPocket()));
		}
		
		if (e.getPartnerID() != null) {
			if(st.getPartner()==null || !e.getPartnerID().equals(st.getPartner().getId())){
        		log.info("Settlement ID:"+ st.getId()+" Partner updated to "+e.getPartnerID()+" by user:"+getLoggedUserNameWithIP());
        	}
			st.setPartner(pDAO.getById(e.getPartnerID()));
		}
		
	}
	
	private void updateMessage(SettlementTemplate st, CMJSSettlementTemplate.CGEntries e) {
		e.setID(st.getId().longValue());
		e.setMSPID(st.getMfinoServiceProvider().getId().longValue());
		e.setSettlementName(st.getSettlementname());
		if(st.getScheduleTemplateByCutofftime() != null){
			e.setScheduleTemplateID(st.getScheduleTemplateByCutofftime().getId().longValue());
			e.setSettlementTypeText(st.getScheduleTemplateByCutofftime().getName());
		}
		e.setSettlementPocket(st.getPocket().getId().longValue());
		e.setCardPAN(st.getPocket().getCardpan());
		if (StringUtils.isNotBlank(st.getPocket().getCardpan()) && 
				st.getPocket().getPocketTemplate() != null) {
        	String cPan = st.getPocket().getCardpan();
        	if (cPan.length() > 6) {
        		cPan = cPan.substring(cPan.length()-6);
        	}
        	e.setPocketDispText(st.getPocket().getPocketTemplate().getDescription() + " - " + cPan);
		} else if (st.getPocket().getPocketTemplate() != null) {
			e.setPocketDispText(st.getPocket().getPocketTemplate().getDescription());
		}
		e.setPartnerID(st.getPartner().getId().longValue());
		e.setRecordVersion(Integer.valueOf(Long.valueOf(st.getVersion()).intValue()));
		e.setCreatedBy(st.getCreatedby());
		e.setCreateTime(st.getCreatetime());
		e.setUpdatedBy(st.getUpdatedby());
		e.setLastUpdateTime(st.getLastupdatetime());
		//e.setCutoffTime(st.getCutoffTime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSSettlementTemplate realMsg = (CMJSSettlementTemplate) msg;
		SettlementTemplateDAO dao  = DAOFactory.getInstance().getSettlementTemplateDAO();
		
		if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSSettlementTemplate.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSSettlementTemplate.CGEntries e: entries) {
				SettlementTemplate st = dao.getById(e.getID());
				
				if (!(e.getRecordVersion().equals(st.getVersion()))) {
					handleStaleDataException();
				}
				
				
				/*
				 * Whenever a settlement type is updated for a settlement template we need code for rescheduling, the settlement
				 * This block would get called only when settlement type is updated.
				 */
				if(e.getScheduleTemplateID() != null){
					log.info("SettlementTemplateProcessor :: schedule template type is changed for id "+st.getId());
					Set<ServiceSettlementCfg> serviceConfigs = st.getServiceSettlementCfgs();
					
					log.info("SettlementTemplateProcessor :: serviceConfigs "+serviceConfigs);
					
					if((serviceConfigs != null) && (serviceConfigs.size() > 0)){
						for(ServiceSettlementCfg serviceConfig : serviceConfigs){
							if(!serviceConfig.getSchedulerstatus().equals(CmFinoFIX.SchedulerStatus_TobeScheduled)){
								serviceConfig.setSchedulerstatus(CmFinoFIX.SchedulerStatus_Rescheduled.longValue());
								log.info("User:" + getLoggedUserNameWithIP() + ": Scheduler rescheduled for serviceConfig: " + serviceConfig.getId());
								ServiceSettlementConfigDAO scDao = DAOFactory.getInstance().getServiceSettlementConfigDAO();
								scDao.save(serviceConfig);
								log.info("serviceConfig: " + serviceConfig.getId() + " updated by user:" +getLoggedUserNameWithIP());
							}
						}
					}
				}
				log.info("Settlement Template: " + st.getId() + " edit completed by user:" +getLoggedUserNameWithIP());
				
				updateEntity(st, e);
				dao.save(st);
				updateMessage(st, e);		

			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			SettlementTemplateQuery query = new SettlementTemplateQuery();
			int i=0;
			
			query.setPartnerId(realMsg.getPartnerID());
			query.setStart(realMsg.getstart());
			query.setLimit(realMsg.getlimit());
			
			List<SettlementTemplate> lst = dao.get(query);
			
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				
				for(SettlementTemplate st: lst) {
					CMJSSettlementTemplate.CGEntries e = new CMJSSettlementTemplate.CGEntries();
					updateMessage(st, e);
					realMsg.getEntries()[i] = e;
					i++;
					log.info("Settlement Template:" + st.getSettlementname() + "ID:" + st.getId() +" details viewing completed by user:" + getLoggedUserNameWithIP());
				}
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSSettlementTemplate.CGEntries[] entries = realMsg.getEntries();
			for (CMJSSettlementTemplate.CGEntries e: entries) {
				SettlementTemplate st = new SettlementTemplate();
				updateEntity(st, e);
				dao.save(st);
				log.info("Settlement Template:" + st.getSettlementname() + "ID:" + st.getId() +" created by " + getLoggedUserNameWithIP());
				updateMessage(st, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
            
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			
		}
		
		return realMsg;
	}
}

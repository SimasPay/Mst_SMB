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
import com.mfino.domain.ServiceSettlementConfig;
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
			if(!e.getSettlementName().equals(st.getSettlementName())){
        		log.info("Settlement ID:"+ st.getID()+" Settlement Name updated to "+e.getSettlementName()+" by user:"+getLoggedUserNameWithIP());
        	}
			st.setSettlementName(e.getSettlementName());
			
		}
		
		if(e.getScheduleTemplateID() != null){
			st.setScheduleTemplate(scheduleTemplateDAO.getById(e.getScheduleTemplateID()));
		}
		if(e.getCutoffTime()!=null){
			st.setCutoffTime(e.getCutoffTime());
		}
		if (e.getSettlementPocket() != null) {
			if(st.getPocketBySettlementPocket()==null || !e.getSettlementPocket().equals(st.getPocketBySettlementPocket().getID())){
        		log.info("Settlement ID:"+ st.getID()+" Settlement Pocket updated to "+e.getSettlementPocket()+" by user:"+getLoggedUserNameWithIP());
        	}
			st.setPocketBySettlementPocket(pocketDAO.getById(e.getSettlementPocket()));
		}
		
		if (e.getPartnerID() != null) {
			if(st.getPartner()==null || !e.getPartnerID().equals(st.getPartner().getID())){
        		log.info("Settlement ID:"+ st.getID()+" Partner updated to "+e.getPartnerID()+" by user:"+getLoggedUserNameWithIP());
        	}
			st.setPartner(pDAO.getById(e.getPartnerID()));
		}
		
	}
	
	private void updateMessage(SettlementTemplate st, CMJSSettlementTemplate.CGEntries e) {
		e.setID(st.getID());
		e.setMSPID(st.getmFinoServiceProviderByMSPID().getID());
		e.setSettlementName(st.getSettlementName());
		if(st.getScheduleTemplate() != null){
			e.setScheduleTemplateID(st.getScheduleTemplate().getID());
			e.setSettlementTypeText(st.getScheduleTemplate().getName());
		}
		e.setSettlementPocket(st.getPocketBySettlementPocket().getID());
		e.setCardPAN(st.getPocketBySettlementPocket().getCardPAN());
		if (StringUtils.isNotBlank(st.getPocketBySettlementPocket().getCardPAN()) && 
				st.getPocketBySettlementPocket().getPocketTemplate() != null) {
        	String cPan = st.getPocketBySettlementPocket().getCardPAN();
        	if (cPan.length() > 6) {
        		cPan = cPan.substring(cPan.length()-6);
        	}
        	e.setPocketDispText(st.getPocketBySettlementPocket().getPocketTemplate().getDescription() + " - " + cPan);
		} else if (st.getPocketBySettlementPocket().getPocketTemplate() != null) {
			e.setPocketDispText(st.getPocketBySettlementPocket().getPocketTemplate().getDescription());
		}
		e.setPartnerID(st.getPartner().getID());
		e.setRecordVersion(st.getVersion());
		e.setCreatedBy(st.getCreatedBy());
		e.setCreateTime(st.getCreateTime());
		e.setUpdatedBy(st.getUpdatedBy());
		e.setLastUpdateTime(st.getLastUpdateTime());
		e.setCutoffTime(st.getCutoffTime());
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
					log.info("SettlementTemplateProcessor :: schedule template type is changed for id "+st.getID());
					Set<ServiceSettlementConfig> serviceConfigs = st.getServiceSettlementConfigFromSettlementTemplateID();
					
					log.info("SettlementTemplateProcessor :: serviceConfigs "+serviceConfigs);
					
					if((serviceConfigs != null) && (serviceConfigs.size() > 0)){
						for(ServiceSettlementConfig serviceConfig : serviceConfigs){
							if(!serviceConfig.getSchedulerStatus().equals(CmFinoFIX.SchedulerStatus_TobeScheduled)){
								serviceConfig.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Rescheduled);
								log.info("User:" + getLoggedUserNameWithIP() + ": Scheduler rescheduled for serviceConfig: " + serviceConfig.getID());
								ServiceSettlementConfigDAO scDao = DAOFactory.getInstance().getServiceSettlementConfigDAO();
								scDao.save(serviceConfig);
								log.info("serviceConfig: " + serviceConfig.getID() + " updated by user:" +getLoggedUserNameWithIP());
							}
						}
					}
				}
				log.info("Settlement Template: " + st.getID() + " edit completed by user:" +getLoggedUserNameWithIP());
				
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
					log.info("Settlement Template:" + st.getSettlementName() + "ID:" + st.getID() +" details viewing completed by user:" + getLoggedUserNameWithIP());
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
				log.info("Settlement Template:" + st.getSettlementName() + "ID:" + st.getID() +" created by " + getLoggedUserNameWithIP());
				updateMessage(st, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
            
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			
		}
		
		return realMsg;
	}
}

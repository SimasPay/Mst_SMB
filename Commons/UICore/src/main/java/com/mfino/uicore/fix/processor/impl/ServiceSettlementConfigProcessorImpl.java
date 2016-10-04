package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.ServiceSettlementConfigDAO;
import com.mfino.dao.SettlementTemplateDAO;
import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.ServiceSettlementCfg;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSServiceSettlementConfig;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServiceSettlementConfigProcessor;

@Service("ServiceSettlementConfigProcessorImpl")
public class ServiceSettlementConfigProcessorImpl extends BaseFixProcessor implements ServiceSettlementConfigProcessor{
	public void updateEntity(ServiceSettlementCfg sc, CMJSServiceSettlementConfig.CGEntries e) {
		SettlementTemplateDAO stDAO = DAOFactory.getInstance().getSettlementTemplateDAO();
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		
		if (e.getSettlementTemplateID() != null) {
			if(sc.getSettlementTemplate()==null || !e.getSettlementTemplateID().equals(sc.getSettlementTemplate().getId())){
        		log.info("Service Settlement Config:" + sc.getId() + " Settlement template updated to " + e.getSettlementTemplateID() + " by user:"+getLoggedUserNameWithIP());
        	}
			sc.setSettlementTemplate(stDAO.getById(e.getSettlementTemplateID()));
		}
		
		if (e.getPartnerServicesID() != null) {
			if(sc.getPartnerServices()==null || !e.getPartnerServicesID().equals(sc.getPartnerServices().getId())){
        		log.info("Service Settlement Config:" + sc.getId() + " Partner Services by Partnerr Service ID updated to " + e.getPartnerServicesID() + " by user:"+getLoggedUserNameWithIP());
        	}
			PartnerServices partnerServices = psDAO.getById(e.getPartnerServicesID());
			sc.setPartnerServices(partnerServices);
			sc.setPocketByCollectorPocket(partnerServices.getPocketByCollectorPocket());
		}
		
		if (e.getIsDefault() != null) {
			if(!e.getIsDefault().equals(sc.getIsdefault())){
        		log.info("Service Settlement Config:" + sc.getId() + " Is Default value updated to " + e.getIsDefault() + " by user:"+getLoggedUserNameWithIP());
        	}
			sc.setIsdefault((short) Boolean.compare(e.getIsDefault(), false));
		}
		
		if (e.getIsDefault() != null && e.getIsDefault().booleanValue()) {
			log.info("Service Settlement Config:" + sc.getId() + " start date and end date intialized to null");
			sc.setStartdate(null);
			sc.setEnddate(null);
		} else {
			if (e.getStartDate() != null) {
				if(!e.getStartDate().equals(sc.getStartdate())){
	        		log.info("Service Settlement Config:" + sc.getId() + " start date updated to " + e.getStartDate() + " by user:"+getLoggedUserNameWithIP());
	        	}
				sc.setStartdate(e.getStartDate());
			}
			
			if (e.getEndDate() != null) {
				if(!e.getEndDate().equals(sc.getEnddate())){
	        		log.info("Service Settlement Config:" + sc.getId() + " end date updated to " + e.getEndDate() + " by user:"+getLoggedUserNameWithIP());
	        	}
				sc.setEnddate(e.getEndDate());
			}			
		}
		if(e.getSchedulerStatus() != null){
			if(!e.getSchedulerStatus().equals(sc.getSchedulerstatus())){
        		log.info("Service Settlement Config:" + sc.getId() + " Scheduler status updated to " + e.getSchedulerStatus() + " by user:"+getLoggedUserNameWithIP());
        	}
			sc.setSchedulerstatus(e.getSchedulerStatus().longValue());
		}
	}
	
	public void updateMessage(ServiceSettlementCfg sc, CMJSServiceSettlementConfig.CGEntries e) {
		e.setID(sc.getId().longValue());
		e.setMSPID(sc.getMfinoServiceProvider().getId().longValue());
		e.setSettlementTemplateID(sc.getSettlementTemplate().getId().longValue());
		e.setSettlementName(sc.getSettlementTemplate().getSettlementname());
		e.setPartnerServicesID(sc.getPartnerServices().getId().longValue());
		e.setIsDefault(Boolean.valueOf(sc.getIsdefault().toString()));
		e.setStartDate(sc.getStartdate());
		e.setEndDate(sc.getEnddate());
		e.setRecordVersion(Integer.valueOf(Long.valueOf(sc.getVersion()).intValue()));
		e.setSchedulerStatus(sc.getSchedulerstatus().intValue());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSServiceSettlementConfig realMsg = (CMJSServiceSettlementConfig)msg;
		ServiceSettlementConfigDAO dao = DAOFactory.getInstance().getServiceSettlementConfigDAO();
		
		if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSServiceSettlementConfig.CGEntries[] entries = realMsg.getEntries();
			log.info("Service Settlement Config details edit requested by user:" + getLoggedUserNameWithIP());
			
			for (CMJSServiceSettlementConfig.CGEntries e: entries) {
				ServiceSettlementCfg sc = dao.getById(e.getID());
				
				if (!(Integer.valueOf(Long.valueOf(sc.getVersion()).intValue()).equals(e.getRecordVersion()))) {
					log.warn("Service settlement Configuration: " + sc.getId() + " stale data exception for user:" + getLoggedUserNameWithIP());
					handleStaleDataException();
				}
				e.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Rescheduled);
				updateEntity(sc, e);
				dao.save(sc);				
				updateMessage(sc, e);
				log.info("Service settlement Configuration: " + sc.getId() +" details edit completed by user:"+getLoggedUserNameWithIP());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			ServiceSettlementConfigQuery query = new ServiceSettlementConfigQuery();
			int i = 0;

			query.setPartnerServiceId(realMsg.getPartnerServicesID());
        	
        	List<ServiceSettlementCfg> results = dao.get(query);
        	if (CollectionUtils.isNotEmpty(results)) {
        		realMsg.allocateEntries(results.size());
        		
        		for (ServiceSettlementCfg sc: results) {
        			CMJSServiceSettlementConfig.CGEntries e = new CMJSServiceSettlementConfig.CGEntries();
        			updateMessage(sc, e);
        			realMsg.getEntries()[i] = e;
        			i++;
        			log.info("Service Settlement Config:" + sc.getId() + " details viewing completed by user:"+getLoggedUserNameWithIP());
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
        	
		} if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSServiceSettlementConfig.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSServiceSettlementConfig.CGEntries e: entries) {
				ServiceSettlementCfg sc = new ServiceSettlementCfg();
				updateEntity(sc, e);
				
				 /*
	             * we schedule only when default configurations are added
	             * Set the scheduler status to TobeScheduled, this is picked up by scheduler
	             */
	            if((Boolean.valueOf(sc.getIsdefault().toString()) != null) && (Boolean.valueOf(sc.getIsdefault().toString()))){
	            	sc.setSchedulerstatus(CmFinoFIX.SchedulerStatus_TobeScheduled.longValue());
	            }
				dao.save(sc);
				updateMessage(sc, e);
				
	            log.info("ServiceSettlementConfiguration created with id "+sc.getId());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSServiceSettlementConfig.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSServiceSettlementConfig.CGEntries e: entries) {
				dao.deleteById(e.getID());
				log.info("ServiceSettlementConfiguration with id "+ e.getID() + " deleted by user " + getLoggedUserNameWithIP());				
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} 
		return realMsg;
	}
}

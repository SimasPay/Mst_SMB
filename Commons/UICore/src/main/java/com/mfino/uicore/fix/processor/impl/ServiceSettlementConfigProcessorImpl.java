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
import com.mfino.domain.ServiceSettlementConfig;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSServiceSettlementConfig;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServiceSettlementConfigProcessor;

@Service("ServiceSettlementConfigProcessorImpl")
public class ServiceSettlementConfigProcessorImpl extends BaseFixProcessor implements ServiceSettlementConfigProcessor{
	public void updateEntity(ServiceSettlementConfig sc, CMJSServiceSettlementConfig.CGEntries e) {
		SettlementTemplateDAO stDAO = DAOFactory.getInstance().getSettlementTemplateDAO();
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		
		if (e.getSettlementTemplateID() != null) {
			if(sc.getSettlementTemplate()==null || !e.getSettlementTemplateID().equals(sc.getSettlementTemplate().getID())){
        		log.info("Service Settlement Config:" + sc.getID() + " Settlement template updated to " + e.getSettlementTemplateID() + " by user:"+getLoggedUserNameWithIP());
        	}
			sc.setSettlementTemplate(stDAO.getById(e.getSettlementTemplateID()));
		}
		
		if (e.getPartnerServicesID() != null) {
			if(sc.getPartnerServicesByPartnerServiceID()==null || !e.getPartnerServicesID().equals(sc.getPartnerServicesByPartnerServiceID().getID())){
        		log.info("Service Settlement Config:" + sc.getID() + " Partner Services by Partnerr Service ID updated to " + e.getPartnerServicesID() + " by user:"+getLoggedUserNameWithIP());
        	}
			sc.setPartnerServicesByPartnerServiceID(psDAO.getById(e.getPartnerServicesID()));
		}
		
		if (e.getIsDefault() != null) {
			if(!e.getIsDefault().equals(sc.getIsDefault())){
        		log.info("Service Settlement Config:" + sc.getID() + " Is Default value updated to " + e.getIsDefault() + " by user:"+getLoggedUserNameWithIP());
        	}
			sc.setIsDefault(e.getIsDefault());
		}
		
		if (e.getIsDefault() != null && e.getIsDefault().booleanValue()) {
			log.info("Service Settlement Config:" + sc.getID() + " start date and end date intialized to null");
			sc.setStartDate(null);
			sc.setEndDate(null);
		} else {
			if (e.getStartDate() != null) {
				if(!e.getStartDate().equals(sc.getStartDate())){
	        		log.info("Service Settlement Config:" + sc.getID() + " start date updated to " + e.getStartDate() + " by user:"+getLoggedUserNameWithIP());
	        	}
				sc.setStartDate(e.getStartDate());
			}
			
			if (e.getEndDate() != null) {
				if(!e.getEndDate().equals(sc.getEndDate())){
	        		log.info("Service Settlement Config:" + sc.getID() + " end date updated to " + e.getEndDate() + " by user:"+getLoggedUserNameWithIP());
	        	}
				sc.setEndDate(e.getEndDate());
			}			
		}
		if(e.getSchedulerStatus() != null){
			if(!e.getSchedulerStatus().equals(sc.getSchedulerStatus())){
        		log.info("Service Settlement Config:" + sc.getID() + " Scheduler status updated to " + e.getSchedulerStatus() + " by user:"+getLoggedUserNameWithIP());
        	}
			sc.setSchedulerStatus(e.getSchedulerStatus());
		}
	}
	
	public void updateMessage(ServiceSettlementConfig sc, CMJSServiceSettlementConfig.CGEntries e) {
		e.setID(sc.getID());
		e.setMSPID(sc.getmFinoServiceProviderByMSPID().getID());
		e.setSettlementTemplateID(sc.getSettlementTemplate().getID());
		e.setSettlementName(sc.getSettlementTemplate().getSettlementName());
		e.setPartnerServicesID(sc.getPartnerServicesByPartnerServiceID().getID());
		e.setIsDefault(sc.getIsDefault());
		e.setStartDate(sc.getStartDate());
		e.setEndDate(sc.getEndDate());
		e.setRecordVersion(sc.getVersion());
		e.setSchedulerStatus(sc.getSchedulerStatus());
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
				ServiceSettlementConfig sc = dao.getById(e.getID());
				
				if (!(sc.getVersion().equals(e.getRecordVersion()))) {
					log.warn("Service settlement Configuration: " + sc.getID() + " stale data exception for user:" + getLoggedUserNameWithIP());
					handleStaleDataException();
				}
				e.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Rescheduled);
				updateEntity(sc, e);
				dao.save(sc);				
				updateMessage(sc, e);
				log.info("Service settlement Configuration: " + sc.getID() +" details edit completed by user:"+getLoggedUserNameWithIP());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			ServiceSettlementConfigQuery query = new ServiceSettlementConfigQuery();
			int i = 0;

			query.setPartnerServiceId(realMsg.getPartnerServicesID());
        	
        	List<ServiceSettlementConfig> results = dao.get(query);
        	if (CollectionUtils.isNotEmpty(results)) {
        		realMsg.allocateEntries(results.size());
        		
        		for (ServiceSettlementConfig sc: results) {
        			CMJSServiceSettlementConfig.CGEntries e = new CMJSServiceSettlementConfig.CGEntries();
        			updateMessage(sc, e);
        			realMsg.getEntries()[i] = e;
        			i++;
        			log.info("Service Settlement Config:" + sc.getID() + " details viewing completed by user:"+getLoggedUserNameWithIP());
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
        	
		} if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSServiceSettlementConfig.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSServiceSettlementConfig.CGEntries e: entries) {
				ServiceSettlementConfig sc = new ServiceSettlementConfig();
				updateEntity(sc, e);
				
				 /*
	             * we schedule only when default configurations are added
	             * Set the scheduler status to TobeScheduled, this is picked up by scheduler
	             */
	            if((sc.getIsDefault() != null) && (sc.getIsDefault())){
	            	sc.setSchedulerStatus(CmFinoFIX.SchedulerStatus_TobeScheduled);
	            }
				dao.save(sc);
				updateMessage(sc, e);
				
	            log.info("ServiceSettlementConfiguration created with id "+sc.getID());
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

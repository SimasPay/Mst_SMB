package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Partner;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSServiceProvider;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServiceProviderProcessor;

@Service("ServiceProviderProcessorImpl")
public class ServiceProviderProcessorImpl extends BaseFixProcessor implements ServiceProviderProcessor{

	private PartnerDAO serviceProviderDao = DAOFactory.getInstance().getPartnerDAO();

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.debug("ServiceProviderProcessor :: process() method");
		
		CMJSServiceProvider realMsg = (CMJSServiceProvider) msg;
		
		log.debug("ServiceProviderProcessor :: process() method realMsg.getaction() "+realMsg.getaction());
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			/*
			 * Update action not required for now.
			 */
		}else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			/*
			 * Insert action not required for now
			 */
		}
		else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
			PartnerQuery serviceProviderQuery = new PartnerQuery();
			
			log.debug(" "+realMsg.getPartnerIDSearch());
//			log.debug(" "+realMsg.get);
			
			if ((realMsg.getPartnerTypeSearch() != null && CmFinoFIX.BusinessPartnerType_ServicePartner.equals(realMsg.getPartnerTypeSearch())) 
					&& StringUtils.isNotBlank(realMsg.getPartnerIDSearch())) {
				serviceProviderQuery.setId(Long.valueOf(realMsg.getPartnerIDSearch()));
			}
			if((null != realMsg.getServiceProviderNameSearch()) && !("".equals(realMsg.getServiceProviderNameSearch()))){
				serviceProviderQuery.setTradeName(realMsg.getServiceProviderNameSearch());
			}
						
//			serviceProviderQuery.setStart(realMsg.getstart());
//			serviceProviderQuery.setLimit(realMsg.getlimit());
			serviceProviderQuery.setPartnerType(CmFinoFIX.BusinessPartnerType_ServicePartner);
			
            List<Partner> results = serviceProviderDao.get(serviceProviderQuery);
            realMsg.allocateEntries(results.size());
			
            for (int i = 0; i < results.size(); i++) {
            	Partner objServiceProvider = results.get(i); 
                CMJSServiceProvider.CGEntries entry = new CMJSServiceProvider.CGEntries();

                updateMessage(objServiceProvider, entry);
                realMsg.getEntries()[i] = entry;
                log.info("Service Provider: " + objServiceProvider.getTradename() + " ID: " + objServiceProvider.getId() + " details viewing completed by user: " + getLoggedUserNameWithIP());
            }
            
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(serviceProviderQuery.getTotal());	
            log.debug("ServiceProviderProcessor::process#select total results "+serviceProviderQuery.getTotal());
		}
		
		return realMsg;
	}
	
    private void updateMessage(Partner serviceProvider,  CMJSServiceProvider.CGEntries entry) {

        
        if(serviceProvider!= null){
        	entry.setID(serviceProvider.getId().longValue());
//	        if(null != serviceProvider.getPartner()){
//	        	entry.setPartnerID(serviceProvider.getPartner().getID());
//	        }
	        if(serviceProvider.getMfinoServiceProvider() != null){
	        	entry.setMSPID(serviceProvider.getMfinoServiceProvider().getId().longValue());
	        }
	        if((null != serviceProvider.getTradename()) && !("".equals(serviceProvider.getTradename()))){
	        	entry.setServiceProviderName(serviceProvider.getTradename());
	        }
        }
    }
}

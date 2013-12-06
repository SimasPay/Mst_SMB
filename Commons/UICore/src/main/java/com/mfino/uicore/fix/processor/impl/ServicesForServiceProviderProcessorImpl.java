package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.domain.Service;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSServicesForServiceProvider;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServicesForServiceProviderProcessor;

@org.springframework.stereotype.Service("ServicesForServiceProviderProcessorImpl")
public class ServicesForServiceProviderProcessorImpl extends BaseFixProcessor implements ServicesForServiceProviderProcessor{
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSServicesForServiceProvider realMsg = (CMJSServicesForServiceProvider) msg;
		List<Service> lstServices = null;
		int size = 0;
    	int i = 0;
		
    	if (realMsg.getPartnerTypeSearch() != null && CmFinoFIX.BusinessPartnerType_ServicePartner.equals(realMsg.getPartnerTypeSearch())) {
    		ServiceDAO sDAO = DAOFactory.getInstance().getServiceDAO();
    		lstServices = sDAO.getAll();
    	} else {
    		if (realMsg.getServiceProviderID() != null) {
    			PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
    			lstServices = psDAO.getServices(realMsg.getServiceProviderID());
    		}
    	}
    	
    	if (CollectionUtils.isNotEmpty(lstServices)) {
    		size = lstServices.size();
    		realMsg.allocateEntries(size);
    		for (Service s: lstServices) {
    			CMJSServicesForServiceProvider.CGEntries e = new CMJSServicesForServiceProvider.CGEntries();
    			e.setServiceID(s.getID());
    			e.setServiceName(s.getDisplayName());
    			realMsg.getEntries()[i] = e;
    			i++;
    			log.info("Services For Service Provider message contains service: " + s.getDisplayName() + " for user: " + getLoggedUserNameWithIP());
    		}
    	}

    	realMsg.setsuccess(CmFinoFIX.Boolean_True);
    	realMsg.settotal(size);
		
		return realMsg;
	}
}

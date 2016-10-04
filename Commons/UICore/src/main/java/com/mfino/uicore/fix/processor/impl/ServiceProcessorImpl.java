package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.query.ServiceQuery;
import com.mfino.domain.Service;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServiceProcessor;

@org.springframework.stereotype.Service("ServiceProcessorImpl")
public class ServiceProcessorImpl extends BaseFixProcessor implements ServiceProcessor{
	 private void updateMessage(Service s, CMJSService.CGEntries e) {
		 e.setID(s.getId().longValue());
		 e.setMSPID(s.getMfinoServiceProvider().getId().longValue());
		 if (s.getDisplayname() != null) {
			 e.setServiceName(s.getDisplayname());
		 }
	 }

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSService realMsg = (CMJSService) msg;
		ServiceDAO dao = DAOFactory.getInstance().getServiceDAO();
		ServiceQuery query = new ServiceQuery();
		
		if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			log.info("ServiceProcessor :: select action");
			log.info("ServiceProcessor :: realMsg.getserviceProviderIdSearch() "+realMsg.getServiceProviderIDSearch());
			
			if(realMsg.getIDSearch()!=null){
				query.setId(realMsg.getIDSearch());
			}
			// *FindbugsChange*
        	// Previous -- if((null != realMsg.getServiceProviderIDSearch()) && !("".equals(realMsg.getServiceProviderIDSearch()))){
			// Null comparison is enough
			if((null != realMsg.getServiceProviderIDSearch()) ){
				query.setServiceProviderId(realMsg.getServiceProviderIDSearch());
			}
			if((null != realMsg.getPartnerIDSearch()) && !("".equals(realMsg.getPartnerIDSearch()))){
				query.setPartnerId(Long.valueOf(realMsg.getPartnerIDSearch()));
			}
			List<Service> results = dao.get(query);
			realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
            	Service s = results.get(i);

                CMJSService.CGEntries entry =   new CMJSService.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
            log.info("ServiceProcessor :: query.getTotal() "+query.getTotal());
		}else if(CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())){
			
		}else if(CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())){
			
		}
		return msg;
	}
}

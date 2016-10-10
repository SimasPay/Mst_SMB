/**
 * 
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IPMappingDAO;
import com.mfino.dao.IntegrationPartnerMappingDAO;
import com.mfino.dao.query.IPMappingQuery;
import com.mfino.domain.IpMapping;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSIPMapping;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.IPMappingprocessor;

/**
 * @author Amar
 *
 */
@Service("IPMappingprocessorImpl")
public class IPMappingprocessorImpl extends BaseFixProcessor implements IPMappingprocessor {
	
	private void updateEntity(IpMapping ipm, CMJSIPMapping.CGEntries e) {
		IntegrationPartnerMappingDAO integrationPartnerMappingDao = DAOFactory.getInstance().getIntegrationPartnerMappingDAO();
		if (e.getIntegrationID() != null) {
			ipm.setIntegrationPartnerMap(integrationPartnerMappingDao.getById(e.getIntegrationID()));
		}
		if (e.getIPAddress() != null) {
			ipm.setIpaddress(e.getIPAddress());
		}
	}
	
	private void updateMessage(IpMapping ipm, CMJSIPMapping.CGEntries e) {
		e.setID(ipm.getId().longValue());
		if(ipm.getIntegrationPartnerMap() != null)
		{
			e.setIntegrationID(ipm.getIntegrationPartnerMap().getId().longValue());
		}
		if (ipm.getIpaddress() != null) {
			e.setIPAddress(ipm.getIpaddress());
		}
		e.setRecordVersion(((Long)ipm.getVersion()).intValue());
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSIPMapping realMsg = (CMJSIPMapping) msg;
		IPMappingDAO dao = DAOFactory.getInstance().getIPMappingDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			IPMappingQuery query = new IPMappingQuery();
			int i=0;
			
			if (realMsg.getIntegrationID() != null) {
				query.setIntegrationID(realMsg.getIntegrationID());
			}
			
			List<IpMapping> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (IpMapping ipm: lst){
					CMJSIPMapping.CGEntries e = new CMJSIPMapping.CGEntries();
					updateMessage(ipm, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSIPMapping.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSIPMapping.CGEntries e: entries) {
				IpMapping ipm = new IpMapping();
				updateEntity(ipm, e);
				dao.save(ipm);
				updateMessage(ipm, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSIPMapping.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSIPMapping.CGEntries e: entries) {
				IpMapping ipm = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(ipm.getVersion()))) {
        			handleStaleDataException();
        		}
        		
        		updateEntity(ipm, e);
				dao.save(ipm);
        		updateMessage(ipm, e);
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSIPMapping.CGEntries[] entries = realMsg.getEntries();
			for (CMJSIPMapping.CGEntries e: entries) {
				dao.deleteById(e.getID());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);			
		}
		return realMsg;
	}
}

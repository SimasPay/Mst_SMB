package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.ChannelSessionManagement;
import com.mfino.fix.CmFinoFIX;

/**
 * @author sasidhar
 *
 */
public class ChannelSessionManagementDAO extends BaseDAO<ChannelSessionManagement>{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public ChannelSessionManagement getChannelSessionManagemebtByMDNID(Long mdnID){
		log.info("ChannelSessionManagement : get"+mdnID);
		  //Before Correcting errors reported by Findbugs:
			//		if((null == mdnID) || ("".equals(mdnID))) return null;
		
		  //After Correcting the errors reported by Findbugs
		if(null == mdnID) return null;
		
		Criteria channelSessionManagementCriteria = createCriteria();
		Criteria subscriberMdnCriteria = channelSessionManagementCriteria.createCriteria(CmFinoFIX.CRChannelSessionManagement.FieldName_SubscriberMDNByMDNID);
		subscriberMdnCriteria.add(Restrictions.eq(CmFinoFIX.CRSubscriberMDN.FieldName_RecordID, mdnID));
		
        List<ChannelSessionManagement> results = channelSessionManagementCriteria.list();
        
        if((results != null) && (results.size() > 0)) return results.get(0);
        
        return null;
	}
	
}

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.ChannelSessionMgmt;
import com.mfino.domain.SubscriberMdn;

/**
 * @author sasidhar
 *
 */
public class ChannelSessionManagementDAO extends BaseDAO<ChannelSessionMgmt>{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public ChannelSessionMgmt getChannelSessionManagemebtByMDNID(Long mdnID){
		log.info("ChannelSessionManagement : get"+mdnID);
		  //Before Correcting errors reported by Findbugs:
			//		if((null == mdnID) || ("".equals(mdnID))) return null;
		
		  //After Correcting the errors reported by Findbugs
		if(null == mdnID) return null;
		
		Criteria channelSessionManagementCriteria = createCriteria();
		Criteria subscriberMdnCriteria = channelSessionManagementCriteria.createCriteria(ChannelSessionMgmt.FieldName_SubscriberMDNByMDNID);
		subscriberMdnCriteria.add(Restrictions.eq(SubscriberMdn.FieldName_RecordID, mdnID));
		
        List<ChannelSessionMgmt> results = channelSessionManagementCriteria.list();
        
        if((results != null) && (results.size() > 0)) return results.get(0);
        
        return null;
	}
	
}

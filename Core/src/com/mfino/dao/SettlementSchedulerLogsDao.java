package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.SettlementSchedulerLogs;


/**
 * @author sasidhar
 *
 */
public class SettlementSchedulerLogsDao extends BaseDAO<SettlementSchedulerLogs> {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	public SettlementSchedulerLogs getByJobId(String jobId){
		log.info("SettlementSchedulerLogsDao :: getByJobId() BEGIN");
		
		SettlementSchedulerLogs ssLog = null;
		
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("QrtzJobId", jobId).ignoreCase());
        List<SettlementSchedulerLogs> results = criteria.list();
        
        if((results != null) && (results.size() > 0)){
        	ssLog = results.get(0);
        }
        
        log.info("SettlementSchedulerLogsDao :: getByJobId() END");
        return ssLog;
	}
	
	public SettlementSchedulerLogs getByPartnerServiceId(Long partnerServiceId){
		log.info("SettlementSchedulerLogsDao :: getByPartnerServiceId() BEGIN");
		
		SettlementSchedulerLogs ssLog = null;
		
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("PartnerServicesID", partnerServiceId));
        List<SettlementSchedulerLogs> results = criteria.list();
        
        if((results != null) && (results.size() > 0)){
        	ssLog = results.get(0);
        }
        
        log.info("SettlementSchedulerLogsDao :: getByPartnerServiceId() END");
        return ssLog;
	}
}

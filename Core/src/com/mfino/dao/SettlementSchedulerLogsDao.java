package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.SettlementScheduleLog;


/**
 * @author sasidhar
 *
 */
public class SettlementSchedulerLogsDao extends BaseDAO<SettlementScheduleLog> {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	public SettlementScheduleLog getByJobId(String jobId){
		log.info("SettlementSchedulerLogsDao :: getByJobId() BEGIN");
		
		SettlementScheduleLog ssLog = null;
		
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("QrtzJobId", jobId).ignoreCase());
        List<SettlementScheduleLog> results = criteria.list();
        
        if((results != null) && (results.size() > 0)){
        	ssLog = results.get(0);
        }
        
        log.info("SettlementSchedulerLogsDao :: getByJobId() END");
        return ssLog;
	}
	
	public SettlementScheduleLog getByPartnerServiceId(Long partnerServiceId){
		log.info("SettlementSchedulerLogsDao :: getByPartnerServiceId() BEGIN");
		
		SettlementScheduleLog ssLog = null;
		
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("PartnerServicesID", partnerServiceId));
        List<SettlementScheduleLog> results = criteria.list();
        
        if((results != null) && (results.size() > 0)){
        	ssLog = results.get(0);
        }
        
        log.info("SettlementSchedulerLogsDao :: getByPartnerServiceId() END");
        return ssLog;
	}
}

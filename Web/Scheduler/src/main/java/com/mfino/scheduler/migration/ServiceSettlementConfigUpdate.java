/**
 * 
 */
package com.mfino.scheduler.migration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.ServiceSettlementConfig;
import com.mfino.fix.CmFinoFIX;
import com.mfino.scheduler.service.impl.BaseServiceImpl;
import com.mfino.service.ServiceSettlementConfigCoreService;

/**
 * Updates the ServiceSettlementConfig table to set CollectorPocket IDs based on the partner services.
 * 
 * @author Chaitanya
 *
 */
@Service("ServiceSettlementConfigUpdate")
public class ServiceSettlementConfigUpdate extends BaseServiceImpl{
	
	@Autowired
	@Qualifier("ServiceSettlementConfigCoreServiceImpl")
	private ServiceSettlementConfigCoreService serviceSettlementConfigCoreService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void updateCollectorPocketNStatus(){
		log.info("updateCollectorPocketNStatus() BEGIN");
		
			ServiceSettlementConfigQuery configQuery = new ServiceSettlementConfigQuery();
			configQuery.setCollectorPocket(null);
			List<ServiceSettlementConfig> settlementConfigs = serviceSettlementConfigCoreService.get(configQuery);

			for(ServiceSettlementConfig config:settlementConfigs){
				PartnerServices partnerServices = config.getPartnerServicesByPartnerServiceID();
				config.setPocketByCollectorPocket(partnerServices.getPocketByCollectorPocket());
				if(config.getSchedulerStatus()==CmFinoFIX.SchedulerStatus_Scheduled){
					config.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Rescheduled);
					log.info("Set status to ReScheduled for ServiceSettlementConfig:"+config.getID());
				}else{
					config.setSchedulerStatus(CmFinoFIX.SchedulerStatus_TobeScheduled);
					log.info("Set status to TobeScheduled for ServiceSettlementConfig:"+config.getID());
				}
				serviceSettlementConfigCoreService.save(config);
			}
			log.info("updateCollectorPocketNStatus() END");
	}

}

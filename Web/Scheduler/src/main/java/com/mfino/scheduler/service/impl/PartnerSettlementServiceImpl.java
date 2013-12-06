/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.ServiceSettlementConfig;
import com.mfino.domain.SettlementSchedulerLogs;
import com.mfino.domain.SettlementTemplate;
import com.mfino.exceptions.CoreException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.scheduler.service.PartnerSettlementService;
import com.mfino.scheduler.settlement.PartnerSettlementJob;
import com.mfino.scheduler.settlement.SchedulerService;
import com.mfino.service.ServiceSettlementConfigCoreService;
import com.mfino.service.SettlementSchedulerLogsService;

/**
 * Default implementation of PartnerSettlementService
 * 
 * @author Chaitanya
 *
 */
@Service("PartnerSettlementServiceImpl")
public class PartnerSettlementServiceImpl implements PartnerSettlementService, InitializingBean{

	@Autowired
	@Qualifier("ServiceSettlementConfigCoreServiceImpl")
	private ServiceSettlementConfigCoreService serviceSettlementConfigCoreService;
	
	@Autowired
	@Qualifier("SettlementSchedulerLogsServiceImpl")
	private SettlementSchedulerLogsService settlementSchedulerLogsService;
	
	private Scheduler mfinoScheduler;

	private TimeZone timeZone;

	private Map<String,String> cronExpressions;
	// *FindbugsChange*
	// Previous -- protected static Logger log = LoggerFactory.getLogger(PartnerSettlementServiceImpl.class);
	private static final Logger log = LoggerFactory.getLogger(PartnerSettlementServiceImpl.class);
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	public PartnerSettlementServiceImpl() {
		
	}
	
	
	public void processSettlementJobs(){

		log.debug("Scheduling Settlements");

			ServiceSettlementConfigQuery query = new ServiceSettlementConfigQuery();

			query.setSchedulerStatus(CmFinoFIX.SchedulerStatus_TobeScheduled);
			List<ServiceSettlementConfig> settlementConfigs = serviceSettlementConfigCoreService.get(query);


			for(ServiceSettlementConfig sc : settlementConfigs){
				PartnerServices partnerServices = sc.getPartnerServicesByPartnerServiceID();
				if(partnerServices != null){

					try{
						scheduleSettlement(partnerServices);
					}catch(CoreException coreEx){
						//Error occurs only when there is a problem deleting an existing job
						//so no need to rollback transaction as there would not be any transaction initated by then
						log.error("Error occurred while scheduling Settlement for Partner Services:"+partnerServices.getID(), coreEx);
					}

				}
			}

			query.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Rescheduled);
			List<ServiceSettlementConfig> resettlementConfigs = serviceSettlementConfigCoreService.get(query);

			for(ServiceSettlementConfig sc : resettlementConfigs){
				PartnerServices partnerServices = sc.getPartnerServicesByPartnerServiceID();
				if(partnerServices != null){
					try{
						reScheduleSettlement(partnerServices);
					}catch(CoreException coreEx){
						//Error occurs only when there is a problem deleting an existing job
						//so no need to rollback transaction as there would not be any transaction initated by then
						log.error("Error occurred while rescheduling Settlement for Partner Services:"+partnerServices.getID(), coreEx);
					}
				}
			}
	}


	
	public SettlementSchedulerLogs scheduleSettlement(PartnerServices partnerService) throws CoreException{

		log.info("PartnerSettlementScheduler :: scheduleSettlement partnerService#id="+partnerService.getID());
		SettlementSchedulerLogs ssLog =  settlementSchedulerLogsService.getByPartnerServiceId(partnerService.getID());

		if(ssLog == null){
			ssLog = new SettlementSchedulerLogs();
		}
		else{
			/*
			 * Check if a settlement is already scheduled for this partner service.
			 * If so delete the job and do a new schedule.
			 */
			log.info("A job is already scheduled for PartnerService with id "+partnerService.getID());

			String jobId = ssLog.getQrtzJobId();

			if((null != jobId) && !("").equalsIgnoreCase(jobId)){
				try {
					mfinoScheduler.deleteJob(jobId, null);
				} catch (SchedulerException e) {
					throw new CoreException("Error while unscheduling job", e);
				}
			}
		}

		ssLog.setMSPID(1L);
		ssLog.setPartnerServicesID(partnerService.getID());
		ssLog.setIsScheduled(Boolean.FALSE);

		Set<ServiceSettlementConfig> settlementConfigs = partnerService.getServiceSettlementConfigFromPartnerServiceID();

		ServiceSettlementConfig settlementConfig = null;

		if((settlementConfigs != null) && (settlementConfigs.size() > 0)){
			/*
			 * Expectation is currently we are not considering date effectivity and there will be only one settlement configuration.
			 * This part needs to be modified when date effectivity comes into picture.
			 */
			for(ServiceSettlementConfig sc : settlementConfigs){
				if((sc.getIsDefault() != null) && (sc.getIsDefault())){
					settlementConfig = sc;
					break;
				}
			}
		}
		else{
			ssLog.setReasonText("No Settlement Config");
			settlementSchedulerLogsService.save(ssLog);
			log.error("A ServiceSettlementConfig does not exist for PartnerServiceID "+partnerService.getID());
			return ssLog;
		}

		ssLog.setServiceSettlementConfigID(settlementConfig.getID());

		SettlementTemplate settlementTemplate = settlementConfig.getSettlementTemplate();

		if(settlementTemplate == null){
			ssLog.setReasonText("No Settlement Template");
			settlementSchedulerLogsService.save(ssLog);
			log.error("A SettlementTemplate does not exist for PartnerServiceID "+partnerService.getID());
			return ssLog;
		}

		List<ServiceSettlementConfig> similarScheduledConfigs = getSimilarSettlementConfigs(partnerService);
		if(!similarScheduledConfigs.isEmpty())
		{
			//There should be only one config set. Check if it is same as the one being processed.
			ServiceSettlementConfig similarConfig = similarScheduledConfigs.get(0);
			//FIXME :: what to do in a scenario where multiple similar configs are scheduled.
			// *FindbugsChange*
        	// Previous -- if(similarConfig.getID() == (settlementConfig.getID())){
			if((similarConfig.getID().equals(settlementConfig.getID()))){
				log.info("Settlement Config is already scheduled:"+similarConfig.getID()+" for PartnerService:"+partnerService.getID());
				ssLog.setReasonText("Settlement already scheduled");
				settlementSchedulerLogsService.save(ssLog);
				return ssLog;
			}else{
				log.info("Similar Settlement Config is already scheduled:"+similarConfig.getID()+" for PartnerService:"+partnerService.getID());
				settlementConfig.setSchedulerStatus(CmFinoFIX.SchedulerStatus_SimilarConfigScheduled);
				settlementConfig.setSimilarConfigID(similarConfig.getID());
				serviceSettlementConfigCoreService.save(settlementConfig);
				
				ssLog.setReasonText("Similar Settlement already scheduled");
				settlementSchedulerLogsService.save(ssLog);
				return ssLog;
			}
			
		}
		
		
		String jobId = partnerService.getID().toString() + ":" + getUniqueJobId();
		CronTrigger trigger = new CronTrigger(jobId);
		trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		String cronExpression = null;
		try
		{
			//if(settlementTemplate.getSettlementType().equals(CmFinoFIX.SettlementType_Daily)){
			if(settlementTemplate.getScheduleTemplate().getCron()!=null){
				cronExpression = settlementTemplate.getScheduleTemplate().getCron();
				trigger.setCronExpression(cronExpression);
				trigger.setTimeZone(timeZone);
			}
			/*else if(settlementTemplate.getSettlementType().equals(CmFinoFIX.SettlementType_Weekly)){
				cronExpression = cronExpressions.get(SchedulerConstants.CRON_WEEKLY);
				trigger.setCronExpression(cronExpression);
				trigger.setTimeZone(timeZone);
			}
			else if(settlementTemplate.getSettlementType().equals(CmFinoFIX.SettlementType_Monthly)){
				cronExpression = cronExpressions.get(SchedulerConstants.CRON_MONTHLY);
				trigger.setCronExpression(cronExpression);
				trigger.setTimeZone(timeZone);
			}*/
			else{
				ssLog.setReasonText("Incompatible settlement type");
				settlementSchedulerLogsService.save(ssLog);
				log.error("Incompatible settlement type for PartnerServiceID "+partnerService.getID());
				return ssLog;
			}
		}
		catch(ParseException pe){
			ssLog.setReasonText("Invalid cron expression");
			settlementSchedulerLogsService.save(ssLog);
			log.error("PartnerSettlementScheduler :: Invalid Cron Expression", pe);
			return ssLog;
		}

		//create the job

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("partnerServiceId", partnerService.getID());
		jobDataMap.put("jobId", jobId);

		JobDetail job = new JobDetail(jobId, PartnerSettlementJob.class);
		job.setJobDataMap(jobDataMap);
		try {
			mfinoScheduler.scheduleJob(job, trigger);

			ssLog.setNextSettle(new Timestamp(trigger.getNextFireTime()));
			ssLog.setQrtzJobId(jobId);
			ssLog.setReasonText("Job Scheduled");
			ssLog.setIsScheduled(Boolean.TRUE);
			settlementSchedulerLogsService.save(ssLog);

			settlementConfig.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Scheduled);
			serviceSettlementConfigCoreService.save(settlementConfig);

		} catch (SchedulerException e) {
			String errorMsg = e.getMessage();
			ssLog.setReasonText("Job Could not be Scheduled");
			if(errorMsg!=null){
				ssLog.setReasonText(errorMsg.substring(0,240));
			}
			ssLog.setIsScheduled(Boolean.FALSE);
			settlementSchedulerLogsService.save(ssLog);
			log.error("Error while scheduling job for partner service id "+partnerService.getID(), e);	
			return ssLog;
		}
		return ssLog;
	}

	
	public  SettlementSchedulerLogs reScheduleSettlement(PartnerServices partnerService) throws CoreException{
		log.info("partnerSettlementScheduler :: Reschedule Settlement Called");
		SettlementSchedulerLogs ssl =  settlementSchedulerLogsService.getByPartnerServiceId(partnerService.getID());
		if(ssl!=null){
			String jobId = ssl.getQrtzJobId();
			if((null != jobId) && !("").equalsIgnoreCase(jobId)){
				try {
					mfinoScheduler.deleteJob(jobId,null);
				} catch (SchedulerException e) {
					throw new CoreException("Error while unscheduling job", e);
				}
			}
		}
		return scheduleSettlement(partnerService); //again schedule new job and triger for the same partner service
	}

	private String getUniqueJobId(){
		UUID idOne = UUID.randomUUID();
		return idOne.toString();
	}

	protected List<ServiceSettlementConfig> getSimilarSettlementConfigs(PartnerServices partnerServices){

		ServiceSettlementConfigQuery query = new ServiceSettlementConfigQuery();

		query.setCollectorPocket(partnerServices.getPocketByCollectorPocket());
		query.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Scheduled);
		List<ServiceSettlementConfig> settlementConfigs = serviceSettlementConfigCoreService.get(query);		
		return settlementConfigs;
	}

	@Override
	protected void finalize() throws Throwable {
		SchedulerService.getInstance().shutdown();
		super.finalize();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("In PartnerSettlementService afterPropertiesSet");
		SchedulerService schedService = SchedulerService.getInstance();
		mfinoScheduler = schedService.getMfinoScheduler();
		
		schedService.start(schedService.getTxManager());
		
		timeZone = schedService.getTimeZone();
		if(timeZone == null){
			timeZone = TimeZone.getDefault();
		}

		cronExpressions = schedService.getCronExpressions();

		log.info("Cron Expressions Map from ApplicationContext "+cronExpressions);
	}
}

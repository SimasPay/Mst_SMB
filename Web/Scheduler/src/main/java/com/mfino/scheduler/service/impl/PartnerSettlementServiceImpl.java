/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
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
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceSettlementCfg;
import com.mfino.domain.SettlementScheduleLog;
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
			List<ServiceSettlementCfg> settlementConfigs = serviceSettlementConfigCoreService.get(query);


			for(ServiceSettlementCfg sc : settlementConfigs){
				PartnerServices partnerServices = sc.getPartnerServices();
				if(partnerServices != null){

					try{
						scheduleSettlement(partnerServices);
					}catch(CoreException coreEx){
						//Error occurs only when there is a problem deleting an existing job
						//so no need to rollback transaction as there would not be any transaction initated by then
						log.error("Error occurred while scheduling Settlement for Partner Services:"+partnerServices.getId(), coreEx);
					}

				}
			}

			query.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Rescheduled);
			List<ServiceSettlementCfg> resettlementConfigs = serviceSettlementConfigCoreService.get(query);

			for(ServiceSettlementCfg sc : resettlementConfigs){
				PartnerServices partnerServices = sc.getPartnerServices();
				if(partnerServices != null){
					try{
						reScheduleSettlement(partnerServices);
					}catch(CoreException coreEx){
						//Error occurs only when there is a problem deleting an existing job
						//so no need to rollback transaction as there would not be any transaction initated by then
						log.error("Error occurred while rescheduling Settlement for Partner Services:"+partnerServices.getId(), coreEx);
					}
				}
			}
	}


	
	public SettlementScheduleLog scheduleSettlement(PartnerServices partnerService) throws CoreException{

		log.info("PartnerSettlementScheduler :: scheduleSettlement partnerService#id="+partnerService.getId());
		SettlementScheduleLog ssLog =  settlementSchedulerLogsService.getByPartnerServiceId(partnerService.getId().longValue());

		if(ssLog == null){
			ssLog = new SettlementScheduleLog();
		}
		else{
			/*
			 * Check if a settlement is already scheduled for this partner service.
			 * If so delete the job and do a new schedule.
			 */
			log.info("A job is already scheduled for PartnerService with id "+partnerService.getId());

			String jobId = ssLog.getQrtzjobid();

			if((null != jobId) && !("").equalsIgnoreCase(jobId)){
				try {
					mfinoScheduler.deleteJob(jobId, null);
				} catch (SchedulerException e) {
					throw new CoreException("Error while unscheduling job", e);
				}
			}
		}

		ssLog.setMspid(BigDecimal.valueOf(1L));
		ssLog.setPartnerservicesid(partnerService.getId());
		ssLog.setIsscheduled((short)0);

		Set<ServiceSettlementCfg> settlementConfigs = partnerService.getServiceSettlementCfgs();

		ServiceSettlementCfg settlementConfig = null;

		if((settlementConfigs != null) && (settlementConfigs.size() > 0)){
			/*
			 * Expectation is currently we are not considering date effectivity and there will be only one settlement configuration.
			 * This part needs to be modified when date effectivity comes into picture.
			 */
			for(ServiceSettlementCfg sc : settlementConfigs){
				if((sc.getIsdefault() != null) && (sc.getIsdefault() != 0)){
					settlementConfig = sc;
					break;
				}
			}
		}
		else{
			ssLog.setReasontext("No Settlement Config");
			settlementSchedulerLogsService.save(ssLog);
			log.error("A ServiceSettlementConfig does not exist for PartnerServiceID "+partnerService.getId());
			return ssLog;
		}

		ssLog.setServicesettlementconfigid(settlementConfig.getId());

		SettlementTemplate settlementTemplate = settlementConfig.getSettlementTemplate();

		if(settlementTemplate == null){
			ssLog.setReasontext("No Settlement Template");
			settlementSchedulerLogsService.save(ssLog);
			log.error("A SettlementTemplate does not exist for PartnerServiceID "+partnerService.getId());
			return ssLog;
		}

		List<ServiceSettlementCfg> similarScheduledConfigs = getSimilarSettlementConfigs(partnerService);
		if(!similarScheduledConfigs.isEmpty())
		{
			//There should be only one config set. Check if it is same as the one being processed.
			ServiceSettlementCfg similarConfig = similarScheduledConfigs.get(0);
			//FIXME :: what to do in a scenario where multiple similar configs are scheduled.
			// *FindbugsChange*
        	// Previous -- if(similarConfig.getID() == (settlementConfig.getID())){
			if((similarConfig.getId().equals(settlementConfig.getId()))){
				log.info("Settlement Config is already scheduled:"+similarConfig.getId()+" for PartnerService:"+partnerService.getId());
				ssLog.setReasontext("Settlement already scheduled");
				settlementSchedulerLogsService.save(ssLog);
				return ssLog;
			}else{
				log.info("Similar Settlement Config is already scheduled:"+similarConfig.getId()+" for PartnerService:"+partnerService.getId());
				settlementConfig.setSchedulerstatus(Long.valueOf(CmFinoFIX.SchedulerStatus_SimilarConfigScheduled));
				settlementConfig.setSimilarconfigid(similarConfig.getId());
				serviceSettlementConfigCoreService.save(settlementConfig);
				
				ssLog.setReasontext("Similar Settlement already scheduled");
				settlementSchedulerLogsService.save(ssLog);
				return ssLog;
			}
			
		}
		
		
		String jobId = partnerService.getId().toString() + ":" + getUniqueJobId();
		CronTrigger trigger = new CronTrigger(jobId);
		trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		String cronExpression = null;
		try
		{
			//if(settlementTemplate.getSettlementType().equals(CmFinoFIX.SettlementType_Daily)){
			if(settlementTemplate.getScheduleTemplateByCutofftime().getCron()!=null){
				cronExpression = settlementTemplate.getScheduleTemplateByCutofftime().getCron();
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
				ssLog.setReasontext("Incompatible settlement type");
				settlementSchedulerLogsService.save(ssLog);
				log.error("Incompatible settlement type for PartnerServiceID "+partnerService.getId());
				return ssLog;
			}
		}
		catch(ParseException pe){
			ssLog.setReasontext("Invalid cron expression");
			settlementSchedulerLogsService.save(ssLog);
			log.error("PartnerSettlementScheduler :: Invalid Cron Expression", pe);
			return ssLog;
		}

		//create the job

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("partnerServiceId", partnerService.getId());
		jobDataMap.put("jobId", jobId);

		JobDetail job = new JobDetail(jobId, PartnerSettlementJob.class);
		job.setJobDataMap(jobDataMap);
		try {
			mfinoScheduler.scheduleJob(job, trigger);

			ssLog.setNextsettle(new Timestamp(trigger.getNextFireTime()));
			ssLog.setQrtzjobid(jobId);
			ssLog.setReasontext("Job Scheduled");
			ssLog.setIsscheduled((short) 1);
			settlementSchedulerLogsService.save(ssLog);

			settlementConfig.setSchedulerstatus(Long.valueOf(CmFinoFIX.SchedulerStatus_Scheduled));
			serviceSettlementConfigCoreService.save(settlementConfig);

		} catch (SchedulerException e) {
			String errorMsg = e.getMessage();
			ssLog.setReasontext("Job Could not be Scheduled");
			if(errorMsg!=null){
				ssLog.setReasontext(errorMsg.substring(0,240));
			}
			ssLog.setIsscheduled((short) 0);
			settlementSchedulerLogsService.save(ssLog);
			log.error("Error while scheduling job for partner service id "+partnerService.getId(), e);	
			return ssLog;
		}
		return ssLog;
	}

	
	public  SettlementScheduleLog reScheduleSettlement(PartnerServices partnerService) throws CoreException{
		log.info("partnerSettlementScheduler :: Reschedule Settlement Called");
		SettlementScheduleLog ssl =  settlementSchedulerLogsService.getByPartnerServiceId(partnerService.getId().longValue());
		if(ssl!=null){
			String jobId = ssl.getQrtzjobid();
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

	protected List<ServiceSettlementCfg> getSimilarSettlementConfigs(PartnerServices partnerServices){

		ServiceSettlementConfigQuery query = new ServiceSettlementConfigQuery();
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Pocket pocket = pocketDAO.getById(partnerServices.getCollectorpocket().longValue());
		query.setCollectorPocket(pocket);
		query.setSchedulerStatus(CmFinoFIX.SchedulerStatus_Scheduled);
		List<ServiceSettlementCfg> settlementConfigs = serviceSettlementConfigCoreService.get(query);		
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

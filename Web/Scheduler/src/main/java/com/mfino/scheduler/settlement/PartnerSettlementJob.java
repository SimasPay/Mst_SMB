package com.mfino.scheduler.settlement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.domain.SettlementScheduleLog;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.impl.SettlementSchedulerLogsServiceImpl;

/**
 * @author sasidhar
 *
 */

public class PartnerSettlementJob implements Job{
	
	//private static HibernateSessionHolder hibernateSessionHolder = null;
	
	//private static SessionFactory sessionFactory;
	
	private HibernateSessionHolder hibernateSessionHolder = null;
	
	private SessionFactory sessionFactory;
	private HibernateTransactionManager htm;
	
	// *FindbugsChange*
	// Previous --	protected static Logger log = LoggerFactory.getLogger(PartnerSettlementJob.class);
	private static final Logger log = LoggerFactory.getLogger(PartnerSettlementJob.class);
	
	/*static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}*/
	
	public PartnerSettlementJob(){
		
	}
	
	private SettlementSchedulerLogsServiceImpl settlementSchedulerLogsServiceImpl =SettlementSchedulerLogsServiceImpl.getInstance();
	
	private SettlementHandlerImpl settlementHandler = SettlementHandlerImpl.getInstance();
	
	@Override
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("PartnerSettlementJob :: execute() BEGIN");
		
		SchedulerContext schedContext;
		try {
			schedContext = context.getScheduler().getContext();
		
			htm = (HibernateTransactionManager)schedContext.get(HibernateTransactionManager.class.getName());
			sessionFactory = htm.getSessionFactory();
			Session session = SessionFactoryUtils.getSession(sessionFactory, true);
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			
			Long partnerServiceId = (Long)dataMap.get("partnerServiceId");
			String jobId = (String)dataMap.get("jobId");
			
			log.info("PartnerSettlementJob execution for partnerServiceId="+partnerServiceId + ", and jobID="+jobId);

			
			synchronized (PartnerSettlementJob.class) {
				settlementHandler.doSettlement(partnerServiceId);	
			}
			
			SettlementScheduleLog ssLog = settlementSchedulerLogsServiceImpl.getByJobId(jobId);
			if(ssLog != null){
				//ssLog.setLastSettled(new Timestamp(new Date()));
				if(context.getPreviousFireTime() != null){
					ssLog.setLastsettled(new Timestamp(context.getPreviousFireTime()));
				}
				ssLog.setNextsettle(new Timestamp(context.getNextFireTime()));
				settlementSchedulerLogsServiceImpl.save(ssLog);
			}
			else{
				log.error("PartnerSettlementJob :: could not find a SettlementSchedulerLogs for jobId="+jobId + " and PartnerServicesId="+partnerServiceId);
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			  SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
		log.info("PartnerSettlementJob :: execute() END");
	}
}

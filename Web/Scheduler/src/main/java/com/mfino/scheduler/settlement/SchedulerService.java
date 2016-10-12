package com.mfino.scheduler.settlement;

import java.util.Map;
import java.util.TimeZone;

import org.hibernate.SessionFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import com.mfino.hibernate.session.HibernateSessionHolder;

/**
 * @author sasidhar
 * Provides Scheduler instance, which can be used to schedule jobs.
 * Scheduler should not be shutdown while running in container.
 */
public class SchedulerService implements DisposableBean{
	
	private static Logger log = LoggerFactory.getLogger(SchedulerService.class);
	
	private Scheduler mfinoScheduler;
	
	private static SchedulerService schedulerService;
	
	private TimeZone timeZone;
	
	private Map<String,String> cronExpressions;
	
	private SessionFactory sessionFactory;
	
	private HibernateTransactionManager txManager;
	
	private SchedulerService(){
		
	}

	public static SchedulerService getInstance(){
		if(schedulerService == null){
			schedulerService = new SchedulerService();

			try {
				schedulerService.mfinoScheduler = StdSchedulerFactory.getDefaultScheduler();	
				/*SchedulerContext context = schedulerService.mfinoScheduler.getContext();
				context.put(HibernateSessionHolder.class, hibernateSessionHolder);*/
			} catch (SchedulerException schedErr) {
				log.error("Unable to start scheduler:", schedErr);
			}
		}
		
		return schedulerService;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public Scheduler getMfinoScheduler() {
		return mfinoScheduler;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public Map<String, String> getCronExpressions() {
		return cronExpressions;
	}

	public void setCronExpressions(Map<String, String> cronExpressions) {
		this.cronExpressions = cronExpressions;
	}
	
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}
	public void start(HibernateTransactionManager txManager){
		try {
		if(mfinoScheduler!=null && !mfinoScheduler.isStarted()){
			SchedulerContext context = mfinoScheduler.getContext();
			context.put(HibernateTransactionManager.class.getName(), txManager);
			mfinoScheduler.start();
		}
		} catch (SchedulerException schedErr) {
			log.error("Exception during start of scheduler", schedErr);
		}
	}
	
	public void shutdown(){
		try {
			if(mfinoScheduler!=null && !mfinoScheduler.isShutdown()){
				mfinoScheduler.shutdown(true);
			}
		} catch (SchedulerException schedErr) {
			log.error("Exception during shutdown of scheduler", schedErr);
		}
		mfinoScheduler = null;
	}

	@Override
	public void destroy() throws Exception {
		if(!(mfinoScheduler.isShutdown())){
			mfinoScheduler.shutdown(true);
			Thread.sleep(10000);
		}
	}
	
	
	
/*	public static void main(String[] args) throws ParseException, SchedulerException, ClassNotFoundException, NoSuchMethodException {
//		ApplicationContext context = new FileSystemXmlApplicationContext("e:/programs/mfino-p2g4/Web/AdminApplication/web/WEB-INF/applicationContext.xml");
		SchedulerService schedulerService = SchedulerService.getInstance();
//		schedulerService.scheduleDynamicJob();
		
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        schedulerService.setMfinoScheduler(scheduler);
        scheduler.start();
        
        schedulerService.scheduleDynamicJob();
        
//        scheduler.shutdown();
	}*/
}

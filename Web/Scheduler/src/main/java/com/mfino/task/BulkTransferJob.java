/**
 * 
 */
package com.mfino.task;

import java.io.IOException;
import java.math.BigDecimal;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.BulkUploadService;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mfino.service.PocketService;
import com.mfino.transactionapi.service.BulkTransferService;

/**
 * @author Bala Sunku
 *
 */
public class BulkTransferJob implements Job {

	private HibernateSessionHolder hibernateSessionHolder = null;
	private SessionFactory sessionFactory;
	private Session session = null;
	private Logger log = LoggerFactory.getLogger(BulkTransferJob.class);
	
	
	private PocketService pocketService;

	public HibernateSessionHolder getHibernateSessionHolder() {
		return hibernateSessionHolder;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	private BulkTransferService btService;
	
	
	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	public BulkTransferService getBtService() {
		return btService;
	}

	public void setBtService(BulkTransferService btService) {
		this.btService = btService;
	}

	public BulkUploadService getBulkuploadService() {
		return bulkuploadService;
	}

	public void setBulkuploadService(BulkUploadService bulkuploadService) {
		this.bulkuploadService = bulkuploadService;
	}

	private BulkUploadService bulkuploadService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("BEGIN bulkTransferJob::execute");

		try {
			SchedulerContext schedContext = context.getScheduler().getContext();
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			Long bulkTransferId = (Long)jobDataMap.get("bulkTransferId");
			String jobId = (String)jobDataMap.get("jobId");
			log.info("BEGIN Executing the Bulk Transfer " + bulkTransferId + " with Job id -->" + jobId );
			BulkUpload bulkUpload = bulkuploadService.getById(bulkTransferId);
			doBulkTransfer(bulkUpload);
		} 
		catch (SchedulerException e) {
			log.error("Could not execute the Bulk transfer job: " + e.getMessage(), e);
		} 
		finally {
			if (session != null) {
				session.close();
			}
		}
		log.info("END bulkTransferJob::execute");
	}

	/**
	 * Calculates the Charge for the Bulk transfer and transfers the money to eMoney pocket if the source is Bank pocket 
	 * and Charge amount to the Charges pocket.
	 * @param bulkUpload
	 * @param buDAO
	 */
	public void doBulkTransfer(BulkUpload bulkUpload) {


		// change the Bulk upload file status to processing.
		bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Processing);
		bulkUpload.setDeliveryDate(new Timestamp());
		bulkuploadService.save(bulkUpload);

		BigDecimal moneyAvailbleBeforeTheJob = pocketService.getSuspencePocket(bulkUpload.getUser()).getCurrentBalance();
		try {
			bulkUpload = btService.processBulkTransferData(bulkUpload);
		} catch (IOException e) {
			log.error("Could not able to process the uploaded Tranfer file for Bulk Transfer: " + bulkUpload.getID() + "." + e.getMessage(), e);
			String failureReason = e.getMessage().length() > 255 ? e.getMessage().substring(0, 255) : e.getMessage(); 
			btService.failTheBulkTransfer(bulkUpload, failureReason);
			return;
		}
		bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Complete);
		bulkUpload.setDeliveryDate(new Timestamp());
		bulkuploadService.save(bulkUpload);
		
		Pocket pocket = pocketService.getSuspencePocket(bulkUpload.getUser());
		HibernateService hbnService = CoreServiceFactory.getInstance().getHibernateService();
		//sessionFactory = hbnService.getSessionFactory();
		//Session session = sessionFactory.openSession();
//		DAOFactory.getInstance().getHibernateSessionHolder().getSession().refresh(pocket);
		//session.refresh(pocket);
		//session.close();
		BigDecimal moneyAvailbleAfterTheJob = pocket.getCurrentBalance();
		btService.sendEmailBulkUploadSummary(bulkUpload, moneyAvailbleBeforeTheJob, moneyAvailbleAfterTheJob);	
		btService.sendNotification(bulkUpload, CmFinoFIX.NotificationCode_BulkTransferCompletedToPartner);
		
	}


}

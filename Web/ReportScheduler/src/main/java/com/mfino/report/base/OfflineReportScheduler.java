/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.domain.OfflineReport;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.module.DefaultModule;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mfino.util.ConfigurationUtil;

/**
 * 
 * @author Maruthi
 */
public class OfflineReportScheduler {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private DAOFactory daoFactory = DAOFactory.getInstance();
	private static ExecutorService threadPool = Executors.newCachedThreadPool();

	Injector injector = null;

	public OfflineReportScheduler() {
		this.injector = Guice.createInjector(new DefaultModule());
	}

	public OfflineReportScheduler(Injector injector) {
		this.injector = injector;
	}

	public static void main(String[] args) {
		Calendar cal = new GregorianCalendar(
				ConfigurationUtil.getLocalTimeZone());
		cal.setTime(new Date());
		Date end = DateUtils.truncate(cal, Calendar.DATE).getTime();
		Date start = DateUtils.addDays(end, -1);

		OfflineReportScheduler program = new OfflineReportScheduler();
		program.run(start, end);
	}

	public void run(Date start, Date end) {
		log.info("Starting offline report processing for "
				+ SimpleDateFormat.getInstance().format(start) + " to "
				+ SimpleDateFormat.getInstance().format(end));
		HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
		Session session = hibernateService.getSessionFactory().openSession();
		HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
		sessionHolder.setSession(session);
		DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
		try {
			// get all requires data into ReportBaseData
			ReportBaseData data = new ReportBaseData();
			data.intializeStaticData();
			data.getCommodityTransactions(start, end);
			data.getPendingCommodityTransactions(start, end);
			data.getServiceTransactionLogs(start, end);

			// load all report records
			OfflineReportDAO dao = daoFactory.getOfflineReportDAO();
			List<OfflineReport> reports = dao.getAll();
			// initialize the lazy loading property. maybe there is a cleaner
			// way.
			for (OfflineReport report : reports) {
				Hibernate.initialize(report
						.getOfflineReportReceiverFromReportID());
			}

			// execute all the reports
			for (OfflineReport report : reports) {
				if(report.getTriggerEnable()){
				log.info("Processing offline report: " + report.getName());
				executeClassReport(report, start, end, data);
				}
			}
		} catch (Throwable t) {
			try {
				session.getTransaction().rollback();
			} catch (Throwable innerT) {
				log.error("Failed to rollback transaction", innerT);
			}
			log.error("Error in OfflineReport", t);
		}finally{
			if(session!=null){
				session.close();
			}
		}
	}

	private void executeClassReport(OfflineReport report, Date start, Date end, ReportBaseData data) {
		/*threadPool.execute(new Runnable() {
			@Override
			public void run() {*/

				List<File> files = null;
				try {
					@SuppressWarnings("unchecked")
					Class<OfflineReportBase> c = (Class<OfflineReportBase>) Class.forName(report.getReportClass());
					if (c == null) {
						log.error("Bad report class name:" + report);
					} else {
						OfflineReportBase obj = injector.getInstance(c);
						obj.setReportName(report.getName());
						if (obj.hasMultipleReports()) {
							files = obj.runAndGetMutlipleReports(start, end,data);
						} else {
							File resultantFile = obj.run(start, end, data);
							files = new ArrayList<File>();
							files.add(resultantFile);
						}
					}
				} catch (IllegalArgumentException ex) {
					log.error("Error in OfflineReport" + report, ex);
				} catch (ClassNotFoundException ex) {
					log.error("Error in OfflineReport: Cannot load class "+ report, ex);
				}catch (Exception e) {
					log.error("Error in OfflineReport: "+ report, e);
				}

				if (files == null) {
					log.error("Class " + report.getReportClass()+ " did not return any file.");
				} else {
					ReportMailUtil reportMailUtil = new ReportMailUtil();
					reportMailUtil.sendReports(report, files);
				}

		/*	}
		});*/
	}

}

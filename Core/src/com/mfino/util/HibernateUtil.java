package com.mfino.util;

import java.io.File;
import java.util.TimeZone;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);
	
    private static SessionFactory mainSessionFactory;
    private static SessionFactory reportSessionFactory;
    public static ThreadLocal tlData = new ThreadLocal(); 
    static {
        init();
    }

    public static void init() {
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            
        	if(ConfigurationUtil.getHibernateMFinoDBConfigFile() != null){
        		File configFile = new File(ConfigurationUtil.getHibernateMFinoDBConfigFile());
        		if(configFile.exists()){
            		log.info("Reading main Hibernate configuraiton file " + ConfigurationUtil.getHibernateMFinoDBConfigFile());
                    mainSessionFactory = new Configuration().configure(configFile).buildSessionFactory();
        		}
        	}
        	
        	if(ConfigurationUtil.getHibernateMFinoReportDBConfigFile() != null){
        		File configFile = new File(ConfigurationUtil.getHibernateMFinoReportDBConfigFile());
        		if(configFile.exists()){
            		log.info("Reading report Hibernate configuraiton file " + ConfigurationUtil.getHibernateMFinoReportDBConfigFile());
                    reportSessionFactory = new Configuration().configure(configFile).buildSessionFactory();
        		}
        	}
        	
        	if(mainSessionFactory == null){
        		mainSessionFactory = new Configuration().configure().buildSessionFactory();
        	}
        	
        	if(reportSessionFactory == null){
        		reportSessionFactory = mainSessionFactory;
        	}
        	
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static Session getCurrentSession() {
        Session thisSession = mainSessionFactory.getCurrentSession();
        thisSession.setFlushMode(FlushMode.COMMIT);
        return thisSession;
    }

    public static Transaction getCurrentTransaction() {
        return getCurrentSession().getTransaction();
    }
    
    public static Session getCurrentReportSession(){
        Session thisSession = reportSessionFactory.getCurrentSession();
     /* TODO: Figure out how best to do a read-only querying on the DB for reporting 
      *  try {
          thisSession.connection().setReadOnly(true);
        } catch (HibernateException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }*/
//        thisSession.setFlushMode(FlushMode.MANUAL);
        thisSession.setFlushMode(FlushMode.AUTO);
        return thisSession;
    }
    
    public static Transaction getCurrentReportTransaction(){
    	return getCurrentReportSession().getTransaction();
    }
    
	  @SuppressWarnings("unchecked")
	 public static void setThreadLocalSession(Session session){
    	tlData.set(session);
	  }

	public static void removeThreadLocal() {
			tlData.remove();
	}
}

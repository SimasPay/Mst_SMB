package com.mfino.dbcopytool.dbcopy;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dbcopytool.persistence.CopyScheduleInfoDAO;
import com.mfino.dbcopytool.persistence.MfinoDbHibernateUtil;
import com.mfino.dbcopytool.reportdb.domain.CopyScheduleInfo;
import com.mfino.dbcopytool.reportdb.domain.TableRow;
import com.mfino.hibernate.Timestamp;

public class DbCopy {
	
	private static Logger log = LoggerFactory.getLogger("DbCopy");
	
	public static void copy(String hqlQuery, String className) {
		
		TableRow row = RowFactory.getRow(className);
		if (row != null) {
			Session session =MfinoDbHibernateUtil.getSessionFactory().openSession();
			Query query = session.createQuery(hqlQuery);
			List subList = query.list();
			row.getRequiredEnums();
			session.close();
			
			log.info("Total records"+subList.size());
			for (int i = 0; i < subList.size(); i++) {				
				row.initialiseRow((Object[]) subList.get(i));
				row.insertRow();
//				row.printRow();
			}						
		}
	}
	
	public static void main(String args[]) {

		Date currentUpdateTime = new Timestamp();
		log.info("Started Copying the required Data StartTime"+new Timestamp().toString());
		
		Set<String> domainClasses = QueryStringAndDomainConstants.domainQueryMap.keySet();
		for(String domainClass:domainClasses){
			log.info("Executing copy for class:"+domainClass +" StartTime"+new Timestamp().toString());			
			try{
			 CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
			 CopyScheduleInfo csi = dao.getClassInfo(domainClass);
					 for(String query:QueryStringAndDomainConstants.domainQueryMap.get(domainClass)){
					query = query.replace(QueryStringAndDomainConstants.lastUpdateTime, getDateString(csi.getLastCopyTime()));
					query = query.replace(QueryStringAndDomainConstants.currentUpdateTime, getDateString(currentUpdateTime));
					copy(query, domainClass);
				}
			 csi.setLastCopyTime(currentUpdateTime); 
			 dao.save(csi);	
			}catch (Exception e) {
				log.error("Exception "+domainClass,e);
			}
			log.info("Data copy completed for "+domainClass+" EndTime"+new Timestamp().toString());
		}
		log.info("Completed Copying the required Data EndTime"+new Timestamp().toString());

	}
	
	public static String getDateString(Date d){
		return "'"+d.toString()+"'";
	}

}

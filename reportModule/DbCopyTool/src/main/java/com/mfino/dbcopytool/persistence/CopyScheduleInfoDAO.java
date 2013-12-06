package com.mfino.dbcopytool.persistence;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dbcopytool.reportdb.domain.CopyScheduleInfo;

public class CopyScheduleInfoDAO {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	

	@SuppressWarnings("deprecation")
	public CopyScheduleInfo getClassInfo(String domainClass)
	{
		Session session = ReportDbHibernateUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(CopyScheduleInfo.class);
		criteria.add(Restrictions.eq("className", domainClass));
		List list = criteria.list();
		CopyScheduleInfo info;
		if(list!=null&&!list.isEmpty()){
		 info =  (CopyScheduleInfo)list.get(0);
		 session.close();
		}else{
			info = new CopyScheduleInfo();
			info.setClassName(domainClass);
			info.setLastCopyTime(new Date(1, 0, 1));
			log.info("Adding CopyInfo for class:"+domainClass);
			save(info);
		}
		
		return info;
	}
	
	
	public void save(CopyScheduleInfo csi) {
		Session session = ReportDbHibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.saveOrUpdate(csi);
		session.getTransaction().commit();	
		session.close();		
	}
}

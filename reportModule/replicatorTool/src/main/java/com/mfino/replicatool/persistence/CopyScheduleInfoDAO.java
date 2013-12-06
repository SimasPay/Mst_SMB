package com.mfino.replicatool.persistence;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.replicatool.domain.decrypted.CopyScheduleInfo;




public class CopyScheduleInfoDAO {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	

	@SuppressWarnings("deprecation")
	public com.mfino.replicatool.domain.decrypted.CopyScheduleInfo getClassInfo(String domainClass)
	{
		Session session = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(CopyScheduleInfo.class);
		criteria.add(Restrictions.eq("className", domainClass));
		List list = criteria.list();
		com.mfino.replicatool.domain.decrypted.CopyScheduleInfo info;
		if(list!=null&&!list.isEmpty()){
		 info =  (com.mfino.replicatool.domain.decrypted.CopyScheduleInfo)list.get(0);
		 session.close();
		}else{
			info = new com.mfino.replicatool.domain.decrypted.CopyScheduleInfo();
			info.setClassName(domainClass);
			info.setLastCopyTime(new Date(1, 0, 1));
			log.info("Adding CopyInfo for class:"+domainClass);
			save(info);
		}
		
		return info;
	}
	
	
	public void save(com.mfino.replicatool.domain.decrypted.CopyScheduleInfo csi) {
		Session session = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.saveOrUpdate(csi);
		session.getTransaction().commit();	
		session.close();		
	}
}

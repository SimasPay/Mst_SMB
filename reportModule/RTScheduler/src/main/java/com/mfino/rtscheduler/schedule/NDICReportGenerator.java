package com.mfino.rtscheduler.schedule;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.CurrentBalanceInfoDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.CurrentBalanceInfo;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
//import com.mfino.utils.StringEncryptor;
@Deprecated
public class NDICReportGenerator {
	private static Logger log = LoggerFactory.getLogger("NDICReportGenerator");
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	@Deprecated
	void NDICReportGeneration(){
		log.info("NDICReportGenerator::myfunction");
		String hqlQuery = "select p.CurrentBalance,s.ID,s.KYCLevel from pocket p,subscriber_mdn mdn,subscriber s where p.MDNID=mdn.ID and s.ID=mdn.SubscriberID";
		
		SessionFactory sessionFactory = null;

		HibernateSessionHolder hibernateSessionHolder = null;
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
		Session session = sessionFactory.openSession();

		hibernateSessionHolder.setSession(session);		
		Query query = session.createSQLQuery(hqlQuery);

		List subList = query.list();
	
		BigDecimal[] obj1 = null;
		String CurrentBalance[]=new String[subList.size()];
		String ID[]=new String[subList.size()];
		String KYCLevel[]=new String[subList.size()];

		for(int i = 0; i < subList.size(); i++) {		
			int j = -1,k=-1,m=-1;
			Object[] obj = (Object[]) subList.get(i);
			j++;
			//CurrentBalance[j] =  StringEncryptor.decrypt(obj[j].toString());
		    k=j+1;
			ID[k]=obj[k].toString();
			m=k+1;
			KYCLevel[m]=obj[m].toString();
			
			CurrentBalanceInfo CBinfo = new CurrentBalanceInfo();
			BigDecimal amt = new BigDecimal(CurrentBalance[j]);
			CBinfo.setCurrentBalance(amt);
			CBinfo.setKYCLevel((Long.parseLong(KYCLevel[m])));
			CBinfo.setSubscriberID((Long.parseLong(ID[k])));

			CurrentBalanceInfoDAO CurrentDAO = DAOFactory.getInstance().getCurrentBalanceInfoDAO();
			CurrentDAO.save(CBinfo); 
            
		}
		
      session.close();
	}

	public static void main(String args[]){
		NDICReportGenerator ned= new NDICReportGenerator();
		ned.NDICReportGeneration();
	}




}

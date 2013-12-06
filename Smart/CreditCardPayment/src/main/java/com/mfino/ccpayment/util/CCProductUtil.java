package com.mfino.ccpayment.util;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mfino.cc.message.CCProductInfo;
import com.mfino.dao.CreditCardProductDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.CreditCardProductQuery;
import com.mfino.domain.CreditCardProduct;
import com.mfino.hibernate.session.HibernateSessionHolder;

public class CCProductUtil {

	private static Logger log = LoggerFactory.getLogger(CCProductUtil.class);

	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}

	public CCProductInfo getInfo(CCProductInfo productInfo) {
		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);
		try
		{
			CreditCardProductDAO ccpDAO = DAOFactory.getInstance().getCreditCardProductDAO();
			CreditCardProductQuery ccpQuery = new CreditCardProductQuery();
			if(productInfo.getCompanyID() != null)
				ccpQuery.setCompanyID(productInfo.getCompanyID());
			List<CreditCardProduct> ccpList = ccpDAO.get(ccpQuery);
			productInfo.setCCPList(ccpList);
			return productInfo;		
		}
		finally
		{
			if(session!=null)
			{
				session.close();
			}
		}
	}	

	public enum RequestType {

		Update, Register, Select
	}
}

package com.mfino.vah.handlers.inqury;

import java.util.Properties;

import javax.jms.JMSException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.service.SubscriberService;
import com.mfino.service.impl.SubscriberServiceImpl;
import com.mfino.vah.handlers.TransactionHandler;
import com.mfino.vah.iso8583.IsoToFixConverterFactory;
import com.mfino.vah.messaging.QueueChannel;

public class InquiryHandler {

	private static Logger log = LoggerFactory.getLogger(InquiryHandler.class);
	
	private ISOMsg	msg;

	/*public InquiryHandler(ISOMsg msg) {
		this.msg = msg;
	}*/

	private SubscriberService subscriberService;
	private SessionFactory sessionFactory;
	private HibernateTransactionManager htm;
	private static InquiryHandler inquiryHandler;
	private static Properties property;
	

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
	
	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}

	public static InquiryHandler createInstance(){
		if(inquiryHandler==null){
			inquiryHandler = new InquiryHandler();
			property = new Properties();
		}
		
		return inquiryHandler;
	}
	
	public static InquiryHandler getInstance(){
		if(inquiryHandler==null){
			throw new RuntimeException("Instance is not already created");
		}
		return inquiryHandler;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public String getInquiryResponseElement48(ISOMsg msg) throws InvalidRequestException {

		String result = null;

		try {

			String oMdn = msg.getValue(103).toString();
			if (!oMdn.startsWith("8881")){
				log.warn("received a request other than 8881");
				throw new InvalidRequestException();
			}
			
			sessionFactory = htm.getSessionFactory();
			Session session = SessionFactoryUtils.getSession(sessionFactory, true);
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));

			String mdn = subscriberService.normalizeMDN(oMdn.substring(4));
			log.info("normalized mdn="+mdn);
			SubscriberMDNDAO dao = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMDN subMdn = dao.getByMDN(mdn);
			Subscriber subscriber = subMdn.getSubscriber();

			String firstName = subscriber.getFirstName();
			String lastName = subscriber.getLastName();

			result = String.format("%-30s%-16s%-30s%-30s%s", "SMEM QQ " + firstName + " " + lastName, oMdn, "", "SMART E-MONEY", "10");
			
			log.info("Inquiry response DE-48="+result);
			
		}
		catch (ISOException ex) {

		}finally{
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}

		return result;
	}
}

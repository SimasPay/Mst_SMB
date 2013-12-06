/**
 * 
 */
package com.mfino.stk;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.util.MessageHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.mce.notification.SMSNotification;
import com.mfino.result.XMLResult;
import com.mfino.service.MfinoService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberService;
import com.mfino.stk.processor.RequestProcessorFactory;
import com.mfino.stk.vo.STKRequest;

/**
 * @author Bala Sunku
 * 
 */
public class STKServiceImpl implements STKService {

	public Log	                         log	                 = LogFactory.getLog(this.getClass());
	public static final String	         STK_DELIMETER	         = "*";
	public static final String	         EMONEY_POCKET_TYPE_CODE	= "1";
	public static final String	         BANK_POCKET_TYPE_CODE	 = "2";
	public static final String	         CHANNEL_CODE	         = "6";
	private SessionFactory sessionFactory;
	private NotificationMessageParserService notificationMessageParserService;
	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}

	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}

	private MfinoService mfinoService;
	public MfinoService getMfinoService() {
		return mfinoService;
	}

	public void setMfinoService(MfinoService mfinoService) {
		this.mfinoService = mfinoService;
	}

	private PartnerService partnerService;
	public PartnerService getPartnerService() {
		return partnerService;
	}

	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	private NotificationService notificationService;

	
	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	private HibernateTransactionManager htm;
	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}

	private VisafoneEncryptionDecryption	visafoneEncryptionDecryption;
	private SubscriberService subscriberService;
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	private String						 smsQueueName = "jms:visafoneSMSNotificationQueue?disableReplyTo=true"; 	
	
	private RequestProcessorFactory 	processorFactory		= null;
	public VisafoneEncryptionDecryption getVisafoneEncryptionDecryption() {
		return visafoneEncryptionDecryption;
	}

	public void setVisafoneEncryptionDecryption(VisafoneEncryptionDecryption visafoneEncryptionDecryption) {
		this.visafoneEncryptionDecryption = visafoneEncryptionDecryption;
	}

	public String getSmsQueueName() {
		return smsQueueName;
	}

	public void setSmsQueueName(String smsQueueName) {
		this.smsQueueName = smsQueueName;
	}

	/**
	 * The smpp uri 
	 * smpp://MobileM@41.138.162.32:5020?password=MobileM&amp;sourceAddrTon=5&amp;systemType=consumer&amp;dataCoding=4&amp;alphabet=4
	 * 
	 * dataCoding=4 and alphabet=4 are compulsory
	 */
	@Override
	public void process(Exchange exchange) {
		
		sessionFactory = htm.getSessionFactory();
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
//		SessionFactoryUtils.initDeferredClose(getSessionFactory());
		
		SMSNotification smsNotification = new SMSNotification();
		log.info("STKServiceImpl::process --> Begin");
		if (exchange == null) {
			log.error("Exchange object is Null");
			return;
		}

		STKRequest stkRequest = new STKRequest();
		String sourceMDN = (String) exchange.getIn().getHeader("camelsmppsourceaddr");
		if (StringUtils.isBlank(sourceMDN)) {
			log.error("Source MDN is Null");
			return;
		}
		
		try
		{
			sourceMDN = subscriberService.normalizeMDN(sourceMDN);
			stkRequest.setSourceMDN(sourceMDN);

			String entireMessage = MessageHelper.extractBodyAsString(exchange.getIn());
			log.info("Length of the string is --> " + entireMessage.length());
			
			String str = "";
			int[] intRequest = new int[entireMessage.length()];
			for (int i = 0; i < entireMessage.length(); i++) {
				str = str + "," + ((int) entireMessage.charAt(i));
				intRequest[i] = ((int) entireMessage.charAt(i));
			}
			log.info("entireMessage as int string --> " + str);
			stkRequest.setRequestMsg(entireMessage);
			smsNotification.setMdn(sourceMDN);
	
			if (StringUtils.isBlank(entireMessage)) {
				log.error("received bitsteam as a string is Null");
				smsNotification.setContent("Request message is Null");
			}
			else {
				log.info("msg is not null.Processing");
				stkRequest.setRequestAsInts(intRequest);
				try {

					XMLResult result = processorFactory.processRequest(stkRequest);
					if (result != null ) {
						if (StringUtils.isNotBlank(result.getMessage())) {
							smsNotification.setContent(result.getMessage()); 
						} 
						else {
							result.setNotificationMessageParserService(notificationMessageParserService);
							result.setMfinoService(mfinoService);
							result.setPartnerService(partnerService);
							result.setNotificationService(notificationService);

							result.buildMessage();
							StringBuilder sb = new StringBuilder("(");
							sb.append(result.getXMlelements().get("code"));
							sb.append(")");
							sb.append(result.getXMlelements().get("message"));
							smsNotification.setContent(sb.toString());
						}
					}
					else {
						smsNotification.setContent(StringUtils.EMPTY);
					}
				} catch (Exception e) {
					smsNotification.setContent(StringUtils.EMPTY);
					log.error("Error While Parsing the Result -->" + e.getMessage(), e);
				}
			}
	
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			template.sendBody(smsQueueName, smsNotification);
			template.stop();
			exchange.getIn().setBody(smsNotification);
			exchange.setOut(exchange.getIn());
		} 
		catch (Exception e) 
		{
			log.error("error performing transaction ",e);
		}

		log.info("STKServiceImpl::process --> End");

		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.closeSession(sessionHolder.getSession());
		
		return;
	}

	public static byte[] getBytes(String str) {
		byte[] b = new byte[str.length()];
		for (int i = 0; i < b.length; i++)
			b[i] = (byte) (str.charAt(i) & 0xff);
		return b;
	}

	public static byte[] getFirst7Bits(String str) {
		byte[] b = new byte[str.length()];
		for (int i = 0; i < b.length; i++)
			b[i] = (byte) (str.charAt(i) & 0x7f);
		return b;
	}

	public static byte[] getFirst8Bits(String str) {
		byte[] b = new byte[str.length()];
		for (int i = 0; i < b.length; i++)
			b[i] = (byte) (str.charAt(i) & 0xff);
		return b;
	}


	public RequestProcessorFactory getProcessorFactory() {
		return processorFactory;
	}

	public void setProcessorFactory(RequestProcessorFactory processorFactory) {
		this.processorFactory = processorFactory;
	}

	class ByteArray {

	}
}

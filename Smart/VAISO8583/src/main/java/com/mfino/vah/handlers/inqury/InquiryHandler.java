package com.mfino.vah.handlers.inqury;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.service.impl.SubscriberServiceImpl;

public class InquiryHandler {

	private static Logger log = LoggerFactory.getLogger(InquiryHandler.class);
	
	private ISOMsg	msg;

	public InquiryHandler(ISOMsg msg) {
		this.msg = msg;
	}

	public String getInquiryResponseElement48() throws InvalidRequestException {

		String result = null;

		try {

			String oMdn = msg.getValue(103).toString();
			if (!oMdn.startsWith("8881")){
				log.warn("received a request other than 8881");
				throw new InvalidRequestException();
			}
			SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();
			String mdn = subscriberServiceImpl.normalizeMDN(oMdn.substring(4));
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

		}

		return result;
	}
}

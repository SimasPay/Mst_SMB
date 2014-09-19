package com.mfino.bsim.iso8583.handlers;

import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.packager.XMLPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.util.MfinoUtil;
import com.mfino.bsim.iso8583.handlers.ATMRegistrationHandler;
import com.mfino.bsim.iso8583.processor.fixtoiso.GetSubscriberDetailsToBankProcessor;
import com.mfino.bsim.iso8583.utils.SyncProducer;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;


public class TransactionHandler implements Runnable {

	private static Logger	         log	= LoggerFactory.getLogger(TransactionHandler.class);

	private ISOMsg	                 msg;
	private ISOSource	             source;
	private String muxName;
 	
	public TransactionHandler(ISOMsg msg, ISOSource source) throws JMSException {
		this.msg = msg;
		this.source = source;
		this.muxName = "bsmmux";
	}

	@Override
	public void run() {
		String element39 = GetConstantCodes.FAILURE;

			try {
				log.info("isomsg received in transactionhandler" + msg);
				XMLPackager packager = new XMLPackager();
				log.info("received isomsg-->" + new String(packager.pack(msg)));

				// signon not done
				msg.set(38, "123456"); // authorization identification response
				if (StatusRegistrar.getSignonStatus(muxName).equals(NMStatus.Failed)) {
					log.warn("ISO Request received before signon, so rejected");
					element39 = GetConstantCodes.FAILURE;
				}
				else {
					String processingCode = msg.getString(3);
					//String de24=msg.getString(24);
					String mti = msg.getMTI();
					log.info("mti=" + mti + " processingCode=" + processingCode);
					
					try {
						if(processingCode.startsWith("98")){
						ATMRequestHandler.getInstance().handle(msg);
						element39 = msg.getString(39);	
						}else{
							log.error("TransactionHandler :: handle Unsupported transaction receive. Rejected");
							element39=GetConstantCodes.FAILURE;
						}
					}
					catch (Exception ex2) {
						log.warn("exceptin occured.", ex2);
						element39 = GetConstantCodes.FAILURE;
					}
					
				}
			}
			catch (ISOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
					try{
					msg.set(39, element39);
					msg.setResponseMTI();
					XMLPackager packager = new XMLPackager();
					log.info("response isomsg-->" + new String(packager.pack(msg)));
					source.send(msg);
					//try to get Subscriber Details by sending iso msg to cbs
					if(GetConstantCodes.SUCCESS.equals(msg.getString(39)))
					{
						log.info("TransactionHandler :: run() ATM Transaction Successful Sending msg to CBS to get Subscriber Details");
					ATMRequestHandler.getInstance().updateDetails(msg);
					}
					}catch(ISOException e){
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}

	}
}

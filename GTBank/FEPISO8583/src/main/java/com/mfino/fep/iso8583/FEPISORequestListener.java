package com.mfino.fep.iso8583;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

import com.mfino.fep.FEPConfiguration;
import com.mfino.fep.FEPConstants;
import com.mfino.fep.handlers.NMRequestHandler;
import com.mfino.fep.handlers.TransactionHandler;


public class FEPISORequestListener implements ISORequestListener,Configurable {
	private static Log	    log	= LogFactory.getLog(FEPISORequestListener.class);	
	private ExecutorService	NMpool =  Executors.newSingleThreadExecutor();
	private ExecutorService	transactionPool = Executors.newFixedThreadPool(10);
		
	@Override
	public boolean process(ISOSource source, ISOMsg m) {

		log.info("received request from FEP");
		
		try {
			String mti = m.getMTI();
			if (FEPConstants.NETWORK_REQUEST_MSG_TYPE.equals(mti)) {
				this.NMpool.execute(new NMRequestHandler(m, source));
			}
			else if (FEPConstants.REQUEST_MSG_TYPE.equals(mti)||FEPConstants.REVERSAL_REQUEST_MSG_TYPE.equals(mti)) {
				transactionPool.execute(new TransactionHandler(m, source));
			}
			else {
				log.error("iso mti="+mti+" is not supported.");
				m.set(39, FEPConfiguration.getISOResponseCode(FEPConstants.ISORESPONSE_NOTSUPPORTED));
				m.setResponseMTI();
				source.send(m);
				return true;
			}
		}
		catch (IOException ex) {
			log.error("Exception occured while sending iso response",ex);
		}
		catch (Exception ex) {
			log.error("Exception occured",ex);
		}
		return false;
	}
	
	public void finalize(){
		
	}


	@Override
	public void setConfiguration(Configuration cfg) throws ConfigurationException {
		String[] responseCodes = cfg.getAll("notificationToISOResponse");
		Map<String, String> notificationToISOResponse = new HashMap<String, String>();
		for(String notificationToIso:responseCodes){
			String[] codes = notificationToIso.split("-"); 
			notificationToISOResponse.put(codes[0], codes[1]);
		}
		FEPConfiguration.setNotificationToISOResponse(notificationToISOResponse);
		
		String frontEndId = cfg.get("frontendID");
		FEPConfiguration.setFrontEndID(frontEndId);		
	}

}
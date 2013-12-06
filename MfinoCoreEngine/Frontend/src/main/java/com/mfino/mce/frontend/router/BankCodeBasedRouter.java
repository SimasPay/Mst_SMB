package com.mfino.mce.frontend.router;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMOperatorRequest;
import com.mfino.mce.core.MCEMessage;

public class BankCodeBasedRouter {

//	private String	   codeToQueueMappingFile;
	Log	               log	= LogFactory.getLog(BankCodeBasedRouter.class);

	private Properties	props = new Properties();
	String	           defaultQueue;

	public String getDefaultQueue() {
		return defaultQueue;
	}

	public void setDefaultQueue(String defaultQueue) {
		this.defaultQueue = defaultQueue;
	}

	public BankCodeBasedRouter(String codeToQueueMappingFile) throws FileNotFoundException, IOException {

		log.info("codeToQueueMappingFile="+codeToQueueMappingFile);
//		this.codeToQueueMappingFile = codeToQueueMappingFile;
//		try {
			props.load(new FileInputStream(new File(codeToQueueMappingFile)));
//		}
//		catch (Exception ex) {
//		}
	}

	public String nextQueue(MCEMessage body, @Header(Exchange.SLIP_ENDPOINT) String previous) {
		return findNextQueue(body.getResponse(), previous);
	}

//	public String nextQueue(CFIXMsg body, @Header(Exchange.SLIP_ENDPOINT) String previous) {
//		return findNextQueue(body, previous);
//	}

	private String findNextQueue(CFIXMsg fixMesg, String previous) {
		log.info("BankCodeBasedRouter :: findNextDestination :: fixMesg=" + fixMesg + ", previous=" + previous);
		if (previous != null)
			return null;

		log.info("finding the next queue ::");
		
		log.info("request of type "+fixMesg.getClass().getName()+" received");
		
		if(!((fixMesg instanceof CMBankRequest)||(fixMesg instanceof CMOperatorRequest))){
			log.info(fixMesg.getClass().getName()+" is not an instanceof CmBankRequest or CMOperatorRequest .");
			return defaultQueue;
		}
		
		String routingCode;
		
		if(props==null){
			log.info("can not load properties file so returning default queue");
			return defaultQueue;
		}
		if (fixMesg instanceof CMBankRequest) {
			CMBankRequest request = (CMBankRequest) fixMesg;
			if (request.getBankCode() == null) {
				log.info("Can not find the target queue because bankcode="+ request.getBankCode() + " is null.");
				return defaultQueue;
			}
			routingCode = request.getBankCode().toString();
		}else{
			CMOperatorRequest request = (CMOperatorRequest) fixMesg;
			if (request.getOperatorCode() == null) {
				log.info("Can not find the target queue because bankcode="+ request.getOperatorCode() + " is null.");
				return defaultQueue;
			}
			routingCode = request.getOperatorCode().toString();
		}
		
		String nextQueue = props.getProperty(routingCode.toString());
		if(nextQueue==null){
			log.info("routingCode="+routingCode+" doesn't have a valid queue associated with it.");
			return defaultQueue;
		}
		log.info("returning nextqueue="+nextQueue);
		return nextQueue;
	}
}

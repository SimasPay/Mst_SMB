package com.mfino.mce.frontend.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.frontend.FrontendService;

public class FrontendServiceDefaultImpl implements FrontendService {

	Log	                              log	= LogFactory.getLog(FrontendServiceDefaultImpl.class);

	private IFixToIsoProcessorFactory	fixtoisoFactoryInstance;

	public void setFixtoisoFactoryInstance(IFixToIsoProcessorFactory fixtoisoFactoryInstance) {
		this.fixtoisoFactoryInstance = fixtoisoFactoryInstance;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public ISOMsg processMessage(MCEMessage mesg) {
		ISOMsg isoMsg = null;
		CFIXMsg request = mesg.getResponse();
		
		log.info("Message in frontend " + request.DumpFields());
		log.info("In Frontend --> MCERequest= "+mesg.getRequest().getClass());
		log.info("In Frontend --> MCEResponse= "+mesg.getResponse().getClass());
		
		//for reversals. We get NoIsoResponseMsg in request and CmBankRequest in response
		//convert the response to a reversal request
//		if(mesg.getRequest() instanceof NoISOResponseMsg){
//			log.info("Identified this request as a reversal request");
//			ConversionToReversalRequestProcessor proc = new ConversionToReversalRequestProcessor();
//			request = proc.processMessage(mesg).getResponse();
////			mesg.setRequest(mes);
//			mesg.setResponse(request);
//		}

		IFixToIsoProcessor processor = null;
		try {

			processor = fixtoisoFactoryInstance.getProcessor(request);
			isoMsg = processor.process(request);

			log.info("constructed ISO Message " + isoMsg);
			return isoMsg;
		}
		catch (Exception e) {
			log.error("Exception", e);

		}
		return null;
	}
}

package com.mfino.zte.iso8583;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;
import com.mfino.zte.iso8583.processor.isotofix.AccountInquiryFromZTEProcessor;
import com.mfino.zte.iso8583.processor.isotofix.CommodityTransferFromZTEProcessor;

public class ZTEIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final ZTEIsoToFixProcessorFactory factory = new ZTEIsoToFixProcessorFactory();
	 private static Logger log = LoggerFactory.getLogger(ZTEIsoToFixProcessorFactory.class);
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		log.info("ZTEIsoToFixProcessorFactory"+requestFixMsg.toString());
		
		if (requestFixMsg instanceof CMGetMDNBillDebtsToOperator)
			processor = new AccountInquiryFromZTEProcessor();
		else if (requestFixMsg instanceof CMCommodityTransferToOperator)
			processor = new CommodityTransferFromZTEProcessor();
		else throw new ProcessorNotAvailableException();

		return processor;
	}
	
	public static ZTEIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}

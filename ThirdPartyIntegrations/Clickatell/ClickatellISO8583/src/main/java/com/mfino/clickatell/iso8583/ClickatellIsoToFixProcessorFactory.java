package com.mfino.clickatell.iso8583;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mfino.clickatell.iso8583.processor.isotofix.BillPaymentFromClickatellProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;

public class ClickatellIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final ClickatellIsoToFixProcessorFactory factory = new ClickatellIsoToFixProcessorFactory();
	 private static Logger log = LoggerFactory.getLogger(ClickatellIsoToFixProcessorFactory.class);
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		log.info("ClickatellIsoToFixProcessorFactory"+requestFixMsg.toString());
		if (requestFixMsg instanceof CMCommodityTransferToOperator)
			processor = new BillPaymentFromClickatellProcessor();
		else throw new ProcessorNotAvailableException();

		return processor;
	}
	
	public static ClickatellIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}

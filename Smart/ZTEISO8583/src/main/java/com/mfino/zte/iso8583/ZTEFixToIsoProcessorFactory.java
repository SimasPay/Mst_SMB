package com.mfino.zte.iso8583;

import java.util.Map;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;
import com.mfino.zte.iso8583.processor.fixtoiso.AccountInquiryToZTEProcessor;
import com.mfino.zte.iso8583.processor.fixtoiso.CommodityTransferToZTEProcessor;

public class ZTEFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final ZTEFixToIsoProcessorFactory factory = new ZTEFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;

	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}
	
	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
		if (request instanceof CMGetMDNBillDebtsToOperator)
			processor = new AccountInquiryToZTEProcessor();
		else if (request instanceof CMCommodityTransferToOperator)
			processor = new CommodityTransferToZTEProcessor();
		else
			throw new ProcessorNotAvailableException();

		processor.setConstantFieldsMap(constantFieldsMap);
		return processor;
	}
	
	public static ZTEFixToIsoProcessorFactory getInstance(){
		return factory;
	}

}

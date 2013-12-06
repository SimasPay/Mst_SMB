package com.mfino.clickatell.iso8583;

import java.util.Map;
import com.mfino.clickatell.iso8583.processor.fixtoiso.BillPaymentToClickatellProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;

public class ClickatellFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final ClickatellFixToIsoProcessorFactory factory = new ClickatellFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;
    public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}
	
	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
		if (request instanceof CMCommodityTransferToOperator)
			processor = new BillPaymentToClickatellProcessor();
		else
			throw new ProcessorNotAvailableException();
		processor.setConstantFieldsMap(constantFieldsMap);
		return processor;
	}
	
	public static ClickatellFixToIsoProcessorFactory getInstance(){
		return factory;
	}

}

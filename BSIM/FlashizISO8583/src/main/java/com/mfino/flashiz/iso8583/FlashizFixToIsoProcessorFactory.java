package com.mfino.flashiz.iso8583;

import java.util.Map;
import java.util.Set;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.fix.CmFinoFIX.CMPaymentAuthorizationToBankForBsim;
import com.mfino.flashiz.iso8583.processor.fixtoiso.GetUserAPIKeyToBankProcessor;
import com.mfino.flashiz.iso8583.processor.fixtoiso.PaymentAcknowledgementToBankProcessor;
import com.mfino.flashiz.iso8583.processor.fixtoiso.PaymentAuthorizationToBankProcessor;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;

public class FlashizFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final FlashizFixToIsoProcessorFactory factory = new FlashizFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;
	
	private Set<String> offlineBillers;
	
	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}

	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;

		if (request instanceof CMGetUserAPIKeyToBank)
			processor = new GetUserAPIKeyToBankProcessor();
		else if (request instanceof CMPaymentAuthorizationToBankForBsim) 
			processor = new PaymentAuthorizationToBankProcessor();
		else if (request instanceof CMPaymentAcknowledgementToBankForBsim)
			processor = new PaymentAcknowledgementToBankProcessor();
		else
			throw new ProcessorNotAvailableException();

		processor.setConstantFieldsMap(constantFieldsMap);
		return processor;
	}
	
	public static FlashizFixToIsoProcessorFactory getInstance(){
		return factory;
	}

	public Set<String> getOfflineBillers() {
		return offlineBillers;
	}

	public void setOfflineBillers(Set<String> offlineBillers) {
		this.offlineBillers = offlineBillers;
	}
}

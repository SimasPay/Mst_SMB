package com.mfino.flashiz.iso8583;


import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.flashiz.iso8583.processor.isotofix.GetUserAPIKeyFromBankProcessor;
import com.mfino.flashiz.iso8583.processor.isotofix.PaymentAcknowledgementFromBankProcessor;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;

public class FlashizIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final FlashizIsoToFixProcessorFactory factory = new FlashizIsoToFixProcessorFactory();
	
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		if (requestFixMsg instanceof CMGetUserAPIKeyToBank)
			processor = new GetUserAPIKeyFromBankProcessor();
		else if (requestFixMsg instanceof CMPaymentAcknowledgementToBankForBsim)
			processor = new PaymentAcknowledgementFromBankProcessor();
		else throw new ProcessorNotAvailableException();
           return processor;
	}
	
	public static FlashizIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}

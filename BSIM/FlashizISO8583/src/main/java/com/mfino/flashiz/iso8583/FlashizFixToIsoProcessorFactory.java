package com.mfino.flashiz.iso8583;

import java.util.Map;
import java.util.Set;


import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiryToBank;

import com.mfino.fix.CmFinoFIX.CMBillPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentToBank;

import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.flashiz.iso8583.processor.fixtoiso.GetUserAPIKeyToBankProcessor;
import com.mfino.flashiz.iso8583.processor.fixtoiso.PaymentAcknowledgementToBankProcessor;
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

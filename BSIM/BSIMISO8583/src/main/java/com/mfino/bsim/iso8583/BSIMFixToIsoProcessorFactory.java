package com.mfino.bsim.iso8583;

import java.util.Map;
import java.util.Set;

import com.mfino.bsim.iso8583.processor.fixtoiso.BalanceInquiryToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.BillPaymentInquiryToBankProcessor;

import com.mfino.bsim.iso8583.processor.fixtoiso.BillPaymentAmountInquiryToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.BillPaymentReversalToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.BillPaymentToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.ExistingSubscriberReActivationToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.GetLastTrxnsToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.GetSubscriberDetailsToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.InterBankMoneyTransferToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.InterBankTransferInquiryToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.MoneyTransferReversalToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.MoneyTransferToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.NewSubscriberActivationToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.TransferInquiryToBankProcessor;
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
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;

public class BSIMFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final BSIMFixToIsoProcessorFactory factory = new BSIMFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;
	
	private Set<String> offlineBillers;
	
	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}

	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
		if (request instanceof CMInterBankTransferInquiryToBank)
			processor = new InterBankTransferInquiryToBankProcessor();
		else if(request instanceof CMInterBankMoneyTransferToBank){
			processor = new InterBankMoneyTransferToBankProcessor();
		}		
		else if(request instanceof CMBSIMGetAmountToBiller)
			processor = new BillPaymentAmountInquiryToBankProcessor();
		else if (request instanceof CMBSIMBillPaymentReversalToBank)
			processor = new BillPaymentReversalToBankProcessor();
		else if (request instanceof CMBSIMBillPaymentInquiryToBank){
			BillPaymentInquiryToBankProcessor billPaymentInquiryToBankProcessor = new BillPaymentInquiryToBankProcessor();
			billPaymentInquiryToBankProcessor.setOfflineBillers(offlineBillers);
			processor = billPaymentInquiryToBankProcessor;
		}
		else if (request instanceof CMBSIMBillPaymentToBank)
			processor = new BillPaymentToBankProcessor();
		else if (request instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryToBankProcessor();
		else if (request instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryToBankProcessor();
		else if (request instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalToBankProcessor();
		else if (request instanceof CMMoneyTransferToBank)
			processor = new MoneyTransferToBankProcessor();
		else if (request instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsToBankProcessor();
		else if (request instanceof CMNewSubscriberActivationToBank)
			processor = new NewSubscriberActivationToBankProcessor();
		else if (request instanceof CMExistingSubscriberReactivationToBank)
			processor = new ExistingSubscriberReActivationToBankProcessor();
		else if (request instanceof CMGetSubscriberDetailsToBank)
			processor = new GetSubscriberDetailsToBankProcessor();
		else
			throw new ProcessorNotAvailableException();

		processor.setConstantFieldsMap(constantFieldsMap);
		return processor;
	}
	
	public static BSIMFixToIsoProcessorFactory getInstance(){
		return factory;
	}

	public Set<String> getOfflineBillers() {
		return offlineBillers;
	}

	public void setOfflineBillers(Set<String> offlineBillers) {
		this.offlineBillers = offlineBillers;
	}
}
